import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

public class Viewer extends JFrame {
    private Controller controller;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;

    public Viewer() {
        setTitle("Task Scheduler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        JButton createButton = new JButton("Create Task");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTaskEntryDialog();
            }
        });

        JButton deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTask = taskList.getSelectedValue();
                if (selectedTask != null) {
                    controller.deleteTask(selectedTask.split(" - ")[0]);
                }
            }
        });

        JButton saveButton = new JButton("Save Schedule");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = JOptionPane.showInputDialog("Enter the filename to write to (e.g., schedule.json):");
                if (fileName != null && !fileName.trim().isEmpty()) {
                    controller.saveScheduleToFile(fileName);
                }
            }
        });

        JButton viewButton = new JButton("View Schedule");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startDate = JOptionPane.showInputDialog("Enter start date (yyyy-MM-dd):");
                String viewType = JOptionPane.showInputDialog("View schedule for (day/week/month):");
                controller.viewSchedule(startDate, viewType);
            }
        });

        JButton readButton = new JButton("Read from File");
        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = JOptionPane.showInputDialog("Enter the filename to read from (e.g., schedule.json):");
                if (fileName != null && !fileName.trim().isEmpty()) {
                    controller.readScheduleFromFile(fileName);
                }
            }
        });

        panel.add(createButton);
        panel.add(deleteButton);
        panel.add(saveButton);
        panel.add(viewButton);
        panel.add(readButton);

        add(panel, BorderLayout.SOUTH);
    }

    private void showTaskEntryDialog() {
        JDialog dialog = new JDialog(this, "Create Task", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(0, 2));

        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField startDateField = new JTextField();
        JTextField startTimeField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField endDateField = new JTextField();
        JTextField frequencyField = new JTextField();

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Type:"));
        dialog.add(typeField);
        dialog.add(new JLabel("Start Date (YYYYMMDD):"));
        dialog.add(startDateField);
        dialog.add(new JLabel("Start Time (HH:MM):"));
        dialog.add(startTimeField);
        dialog.add(new JLabel("Duration (HH:MM):"));
        dialog.add(durationField);
        dialog.add(new JLabel("End Date (YYYYMMDD):"));
        dialog.add(endDateField);
        dialog.add(new JLabel("Frequency:"));
        dialog.add(frequencyField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String type = typeField.getText();
                Integer startDate = Integer.parseInt(startDateField.getText());
                String startTimeTemp = startTimeField.getText();
                if (startTimeTemp.length() != 5) {
                    System.out.println("Error: Invalid Time Format!");
                    return;
                }
                Float hour = Float.valueOf(startTimeTemp.substring(0, 2));
                Integer minute = Integer.valueOf(startTimeTemp.substring(3, 5));
                Float min = Float.valueOf(minute / 15) * .25f;
                Float startTime = hour + min;

                String durationTemp = durationField.getText();
                if (durationTemp.length() != 5) {
                    System.out.println("Error: Invalid Time Format!");
                    return;
                }
                hour = Float.valueOf(durationTemp.substring(0, 2));
                minute = Integer.valueOf(durationTemp.substring(3, 5));
                min = Float.valueOf(minute / 15) * .25f;
                Float duration = hour + min;
                Integer endDate = endDateField.getText().isEmpty() ? null : Integer.parseInt(endDateField.getText());
                Integer frequency = frequencyField.getText().isEmpty() ? null : Integer.parseInt(frequencyField.getText());
                controller.createTask(name, type, startDate, startTime, duration, endDate, frequency);
                dialog.dispose();
            }
        });

        dialog.add(submitButton);
        dialog.setVisible(true);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void updateTaskList(ArrayList<Task> tasks) {
        taskListModel.clear();
        for (Task task : tasks) {
            String date = String.valueOf(task.getDate());

            String year = date.substring(0, 4);
            String month = date.substring(4, 6);
            String day = date.substring(6, 8);
            date = month + "/" + day + "/" + year;

            float endTime = task.getStartTime() + task.getDuration();
            int endHour = (int) Math.floor(endTime);
            int endMinute = (int) ((endTime - endHour) * 60);

            int startHour = (int) Math.floor(task.getStartTime());
            int startMinute = (int) ((task.getStartTime() - startHour) * 60);
            taskListModel.addElement(task.getName() + " - " + task.getType() + " - " + date + " - Start: " + startHour + ":" + startMinute + " - End: " + endHour + ":" + endMinute);
        }
    }

    public static void main(String[] args) {
        Model model = new Model();
        Viewer viewer = new Viewer();
        Controller controller = new Controller(model, viewer);
        viewer.setVisible(true);
    }
}