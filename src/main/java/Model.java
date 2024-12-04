import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Model is the main data processing class for the PSS project. Stores all the tasks created by or loaded into the program.
 */
public class Model {
    /**
     * The list of Task objects.
     */
    private ArrayList<Task> TaskList;

    /**
     * Default constructor for the Model, initializes the list of tasks.
     */
    public Model() {
        TaskList = new ArrayList<>();
    }

    /**
     * Checks to see if an integer is in a valid date format. Date must be in format: YYYYMMDD.
     * @param date the integer to be checked.
     * @return true if the integer is a real date, false otherwise.
     */
    private boolean isValidDate(Integer date){
        //Check for correct date format
        int dateLength = String.valueOf(date).length();
        System.out.println(dateLength);
        if(dateLength != 8 || date < 0){
            //DEBUG System.out.println("Error: Invalid date! J");
            return false;
        }

        //Check for valid month and day values. Year will not be checked.
        int year = Integer.parseInt(Integer.toString(date).substring(0, 4));
        int month = Integer.parseInt(Integer.toString(date).substring(4, 6));
        int day = Integer.parseInt(Integer.toString(date).substring(6));
        System.out.println("Year: " + year);
        System.out.println("Month: " + month);
        System.out.println("Day: " + day);

        if(month < 1 || month > 12 || day < 1 || day > 31){
            //DEBUG System.out.println("Error: Invalid date! A");
            return false;
        }

        switch (month){
            case 4, 6, 9, 11:
                if (day > 30){
                    //DEBUG System.out.println("Error: Invalid date! B");
                    return false;
                } else {
                    break;
                }
            case 2:
                if (year%4 == 0){ //year is a leap year
                    if (day > 29){
                        //DEBUG System.out.println("Error: Invalid date! C");
                        return false;
                    } else {
                        break;
                    }
                } else { //year is not leap year
                    if (day > 28){
                        //DEBUG System.out.println("Error: Invalid date! D");
                        return false;
                    } else {
                        break;
                    }
                }
            default: 
                return true;
        }
        return true;
    }

    /**
     * Attempts to create a new task with the given parameters. Checks for input errors.
     * @param name  the name of the task.
     * @param type  the type of task.
     * @param startDate if recurring task, the first instance of the task. Otherwise the date of the task.
     * @param startTime the start time (as a float) of the task.
     * @param duration the duration (as a float) of the task.
     * @param endDate if recurring task, the cutoff date of the task. Does not need to have an occurance on said date.
     * @param frequency how often (in days) the recurring task repeats.
     * @return true if task succesfully created, false otherwise.
     */
    public boolean createTask(String name, String type, Integer date, Float startTime, Float duration, Integer endDate, Integer frequency) {

        if (!isValidDate(date)){
            System.out.println("Error: Invalid Start Date!");
            return false;
        }

        //Check for valid start time
        if(startTime < 0 || startTime >= 24){
            System.out.println("Error: Invalid start time!");
            return false;
        }

        if(duration <= 0){
            System.out.println("Error: Invalid duration!");
            return false;
        }

        Task newTask;
        switch (type) { //Removed the .toLowerCase(), the project specifies that the first letter must be capitalized. "Note that the spelling and capitalization of these strings must be correct!" 
            //Note: these cases should not be the literal type of task but the user given ones. I've updated them to be accurate. - Julianne
            case "Visit", "Shopping", "Appointment": //transient task types
                newTask = new TransientTask(name, type, startTime, duration, date, null);
                break;
            case "Class", "Study", "Sleep", "Exercise", "Work", "Meal": //recurring task types
                //Validate End Date
                if (endDate != null){
                    if (!isValidDate(endDate) || date > endDate){
                        System.out.println("Error: Invalid End Date!");
                        return false;
                    }
                }

                //Validate Frequency
                if (frequency == null){
                    System.out.println("Error: Please input a frequency.");
                    return false;
                }

                if (frequency < 1 || frequency > 365){ //frequency can not be faster than daily or slower than yearly.
                    System.out.println("Error: Invalid Frequency!");
                    return false;
                }


                newTask = new RecurringTask(name, type, startTime, duration, date, frequency, endDate);
                break;
            case "Cancellation": //anti task types
                newTask = new AntiTask(name, type, startTime, duration, date);
                break;
            default:
                System.out.println("Error: Invalid task type!");
                return false;
        }

        return (addTask(newTask));
    }

    /**
     * Attempts to add a task to the task list
     * @param task the task to be added.
     * @return  true if successful, false otherwise.
     */
    public boolean addTask(Task task) {
        if (checkTaskConflicts(task)) {
            TaskList.add(task);
            System.out.println("Task added successfully: " + task.getName());
            return true;
        } else {
            System.out.println("Error: Task conflicts with an existing task!");
            return false;
        }
    }

    /**
     * Gets a task from the task list, given a task name. Is case-sensitive.
     * @param taskName the name of the task to find.
     * @return the task, if found, otherwise null.
     */
    public Task getTaskByName(String taskName) {
        for (Task task : TaskList) {
            if (task.getName().equals(taskName)) {
                return task;
            }
        }
        return null; // Task not found
    }

    /**
     * Deletes a specified task from the task list. If no task is found, nothing happens.
     * @param taskName the name of the task to delete.
     */
    public void deleteTask(String taskName) {
        Task task = getTaskByName(taskName);
        if (task != null) {
            TaskList.remove(task);
            System.out.println("Task deleted: " + taskName);
        } else {
            System.out.println("Error: Task not found: " + taskName);
        }
    }

