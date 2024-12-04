import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Scheduler works with Model to turn a task list into a proper schedule with formatted dates and times.
 */
public class Scheduler {

    /**
     * The connected model.
     */
    private Model model;

    /**
     * The constructor for the scheduler.
     * @param model the Model to connect to.
     */
    public Scheduler(Model model) {
        this.model = model;
    }

    /**
     * Returns all the tasks in a given period.
     * @param startDateStr  The start date of the period to check. Formatted as: YYYYMMDD
     * @param viewType  The type of period to check (day, week, month)
     * @return an arraylist of tasks within the given period.
     */
    public ArrayList<Task> viewSchedule(String startDateStr, String viewType) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate;

        // Attempt to parse the start date
        try {
            startDate = LocalDate.parse(startDateStr, formatter);
        } catch (Exception e) {
            System.out.println("Error: Invalid Start Date!");
            return null;
        }

        System.out.println("Checking tasks for the " + viewType + " of " + startDate);

        List<Task> tasks = model.getTasks();
        ArrayList<Task> taskList = new ArrayList<Task>();
        for (Task task : tasks) {
            // Check for anti-tasks logic
            if (task instanceof AntiTask) {
                continue; // Skip anti-tasks
            }

            // Filtering based on the viewType
            if (isTaskInViewPeriod(task, startDate, viewType)) {
                System.out.println(formatTask(task));
                taskList.add(task);
            }
        }

        return taskList;
    }

    /**
     * Thecks to see if a given task is within a specified time period.
     * @param task the task to check.
     * @param startDate the start date of the period to check. Formatted as: YYYYMMDD
     * @param viewType  the type of period to check (day, week, month)
     * @return true if task is in view period, false otherwise.
     */
    private boolean isTaskInViewPeriod(Task task, LocalDate startDate, String viewType) {
        LocalDate taskDate = convertIntToLocalDate(task.getDate()); // Convert int to LocalDate

        switch (viewType.toLowerCase()) {
            case "day":
                return taskDate.isEqual(startDate);
            case "week":
                return !taskDate.isBefore(startDate) && !taskDate.isAfter(startDate.plusDays(6));
            case "month":
                return taskDate.getMonth() == startDate.getMonth() && taskDate.getYear() == startDate.getYear();
            default:
                return false;
        }
    }

    /**
     * Converts an integer value to a LocalDate object. Int value must be in format: YYYYMMDD.
     * @param date the integer date to convert.
     * @return the date as a LocalDate object.
     */
    private LocalDate convertIntToLocalDate(int date) {
        String dateString = String.valueOf(date);
        // Extract year, month, and day
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(4, 6));
        int day = Integer.parseInt(dateString.substring(6, 8));
        return LocalDate.of(year, month, day); // Create and return LocalDate
    }

    /**
     * Formats the task to show its name, type, and date.
     * @param task  the task to format.
     * @return  the formatted task information, as a String.
     */
    private String formatTask(Task task) {
        return task.getName() + " - " + task.getType() + " on " + task.getDate(); // Format as needed
    }
}
