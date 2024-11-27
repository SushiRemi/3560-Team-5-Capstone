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
}
