package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/*
 * Table Model for PortholeSelectDialog fileTable
 */
class FileTableModel extends AbstractTableModel {
    private String[] columnNames = {"Filename",
                                    "Wavelength",
                                    "Type"};
    
    //Vector of FileWaveType objects
    private ArrayList<ImgPlusMeta> data = new ArrayList<ImgPlusMeta>(); 
    
    public FileTableModel() {
    	
    }
    
    //Sorts the table in ascending order of wavelength
    public void sortTable() {
    	Comparator<ImgPlusMeta> c = new Comparator<ImgPlusMeta>() {
    		@Override
    		public int compare(ImgPlusMeta o1, ImgPlusMeta o2) {
    			return o1.getWavelength() - o2.getWavelength();
    			}
    	};
    	
    	data.sort(c);
    }
    
    public ArrayList<ImgPlusMeta> getData() {
    	return data;
    }
    
    public void addRow(File file, int wavelength, char type) {
    	data.add(new ImgPlusMeta(file,wavelength,type));
    }
    
    public void removeRow(int row) {
    	data.remove(row);
    }
    
    public void clear() {
    	data.clear();
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
    	ImgPlusMeta iwt = data.get(row);
    	       	
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