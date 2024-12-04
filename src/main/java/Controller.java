import java.util.ArrayList;

public class Controller {
    private Model model;
    private Viewer viewer;
    private Scheduler scheduler;

    public Controller(Model model, Viewer viewer) {
        this.model = model;
        this.viewer = viewer;
        this.scheduler = new Scheduler(model);
        this.viewer.setController(this);
    }

    public boolean createTask(String name, String type, Integer startDate, Float startTime, Float duration, Integer endDate, Integer frequency){
        if(model.createTask(name, type, startDate, startTime, duration, endDate, frequency)){
            viewTasks();
            return true;
        } else {
            viewTasks();
            return false;
        }
    }

    public void editTask(String operation, String taskName, String argument) {
        model.editTask(operation, taskName, argument);
        viewTasks();
    }

    public void deleteTask(String taskName) {
        model.deleteTask(taskName);
        viewTasks();
    }

    public void saveScheduleToFile(String fileName) {
        System.out.println(fileName);
        model.scheduleToFile(fileName);
        viewTasks();
    }

    public void viewSchedule(String startDate, String viewType) {
        ArrayList<Task> taskList = scheduler.viewSchedule(startDate, viewType);
        viewer.showScheduleViewer(taskList, Integer.parseInt(startDate), viewType);
        viewTasks();
    }

    public void readScheduleFromFile(String fileName) {
        model.readScheduleFromFile(fileName);
        viewTasks();
    }

    public void viewTasks() {
        viewer.updateTaskList(model.getTasks(), viewer.getListModel(), 0, null);
    }
}