package cu.cs.cpsc215.project1;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * Class that defines a web page
 */
public class WebPage implements WebElement{
	private Document d;
	private URL myUrl;
	
	public WebPage(String url){
		//only creates web pages for usable urls
		try {
			myUrl = new URL(url);
		} catch (MalformedURLException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}
	
	public void crawl() {
		URLConnection connection;
		InputStream in;
		
		try {
			connection = myUrl.openConnection();				//opens connection to url
			in = connection.getInputStream();					//creates an input stream connection
			d = Jsoup.parse(in, null, myUrl.toString());		//parses html code and saves in Document d
		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}

	public Vector<WebImage> getImages() {
		Elements imgs;
		Vector<WebImage> found = new Vector<WebImage>();
		
		//selects img tags that contain common image types
		imgs = d.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		for (Element aimg : imgs)
		{
			//adds imgs to 'found' vector and
			//adds root url to relative links
			if(aimg.attr("src").startsWith("http://") || aimg.attr("src").startsWith("https://")){
				found.add(new WebImage(aimg.attr("src")));
				System.out.println("IMG (" +aimg.attr("src")+")");
			}
			else{
				found.add(new WebImage(myUrl.toString() + aimg.attr("src")));
				System.out.println("With Prepend: IMG (" + myUrl.toString() + aimg.attr("src")+")");
			}
		}
		return found;
	}

	public Vector<WebFile> getFiles() {
		Elements files;
		Vector<WebFile> found = new Vector<WebFile>();
		
		//selects 'a' tags that contain common file types
		files = d.select("a[href~=(?i)\\.(doc|docx|pdf|pptx|ppt|pps|xlk|xls|xlsx|xlt|xltm|xlw)]");
		for (Element file : files)
		{
			//adds files to 'found' vector and
			//adds root url to relative links
			if(file.attr("href").startsWith("http://") || file.attr("href").startsWith("https://")){
				found.add(new WebFile(file.attr("href")));
				System.out.println("FILE (" +file.attr("href")+")");
			}
			else{
				found.add(new WebFile(myUrl.toString() + file.attr("src")));
				System.out.println("With Prepend: FILE (" + myUrl.toString() + file.attr("href")+")");
			}
		}
		
		return found;
	}

	public Vector<WebPage> getWebPages() {
		Elements links;
		Vector<WebPage> found = new Vector<WebPage>();
		
		//selects all 'a' tags
		links = d.select("a[href]");
		String href = "";
		String ending = "";
		for (Element link : links)
		{
			href = link.attr("href");	//holds the url from the tag
			ending = href.substring(href.lastIndexOf('/')+1, href.length());
			
			//selects all urls from links that end in htm, html, php, /, or have no suffix
			if(href.endsWith(".htm") || href.endsWith(".html") || href.endsWith(".php") || href.endsWith("/") || !ending.contains(".")){
				if(href.startsWith("http://") || href.startsWith("https://")){
					found.add(new WebPage(href));
					System.out.println("LINK (" +href+") - "+link.text());
				}
				else{
					found.add(new WebPage(myUrl.toString() + href));
					System.out.println("With Prepend: LINK (" + myUrl.toString() + href+") - "+link.text());
				}
			}
		}
		
		return found;
	}

	@Override
	public void saveToDisk(String folderPath, int totalCount) {
		//not saving web pages in this project
		//to be completed later
	}
}
