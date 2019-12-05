package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.scijava.widget.FileWidget;

import net.imagej.Dataset;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class  PortholeSelectDialog extends PortholeDialog {
	
	/*
	 * Declare and initialise class variables
	 */
	
	
	/*
	 * Declare JComponents
	 */		
	JPanel confirmPanel;
	JPanel infoPanel;
	JPanel filePanel;	
	JScrollPane tableScroller;
	JTable fileTable;
	JLabel fileName;
	JToolBar fileBar;
	JButton addButton;
	JButton removeButton;
	JButton confirmButton;
	FileTableModel fileTableModel;
	
	

	public PortholeSelectDialog() {
		setName("PortholeSelect");
		setBounds(100, 100, 500, 450);
		getContentPane().setLayout(new BorderLayout());
		
		 fileBar = new JToolBar("");
		 addButton = new JButton("Add");
		 removeButton = new JButton("Remove");
		 confirmButton = new JButton("Confirm");
		 confirmPanel = new JPanel();
		 infoPanel = new JPanel();
		 filePanel = new JPanel();
		 fileTableModel = new FileTableModel();
		 fileTable = new JTable(fileTableModel);
				
		getContentPane().add(infoPanel,BorderLayout.CENTER);
		{			
			fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    fileTable.setRowSelectionAllowed(true);
		    fileTable.setColumnSelectionAllowed(false);
			fileTable.setDefaultRenderer(File.class, new FileTableCellRenderer());
			
			tableScroller = new JScrollPane(fileTable);
			fileTable.setFillsViewportHeight(true);
			infoPanel.add(tableScroller);
		}
		
		getContentPane().add(filePanel, BorderLayout.PAGE_START);
		{
			/*
			 * addButton configuration
			 * calls UI to prompt user to choose a file
			 * adds chosen file to list
			 */
			
			addButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					
					//Retrieve value from ui chooseFiles
				    List<File>initialValue = new LinkedList<File>();
					List<File>inputList = getUIService().chooseFiles(null , initialValue, new ImageFileFilter(), FileWidget.OPEN_STYLE);
					if(inputList == null) {
						return;
					}
					
					//Iterate through list of chosen files
					Iterator<File> fileItr = inputList.iterator();						
					FileHelper fileHelper = new FileHelper();
					File fileToAdd;
					Vector<Object> rowToAdd;
									
					//While iterator hasNext, add it as a row to table
					while (fileItr.hasNext()) {
						fileToAdd = fileItr.next();
						fileHelper.setFilename(fileToAdd.getName());
						fileTableModel.addRow(fileToAdd, 
								fileHelper.getWavelength(), 
								fileHelper.getType());
					}
					fileTableModel.sortTable();
					fileTableModel.fireTableDataChanged();
					resizeColumnWidth();
					
					//If there's more than one row available, enable the confirm button
					if(fileTableModel.getRowCount() > 0)
						confirmButton.setEnabled(true);			
					
				}
				
			});
			
			fileBar.add(addButton);
			
			/*
			 * removeButton configuration
			 * removes selected file from list when clicked
			 */
		
			removeButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					int index = fileTable.getSelectedRow();
					fileTableModel.removeRow(index);	
					fileTableModel.sortTable();
					fileTableModel.fireTableDataChanged();
					resizeColumnWidth();
					
					//If there's less than one row available, disable the confirm button
					if(fileTableModel.getRowCount() < 1)
						confirmButton.setEnabled(false);
									
				}
				
			});
			
			fileBar.add(removeButton);
			filePanel.add(fileBar);
		
			
		}
		
		getContentPane().add(confirmPanel,BorderLayout.PAGE_END);
		{
			
			/*
			 * confirmButton configuration
			 * confirms the selection of files
			 */
		
			confirmButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					setVisible(false);
					Vector<FileWaveType> tableData = fileTableModel.getData();
					Iterator<FileWaveType> tableItr = tableData.iterator();
					Vector<Dataset> datasets = new Vector<Dataset>();
					FileWaveType currentFile;
					
					while(tableItr.hasNext()) {
						currentFile = tableItr.next();
						try {
							datasets.add(getDatasetIOService().open(currentFile.file.getPath()));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					Iterator<Dataset> dataItr = datasets.iterator();
					
					while(dataItr.hasNext())
					  getUIService().show(dataItr.next());			
				}
				
			});
			
			confirmPanel.add(confirmButton);
			confirmButton.setEnabled(false);
			
		}	
		
	}
	
	
	/*
	 * Accessors & Mutators
	 */	
	
	
	
	/*
	 * Fits columns to width of data
	 * Source: https://stackoverflow.com/questions/17627431/auto-resizing-the-jtable-column-widths
	 */
	public void resizeColumnWidth() {
	    final TableColumnModel columnModel = fileTable.getColumnModel();
	    for (int column = 0; column < fileTable.getColumnCount(); column++) {
	        int width = 15; // Min width
	        for (int row = 0; row < fileTable.getRowCount(); row++) {
	            TableCellRenderer renderer = fileTable.getCellRenderer(row, column);
	            Component comp = fileTable.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        if(width > 300)
	            width=300;
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}
	
}