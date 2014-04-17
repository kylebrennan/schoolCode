package cu.cs.cpsc215.project4;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.sun.mail.imap.IMAPFolder;

public class EmailReceiverDlg extends JDialog{
	private static final long serialVersionUID = 1L;

	private Container pane;
	private JTable table;
	private List<Message> messages;
	private final MessageTableModel model;
	private DataStore data;
	
	public EmailReceiverDlg(JDialog dlg){
		super(dlg, "Email Inbox");
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		pane = this.getContentPane();
		this.setSize(new Dimension(600,450));
		pane.setLayout(new BorderLayout());
		
		ImageIcon icon = new ImageIcon("./icon.png");
		this.setIconImage(icon.getImage());
		
		data = DataStore.getInstance();
		
		model = new MessageTableModel();
		
		//set the titles for all the columns
		table = new JTable(model);
		table.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("From");
		table.getTableHeader().getColumnModel().getColumn(1).setHeaderValue("Subject");
		table.getTableHeader().getColumnModel().getColumn(2).setHeaderValue("Date Received");
		table.setGridColor(Color.BLACK);
		
		//make the table scrollable
		JScrollPane scrollTbl = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		
		//if row double clicked, open message in new window
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 2) {
			         JTable target = (JTable)e.getSource();
			         int row = target.getSelectedRow();
			         Message received = messages.get(row);
			         EmailReaderDlg reader = new EmailReaderDlg(new JDialog(), received);
			         reader.setVisible(true);
				}
			}
		});
		
		
		try {
			receiveEmail();
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
		pane.add(scrollTbl, BorderLayout.CENTER);
		
	}
	
	//this method connects to imap server and fetches emails for primary email address
	private String password;
	private void receiveEmail() throws MessagingException, IOException{
		IMAPFolder folder = null;
        Store store = null;
        JLabel numMessages = null, numUnread = null;
        JPanel numbersPanel = null;
        try{ 
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            
            //get the authentication password for the primary email
            JPasswordField jpf = new JPasswordField(24);
		    JLabel jl = new JLabel("Enter Your Password: ");
		    Object[] options = {"OK"};
		    JPanel panel = new JPanel();
		    panel.add(jl);
		    panel.add(jpf);
		    JOptionPane.showOptionDialog(null, panel, "Password Entry", JOptionPane.NO_OPTION, 
		    									JOptionPane.QUESTION_MESSAGE, null, options , options[0]);

	    	password = new String(jpf.getPassword());

            Authenticator auth = new Authenticator () {
            	public PasswordAuthentication getPasswordAuthentication(){
					return new PasswordAuthentication(data.getConfig().getSmtpPrimaryEmail(), password);
				}
			};
			
            Session session = Session.getInstance(props, auth);

            try{
            	//connect to imap session
	            store = session.getStore("imaps");
	            store.connect(data.getConfig().getImapHost(),data.getConfig().getSmtpPrimaryEmail(), password);
	            
	            folder = (IMAPFolder) store.getFolder("inbox");
	            
	            //store the inbox messages into folder
	            if(!folder.isOpen())
	            	folder.open(Folder.READ_WRITE);
	           
	            //make folder messages into arraylist
	            messages = Arrays.asList(folder.getMessages());
	            for(Message msg : messages)
	            	model.addMessage(msg);
	            
	            //add messages to a MessageTableModel
	            model.fireTableDataChanged();
	            
	            //give details of inbox
	            numMessages = new JLabel("Total Messages: " + folder.getMessageCount());
	            numUnread = new JLabel("Unread Messages: " + folder.getUnreadMessageCount());
	            numbersPanel = new JPanel();
	            numbersPanel.setLayout(new GridLayout(2,1));
	            numbersPanel.add(numUnread);
	            numbersPanel.add(numMessages);
	            pane.add(numbersPanel, BorderLayout.WEST);
            }catch(Exception e){
            	
            }
        }
        finally{
        }
	}
}