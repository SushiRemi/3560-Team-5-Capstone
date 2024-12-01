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

        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField startDateField = new JTextField();
        JTextField startTimeField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField endDateField = new JTextField();
        JTextField frequencyField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Start Date (YYYYMMDD):"));
        panel.add(startDateField);
        panel.add(new JLabel("Start Time (HH:MM):"));
        panel.add(startTimeField);
        panel.add(new JLabel("Duration (HH:MM):"));
        panel.add(durationField);
        panel.add(new JLabel("End Date (YYYYMMDD):"));
        panel.add(endDateField);
        panel.add(new JLabel("Frequency:"));
        panel.add(frequencyField);

        JButton createButton = new JButton("Create Task");
        createButton.addActionListener(new ActionListener() {
            @Override
            // rounding moved to here 
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String type = typeField.getText();
                Integer startDate = Integer.parseInt(startDateField.getText());
                String startTimeTemp = startTimeField.getText();
                Float hour = Float.valueOf(startTimeTemp.substring(0,2)); // hh:mm
                Integer minute  = Integer.valueOf(startTimeTemp.substring(3, 5));
                System.out.println("minute: " + minute);
                Float min = Float.valueOf(minute / 15)*.25f;
                System.out.println("changed minute: " + min);
                Float startTime = hour + min;
                String durationTemp = durationField.getText();
                hour = Float.valueOf(durationTemp.substring(0,2)); // hh:mm
                minute  = Integer.valueOf(durationTemp.substring(3, 5));
                min = Float.valueOf(minute / 15)*.25f;
                Float duration = hour + min;
                Integer endDate = endDateField.getText().isEmpty() ? null : Integer.parseInt(endDateField.getText());
                Integer frequency = frequencyField.getText().isEmpty() ? null : Integer.parseInt(frequencyField.getText());
                controller.createTask(name, type, startDate, startTime, duration, endDate, frequency);
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
            String date = String.valueOf(task.getDate());

            String year = date.substring(0,4);
            String month = date.substring(4,6);
            String day = date.substring(6,8);
            date = month + "/" + day + "/" + year;

            float endTime = task.getStartTime() + task.getDuration();
            int endHour = (int) Math.floor(endTime);
            int endMinute = (int)((endTime- endHour)*60);

            int startHour = (int) Math.floor(task.getStartTime());
            int startMinute = (int)((task.getStartTime() - startHour)*60);
            taskListModel.addElement(task.getName() + " - " + task.getType() + " - "+ date + " - Start: " + startHour + ":" + startMinute + " - End: " + endHour + ":" + endMinute);
        }
    }

    public static void main(String[] args) {
        Model model = new Model();
        Viewer viewer = new Viewer();
        Controller controller = new Controller(model, viewer);
        viewer.setVisible(true);
    }
}