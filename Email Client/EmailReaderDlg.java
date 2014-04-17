package cu.cs.cpsc215.project4;


import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/* creates a window to view specific inbox messages */
public class EmailReaderDlg extends JDialog{
	private static final long serialVersionUID = 1L;
	
	private Container pane;
	private JTextArea body;
	private JTextField sender, subject;

	public EmailReaderDlg(JDialog dlg, Message msg){
		super(dlg, "Email Reader");
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		pane = this.getContentPane();
		this.setSize(new Dimension(600,450));
		pane.setLayout(new GridBagLayout());
		//sets constraints of layout
		GridBagConstraints c = new GridBagConstraints();
		//tell the content pane to take up whole window
		c.weightx = 1.0;
		c.weighty = 1.0;
		
		ImageIcon icon = new ImageIcon("./icon.png");
		this.setIconImage(icon.getImage());

		try {
			//set text fields
			sender = new JTextField("From: " + msg.getFrom()[0]);
			sender.setEditable(false);
			sender.setBackground(Color.WHITE);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;	//this will put it at the top
			pane.add(sender, c);
			
			subject = new JTextField("Subject: " + msg.getSubject());
			subject.setEditable(false);
			subject.setBackground(Color.WHITE);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;	//second from top
			pane.add(subject, c);
			
			body = new JTextArea();
			body.setText(msg.getContent().toString());
			body.setEditable(false);
			body.setBackground(Color.WHITE);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.ipady = 200;
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 2;	//third from top
			pane.add(body, c);
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
	}
}
