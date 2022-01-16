
package minima_list;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import layout.TableLayout;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.*;

public class UserInterface 
{
    // Master HashMap
    ListManager manager = new ListManager();
    
    // Tag and Weekday Hashmaps
    private static HashMap<String, JPanel> weekdaySections = new HashMap<String, JPanel>();   
    private static HashMap<String, Integer> taskTags = new HashMap<String, Integer>();

    // Stores tag information
    // The tagIndex is a unique number used by tasks to reference the tag assigned to it
    private static ArrayList<Tag> tagList = new ArrayList<Tag>();
    private static int tagIndex = 0;
    
    // Panels 
    private static JPanel mainPanel = new JPanel();

    private static JPanel masterListSection = new JPanel(); 
    private static JPanel tagListSection = new JPanel();
    private static JScrollPane tagScroll = new JScrollPane(tagListSection);
    private static JPanel tagPanel = new JPanel();
    
    //Buttons
    private static JButton newTaskButton = new JButton("+");     
    private static JButton removeTaskButton = new JButton("Remove from Weekdays");    
    private static JButton editTaskButton = new JButton("Edit Assignment");
    private static JButton deleteTaskButton = new JButton("Mark as Complete / Delete");
    private static JButton informationButton = new JButton("?");
    private static JButton tagButton = new JButton("Manage Tags");
    private static JButton mondayButton = new JButton("Monday");
    private static JButton tuesdayButton = new JButton("Tuesday");
    private static JButton wednesdayButton = new JButton("Wednesday");
    private static JButton thursdayButton = new JButton("Thursday");
    private static JButton fridayButton = new JButton("Friday");
    private static JButton saturdayButton = new JButton("Saturday");
    private static JButton sundayButton = new JButton("Sunday");
    
    private ButtonGroup taskButtons = new ButtonGroup();
    private ButtonGroup tagButtons = new ButtonGroup();
    
    private JColorChooser colorChooser = new JColorChooser();
    
    public void setUp(JFrame frame)
    {
        setUpHashMap();
        
        // Main Panel Design
        double size[][] = {{10, 200, 10, 145, 145, 145, 145, 145, 145, 145}, // Columns
            {10, 550, 10}}; // Rows
        this.mainPanel.setLayout(new TableLayout(size));

        this.mainPanel.add(masterListPanel(), "1, 1");
        this.mainPanel.add(mondayPanel(), "3, 1");
        this.mainPanel.add(tuesdayPanel(), "4, 1");
        this.mainPanel.add(wednesdayPanel(), "5, 1");
        this.mainPanel.add(thursdayPanel(), "6, 1");
        this.mainPanel.add(fridayPanel(), "7, 1");
        this.mainPanel.add(saturdayPanel(), "8, 1");
        this.mainPanel.add(sundayPanel(), "9, 1");
        
        
        // Adds Listeners to Buttons
        this.newTaskButton.addActionListener(new NewTaskListener());
        this.editTaskButton.addActionListener(new EditTaskListener());
        this.deleteTaskButton.addActionListener(new DeleteTaskListener());
        this.removeTaskButton.addActionListener(new RemoveTaskListener());
        this.informationButton.addActionListener(new InstructionsListener());
        this.tagButton.addActionListener(new TagListener());
        this.mondayButton.addActionListener(new MondayListener());
        this.tuesdayButton.addActionListener(new TuesdayListener());
        this.wednesdayButton.addActionListener(new WednesdayListener());
        this.thursdayButton.addActionListener(new ThursdayListener());
        this.fridayButton.addActionListener(new FridayListener());
        this.saturdayButton.addActionListener(new SaturdayListener());
        this.sundayButton.addActionListener(new SundayListener());
    
        frame.add(this.mainPanel);
    }
    
    // Updates panels to reflect changes
    public void refresh()
    {
        String masterKey = "master";                
        this.masterListSection.add(createMainLabels(masterKey));
        
        // Places all keys in an arraylist of type String
        Set<String> keySet = this.weekdaySections.keySet();
        Object[] keyArray = keySet.toArray();
        ArrayList<String> keyArrayList = new ArrayList<String>();
        for(int i = 0; i < keyArray.length; i++)
        {
            keyArrayList.add((String) keyArray[i]);
        }
        for(int i = 0; i < this.weekdaySections.size(); i++)
        {
            this.weekdaySections.get(keyArrayList.get(i)).add(createWeekLabels(keyArrayList.get(i)));
        }
        
        this.tagListSection.add(createTagLabels());
        this.tagListSection.validate();
        this.tagListSection.repaint();
                
        this.mainPanel.validate();
        this.mainPanel.repaint();
    }
    
