package cu.cs.cpsc215.project1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;

/*
 * Class that defines a web image
 */
public class WebImage implements WebElement{
	private String m_url;

	public WebImage(String imgName){
		m_url = imgName;
	}
	
	public String getUrl(){
		return m_url;
	}
	
	//method to save image to specified folder path
	@Override
	public void saveToDisk(String folderPath, int imgCount) {
		URL url;
		BufferedImage image = null;
		
		try {
			url = new URL(this.getUrl());
			image = ImageIO.read(url);
			
			//writes image to specified directory
			//name will be 'img' + the number of images found to this point
			//also adds the correct file type ending to name
			ImageIO.write(image, this.getUrl().substring(this.getUrl().lastIndexOf('.') + 1), 
					new File(folderPath+"img"+imgCount+"."+this.getUrl().substring(this.getUrl().lastIndexOf('.') + 1)));
		} catch (MalformedURLException e) {
			System.out.print(this.getUrl());
			System.out.println("Error: " + e.getMessage());
		} catch (IOException e) {
			System.out.print(this.getUrl());
			System.out.println("Error: " + e.getMessage());
		}
		
		System.out.println("IMAGE SAVED!");
	}
}
