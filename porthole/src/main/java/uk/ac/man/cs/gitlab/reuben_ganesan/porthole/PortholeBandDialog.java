package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class  PortholeBandDialog extends PortholeDialog {
	
	/*
	 * Declare and initialise class variables
	 */
	List<File> fileList = new LinkedList<File>();
	
	/*
	 * Declare JComponents
	 */		
	JPanel confirmPanel = new JPanel();
	JPanel infoPanel = new JPanel();
	JScrollPane tableScroller;
	JTable bandTable;
	JLabel fileName;
	BandTableModel bandTableModel;

	public PortholeBandDialog() {
		setName("PortholeBand");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		
		
		getContentPane().add(infoPanel,BorderLayout.CENTER);
		{
			bandTableModel = new BandTableModel();
			bandTable = new JTable(bandTableModel);
			bandTable.setDefaultRenderer(File.class, new FileTableCellRenderer());
			tableScroller = new JScrollPane(bandTable);
			bandTable.setFillsViewportHeight(true);
			infoPanel.add(tableScroller);
		}
		
		getContentPane().add(confirmPanel,BorderLayout.PAGE_END);
		{
			
		}	
		
	}
	
	
	/*
	 * Accessors & Mutators
	 */	
	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}
	
	public List<File> getFileList() {
		return this.fileList;
	}
	
	/*
	 * Adds data from file list to rows
	 */
	public void refreshRows() {
		//Iterate through list of chosen files
		Iterator<File> fileItr = fileList.iterator();
		FileHelper helper = new FileHelper();
		
		while (fileItr.hasNext()) {
			File fileToAdd = fileItr.next();
			Vector<Object> rowToAdd = new Vector<Object>();
			rowToAdd.add(fileToAdd);
			rowToAdd.add(helper.getWavelength(fileToAdd));
			rowToAdd.add(helper.getType(fileToAdd));
			bandTableModel.addRow(rowToAdd);
		}
	}
	
	/*
	 * Fits columns to width of data
	 * Source: https://stackoverflow.com/questions/17627431/auto-resizing-the-jtable-column-widths
	 */
	public void resizeColumnWidth() {
	    final TableColumnModel columnModel = bandTable.getColumnModel();
	    for (int column = 0; column < bandTable.getColumnCount(); column++) {
	        int width = 15; // Min width
	        for (int row = 0; row < bandTable.getRowCount(); row++) {
	            TableCellRenderer renderer = bandTable.getCellRenderer(row, column);
	            Component comp = bandTable.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        if(width > 300)
	            width=300;
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}
	
	
	class BandTableModel extends AbstractTableModel {
        private String[] columnNames = {"Filename",
                                        "Wavelength",
                                        "Type"};
        
        private Vector<Vector<Object>> data = new Vector<Vector<Object>>(); 
        
        public BandTableModel() {
        	
        }
        
        public void addRow(Vector<Object> rowData) {
        	data.add(rowData);
        }
        
        public void removeRow(int row) {
        	data.remove(row);
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }
 
        public int getRowCount() {
            return data.size();
        }
 
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        public Object getValueAt(int row, int col) {
            return data.get(row).get(col);
        }
 
        public Class<? extends Object> getColumnClass(int c) {
            return getValueAt(0,c).getClass();
        }
        
	}
}