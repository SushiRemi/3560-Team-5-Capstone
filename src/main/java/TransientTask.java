/**
 * TransientTask is a subclass of the class Task, for tasks that only occur once.
 */
public class TransientTask extends Task {
    /**
     * The antiTask object that cancels out the current TransientTask, if one exists.
     */
    private AntiTask antiTask;

    /**
     * Primary constructor for TransientTask
     * @param name the name of the task.
     * @param type the type of task.
     * @param startTime the start time of the task.
     * @param duration the duration of the task.
     * @param date the date of the task.
     * @param antiTask the antiTask connected to this task, if one exists.
     */
    public TransientTask(String name, String type, float startTime, float duration, int date, AntiTask antiTask){
        super(name, type, startTime, duration, date);
        this.antiTask = antiTask;
    }

    /**
     * Default constructor for Transient Task, all fields are nulled.
     */
    public TransientTask(){
        super();
        this.antiTask = null;
    }

    /**
     * Sets the antitask for this task.
     * @param antiTask the antiTask to be added.
     */
    public void setAntiTask(AntiTask antiTask){
        this.antiTask = antiTask;
    }

    /**
     * Gets the current antiTask, if one exists.
     * @return the current Antitask.
     */
    public AntiTask getAntiTask(){
        return this.antiTask;
    }
}
