package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.BorderLayout;
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
		
	public void refreshRows() {
		//Iterate through list of chosen files
		Iterator<File> fileItr = fileList.iterator();							
		while (fileItr.hasNext()) {
			File fileToAdd = fileItr.next();
			Vector<Object> rowToAdd = new Vector<Object>();
			rowToAdd.add(fileToAdd);
			rowToAdd.add(100);
			rowToAdd.add("N");
			bandTableModel.addRow(rowToAdd);
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