    /**
     * Attempts to edit the properties of a specified task.
     * @param oldName the original task name.
     * @param newName   the new task name.
     * @param type  the new task type.
     * @param startDate the new task start date.
     * @param startTime the new task start time.
     * @param duration the new task duration.
     * @param endDate   if recurring, the new task end date.
     * @param frequency if recurring, the new task frequency.
     * @return true if successully edited, false otherwise.
     */
    public boolean editTask(String oldName, String newName, String type, Integer startDate, Float startTime, Float duration, Integer endDate, Integer frequency) {
        Task task = getTaskByName(oldName);
        if (task == null) {
            System.out.println("Error: Task not found: " + oldName);
            return false;
        }

        // Temporarily remove the task to avoid self-conflict
        TaskList.remove(task);

        // Create a new task with the updated details
        Task updatedTask;
        switch (type) {
            case "Visit", "Shopping", "Appointment":
                updatedTask = new TransientTask(newName, type, startTime, duration, startDate, null);
                break;
            case "Class", "Study", "Sleep", "Exercise", "Work", "Meal":
                updatedTask = new RecurringTask(newName, type, startTime, duration, startDate, frequency, endDate);
                break;
            case "Cancellation":
                updatedTask = new AntiTask(newName, type, startTime, duration, startDate);
                break;
            default:
                System.out.println("Error: Invalid task type!");
                TaskList.add(task); // Re-add the original task
                return false;
        }

        // Check for conflicts with the updated task
        if (checkTaskConflicts(updatedTask)) {
            // Apply the changes
            task.setName(newName);
            task.setType(type);
            task.setDate(startDate);
            task.setStartTime(startTime);
            task.setDuration(duration);
            if (task instanceof RecurringTask) {
                ((RecurringTask) task).setEndDate(endDate);
                ((RecurringTask) task).setFrequency(frequency);
            }
            TaskList.add(task);
            System.out.println("Task edited successfully: " + newName);
            return true;
        } else {
            System.out.println("Error: Task conflicts with an existing task!");
            TaskList.add(task); // Re-add the original task
            return false;
        }
    }

    /**
     * Gets the ArrayList of tasks.
     * @return the current task list.
     */
    public ArrayList<Task> getTasks() {
        ArrayList<Task> copy = new ArrayList<>();
        for (Task task : TaskList) {
            copy.add(task);
        }
        return copy;
    }

    /**
     * Attempts to save the current schedule to a .json file.
     * @param fileName the name of the file to save to/be created.
     */
    public void scheduleToFile(String fileName) {
        // Create a JSON array to hold tasks
        JSONArray tasksJsonArray = new JSONArray();

        for (Task task : TaskList) {
            JSONObject taskJson = new JSONObject();

            // Add common attributes
            taskJson.put("Name", task.getName());
            taskJson.put("Type", task.getType());
            taskJson.put("StartTime", task.getStartTime());
            taskJson.put("Duration", task.getDuration());

            if (task instanceof TransientTask) {
                // Add transient task-specific attributes
                taskJson.put("Date", ((TransientTask) task).getDate());
            } else if (task instanceof RecurringTask) {
                // Add recurring task-specific attributes
                RecurringTask recurringTask = (RecurringTask) task;
                taskJson.put("StartDate", recurringTask.getDate());
                taskJson.put("EndDate", recurringTask.getEndDate());
                taskJson.put("Frequency", recurringTask.getFrequency());
            } else if (task instanceof AntiTask) {
                // Add anti-task-specific attributes
                taskJson.put("Date", ((AntiTask) task).getDate());
            }

            // Add the task to the JSON array
            tasksJsonArray.add(taskJson);
        }

        // Write the JSON array to a file
        try (FileWriter file = new FileWriter("src/main/resources/" + fileName)) {
            file.write(tasksJsonArray.toJSONString());
            file.flush();
            System.out.println("Schedule saved to '" + fileName + "'.");
        } catch (IOException e) {
            System.out.println("Error saving schedule: " + e.getMessage());
        }
    }

    /**
     * Checks to see if a task conflicts with the current task list.
     * @param newTask the task to check.
     * @return  true if no conflict, false otherwise.
     */
    public boolean checkTaskConflicts(Task newTask) {
        for (Task existingTask : TaskList) {
            // Skip anti-tasks during conflict checks
            if (newTask instanceof AntiTask || existingTask instanceof AntiTask) {
                continue;
            }

            // Check if the tasks overlap on the same date
            if (newTask.getDate() == existingTask.getDate()) {
                float existingEndTime = existingTask.getStartTime() + existingTask.getDuration();
                float newEndTime = newTask.getStartTime() + newTask.getDuration();

                if (newTask.getStartTime() < existingEndTime && newEndTime > existingTask.getStartTime()) {
                    return false; // Conflict detected
                }
            }
        }
        return true; // No conflicts
    }

    /**
     * Reads a schedule from a JSON file (using TaskReader)
     * @param fileName the JSON file to read from.
     */
    public void readScheduleFromFile(String fileName) {
        fileName = "src/main/resources/" + fileName;
        TaskReader taskReader = new TaskReader(this);
        taskReader.readFromJson(fileName);
    }
}
