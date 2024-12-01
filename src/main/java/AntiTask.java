/**
 * AntiTask is a subclass of the class Task, designed to negate a single occurrence of a recurring task.
 */
public class AntiTask extends Task {

       /**
        * Constructor for AntiTask.
        *
        * @param name      the name of the anti-task.
        * @param type      the type of the anti-task.
        * @param startTime the start time of the anti-task, rounded to the nearest 15 minutes.
        * @param duration  the duration of the anti-task, rounded to the nearest 15 minutes.
        * @param date      the date of the anti-task, represented as YYYYMMDD.
        */
       public AntiTask(String name, String type, float startTime, float duration, int date) {
           super(name, type, startTime, duration, date);
       }
   
       /**
        * Default constructor for AntiTask.
        */
       public AntiTask() {
           super();
       }
   }
   