package cu.cs.cpsc215.project4;


import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/* This class sends the email */
public class EmailTransmissionDlg extends JDialog{
	private static final long serialVersionUID = 1L;
	
	private JTextField source, destination, subject;
	private JTextArea body;
	private DataStore data;
	private Contact recipient;
	private JButton sendBtn, cancelBtn;
	
	public EmailTransmissionDlg(JDialog dlg, Contact contact){
		super(dlg, "Email Transmission", true);
		
		Container pane = this.getContentPane();
		this.setSize(new Dimension(600,450));
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		ImageIcon icon = new ImageIcon("./icon.png");
		this.setIconImage(icon.getImage());
		
		data = DataStore.getInstance();
		recipient = contact;
		
		//add text fields to the window
		source = new JTextField();
		source.setEditable(false);
		source.setBackground(Color.WHITE);
		source.setText("From: " + data.getConfig().getSmtpPrimaryEmail());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(source, c);
		
		destination = new JTextField();
		destination.setEditable(true);
		destination.setBackground(Color.WHITE);
		destination.setText("To: " + recipient.getEmail());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		pane.add(destination, c);
		
		subject = new JTextField();
		subject.setEditable(true);
		subject.setBackground(Color.WHITE);
		subject.setText("Subject");
		subject.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//clear text when field clicked into
				subject.setText("");
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		pane.add(subject, c);
		
		body = new JTextArea(50, 100);
		JScrollPane scrollPane = new JScrollPane(body);
		body.setEditable(true);
		body.setBackground(Color.WHITE);
		body.setText("Enter message body here...");
		body.setSize(new Dimension(300, 500));
		body.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				body.setText("");
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 200;
		c.weightx = 0.0;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 3;
		pane.add(scrollPane, c);
		
		sendBtn = new JButton("Send");
		sendBtn.setBackground(new Color(0xEA6A20));
		sendBtn.setForeground(Color.WHITE);
		sendBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				//move past the 'To: '
				String emails = destination.getText().substring(4);
				//split up emails based on commas
				List<String> eachEmail = Arrays.asList(emails.split(","));
				sendMessage(eachEmail);
				dispose();
			}
		});
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.setBackground(new Color(0xEA6A20));
		cancelBtn.setForeground(Color.WHITE);
		cancelBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		
		JPanel btnPanel = new JPanel();
		btnPanel.add(sendBtn);
		btnPanel.add(cancelBtn);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;
		c.ipady = 0;
		pane.add(btnPanel, c);
	}
	
	//sends message to every email in list
	private String password;
	public void sendMessage(List<String> email){
		try {
			//set all the properties for sending an email
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
	        props.put("mail.smtp.host", data.getConfig().getSmtpHost());
	        props.put("mail.smtp.port", data.getConfig().getSmtpPort());
	        
	        props.put("mail.smtp.socketFactory.port", data.getConfig().getSmtpPort());
	        props.put("mail.smtp.socketFactory.class", 
	        		"javax.net.ssl.SSLSocketFactory");
	        props.put("mail.smtp.auth", "true");
	        
	        //get password
	        password = null;
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
			session.setDebug(true);
			
			//make a new email message based on body text
			Message msg;
			msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(data.getConfig().getSmtpPrimaryEmail()));
			msg.setSubject(subject.getText());
			msg.setText(body.getText());
			int i;
			//for every email in list, try to send
			for(i=0;i<email.size();i++){
				try{
					msg.setRecipient(RecipientType.TO, new InternetAddress(email.get(i)));
					Transport.send(msg);
					//if this doesn't work, we have an invalid address
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, email.get(i) + " is an invalid address.");
				}
			}
		
		    System.out.println("Message sent.");
			
		} catch(Exception exc) {
			System.out.println("Exception: " + exc);
		}
	}
}