import java.util.ArrayList;

public class RecurringTask extends Task {
    private int frequency;
    private float endDate;
    private ArrayList<AntiTask> antiTasks;

    public RecurringTask(String name, String type, float startTime, float duration, int date, int frequency, float endDate){
        super(name, type, startTime, duration, date);
        this.frequency = frequency;
        this.endDate = endDate;
        ArrayList<AntiTask> temp = new ArrayList<>();
        this.antiTasks = temp;
    }

    // no argument constuctor
    public RecurringTask() {
        super();
        this.frequency = 0; 
        this.endDate = 0.0f; 
        this.antiTasks = new ArrayList<>(); 
    }


    public AntiTask getAntiTask(int position){
        return this.antiTasks.get(position);
    }
    
    public void addAntiTask(AntiTask antiTask){
        antiTasks.add(antiTask);
    }

    public void removeAntiTask(int position){
        this.antiTasks.remove(position);
    }

    public void removeAntiTask(AntiTask task){
        this.antiTasks.remove(task);
    }

    public void setFrequency(int frequency){
        this.frequency = frequency;
    }

    public int getFrequency(){
        return this.frequency;
    }

    public void setEndDate(float endDate){
        this.endDate = endDate;
    }

    public float getEndDate(){
        return this.endDate;
    }

}
