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

/* creates window to edit contacts */
public class ContactEditingDlg extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private DataStore data;
	private Contact toChange;
	private JTextField name, postAddress, email, phone;
	private JLabel namelab, palab, emaillab, phlab;
	private JButton saveBtn, cancelBtn;
	private int nClick, pClick, eClick, phClick, toAdd;
	
	public ContactEditingDlg(JDialog dlg, Contact contact, int add){
		super(dlg, "Contact Editor", true);
		this.setSize(new Dimension(400,300));
		
		Container pane = this.getContentPane();
		pane.setBackground(Color.LIGHT_GRAY);
		pane.setLayout(new BorderLayout());
		
		//set custom icon
		ImageIcon icon = new ImageIcon("./icon.png");
		this.setIconImage(icon.getImage());
		
		//get instance of DataStore
		data = DataStore.getInstance();
		data.getContacts();
		toChange = contact;
		toAdd = add; //if this is 1, we are adding; 0 if we are editing
		
		namelab = new JLabel("Full Name: ");
		palab = new JLabel("Postal Address: ");
		emaillab = new JLabel("Email Address: ");
		phlab = new JLabel("Phone Number: ");
		
		//make a panel for the labels
		JPanel labelPane = new JPanel();
		labelPane.setLayout(new GridLayout(4,1));
		labelPane.add(namelab);
		labelPane.add(palab);
		labelPane.add(emaillab);
		labelPane.add(phlab);
		
		pane.add(labelPane, BorderLayout.WEST);
		
		//create the text fields just like in ConfigurationDlg
		name = new JTextField();
		if(toChange.getName() == null){
			name = new JTextField("Name");
			name.setForeground(Color.GRAY);
		}
		else
			name = new JTextField(toChange.getName());
		
		name.setEditable(true);
		name.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(nClick == 0){
					name.setText("");
					name.setForeground(Color.BLACK);
				}
				nClick++;
			}
		});
		
		postAddress = new JTextField();
		if(toChange.getPostalAddress() == null){
			postAddress = new JTextField("Postal Address");
			postAddress.setForeground(Color.GRAY);
		}
		else
			postAddress = new JTextField(toChange.getPostalAddress());
		
		postAddress.setEditable(true);
		postAddress.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(pClick == 0){
					postAddress.setText("");
					postAddress.setForeground(Color.BLACK);
				}
				pClick++;
			}
		});
		
		email = new JTextField();
		if(toChange.getEmail() == null){
			email = new JTextField("Email");
			email.setForeground(Color.GRAY);
		}
		else
			email = new JTextField(toChange.getEmail());

		email.setEditable(true);
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
		
		phone = new JTextField();
		if(toChange.getPhone() == null){
			phone = new JTextField("Phone");
			phone.setForeground(Color.GRAY);
		}
		else
			phone = new JTextField(toChange.getPhone());
		
		phone.setEditable(true);
		phone.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(phClick == 0){
					phone.setText("");
					phone.setForeground(Color.BLACK);
				}
				phClick++;
			}
		});
		
		//add text fields to a panel
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(4,1));
		textPanel.add(name);
		textPanel.add(postAddress);
		textPanel.add(email);
		textPanel.add(phone);
		
		saveBtn = new JButton("Save");
		saveBtn.setForeground(Color.WHITE);
		saveBtn.setBackground(new Color(0xEA6A20));
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.setForeground(Color.WHITE);
		cancelBtn.setBackground(new Color(0xEA6A20));
		
		saveBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ae){
				//if everything is good, we update the contact information
				if(name != null)
					toChange.setName(name.getText());
				if(postAddress != null)
					toChange.setPostAddress(postAddress.getText());
				if(phone != null)
					toChange.setPhone(phone.getText());
				if(email.getText() != null && isValid(email.getText()))
					toChange.setEmail(email.getText());
				else if(!isValid(email.getText())){
					JOptionPane.showMessageDialog(null, email.getText() + " is an invalid address.");
				}
				
				//if we are adding, we need to add the conact
				//otherwise we are just editing an existing contact
				if(toAdd == 1)
					data.addContact(toChange);
				dispose();
			}
		});
		
		cancelBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		//add everything to the content pane
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
