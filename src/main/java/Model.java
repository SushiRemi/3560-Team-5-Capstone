import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Model {
    private ArrayList<Task> TaskList;

    public Model() {
        TaskList = new ArrayList<>();
    }

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

    // Add a new task based on parameters
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

    // Add a new task to the schedule if no conflicts exist
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

    // Get a task by its name (case-sensitive)
    public Task getTaskByName(String taskName) {
        for (Task task : TaskList) {
            if (task.getName().equals(taskName)) {
                return task;
            }
        }
        return null; // Task not found
    }

    // Delete a task by name
    public void deleteTask(String taskName) {
        Task task = getTaskByName(taskName);
        if (task != null) {
            TaskList.remove(task);
            System.out.println("Task deleted: " + taskName);
        } else {
            System.out.println("Error: Task not found: " + taskName);
        }
    }

    // Edit a task by updating its attributes
    public void editTask(String taskName, String attribute, Object newValue) {
        Task task = getTaskByName(taskName);
        if (task == null) {
            System.out.println("Error: Task not found: " + taskName);
            return;
        }

        switch (attribute.toLowerCase()) {
            case "name":
                task.setName((String) newValue);
                break;
            case "type":
                task.setType((String) newValue);
                break;
            case "starttime":
                task.setStartTime(Float.parseFloat(newValue.toString()));
                break;
            case "duration":
                task.setDuration(Float.parseFloat(newValue.toString()));
                break;
            case "date":
                task.setDate(Integer.parseInt(newValue.toString()));
                break;
            default:
                System.out.println("Error: Unknown attribute: " + attribute);
                return;
        }

        if (!checkTaskConflicts(task)) {
            System.out.println("Error: Edited task conflicts with an existing task!");
        } else {
            System.out.println("Task updated successfully: " + task.getName());
        }
    }

    // Return a deep copy of the task list
    public ArrayList<Task> getTasks() {
        ArrayList<Task> copy = new ArrayList<>();
        for (Task task : TaskList) {
            copy.add(task);
        }
        return copy;
    }

    // Save the schedule to a JSON file
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

    // Check for task conflicts
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

    // Read a schedule from a JSON file (using TaskReader)
    public void readScheduleFromFile(String fileName) {
        fileName = "src/main/resources/" + fileName;
        TaskReader taskReader = new TaskReader(this);
        taskReader.readFromJson(fileName);
    }
}
