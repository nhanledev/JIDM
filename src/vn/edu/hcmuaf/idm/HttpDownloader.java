package vn.edu.hcmuaf.idm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is a TableCellRender that control the JProgress cell in the DownloadTableModel
 * 
 * @author Nhan Le
 *
 */
public class HttpDownloader extends Downloader {

	/**
	 * Constructor
	 * 
	 * @param url
	 * @param outputFolder
	 * @param numConnections
	 */
	public HttpDownloader(URL url, String outputFolder, int numConnections) {
		// Open a thread and download immediately
		super(url, outputFolder, numConnections);
		download();
	}

	/**
	 * Download algorithm
	 */
	@Override
	public void run() {
		HttpURLConnection connection = null;
		try {
			// Open connection
			connection = (HttpURLConnection) dURL.openConnection();
			connection.setConnectTimeout(10000);
			connection.connect();

			// Only accept response code in 2xx / success
			if (connection.getResponseCode() / 100 != 2) {
				error();

				// System debugs
				System.err.println("ERROR: Server response code: " + connection.getResponseCode());
			}

			// Check for valid content length (file size)
			long contentLength = connection.getContentLengthLong();
			if (contentLength < 1) {
				error();
				
				// System debugs
				System.err.println("ERROR: The URL is not a file (contentLength < 1) ");
			}

			if (dFileSize == -1) {
				dFileSize = contentLength;
				stateChanged();
				
				// System debug
				System.out.println("File size: " + dFileSize);
			}

			// If the state is DOWNLOADING, start download
			if (dState == DOWNLOADING) {
				
				// If download have no thread, init and download
				if (dDownloadThreadList.size() == 0) {
					
					if (dFileSize > MIN_DOWNLOAD_SIZE && validateServerResume()) {
						// Calculate size for each part
						long partSize =  (long) Math.ceil( ( ((float) dFileSize / dConnections) / BLOCK_SIZE ) ) * BLOCK_SIZE;
						
						// System debug
						System.out.println("Part size: " + partSize);

						// Calculate start/end byte
						long startByte = 0;
						long endByte = partSize - 1;
						HttpDownloadThread downloadThread = new HttpDownloadThread(1, dURL, FileUltil.joinPath(dOutputFolder, dFileName), startByte, endByte);
						dDownloadThreadList.add(downloadThread);
						
						// Add other threads
						for (int i = 2; endByte < dFileSize; i++) {
							startByte = endByte + 1;
							
							// The last thread is end at the end size of filesize
							if (i == dConnections) {
								endByte = dFileSize;
							} else {
								endByte += partSize;
							}
							
							downloadThread = new HttpDownloadThread(i, dURL, FileUltil.joinPath(dOutputFolder, dFileName), startByte, endByte);
							dDownloadThreadList.add(downloadThread);
						}
						
					}
					
					// If file size smaller than 400KB or not support resume, use one thread
					else {
						HttpDownloadThread downloadThread = new HttpDownloadThread(1, dURL, FileUltil.joinPath(dOutputFolder, dFileName), 0, (int) dFileSize);
						dDownloadThreadList.add(downloadThread);
					}
				}
				
				// Resume all threads if download already have thread list
				else {
					for (DownloadThread thread : dDownloadThreadList) {
						if (!thread.isFinished())
							thread.download();
					}
				}

				// Waiting for all threads to complete
				for (DownloadThread thread : dDownloadThreadList) {
					if (!thread.isFinished())
						thread.waitFinish();
				}

				// Mark state as completed
				if (dState == DOWNLOADING) {
					setState(COMPLETED);
				}
			}
			
		} catch (InterruptedException e) {
			error();
			
			// System debugs
			System.err.println("ERROR: Error while waiting for threads to finish");
		} catch (IOException e) {
			error();
			
			// System debugs
			System.err.println("ERROR: Can not connect to server");
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	/**
	 * Thread that use Http protocol to download file part
	 */
	private class HttpDownloadThread extends DownloadThread {

		/**
		 * Constructor
		 * 
		 * @param threadID
		 * @param url
		 * @param outputFile
		 * @param startByte
		 * @param endByte
		 */
		public HttpDownloadThread(int threadID, URL url, String outputFile,
				long startByte, long endByte) {
			super(threadID, url, outputFile, startByte, endByte);
		}

		@Override
		public void run() {
			BufferedInputStream bis = null;
			RandomAccessFile raf = null;

			try {
				// Open Http connection
				HttpURLConnection connection = (HttpURLConnection) tURL.openConnection();

				// Request the range of byte to download
				String byteRange = tStartByte + "-" + tEndByte;
				connection.setRequestProperty("Range", "bytes=" + byteRange);
				
				// Reset speed metering
				speedRefresh();

				// Connect to server
				connection.connect();

				// Make sure the response code is in the 2xx range / success
				if (connection.getResponseCode() / 100 != 2) {
					error();
					
					// System debugs
					System.err.println("ERROR: (HTTPThread " + tThreadID + ") Server response code: " + connection.getResponseCode());
				}
				
				// System debug
				System.out.println("HTTP Thread " + tThreadID + " start at byte " + tStartByte );
				System.out.println("- Byte: " + byteRange);

				// Get the input stream
				bis = new BufferedInputStream(connection.getInputStream());

				// Open and write file
				raf = new RandomAccessFile(tOutputFile, "rw");
				raf.seek(tStartByte);
				
				byte data[] = new byte[BUFFER_SIZE];
				int numRead;
				while ((dState == DOWNLOADING) && ((numRead = bis.read(data, 0, BUFFER_SIZE)) != -1)) {
					// Write
					raf.write(data, 0, numRead);
					
					// Increase startbyte for resuming
					tStartByte += numRead;
					
					// Increase the downloaded size
					downloaded(numRead);
					
					// Count the byte read for speed metering
					tDownloaded += numRead;
					
				}

				if (dState == DOWNLOADING) {
					tIsFinished = true;
				}
				
			} catch (IOException e) {
				error();

				// System debugs
				System.err.println("ERROR: (HTTP Thread " + tThreadID + ") I/O Exception");
			} finally {
				if (raf != null) {
					try {
						raf.close();
					} catch (IOException e) {
					}
				}

				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
					}
				}
			}

			// System debug
			System.out.println("HTTP Thread " + tThreadID + " end.");
		}
	}
}
