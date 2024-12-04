import java.util.ArrayList;

/**
 * Controller is the in-between class to handle interactions between the Viewer, Model, and the Scheduler
 */
public class Controller {
    /**
     * The connected model.
     */
    private Model model;

    /**
     * The connected viewer.
     */
    private Viewer viewer;

    /**
     * The connected Scheduler.
     */
    private Scheduler scheduler;

    /**
     * The constructor for Controller. Automatically connects to Model, Viewer, and Scheduler.
     * @param model     the Model to connect to
     * @param viewer    the Viewer to connect to.
     */
    public Controller(Model model, Viewer viewer) {
        this.model = model;
        this.viewer = viewer;
        this.scheduler = new Scheduler(model);
        this.viewer.setController(this);
    }

    /**
     * Attempts to create a task in the model with the given parameters. Updates the task list in viewer.
     * @param name  the name of the task.
     * @param type  the type of task.
     * @param startDate if recurring task, the first instance of the task. Otherwise the date of the task.
     * @param startTime the start time (as a float) of the task.
     * @param duration the duration (as a float) of the task.
     * @param endDate if recurring task, the cutoff date of the task. Does not need to have an occurance on said date.
     * @param frequency how often (in days) the recurring task repeats.
     * @return true if task succesfully created, false otherwise.
     */
    public boolean createTask(String name, String type, Integer startDate, Float startTime, Float duration, Integer endDate, Integer frequency){
        if(model.createTask(name, type, startDate, startTime, duration, endDate, frequency)){
            viewTasks();
            return true;
        } else {
            viewTasks();
            return false;
        }
    }

    /**
     * Removes a task from the model. If no task with given name is found, nothing will happen. Updates the task list in viewer.
     * @param taskName the name of the task to remove.
     */
    public void deleteTask(String taskName) {
        model.deleteTask(taskName);
        viewTasks();
    }

    /**
     * Attempts to save the current schedule to a file. Updates the task list in viewer.
     * @param fileName the name of the file to create/write to.
     */
    public void saveScheduleToFile(String fileName) {
        System.out.println(fileName);
        model.scheduleToFile(fileName);
        viewTasks();
    }

    /**
     * Retrieves all tasks in a given period and sends to the viewer to display. Updates the task list in viewer.
     * @param startDate the start date of the desired period to check
     * @param viewType the period of time to check (day, week, month)
     */
    public void viewSchedule(String startDate, String viewType) {
        ArrayList<Task> taskList = scheduler.viewSchedule(startDate, viewType);
        viewer.showScheduleViewer(taskList, Integer.parseInt(startDate), viewType);
        viewTasks();
    }

    /**
     * Reads the tasks from a specified file. Updates the task list in viewer.
     * @param fileName the name of the file to read tasks from.
     */
    public void readScheduleFromFile(String fileName) {
        model.readScheduleFromFile(fileName);
        viewTasks();
    }

    /**
     * Updates the task list in viewer.
     */
    public void viewTasks() {
        viewer.updateTaskList(model.getTasks(), viewer.getListModel(), 0, null);
    }

    // src/main/java/Controller.java
    /**
     * Edits a specified task, given new parameters. Updates the task list in viewer.
     * @param oldName the original task name.
     * @param newName   the new task name.
     * @param type  the new task type.
     * @param startDate the new task start date.
     * @param startTime the new task start time.
     * @param duration the new task duration.
     * @param endDate   if recurring, the new task end date.
     * @param frequency if recurring, the new task frequency.
     * @return true if successully edited, false otherwise.
     */
    public boolean editTask(String oldName, String newName, String type, Integer startDate, Float startTime, Float duration, Integer endDate, Integer frequency) {
        boolean result = model.editTask(oldName, newName, type, startDate, startTime, duration, endDate, frequency);
        if (result) {
            viewTasks();
        }
        return result;
    }

    /**
     * Gets a designated task.
     * @param taskName the task to be retrieved.
     * @return the desired task.
     */
    public Task getTaskByName(String taskName) {
        return model.getTaskByName(taskName);
    }

}