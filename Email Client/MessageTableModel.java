package cu.cs.cpsc215.project4;

import java.io.Serializable;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.table.AbstractTableModel;

/* creates an abstract table that will hold email messages */
public class MessageTableModel extends AbstractTableModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private Vector<Message> messages;
	
	public MessageTableModel(){
		messages = new Vector<Message>();
	}
	
	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return messages.size();
	}

	@Override
	public String getValueAt(int row, int col) {
		Message result = messages.elementAt(row);
		
		//set the from, subject, and date columns
		try{
			if(col == 0)
				return result.getFrom()[0].toString();
			else if(col == 1)
				return result.getSubject();
			else if(col == 2)
				return result.getReceivedDate().toString();
			else{
				System.out.println("Invalid column number");
			}
		}catch(MessagingException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void addMessage(Message msg){
		messages.add(msg);
	}
	
	public Message getMessageAt(int row){
		return messages.elementAt(row);
	}
}
