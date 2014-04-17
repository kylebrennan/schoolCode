package cu.cs.cpsc215.project1;

import java.util.HashSet;

/* 
 * Singleton class that holds
 * all web pages, images, and files
 * that have been seen in a 
 * hash set.
 */

public class DownloadRepository {

	private static DownloadRepository bin;
	private HashSet<WebElement> holder;
	
	private DownloadRepository(){
		holder = new HashSet<WebElement>();
	}
	
	//returns a static DownloadRepository
	//means it will only create one
	public static DownloadRepository getInstance(){
		if(bin == null) bin = new DownloadRepository();
		return bin;
	}
	
	//adds element to repository
	public void add(WebElement element){
		holder.add(element);
	}
	
	//checks to see if element is already in repository
	public boolean contains(WebElement element){
		return holder.contains(element);
	}
}
