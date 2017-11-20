package vn.edu.hcmuaf.idm;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
/**
 * This is the main application class
 * 
 * @author Shady
 *
 */
public class Application extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;

	// Member variables
	private DownloadTableModel tableModel;
	private Downloader selectedDownloader;
	private boolean isClearing;

	// Swing components
	private JMenuBar jMenuBar;
	private JMenu jmnTaskMenu;
	private JMenu jmnDownloadMenu;
	private JMenu jmnHelpMenu;
	private JMenuItem jmiTaskAddNewDownloadItem;
	private JMenuItem jmiTaskExitItem;
	private JMenuItem jmiDownloadResumeItem;
	private JMenuItem jmiDownloadPauseItem;
	private JMenuItem jmiDownloadCancelItem;
	private JMenuItem jmiDownloadRemoveItem;
	private JMenuItem jmiDownloadOptionItem;
	private JMenuItem jmiHelpAboutItem;
	
	private JPanel mainPanel;
	private JPanel subPanel;
	private JDialog jDialog;
	
	private JButton jbnMainAdd;
	private JButton jbnMainCancel;
	private JButton jbnMainPause;
	private JButton jbnMainRemove;
	private JButton jbnMainResume;
	private JButton jbnMainOption;
	
	private JScrollPane jspMainDownloadList;
	private JTable jtbMainDownloadList;
	
	private JLabel jlbTaskAddURL;
	private JTextField jtxTaskAddURL;
	private JButton jbnTaskAddURL;
	private JLabel jlbOptionConnections;
	private JComboBox<Integer> jcbOptionConnections;
	private JLabel jlbOptionOutputFolder;
	private JTextField jtxOptionOutputFolder;
	private JButton jbnOptionOutputFolderChoose;
	private JFileChooser jfcOptionOutputFolderChoose;
	private JButton jbnOptionSave;
	private final Integer[] connectionsValue = {2, 4, 8, 16, 32};
	private JLabel jlbHelpAboutImg;
	private JLabel jlbHelpAboutVersion;
	private JLabel jlbHelpAboutInfo;

	// Icons
	private final ImageIcon idmIcon = new ImageIcon(this.getClass().getResource("/idm_big.png"));
	private final ImageIcon idmAddBtn = new ImageIcon(this.getClass().getResource("/add.png"));
	private final ImageIcon idmCancelBtn = new ImageIcon(this.getClass().getResource("/cancel.png"));
	private final ImageIcon idmPauseBtn = new ImageIcon(this.getClass().getResource("/pause.png"));
	private final ImageIcon idmRemoveBtn = new ImageIcon(this.getClass().getResource("/remove.png"));
	private final ImageIcon idmResumeBtn = new ImageIcon(this.getClass().getResource("/resume.png"));
	private final ImageIcon idmOptionBtn = new ImageIcon(this.getClass().getResource("/option.png"));
	
	/**
	 * Default main method
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// System debugs
			System.err.println("ERROR: Can not set LookAndFeel for UIManager");
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Application().setVisible(true);
			}
		});
	}

	/**
	 * Main Application
	 */
	public Application() {
		initComponents();
		initialize();
	}

	/**
	 * Init Swing components
	 */
	private void initComponents() {
		setTitle("Internet Download Manager");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(800, 500));
		setIconImage(idmIcon.getImage());

		buildMenubar();
		buildMainWindow();
		updateControlButtons(); // Update button and menu state

		pack();
		setLocationRelativeTo(null);// Center the window
	}
	
	/**
	 * Build main menu
	 */
	private void buildMenubar() {
		jMenuBar = new JMenuBar();

		// Task menu ------------------------
		jmnTaskMenu = new JMenu("Tasks");
		
		jmiTaskAddNewDownloadItem = new JMenuItem("Add new download", KeyEvent.VK_N);
		jmiTaskAddNewDownloadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				taskAddNewDownload(evt);
			}
		});
		
		jmiTaskExitItem = new JMenuItem("Exit", KeyEvent.VK_X);
		jmiTaskExitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				taskExit(evt);
			}
		});
		
		jmnTaskMenu.add(jmiTaskAddNewDownloadItem);
		jmnTaskMenu.addSeparator();
		jmnTaskMenu.add(jmiTaskExitItem);
		
		// Download menu ------------------------
		jmnDownloadMenu = new JMenu("Downloads");
		
		jmiDownloadResumeItem = new JMenuItem("Resume", KeyEvent.VK_R);
		jmiDownloadResumeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				downloadResume(evt);
			}
		});
		jmiDownloadPauseItem = new JMenuItem("Pause", KeyEvent.VK_P);
		jmiDownloadPauseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				downloadPause(evt);
			}
		});
		jmiDownloadCancelItem = new JMenuItem("Cancel", KeyEvent.VK_C);
		jmiDownloadCancelItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				downloadCancel(evt);
			}
		});
		jmiDownloadRemoveItem = new JMenuItem("Remove", KeyEvent.VK_M);
		jmiDownloadRemoveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				downloadRemove(evt);
			}
		});
		jmiDownloadOptionItem = new JMenuItem("Options", KeyEvent.VK_O);
		jmiDownloadOptionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				downloadOption(evt);
			}
		});
		
		jmnDownloadMenu.add(jmiDownloadResumeItem);
		jmnDownloadMenu.add(jmiDownloadPauseItem);
		jmnDownloadMenu.add(jmiDownloadCancelItem);
		jmnDownloadMenu.add(jmiDownloadRemoveItem);
		jmnDownloadMenu.addSeparator();
		jmnDownloadMenu.add(jmiDownloadOptionItem);
		
		// Help menu ------------------------
		jmnHelpMenu = new JMenu("Help");
		jmiHelpAboutItem = new JMenuItem("About IDM", KeyEvent.VK_B);
		jmiHelpAboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				helpAbout(evt);
			}
		});

		jmnHelpMenu.add(jmiHelpAboutItem);
		
		// Add all menu items to menubar  ------------------------
		jMenuBar.add(jmnTaskMenu);
		jMenuBar.add(jmnDownloadMenu);
		jMenuBar.add(jmnHelpMenu);
		setJMenuBar(jMenuBar);
		
	}
	
	/**
	 * Build main window
	 */
	private void buildMainWindow() {

		// Add button
		jbnMainAdd = new JButton("Add URL");
		jbnMainAdd.setIcon(idmAddBtn);
		jbnMainAdd.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainAdd.setHorizontalTextPosition(SwingConstants.CENTER);
		jbnMainAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				taskAddNewDownload(evt);
			}
		});

		// Resume button
		jbnMainResume = new JButton("Resume");
		jbnMainResume.setIcon(idmResumeBtn);
		jbnMainResume.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainResume.setHorizontalTextPosition(SwingConstants.CENTER);
		jbnMainResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				downloadResume(evt);
			}
		});
		
		// Pause button
		jbnMainPause = new JButton("Pause");
		jbnMainPause.setIcon(idmPauseBtn);
		jbnMainPause.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainPause.setHorizontalTextPosition(SwingConstants.CENTER);
		jbnMainPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				downloadPause(evt);
			}
		});
		
		// Cancel button
		jbnMainCancel = new JButton("Cancel");
		jbnMainCancel.setIcon(idmCancelBtn);
		jbnMainCancel.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainCancel.setHorizontalTextPosition(SwingConstants.CENTER);
		jbnMainCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				downloadCancel(evt);
			}
		});
		
		// Remove button
		jbnMainRemove = new JButton("Remove");
		jbnMainRemove.setIcon(idmRemoveBtn);
		jbnMainRemove.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainRemove.setHorizontalTextPosition(SwingConstants.CENTER);
		jbnMainRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				downloadRemove(evt);
			}
		});
		
		// Option button
		jbnMainOption = new JButton("Options");
		jbnMainOption.setIcon(idmOptionBtn);
		jbnMainOption.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainOption.setHorizontalTextPosition(SwingConstants.CENTER);
		jbnMainOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				downloadOption(evt);
			}
		});

		// Download list
		jspMainDownloadList = new JScrollPane();
		jtbMainDownloadList = new JTable();
		
		// Map data
		tableModel = new DownloadTableModel();
		jtbMainDownloadList.setModel(tableModel);
		jspMainDownloadList.setViewportView(jtbMainDownloadList);
		
		// A little tweak
		jspMainDownloadList.getViewport().setBackground(new Color(255, 255, 255));
		jtbMainDownloadList.setGridColor(new Color(240, 240, 240));
		
		// Arrange elements by using MigLayout
		mainPanel = new JPanel(new MigLayout("fill"));
		mainPanel.add(jbnMainAdd);
		mainPanel.add(jbnMainResume);
		mainPanel.add(jbnMainPause);
		mainPanel.add(jbnMainCancel);
		mainPanel.add(jbnMainRemove);
		mainPanel.add(jbnMainOption, "wrap");
		mainPanel.add(jspMainDownloadList, "span, width 100%, height 100%");
		
		add(mainPanel);
		
	}

	/**
	 * Init the data in download table
	 */
	private void initialize() {
		// Set up JTable
		jtbMainDownloadList.getSelectionModel().addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					tableChangeDownloader();
				}
			});

		// Only 1 row is selected at a time
		jtbMainDownloadList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Init the progress bar
		RendererProgressBar progressBar = new RendererProgressBar(1, 100);
		progressBar.setStringPainted(true);
		jtbMainDownloadList.setDefaultRenderer(JProgressBar.class, progressBar);
		
	}

	/**
	 * Action Event -----------------------------------------
	 */
	
	/**
	 * Add new URL window
	 */
	private void taskAddNewDownload(ActionEvent evt) {
		jDialog = new JDialog(this, "Enter new address to download", Dialog.ModalityType.DOCUMENT_MODAL);
		jDialog.setPreferredSize(new Dimension(600, 70));
		jDialog.setIconImage(idmIcon.getImage());
		jDialog.setResizable(false);
		
		jlbTaskAddURL = new JLabel("Address:");
		jtxTaskAddURL = new JTextField("");
		jbnTaskAddURL = new JButton("OK");
		jbnTaskAddURL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				taskAddNewDownloadURL(evt);
			}
		});
		
		// Arrange elements by using MigLayout
		subPanel = new JPanel(new MigLayout("fill"));
		subPanel.add(jlbTaskAddURL);
		subPanel.add(jtxTaskAddURL, "width 100%");
		subPanel.add(jbnTaskAddURL);
		
		jDialog.add(subPanel);
		
		jDialog.getRootPane().setDefaultButton(jbnTaskAddURL);
		jDialog.pack();
		jDialog.setLocationRelativeTo(this);
		jDialog.setVisible(true);
    }
	
	private void taskAddNewDownloadURL(ActionEvent evt) {
		URL verifiedUrl = DownloadManager.verifyURL(jtxTaskAddURL.getText());
		
		if (verifiedUrl != null) {
			Downloader download = DownloadManager.getInstance()
					.createDownload(verifiedUrl, DownloadManager.getInstance().getOutputFolder());
			
			tableModel.addNewDownload(download);
			jtxTaskAddURL.setText(""); // reset text field
			jDialog.dispose(); // close dialog
			
		} else {
			JOptionPane.showMessageDialog(this, 
					"Invalid Download URL. Please note that IDM support only HTTP protocol at this time.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void taskExit(ActionEvent evt) {
		dispose();
    }
	
	private void downloadPause(ActionEvent evt) {
		selectedDownloader.pause();
		updateControlButtons();
	}

	private void downloadResume(ActionEvent evt) {
		selectedDownloader.resume();
		updateControlButtons();
	}

	private void downloadCancel(ActionEvent evt) {
		selectedDownloader.cancel();
		updateControlButtons();
	}

	private void downloadRemove(ActionEvent evt) {
		isClearing = true;
		int index = jtbMainDownloadList.getSelectedRow();
		DownloadManager.getInstance().removeDownload(index);
		tableModel.clearDownload(index);
		isClearing = false;
		selectedDownloader = null;
		updateControlButtons();
	}
	
	/**
	 * Download options window
	 */
	private void downloadOption(ActionEvent evt) {
		jDialog = new JDialog(this, "Internet Download Manager Options", Dialog.ModalityType.DOCUMENT_MODAL);
		jDialog.setPreferredSize(new Dimension(400, 100));
		jDialog.setIconImage(idmIcon.getImage());
		jDialog.setResizable(false);
		
		// Connections
		jlbOptionConnections = new JLabel("Max connection:");
		jcbOptionConnections = new JComboBox<Integer>(connectionsValue);
		jcbOptionConnections.setSelectedItem(DownloadManager.getInstance().getConnectionNumber());
		
		// Choose save location
		jlbOptionOutputFolder = new JLabel("Save location:");
		jtxOptionOutputFolder = new JTextField(DownloadManager.getInstance().getOutputFolder(), 25);
		jtxOptionOutputFolder.setEditable(false); // prevent handy editing
		jbnOptionOutputFolderChoose = new JButton("Browse");
		jbnOptionOutputFolderChoose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				downloadOptionSelectFolder(evt);
			}
		});
		
		// Save
		jbnOptionSave = new JButton("Save");
		jbnOptionSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				downloadOptionSave(evt);
				jDialog.dispose(); // close dialog
			}
		});
		
		// Arrange elements by using MigLayout
		subPanel = new JPanel(new MigLayout("fill"));
		subPanel.add(jlbOptionConnections, "align right");
		subPanel.add(jcbOptionConnections);
		subPanel.add(jbnOptionSave, "wrap, growx");
		subPanel.add(jlbOptionOutputFolder, "align right");
		subPanel.add(jtxOptionOutputFolder);
		subPanel.add(jbnOptionOutputFolderChoose, "wrap, growx");
		
		jDialog.add(subPanel);
		
		jDialog.pack();
		jDialog.setLocationRelativeTo(this);
		jDialog.setVisible(true);
    }
	
	private void downloadOptionSelectFolder(ActionEvent evt) {
		jfcOptionOutputFolderChoose = new JFileChooser();
		jfcOptionOutputFolderChoose.setDialogTitle("Choose Save Location");
		jfcOptionOutputFolderChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfcOptionOutputFolderChoose.setCurrentDirectory(new File(DownloadManager.getInstance().getOutputFolder()));
		jfcOptionOutputFolderChoose.setAcceptAllFileFilterUsed(false);
		
		// Set temporary info for displaying
		if (jfcOptionOutputFolderChoose.showOpenDialog(jDialog) == JFileChooser.APPROVE_OPTION) {
			jtxOptionOutputFolder.setText(jfcOptionOutputFolderChoose.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void downloadOptionSave(ActionEvent evt) {
		// Save info to DownloadManager
		try {
			DownloadManager.getInstance().setConnectionNumber((int) jcbOptionConnections.getSelectedItem());
			DownloadManager.getInstance().setOutputFolder(jfcOptionOutputFolderChoose.getSelectedFile().getAbsolutePath());
			
		} catch (Exception e) {
			// Do nothing when this button hits java.lang.NullPointerException when jfcOptionOutputFolderChoose's path is null

			// System debugs
			System.err.println("WARNING: No Folder was chosen. OutputFolder left as default");
		}
		
		// System logs
		System.out.println("Saved: connections=" +  DownloadManager.getInstance().getConnectionNumber() +
				" | outputFolder=" + DownloadManager.getInstance().getOutputFolder());
			
	}
	
	/**
	 * Help about window
	 */
	private void helpAbout(ActionEvent evt) {
		jDialog = new JDialog(this, "About Internet Download Manager", Dialog.ModalityType.DOCUMENT_MODAL);
		jDialog.setPreferredSize(new Dimension(420, 200));
		jDialog.setIconImage(idmIcon.getImage());
		jDialog.setResizable(false);
		
		jlbHelpAboutImg = new JLabel();
		jlbHelpAboutImg.setIcon(new ImageIcon("asset/about.png"));
		
		jlbHelpAboutVersion = new JLabel("Version 0.1 alpha");
		jlbHelpAboutInfo = new JLabel("<html><div style='text-align:center'>This program was designed and programmed by <b>GroupNine</b>.<br />" +
				"Software Engineering: Specialized Project - Spring 2015 - Nong Lam University.<br />" +
				"Instructor: <b>Prof. Pham Van Tinh PhD.</b></div></html>");
		
		// Arrange elements by using MigLayout
		subPanel = new JPanel(new MigLayout("fill"));
		subPanel.add(jlbHelpAboutImg, "wrap");
		subPanel.add(jlbHelpAboutVersion, "wrap, align center");
		subPanel.add(jlbHelpAboutInfo, "height 100%, align center");
		
		jDialog.add(subPanel);
		
		jDialog.pack();
		jDialog.setLocationRelativeTo(this);
		jDialog.setVisible(true);
    }
	
	/**
	 * System functions
	 */

	private void tableChangeDownloader() {
		// Unregister old downloader
		if (selectedDownloader != null)
			selectedDownloader.deleteObserver(Application.this);

		// If downloader is not in delete progress, register this class to be its observer
		if (!isClearing) {
			int index = jtbMainDownloadList.getSelectedRow();
			if (index != -1) {
				selectedDownloader = DownloadManager.getInstance().getDownload(jtbMainDownloadList.getSelectedRow());
				selectedDownloader.addObserver(Application.this);
			} else {
				selectedDownloader = null;
			}
				
			updateControlButtons();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// Update buttons if the selected downloader changed
		if (selectedDownloader != null && selectedDownloader.equals(o))
			updateControlButtons();
	}

	/**
	 * Update the status of control buttons and menus
	 */
	private void updateControlButtons() {
		if (selectedDownloader != null) {
			int state = selectedDownloader.getState();
			switch (state) {
			case Downloader.DOWNLOADING:
				jbnMainResume.setEnabled(false);
				jbnMainPause.setEnabled(true);
				jbnMainCancel.setEnabled(true);
				jbnMainRemove.setEnabled(false);
				jmiDownloadResumeItem.setEnabled(false);
				jmiDownloadPauseItem.setEnabled(true);
				jmiDownloadCancelItem.setEnabled(true);
				jmiDownloadRemoveItem.setEnabled(false);
				break;
			case Downloader.PAUSED:
				jbnMainResume.setEnabled(true);
				jbnMainPause.setEnabled(false);
				jbnMainCancel.setEnabled(true);
				jbnMainRemove.setEnabled(false);
				jmiDownloadResumeItem.setEnabled(true);
				jmiDownloadPauseItem.setEnabled(false);
				jmiDownloadCancelItem.setEnabled(true);
				jmiDownloadRemoveItem.setEnabled(false);
				break;
			case Downloader.ERROR:
				jbnMainResume.setEnabled(true);
				jbnMainPause.setEnabled(false);
				jbnMainCancel.setEnabled(false);
				jbnMainRemove.setEnabled(true);
				jmiDownloadResumeItem.setEnabled(true);
				jmiDownloadPauseItem.setEnabled(false);
				jmiDownloadCancelItem.setEnabled(false);
				jmiDownloadRemoveItem.setEnabled(true);
				break;
			default:
				// CANCELLED & COMPLETED
				jbnMainResume.setEnabled(false);
				jbnMainPause.setEnabled(false);
				jbnMainCancel.setEnabled(false);
				jbnMainRemove.setEnabled(true);
				jmiDownloadResumeItem.setEnabled(false);
				jmiDownloadPauseItem.setEnabled(false);
				jmiDownloadCancelItem.setEnabled(false);
				jmiDownloadRemoveItem.setEnabled(true);
			}
		} else {
			// No download is selected in JTable
			jbnMainResume.setEnabled(false);
			jbnMainPause.setEnabled(false);
			jbnMainCancel.setEnabled(false);
			jbnMainRemove.setEnabled(false);
			jmiDownloadResumeItem.setEnabled(false);
			jmiDownloadPauseItem.setEnabled(false);
			jmiDownloadCancelItem.setEnabled(false);
			jmiDownloadRemoveItem.setEnabled(false);
		}
	}

}
