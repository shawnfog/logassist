package logassist;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.*;
import java.util.Scanner;

public class MainGUI
{
// window frame
    private JFrame frame; // primary window frame
    
    private JPanel contentPane; //contents of all regions goes in here
    private JTextArea logDisplay;
    private JScrollPane scrollArea;
    private JButton logButton, funcButton, clearButton;
    private File currentFile;
    private String line;
    
    public static void main (String [] args) 
    {
    	// create new GUI object to display everything
        MainGUI gui = new MainGUI();
        gui.start();
    }    
    
    public void start() 
    {
    	// This is the main frame where other methods are set to display
        frame = new JFrame("Log Analysis Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        makeMenus();
        makeContent();
        
        frame.pack();
        frame.setVisible(true);
    }
    
    private void makeMenus()
    {
        JMenuBar menuBar;
        
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        // set up menus
        menuBar.add(makeFileMenu());
        menuBar.add(makeHelpMenu());
    }
    
    private JMenu makeFileMenu()
    {
        JMenu menu;
        JMenuItem menuItem;        // set up the File menu
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        
        // add New menu item
        menuItem = new JMenuItem("Menu Item Stub 1");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(new NewListener());
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N,
                                       Event.CTRL_MASK));
        menu.add(menuItem);
          
        // add Save menu item
        menuItem = new JMenuItem("Menu Item Stub 2");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.addActionListener(new SaveListener());
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                       Event.CTRL_MASK));
        menu.add(menuItem);        // add Exit menu item
        menu.addSeparator();
        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.addActionListener(new ExitListener());
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                       Event.CTRL_MASK));
        menu.add(menuItem);
          
        return menu;
    }    
    
    private JMenu makeHelpMenu()
    {
        JMenu menu;
        JMenuItem menuItem;        // set up the Help menu
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        
        // add About menu item
        menuItem = new JMenuItem("About this tool...");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(new AboutListener());
        menu.add(menuItem);
          
        return menu;
    }
    
    // This section defines inner class listeners to perform actions whenever an item is selected in the GUI
    // There will be one listener for each interactive item unless one listener can be easily utilized for a common set of actions
    private class AboutListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            JOptionPane.showMessageDialog(frame, 
                    "Log Analysis Assistant\n\n" +
                    "Version 1.0\n" +
                    "Designed by: Shawn Fogarty\n" +
                    "Technical Support - OpenText\n\n" +
                    "This tool is to aid support personnel in quickly finding useful data within Content Server log files.",
                    "Log Analysis Assistant Help", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            JOptionPane.showMessageDialog(frame, "");
        }
    }
    
    // Set's main layout for entire main window portion of GUI and contains each region for Border Layout
    private void makeContent()
    {
    	// This is alternate simple object to use instead of below: Container contentPane = frame.getContentPane(); 
    	// must use below to cast the frame as a JPanel object so that it can store sub panels "regions" within it to handle
    	// more complex GUI layouts 
    	contentPane = (JPanel)frame.getContentPane();
        contentPane.setLayout(new BorderLayout(6,6)); // 6,6 represents the border gap sizes
        
        makeNorthRegion(); // holds File chooser button, name of of file selected
        makeCenterRegion(); // holds a flow layout of all the log search buttons
        makeSouthRegion(); // holds display of the log search results read from the log file, clear button
    }
    
    private void makeNorthRegion()
    {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    	logButton = new JButton("Choose Log File");
    	panel.add(logButton);
    	logButton.addActionListener(new LoadLogFileListener());   	
    	contentPane.add(panel,BorderLayout.NORTH);
    }
    
    private void makeCenterRegion()
    {
    	JPanel panel = new JPanel();
    	panel.setLayout(new FlowLayout());
    	
    	clearButton = new JButton("Clear Log");
    	panel.add(clearButton);
    	clearButton.addActionListener(new LogParseButtonsListener());
    	
    	funcButton = new JButton("All Functions");
    	panel.add(funcButton);
    	funcButton.addActionListener(new LogParseButtonsListener());
    	
    	panel.add(new JButton("Errors"));
    	panel.add(new JButton("Patches"));
    	contentPane.add(panel,BorderLayout.CENTER);
    }
    
    private void makeSouthRegion()
    {
    	JPanel panel = new JPanel();
    
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setBorder(BorderFactory.createTitledBorder("Parsed Data from Log File Read"));
    	
    	   // create a text area to display data: need to make a "sub" panel to hold text area within this panel
    	JPanel smallPanel = new JPanel();
    	smallPanel.setLayout(new BoxLayout(smallPanel,BoxLayout.Y_AXIS));
        logDisplay = new JTextArea(20,100);
        // next 3 lines required to add scroll bar display always, note that the textarea called logdisplay is attached to the scrollarea
        // and that scrollarea object is added to the small pane.
        scrollArea = new JScrollPane(logDisplay);
        scrollArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        smallPanel.add(scrollArea,BorderLayout.SOUTH);
        panel.add(smallPanel);
        
    	contentPane.add(panel,BorderLayout.SOUTH);
   
    }
    
    //******* Section for all the listeners on page interaction from the GUI to perform appropriate processing *******
    
    
    private class ExitListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    }
    
    private class NewListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            JOptionPane.showMessageDialog(frame, 
                    "The  File > New  menu option was clicked", 
                    "placeholder", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private class SaveListener implements ActionListener
    {
        public void actionPerformed(ActionEvent ae)
        {
            JOptionPane.showMessageDialog(frame, 
                    "The  File > Save  menu option was clicked", 
                    "place holder", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }  
    
    private class LoadLogFileListener implements ActionListener
    {
    	//private File currentFile;
    	
    	
    	public void actionPerformed(ActionEvent e)
    	{
    		StringBuilder sb = new StringBuilder();
    		// file chooser box with filter on text files
        	JFileChooser fc = new JFileChooser();
        	FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "out", "log");
        	fc.setFileFilter(filter);
        	fc.showOpenDialog(frame);
        	currentFile = fc.getSelectedFile();
        	String filename = currentFile.getAbsolutePath(); // last 2 lines here stores the path to actual file that next lines will read
        	// in the try block below
        	
        	try 
        	{
        		FileReader reader = new FileReader(filename);
        		BufferedReader br = new BufferedReader(reader);
        		logDisplay.read(br, null);
        		br.close();
            }        	
        	catch (IOException fnfe)
        	{
        		System.out.println("*** I/O Error ***\n" + fnfe);
        	}
    	}
    }
    
    private class LogParseButtonsListener implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		
    		if (e.getSource() == clearButton)
    			logDisplay.setText("");
    		else if (e.getSource() == funcButton)
    			logDisplay.setText("");
    			
    	}
    }
}
