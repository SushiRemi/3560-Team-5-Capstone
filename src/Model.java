import java.util.ArrayList;
import java.util.UUID;

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

    public void deleteTask(String taskName){
        for( int i = 0; i < TaskList.size(); i++){
            if(TaskList.getName().get(i).equals(taskName)){
                TaskList.remove(i);
                System.out.println("Object removed");
                return;
            }
        }
        System.out.println("Object not found");
    }
    
}
