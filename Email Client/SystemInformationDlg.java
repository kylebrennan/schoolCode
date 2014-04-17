package cu.cs.cpsc215.project4;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/* simple class to show the system and application build information */
public class SystemInformationDlg extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public SystemInformationDlg(JDialog dlg){
		super(dlg, "System Info", true);
		
		Container pane = this.getContentPane();
		this.setSize(new Dimension(450,350));
		pane.setBackground(Color.WHITE);
		
		ImageIcon icon = new ImageIcon("./icon.png");
		this.setIconImage(icon.getImage());
		
		JLabel pic = new JLabel();
		pic.setIcon(icon);
		
		JTextArea about = new JTextArea();
		about.setEditable(false);
		about.setText("\n\n\n\n" +
					  "Designed by: Kyle Brennan and Harrison Cunningham\n" +
					  "Build Number: 00001\n" +
					  "\n" +
					  "Email Client designed specifically for GMail users.\n\n" +
					  "Created at Clemson University in CPSC 215\n" +
					  "Professor: Dr. Jacob Sorber");
		
		pane.setLayout(new BorderLayout());
		pane.add(pic, BorderLayout.WEST);
		pane.add(about, BorderLayout.EAST);
	}
}