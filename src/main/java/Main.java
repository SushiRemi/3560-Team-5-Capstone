public class Main {
    public static void main(String[] args) {
        // Create a new Model instance
        Model model = new Model();

        // Create some tasks
        model.createTask("Task 1", "transient", 9, 1.0f, 10, null);
        model.createTask("Task 2", "recurring", 10, 1.0f, 11, 2);
        model.createTask("Task 3", "anti", 11, 1.0f, 12, null);

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
        model.scheduleToFile();
    }
}