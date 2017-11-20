package vn.edu.hcmuaf.idm;

import java.awt.Component;
import java.text.MessageFormat;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * This class is a TableCellRender that control the JProgress cell in the DownloadTableModel
 * 
 * @author Nhan Le
 *
 */
public class RendererProgressBar extends JProgressBar implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param min
	 * @param max
	 */
	public RendererProgressBar(int min, int max) {
		super(min, max);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		// Set the value
		setValue( (int) (((Float) value) * 100) );

		// Return percent value as string for progress
		setString(MessageFormat.format("{0,number,#.##%}", value));
		
		return this;
	}

}
