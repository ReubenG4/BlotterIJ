package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.io.File;

import javax.swing.table.DefaultTableCellRenderer;

class FileTableCellRenderer extends DefaultTableCellRenderer{
	
	public FileTableCellRenderer() {super();}
	
	public void setValue(Object value) {
		
		File file = (File)value;
		
		setText(file.getName());
	}
	
}