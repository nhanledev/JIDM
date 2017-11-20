package vn.edu.hcmuaf.idm;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;

/**
 * This is Downloader that handle a single download in Download List
 * is observable and is run as a thread 
 * 
 * @author Nhan Le
 *
 */
public abstract class Downloader extends Observable implements Runnable {

	// Member variables
	protected URL dURL;
	protected String dOutputFolder;
	protected int dConnections;
	protected String dFileName;
	protected long dFileSize;
	protected int dState;
	protected int dDownloaded;
	protected int currentSpeed;
	protected ArrayList<DownloadThread> dDownloadThreadList;

	// Default constant
	protected static final int BLOCK_SIZE = 4096;
	protected static final int BUFFER_SIZE = 4096;
	protected static final int MIN_DOWNLOAD_SIZE = BLOCK_SIZE * 100;

	// State list
	public static final String STATUS[] = { "Downloading", "Paused", "Completed", "Cancelled", "Error" };

	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETED = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;

	/**
	 * Constructor
	 * 
	 * @param fileURL
	 * @param outputFolder
	 * @param connections
	 */
	protected Downloader(URL url, String outputFolder, int connections) {
		dURL = url;
		dOutputFolder = outputFolder;
		dConnections = connections;
		dFileName = FileUltil.getFileNameFromURL(url);
		// Validate file name in outputFolder
		validateFile();
		dFileSize = -1;
		dDownloaded = 0;
		
		dDownloadThreadList = new ArrayList<DownloadThread>();
		
		// System debug
		System.out.println("Filename: " + dFileName);
	}
	
	/**
	 * ERROR HANDLE -----------------------------------
	 */
	
	protected void error() {
		setState(ERROR);
	}
	
	/**
	 * DOWNLOAD STATE HANDLE -----------------------------------
	 */
	
	/**
	 *  Pause download
	 */
	public void pause() {
		setState(PAUSED);
	}

	/**
	 * Resume download
	 */
	public void resume() {
		download();
	}

	/**
	 * Cancel download
	 */
	public void cancel() {
		setState(CANCELLED);
	}
	
	/**
	 * Start / resume download
	 */
	protected void download() {
		setState(DOWNLOADING);
		
		Thread t = new Thread(this);
		t.start();
	}
	
	/**
	 * Get current state of the downloader
	 */
	public int getState() {
		return dState;
	}

	/**
	 * Set the state of the downloader
	 */
	protected void setState(int value) {
		dState = value;
		stateChanged();
		
		// System debug
		System.out.println("State changed: " + STATUS[dState]);
	}
	
	/**
	 * Set the state changed and notify the observers
	 */
	protected void stateChanged() {
		setChanged();
		notifyObservers();
	}
	
	/**
	 * SYSTEM HANDLE -----------------------------------
	 */

	/**
	 * Get the URL (in String)
	 */
	public String getURL() {
		return dURL.toString();
	}
	
	/**
	 * Get Filename
	 */
	public String getFileName() {
		return dFileName;
	}

	/**
	 * Get the file size
	 */
	public long getFileSize() {
		return dFileSize;
	}

	/**
	 * Get the current progress of the download
	 */
	public float getProgress() {
		return ((float) dDownloaded / dFileSize);
	}
	
	/**
	 * Get the current speed of the download
	 */
	public int getSpeed() {
		int currentSpeed = 0;
		for (DownloadThread thread : dDownloadThreadList) {
			currentSpeed += thread.getSpeed();
		}
		return currentSpeed;
	}

	/**
	 * Increase the downloaded size
	 */
	protected synchronized void downloaded(int value) {
		dDownloaded += value;
		stateChanged();
	}
	
	/**
	 * Check and rename filename if file is already exists
	 */
	protected void validateFile() {
		File f = new File(FileUltil.joinPath(dOutputFolder, dFileName));
		if (f.exists() && !f.isDirectory()) {
			dFileName = "Copy of " + dFileName;
			validateFile();
		}
	}
	
	/**
	 * Check if the server accept resume or not
	 */
	protected boolean validateServerResume() {
		HttpURLConnection connection = null;
		boolean isSupported = false;
		
		try {
			// Open connection
			connection = (HttpURLConnection) dURL.openConnection();
			connection.setConnectTimeout(10000);
			connection.setRequestProperty("Range", "bytes=10-20");
			connection.connect();

			// HTTP/206: Partial Content: Support multiple simultaneous streams
			if (connection.getResponseCode() == 206) {
				isSupported = true;

				// System debugs
				System.out.println("Server support resume");
			}

		} catch (IOException e) {
			error();
			
			// System debugs
			System.err.println("ERROR: Can not connect to server");
		} finally {
			if (connection != null)
				connection.disconnect();
		}
		
		return isSupported;
	}
	

	/**
	 * Thread to download parts of a file
	 */
	protected abstract class DownloadThread implements Runnable {
		protected int tThreadID;
		protected URL tURL;
		protected String tOutputFile;
		protected long tStartByte;
		protected long tEndByte;
		protected long tStartTime;
		protected long tDownloaded;
		protected boolean tIsFinished;
		protected Thread tThread;

		/**
		 * Constructor
		 * 
		 * @param threadID
		 * @param url
		 * @param outputFile
		 * @param startByte
		 * @param endByte
		 */
		public DownloadThread(int threadID, URL url, String outputFile, long startByte, long endByte) {
			tThreadID = threadID;
			tURL = url;
			tOutputFile = outputFile;
			tStartByte = startByte;
			tEndByte = endByte;
			tIsFinished = false;

			download();
		}

		/**
		 * Get finish status of thread
		 */
		public boolean isFinished() {
			return tIsFinished;
		}

		/**
		 * Start / resume the download thread
		 */
		public void download() {
			tThread = new Thread(this);
			tThread.start();
		}
		
		/**
		 * Get current speed of thread
		 */
		public int getSpeed() {
			long elapsedTime = System.currentTimeMillis() - tStartTime;
			
			// Refresh cache every 5 secs & prevent fake speed when resume
			if (elapsedTime > 5000) {
				speedRefresh();
				return 0;
			}
			
			return Math.round(1000f * tDownloaded / elapsedTime);
		}
		
		/**
		 * Refresh the speed meter
		 */
		protected void speedRefresh() {
			tStartTime = System.currentTimeMillis();
			tDownloaded = 0;
		}

		/**
		 * Waiting for the thread to finish
		 * 
		 * @throws InterruptedException
		 */
		public void waitFinish() throws InterruptedException {
			tThread.join();
		}

	}
}
