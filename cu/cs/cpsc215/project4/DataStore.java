package cu.cs.cpsc215.project4;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/* DataStore is a Singleton class 
 * that loads and stores configuration
 * and contact data; can also be used
 * as a tablemodel
 */
public class DataStore extends AbstractTableModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private static DataStore data = null;
	private static Vector<Contact> contacts;
	private static Configuration config;
	
	private DataStore(){
		//private do nothing constructor
	}
	
	public static DataStore getInstance(){
		if(data == null) data = new DataStore();
			return data;
	}
	
	public Vector<Contact> getContacts(){
		if(contacts == null)
			contacts = new Vector<Contact>();
		return contacts;
	}
	
	public Configuration getConfig() {
		if(config == null)
			//default settings are for gmail
			config = new Configuration("smtp.gmail.com", 465, null, "imap.googlemail.com");
		return config;
	}
	
	public void deleteContact(int i){
		if(contacts.size() > 0)
			contacts.remove(i);
	}
	
	public void addContact(Contact person){
		contacts.add(person);
	}
	
	public void loadData() throws ClassNotFoundException, IOException{
		loadContacts();
		loadConfig();
	}
	
	@SuppressWarnings("unchecked")
	public void loadContacts() throws IOException, ClassNotFoundException{
		//this will load the contacts from the contact file
		FileInputStream fileIn = new FileInputStream("contacts.ser");
		ObjectInputStream in = new ObjectInputStream(fileIn);
        
		//reads in contacts
        contacts = (Vector<Contact>)in.readObject();
        
        in.close();
        fileIn.close();
	}
	//loads configuration data
	public void loadConfig() throws IOException, ClassNotFoundException{
		FileInputStream fileIn = new FileInputStream("config.ser");
		ObjectInputStream in = new ObjectInputStream(fileIn);
        
        config = (Configuration) in.readObject();
        
        in.close();
        fileIn.close();
	}
	
	public void storeData() throws IOException{
		storeContacts();
		storeConfig();
	}
	//saves contacts to disk
	public void storeContacts() throws IOException{
    	 FileOutputStream fileOut = new FileOutputStream("contacts.ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);

         out.writeObject(contacts);
		 out.close();
		 fileOut.close();
		 
         System.out.println("Serialized data is saved in contacts.ser");
	}
	//saves configuration data to disk
	public void storeConfig() throws IOException{
		FileOutputStream fileOut = new FileOutputStream("config.ser");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
         
        out.writeObject(config);
         
        out.close();
        fileOut.close();
        
        System.out.println("Serialized data is saved in config.ser");
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public int getRowCount() {
		return contacts.size();
	}

	@Override
	public String getValueAt(int row, int col) {
		Contact result = contacts.elementAt(row);
		
		if(col == 0)
			return result.getName();
		else if(col == 1)
			return result.getPostalAddress();
		else if(col == 2)
			return result.getEmail();
		else if(col == 3)
			return result.getPhone();
		else{
			System.out.println("Invalid column number");
			return null;
		}
	}
}

