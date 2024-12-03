import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Create a new Model instance
        Model model = new Model();
        Scheduler scheduler = new Scheduler(model);

        // Create some tasks
        model.createTask("Task 1", "Appointment", 20241202, 15.0f, 1.0f, null, null);
        model.createTask("Task 2", "Class", 20241203, 12.0f, 1.75f, 20250528, 7);
        model.createTask("Task 3", "Cancellation", 20241203, 12.0f, 1.75f, null, null);

        // User input for viewing schedule
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter start date (yyyy-MM-dd): ");
        String startDate = scanner.nextLine();
        System.out.print("View schedule for (day/week/month): ");
        String viewType = scanner.nextLine();

        scheduler.viewSchedule(startDate, viewType);
        
        // Print all tasks
        System.out.println("All tasks:");
        for (Task task : model.getTasks()) {
            System.out.println(task.getName() + " - " + task.getType());
        }

        // Edit a task
        model.editTask("edit name", "Task 1", "Updated Task 1");

        // Print all tasks after editing
        System.out.println("\nTasks after editing:");
        for (Task task : model.getTasks()) {
            System.out.println(task.getName() + " - " + task.getType());
        }

        // Delete a task
        model.deleteTask("Task 2");

        // Print all tasks after deletion
        System.out.println("\nTasks after deletion:");
        for (Task task : model.getTasks()) {
            System.out.println(task.getName() + " - " + task.getType());
        }

        // Save schedule to file
        model.scheduleToFile("schedule.json");
        scanner.close();
    }
}