/**
 * Task is the primary data structure class made for the program. All other task classes are subclasses of this class.
 */
public class Task {

    /**
     * The name of the task.
     */
    private String name;

    /**
     * The type of task.
     */
    private String type;

    /**
     * The time at which the task starts, rounded to the nearest 15 minutes.
     */
    private float startTime;

    /**
     * The duration of the task, rounded to the nearest 15 minutes.
     */
    private float duration;

    /**
     * The date which the task starts, represented as YYYYMMDD.
     */
    private int date;

    /**
     * The primary constructor for Task objects.
     * @param name      the name of the task
     * @param type      the type of task
     * @param startTime the time at which the task starts, rounded to the nearst 15 minutes.
     * @param duration  the length of the task, rounded to the nearest 15 minutes.
     * @param date      the date which the task starts, represented as YYYYMMDD.
     */
    public Task(String name, String type, float startTime, float duration, int date) {
        this.name = name;
        this.type = type;
        this.startTime = startTime;
        this.duration = duration;
        this.date = date;
    }

    /**
     * A no argument, default constructor for Task objects.
     */
    public Task() {
        this.name = "";
        this.type = "";
        this.startTime = 0.0f;
        this.duration = 0.0f;
        this.date = 0;
    }

    /**
     * Getter method for the task name.
     * @return the name of the task.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method for the task type.
     * @return the type of the task.
     */
    public String getType() {
        return type;
    }

    /**
     * Getter method for the task's start time.
     * @return the start time of the task, in decimal form.
     */
    public float getStartTime() {
        return startTime;
    }

    /**
     * Getter method for the task's duration.
     * @return the duration of the task, in decimal form.
     */
    public float getDuration() {
        return duration;
    }

    /**
     * Getter method for the date of the task.
     * @return the date of the task, in the form YYYYMMDD.
     */
    public int getDate() {
        return date;
    }

    /**
     * Sets the name of the task.
     * @param name the new name of the task.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the type of task.
     * @param type the new type of task.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the start time of the task.
     * @param startTime the new start time of the task. Should be rounded to the nearest 15 minutes.
     */
    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the task duration.
     * @param duration the new duration of the task. Should be rounded to the nearest 15 minutes.
     */
    public void setDuration(float duration) {
        this.duration = duration;
    }

    /**
     * Sets the date of the task.
     * @param date the new date of the task, in YYYYMMDD format.
     */
    public void setDate(int date) {
        this.date = date;
    }
}
