
package minima_list;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import javax.swing.*;

public class Minima_List{  
    

    public static void main(String[] args) {
        
        UserInterface ui = new UserInterface();
        
        JFrame frame = new JFrame();
        frame.setTitle("Minima.List: Your minimalistic personal planner");
        frame.setSize(1250,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ui.setUp(frame);

        frame.setResizable(false);
        frame.addWindowListener(new WindowListener()
        {
            @Override
            public void windowOpened(WindowEvent e) 
            {
                // When application opens it reads saved information from files
                String pathTaskName = "Files/tasks.txt";
                String pathTagName = "Files/tags.txt";
                
                File tempTaskFile = new File(pathTaskName);
                
                if(tempTaskFile.length() != 0)
                {
                    try
                    {
                        ui.readfromTaskFile(pathTaskName);
                        ui.refresh();
                    }
                    catch(Exception fileException1)
                    {
                    }
                    
                }
                
                File tempTagFile = new File(pathTagName);
                
                if(tempTagFile.length() != 0)
                {
                    try
                    {
                        ui.readfromTagFile(pathTagName);
                        ui.refresh();
                    }
                    catch(Exception fileException2)
                    {
                    }
                    
                }
            }

            @Override
            public void windowClosing(WindowEvent e) 
            {
                // When application closes all tasks and tags are saved to a file
                String taskPathName = "Files/tasks.txt";
                String tagPathName = "Files/tags.txt";
                try
                {
                    ui.saveToTaskFile(taskPathName);
                    ui.saveToTagFile(tagPathName);
                }
                catch(Exception exception){}

            }

            @Override
            public void windowClosed(WindowEvent e) {}

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        frame.setVisible(true);        
    }
 
}
