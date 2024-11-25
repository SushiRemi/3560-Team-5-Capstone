import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.UUID;

import javax.lang.model.type.NullType;
import javax.swing.plaf.TreeUI;

public class Model {
    private ArrayList<Task> TaskList;

    public Model(){
        TaskList = new ArrayList<Task>();
    }

    public void createTask(String taskName, String taskType, 
                            Float startDate, Float duration,
                            Float endDate, Integer Frequency){
        // create new task
        Task newTask = new Task();
        
        // set attributes to new task
        newTask.setName(taskName);
        newTask.setType(taskType);
        newTask.setStartTime(startDate);
        newTask.setDuration(duration);
        newTask.setEndDate(taskName);
        newTask.setFrequency(Frequency);
        
        // add task to task list, if no schedule conflicts
        if(checkTimeConflicts(newTask)){
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
                safelyEditTaskTime(argument, task, "end");
                break;
            case "edit duration":
                safelyEditTaskDuration(argument, task);
                break;
            case "edit frequency":
                editTaskFrequency(argument, task);
                break;
            case "edit type":
                editTaskType(argument, task);
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
    public void scheduleToFile(){

    }
    public void readScheduleFromFile(){

    }






    // Abstracted helper functions:

    private void editTaskName(String newName, Task task) {
        task.setName(newName);
        System.out.println("Task name updated successfully!");
    }    

    private void safelyEditTaskTime(String newTime, Task task, String type) {
        // Store original time
        float originalStartTime = task.getStartTime();
        float originalEndTime = task.getEndTime();
    
        // Update time
        try {
            float newTimeValue = Float.parseFloat(newTime);
            if (type.equals("start")) {
                task.setStartTime(newTimeValue);
            } else if (type.equals("end")) {
                task.setEndTime(newTimeValue);
            }
    
            // Check for conflicts
            if (!checkTimeConflicts(task)) {
                // Revert to original time if conflict
                task.setStartTime(originalStartTime);
                task.setEndTime(originalEndTime);
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
        if (!checkTimeConflicts(task)) {
            // Revert to original duration if conflict
            task.setDuration(originalDuration);
            System.out.println("Conflict detected! Duration not updated.");
        } else {
            System.out.println("Task duration updated successfully!");
        }
    }

    private void editTaskFrequency(String newFrequency, Task task) {
        task.setFrequency(newFrequency);
        System.out.println("Task frequency updated successfully!");
    }
    private void editTaskType(String newType, Task task) {
        task.setType(newType);
        System.out.println("Task type updated successfully!");
    }    

    private boolean checkTimeConflicts(Task newTask) {
        for (int i = 0; i < TaskList.size(); i++) {
            Task existingTask = TaskList.get(i);
            // Check if the tasks overlap
            if (newTask.getStartTime() < existingTask.getEndDate() && newTask.getEndDate() > existingTask.getStartTime()) {
                return false; // Conflict found
            }
        }
        return true; // No conflicts
    }
    
    
}
