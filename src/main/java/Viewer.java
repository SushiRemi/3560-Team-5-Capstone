import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

        JButton viewTasksButton = new JButton("View Tasks");
        viewTasksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.viewTasks();
            }
        });

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

        JButton editButton = new JButton("Edit Task");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTask = taskList.getSelectedValue();
                if (selectedTask != null) {
                    //bring up same menu as create task
                    showTaskEntryDialog();
                    //--controller.editTask(selectedTask.split(" - ")[0]);
                }
            }
        });

        panel.add(createButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(saveButton);
        panel.add(viewTasksButton);
        panel.add(readButton);

        add(panel, BorderLayout.SOUTH);
    }

    private void showTaskEntryDialog() {
        JDialog dialog = new JDialog(this, "Create Task", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new FlowLayout());

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
                try {
                    String name = nameField.getText();
                    String type = typeBox.getSelectedItem().toString();
                    Integer startDate = Integer.parseInt(startDateField.getText());
                    Float startTime = Float.parseFloat(startTimeField.getText());
                    Float duration = Float.parseFloat(durationField.getText());
                    Integer endDate = endDateField.getText().isEmpty() ? null : Integer.parseInt(endDateField.getText());
                    Integer frequency = frequencyField.getText().isEmpty() ? null : Integer.parseInt(frequencyField.getText());

                    if(controller.createTask(name, type, startDate, startTime, duration, endDate, frequency)){
                        //Show user that task was successfully created.
                        System.out.println("DEBUG: Task Created");
                        JOptionPane.showMessageDialog(mainPanel, "Task successfully created", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        //Show user that there is input errors.
                        System.out.println("DEBUG: Task Failed");
                        JOptionPane.showMessageDialog(mainPanel, "Task information invalid, or conflicts with another task.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception submitException) {
                    System.out.println("DEBUG: Task Failed");
                    JOptionPane.showMessageDialog(mainPanel, "Task information invalid, or conflicts with another task.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                    
                
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                dialog.dispose();
            }
        });

        JPanel submitExitPanel = new JPanel(new GridLayout(0, 2));
        submitExitPanel.add(submitButton);
        submitExitPanel.add(exitButton);

        dialog.add(mainPanel);
        dialog.add(cardPanel);
        dialog.add(submitExitPanel);
        dialog.setVisible(true);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void updateTaskList(ArrayList<Task> tasks) {
        taskListModel.clear();
        ArrayList<Task> cancellationTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof AntiTask) {
                cancellationTasks.add(task);
            }
        }

        for (Task task : tasks) {
            if (task instanceof RecurringTask) {
                RecurringTask recurringTask = (RecurringTask) task;
                int frequency = recurringTask.getFrequency();
                int endDate = recurringTask.getEndDate();
                int currentDate = task.getDate();

                while (currentDate <= endDate) {
                    boolean isCancelled = false;
                    String cancellationName = "";
                    for (Task cancellationTask : cancellationTasks) {
                        if (cancellationTask.getDate() == currentDate && cancellationTask.getStartTime() == task.getStartTime()) {
                            isCancelled = true;
                            cancellationName = cancellationTask.getName();
                            break;
                        }
                    }
                    if (isCancelled) {
                        addCancelledTaskToList(task, currentDate, cancellationName);
                    } else {
                        addTaskToList(task, currentDate);
                    }
                    currentDate = incrementDate(currentDate, frequency);
                }
            } else if (!(task instanceof AntiTask)) {
                addTaskToList(task, task.getDate());
            }
        }
    }

    private void addTaskToList(Task task, int date) {
        String dateString = String.valueOf(date);
        String year = dateString.substring(0, 4);
        String month = dateString.substring(4, 6);
        String day = dateString.substring(6, 8);
        dateString = month + "/" + day + "/" + year;

        float endTime = task.getStartTime() + task.getDuration();
        int endHour = (int) Math.floor(endTime);
        int endMinute = (int) ((endTime - endHour) * 60);

        int startHour = (int) Math.floor(task.getStartTime());
        int startMinute = (int) ((task.getStartTime() - startHour) * 60);
        taskListModel.addElement(task.getName() + " - " + task.getType() + " - " + dateString + " - Start: " + startHour + ":" + startMinute + " - End: " + endHour + ":" + endMinute);
    }

    private void addCancelledTaskToList(Task task, int date, String cancellationName) {
        String dateString = String.valueOf(date);
        String year = dateString.substring(0, 4);
        String month = dateString.substring(4, 6);
        String day = dateString.substring(6, 8);
        dateString = month + "/" + day + "/" + year;

        float endTime = task.getStartTime() + task.getDuration();
        int endHour = (int) Math.floor(endTime);
        int endMinute = (int) ((endTime - endHour) * 60);

        int startHour = (int) Math.floor(task.getStartTime());
        int startMinute = (int) ((task.getStartTime() - startHour) * 60);
        taskListModel.addElement("<html><strike>" + task.getName() + " - " + task.getType() + " - " + dateString + " - Start: " + startHour + ":" + startMinute + " - End: " + endHour + ":" + endMinute + "</strike> - Cancelled by " + cancellationName + "</html>");
    }

    private int incrementDate(int date, int frequency) {
        String dateString = String.valueOf(date);
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(4, 6));
        int day = Integer.parseInt(dateString.substring(6, 8));
        LocalDate localDate = LocalDate.of(year, month, day);
        localDate = localDate.plusDays(frequency);
        return Integer.parseInt(localDate.format(DateTimeFormatter.BASIC_ISO_DATE));
    }

    public static void main(String[] args) {
        Model model = new Model();
        Viewer viewer = new Viewer();
        Controller controller = new Controller(model, viewer);
        viewer.setVisible(true);
    }
}