import org.json.simple.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.UUID;

import javax.lang.model.type.NullType;
import javax.swing.plaf.TreeUI;
import java.io.FileWriter;
import java.io.IOException;

public class Model {
    private ArrayList<Task> TaskList;

    public Model(){
        TaskList = new ArrayList<Task>();
    }

    public void createTask(String taskName, String taskType, 
                            Integer startDate, Float duration,
                            Integer endDate, Integer frequency){
        // create new task
        Task newTask;

        if(taskType.equalsIgnoreCase("transient")){
            newTask = new TransientTask();
        }else if (taskType.equalsIgnoreCase("recurring")) {
            newTask = new RecurringTask();
        } else if (taskType.equalsIgnoreCase("anti")){
            newTask = new AntiTask();
        } else {
            System.out.println("Invalid Task Type!");
            return;
        }
        
        // set attributes to new task
        newTask.setName(taskName);
        newTask.setType(taskType);
        newTask.setStartTime(startDate);
        newTask.setDuration(duration);
        if (!taskType.equalsIgnoreCase("transient")) {
            if (newTask instanceof RecurringTask) {
                ((RecurringTask) newTask).setEndDate(endDate);
                ((RecurringTask) newTask).setFrequency(frequency);
            } else if (newTask instanceof AntiTask) {
                ((AntiTask) newTask).setEndDate(endDate);
            } else {
                System.out.println("The task type does not support an end date.");
            }
        }

        
        // add task to task list, if no schedule conflicts
        if(checkTaskConflicts(newTask)){
            TaskList.add(newTask);
        }else{
            System.out.println("New Task conflicts with existing tasks!");
        }
        
    }
    public Integer getTaskByName(String taskName){
        for( int i = 0; i < TaskList.size(); i++){
            if(TaskList.get(i).getName().equals(taskName)){
                return i;
            }
        }
        return -1;
    }

    public void deleteTask(String taskName){
        int index = getTaskByName(taskName);
        if(index != -1){
            TaskList.remove(index);
        }else{
            System.out.println("Object not found");
        }        
    }
    /*
     * need more clarification on how this will work
     */
    public void editTask(String operation, String taskName, String argument){
        int index = getTaskByName(taskName);
        if (index == -1){
            System.out.println("Task not found!");
            return;
        }
        
        // Task in reference
        Task task = TaskList.get(index);

        // Call appropriate functions
        switch (operation) {
            case "edit name":
                editTaskName(argument, task);
                break;
            case "edit start time":
                safelyEditTaskTime(argument, task, "start");
                break;
            case "edit end time":
            if(!task.getType().equalsIgnoreCase("transient")){
                safelyEditTaskTime(argument, task, "end");
            }else{
                System.out.println("Task Does not have Duration!");
            }
                break;
            case "edit duration":
            if(task.getType().equalsIgnoreCase("transient")){
                safelyEditTaskDuration(argument, task);
            }else{
                System.out.println("Task Does not have Duration!");
            }
                break;
            case "edit frequency":
                if(!task.getType().equalsIgnoreCase("transient")){
                    editTaskFrequency(argument, task);
                }else{
                    System.out.println("Task Does not have Frequency!");
                }
                break;
            default:
                System.out.println("Invalid operation!");
        }
    }

    // returns a DEEP COPY of the current tasks
    public ArrayList<Task> getTasks(){
        ArrayList<Task> newList = new ArrayList<>();
        for (Task temp : TaskList){
            newList.add(temp);
        }
        return newList;
    }
    
    public void scheduleToFile() {
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
        try (FileWriter file = new FileWriter("src/main/resources/schedule.json")) {
            file.write(tasksJsonArray.toJSONString());
            file.flush();
            System.out.println("Schedule saved to 'schedule.json'.");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    
    public void readScheduleFromFile(){

    }






    // Abstracted helper functions:

    private void editTaskName(String newName, Task task) {
        task.setName(newName);
        System.out.println("Task name updated successfully!");
    }    

    private void safelyEditTaskTime(String newTime, Task task, String type) {
        // Store original start time and calculate end time using duration
        float originalStartTime = task.getStartTime();
        float originalEndTime = originalStartTime + task.getDuration();
    
        // Update time
        try {
            float newTimeValue = Float.parseFloat(newTime);
            if (type.equals("start")) {
                task.setStartTime(newTimeValue);
            } else if (type.equals("end")) {
                if (task instanceof RecurringTask || task instanceof AntiTask) {
                    // Adjust end time only for tasks that support it
                    float duration = task.getDuration();
                    task.setStartTime(newTimeValue - duration);
                } else {
                    System.out.println("End time adjustment not supported for this task type.");
                    return;
                }
            }
    
            // Check for conflicts
            if (!checkTaskConflicts(task)) {
                // Revert to original times if conflict
                task.setStartTime(originalStartTime);
                task.setDuration(originalEndTime - originalStartTime);
                System.out.println("Conflict detected! Time not updated.");
            } else {
                System.out.println("Task time updated successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid time format! Please enter a valid float value.");
        }
    }
    
    private void safelyEditTaskDuration(String newDuration, Task task) {
        // Store original duration
        float originalDuration = task.getDuration();
    
        // Update duration
        task.setDuration(Float.parseFloat(newDuration));
    
        // Check for conflicts
        if (!checkTaskConflicts(task)) {
            // Revert to original duration if conflict
            task.setDuration(originalDuration);
            System.out.println("Conflict detected! Duration not updated.");
        } else {
            System.out.println("Task duration updated successfully!");
        }
    }

    private void editTaskFrequency(String newFrequency, Task task) {
        // Check if task supports frequency
        if (task instanceof RecurringTask) {
            try {
                int frequency = Integer.parseInt(newFrequency);
                ((RecurringTask) task).setFrequency(frequency);
                System.out.println("Task frequency updated successfully!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid frequency format! Please enter a valid integer value.");
            }
        } else {
            System.out.println("Frequency cannot be set for this task type.");
        }
    }

    // Returns FALSE if conflicts, TRUE if it is OK
    private boolean checkTaskConflicts(Task newTask) {
        for (int i = 0; i < TaskList.size(); i++) {
            Task existingTask = TaskList.get(i);
            // Check if the tasks overlap
        // Calculate end times dynamically using startTime and duration
        float existingTaskEndTime = existingTask.getStartTime() + existingTask.getDuration();
        float newTaskEndTime = newTask.getStartTime() + newTask.getDuration();

        // Check if the tasks overlap
        if (newTask.getStartTime() < existingTaskEndTime && newTaskEndTime > existingTask.getStartTime()) {
            return false; // Conflict found
        }
        }
        return true; // No conflicts
    }
    
    
}