    // Creates task labels in master list
    public JPanel createMainLabels(String listName)
    {
        this.masterListSection.removeAll();
        JPanel label = new JPanel();
        label.setLayout(new BoxLayout(label, BoxLayout.Y_AXIS));
        
        ArrayList<String> temp = this.manager.displayListContents(listName);
        ArrayList<Integer> tagMatching = this.manager.returnTaskTagMatching(listName);
        int rgbInt;
        for(int i = 0; i < temp.size(); i++)
        {
            String taskInfo = temp.get(i);                 
            JRadioButton button = new JRadioButton(taskInfo);
            
            // If the task does not have a tag the rgbInt will be returned as -1
            rgbInt = getCorrespondingTagColor(tagMatching.get(i));
            
            if (rgbInt != -1)
            {
                Color color = new Color(rgbInt);

                Border buttonBorder = BorderFactory.createLineBorder(color, 1);            
                button.setBorder(buttonBorder);
                button.setBorderPainted (true);
            }
            else
            {
                button.setBorderPainted (false);
            }
            
            // Allows for task information to be broadcasted when selected
            button.setActionCommand(taskInfo);
            this.taskButtons.add(button);         
            label.add(button);
            label.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        return label;
    }
    
    // Creates task labels in weekly lists
    public JPanel createWeekLabels(String listName)
    {
        this.weekdaySections.get(listName).removeAll();
        JPanel taskLabels = new JPanel();
        taskLabels.setLayout(new BoxLayout(taskLabels, BoxLayout.Y_AXIS));
        
        ArrayList<String> temp = this.manager.displayListContents(listName);
        ArrayList<Integer> tagMatching = this.manager.returnTaskTagMatching(listName);
        for(int i = 0; i < temp.size(); i++)
        {
            String taskInfo = temp.get(i);  
            String onlyName = taskInfo.substring(17, taskInfo.indexOf("<br/>Du"));
            String onlyDate = taskInfo.substring(taskInfo.indexOf("te: ") + 4, taskInfo.indexOf("<br/>De"));
            JLabel label = new JLabel("<html>" + onlyName + "<br/>" + onlyDate + "</html>");
            
            int tagIndex = tagMatching.get(i);
            int rgbInt = getCorrespondingTagColor(tagIndex);
            
            if (rgbInt > -1)
            {
                Color color = new Color(rgbInt);

                Border buttonBorder = BorderFactory.createLineBorder(color, 1);            
                label.setBorder(buttonBorder);  
            }
            else
            {
                label.setBorder(null);
            }
            
            taskLabels.add(label);
            taskLabels.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        return taskLabels;
    }
    
    // Creates tag labels in tag menu
    public JPanel createTagLabels()
    {
        this.tagListSection.removeAll();
        JPanel tagLabels = new JPanel();
        tagLabels.setLayout(new BoxLayout(tagLabels, BoxLayout.Y_AXIS));
        
        for(int i = 0; i < tagList.size(); i++)
        {
            Tag temp = tagList.get(i);
            String tagName = temp.getTagName();  
            int tagColor = temp.getTagColor();
            Color color = new Color(tagColor);
            JRadioButton button = new JRadioButton(tagName);

            Border buttonBorder = BorderFactory.createLineBorder(color, 1);            
            button.setBorder(buttonBorder);
            button.setBorderPainted (true);
            
            // Allows for task information to be broadcasted when selected
            button.setActionCommand(tagName);
            this.tagButtons.add(button);         
            
            tagLabels.add(button);
            tagLabels.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        return tagLabels;
    }
    
    // Returns true is there is a tag with the designated name in the tag list
    private boolean containsTag(String tagName)
    {
        boolean contains = false;
        for(int i = 0; i < tagList.size() && (!contains); i++)
        {
            Tag temp = tagList.get(i);
            String tempName = temp.getTagName();
            if(tempName.equals(tagName))
            {
                contains = true;
            }
        }
        return contains;        
    }
    
    // returns index of tag in tagList
    private int findTag(String tagName)
    {
        int index = -1;
        boolean contains = false;
        for(int i = 0; i < tagList.size() && (!contains); i++)
        {
            Tag temp = tagList.get(i);
            String tempName = temp.getTagName();
            if(tempName.equals(tagName))
            {
                contains = true;
                index = i;
            }
        }
        
        return index;
    }
    
    // returns array of tag names, used in JComboBoxes
    private String[] getTagNames()
    {
        String[] tagNames;
        
        if(!tagList.isEmpty())
        {
            tagNames = new String[tagList.size()+1];
        
            for(int i = 0; i < tagList.size(); i++)
            {
                Tag temp = tagList.get(i);
                String tagName = temp.getTagName();
                tagNames[i] = tagName;
            }

            // adds none value to list, represents no label
            tagNames[tagList.size()] = "None";

            return tagNames;
        }
        else
        {
            return tagNames = new String[]{"None"};
        }
        
    }
    
    // Returns the color of the corresponding tag at tagIndex
    public int getCorrespondingTagColor(int tagIndex)
    {
        int tagRGBInt = 0;
        boolean found = false;
        for(int i = 0; i < tagList.size() && !found; i++)
        {
            Tag temp = tagList.get(i);
            if (temp.getTagIndex() == tagIndex)
            {
                tagRGBInt = temp.getTagColor();
                found = true;
            }
            
        }
        if(!found)
        {
            tagRGBInt =-1;
        }

        return tagRGBInt;
        
    }
    
    // Returns the name of the corresponding tag at tagIndex
    public String getCorrespondingTagName(int tagIndex)
    {
        String tagName = "";
        boolean found = false;
        
         for(int i = 0; i < tagList.size() && !found; i++)
        {
            Tag temp = tagList.get(i);
            if (temp.getTagIndex() == tagIndex)
            {
                tagName = temp.getTagName();
                found = true;
            }
            
        }
        if(!found)
        {
            tagName = "None";
        }

        return tagName;
    }
    
    // Returns the information of the task selected in the button group
    public ArrayList getSelectedTask()
    {       
        String fullInfo = this.taskButtons.getSelection().getActionCommand();
        
        // The information of the task will always be returned in a string with this format:
        // <html>Task Name: __<br/>Due Date: __<br/>Description: __</html>
        // Parse the string using locations of '<br/>'
        String taskName = fullInfo.substring(17, fullInfo.indexOf("<br/>Du"));
        String taskDate = fullInfo.substring(fullInfo.indexOf("te: ") + 4, fullInfo.indexOf("<br/>De"));
        String taskDescription = fullInfo.substring(fullInfo.indexOf("n: ") + 3, fullInfo.indexOf("</html>"));
        
        int indexOfTask = this.manager.findTask("master", taskName);
        Task temp = this.manager.getTask(indexOfTask);
        int tagIndex = temp.getTaskTagIndex();
        
        ArrayList<String> infoArray = new ArrayList<String>();
        
        
        infoArray.add(taskName);
        infoArray.add(taskDate);
        infoArray.add(taskDescription);
        infoArray.add(tagIndex + "");
        
        return infoArray;
    }
    
    // Prevents user from adding the same task multiple times to the weekday lists
    public boolean doubleTask(String listName, String taskName)
    {
        boolean contains = this.manager.containsTask(listName, taskName);
        
        if(contains == true)
        {
            JOptionPane.showMessageDialog(this.mainPanel,
            "A task with the same is already present in this list. \n\n"
                    + "Suggestions: Make sure you are adding the task to the correct list or change the name of the task.",
            "Double Task Error",
            JOptionPane.ERROR_MESSAGE);
        }
         return contains;
    }
    
    //Determines if a character is an integer, used in time input validation
    public boolean isInt(Character num)
    {
        boolean isInt = true;
        String number = num + "";
        
        try
        {
            int intNum = Integer.parseInt(number);
        }
        catch(Exception e)
        {
            isInt = false;
        }
        
        return isInt;
    }
    
    // Saves all task information to a formatted csv file
    public void saveToTaskFile(String filePath) throws IOException
    {
        FileWriter writer = new FileWriter(filePath);
        
        writer.write(manager.tabSeparateContents());
        
        writer.close();
    }
    
    // Reads information from the csv file
    // Inputs information into the arraylists to be displayed when application opens
    public void readfromTaskFile(String filePath) throws FileNotFoundException
    {
        File myFile = new File(filePath);
        Scanner reader = new Scanner(myFile);
        String entireFile = "";
        
        // There will always be 8 lines in the task file as there are 8 lists
        String[] tokens = new String[8];
        int lineIndex = 0;
        while(reader.hasNextLine())
        {
            String nextLine = reader.nextLine();
            entireFile = entireFile + nextLine;
            tokens[lineIndex] = nextLine;
            lineIndex++;
        }
        
        for(int i = 0; i < tokens.length; i++)
        {
            try
            {
                this.manager.readfromTabFile(tokens[i]);
            }
            catch (Exception e) {}    
        }
        
        reader.close();
    }
    
    // Saves tag information to a saved csv file
    public void saveToTagFile(String filePath) throws IOException
    {
        FileWriter writer = new FileWriter(filePath);
        
        for(int i = 0; i < tagList.size(); i++)
        {
            Tag temp = tagList.get(i);
            writer.write(temp.getTagName() + "\t" + temp.getTagColor() + "\t" + temp.getTagIndex() + "\n");
        }
        
        writer.close();
    }
    
    // Reads information from tag file
    // Input information into designated arrayList to be displayed
    public void readfromTagFile(String filePath) throws FileNotFoundException
    {
        File myFile = new File(filePath);
        Scanner reader = new Scanner(myFile);
        
        int highestIndex = -1;
        
        while(reader.hasNextLine())
        {
            String nextLine = reader.nextLine();
            String delimiter = "[\t]";
            String[] tokens = nextLine.split(delimiter, 3);
            String tagColorStr = tokens[1];
            int tagColorInt = Integer.parseInt(tagColorStr);
            String tagIndexStr = tokens[2];
            int tagIndexInt = Integer.parseInt(tagIndexStr);
            if (tagIndexInt > highestIndex)
            {
                highestIndex = tagIndexInt;
            }
            this.tagList.add(new Tag(tokens[0], tagColorInt, tagIndexInt));
              
        }
        
        // The tagIndex is a unique number associated with each tag
        // When the tag file is read the highest tagIndex is recorded
        // The tagindex is then incremented by 1 so that it remains a unique number
        this.tagIndex = highestIndex + 1;
        reader.close();
    }  
     
    // Adds all weekday panels to weekday hashmap
    private void setUpHashMap()
    {
        JPanel mondayListSection = new JPanel();
        JPanel tuesdayListSection = new JPanel();
        JPanel wednesdayListSection = new JPanel();
        JPanel thursdayListSection = new JPanel();
        JPanel fridayListSection = new JPanel();
        JPanel saturdayListSection = new JPanel();
        JPanel sundayListSection = new JPanel();

        this.weekdaySections.put("monday", mondayListSection);
        this.weekdaySections.put("tuesday", tuesdayListSection);
        this.weekdaySections.put("wednesday", wednesdayListSection);
        this.weekdaySections.put("thursday", thursdayListSection);
        this.weekdaySections.put("friday", fridayListSection);
        this.weekdaySections.put("saturday", saturdayListSection);
        this.weekdaySections.put("sunday", sundayListSection);
    }
    
    // JPanel design of master list
    private JPanel masterListPanel()
    {
        JPanel masterListPanel = new JPanel();

        // Top Bar
        JPanel topPanel = new JPanel();
        double topSize[][] = {{50, 100, 50}, // Columns
            {25}}; // Rows
        topPanel.setLayout(new TableLayout(topSize));
                
        JLabel masterListLabel = new JLabel("      Master List");
        
        topPanel.add(this.informationButton, "0,0");
        topPanel.add(masterListLabel, "1,0");
        topPanel.add(this.newTaskButton, "2,0");

        // Bottom Bar
        JPanel bottomPanel = new JPanel();
        double bottompSize[][] = {{200}, // Columns
            {25, 25, 25,25}}; // Rows
        bottomPanel.setLayout(new TableLayout(bottompSize));
        
        bottomPanel.add(this.editTaskButton, "0,0");
        bottomPanel.add(this.removeTaskButton, "0,1");
        bottomPanel.add(this.deleteTaskButton, "0,2");
        bottomPanel.add(this.tagButton, "0,3");
        
        // Master Panel
        // This ensures that the JLabels appear in the correct place
        JPanel masterPanel = new JPanel();
        double masterSize[][] = {{200}, // Columns
            {400}}; // Rows
        masterPanel.setLayout(new TableLayout(masterSize));
        
        JScrollPane master = new JScrollPane(this.masterListSection);
        
        masterPanel.add(master, "0,0");
        
        // Main Panel
        double size[][] = {{550}, // Columns
            {25, 25, 400, 100}}; // Rows
        masterListPanel.setLayout(new TableLayout(size));
        
        JLabel dashes = new JLabel("--------------------------------------------------------------");
        
        masterListPanel.add(topPanel,"0,0");
        masterListPanel.add(dashes,"0,1");
        masterListPanel.add(masterPanel, "0,2");
        masterListPanel.add(bottomPanel, "0,3");
        
        return masterListPanel;
    }
    
    // JPanel design of monday list
    private JPanel mondayPanel()
    {
        // Weekday Panel
        JPanel dayListSection = new JPanel();
        double dayListSize[][] = {{145}, // Columns
            {500}}; // Rows
        dayListSection.setLayout(new TableLayout(dayListSize));
        JScrollPane mondayScroll = new JScrollPane(this.weekdaySections.get("monday"));
        dayListSection.add(mondayScroll, "0,0");
        
        
        // Monday Panel
        JPanel mondayPanel = new JPanel();
        double mondaySize[][] = {{143}, // Columns
            {25, 23, 500}}; // Rows
        mondayPanel.setLayout(new TableLayout(mondaySize));
        
        JLabel dashes = new JLabel("-----------------------------------");
        
        mondayPanel.add(this.mondayButton, "0,0");
        mondayPanel.add(dashes, "0,1");
        mondayPanel.add(dayListSection, "0,2");

        Border blackline = BorderFactory.createLineBorder(Color.black);
        mondayPanel.setBorder(blackline);
        
        return mondayPanel;
    }
    
    // JPanel design of tuesday list
    private JPanel tuesdayPanel()
    {
        // Weekday Panel
        JPanel dayListSection = new JPanel();
        double dayListSize[][] = {{145}, // Columns
            {500}}; // Rows
        dayListSection.setLayout(new TableLayout(dayListSize));
        JScrollPane tuesdayScroll = new JScrollPane(this.weekdaySections.get("tuesday"));
        dayListSection.add(tuesdayScroll, "0,0");
        
        
        // Tuesday Panel
        JPanel tuesdayPanel = new JPanel();
        double tuesdaySize[][] = {{143}, // Columns
            {25, 23, 500}}; // Rows
        tuesdayPanel.setLayout(new TableLayout(tuesdaySize));
        
        JLabel dashes = new JLabel("-----------------------------------");
        
        tuesdayPanel.add(this.tuesdayButton, "0,0");
        tuesdayPanel.add(dashes, "0,1");
        tuesdayPanel.add(dayListSection, "0,2");
        
        Border blackline = BorderFactory.createLineBorder(Color.black);
        tuesdayPanel.setBorder(blackline);
        
        return tuesdayPanel;
    }
    
    // JPanel design of wednesday list
    private JPanel wednesdayPanel()
    {
        // Weekday Panel
        JPanel dayListSection = new JPanel();
        double dayListSize[][] = {{145}, // Columns
            {500}}; // Rows
        dayListSection.setLayout(new TableLayout(dayListSize));
        JScrollPane wednesdayScroll = new JScrollPane(this.weekdaySections.get("wednesday"));
        dayListSection.add(wednesdayScroll, "0,0");
        
        
        // Wednesday Panel
        JPanel wednesdayPanel = new JPanel();
        double wednesdaySize[][] = {{143}, // Columns
            {25, 23, 500}}; // Rows
        wednesdayPanel.setLayout(new TableLayout(wednesdaySize));
        
        JLabel dashes = new JLabel("-----------------------------------");
        
        wednesdayPanel.add(this.wednesdayButton, "0,0");
        wednesdayPanel.add(dashes, "0,1");
        wednesdayPanel.add(dayListSection, "0,2");
        
        Border blackline = BorderFactory.createLineBorder(Color.black);
        wednesdayPanel.setBorder(blackline);
        
        return wednesdayPanel;
    }
    
    // JPanel design of thursday list
    private JPanel thursdayPanel()
    {
        // Weekday Panel
        JPanel dayListSection = new JPanel();
        double dayListSize[][] = {{145}, // Columns
            {500}}; // Rows
        dayListSection.setLayout(new TableLayout(dayListSize));
        JScrollPane thursdayScroll = new JScrollPane(this.weekdaySections.get("thursday"));
        dayListSection.add(thursdayScroll, "0,0");
        
        
        // Thursday Panel
        JPanel thursdayPanel = new JPanel();
        double thursdaySize[][] = {{143}, // Columns
            {25, 23, 500}}; // Rows
        thursdayPanel.setLayout(new TableLayout(thursdaySize));
        
        JLabel dashes = new JLabel("-----------------------------------");
        
        thursdayPanel.add(this.thursdayButton, "0,0");
        thursdayPanel.add(dashes, "0,1");
        thursdayPanel.add(dayListSection, "0,2");
                
        Border blackline = BorderFactory.createLineBorder(Color.black);
        thursdayPanel.setBorder(blackline);
        
        return thursdayPanel;
    }
    
    // JPanel design of friday list
    private JPanel fridayPanel()
    {
        // Weekday Panel
        JPanel dayListSection = new JPanel();
        double dayListSize[][] = {{145}, // Columns
            {500}}; // Rows
        dayListSection.setLayout(new TableLayout(dayListSize));
        JScrollPane fridayScroll = new JScrollPane(this.weekdaySections.get("friday"));
        dayListSection.add(fridayScroll, "0,0");        
        
        // Friday Panel
        JPanel fridayPanel = new JPanel();
        double fridaySize[][] = {{143}, // Columns
            {25, 23, 500}}; // Rows
        fridayPanel.setLayout(new TableLayout(fridaySize));
        
        JLabel dashes = new JLabel("-----------------------------------");
        
        fridayPanel.add(this.fridayButton, "0,0");
        fridayPanel.add(dashes, "0,1");
        fridayPanel.add(dayListSection, "0,2");

        Border blackline = BorderFactory.createLineBorder(Color.black);
        fridayPanel.setBorder(blackline);
        
        return fridayPanel;
    }
    
    // JPanel design of saturday list
    private JPanel saturdayPanel()
    {
        // Weekday Panel
        JPanel dayListSection = new JPanel();
        double dayListSize[][] = {{145}, // Columns
            {500}}; // Rows
        dayListSection.setLayout(new TableLayout(dayListSize));
        JScrollPane saturdayScroll = new JScrollPane(this.weekdaySections.get("saturday"));
        dayListSection.add(saturdayScroll, "0,0");        
        
        // Saturday Panel
        JPanel saturdayPanel = new JPanel();
        double saturdaySize[][] = {{143}, // Columns
            {25, 23, 500}}; // Rows
        saturdayPanel.setLayout(new TableLayout(saturdaySize));
        
        JLabel dashes = new JLabel("-----------------------------------");
        
        saturdayPanel.add(this.saturdayButton, "0,0");
        saturdayPanel.add(dashes, "0,1");
        saturdayPanel.add(dayListSection, "0,2");

        Border blackline = BorderFactory.createLineBorder(Color.black);
        saturdayPanel.setBorder(blackline);
        
        return saturdayPanel;
    }
    
    // JPanel design of sunday list
    private JPanel sundayPanel()
    {
        // Weekday Panel
        JPanel dayListSection = new JPanel();
        double dayListSize[][] = {{145}, // Columns
            {500}}; // Rows
        dayListSection.setLayout(new TableLayout(dayListSize));
        JScrollPane sundayScroll = new JScrollPane(this.weekdaySections.get("sunday"));
        dayListSection.add(sundayScroll, "0,0");        
        
        // Sunday Panel
        JPanel sundayPanel = new JPanel();
        double sundaySize[][] = {{143}, // Columns
            {25, 23, 500}}; // Rows
        sundayPanel.setLayout(new TableLayout(sundaySize));
        
        JLabel dashes = new JLabel("-----------------------------------");
        
        sundayPanel.add(this.sundayButton, "0,0");
        sundayPanel.add(dashes, "0,1");
        sundayPanel.add(dayListSection, "0,2");
        
        Border blackline = BorderFactory.createLineBorder(Color.black);
        sundayPanel.setBorder(blackline);
        
        return sundayPanel;
    }
    
    // Responds to a click on the + (new task) button
    private class NewTaskListener implements ActionListener
    {
        private int tagReference;
        public void actionPerformed(ActionEvent e)
        {
            tagReference = -1;
            
            JTextField taskName = new JTextField();
            taskName.setBorder(new LineBorder(Color.BLACK, 1));
            JTextField taskTime = new JTextField();
            taskTime.setBorder(new LineBorder(Color.BLACK, 1));
            JTextArea taskDescription = new JTextArea(25,75);
            
            // Add key listener to change the TAB behavior in
            // JTextArea to transfer focus to other component forward
            taskDescription.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) 
                {
                    if (e.getKeyCode() == KeyEvent.VK_TAB) 
                    {
                        if (e.getModifiersEx() > 0) 
                        {
                            taskDescription.transferFocusBackward();
                        } 
                        else 
                        {
                            taskDescription.transferFocus();
                        }
                        e.consume();
                    }
                }
            });
            
            taskDescription.setBorder(new LineBorder(Color.BLACK, 1));
            
            JLabel chooseTag = new JLabel("Choose Tag");
            JComboBox tags = new JComboBox(getTagNames());
            tags.setSelectedIndex(tagList.size());
            
            // Records tag that was chosen from drop down menu
            tags.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e) 
                {
                    String chosenTag = tags.getSelectedItem().toString();                    
                    if(!chosenTag.equals("None"))
                    {
                        int index = findTag(chosenTag);
                        Tag temp = tagList.get(index);
                        tagReference = temp.getTagIndex();
                    }
                    else
                    {
                        tagReference = -1;    
                    }
                    
                }
            });
            
            JLabel nameInvalid = new JLabel();
            JLabel invalid = new JLabel();
            
            ButtonGroup timeButtons = new ButtonGroup();
            JRadioButton am = new JRadioButton("am");
            am.setActionCommand("am");
            JRadioButton pm = new JRadioButton("pm");
            pm.setActionCommand("pm");
            
            timeButtons.add(am);
            timeButtons.add(pm);


            JPanel dialoguePanel = new JPanel();
            dialoguePanel.setPreferredSize(new Dimension(425,240));
            
            double dialogSize[][] = {{75, 250,50,50}, // Columns
            {25,25,5, 25, 5,25, 25, 25,5, 75}}; // Rows
            dialoguePanel.setLayout(new TableLayout(dialogSize));
            
            UtilDateModel calendarModel = new UtilDateModel();
            
            Properties p = new Properties();
            p.put("text.today", "Today");
            p.put("text.month", "Month");
            p.put("text.year", "Year");
            JDatePanelImpl datePanel = new JDatePanelImpl(calendarModel, p);
            JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
            
            dialoguePanel.add(nameInvalid, "1,0");
            dialoguePanel.add(new JLabel("<html>Task Name<font color=\"red\">*</font></html>"), "0,1");
            dialoguePanel.add(taskName, "1,1");
            dialoguePanel.add(chooseTag, "0,3");
            dialoguePanel.add(tags, "1,3");
            dialoguePanel.add(new JLabel("<html>Due Date<font color=\"red\">*</font></html>"),"0,5");
            dialoguePanel.add(datePicker, "1,5");
            dialoguePanel.add(invalid, "1,6");
            dialoguePanel.add(new JLabel("<html>Time<font color=\"red\">*</font></html>"),"0,7");
            dialoguePanel.add(taskTime, "1,7");
            dialoguePanel.add(am, "2,7");
            dialoguePanel.add(pm, "3,7");
            dialoguePanel.add(new JLabel("Description"), "0,9");
            dialoguePanel.add(taskDescription, "1,9");
            
            // If task name entered already exists it will highlight box red and notify user
            taskName.addFocusListener(new FocusListener()
            {
                @Override
                public void focusGained(FocusEvent e) {}

                @Override
                public void focusLost(FocusEvent e) 
                {
                    String nameTemp = taskName.getText();
                    if(manager.containsTask("master",nameTemp))
                    {
                        nameInvalid.setForeground(Color.RED);
                        nameInvalid.setText("Invalid Input: Name of already existing task");
                        taskName.setBorder(new LineBorder(Color.RED, 1));
                    }
                    else
                    {
                        nameInvalid.setText("");
                        taskName.setBorder(new LineBorder(Color.BLACK, 1));
                    }
                }
            });
            
            // If time is entered incorrectly it will highlight box red and notify user
            taskTime.addFocusListener(new FocusListener() 
            {
                @Override
                public void focusGained(FocusEvent e) {}

                @Override
                public void focusLost(FocusEvent e) 
                {
                        String timeTemp = taskTime.getText();
                        if(timeTemp.length() == 5)
                        {
                            Character colon = timeTemp.charAt(2);
                            if(colon.equals(':'))
                            {
                                invalid.setText("");
                                taskTime.setBorder(new LineBorder(Color.BLACK, 1));
                            }
                            else
                            {
                                invalid.setForeground(Color.RED);
                                invalid.setText("Invalid Input: Enter in this format hh:mm");
                                taskTime.setBorder(new LineBorder(Color.RED, 1));
                            }
                        }
                        else
                        {
                            invalid.setForeground(Color.RED);
                            invalid.setText("Invalid Input: Enter in this format hh:mm");
                            taskTime.setBorder(new LineBorder(Color.RED, 1));
                        }
                }
                
            });

            int result = JOptionPane.showConfirmDialog(null, dialoguePanel,
                "Enter the Information of the New Task", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) 
            {
                String key = "master";
                String name = "";
                String date = "";
                String time = "";
                String meridian = "";
                String fullTime = "";
                
                int test = 0;
                
                boolean error = false;
                
                // Gets information entered in text fields
                try
                { 
                    name = taskName.getText();
                    date = datePicker.getJFormattedTextField().getText();
                    time = taskTime.getText();
                    meridian = timeButtons.getSelection().getActionCommand();
                    fullTime = date + " " + time + " " + meridian;
                    test = Integer.parseInt(time.substring(0,2));
                    
                }
                catch(NullPointerException invalidInputError)
                {
                    error = true;
                    JOptionPane.showMessageDialog(null, "All required fields must be entered. Please input task information again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                // Only accepts a new task if all required fields are entered
                // Task name is not a duplicate
                // And the time is in the correct format
                if(!error)
                {
                    if(name.equals("") || date.equals("") || time.equals(""))
                    {
                        JOptionPane.showMessageDialog(null, "All required fields must be entered. Please input task information again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if(manager.containsTask("master",name))
                    {
                        JOptionPane.showMessageDialog(null, "Entered a name of an already existing task.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (time.length() != 5)
                    {

                        JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (time.length() == 5 && !(time.charAt(2)==(':')))
                    {

                        JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (time.length() == 5 && !(isInt(time.charAt(0))))
                    {

                        JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (time.length() == 5 && !(isInt(time.charAt(1))))
                    {

                        JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (time.length() == 5 && !(isInt(time.charAt(3))))
                    {

                        JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (time.length() == 5 && !(isInt(time.charAt(4))))
                    {

                        JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (time.length() == 5 && !(((Integer.parseInt(time.substring(0,2))) <= 12) && ((Integer.parseInt(time.substring(0,2))) > 0)))
                    {

                        JOptionPane.showMessageDialog(null, "Please enter an hour between 01-12",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (time.length() == 5 && !(((Integer.parseInt(time.substring(3))) <= 59) && ((Integer.parseInt(time.substring(3))) >= 0)))
                    {

                        JOptionPane.showMessageDialog(null, "Please enter a minute between 00-59",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                       //Translates description to preserve line breaks
                        String oldDescription = taskDescription.getText();
                        String correctDescription = oldDescription.replace("\n", "<br>");
                        manager.addTask(key, name, fullTime, correctDescription, tagReference);
                        refresh(); 
                    }
                    
                }
                
            }            
        }    
    }
    
    // Responds to a click on the edit task button
    private class EditTaskListener implements ActionListener
    {
        private boolean timeCorrect = false;
        private int tagReference;
        private int tagInt;
        private String chosenTag = "";
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            // Assigns tagReference to the previously chosen tag
            // This is -1 if there is no tag assigned
            tagReference = -1;
            tagInt = 0;
            if(taskButtons.getSelection() != null)
            {
                ArrayList<String> infoArray = getSelectedTask();
                
                String taskName = infoArray.get(0);
                String taskDate = infoArray.get(1);
                String taskDescription = infoArray.get(2);
                String tagString = infoArray.get(3);
                tagInt = Integer.parseInt(tagString);
                
                tagReference = tagInt;
                
                int tagIndexinList = -1;
                
                // Assigns tagIndexinList to correct reference to be reflected in JComboBox
                if(tagInt != -1)
                {
                    String tagName = getCorrespondingTagName(tagInt);
                    tagIndexinList = findTag(tagName);
                }
                
                
                //Translates string into another format
                String correctDescription = taskDescription.replace("<br>", "\n");
                
                String justDate = taskDate.substring(0,8);
                String justTime = taskDate.substring(9,14);
                String justMeridian = taskDate.substring(15,17);
                
                // Creates dialog box with selected tasks information to be editted
                JTextField name = new JTextField(taskName);
                name.setBorder(BorderFactory.createLineBorder(Color.black));
                JTextField time = new JTextField(justTime);
                time.setBorder(BorderFactory.createLineBorder(Color.black));
                JTextArea description = new JTextArea(correctDescription);
                
                description.addKeyListener(new KeyAdapter() 
                {
                    @Override
                    public void keyPressed(KeyEvent e) 
                    {
                        if (e.getKeyCode() == KeyEvent.VK_TAB) 
                        {
                            if (e.getModifiersEx() > 0) 
                            {
                                description.transferFocusBackward();
                            } 
                            else 
                            {
                                description.transferFocus();
                            }
                            e.consume();
                        }
                    }
                });
                
                description.setBorder(BorderFactory.createLineBorder(Color.black));
                
                JLabel chooseTag = new JLabel("Choose Tag");
                JComboBox tags = new JComboBox(getTagNames()); 
                
                if(tagIndexinList != -1)
                {
                    tags.setSelectedIndex(tagIndexinList);
                }
                else
                {
                    tags.setSelectedIndex(tagList.size());
                }
                // Records tag that was chosen from drop down menu
                tags.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e) 
                    {
                        chosenTag = tags.getSelectedItem().toString();
                        if(!chosenTag.equals("None"))
                        {
                            int index = findTag(chosenTag);
                            Tag temp = tagList.get(index);
                            tagReference = temp.getTagIndex();
                        }
                        else
                        {
                            tagReference = -1;    
                        }
                        
                    }
                });
            
                JLabel nameInvalid = new JLabel();
                JLabel invalid = new JLabel();
                
                ButtonGroup timeButtons = new ButtonGroup();
                JRadioButton am = new JRadioButton("am");
                am.setActionCommand("am");
                JRadioButton pm = new JRadioButton("pm");
                pm.setActionCommand("pm");

                timeButtons.add(am);
                timeButtons.add(pm);
                
                if(justMeridian.equals("am"))
                {
                    am.setSelected(true);
                }
                else
                {
                    pm.setSelected(true);
                }

                JPanel dialoguePanel = new JPanel();
                dialoguePanel.setPreferredSize(new Dimension(425,240));

                double dialogSize[][] = {{75, 250, 50, 50}, // Columns
                {25,25,5, 25, 5,25, 25, 25,5, 75}}; // Rows
                dialoguePanel.setLayout(new TableLayout(dialogSize));

                UtilDateModel calendarModel = new UtilDateModel();
            
                Properties p = new Properties();
                p.put("text.today", "Today");
                p.put("text.month", "Month");
                p.put("text.year", "Year");
                JDatePanelImpl datePanel = new JDatePanelImpl(calendarModel, p);
                JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
                datePicker.getJFormattedTextField().setText(justDate);
            
                dialoguePanel.add(nameInvalid, "1,0");
                dialoguePanel.add(new JLabel("<html>Task Name<font color=\"red\">*</font></html>"), "0,1");
                dialoguePanel.add(name, "1,1");
                dialoguePanel.add(chooseTag, "0,3");
                dialoguePanel.add(tags, "1,3");
                dialoguePanel.add(new JLabel("<html>Due Date<font color=\"red\">*</font></html>"),"0,5");
                dialoguePanel.add(datePicker, "1,5");
                dialoguePanel.add(invalid, "1,6");
                dialoguePanel.add(new JLabel("<html>Time<font color=\"red\">*</font></html>"),"0,7");
                dialoguePanel.add(time, "1,7");
                dialoguePanel.add(am, "2,7");
                dialoguePanel.add(pm, "3,7");
                dialoguePanel.add(new JLabel("Description"), "0,9");
                dialoguePanel.add(description, "1,9");
                
                // Notifies user if name or time is submitted incorrectly
                name.addFocusListener(new FocusListener()
                {
                    @Override
                    public void focusGained(FocusEvent e) {}

                    @Override
                    public void focusLost(FocusEvent e) 
                    {
                        String nameTemp = name.getText();
                        if(manager.containsTask("master",nameTemp) && (!nameTemp.equals(taskName)))
                        {
                            nameInvalid.setForeground(Color.RED);
                            nameInvalid.setText("Invalid Input: Name of already existing task");
                            name.setBorder(new LineBorder(Color.RED, 1));
                        }
                        else
                        {
                            nameInvalid.setText("");
                            name.setBorder(new LineBorder(Color.BLACK, 1));
                        }
                    }
                });
                
                time.addFocusListener(new FocusListener() 
                {

                    @Override
                    public void focusGained(FocusEvent e) {}

                    @Override
                    public void focusLost(FocusEvent e) 
                    {
                            String timeTemp = time.getText();
                            if(timeTemp.length() == 5)
                            {
                                Character colon = timeTemp.charAt(2);
                                if(colon.equals(':'))
                                {
                                    timeCorrect = true;
                                    invalid.setText("");
                                    time.setBorder(BorderFactory.createLineBorder(Color.black));
                                }
                                else
                                {
                                    invalid.setForeground(Color.RED);
                                    invalid.setText("Invalid Input: Enter in this format hh:mm");
                                    time.setBorder(BorderFactory.createLineBorder(Color.red));
                                }
                            }
                            else
                            {
                                invalid.setForeground(Color.RED);
                                invalid.setText("Invalid Input: Enter in this format hh:mm");
                                time.setBorder(BorderFactory.createLineBorder(Color.red));
                            }
                    }
                
                });
                

                int result = JOptionPane.showConfirmDialog(null, dialoguePanel,
                    "Edit the Information of the Selected Task", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) 
                {
                    String newName = "";
                    String newDate = "";
                    String newTime = "";
                    String newMeridian = "";
                    String newDescription = "";
                    String fullDate = "";
                    boolean error = false;
                
                    try
                    { 
                        newName = name.getText();
                        newDate = datePicker.getJFormattedTextField().getText();
                        newTime = time.getText();
                        newMeridian = timeButtons.getSelection().getActionCommand();
                        newDescription = description.getText().replace("\n","<br>");
                        fullDate = newDate + " " + newTime + " " + newMeridian;
                    }
                    catch(NullPointerException invalidInputError)
                    {
                        error = true;
                        JOptionPane.showMessageDialog(null, "All required fields must be entered. Please edit task information again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
        
                    // Only accepts a new task if all required fields are entered
                    // Task name is not a duplicate
                    // And the time is in the correct format
                    if(!error)
                    {
                        if(newName.equals("") || newDate.equals("") || newTime.equals(""))
                        {
                            JOptionPane.showMessageDialog(null, "All required fields must be entered. Please edit task information again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else if(manager.containsTask("master",newName) && (!newName.equals(taskName)))
                        {
                            JOptionPane.showMessageDialog(null, "Entered a name of an already existing task.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (newTime.length() != 5)
                        {
                            JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                            "Error", JOptionPane.ERROR_MESSAGE);    
                        }
                        else if (newTime.length() == 5 && !(newTime.charAt(2)==(':')))
                        {
                            
                            JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (newTime.length() == 5 && !(isInt(newTime.charAt(0))))
                        {

                            JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (newTime.length() == 5 && !(isInt(newTime.charAt(1))))
                        {

                            JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (newTime.length() == 5 && !(isInt(newTime.charAt(3))))
                        {

                            JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (newTime.length() == 5 && !(isInt(newTime.charAt(4))))
                        {

                            JOptionPane.showMessageDialog(null, "Entered time in an incorrect format.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (newTime.length() == 5 && !(((Integer.parseInt(newTime.substring(0,2))) <= 12) && ((Integer.parseInt(newTime.substring(0,2))) > 0)))
                        {

                            JOptionPane.showMessageDialog(null, "Please enter an hour between 01-12",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else if (newTime.length() == 5 && !(((Integer.parseInt(newTime.substring(3))) <= 59) && ((Integer.parseInt(newTime.substring(3))) >= 0)))
                        {

                            JOptionPane.showMessageDialog(null, "Please enter a minute between 00-59",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {
                           if(manager.containsTask("master",taskName))
                            {
                                int indexEditedTask = manager.findTask("master",taskName);
                                manager.editTask(indexEditedTask, taskName, newName, fullDate, newDescription, tagReference);                              

                                refresh();
                            }
                        }

                    }
                    taskButtons.clearSelection();
                 
                }
            }
        }
    }
    
    //Responds to click on delete button
    // Deletes task in master list and all week day lists
    private class DeleteTaskListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(taskButtons.getSelection() != null)
            {
                ArrayList<String> infoArray = getSelectedTask();
                
                String taskName = infoArray.get(0);
                manager.deleteTask(taskName);
                refresh();
                taskButtons.clearSelection();
            }
        }
    }
    
    //Responds to a click on the remove task button
    // Removes taks from week day lists
    private class RemoveTaskListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(taskButtons.getSelection() != null)
            {
                ArrayList<String> infoArray = getSelectedTask();
                
                String taskName = infoArray.get(0);
                manager.removeTaskFromDays(taskName);
                refresh();
                taskButtons.clearSelection();
            }
        }
    }
    
    // Create tag dialogue box when user clicks on manage tags
    private class TagListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            
            tagPanel.setPreferredSize(new Dimension(200,385));
            double tagPanelSize[][] = {{200}, // Columns
            {125,10,250}}; // Rows
            tagPanel.setLayout(new TableLayout(tagPanelSize));

            // Top Header
            JPanel topPanel = new JPanel();
            double topPanelSize[][] = {{200}, // Columns
            {25,25,25,25,25}}; // Rows
            topPanel.setLayout(new TableLayout(topPanelSize));
            
            JLabel title = new JLabel("                         Your Tags");
            JButton edit = new JButton("Edit Tag");
            JButton create = new JButton("Create New Tag");
            JButton delete = new JButton("Delete Tag");
            
            topPanel.add(title,"0,1");
            topPanel.add(create,"0,2");
            topPanel.add(edit,"0,3");
            topPanel.add(delete,"0,4");
            
            tagPanel.add(topPanel,"0,0");
            tagPanel.add(tagScroll, "0,2");
            
            create.addActionListener(new NewTagListener());
            edit.addActionListener(new EditTagListener());
            delete.addActionListener(new DeleteTagListener());
            
            int result = JOptionPane.showConfirmDialog(null, tagPanel,
                "Manage Your Task Tags",JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) 
            {
                refresh();
                tagScroll.validate();
                tagScroll.repaint();
            }
        }
    }
    
    // responds to a click on the delete tag button
    // Removes tag from list
    // Changes all task references to deleted tag to -1
    private class DeleteTagListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(tagButtons.getSelection() != null)
            {
                String selectedTagName = tagButtons.getSelection().getActionCommand();
                
                int index = findTag(selectedTagName);
                
                // gets the correspoinding tagIndex of the tag that was deleted
                // then changes all tasks that referrence that index to -1
                Tag tempTag = tagList.get(index);
                int tagReferrence = tempTag.getTagIndex();
                
                manager.deleteTagReferences(tagReferrence);
                
                tagList.remove(index);
                
                refresh();
                tagScroll.validate();
                tagScroll.repaint();
                
                tagButtons.clearSelection();
                
            }
        }
    }
    
    // Responds to a click on the new tag button
    private class NewTagListener implements ActionListener
    {
        private int rgbInt;
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            
            JPanel newTagPanel = new JPanel();
            newTagPanel.setPreferredSize(new Dimension(500,400));
            double newPanelSize[][] = {{500}, // Columns
            {125,250}}; // Rows
            newTagPanel.setLayout(new TableLayout(newPanelSize));

            //top bar
            JPanel topPanel = new JPanel();
            JLabel tagName = new JLabel("<html>Tag Name<font color=\"red\">*</font></html>");
            JLabel tagColor = new JLabel("<html>Tag Color<font color=\"red\">*</font> (Choose Below)</html>");
            JTextField nameInput = new JTextField();
            
            JLabel nameInvalid = new JLabel();
            
            // Notifies user if attempts to create a tag with same as an already existing one
            nameInput.addFocusListener(new FocusListener()
            {
                @Override
                public void focusGained(FocusEvent e) {}

                @Override
                public void focusLost(FocusEvent e) 
                {
                    String nameTemp = nameInput.getText();

                    if(containsTag(nameTemp))
                    {
                        nameInvalid.setForeground(Color.RED);
                        nameInvalid.setText("Invalid Input: Name of already existing tag");
                        nameInput.setBorder(new LineBorder(Color.RED, 1));
                    }
                    else
                    {
                        nameInvalid.setText("");
                        nameInput.setBorder(new LineBorder(Color.BLACK, 1));
                    }
                }
            });
            
            double topPanelSize[][] = {{75,400}, // Columns
            {25,25,25,25}}; // Rows
            topPanel.setLayout(new TableLayout(topPanelSize));
            
            topPanel.add(nameInvalid,"1,0");
            topPanel.add(tagName,"0,1");
            topPanel.add(nameInput,"1,1");
            topPanel.add(tagColor,"1,3");
            
            newTagPanel.add(topPanel,"0,0");
            newTagPanel.add(colorChooser,"0,1");
            
            colorChooser.getSelectionModel().addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e) 
                {
                    Color color = colorChooser.getColor();
                    // convert rgb to hex to be stored easily and allows wider range of colors
                    int red = color.getRed();
                    int green = color.getGreen();
                    int blue = color.getBlue();
                    
                    rgbInt = (red << 16) + (green << 8) + blue;

                }
            });
            
            // Only accepts user input if all required fields are entered
            int result = JOptionPane.showConfirmDialog(null, newTagPanel,
                "New Tag", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) 
            {

                String name = nameInput.getText();
                
                if(name.equals(""))
                {
                    JOptionPane.showMessageDialog(null, "All required fields must be entered. Please input tag information again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                }
                else if (containsTag(name))
                {
                    JOptionPane.showMessageDialog(null, "Entered a name of an already existing tag.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    tagList.add(new Tag(name,rgbInt,tagIndex));
                    tagIndex++;

                    refresh();
                    tagScroll.validate();
                    tagScroll.repaint(); 
                }
                
                
            }
        }
    }
    
    // Responds to a click on the edit tag button
    private class EditTagListener implements ActionListener
    {
        private int rgbInt;
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(tagButtons.getSelection() != null)
            {
                String selectedTagName = tagButtons.getSelection().getActionCommand();
                
                // Get original color value associated with tag
                int tagIndex = findTag(selectedTagName);
                rgbInt = tagList.get(tagIndex).getTagColor();
            
                JPanel newTagPanel = new JPanel();
                newTagPanel.setPreferredSize(new Dimension(500,400));
                double newPanelSize[][] = {{500}, // Columns
                {125,250}}; // Rows
                newTagPanel.setLayout(new TableLayout(newPanelSize));

                //top bar
                JPanel topPanel = new JPanel();
                JLabel tagName = new JLabel("<html>Tag Name<font color=\"red\">*</font></html>");
                JLabel tagColor = new JLabel("<html>Tag Color<font color=\"red\">*</font> (Choose Below)</html>");
                JTextField nameInput = new JTextField(selectedTagName);

                JLabel nameInvalid = new JLabel();

                // Notifies user if attempts to create a tag with same as an already existing one
                nameInput.addFocusListener(new FocusListener()
                {
                    @Override
                    public void focusGained(FocusEvent e) {}

                    @Override
                    public void focusLost(FocusEvent e) 
                    {
                        String nameTemp = nameInput.getText();

                        if(containsTag(nameTemp) && (!nameTemp.equals(selectedTagName)))
                        {
                            nameInvalid.setForeground(Color.RED);
                            nameInvalid.setText("Invalid Input: Name of already existing tag");
                            nameInput.setBorder(new LineBorder(Color.RED, 1));
                        }
                        else
                        {
                            nameInvalid.setText("");
                            nameInput.setBorder(new LineBorder(Color.BLACK, 1));
                        }
                    }
                });

                double topPanelSize[][] = {{75,400}, // Columns
                {25,25,25,25}}; // Rows
                topPanel.setLayout(new TableLayout(topPanelSize));

                topPanel.add(nameInvalid,"1,0");
                topPanel.add(tagName,"0,1");
                topPanel.add(nameInput,"1,1");
                topPanel.add(tagColor,"1,3");

                newTagPanel.add(topPanel,"0,0");
                newTagPanel.add(colorChooser,"0,1");

                colorChooser.getSelectionModel().addChangeListener(new ChangeListener()
                {
                    public void stateChanged(ChangeEvent e) 
                    {
                        Color color = colorChooser.getColor();
                        // convert rgb to hex to be stored easily and allows wider range of colors
                        int red = color.getRed();
                        int green = color.getGreen();
                        int blue = color.getBlue();

                        rgbInt = (red << 16) + (green << 8) + blue;

                    }
                });

                // Only accepts user input if all required fields are entered
                int result = JOptionPane.showConfirmDialog(null, newTagPanel,
                    "New Tag", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) 
                {
                    
                    String name = nameInput.getText();
                    
                    if(name.equals(""))
                    {
                        JOptionPane.showMessageDialog(null, "All required fields must be entered. Please input tag information again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (containsTag(name) && (!name.equals(selectedTagName)))
                    {
                        JOptionPane.showMessageDialog(null, "Entered a name of an already existing tag.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        if(containsTag(selectedTagName))
                        {
                            int index = findTag(selectedTagName);
                            int oldTagIndex = tagList.get(index).getTagIndex();
                            tagList.remove(index);

                            tagList.add(new Tag(name,rgbInt,oldTagIndex));

                            refresh();
                            tagScroll.validate();
                            tagScroll.repaint();

                        }
                    }
                    tagButtons.clearSelection();
                    
                }
            }
            
        }
    }
    
    // Displays application instructions if you clicks on '?' button
    private class InstructionsListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
           
            JLabel instructions = new JLabel(
"<html><p><span style=\"font-size: 14px;\"><strong>Welcome to Minima.List!</strong></span></p><br>" +
"<p><span style=\"font-size: 10px;\">An online school planner whose minimalistic design allows you to effectively organize and complete assignments</span></p><br>" +
"<p><span style=\"font-size: 10px;\"><strong>Main Purpose:</strong></span></p>\n" +
"<p><span style=\"font-size: 10px;\">The purpose of the Minima.List School Planner is to allows its users to plan the assignments they must complete and exactly when they want to complete those assignments.</span></p>\n" +
"<p><span style=\"font-size: 10px;\">Users can do this by utilizing the Master List and Weekday List features. Users can add, edit, and delete assignments all within the Master List.</span></p>\n" +
"<p><span style=\"font-size: 10px;\">However, by simply selecting an assignment in the Master List, users can click the name of the weekday(s) they would like to complete the assignment on, "
        + "<br>and the assignment will then appear in that day's list.</span></p><br>" +
"<p><span style=\"font-size: 10px;\"><strong>Key Features Include:</strong></span></p><br>" +
"<ul>\n" +
"    <li><span style=\"font-size: 10px;\">All assignments within the Master List and Weekday Lists are ordered by urgency based on due date,"
        + "<br> meaning the assignment with the earliest due date will appear at the top of the list.</span></li>\n" +
"    <li><span style=\"font-size: 10px;\">Tags that allow the user to color code assignments.</span></li>\n" +
"    <li><span style=\"font-size: 10px;\">Assignments and tags are automatically saved to a csv file when the application is closed.</span>\n" +
"        <ul>\n" +
"            <li><span style=\"font-size: 10px;\">This csv file can be imported into Google Sheets or Excel.</span></li>\n" +
"        </ul>\n" +
"    </li>\n" +
"</ul>\n" +
"<p><span style=\"font-size: 10px;\"><strong>Instructions for Use:</strong></span></p>\n" +
"<ul>\n" +
"    <li><span style=\"font-size: 10px;\"><u>Adding New Assignments</u>: Press the '+' button to the right of the Master List title to create a new assignment.</span></li>\n" +
"    <li><span style=\"font-size: 10px;\"><u>Editing Assignments</u>: Once an assignment is added to the Master List, select the assignment then press 'Edit Assignment' to change any information.</span></li>\n" +
"    <li><span style=\"font-size: 10px;\"><u>Removing Assignments from WeekDay Lists</u>: Select the assignment in the Master List you would like to remove from all Weekday Lists, then press 'Remove from Weekdays'.</span></li>\n" +
"    <li><span style=\"font-size: 10px;\"><u>Completing/Deleting Assignments</u>: Select the assignment in the Master List you would like to mark as complete, then press 'Delete'. "
        + "<br>This assignment will then be removed from the Master List and all Weekday Lists.</span></li>\n" +
"    <li><span style=\"font-size: 10px;\"><u>Managing Tags</u>: Click the 'Manage Tag' button to create, edit, and delete tags that you can use to color code assignments.</span></li>\n" +
"</ul>"
        + "<p>Check out our website for m<span style=\"color: rgb(0, 0, 0);\">ore: minima-list.angelalayton.com</span></p>\n" +
"<p><br></p>"
        + "</html>");

            JOptionPane.showMessageDialog(mainPanel,instructions,"Instructions for using this online planner",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Adds selected task to monday list if button is clicked
    private class MondayListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String key = "monday";
            
            if(taskButtons.getSelection() != null)
            {
                ArrayList<String> infoArray = getSelectedTask();
                
                String taskName = infoArray.get(0);
                String taskDate = infoArray.get(1);
                String taskDescription = infoArray.get(2);
                String tagString = infoArray.get(3);
                int tagInt = Integer.parseInt(tagString);
                
                if (!doubleTask(key, taskName))
                {
                    manager.addTask(key, taskName,taskDate, taskDescription, tagInt);
                }
                refresh();
            }
        }
    }
    
    // Adds selected task to tuesday list if button is clicked
    private class TuesdayListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String key = "tuesday";
            if(taskButtons.getSelection() != null)
            {
                ArrayList<String> infoArray = getSelectedTask();
                
                String taskName = infoArray.get(0);
                String taskDate = infoArray.get(1);
                String taskDescription = infoArray.get(2);
                String tagString = infoArray.get(3);
                int tagInt = Integer.parseInt(tagString);
                
                if (!doubleTask(key, taskName))
                {
                    manager.addTask(key, taskName,taskDate, taskDescription,tagInt);
                }
                refresh();
            }
        }
    }
    
    // Adds selected task to wednesday list if button is clicked
    private class WednesdayListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String key = "wednesday";
            if(taskButtons.getSelection() != null)
            {
                ArrayList<String> infoArray = getSelectedTask();
                
                String taskName = infoArray.get(0);
                String taskDate = infoArray.get(1);
                String taskDescription = infoArray.get(2);
                String tagString = infoArray.get(3);
                int tagInt = Integer.parseInt(tagString);
                
                if (!doubleTask(key, taskName))
                {
                    manager.addTask(key, taskName,taskDate, taskDescription, tagInt);
                }
                refresh();
            }
        }
    }
    
    // Adds selected task to thursday list if button is clicked
    private class ThursdayListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String key = "thursday";
            if(taskButtons.getSelection() != null)
            {
                ArrayList<String> infoArray = getSelectedTask();
                
                String taskName = infoArray.get(0);
                String taskDate = infoArray.get(1);
                String taskDescription = infoArray.get(2);
                String tagString = infoArray.get(3);
                int tagInt = Integer.parseInt(tagString);
                
                if (!doubleTask(key, taskName))
                {
                    manager.addTask(key, taskName,taskDate, taskDescription,tagInt);
                }
                
                refresh();
            }
        }
    }
    
    // Adds selected task to friday list if button is clicked
    private class FridayListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String key = "friday";
            if(taskButtons.getSelection() != null)
            {
                ArrayList<String> infoArray = getSelectedTask();
                
                String taskName = infoArray.get(0);
                String taskDate = infoArray.get(1);
                String taskDescription = infoArray.get(2);
                String tagString = infoArray.get(3);
                int tagInt = Integer.parseInt(tagString);
                
                if (!doubleTask(key, taskName))
                {
                    manager.addTask(key, taskName,taskDate, taskDescription,tagInt);
                }
                refresh();
            }
        }
    }
    
    // Adds saturday task to monday list if button is clicked
    private class SaturdayListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String key = "saturday";
            if(taskButtons.getSelection() != null)
            {
                ArrayList<String> infoArray = getSelectedTask();
                
                String taskName = infoArray.get(0);
                String taskDate = infoArray.get(1);
                String taskDescription = infoArray.get(2);
                String tagString = infoArray.get(3);
                int tagInt = Integer.parseInt(tagString);
                
                if (!doubleTask(key, taskName))
                {
                    manager.addTask(key, taskName,taskDate, taskDescription,tagInt);
                }
                refresh();
            }
        }
    }
    
    // Adds sunday task to monday list if button is clicked
    private class SundayListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String key = "sunday";
            if(taskButtons.getSelection() != null)
            {
                ArrayList<String> infoArray = getSelectedTask();
                
                String taskName = infoArray.get(0);
                String taskDate = infoArray.get(1);
                String taskDescription = infoArray.get(2);
                String tagString = infoArray.get(3);
                int tagInt = Integer.parseInt(tagString);
                
                if (!doubleTask(key, taskName))
                {
                manager.addTask(key, taskName,taskDate, taskDescription,tagInt);
                }
                refresh();
            }
        }
    }
    
    // Calendar and date formatter
    public class DateLabelFormatter extends AbstractFormatter 
    {

        private String dateFormat = "MM/dd/yy";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(this.dateFormat);

        @Override
        public Object stringToValue(String text) throws ParseException 
        {
            return this.dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException 
        {
            if (value != null) {
                Calendar calendar = (Calendar) value;
                return this.dateFormatter.format(calendar.getTime());
            }

            return "";
        }

    }
    
    
    
}
