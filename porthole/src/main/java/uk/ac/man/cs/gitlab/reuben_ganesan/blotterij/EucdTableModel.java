package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/*
 * Table Model for PortholeSelectDialog fileTable
 */
class EucdTableModel extends AbstractTableModel {
    
    //Vectors of data objects
	private ArrayList<String> rowColNames = new ArrayList<String>();
    private ArrayList<double[]> data = new ArrayList<double[]>(); 
    
    
    public EucdTableModel(ArrayList<String> rowColNames, ArrayList<double[]> data) {
    	this.rowColNames.addAll(rowColNames);
		this.data.addAll(data);
    }
    
    public ArrayList<double[]> getData() {
    	return data;
    }
    
    public void addRow(String name, double[] eucdValues) {
    	rowColNames.add(name);
    	data.add(eucdValues);
    }
    
    public void removeRow(int row) {
    	data.remove(row);
    }
    
    public void clear() {
    	data.clear();
    }
    
    public int getColumnCount() {
        return rowColNames.size();
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return rowColNames.get(col);
    }
    
    public String getRowName(int row) {
    	 return rowColNames.get(row);
    }

    //First column displays names
    //Every subsequent column display data
    public Object getValueAt(int row, int col) {
    	
    	if(col==0)
    		return rowColNames.get(row + 1);
    	
    	return data.get(row)[col - 1];          
    }
    
    //Class of object in first column is String
    //Class of object in every following column is double
    public Class<? extends Object> getColumnClass(int c) {
    	if(c == 0)
    		return getValueAt(0,c).getClass();
    	else
    		return getValueAt(1,c).getClass();
    }
    
}