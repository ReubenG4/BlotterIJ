package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/*
 * Table Model for SelectSpectraDialog fileTable
 */
class SpectraTableModel extends AbstractTableModel {
    private String[] columnNames = {"Name","Location",
                                    "Size",};
    
    //ArrayList of SpectraData objects
    private ArrayList<SpectraData> data = new ArrayList<SpectraData>(); 
    
    public SpectraTableModel() {
    	
    }
    
    //Sorts the table in ascending order of eigenvaluevalue
    public void sortTable() {
    	
    }
    
    public ArrayList<SpectraData> getData() {
    	return data;
    }
    
    public void addData(ArrayList<PcaFeature> data){
    	
    }
    
    public void addRow(SpectraData input) {
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