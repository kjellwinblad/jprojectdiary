package projektdiary.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuBar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

//import sun.swing.SwingUtilities2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import projektdiary.api.DiaryEvent;
import projektdiary.api.DiaryFileFormatHelper;

public class MainFrame extends JFrame implements Serializable {

	// Komponent deklarationer
	// Deklarationer av kommponenter som beh�ver vara publika
	private JList eventList;

	private ActionListener changeListener;

	private JLabel totalTimeLabel;

	private JSplitPane splitPane;

	private DiaryEventEditor editor = null;

	private JPanel eventListPanel;

	private MainFrame thisPane = this;

	private ThemeChangeListener themeChanger = null;

	// Annat
	private List<DiaryEvent> list = null;

	private File saveFile = null;

	private Integer lastSplitPos;

	private boolean changed = false;

	private Properties mainProps;
	
	private JFileChooser fChooser;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2734049189357573813L;

	public MainFrame(String title) {
		super(title);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addComponents();

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (saveQuestionExit()) {
					saveWindowProperies();
					saveQuestionExit();
				}

			}
		});

		// pack();
	}

	/**
	 * Sparar inst�llningarna f�r f�nstret och programmet f�r att kunna ladda
	 * samma vid n�sta start av programmet
	 * 
	 */
	private void saveWindowProperies() {

		// B�rjar med att skapa en lista med egenskpaer utifr�n Programmets
		// tillst�nd:

		Properties props = new Properties();

		props.put("x_pos", (new Integer(getLocation().x).toString()));
		props.put("y_pos", (new Integer(getLocation().y).toString()));
		props.put("width", (new Integer(getSize().width).toString()));
		props.put("height", (new Integer(getSize().height).toString()));
		props.put("component_orientation", (new Integer(splitPane
				.getOrientation()).toString()));
		props
				.put(
						"first_component",
						splitPane.getLeftComponent() instanceof DiaryEventEditor ? "editor"
								: "list");
		props.put("split_pos", (new Integer(splitPane.getDividerLocation())
				.toString()));
		props.put("theme", UIManager.getLookAndFeel().getClass().getName());

		props.put("save_file", saveFile != null ? saveFile.getAbsolutePath()
				: "");

		this.lastSplitPos = splitPane.getDividerLocation();
		splitPane.setDividerLocation(lastSplitPos);
		// Filen att spara till
		final File saveTo = new File(System.getProperty("user.home")
				+ File.separator + ".projektdiary" + File.separator
				+ "mainWindow.ini");

		if (saveTo.exists()) {
			if (!saveTo.delete()) {
				JOptionPane.showMessageDialog(thisPane,
						"Could not delete old window configuration file.");
				return;
			}
		} else {
			File propDir = new File(System.getProperty("user.home")
					+ File.separator + ".projektdiary");
			if (!propDir.exists() && !propDir.mkdir()) {
				JOptionPane
						.showMessageDialog(thisPane,
								"Could not create configuration directory in home folder: .projektdiary");
				return;
			}
		}

		// Vi �r klara f�r att skapa inst�llnigsfilen
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(saveTo);
		} catch (FileNotFoundException e) {
			JOptionPane
					.showMessageDialog(thisPane,
							"Could not open properites file for writing in: .projektdiary");
			return;
		}

		try {
			props.store(fos, "Window and program propertis for Project Diary");

		} catch (IOException e) {
			JOptionPane.showMessageDialog(thisPane,
					"Could not open properites file for writing in: .projektdiary"
							+ e);
		}

	}

	private boolean saveQuestionExit() {

		if (list != null && list.size() != 0 && changed) {
			int option = JOptionPane.showConfirmDialog(this,
					"Do you want to save current diary?");
			if (option == JOptionPane.YES_OPTION) {
				if (editor != null)
					editor.save();
				save(true);
			} else if (option == JOptionPane.NO_OPTION) {
					System.exit(0);
			}else if (option == JOptionPane.CANCEL_OPTION) {

				return false;
			}

		}
		return true;

	}
	
	private synchronized JFileChooser getFileChooser(){
		if(fChooser==null)
			fChooser = new JFileChooser();
		
		return fChooser;
	}

	/**
	 * L�gger till komponenter i f�nstret
	 * 
	 */
	private void addComponents() {

		setLayout(new BorderLayout());
		
		mainProps = loadProperties();
		
		try {
			UIManager.setLookAndFeel(mainProps.getProperty("theme"));
		} catch (Exception e) {
		}
		
		new Thread(new Runnable(){

			public void run() {
				getFileChooser();
				
			}
			
		}).start();
		

		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				this.getClass().getResource("icon.jpg")));

		

		setSize(new Integer(mainProps.getProperty("width")).intValue(),
				new Integer(mainProps.getProperty("height")).intValue());
		this.setLocation(
				new Integer(mainProps.getProperty("x_pos")).intValue(),
				new Integer(mainProps.getProperty("y_pos")).intValue());
		// Menyn!
		addMenu();

		splitPane = new JSplitPane(new Integer(mainProps
				.getProperty("component_orientation")).intValue());

		eventListPanel = createEventListPanel();
		editor = new DiaryEventEditor(new DiaryEvent(), changeListener);

		if (mainProps.getProperty("first_component").equals("list")) {
			splitPane.setLeftComponent(eventListPanel);
			splitPane.setRightComponent(editor);
		} else {
			splitPane.setLeftComponent(editor);
			splitPane.setRightComponent(eventListPanel);

		}


		if (mainProps.getProperty("save_file").length() != 0) {
			saveFile = new File(mainProps.getProperty("save_file"));
			try {
				list = DiaryFileFormatHelper.getFromFile(saveFile);

				int splitPos = new Integer(mainProps.getProperty("split_pos"))
						.intValue();
				eventSelected(list.size() - 1);

				if (splitPane != null)
					splitPane.setDividerLocation(splitPos);
				this.eventListChanged();
			} catch (Exception e) {
				e.printStackTrace();
				createNew();
			}
		} else
			createNew();

		eventListPanel.setVisible(true);

		add(splitPane);

		themeChanger.actionPerformed(null);

	}

	private Properties loadProperties() {

		mainProps = new Properties();

		final File getFrom = new File(System.getProperty("user.home")
				+ File.separator + ".projektdiary" + File.separator
				+ "mainWindow.ini");

		try {
			FileInputStream fis = new FileInputStream(getFrom);
			try {
				mainProps.load(fis);
			} catch (IOException e) {
				
			}
		} catch (FileNotFoundException e) {
			
		}

		if (mainProps.getProperty("width") == null)
			mainProps.setProperty("width", "700");
		if (mainProps.getProperty("height") == null)
			mainProps.setProperty("height", "500");
		if (mainProps.getProperty("component_orientation") == null)
			mainProps.setProperty("component_orientation", new Integer(
					JSplitPane.HORIZONTAL_SPLIT).toString());
		if (mainProps.getProperty("save_file") == null)
			mainProps.setProperty("save_file", "");
		if (mainProps.getProperty("theme") == null)
			mainProps.setProperty("theme", UIManager.getLookAndFeel()
					.getClass().getName());
		if (mainProps.getProperty("y_pos") == null)
			mainProps.setProperty("y_pos", "250");
		if (mainProps.getProperty("x_pos") == null)
			mainProps.setProperty("x_pos", "250");
		if (mainProps.getProperty("split_pos") == null)
			mainProps.setProperty("split_pos", "150");
		if (mainProps.getProperty("first_component") == null)
			mainProps.setProperty("first_component", "editor");

		return mainProps;
	}

	private JPanel createEventListPanel() {
		// Panel f�r h�ndelselistan
		eventListPanel = new JPanel();

		eventListPanel.setLayout(new BorderLayout());

		// Scrollbar f�r event listan

		eventList = new JList();

		eventList.setCellRenderer(new EventListCellRender());

		eventList.addMouseMotionListener(new MouseMotionAdapter() {

			public void mouseMoved(MouseEvent e) {
				// System.out.println(eventList.getComponentAt(e.getPoint()));

			}

		});

		eventList.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				if (eventList.getSelectedIndex() != -1) {
					eventSelected(eventList.getSelectedIndex());
				}

			}

		});

		eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane eventScrollPane = new JScrollPane(eventList);

		changeListener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				eventListChanged();
			}
		};

		JPanel listPanel = new JPanel(new BorderLayout());

		listPanel.setBorder(new TitledBorder("Logged events:"));

		listPanel.add(eventScrollPane, BorderLayout.CENTER);

		eventListPanel.add(listPanel, BorderLayout.CENTER);

		// Andra kontroll info kommponenter till listan

		JPanel controllInfoPanel = new JPanel();

		controllInfoPanel.setLayout(new BoxLayout(controllInfoPanel,
				BoxLayout.Y_AXIS));

		JPanel infoPanel = new JPanel();

		infoPanel.add(new JLabel("Total time:"));

		totalTimeLabel = new JLabel();

		infoPanel.add(totalTimeLabel);

		controllInfoPanel.add(infoPanel);

		JPanel controllPanel = new JPanel();

		JButton addButton = new JButton("Add");

		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				addEvent();
			}

		});

		JButton deleteButton = new JButton("Delete");

		deleteButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				deleteEvent();
			}

		});

		controllPanel.add(addButton);

		controllPanel.add(deleteButton);

		controllInfoPanel.add(controllPanel);

		eventListPanel.add(controllInfoPanel, BorderLayout.PAGE_END);

		eventListPanel.setVisible(false);

		return eventListPanel;

	}

	private void addMenu() {
		JMenuBar menuBar = new JMenuBar();

		// File menyn
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');

		JMenuItem newItem = new JMenuItem("New");
		fileMenu.add(newItem);
		newItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				createNew();
			}

		});

		JMenuItem openItem = new JMenuItem("Open...");
		fileMenu.add(openItem);
		openItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				open();
			}

		});

		fileMenu.add(new JSeparator());

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.setMnemonic('S');
		fileMenu.add(saveItem);
		saveItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				save(false);
			}

		});

		JMenuItem saveAsItem = new JMenuItem("Save As...");
		fileMenu.add(saveAsItem);
		saveAsItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				saveAs(false);
			}

		});

		fileMenu.add(new JSeparator());

		JMenu exportMenu = new JMenu("Export");
		fileMenu.add(exportMenu);

		JMenuItem htmlItem = new JMenuItem("Export to html...");
		htmlItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				exportToHTML();

			}

		});

		exportMenu.add(htmlItem);

		fileMenu.add(new JSeparator());

		JMenuItem exitItem = new JMenuItem("Exit And Save");
		exitItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				saveWindowProperies();
				save(true);
				

			}

		});
		exitItem.setMnemonic('x');
		fileMenu.add(exitItem);

		menuBar.add(fileMenu);
		// Slut file menyn

		// Properties menyn
		JMenu propertiesMenu = new JMenu("Properties");
		propertiesMenu.setMnemonic('P');

		menuBar.add(propertiesMenu);

		JMenu orientationMenu = new JMenu("Component Orientation");
		propertiesMenu.add(orientationMenu);

		// Create the radio buttons.
		JRadioButtonMenuItem verticalOrientation = new JRadioButtonMenuItem(
				"Vertical Orientation");
		orientationMenu.add(verticalOrientation);
		verticalOrientation.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

			}

		});

		JRadioButtonMenuItem horizontalOrientation = new JRadioButtonMenuItem(
				"Horizontal Orientation");
		horizontalOrientation.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

			}

		});
		orientationMenu.add(horizontalOrientation);

		String compOrientationProp = mainProps
				.getProperty("component_orientation");

		if (new Integer(mainProps.getProperty("component_orientation")) == JSplitPane.VERTICAL_SPLIT)
			verticalOrientation.setSelected(true);
		else
			horizontalOrientation.setSelected(true);

		// Group the radio buttons.
		ButtonGroup orientationGroup = new ButtonGroup();
		orientationGroup.add(verticalOrientation);
		orientationGroup.add(horizontalOrientation);

		JMenu firstComponentMenu = new JMenu("First Component");
		propertiesMenu.add(firstComponentMenu);

		// Create the radio buttons.

		JRadioButtonMenuItem editorOption = new JRadioButtonMenuItem("Editor");
		firstComponentMenu.add(editorOption);
		editorOption.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				splitPane.setLeftComponent(null);
				splitPane.setRightComponent(null);				
				splitPane.setLeftComponent(editor);
				splitPane.setRightComponent(eventListPanel);
		
			}

		});

		JRadioButtonMenuItem eventListOption = new JRadioButtonMenuItem(
				"Event List");
		eventListOption.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				splitPane.setLeftComponent(null);
				splitPane.setRightComponent(null);
				splitPane.setLeftComponent(eventListPanel);
				splitPane.setRightComponent(editor);

			}

		});
		firstComponentMenu.add(eventListOption);

		if (mainProps.getProperty("first_component").equals("list"))
			eventListOption.setSelected(true);
		else
			editorOption.setSelected(true);

		ButtonGroup firstComponentGroup = new ButtonGroup();

		firstComponentGroup.add(editorOption);
		firstComponentGroup.add(eventListOption);

		JMenu lookAndFeelMenu = new JMenu("Look And Feel");
		propertiesMenu.add(lookAndFeelMenu);

		ButtonGroup lookAndFeelGroup = new ButtonGroup();

		// Create the radio buttons.

		final LookAndFeelInfo[] lookAndFeels = UIManager
				.getInstalledLookAndFeels();

		for (int n = 0; n < lookAndFeels.length; n++) {

			JRadioButtonMenuItem lookAndFeelItem = new JRadioButtonMenuItem(
					lookAndFeels[n].getName());
			lookAndFeelMenu.add(lookAndFeelItem);
			ThemeChangeListener l = new ThemeChangeListener(lookAndFeels[n]
					.getClassName(), this);
			lookAndFeelItem.addActionListener(l);

			if (lookAndFeels[n].getClassName().equals(
					mainProps.getProperty("theme"))) {
				lookAndFeelItem.setSelected(true);
				themeChanger = l;
			}

			lookAndFeelGroup.add(lookAndFeelItem);

		}

		// Register a listener for the radio buttons.

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');

		JMenuItem helpItem = new JMenuItem("Help...");

		helpItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				HelpDialog dialog = new HelpDialog();
				dialog.setVisible(true);

			}

		});
		helpMenu.add(helpItem);

		JMenuItem aboutItem = new JMenuItem("About...");

		aboutItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								thisPane,
								"Project Diary 1.0 - A project diary program\n\nAuthor: Kjell Winblad \nContact: kjellw@cs.umu.se\nHome page: www.cs.umu.se/~kjellw\n\nLicense: GNU GPL (General Public License Version 2)\nwww.gnu.org/copyleft/gpl.html",
								"About", JOptionPane.INFORMATION_MESSAGE,
								new ImageIcon(Toolkit.getDefaultToolkit()
										.getImage(
												this.getClass().getResource(
														"icon.jpg"))));

			}

		});

		helpMenu.add(aboutItem);

		menuBar.add(helpMenu);
		// Slut properties menyn

		setJMenuBar(menuBar);
		// Slut p� menyn

	}

	private void exportToHTML() {

		new Thread(new Runnable(){

			public void run() {
				final JFileChooser fileChooser = getFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(thisPane)) {
					File exportFile;

					if (null != (exportFile = fileChooser.getSelectedFile())) {
						try {
							DiaryFileFormatHelper.exportToHTML(exportFile, list);
							JOptionPane.showMessageDialog(thisPane,
									"Exported successful to " + exportFile);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(thisPane,
									"Could not  export to file " + exportFile);
						}
					}
				}
			}
		});

				
			}
			
		}).start();

	}

	private void open() {
		saveCurrentQuestion();

		new Thread(new Runnable(){
			
			public void run(){
		final JFileChooser fileChooser = getFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

		

		SwingUtilities.invokeLater( new Runnable(){
			public void run(){
				File openFile;
				if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(thisPane)) {

					if (null != (openFile = fileChooser.getSelectedFile())) {

						if (openFile.canRead())
							openFile(openFile);
						else
							JOptionPane.showMessageDialog(thisPane, "Could not read file "
									+ openFile);

					}

				}
				
			}
		});

			
			}}).start();

	}

	private void openFile(File openFile) {
		try {
			list = DiaryFileFormatHelper.getFromFile(openFile);
			changed = false;
			eventListChanged();

			eventSelected(list.size() - 1);
			eventListPanel.setVisible(true);
			saveFile = openFile;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Could not open file: "
					+ e.getMessage());
		}

	}

	private void saveCurrentQuestion() {
		if (list != null) {
			int option = JOptionPane.showConfirmDialog(this, "Save changes?");
			if (option == JOptionPane.YES_OPTION) {
				editor.save();
				save(false);
			} else if (option == JOptionPane.CANCEL_OPTION) {
				return;
			}

		}
	}

	private void createNew() {
		saveCurrentQuestion();

		saveFile = null;
		eventListPanel.setVisible(true);
		list = new LinkedList<DiaryEvent>();
		changed = false;

		addEvent();
		eventListChanged();
	}

	private void save(boolean exit) {
		
		if (saveFile == null) {
			
			saveAs(exit);
			return;
		} else {
			// Kod f�r att spara
			try {
				if (editor != null)
					editor.save();
				if (list.size() == 0) {
					JOptionPane.showMessageDialog(this,
							"You need at least one element to save");
					return;
				}

				DiaryFileFormatHelper.saveToFile(list, saveFile);
				changed = false;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Coul'd not save to file "
						+ saveFile);
			}

		}

		if(exit)
			System.exit(0);
		
	}

	private void saveAs(final boolean exit) {

		// M�ste hitta en fil att spara till...
		new Thread(new Runnable(){

			public void run() {

				final JFileChooser fileChooser = getFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				
				if (saveFile != null)
					fileChooser.setSelectedFile(saveFile);

				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						
						if (JFileChooser.APPROVE_OPTION == fileChooser.showSaveDialog(thisPane)) {

							if (null != (saveFile = fileChooser.getSelectedFile())) {
								save(exit);
							}
						}
					}
				});
				
			}
			
		}).start();
		




	}

	private void addEvent() {
		DiaryEvent event = new DiaryEvent();

		if (editor == null) {
			editor = new DiaryEventEditor(event, changeListener);

			if (lastSplitPos != null)
				splitPane.setDividerLocation(this.lastSplitPos);
		} else {
			editor.setEvent(event);
		}

		list.add(event);

		eventListChanged();

		eventList.setSelectedIndex(list.size() - 1);

	}

	private boolean noRec = false;

	private void eventSelected(int index) {
		if (noRec) {
			noRec = false;
			return;

		}

		DiaryEvent event = list.get(index);
		// System.out.println("HEJ");
		if (editor == null) {
			editor = new DiaryEventEditor(event, changeListener);

			if (splitPane != null)
				splitPane.setDividerLocation(lastSplitPos);
		} else {
			editor.setEvent(event);
		}
		noRec = true;

		eventList.setSelectedIndex(index);

	}

	private void deleteEvent() {

		if (eventList.getSelectedIndex() != -1) {
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this,
					"Do you want to delete item?")) {

				list.remove(eventList.getSelectedIndex());
				eventListChanged();
				if (list.size() != 0)
					eventList.setSelectedIndex(list.size() - 1);
			}

		}

	}

	/**
	 * Sorterar och uppdaerar listan med h�ndelser
	 * 
	 */
	private void eventListChanged() {

		DiaryEvent eventBefore = (DiaryEvent) eventList.getSelectedValue();
		Collections.sort(list);
		long total = 0;

		for (DiaryEvent e : list) {
			total += e.getElementTimeHouers() * 60 + e.getElementTimeMinutes();
		}
		changed = true;
		totalTimeLabel.setText((new Long(total / 60)).toString() + " h "
				+ (new Long(total % 60)).toString() + " m");

		eventList.setListData(list.toArray());

		int indexNow = list.indexOf(eventBefore);

		if (list.size() > indexNow && indexNow != -1) {
			noRec = true;
			eventList.setSelectedIndex(indexNow);
		}

	}

}
