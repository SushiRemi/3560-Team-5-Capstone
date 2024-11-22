import java.util.ArrayList;
import java.util.UUID;

import javax.lang.model.type.NullType;

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
        
        // add task to task list
        TaskList.add(newTask);
    }
    public Integer getTaskByName(String taskName){
        for( int i = 0; i < TaskList.size(); i++){
            if(TaskList.getName().get(i).equals(taskName)){
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
    public void editTask(String operation, String taskName){
        int index = getTaskByName(taskName);
        if (index == -1){
            System.out.println("Task not found!");
            return;
        }else{
            Task task = TaskList.get(index);
        }
        if(operation.equals("edit name")){
            task.setName();
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
    
}
