package vn.edu.hcmuaf.idm;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * This is the Main class of the IDM
 * This class control 
 * 
 * @author Nhan Le
 *
 */
public class DownloadManager {

	// Instance for singleton pattern
	private static DownloadManager instance = null;

	// Default values
	private static final int DEFAULT_NUM_OF_CONNECTIONS = 8;

	//Member variables
	private int connectionNumber;
	private String outputFolder = new File("").getAbsolutePath();
	private ArrayList<Downloader> downloadList;

	/** Private constructor **/
	private DownloadManager() {
		connectionNumber = DEFAULT_NUM_OF_CONNECTIONS;
		downloadList = new ArrayList<Downloader>();
	}
	
	/**
	 * Get constructor instance
	 */
	public static DownloadManager getInstance() {
		if (instance == null)
			instance = new DownloadManager();

		return instance;
	}

	/**
	 * Get connection number
	 */
	public int getConnectionNumber() {
		return connectionNumber;
	}

	/**
	 * Set connection number
	 */
	public void setConnectionNumber(int value) {
		connectionNumber = value;
	}
	
	/**
	 * Get output folder
	 */
	public String getOutputFolder() {
		return outputFolder;
	}

	/**
	 * Set output folder
	 */
	public void setOutputFolder(String value) {
		outputFolder = value;
	}

	/**
	 * Get the downloader object in the list
	 */
	public Downloader getDownload(int index) {
		return downloadList.get(index);
	}
	
	/**
	 * Get the downloader object in the list
	 */
	public void removeDownload(int index) {
		downloadList.remove(index);
	}

	/**
	 * Get download list
	 */
	public ArrayList<Downloader> getDownloadList() {
		return downloadList;
	}

	/**
	 * Create new download
	 */
	public Downloader createDownload(URL verifiedURL, String outputFolder) {
		HttpDownloader downloader = new HttpDownloader(verifiedURL, outputFolder, connectionNumber);
		downloadList.add(downloader);
		return downloader;
	}

	/**
	 * Verify URL
	 */
	public static URL verifyURL(String fileURL) {
		// Our program only work with http
		if (!fileURL.toLowerCase().startsWith("http://"))
			return null;

		// Verify URL
		URL verifiedUrl = null;
		try {
			verifiedUrl = new URL(fileURL);
		} catch (Exception e) {
			// System debugs
			System.err.println("ERROR: Bad URL");
			return null;
		}

		// Check if URL point to a file
		if (verifiedUrl.getFile().length() < 2)
			return null;

		return verifiedUrl;
	}

}
