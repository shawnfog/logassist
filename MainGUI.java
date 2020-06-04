/*
 * The MainGUI.java file contains all code necessary to run a program that allows you to choose a text based log file and display it's 
 * contents within the application. The end goal is to have a list of buttons that when clicked, will automatically parse through the 
 * selected log file and display only the relevant lines using the logic triggered by the selected button. This application could aid 
 * technical support analysts with quickly identifying important information in logs to help troubleshoot issues.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Scanner;
import java.util.regex.*;

public class MainGUI
{
// window frame
    private JFrame frame; // primary window frame
    
    private JPanel contentPane; //contents of all regions goes in here
    private JTextArea logDisplay, fileNameDisplay;
    private JScrollPane scrollArea;
    private JButton logButton, funcButton, clearButton, errorsButton, patchesButton, patListButton, genErrButton,
    soapButton, restButton, traceButton, wfButton, crtButton, dberrButton, dawfButton, wrButton, customizedButton, regExButton;
    private File currentFile;
    private String line, lineSaveToFile, userInputTest; //userInputTest for TESTING ONLY
    Scanner scan = null;
    PrintStream parsedLogs = null;
    
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
        
        //frame.pack();
        frame.setSize(1000, 700);
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
        menuItem = new JMenuItem("Menu stub item 1");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(new NewListener());
        menuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_N,
                                       Event.CTRL_MASK));
        menu.add(menuItem);
          
        // add Save menu item
        menuItem = new JMenuItem("Save currently displayed log to 'SavedLog' file");
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
    	contentPane.setLayout(new BorderLayout(6,6)); // border gab sizes for this panel
    	
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    	panel.setBorder(BorderFactory.createTitledBorder("Log selection options   |   Path of loaded log file"));
    	logButton = new JButton("Choose Log File");
    	logButton.setForeground(Color.BLUE);
    	panel.add(logButton);
    	logButton.addActionListener(new LoadLogFileListener());
    	
    	// display text area to display selected log file name
    	fileNameDisplay = new JTextArea();
    	panel.add(fileNameDisplay); 
    	fileNameDisplay.setMaximumSize(new Dimension(800,24)); //set fixed size x, y axis for the this text area only
    	
    	 /* get and display image in NORTH region - to be implemented later
        JLabel imgLabel = new JLabel(new ImageIcon("L08-06.jpg"), JLabel.CENTER);
        contentPane.add(imgLabel, BorderLayout.NORTH); */

    	contentPane.add(panel,BorderLayout.NORTH);
    }
    
    private void makeCenterRegion()
    {
    	JPanel panel = new JPanel();
    	panel.setLayout(new FlowLayout());
    	panel.setBorder(BorderFactory.createTitledBorder("Press a button to display the relevent logs  |  Hover mouse over for description    Note: UI may be unresponsive while processing"));
    	
    	clearButton = new JButton("Clear Log");
    	clearButton.setForeground(Color.BLUE);
    	panel.add(clearButton);
    	clearButton.addActionListener(new LogParseButtonsListener());
    	
    	funcButton = new JButton("All Functions");
    	panel.add(funcButton);
    	funcButton.setToolTipText("Uses regex to find/display all lines that contain request data, action data, obj type data related to functions");
    	funcButton.addActionListener(new LogParseButtonsListener());
    	
    	errorsButton = new JButton("Dispatcher Errors");
    	panel.add(errorsButton);
    	errorsButton.setToolTipText("Finds details behind the errors seen in the GUI of CS as they often manifest as a dispatcher type error in the logs");
    	errorsButton.addActionListener(new LogParseButtonsListener());
    	
    	patchesButton = new JButton("Patch Detail");
    	panel.add(patchesButton);
    	patchesButton.setToolTipText("Displays all the patch comments including JIRA #, applicable version, list of fixes for cumulatives. More than just patch list.");
    	patchesButton.addActionListener(new LogParseButtonsListener());
    	
    	patListButton = new JButton("Patch List");
    	panel.add(patListButton);
    	patListButton.setToolTipText("Displays only patch numbers");
    	patListButton.addActionListener(new LogParseButtonsListener());
    	
    	genErrButton = new JButton("All Errors");
    	panel.add(genErrButton);
    	genErrButton.setToolTipText("Looks for any indications of errors in the logs. Good for a quick health check or if you are not sure what the error looks like");
    	genErrButton.addActionListener(new LogParseButtonsListener());
    	
    	soapButton = new JButton("CWS Issues");
    	panel.add(soapButton);
    	soapButton.setToolTipText("Finds SDK related issues with external apps connecting to CS via Content Web Services API");
    	soapButton.addActionListener(new LogParseButtonsListener());
    	
    	restButton = new JButton("REST Issues");
    	panel.add(restButton);
    	restButton.setToolTipText("Finds SDK related issues with external apps connecting to CS via REST API");
    	restButton.addActionListener(new LogParseButtonsListener());
    	
    	traceButton = new JButton("Trace log");
    	panel.add(traceButton);
    	traceButton.setToolTipText("Tells you if there is any trace files created. If you see it return something, go look at the actual trace log for more");
    	traceButton.addActionListener(new LogParseButtonsListener());
    	
    	wfButton = new JButton("Workflow");
    	panel.add(wfButton);
    	wfButton.setToolTipText("Tries to find key data points related to workflow processing activities");
    	wfButton.addActionListener(new LogParseButtonsListener());
    	
    	crtButton = new JButton("CRT Issues");
    	panel.add(crtButton);
    	crtButton.setToolTipText("Tries to find key data points related to CRT / Engineering issues");
    	crtButton.addActionListener(new LogParseButtonsListener());
    	
    	dberrButton = new JButton("DB Errors");
    	panel.add(dberrButton);
    	dberrButton.setToolTipText("Looks for lines containing any ORA or ODBC sql errors");
    	dberrButton.addActionListener(new LogParseButtonsListener());
    	
    	dawfButton = new JButton("DA Agent Workflow");
    	panel.add(dawfButton);
    	dawfButton.setToolTipText("Load a 'distributedagentxxxxx' thread log and this button will provide workflow related info from the DA");
    	dawfButton.addActionListener(new LogParseButtonsListener());
    	
    	wrButton = new JButton("WebReports");
    	panel.add(wrButton);
    	wrButton.setToolTipText("Finds data points such as triggers, ojbid, rkt engine and all types of function lines related to WebReports."
    			+ "Note: WRs don't hold much info in threads, use this to identify where to follow the details in corresponding connect log");
    	wrButton.addActionListener(new LogParseButtonsListener());
    	
    	customizedButton = new JButton("Customizations");
    	panel.add(customizedButton);
    	customizedButton.setToolTipText("Searches known indicators of customizations installed in the system");
    	customizedButton.addActionListener(new LogParseButtonsListener());
    	
    	// *** FOR TESTING ONLY
    	regExButton = new JButton("Regex Test");
    	panel.add(regExButton);
    	regExButton.setToolTipText("EXPERIMENTAL - TESTING PURPOSES ONLY");
    	regExButton.addActionListener(new LogParseButtonsListener());
    	
    	
    	contentPane.add(panel,BorderLayout.CENTER);
    }
    
    private void makeSouthRegion()
    {
    	JPanel panel = new JPanel();
    
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setBorder(BorderFactory.createTitledBorder("Parsed data from log file read    TIP: Use ctrl+c on selected text to easily copy out"));
    	
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
                    "A copy of last parsed log lines are saved in the", 
                    "parsedlogs2 file in same dir as this app",
                    JOptionPane.INFORMATION_MESSAGE);
     
            try
            {
            	// read parsedlogs2 file and save whatever data in this file when actionlistener fires to the SavedLogs.txt file
            	scan = new Scanner(new File("ParsedLogs2.txt"));
            	PrintStream savedLogs = new PrintStream("SavedLogs.txt");
            	while (scan.hasNextLine())
            	{
            		lineSaveToFile = scan.nextLine();
            		savedLogs.println(lineSaveToFile);
            	}
            	scan.close(); // to always explicitely close scan resource to prevent memory leak
                savedLogs.close();
            }
            catch (IOException e)
            {
            	System.out.println("Error Creating SavedLogs file" + e);
            }

        }
    }  
    
    private class LoadLogFileListener implements ActionListener
    {
    	
    	public void actionPerformed(ActionEvent e)
    	{
    		// file chooser box with filter on text files
        	JFileChooser fc = new JFileChooser();
        	
        	FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "out", "log");
        	fc.setFileFilter(filter);
        	int result = fc.showOpenDialog(frame);
        	if (result == JFileChooser.APPROVE_OPTION) {
        		currentFile = fc.getSelectedFile();
        		
        		// Display Path and name of file selected into GUI (next 3 lines for this functionality)
        		File fn = fc.getSelectedFile(); // File object initiated for correct type for compatibility of datatype for JTextArea and setText methods.
        		String strFileName = fn.getAbsolutePath(); // get path of file selected and assign it to the dedicated strFileName var
        		fileNameDisplay.setText(strFileName); // pass filename string as argument to the setText method to display in GUI for user
        	}
        	else System.exit(0);
        	
        	String filename = currentFile.getAbsolutePath(); // last 2 lines here stores the path to actual file that next lines will read
        	// in the try block below
        	
        	try 
        	{    // obtain data from saved file, take it from buffer and display it in jtextarea "logDisplay"
        		FileReader reader = new FileReader(filename);
        		BufferedReader br = new BufferedReader(reader);
        		logDisplay.read(br, null);
        		br.close();
            }        	
        	catch (IOException fnfe)
        	{
        		System.out.println("*** I/O Error ***\n" + fnfe);
        	}
        	
        	// Write file contents from file chosen based on path stored in "filename" into designated text file
        	try
        	{
        		scan = new Scanner(new BufferedReader(new FileReader(filename)));
        		parsedLogs = new PrintStream("ParsedLogs.txt");
        		while (scan.hasNextLine())
        		{
        			line = scan.nextLine();
        			//if (line.contains("Error"))
        			parsedLogs.println(line);
        		}
        		scan.close(); // to always explicitely close scan resource to prevent memory leak
        		parsedLogs.close();

        	}
        	catch (IOException se)
        	{
        		System.out.println("*** I/O Error in Parse file creation.***\n" + se);
        	}
    	}
    }
    
    private class LogParseButtonsListener implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		try
    		{
    			//logic here to parse lines and display based on printstream arguments
    			scan = new Scanner(new File("ParsedLogs.txt")); //scans contents of this file
    			//PrintStream below creates new file to store parsed results into new file
    			PrintStream parsedLogs2 = new PrintStream("ParsedLogs2.txt"); 
    			if (e.getSource() == clearButton)
    				logDisplay.setText("");
    			else if (e.getSource() == funcButton)

    				while (scan.hasNextLine())
    				{
    					// for matches method, it uses regex where the pattern passed in must match the entire line identically. ".+" means any string value may appear
    					// in the location in which this regex is specified in the line matcher
    					line = scan.nextLine();
    					if (line.matches(".+Func='ll..+..+") | line.matches(".+func=ll..+") | line.matches(".+func=.+&objaction=.+") | line.matches(".+Func='.+'.+"))
    					parsedLogs2.println(line);
    					FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
    				}
				else if (e.getSource() == errorsButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.contains("Dispatcher"))
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == patchesButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.indexOf("Comment") > 23) //text Comment must appear 23 or more char spaces from left
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == patListButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.indexOf("Patch PAT") > 31) // text must appear 31 or more char space fr left
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == genErrButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (!line.contains("Comment:") && line.contains("error") ) //exclude errors from patch comments
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == soapButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (!line.contains("Args.") && line.contains("inArgs: A") || line.contains("outArgs: A")
						|| line.contains("Arguments =") || line.contains("[E") || line.contains("DocMan.AttributeGroup")) //api logs
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == restButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.contains("inArgs:") || line.contains("outArgs:")
						|| line.contains("Arguments =") || line.contains("PATH_INFO = '/api/")) //REST logs
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == traceButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.contains("Created trace log:")) //indication of trace file in thread log
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == wfButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.contains("work.taskdone") || line.contains("Select * from wsubworktask where subworktask_workid")
						|| line.contains("dbo.WLock"))
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
    			
				else if (e.getSource() == crtButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.contains("crt.validateloadsheet") || line.contains("crt.uploadfiletocontentserver")
						|| line.contains("crt.getloadfile") || line.contains("crt.getloadfile") || line.contains("crt.bulkloadresults")
						|| line.contains(".ListRevisions") || line.contains("BulkLoadStatusID ="))
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == dberrButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.indexOf("ORA-") > 22 || line.contains("[Microsoft][ODBC SQL Server Driver][SQL Server]")
						|| line.contains("error executing an sql statement")) 
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == dawfButton)
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.contains("Starting task WFMAIN_") || line.contains("'QueueTime'")
						|| line.contains("'Priority=60'") || line.contains("'WorkerID'") || line.contains("SubWork_Title")
						|| line.contains("SubWork_SubWorkID") || line.contains("SubWorkTask_Title") || line.contains("execute item handler step for Work_WorkID")) 
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == wrButton) // finds data points related to execution of WebReports
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.contains("**************  RKTEngine") || line.contains("reportview_content = '") || line.contains("objAction = 'RunReport'") ||
						line.matches(".+.runreport'.+") || line.contains("&objAction=RunReport") || line.contains("WebReports Agent: WebReport Agent: Running report") ||
						line.matches(".+Func='ll..+.WRTrigger'.+") || line.contains("Trigger_1_Path =") || line.contains("Triggers_1_list =")) 
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == customizedButton) // finds indications of 3rd party customizations
				    while (scan.hasNextLine())
				    {
					    line = scan.nextLine();
						if (line.indexOf("Module:") < 38 || line.indexOf("Author:") < 1  || line.indexOf("Company:") < 1 || line.indexOf("Creation Date:") < 1
						|| line.contains("gci") || line.contains("Fastman") || line.contains("Global Cents") || line.contains("imagelinks") || line.contains("answers")
						|| line.contains("kinematik"))
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
				else if (e.getSource() == regExButton) //testing regex and user input
				    while (scan.hasNextLine())
				    {
				    /*	userInputTest = "func=";
				    	
					    line = scan.nextLine();
					    //TEST for text stored in variable
						if (line.contains(userInputTest))
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
    					*/
				    	
				    	line = scan.nextLine();
					    //regex test - argument must match entire line
						if (line.matches(".+Func='ll..+.WRTrigger'.+"))
						parsedLogs2.println(line);
						FileReader reader = new FileReader("ParsedLogs2.txt");
    					logDisplay.read(reader, "ParsedLogs2.txt"); //Object of JTextArea
				    }
    			
    			
    			
    		scan.close();
    		parsedLogs2.close();
    		}
    		catch (IOException pe)
    		{
    			System.out.println("*** I/O Error in File Parsing ***\n" + pe);
    		}
    		return;	
    	}
    }
}

