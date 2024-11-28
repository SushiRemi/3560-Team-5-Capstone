import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField startDateField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField endDateField = new JTextField();
        JTextField frequencyField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Start Date:"));
        panel.add(startDateField);
        panel.add(new JLabel("Duration:"));
        panel.add(durationField);
        panel.add(new JLabel("End Date:"));
        panel.add(endDateField);
        panel.add(new JLabel("Frequency:"));
        panel.add(frequencyField);

        JButton createButton = new JButton("Create Task");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String type = typeField.getText();
                Integer startDate = Integer.parseInt(startDateField.getText());
                Float duration = Float.parseFloat(durationField.getText());
                Integer endDate = endDateField.getText().isEmpty() ? null : Integer.parseInt(endDateField.getText());
                Integer frequency = frequencyField.getText().isEmpty() ? null : Integer.parseInt(frequencyField.getText());
                controller.createTask(name, type, startDate, duration, endDate, frequency);
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
                controller.saveScheduleToFile();
            }
        });

        panel.add(createButton);
        panel.add(deleteButton);
        panel.add(saveButton);

        add(panel, BorderLayout.SOUTH);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void updateTaskList(ArrayList<Task> tasks) {
        taskListModel.clear();
        for (Task task : tasks) {
            float endTime = task.getStartTime() + task.getDuration();
            taskListModel.addElement(task.getName() + " - " + task.getType() + " - Start: " + task.getStartTime() + " - End: " + endTime);
        }
    }

    public static void main(String[] args) {
        Model model = new Model();
        Viewer viewer = new Viewer();
        Controller controller = new Controller(model, viewer);
        viewer.setVisible(true);
    }
}