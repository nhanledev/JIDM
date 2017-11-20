package vn.edu.hcmuaf.idm;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

/**
 * This is a table model for displaying data to JTable
 * and also is a observer that downloaders can notify their changes to
 * 
 * @author Nhan Le
 *
 */
public class DownloadTableModel extends AbstractTableModel implements Observer {
	private static final long serialVersionUID = 1L;

	// Table column name
    private static final String[] columnNames = {"File Name", "Size", "Progress", "Transfer rate", "Status"};
    
    // Table column class
	@SuppressWarnings("rawtypes")
	private static final Class[] columnClasses = {String.class, String.class, JProgressBar.class, String.class, String.class};
    
    /**
     *  Add a new download
     */
    public void addNewDownload(Downloader download) {
    	
        // Register this model to be a downloader's observer
        download.addObserver(this);
        
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }
    
    /**
     *  Remove a download
     */
    public void clearDownload(int row) {        
        fireTableRowsDeleted(row, row);
    }
    
    /**
     *  Get number of rows
     */
    public int getRowCount() {
        return DownloadManager.getInstance().getDownloadList().size();
    }
    
    /**
     *  Get value of a row
     */
    public Object getValueAt(int row, int col) {
        Downloader download = DownloadManager.getInstance().getDownloadList().get(row);
        
        switch (col) {
            case 0: // File name
                return download.getFileName();
            case 1: // Size
                return (download.getFileSize() == -1) ? "" : (FileUltil.readableFileSize(download.getFileSize()));
            case 2: // Progress
                return download.getProgress();
            case 3: // Transfer rate
                return  (download.getState() != 0) ? "" : (FileUltil.readableFileSize((long) download.getSpeed()) + "/sec");
            case 4: // Status
                return Downloader.STATUS[download.getState()];
        }
        
        return "";
    }
    
    /**
     *  Get number of columns
     */
    public int getColumnCount() {
        return columnNames.length;
    }
    
    /**
     *  Get name of column
     */
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    /**
     *  Get class of column
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int col) {
        return columnClasses[col];
    }
    
    /**
     * Update when downloader call Observer
     */
    public void update(Observable o, Object arg) {
        int index = DownloadManager.getInstance().getDownloadList().indexOf(o);
        fireTableRowsUpdated(index, index);
    }
}