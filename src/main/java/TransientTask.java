/**
 * TransientTask is a subclass of the class Task, for tasks that only occur once.
 */
public class TransientTask extends Task {
    private AntiTask antiTask;

    public TransientTask(String name, String type, float startTime, float duration, int date, AntiTask antiTask){
        super(name, type, startTime, duration, date);
        this.antiTask = antiTask;
    }

    public TransientTask(){
        super();
        this.antiTask = null;
    }

    public void setAntiTask(AntiTask antiTask){
        this.antiTask = antiTask;
    }

    public AntiTask getAntiTask(){
        return this.antiTask;
    }
    //TransientTask is a subclass of Task but does not require additional features. The Model class will handle the differences between the two.
}
