import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Model {
    private ArrayList<Task> TaskList;

    public Model() {
        TaskList = new ArrayList<>();
    }

    // Add a new task based on parameters
    public void createTask(String name, String type, Integer date, Float startTime, Float duration, Integer endDate, Integer frequency) {
        Task newTask;
        switch (type.toLowerCase()) {
            case "transient":
                newTask = new TransientTask(name, type, startTime, duration, date, null);
                break;
            case "recurring":
                newTask = new RecurringTask(name, type, startTime, duration, date, frequency, endDate);
                break;
            case "anti":
                newTask = new AntiTask(name, type, startTime, duration, date);
                break;
            default:
                System.out.println("Error: Invalid task type!");
                return;
        }

        addTask(newTask);
    }

    // Add a new task to the schedule if no conflicts exist
    public void addTask(Task task) {
        if (checkTaskConflicts(task)) {
            TaskList.add(task);
            System.out.println("Task added successfully: " + task.getName());
        } else {
            System.out.println("Error: Task conflicts with an existing task!");
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
    public void scheduleToFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the schedule filename (e.g., schedule.json): ");
        String fileName = scanner.nextLine(); // Get filename from user input

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
        scanner.close();
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
        TaskReader taskReader = new TaskReader(this);
        taskReader.readFromJson(fileName);
    }
}
