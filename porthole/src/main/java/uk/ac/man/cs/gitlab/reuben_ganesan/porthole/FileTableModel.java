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
    private Vector<ImgWaveType> data = new Vector<ImgWaveType>(); 
    
    public FileTableModel() {
    	
    }
    
    //Sorts the table in ascending order of wavelength
    public void sortTable() {
    	Comparator<ImgWaveType> c = new Comparator<ImgWaveType>() {
    		@Override
    		public int compare(ImgWaveType o1, ImgWaveType o2) {
    			return o1.getWavelength() - o2.getWavelength();
    			}
    	};
    	
    	data.sort(c);
    }
    
    public Vector<ImgWaveType> getData() {
    	return data;
    }
    
    public void addRow(File file, int wavelength, char type) {
    	data.add(new ImgWaveType(file,wavelength,type));
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
    	ImgWaveType iwt = data.get(row);
    	       	
    	if(col == 0)
    		return (Object)iwt.getFile();
    	
    	if(col == 1)
    		return (Object)iwt.getWavelength();
    	
    	if(col == 2)
    		return (Object)iwt.getType();
    	
    	return null;          
    }

    public Class<? extends Object> getColumnClass(int c) {
        return getValueAt(0,c).getClass();
    }
    
}