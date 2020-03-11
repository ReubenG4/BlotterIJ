package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.scijava.widget.FileWidget;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class  BlotterSelectFileDialog extends BlotterImgDialog {
	
	/*
	 * Declare and initialise class variables
	 */
	ListSelectionModel fileListSelectionModel;
	
	
	/*
	 * Declare JComponents
	 */		
	JPanel confirmPanel;
	JPanel infoPanel;
	JPanel filePanel;	
	JScrollPane tableScroller;
	JTable fileTable;
	JLabel fileName;
	JPanel fileButtons;
	JButton addButton;
	JButton removeButton;
	JButton confirmButton;
	FileTableModel fileTableModel;
	
	/*
	 * Dialog used to select files to load 
	 */
	
	public < T extends RealType<T> & NativeType<T> > BlotterSelectFileDialog() {
		setName("BlotterSelectFile");
		setSize(500, 450);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		
		 fileButtons = new JPanel();
		 addButton = new JButton("Add");
		 removeButton = new JButton("Remove");
		 confirmButton = new JButton("Confirm");
		 confirmPanel = new JPanel();
		 infoPanel = new JPanel();
		 filePanel = new JPanel();
		 fileTableModel = new FileTableModel();
		 fileTable = new JTable(fileTableModel);
		 fileListSelectionModel = fileTable.getSelectionModel();
		 fileListSelectionModel.addListSelectionListener(new FileListSelectionListener());
		 
		 imgData = new ArrayList<ImgWrapper>();
				
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
					MetadataExtractor fileHelper = new MetadataExtractor();
					File fileToAdd;
									
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
					if(fileTableModel.getRowCount() > 2)
						confirmButton.setEnabled(true);			
					
				}
				
			});
			
			fileButtons.add(addButton);
			
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
					if(fileTableModel.getRowCount() < 3)
						confirmButton.setEnabled(false);
									
				}
				
			});
			removeButton.setEnabled(false);
			
			fileButtons.add(removeButton);
			filePanel.add(fileButtons);
		
			
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
					//Clear any presently stored image data
					imgData.clear();
					
					//Retrieve chosen files and store them in imgData
					imgData.addAll(fileTableModel.getData());
					
					//Clear FileTableModel of list entries
					fileTableModel.clear();
					
					//Flag for next state
					setNextState(true);
					
					//SelectDialog set to be no longer visible 
					setVisible(false);
									
				}
				
			});
			
			confirmPanel.add(confirmButton);
			confirmButton.setEnabled(false);
			
		}	
		
	}
	
	class FileListSelectionListener implements ListSelectionListener{
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if(lsm.isSelectionEmpty()) {
				removeButton.setEnabled(false);
			}
			else {
				int choice = fileTable.getSelectedRow();

				if(choice != -1) {	
					removeButton.setEnabled(true);
				}			
				else {
					removeButton.setEnabled(false);
				}
					
					
			}
				
		}
		
	}
	
	
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