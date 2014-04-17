package cu.cs.cpsc215.project1;

/*
 * Interface to be used by WebImage, WebFile, and WebPage
 */
public interface WebElement {
	void saveToDisk(String folderPath, int totalCount);
}