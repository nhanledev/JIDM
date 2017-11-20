package vn.edu.hcmuaf.idm.test.layout;

/**
 * This class is used to design & test UI
 * before copying to the main app
 * 
 * To team: MigLayout whitepaper: http://www.miglayout.com/whitepaper.html
 * 
 * Revision 4.0 - Delete GroupLayout, use MigLayout instead because of its easiness
 * 
 * author: Nhan Le
 */
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import vn.edu.hcmuaf.idm.DownloadManager;
import vn.edu.hcmuaf.idm.DownloadTableModel;

public class LayoutTest extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// Member variables
	private DownloadTableModel tableModel;
	
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
	private final ImageIcon idmIcon = new ImageIcon("asset/idm_big.png");
	private final ImageIcon idmAddBtn = new ImageIcon("asset/add.png");
	private final ImageIcon idmCancelBtn = new ImageIcon("asset/cancel.png");
	private final ImageIcon idmPauseBtn = new ImageIcon("asset/pause.png");
	private final ImageIcon idmRemoveBtn = new ImageIcon("asset/remove.png");
	private final ImageIcon idmResumeBtn = new ImageIcon("asset/resume.png");
	private final ImageIcon idmOptionBtn = new ImageIcon("asset/option.png");
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new LayoutTest().setVisible(true);
			}
		});
	}
	
	public LayoutTest() {
		MainLayout();
	}

	/**
	 * Main Layout (menu, button, downloadlist)
	 * @return 
	 */
	public void MainLayout() {
		setTitle("Internet Download Manager");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(800, 500));
		setIconImage(idmIcon.getImage());
		
		buildMenubar();
		buildMainWindow();
		
		pack();
		setLocationRelativeTo(null);// Center the window
	}
	
	/**
	 * Menubar
	 */
	private void buildMenubar() {
		jMenuBar = new JMenuBar();

		// Task menu ------------------------
		jmnTaskMenu = new JMenu("Tasks");
		
		jmiTaskAddNewDownloadItem = new JMenuItem("Add new download", KeyEvent.VK_N);
		jmiTaskAddNewDownloadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Debug
				System.out.println("Event: Tasks - Add new download");
				taskAddNewDownload(evt);
			}
		});
		
		jmiTaskExitItem = new JMenuItem("Exit", KeyEvent.VK_X);
		jmiTaskExitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Debug
				System.out.println("Event: Tasks - Exit");
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
				// Debug
				System.out.println("Event: Downloads - Resume");
			}
		});
		jmiDownloadPauseItem = new JMenuItem("Pause", KeyEvent.VK_P);
		jmiDownloadPauseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Debug
				System.out.println("Event: Downloads - Pause");
			}
		});
		jmiDownloadCancelItem = new JMenuItem("Cancel", KeyEvent.VK_C);
		jmiDownloadCancelItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Debug
				System.out.println("Event: Downloads - Cancel");
			}
		});
		jmiDownloadRemoveItem = new JMenuItem("Remove", KeyEvent.VK_M);
		jmiDownloadRemoveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Debug
				System.out.println("Event: Downloads - Remove");
			}
		});
		jmiDownloadOptionItem = new JMenuItem("Options", KeyEvent.VK_O);
		jmiDownloadOptionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// Debug
				System.out.println("Event: Downloads - Options");
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
				// Debug
				System.out.println("Event: Help - About IDM");
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

		// Resume button
		jbnMainResume = new JButton("Resume");
		jbnMainResume.setIcon(idmResumeBtn);
		jbnMainResume.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainResume.setHorizontalTextPosition(SwingConstants.CENTER);
		
		// Pause button
		jbnMainPause = new JButton("Pause");
		jbnMainPause.setIcon(idmPauseBtn);
		jbnMainPause.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainPause.setHorizontalTextPosition(SwingConstants.CENTER);
		
		// Cancel button
		jbnMainCancel = new JButton("Cancel");
		jbnMainCancel.setIcon(idmCancelBtn);
		jbnMainCancel.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainCancel.setHorizontalTextPosition(SwingConstants.CENTER);
		
		// Remove button
		jbnMainRemove = new JButton("Remove");
		jbnMainRemove.setIcon(idmRemoveBtn);
		jbnMainRemove.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainRemove.setHorizontalTextPosition(SwingConstants.CENTER);
		
		// Option button
		jbnMainOption = new JButton("Options");
		jbnMainOption.setIcon(idmOptionBtn);
		jbnMainOption.setVerticalTextPosition(SwingConstants.BOTTOM);
		jbnMainOption.setHorizontalTextPosition(SwingConstants.CENTER);

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
		jtxOptionOutputFolder = new JTextField(new File(DownloadManager.getInstance().getOutputFolder()).getAbsolutePath(), 25);
		jbnOptionOutputFolderChoose = new JButton("Browse");
		
		// Save
		jbnOptionSave = new JButton("Save");
		
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
	
	/**
	 * Help about window
	 */
	private void helpAbout(ActionEvent evt) {
		jDialog = new JDialog(this, "Enter new address to download", Dialog.ModalityType.DOCUMENT_MODAL);
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
}
