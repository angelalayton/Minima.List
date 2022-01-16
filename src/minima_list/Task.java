
package minima_list;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
    
    private String taskName;
    private String taskDueDate;
    private String taskDescription;
    private int taskTagIndex;
    
    public Task (String name, String dueDate, String description, int index)
    {
        this.taskName = name;
        this.taskDueDate = dueDate;
        this.taskDescription = description;
        this.taskTagIndex = index;
    }
    
    //Converts due date of task to int for comparison and sorting
    public long dueDateToInt(String dueDate) 
    {
        // Dates will always be entered in this format mm/dd/yy hh:mm am/pm
        
        String fullDate = dueDate;
        
        // This flag is specifically used to designate that the time is between 12:00 pm and 12:59pm so it doesn't need to be military time
        boolean flag = false;
        
        Character timeChar = dueDate.charAt(15);
        String hour = dueDate.substring(9,11);
        String minute = dueDate.substring(12,14);
        String fullTime = hour + minute;
        int integerTime = Integer.parseInt(fullTime);
        
        if (timeChar.equals('p') && (Integer.parseInt(hour) == 12))
        {
            flag = true;
        }
        
        //Convert time to military time if necessary

        if (timeChar.equals('p') && !flag)
        {
            
            integerTime += 1200;
            String timeString = integerTime + "";
            String timeColon = timeString.substring(0,2) + ":" + timeString.substring(2);    

            fullDate = dueDate.substring(0,9) + timeColon;
        }

        //Converts time to milliseconds for comparison
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy HH:mm");
        Date intDate = null;
        try
        {
           intDate = formatter.parse(fullDate); 
        }
        catch(Exception e)
        {}
            
        
        long millisecondDate = intDate.getTime();
        return millisecondDate;     
        
    }
    
    // Separates task information in this format:
    // name,due date,description,tag reference
    public String tabSeparateTasks(Task task)
    {
        String taskComma = task.getTaskName() + "\t" + task.getTaskDueDate() + "\t" + task.getTaskDescription() + "\t" + task.getTaskTagIndex() + "\t";
        
        return taskComma;
    }
       
    
    public String toString()
    {
        String taskString = "<html>Task Name: " + this.taskName + "<br/>" 
                         + "Due Date: " + this.taskDueDate + "<br/>"
                         + "Description: " + this.taskDescription + "</html>";
       
       return taskString;       
    }
   
    public String getTaskName() 
    {
        return taskName;
    }

    public void setTaskName(String name) 
    {
        this.taskName = name;
    }

    public String getTaskDueDate() 
    {
        return taskDueDate;
    }

    public void setTaskDueDate(String dueDate) 
    {
        this.taskDueDate = dueDate;
    }

    public String getTaskDescription() 
    {
        return taskDescription;
    }

    public void setTaskDescription(String description) 
    {
        this.taskDescription = description;
    }

    public int getTaskTagIndex() {
        return taskTagIndex;
    }

    public void setTaskTagIndex(int taskTagIndex) {
        this.taskTagIndex = taskTagIndex;
    }
    
    
}
