package cu.cs.cpsc215.project1;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/*
 * Class that defines a web file
 */
public class WebFile implements WebElement {
	private String m_url;
	
	public WebFile(String fileName){
		m_url = fileName;
	}
	
	public String getUrl(){
		return m_url;
	}

	@Override
	public void saveToDisk(String folderPath, int fileCount) {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        
        try
        {
        	//opens input and output streams to read and write the file
            in = new BufferedInputStream(new URL(this.getUrl()).openStream());
            fout = new FileOutputStream(folderPath+"file"+fileCount+"."+this.getUrl().substring(this.getUrl().lastIndexOf('.') + 1));
            
            byte data[] = new byte[1024];
            int count;
            
            //while not end of file, read in the byte data, and save in array
            while ((count = in.read(data, 0, 1024)) != -1)
                fout.write(data, 0, count);		//save in specified directory and file name
            if (in != null)
                in.close();
            if (fout != null)
                fout.close();
        } catch (IOException e){
        	System.out.print(this.getUrl());
        	System.out.println("Error: " + e.getMessage());
        }
        
        System.out.println("FILE SAVED!");
	}
}
