
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * TaskReader reads tasks from a json file into a Model object.
 */
public class TaskReader {
    /**
     * The connected Model
     */
    private final Model model;

    /**
     * Constructor for the TaskReader, connects to the specified model.
     * @param model the model to connect and read tasks into.
     */
    public TaskReader(Model model) {
        this.model = model;
    }

    /**
     * Attempts to read from a json file. If it does not exist, nothing happens.
     * @param fileName the name of the json file to read from.
     */
    public void readFromJson(String fileName) {
        File file = new File(fileName);

        // Check if the file exists
        if (!file.exists()) {
            System.out.println("Error: File does not exist.");
            return;
        }

        JSONParser parser = new JSONParser();
        ArrayList<Task> tempTaskList = new ArrayList<>();

        try (FileReader reader = new FileReader(fileName)) {
            // Parse the JSON file
            JSONArray tasks = (JSONArray) parser.parse(reader);

            // Parse each task into a temporary list
            for (Object obj : tasks) {
                JSONObject taskJson = (JSONObject) obj;
                Task newTask = parseTask(taskJson);
                tempTaskList.add(newTask);

                // Check for conflicts
                if (!model.checkTaskConflicts(newTask)) {
                    throw new IllegalStateException("Task conflict detected for " + newTask.getName());
                }
            }

            // If all tasks are valid, add them to the model
            for (Task task : tempTaskList) {
                model.addTask(task);
            }

            System.out.println("All tasks read successfully.");

        } catch (ParseException e) {
            System.out.println("Error: Invalid JSON syntax. " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error reading tasks: " + e.getMessage());
        }
    }

    /**
     * Parses a task description from a JSON file into a task.
     * @param taskJson the task json to be parsed.
     * @return the parsed Task.
     */
    private Task parseTask(JSONObject taskJson) {
        // Extract common attributes
        String name = (String) taskJson.get("Name");
        String type = (String) taskJson.get("Type");
        float startTime = Float.parseFloat(taskJson.get("StartTime").toString());
        float duration = Float.parseFloat(taskJson.get("Duration").toString());

        switch (type) { //removed to lowercase, since the capitalization needs to be exact.
            case "Visit", "Shopping", "Appointment": //transient task types
                int date = Integer.parseInt(taskJson.get("Date").toString());
                return new TransientTask(name, type, startTime, duration, date, null);

            case "Class", "Study", "Sleep", "Exercise", "Work", "Meal": //recurring task types
                int startDate = Integer.parseInt(taskJson.get("StartDate").toString());
                int endDate = Integer.parseInt(taskJson.get("EndDate").toString());
                int frequency = Integer.parseInt(taskJson.get("Frequency").toString());
                return new RecurringTask(name, type, startTime, duration, startDate, frequency, endDate);

            case "Cancellation": //anti task types
                int antiDate = Integer.parseInt(taskJson.get("Date").toString());
                return new AntiTask(name, type, startTime, duration, antiDate);

            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
    }
}
