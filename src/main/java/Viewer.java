import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;


public class Viewer extends JFrame {
    private Controller controller;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;

    public DefaultListModel<String> getListModel(){
        return taskListModel;
    }

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
                int startDate = 0;
                String viewType = "";
                try {
                    startDate = Integer.parseInt(JOptionPane.showInputDialog("Enter the start date you would like to view. Format: YYYYMMDD"));
                    if (isValidDate(startDate)){
                        Object[] possibleValues = { "Day", "Week", "Month" };
                        Object selectedValue = JOptionPane.showInputDialog(null, "Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
                        viewType = (String)selectedValue;
                        viewType = viewType.toLowerCase();
                        controller.viewSchedule(Integer.toString(startDate), viewType);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid date.");
                    }
                } catch (NumberFormatException dateException) {
                    JOptionPane.showMessageDialog(null, "Invalid input. Please enter an integer.");
                }

                


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
                    String taskName = selectedTask.split(" - ")[0];
                    Task task = controller.getTaskByName(taskName);
                    if (task != null) {
                        showTaskEditDialog(task);
                    }
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

    private void showTaskEditDialog(Task task) {
        JDialog dialog = new JDialog(this, "Edit Task", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new FlowLayout());

        JPanel mainPanel = new JPanel(new GridLayout(0, 2));
        JTextField nameField = new JTextField(task.getName());
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Visit", "Shopping", "Appointment", "Class", "Study", "Sleep", "Exercise", "Work", "Meal", "Cancellation"});
        typeBox.setSelectedItem(task.getType());
        JTextField startDateField = new JTextField(String.valueOf(task.getDate()));
        JTextField startTimeField = new JTextField(String.valueOf(task.getStartTime()));
        JTextField durationField = new JTextField(String.valueOf(task.getDuration()));

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
        if (task instanceof RecurringTask) {
            RecurringTask recurringTask = (RecurringTask) task;
            endDateField.setText(String.valueOf(recurringTask.getEndDate()));
            frequencyField.setText(String.valueOf(recurringTask.getFrequency()));
        }
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

                    if (controller.editTask(task.getName(), name, type, startDate, startTime, duration, endDate, frequency)) {
                        JOptionPane.showMessageDialog(mainPanel, "Task successfully edited", "Success!", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "Task information invalid, or conflicts with another task.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception submitException) {
                    JOptionPane.showMessageDialog(mainPanel, "Task information invalid, or conflicts with another task.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

    private LocalDate convertIntToLocalDate(int date) {
        String dateString = String.valueOf(date);
        // Extract year, month, and day
        int year = Integer.parseInt(dateString.substring(0, 4));
        int month = Integer.parseInt(dateString.substring(4, 6));
        int day = Integer.parseInt(dateString.substring(6, 8));
        return LocalDate.of(year, month, day); // Create and return LocalDate
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

    private boolean isInPeriod(LocalDate startDate, LocalDate testDate, String period){
        switch (period.toLowerCase()) {
            case "day":
                return testDate.isEqual(startDate);
            case "week":
                return !testDate.isBefore(startDate) && !testDate.isAfter(startDate.plusDays(6));
            case "month":
                return testDate.getMonth() == startDate.getMonth() && testDate.getYear() == startDate.getYear();
            default:
                return false;
        }
    }

    public void updateTaskList(ArrayList<Task> tasks, DefaultListModel<String> listModel, int startDate, String period) {
        listModel.clear();
        ArrayList<Task> cancellationTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof AntiTask) {
                cancellationTasks.add(task);
            }
        }

        boolean showAllTasks = false;
        if(startDate == 0 || period == null){
            showAllTasks = true;
        }

        System.out.println("Show all tasks?: " + showAllTasks);
        System.out.println("Start Date: " + startDate);
        System.out.println("Period: " + period);

        for (Task task : tasks) {
            
            if(showAllTasks || isTaskInViewPeriod(task, convertIntToLocalDate(startDate), period)){
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
                            if(showAllTasks || isInPeriod(convertIntToLocalDate(startDate), convertIntToLocalDate(currentDate), period)){
                                addTaskToList(task, currentDate, listModel);
                            }
                        }
                        currentDate = incrementDate(currentDate, frequency);
                    }
                } else if (!(task instanceof AntiTask)) {
                    addTaskToList(task, task.getDate(), listModel);
                }
            }
        }
    }

    public void showScheduleViewer(ArrayList<Task> tasks, int startDate, String period){
        JDialog dialog = new JDialog(this, "View Schedule", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new FlowLayout());

        JPanel mainPanel = new JPanel();
        JList<String> taskField;
        DefaultListModel<String> scheduleJList = new DefaultListModel<String>();

        updateTaskList(tasks, scheduleJList, startDate, period);


        /*
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
                        String dateString = String.valueOf(task.getDate());
                        String year = dateString.substring(0, 4);
                        String month = dateString.substring(4, 6);
                        String day = dateString.substring(6, 8);
                        dateString = month + "/" + day + "/" + year;

                        float endTime = task.getStartTime() + task.getDuration();
                        int endHour = (int) Math.floor(endTime);
                        int endMinute = (int) ((endTime - endHour) * 60);

                        int startHour = (int) Math.floor(task.getStartTime());
                        int startMinute = (int) ((task.getStartTime() - startHour) * 60);
                        scheduleJList.addElement("<html><strike>" + task.getName() + " - " + task.getType() + " - " + dateString + " - Start: " + startHour + ":" + startMinute + " - End: " + endHour + ":" + endMinute + "</strike> - Cancelled by " + cancellationName + "</html>");
                    } else {
                        String dateString = String.valueOf(task.getDate());
                        String year = dateString.substring(0, 4);
                        String month = dateString.substring(4, 6);
                        String day = dateString.substring(6, 8);
                        dateString = month + "/" + day + "/" + year;

                        float endTime = task.getStartTime() + task.getDuration();
                        int endHour = (int) Math.floor(endTime);
                        int endMinute = (int) ((endTime - endHour) * 60);

                        int startHour = (int) Math.floor(task.getStartTime());
                        int startMinute = (int) ((task.getStartTime() - startHour) * 60);
                        scheduleJList.addElement(task.getName() + " - " + task.getType() + " - " + dateString + " - Start: " + startHour + ":" + startMinute + " - End: " + endHour + ":" + endMinute);
                    }
                    currentDate = incrementDate(currentDate, frequency);
                }
            } else if (!(task instanceof AntiTask)) {
                String dateString = String.valueOf(task.getDate());
                String year = dateString.substring(0, 4);
                String month = dateString.substring(4, 6);
                String day = dateString.substring(6, 8);
                dateString = month + "/" + day + "/" + year;

                float endTime = task.getStartTime() + task.getDuration();
                int endHour = (int) Math.floor(endTime);
                int endMinute = (int) ((endTime - endHour) * 60);

                int startHour = (int) Math.floor(task.getStartTime());
                int startMinute = (int) ((task.getStartTime() - startHour) * 60);
                scheduleJList.addElement(task.getName() + " - " + task.getType() + " - " + dateString + " - Start: " + startHour + ":" + startMinute + " - End: " + endHour + ":" + endMinute);
            }
        }
        */

        /*
        for(int i = 0; i<tasks.size(); i++){

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
                String dateString = String.valueOf(task.getDate());
                String year = dateString.substring(0, 4);
                String month = dateString.substring(4, 6);
                String day = dateString.substring(6, 8);
                dateString = month + "/" + day + "/" + year;

                float endTime = task.getStartTime() + task.getDuration();
                int endHour = (int) Math.floor(endTime);
                int endMinute = (int) ((endTime - endHour) * 60);

                int startHour = (int) Math.floor(task.getStartTime());
                int startMinute = (int) ((task.getStartTime() - startHour) * 60);
                scheduleJList.addElement(task.getName() + " - " + task.getType() + " - " + dateString + " - Start: " + startHour + ":" + startMinute + " - End: " + endHour + ":" + endMinute);
            }
        } */

        

        

        taskField = new JList<>(scheduleJList);
        //add(new JScrollPane(taskField)); // need to turn ArrayList<Task> tasks to JList<String>
        mainPanel.add(new JScrollPane(taskField));
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void addTaskToList(Task task, int date, DefaultListModel<String> listModel) {
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
        listModel.addElement(task.getName() + " - " + task.getType() + " - " + dateString + " - Start: " + startHour + ":" + startMinute + " - End: " + endHour + ":" + endMinute);
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


    //For validating date input for view tasks
    private boolean isValidDate(Integer date){
        //Check for correct date format
        int dateLength = String.valueOf(date).length();
        System.out.println(dateLength);
        if(dateLength != 8 || date < 0){
            //DEBUG System.out.println("Error: Invalid date! J");
            return false;
        }

        //Check for valid month and day values. Year will not be checked.
        int year = Integer.parseInt(Integer.toString(date).substring(0, 4));
        int month = Integer.parseInt(Integer.toString(date).substring(4, 6));
        int day = Integer.parseInt(Integer.toString(date).substring(6));
        System.out.println("Year: " + year);
        System.out.println("Month: " + month);
        System.out.println("Day: " + day);

        if(month < 1 || month > 12 || day < 1 || day > 31){
            //DEBUG System.out.println("Error: Invalid date! A");
            return false;
        }

        switch (month){
            case 4, 6, 9, 11:
                if (day > 30){
                    //DEBUG System.out.println("Error: Invalid date! B");
                    return false;
                } else {
                    break;
                }
            case 2:
                if (year%4 == 0){ //year is a leap year
                    if (day > 29){
                        //DEBUG System.out.println("Error: Invalid date! C");
                        return false;
                    } else {
                        break;
                    }
                } else { //year is not leap year
                    if (day > 28){
                        //DEBUG System.out.println("Error: Invalid date! D");
                        return false;
                    } else {
                        break;
                    }
                }
            default: 
                return true;
        }
        return true;
    }
}