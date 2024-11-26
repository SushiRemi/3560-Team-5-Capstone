public class Task {
    private String name;
    private String type;
    private float startTime;
    private float duration;
    private int date;

    public Task(String name, String type, float startTime, float duration, int date) {
        this.name = name;
        this.type = type;
        this.startTime = startTime;
        this.duration = duration;
        this.date = date;
    }

    // No-argument constructor
    public Task() {
        this.name = "";
        this.type = "";
        this.startTime = 0.0f;
        this.duration = 0.0f;
        this.date = 0;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public float getStartTime() {
        return startTime;
    }

    public float getDuration() {
        return duration;
    }

    public int getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
