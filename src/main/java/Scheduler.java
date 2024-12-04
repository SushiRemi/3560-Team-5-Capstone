import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class Scheduler {

    private Model model;

    public Scheduler(Model model) {
        this.model = model;
    }

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

    private LocalDate convertIntToLocalDate(int date) {
        String dateString = String.valueOf(date);
        // Extract year, month, and day
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(4, 6));
        int day = Integer.parseInt(dateString.substring(6, 8));
        return LocalDate.of(year, month, day); // Create and return LocalDate
    }

    private String formatTask(Task task) {
        return task.getName() + " - " + task.getType() + " on " + task.getDate(); // Format as needed
    }
}
