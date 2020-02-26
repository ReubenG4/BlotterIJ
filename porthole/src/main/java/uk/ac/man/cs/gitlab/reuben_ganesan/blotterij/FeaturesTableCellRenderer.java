package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.io.File;

import javax.swing.table.DefaultTableCellRenderer;

class FeaturesTableCellRenderer extends DefaultTableCellRenderer{
	
	public FeaturesTableCellRenderer() {super();}
	
	public void setValue(Object value) {
		
		File file = (File)value;
		
		setText(file.getName());
	}
	
}