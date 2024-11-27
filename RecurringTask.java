import java.util.ArrayList;

/**
 * RecurringTask is a subclass of the class Task, for tasks that occur on a regular basis, more than once.
 */
public class RecurringTask extends Task {
    private int frequency;
    private int endDate;
    private ArrayList<AntiTask> antiTasks;

    /**
     * The primary constructor for RecurringTask objects.
     * @param name      the name of the task
     * @param type      the type of task
     * @param startTime the time at which the task starts, rounded to the nearst 15 minutes.
     * @param duration  the length of the task, rounded to the nearest 15 minutes.
     * @param date      the date which the task starts, represented as YYYYMMDD.
     * @param frequency the number of days until the task occurs again (1 = daily, 7 = weekly).
     * @param endDate the date which the task stops repeating, represented as YYYYMMDD.
     */
    public RecurringTask(String name, String type, float startTime, float duration, int date, int frequency, int endDate){
        super(name, type, startTime, duration, date);
        this.frequency = frequency;
        this.endDate = endDate;
        ArrayList<AntiTask> temp = new ArrayList<>();
        this.antiTasks = temp;
    }

    /**
     * A no argument, default constructor for RecurringTask objects.
     */
    public RecurringTask() {
        super();
        this.frequency = 0; 
        this.endDate = 0.0f; 
        this.antiTasks = new ArrayList<>(); 
    }

    /**
     * Getter method for the ArrayList of AntiTasks.
     * @param position The array index of the desired AntiTask.
     * @return the AntiTask object at index [position].
     */
    public AntiTask getAntiTask(int position){
        return this.antiTasks.get(position);
    }
    
    /**
     * Adds an AntiTask object to the ArrayList of AntiTask objects antiTasks.
     * @param antiTask the AntiTask object to be added.
     */
    public void addAntiTask(AntiTask antiTask){
        antiTasks.add(antiTask);
    }

    /**
     * Removes an AntiTask object from the ArrayList antiTasks.
     * @param position the index of the AntiTask object to be removed.
     */
    public void removeAntiTask(int position){
        this.antiTasks.remove(position);
        //note: add code to protect against indexOutOfBounds error.
    }

    /**
     * Removes an AntiTask object from the ArrayList antiTasks.
     * @param task a reference to the AntiTask object to be removed.
     */
    public void removeAntiTask(AntiTask task){
        this.antiTasks.remove(task);
        //note: might want to add code that checks whether or not the desired task even exists.
    }

    /**
     * Sets the frequency of the recurring task.
     * @param frequency the number of days until the task occurs again (1 = daily, 7 = weekly).
     */
    public void setFrequency(int frequency){
        this.frequency = frequency;
    }

    /**
     * Getter method for the frequency of the recurring task.
     * @return the number of days until the task occurs again (1 = daily, 7 = weekly).
     */
    public int getFrequency(){
        return this.frequency;
    }

    /**
     * Sets the end date of the recurring task.
     * @param endDate the date at which the task will stop repeating. In the format YYYYMMDD.
     */
    public void setEndDate(int endDate){
        this.endDate = endDate;
    }

    /**
     * Getter method for the end date of the recurring task.
     * @return the date at which the task will stop repeating. In the format YYYYMMDD.
     */
    public float getEndDate(){
        return this.endDate;
    }

}
