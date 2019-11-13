package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class  PortholeBandDialog extends PortholeDialog {
	
	/*
	 * Declare and initialise class variables
	 */
	Vector<String> tableColumnNames = new Vector<String>();
	Vector<Vector<Object>> tableRowData = new Vector<Vector<Object>>();
	List<File> fileList = new LinkedList<File>();
	
	/*
	 * Declare JComponents
	 */		
	JPanel confirmPanel = new JPanel();
	JPanel infoPanel = new JPanel();
	JScrollPane listScroller;
	JTable fileInfoTable;
	JLabel fileName;

	
	public PortholeBandDialog() {
		setName("PortholeBand");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
			
		tableColumnNames.add("Filename");
		tableColumnNames.add("Detected Wavelength");
		
		getContentPane().add(infoPanel,BorderLayout.CENTER);
		{
			
		}
		
		getContentPane().add(confirmPanel,BorderLayout.PAGE_END);
		{
			
		}	
		
	}
	
	/*
	 * Populates fileInfoTable with data from fileList
	 */
	public void populateTable() {
		
		Iterator<File> fileItr = fileList.iterator();
		File currentFile;
		Vector<Object> currentVector;
		
		//Iterate over fileList
		while(fileItr.hasNext()) {
		  currentFile = fileItr.next();
		  currentVector = new Vector<Object>();
		  currentVector.add(currentFile);
		  currentVector.add(new WaveBand(currentFile));			
	      tableRowData.add(currentVector);
		}
		
		
	}
	
	
	
	
	/*
	 * Accessors & Mutators
	 */
	
	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}
	
	public List<File> getFileList(List<File> fileList) {
		return fileList;
	}
}