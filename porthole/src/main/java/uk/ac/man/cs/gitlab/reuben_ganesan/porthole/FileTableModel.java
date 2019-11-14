package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.io.File;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

//Table model for display
class FileTableModel extends AbstractTableModel {
    private String[] columnNames = {"Filename",
                                    "Wavelength",
                                    "Type"};
    
    //Vector of FileWaveType objects
    private Vector<FileWaveType> data = new Vector<FileWaveType>(); 
    
    public FileTableModel() {
    	
    }
    
    //Sorts the table in ascending order of wavelength
    public void sortTable() {
    	Comparator<FileWaveType> c = new Comparator<FileWaveType>() {
    		@Override
    		public int compare(FileWaveType o1, FileWaveType o2) {
    			return o1.wavelength - o2.wavelength;
    			}
    	};
    	
    	data.sort(c);
    }
    
    public Vector<FileWaveType> getData() {
    	return data;
    }
    
    public void addRow(File file, int wavelength, char type) {
    	data.add(new FileWaveType(file,wavelength,type));
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
    	FileWaveType fwt = data.get(row);
    	       	
    	if(col == 0)
    		return (Object)fwt.file;
    	
    	if(col == 1)
    		return (Object)fwt.wavelength;
    	
    	if(col == 2)
    		return (Object)fwt.type;
    	
    	return null;          
    }

    public Class<? extends Object> getColumnClass(int c) {
        return getValueAt(0,c).getClass();
    }
    
}