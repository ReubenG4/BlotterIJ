package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.math4.linear.RealVector;
import org.apache.commons.math4.linear.RealVectorFormat;

/*
 * Table Model for PCASelectDialog fileTable
 */
class FeaturesTableModel extends AbstractTableModel {
    private String[] columnNames = {"N","Eigenvector",
                                    "Eigenalue",};
    
    //Vector of FileWaveType objects
    private ArrayList<PcaFeature> data = new ArrayList<PcaFeature>(); 
    
    public FeaturesTableModel() {
    	
    }
    
    //Sorts the table in ascending order of eigenvaluevalue
    public void sortTable() {
    	Comparator<PcaFeature> c = new Comparator<PcaFeature>() {
    		@Override
    		public int compare(PcaFeature o1, PcaFeature o2) {
    			
    			double o1Value = o1.getValue();
    			double o2Value = o2.getValue();
    			
    			return Double.compare(o2Value, o1Value);
    			}
    	};
    	
    	data.sort(c);
    }
    
    public ArrayList<PcaFeature> getData() {
    	return data;
    }
    
    public void addData(ArrayList<PcaFeature> data){
    	Iterator<PcaFeature> itr = data.iterator();
    	int index = 1;
    		
    	//Assign each an index
    	while(itr.hasNext()) {
    		
    		PcaFeature itrFeature = itr.next();
    		itrFeature.setIndex(index++);
    		addRow(itrFeature);
    		
    	}	
    }
    
    public void addRow(PcaFeature input) {
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
    	PcaFeature iwt = data.get(row);
    	       	
    	if(col == 0) 
    		return (Object)iwt.getIndex();
    		
    	if(col == 1) 
    		return (Object)iwt.getVector();
    	
    	if(col == 2)
    		return (Object)iwt.getValue();
    		
    	return null;          
    }

    public Class<? extends Object> getColumnClass(int c) {
        return getValueAt(0,c).getClass();      
    }
    
}