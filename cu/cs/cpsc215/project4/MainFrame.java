package cu.cs.cpsc215.project4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//creates the main window of the application
public class MainFrame extends JFrame implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private JMenuBar menu;
	private int width, height;
	private DataStore data;
	private JTable table;
	private JButton add, edit, delete, retrieve;
	private ImageIcon icon;
	
	public MainFrame(){
		super("My Email Client");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		width = 750;
		height = 400;
		this.setSize(new Dimension(width,height));
		
		Container pane = this.getContentPane();
		pane.setLayout(new BorderLayout());
		
		//add custom icon to window
		icon = new ImageIcon("./icon.png");
		this.setIconImage(icon.getImage());
		
		//add menubar to top of window
		menu = new JMenuBar();
		
		JMenu file, config, help;
		
		//create the three menus in the menubar
		file = new JMenu("File");
		config = new JMenu("Configuration");
		help = new JMenu("Help");
		
		JMenuItem exit, configure, about;
		
		//if click exit, exit program
		exit = new JMenuItem("Exit");
		exit.setBackground(new Color(0x522D80));
		exit.setForeground(Color.WHITE);
		exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					//when window is closing, save config and contacts
					data.storeData();
				} catch (IOException e) {
				}
				System.exit(0);
			}
		});
		
		//if click configure, change configuration settings
		configure = new JMenuItem("Configure");
		configure.setBackground(new Color(0x522D80));
		configure.setForeground(Color.WHITE);
		configure.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigurationDlg dlg = new ConfigurationDlg(new JDialog());
				dlg.setVisible(true);
			}
		});
		
		//if click about, show application information
		about = new JMenuItem("About");
		about.setBackground(new Color(0x522D80));
		about.setForeground(Color.WHITE);
		about.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				SystemInformationDlg dlg = new SystemInformationDlg(new JDialog());
				dlg.setVisible(true);
			}
		});
		
		//add menus to the menubar
		file.add(exit);
		file.setForeground(Color.WHITE);
		config.add(configure);
		config.setForeground(Color.WHITE);
		help.add(about);
		help.setForeground(Color.WHITE);
		
		menu.add(file);
		menu.add(config);
		menu.add(help);
		menu.setSize(new Dimension(width, 30));
		menu.setBackground(new Color(0x522D80));

		data = DataStore.getInstance();
		data.getContacts();
		
		//create a new table based on the DataStore instance and name columns
		table = new JTable(data);
		table.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("Name");
		table.getTableHeader().getColumnModel().getColumn(1).setHeaderValue("Postal Address");
		table.getTableHeader().getColumnModel().getColumn(2).setHeaderValue("Email Address");
		table.getTableHeader().getColumnModel().getColumn(3).setHeaderValue("Phone Number");
		table.setGridColor(Color.BLACK);
		
		//make table scrollable
		JScrollPane scrollTbl = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		
		//if double click row, open emailtransmissiondlg to send to selected address
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
			         JTable target = (JTable)e.getSource();
			         int row = target.getSelectedRow();
			         Contact receiver = data.getContacts().get(row);
			         EmailTransmissionDlg dlg = new EmailTransmissionDlg(new JDialog(), receiver);
			 		 dlg.setVisible(true);
				}
			}
		});
		
		//makes it so the edit and delete buttons are only enabled when a row is selected
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	            edit.setEnabled(true);
	            delete.setEnabled(true);
	            
	            edit.setBackground(new Color(0xEA6A20));
				edit.setForeground(Color.WHITE);
				
				delete.setBackground(new Color(0xEA6A20));
				delete.setForeground(Color.WHITE);
	        }
	    });
		
		//create buttons
		add = new JButton("Add");
		add.setBackground(new Color(0xEA6A20));
		add.setForeground(Color.WHITE);
		add.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//make a new blank contact, then edit it and add to table
				Contact person = new Contact(null, null, null, null);
				ContactEditingDlg dlg = new ContactEditingDlg(new JDialog(), person, 1);
				dlg.setVisible(true);
				data.fireTableDataChanged();
				//nothing is highlighted after an add, so we gray the edit and delete
				edit.setEnabled(false);
				edit.setBackground(Color.LIGHT_GRAY);
				edit.setForeground(Color.BLACK);
				delete.setEnabled(false);
				delete.setBackground(Color.LIGHT_GRAY);
				delete.setForeground(Color.BLACK);
			}
		});
		
		edit = new JButton("Edit");
		edit.setEnabled(false);
		edit.setBackground(Color.LIGHT_GRAY);
		edit.setForeground(Color.BLACK);
		edit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//if edit clicked, and there are contacts in the DataStore
				//open editing dialog, then update the table
				if(DataStore.getInstance().getContacts().size() > 0){
					int row = table.getSelectedRow();
					if(row != -1){
						Contact contact = data.getContacts().get(row);
						ContactEditingDlg dlg = new ContactEditingDlg(new JDialog(), contact, 0);
						dlg.setVisible(true);
						data.fireTableDataChanged();
					}
					//if row not selected, show error dialog box
					else
						JOptionPane.showMessageDialog(null, "Please select a row to edit.", "Error", 
								JOptionPane.INFORMATION_MESSAGE, icon);
				}
				//after editing, nothing is selected, so edit and delete should be disabled
				edit.setEnabled(false);
				edit.setBackground(Color.LIGHT_GRAY);
				edit.setForeground(Color.BLACK);
				delete.setEnabled(false);
				delete.setBackground(Color.LIGHT_GRAY);
				delete.setForeground(Color.BLACK);
			}
		});
		
		delete = new JButton("Delete");
		delete.setEnabled(false);
		delete.setBackground(Color.LIGHT_GRAY);
		delete.setForeground(Color.BLACK);
		delete.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//if delete clicked, make sure you want to delete, then delete contact from table
				int row = table.getSelectedRow();
				if(row != -1){
					int del = JOptionPane.showConfirmDialog(null, "Delete " + data.getContacts().get(row).getName() + 
																	"?", "Delete Contact?", JOptionPane.YES_NO_OPTION);
					if(del == JOptionPane.YES_OPTION){
						data.deleteContact(row);
						data.fireTableDataChanged();
						if(data.getContacts().size() == 0){
							edit.setEnabled(false);
							delete.setEnabled(false);
							
							edit.setBackground(Color.LIGHT_GRAY);
							edit.setForeground(Color.BLACK);
							delete.setBackground(Color.LIGHT_GRAY);
							delete.setForeground(Color.BLACK);
						}
					}
				}
				//if no row selected, come up with error dialog
				else
					JOptionPane.showMessageDialog(null, "Please select a row to delete.", "Error", 
															JOptionPane.INFORMATION_MESSAGE, icon);
			}
		});
		
		//button to retrieve mail; opens receive window
		retrieve = new JButton("Retrieve Mail");
		retrieve.setBackground(new Color(0xEA6A20));
		retrieve.setForeground(Color.WHITE);
		retrieve.setEnabled(true);
		retrieve.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				EmailReceiverDlg receive = new EmailReceiverDlg(new JDialog());
				receive.setVisible(true);
			}
		});
		
		JPanel btnPanel = new JPanel();
		btnPanel.add(add);
		btnPanel.add(edit);
		btnPanel.add(delete);
		btnPanel.add(retrieve);
		btnPanel.setBackground(new Color(0x522D80));
		
		pane.add(menu, BorderLayout.NORTH);
		pane.add(scrollTbl, BorderLayout.CENTER);
		pane.add(btnPanel, BorderLayout.SOUTH);
		
		this.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				try {
					//when window is closing, save config and contacts
					data.storeData();
				} catch (IOException e) {
				}
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				try {
					//when window opened, load config and contacts
					data.loadData();
				} catch (ClassNotFoundException | IOException e) {
				}
				data.fireTableDataChanged();
			}
		});
	}
}
