package cu.cs.cpsc215.project4;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/* creates window to edit configuration */
public class ConfigurationDlg extends JDialog{
	private static final long serialVersionUID = 1L;
	
	private JTextField host, port, email, imap;
	private JLabel hostlab, portlab, emaillab, ilab;
	private int hClick, pClick, eClick, iClick;
	private DataStore data;
	private JButton saveBtn, cancelBtn;
	
	public ConfigurationDlg(JDialog dlg){
		super(dlg, "Configuration", true);
		
		data = DataStore.getInstance();
		
		Container pane = this.getContentPane();
		this.setSize(new Dimension(400, 300));
		pane.setLayout(new BorderLayout());
		pane.setBackground(Color.LIGHT_GRAY);
		
		//sets a custom icon for the window
		ImageIcon icon = new ImageIcon("./icon.png");
		this.setIconImage(icon.getImage());
		
		hostlab = new JLabel("Smtp Host: ");
		portlab = new JLabel("Smtp Port: ");
		emaillab = new JLabel("Smtp Primary Email: ");
		ilab = new JLabel("IMAP Host: ");
		
		JPanel labelPane = new JPanel();
		labelPane.setLayout(new GridLayout(4,1));
		labelPane.add(hostlab);
		labelPane.add(portlab);
		labelPane.add(emaillab);
		labelPane.add(ilab);
		
		pane.add(labelPane, BorderLayout.WEST);
		
		//these will hold the number of times each textfield is clicked
		hClick = pClick = eClick = iClick = 0;
		
		//create the editable textfields
		if(data.getConfig().getSmtpHost() == null)
			host = new JTextField("SMTP Host");
		else
			host = new JTextField(data.getConfig().getSmtpHost());
		host.setEditable(true);
		host.setForeground(Color.GRAY);
		host.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				//if this is the first time clicking into this field,
				//clear text
				if(hClick == 0){
					host.setText("");
					host.setForeground(Color.BLACK);
				}
				hClick++;
			}
		});
		
		if(data.getConfig().getSmtpPort() == 0)
			port = new JTextField("SMTP Port");
		else
			port = new JTextField(Integer.toString(data.getConfig().getSmtpPort()));
		port.setEditable(true);
		port.setForeground(Color.GRAY);
		port.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(pClick == 0){
					port.setText("");
					port.setForeground(Color.BLACK);
				}
				pClick++;
			}
		});
		
		if(data.getConfig().getSmtpPrimaryEmail() == null)
			email = new JTextField("Primary Email");
		else
			email = new JTextField(data.getConfig().getSmtpPrimaryEmail());
		email.setEditable(true);
		email.setForeground(Color.GRAY);
		email.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(eClick == 0){
					email.setText("");
					email.setForeground(Color.BLACK);
				}
				eClick++;
			}
		});
		
		if(data.getConfig().getImapHost() == null)
			imap = new JTextField("IMAP Host");
		else
			imap = new JTextField(data.getConfig().getImapHost()); 
		imap.setForeground(Color.GRAY);
		imap.setEditable(true);
		imap.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(iClick == 0){
					imap.setText("");
					imap.setForeground(Color.BLACK);
				}
				iClick++;
			}
		});
		
		//make a panel to hold all of the textfields
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(4,1));
		textPanel.add(host);
		textPanel.add(port);
		textPanel.add(email);
		textPanel.add(imap);
		
		//give buttons theme colors
		saveBtn = new JButton("Save");
		saveBtn.setForeground(Color.WHITE);
		saveBtn.setBackground(new Color(0xEA6A20));
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.setForeground(Color.WHITE);
		cancelBtn.setBackground(new Color(0xEA6A20));
		
		saveBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ae){
				int dontDispose = 0;	//this will tell us if we can close the edit window
				
				if(host.getText() != null)
					data.getConfig().setSmtpHost(host.getText());
				if(port.getText() != null)
					data.getConfig().setSmtpPort(Integer.parseInt(port.getText()));
				//check if email is valid
				if(email.getText() != null && isValid(email.getText()))
					data.getConfig().setSmtpPrimaryEmail(email.getText());
				else if(!isValid(email.getText())){
					JOptionPane.showMessageDialog(null, email.getText() + " is an invalid address.");
					dontDispose = 1;
				}
				if(imap.getText() != null)
					data.getConfig().setImapHost(imap.getText());
				if(dontDispose == 0)	//email is valid, close the window
					dispose();
			}
		});
		
		cancelBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		JPanel btnPanel = new JPanel();
		btnPanel.add(saveBtn);
		btnPanel.add(cancelBtn);

		pane.add(textPanel, BorderLayout.CENTER);
		pane.add(btnPanel, BorderLayout.SOUTH);
	}
	
	//validates email address
	public boolean isValid(String input){
		//compares the email address to valid email regular expression
		String emailreg = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		return input.matches(emailreg);
	}
}
