import java.awt.*;
import java.awt.event.*;
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

//        JButton viewButton = new JButton("View Schedule");
//        viewButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String startDate = JOptionPane.showInputDialog("Enter start date (yyyy-MM-dd):");
//                String viewType = JOptionPane.showInputDialog("View schedule for (day/week/month):");
//                controller.viewSchedule(startDate, viewType);
//            }
//        });

        JButton readButton = new JButton("Load Schedule");
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
//        panel.add(viewButton);
        panel.add(readButton);

        add(panel, BorderLayout.SOUTH);
    }

    private void showTaskEntryDialog() {
        JDialog dialog = new JDialog(this, "Create Task", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(0, 2));
        JTextField nameField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Visit", "Shopping", "Appointment", "Class", "Study", "Sleep", "Exercise", "Work", "Meal", "Cancellation"});
        JTextField startDateField = new JTextField();
        JTextField startTimeField = new JTextField();
        JTextField durationField = new JTextField();

        mainPanel.add(new JLabel("Name:"));
        mainPanel.add(nameField);
        mainPanel.add(new JLabel("Type:"));
        mainPanel.add(typeBox);
        mainPanel.add(new JLabel("Start Date (YYYYMMDD):"));
        mainPanel.add(startDateField);
        mainPanel.add(new JLabel("Start Time (float, e.g., 15.0):"));
        mainPanel.add(startTimeField);
        mainPanel.add(new JLabel("Duration (float, e.g., 1.0):"));
        mainPanel.add(durationField);

        JPanel cardPanel = new JPanel(new CardLayout());
        JPanel transientPanel = new JPanel();
        JPanel recurringPanel = new JPanel(new GridLayout(0, 2));

        JTextField endDateField = new JTextField();
        JTextField frequencyField = new JTextField();
        recurringPanel.add(new JLabel("End Date (YYYYMMDD):"));
        recurringPanel.add(endDateField);
        recurringPanel.add(new JLabel("Frequency:"));
        recurringPanel.add(frequencyField);

        cardPanel.add(transientPanel, "Transient");
        cardPanel.add(recurringPanel, "Recurring");

        typeBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedType = (String) e.getItem();
                CardLayout cl = (CardLayout) (cardPanel.getLayout());
                if (selectedType.equals("Class") || selectedType.equals("Study") || selectedType.equals("Sleep") || selectedType.equals("Exercise") || selectedType.equals("Work") || selectedType.equals("Meal")) {
                    cl.show(cardPanel, "Recurring");
                } else {
                    cl.show(cardPanel, "Transient");
                }
            }
        });

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String type = typeBox.getSelectedItem().toString();
                Integer startDate = Integer.parseInt(startDateField.getText());
                Float startTime = Float.parseFloat(startTimeField.getText());
                Float duration = Float.parseFloat(durationField.getText());
                Integer endDate = endDateField.getText().isEmpty() ? null : Integer.parseInt(endDateField.getText());
                Integer frequency = frequencyField.getText().isEmpty() ? null : Integer.parseInt(frequencyField.getText());
                controller.createTask(name, type, startDate, startTime, duration, endDate, frequency);
                dialog.dispose();
            }
        });

        dialog.add(mainPanel, BorderLayout.NORTH);
        dialog.add(cardPanel, BorderLayout.CENTER);
        dialog.add(submitButton, BorderLayout.SOUTH);
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