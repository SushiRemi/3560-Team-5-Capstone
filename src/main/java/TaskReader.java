
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class TaskReader {
    private final Model model;

    public TaskReader(Model model) {
        this.model = model;
    }

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
