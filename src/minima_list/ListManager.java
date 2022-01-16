
package minima_list;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ListManager {
    
    // Hashmap that contains all task information
    // The key is either the name of a weekday or the master list
    // The array list contains all the task objects that have been added to that list
    private Map<String, List> masterMap = new HashMap<String, List>();
    
    public ListManager()
    {
        List<Task> masterList = new ArrayList<Task>();
        List<Task> mondayList = new ArrayList<Task>();
        List<Task> tuesdayList = new ArrayList<Task>();
        List<Task> wednesdayList = new ArrayList<Task>();
        List<Task> thursdayList = new ArrayList<Task>();
        List<Task> fridayList = new ArrayList<Task>();
        List<Task> saturdayList = new ArrayList<Task>();
        List<Task> sundayList = new ArrayList<Task>();
        
        this.masterMap.put("master", masterList);
        this.masterMap.put("monday", mondayList);
        this.masterMap.put("tuesday", tuesdayList);
        this.masterMap.put("wednesday", wednesdayList);
        this.masterMap.put("thursday", thursdayList);
        this.masterMap.put("friday", fridayList);
        this.masterMap.put("saturday", saturdayList);
        this.masterMap.put("sunday", sundayList);
    }

    // Adds task to designated list
    public void addTask(String listName, String taskName, String taskDate, String taskDescription, int taskIndex)
    {
        Task newTask = new Task(taskName, taskDate, taskDescription, taskIndex);
        
        // Sorts the newTask into the appropriate place by due date        
        
        if(this.masterMap.get(listName).isEmpty())
        {
            this.masterMap.get(listName).add(newTask);
        }
        else
        {
            this.masterMap.get(listName).add(newTask);
            sortTask(listName, this.masterMap.get(listName).size());
        }        
    }
    
    public void sortTask(String listName, int listLength)
    {
        // Base case
        if (listLength <= 1)
            return;
       
        // Sort first n-1 elements
        sortTask(listName, listLength - 1);
       
        // Insert last element at its correct position in sorted array
        Task lastTask = (Task)this.masterMap.get(listName).get(listLength - 1);
        long previousTaskTime = lastTask.dueDateToInt(lastTask.getTaskDueDate());
        
        int j = listLength -2;
        
        // Move elements that are greater than key, to one position ahead of their current position
        while (j >= 0 && ((Task)this.masterMap.get(listName).get(j)).dueDateToInt(((Task)this.masterMap.get(listName).get(j)).getTaskDueDate()) > previousTaskTime)
        {
            this.masterMap.get(listName).set(j+1, (Task)this.masterMap.get(listName).get(j) );
            j--;

        }
        this.masterMap.get(listName).set(j+1, lastTask );
    }
    
    // Returns the index of the task with the matching input name
    public int findTask(String listName, String taskName)
    {
        int index = -2;
        boolean found = false;
        for(int i = 0; i < this.masterMap.get(listName).size() && !found; i++)
        {
            Task temp = (Task) this.masterMap.get(listName).get(i);
            if(taskName.equals(temp.getTaskName()))
            {
                index = i;
                found = true;
            }
            else
            {
                index = -1;
            }
        }
        
        return index;
    }
    
    // Returns true or false if specific task is present in the designated list
    public boolean containsTask(String listName, String taskName)
    {
        boolean contains = false;
        
        for(int i = 0; i < this.masterMap.get(listName).size() && !contains; i++)
        { 
            Task temp = (Task) this.masterMap.get(listName).get(i);
            if(taskName.equals(temp.getTaskName()))
            {
                contains = true;
            }
            else
            {
                contains = false;
            }
        }
        
        return contains;
    }
        
    // Edits task information
    public void editTask(int indexinMaster, String oldName, String newName, String newDate, String newDes, int newTagIndex)
    {
       
        // Gets string value of all key sets
        Set<String> keySet = this.masterMap.keySet();
        Object[] keyArray = keySet.toArray();
        ArrayList<String> keyArrayList = new ArrayList<String>();
        for(int i = 0; i < keyArray.length; i++)
        {
            keyArrayList.add((String) keyArray[i]);
        }
        
        // Edits in master list
        this.masterMap.get("master").remove(indexinMaster);
        addTask("master", newName, newDate, newDes, newTagIndex);
        
        // Edits in weekly lists
        for(int i = 0; i < this.masterMap.size(); i++)
        {
            if (containsTask(keyArrayList.get(i), oldName))
            {
                int index = findTask(keyArrayList.get(i), oldName);
                this.masterMap.get(keyArrayList.get(i)).remove(index);
                addTask(keyArrayList.get(i), newName, newDate, newDes, newTagIndex);
            }   
        }      
    }
    
    // removes task from all lists including master and weekday lists
    public void deleteTask(String deleteTaskName)
    {
        
        // Gets string value of all key sets
        Set<String> keySet = this.masterMap.keySet();
        Object[] keyArray = keySet.toArray();
        ArrayList<String> keyArrayList = new ArrayList<String>();
        for(int i = 0; i < keyArray.length; i++)
        {
            keyArrayList.add((String) keyArray[i]);
        }
        
        for(int i = 0; i < this.masterMap.size(); i++)
        {
            if (containsTask(keyArrayList.get(i), deleteTaskName))
            {
               int indexOfRemoval = findTask(keyArrayList.get(i), deleteTaskName);
               this.masterMap.get(keyArrayList.get(i)).remove(indexOfRemoval);  
            }   
        }     
    }
    
    // return task at specified index
    public Task getTask(int indexOfTask)
    {
        Task temp = (Task)this.masterMap.get("master").get(indexOfTask);
        return temp;
    }
    
    // removes specified task only from the weekday lists
    public void removeTaskFromDays(String removeTaskName)
    {
        
        // Gets string value of all key sets
        Set<String> keySet = this.masterMap.keySet();
        Object[] keyArray = keySet.toArray();
        ArrayList<String> keyArrayList = new ArrayList<String>();
        for(int i = 0; i < keyArray.length; i++)
        {
            keyArrayList.add((String) keyArray[i]);
        }
        
        for(int i = 0; i < this.masterMap.size(); i++)
        {
            if (containsTask(keyArrayList.get(i), removeTaskName) && (!keyArrayList.get(i).equals("master")))
            {
               int indexOfRemoval = findTask(keyArrayList.get(i), removeTaskName);
               this.masterMap.get(keyArrayList.get(i)).remove(indexOfRemoval);  
            }   
        }     
    }
    
    // Changes tag references to -1 of all tasks that referenced a specific tag
    // used when a tag is deleted
    public void deleteTagReferences(int oldTagReference)
    {

        String keyName = "master";

        for(int i = 0; i < masterMap.get(keyName).size(); i++)
        {
            Task temp = (Task) masterMap.get(keyName).get(i);
            int tag = temp.getTaskTagIndex();
            String taskName = temp.getTaskName();
            String taskDate = temp.getTaskDueDate();
            String taskDescription = temp.getTaskDescription();

            if (tag == oldTagReference)
            {
                if(containsTask(keyName,taskName))
                {
                    int indexEditedTask = findTask(keyName,taskName);
                    editTask(indexEditedTask, taskName, taskName, taskDate, taskDescription, -1);
                }

            }
        }
        
    }
    
    // Returns information of all tags in a list in the form of an arraylist
    public ArrayList displayListContents(String listName)
    {
        ArrayList<String> contents = new ArrayList<String>();
        for(int i = 0; i < this.masterMap.get(listName).size(); i++)
        {
            String listofTasks = "";
            listofTasks += this.masterMap.get(listName).get(i).toString();
            contents.add(listofTasks);
        }
        
        return contents;
    }
    
    // return all tag references of task in specific list
    public ArrayList returnTaskTagMatching(String listName)
    {
        ArrayList<Integer> taskTagReferences = new ArrayList<Integer>();
        for(int i = 0; i < this.masterMap.get(listName).size(); i++)
        {
            Task temp = (Task)this.masterMap.get(listName).get(i);
            int tempTag = temp.getTaskTagIndex();
            taskTagReferences.add(tempTag);
        }
        
        return taskTagReferences;
    }
    
    // Returns comma seperated information of all tasks
    public String tabSeparateContents()
    {
        String commaContents = "";
        Set<String> keySet = this.masterMap.keySet();
        Object[] keyArray = keySet.toArray();
        ArrayList<String> keyArrayList = new ArrayList<String>();
        for(int i = 0; i < keyArray.length; i++)
        {
            keyArrayList.add((String) keyArray[i]);
        }
        
        for(int i = 0; i < keyArrayList.size(); i++)
        {
            String keyName = keyArrayList.get(i);
            
            commaContents = commaContents + keyName + "\t";
            
            for(int j = 0; j < masterMap.get(keyName).size(); j++)
            {
                Task temp = (Task) masterMap.get(keyName).get(j);
                
                commaContents = commaContents + temp.tabSeparateTasks(temp);
            }
            commaContents = commaContents + "\n";
        }
        return commaContents;
    }
    
    // Read comma seperated information and creates tasks
    // Places tasks in correct list
    public void readfromTabFile(String singleFileLine) throws IOException
    {
      
        int firstTabIndex = singleFileLine.indexOf("\t");
        String listName = singleFileLine.substring(0,firstTabIndex);
        
        String tasks = singleFileLine.substring(firstTabIndex + 1);
        
        //Turn string into char array to be read one at a time
        char[] stringCharacters = new char[tasks.length()]; 
 
        for (int i = 0; i < tasks.length(); i++) { 
            stringCharacters[i] = tasks.charAt(i); 
        } 
        
        // Tab counter counts how many time a tab has been read in the file
        // When the fourth tab is reached this indicates all information for a single task
        int tabCounter = 0;
        String taskString = "";
        
        for(int i = 0; i < stringCharacters.length; i++)
        {
            Character character = stringCharacters[i];
            taskString = taskString + character;
            
            if (character.equals('\t'))
            {
                tabCounter++;
                            
                if(tabCounter % 4 == 0)
                {
                    String subTaskString = taskString.substring(0, taskString.length() - 1);
                    this.masterMap.get(listName).add(reverseTabs(subTaskString));
                    taskString = "";
                }
            }
        }
        
    }
    
    // A single comma string representing a single task is passed in
    // The information is split at the commas and placed in an array
    // The items in the array represent the task name, due date, description, and tag reference
    // A new task is made with this information
    public Task reverseTabs(String tabString)
    {
        String delimiter = "[\t]";
        String[] tokens = tabString.split(delimiter, 4);
        String tagString = tokens[3];
        int tagIndex = Integer.parseInt(tagString);
        Task newTask = new Task(tokens[0], tokens[1], tokens[2], tagIndex);
        
        return newTask;
    }
}
