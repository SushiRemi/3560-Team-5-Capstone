public class Controller {
    private Model model;
    private Viewer viewer;

    public Controller(Model model, Viewer viewer) {
        this.model = model;
        this.viewer = viewer;
        this.viewer.setController(this);
    }

    public void createTask(String name, String type, Integer startDate, Float startTime, Float duration, Integer endDate, Integer frequency) {
        model.createTask(name, type, startDate, startTime, duration, endDate, frequency);
        viewer.updateTaskList(model.getTasks());
    }

    public void editTask(String operation, String taskName, String argument) {
        model.editTask(operation, taskName, argument);
        viewer.updateTaskList(model.getTasks());
    }

    public void deleteTask(String taskName) {
        model.deleteTask(taskName);
        viewer.updateTaskList(model.getTasks());
    }

    public void saveScheduleToFile() {
        model.scheduleToFile();
    }
}