package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.io.File;

import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.math4.linear.RealVector;

class FeaturesTableCellRenderer extends DefaultTableCellRenderer{
	
	public FeaturesTableCellRenderer() {super();}
	
	public void setValue(Object value) {
		
		RealVector vector = (RealVector)value;
		setText(vector.toString());
	}
	
}