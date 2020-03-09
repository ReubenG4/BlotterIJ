package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/*
 * Table Model for SelectSpectraDialog fileTable
 */
class SpectraTableModel extends AbstractTableModel {
    private String[] columnNames = {"Name", "Location", "Size", "Wavelengths"};
    
    //ArrayList of SpectraData objects
    private ArrayList<SpectraData> data = new ArrayList<SpectraData>(); 
    
    int noOfRows = 0;
    
    public SpectraTableModel() {
    	
    }
    
    public void sortTable() {
    	
    }
    
    public ArrayList<SpectraData> getData() {
    	return data;
    }
    
    public void addRow(SpectraData input) {
  
    	input.setName("Region "+ (++noOfRows) );
    	
    	data.add(input);
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
    		
    	return null;          
    }

    public Class<? extends Object> getColumnClass(int c) {
        return getValueAt(0,c).getClass();      
    }

    
}