package cu.cs.cpsc215.project1;

/* Kyle Brennan - kcbrenn
 * Harrison Cunningham - hcunnin
 * CPSC2150 Project 1
 * Java Web Crawler with Jsoup
 * September 30, 2013
 */

import java.util.Vector;

/*
 * Main Driver that accepts 3 arguments
 * 	1st argument is the starting url
 * 	2nd argument is the maximum depth to crawl to
 * 	3rd argument is the specified file path to save images and files
 */
public class WebCrawler {
	
	public static void main(String[] args){
		String url;
		int maxDepth;
		String filePath;
		
		int imgCount;
		int fileCount;
		
		DownloadRepository bin;
		
		//create three vectors that will hold the urls of all
		//images, files, and web pages that are found
		Vector<WebImage> images = new Vector<WebImage>();
		Vector<WebFile> files = new Vector<WebFile>();
		Vector<WebPage> pages = new Vector<WebPage>();
		
		WebPage page;
		
		//set the variables url, maxDepth, and filePath to
		//the commands that are passed in
		url = args[0];
		maxDepth = Integer.parseInt(args[1]);
		filePath = args[2];
		
		//set page to the first web page
		page = new WebPage(url);
		//add this page to the 'pages' vector that holds all web pages
		pages.add(page);
		
		//create a Singleton instance of DownloadRepository class
		bin = DownloadRepository.getInstance();
		
		//set the file and image counters to start at 0
		imgCount = 0;
		fileCount = 0;

		int depthCount = 0;	//int to hold current depth
		int i;
		int size = 0;	//this will hold the current size of the 'pages' vector
		int oldSize;	//this will hold the previous size of the 'pages' vector
		
		while(depthCount <= maxDepth){
			oldSize = size;
			size = pages.size();
			
			for(i=oldSize;i<size;i++){
				try{
					page = pages.get(i);	//set page to the i-th web page in 'pages' vector
					if(!bin.contains(page)){	//if not in DownloadRepository
						bin.add(page);	//add page to 'pages' vector
						page.crawl();
						images.addAll(page.getImages());	//add all images found on this page to 'images'
						files.addAll(page.getFiles());		//add all files found on this page to 'files'
						pages.addAll(page.getWebPages());	//add all new pages found to 'pages'
					}
				} catch(Exception e){
					System.out.println("ERROR: " + e.getMessage());
				}
			}
			depthCount++;
		}
		
		//the following for loops traverse through the 'files' and 'images' vectors
		//and saves the file or image to the specified directory
		//if the file or image has not already been saved
		for(i=0;i<images.size();i++){
			if(!bin.contains(images.get(i))){
				try{
					bin.add(images.get(i));
					images.get(i).saveToDisk(filePath, imgCount);
					imgCount++;
				} catch(Exception e){
					System.out.println("ERROR: " + e.getMessage());
				}
			}
		}
	
		for(i=0;i<files.size();i++){
			if(!bin.contains(files.get(i))){
				try{
					bin.add(files.get(i));
					files.get(i).saveToDisk(filePath, fileCount);
					fileCount++;
				} catch(Exception e){
					System.out.println("ERROR: " + e.getMessage());
				}
			}
		}
		
		System.out.println("I'm Finished!!!");
	}
}
