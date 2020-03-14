package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.ImageProcessor;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class  BlotterSelectSpectraDialog extends BlotterSpectraDialog {
	
	/*
	 * Declare and initialise class variables
	 */
	ArrayList<SpectraData> selectedSpectra;
	ListSelectionModel spectraListSelectionModel;
	
	/*
	 * Declare JComponents
	 */		
	JPanel bottomButtonPanel;
	JPanel centerPanel;
	JPanel topButtonPanel;	
	JScrollPane tableScroller;
	JTable spectraTable;
	JPanel spectraButtons;
	JButton addButton;
	JButton removeButton;
	JButton plotButton;
	JButton eucdButton;
	JButton loadButton;
	JButton saveButton;
	SpectraTableModel spectraTableModel;
	Rectangle selection;
	
	/*
	 * Dialog used to select files to load 
	 */
	
	public < T extends RealType<T> & NativeType<T> > BlotterSelectSpectraDialog() {
		setName("BlotterSelectSpectra");
		setSize(500, 450);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		
		 spectraButtons = new JPanel();
		 addButton = new JButton("Add");
		 removeButton = new JButton("Remove");
		 plotButton = new JButton("Plot");
		 eucdButton = new JButton("Eucd.D");
		 loadButton = new JButton("Load");
		 saveButton = new JButton("Save");
		 bottomButtonPanel = new JPanel();
		 centerPanel = new JPanel();
		 topButtonPanel = new JPanel();
		 spectraTableModel = new SpectraTableModel();
		 spectraTable = new JTable(spectraTableModel);
		 spectraListSelectionModel = spectraTable.getSelectionModel();
		 
		 spectraData = new ArrayList<SpectraData>();
				
		getContentPane().add(centerPanel,BorderLayout.CENTER);
		{			
			spectraTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		    spectraTable.setRowSelectionAllowed(true);
		    spectraTable.setColumnSelectionAllowed(false);
	
		    spectraTable.setDefaultRenderer(File.class, new FileTableCellRenderer());
		    spectraListSelectionModel.addListSelectionListener(new SpectraListSelectionListener());
			
			tableScroller = new JScrollPane(spectraTable);
			spectraTable.setFillsViewportHeight(true);
			centerPanel.add(tableScroller);
		}
		
		getContentPane().add(topButtonPanel, BorderLayout.PAGE_START);
		{
			/*
			 * addButton configuration
			 * adds selected RoI's spectra to list
			 */
			
			addButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					
					/* Retrieve region of interest from imageJ UI */
					
					//Declare and initalise variables
					ImagePlus imp = IJ.getImage();
					ImageProcessor ip = imp.getProcessor();
					Roi roi = imp.getRoi();
					
					//Retrieve image title
					String title = imp.getTitle();
					
					//Check if an area has been selected
					if ((roi==null||!roi.isArea())) {
						IJ.error("Area selection required");
						return;
					}
					
					//Check if the correct image has been selected
					if(title.compareTo("FalseRGB") != 0) {
						IJ.error("Please area select using FalseRGB image");
						return;
					}
					
					//With selection verified, get the rectangle
					selection = roi.getBounds();
					
					//Set flag for next state
					setNextState(true);
					setNextStateIndex(8);
					setVisible(false);
					
				}
				
			});
			
			spectraButtons.add(addButton);
			
			/*
			 * removeButton configuration
			 * removes selected spectra from list when clicked
			 */
		
			removeButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					int index = spectraTable.getSelectedRow();
					
					if(index == -1) {
						IJ.showMessage("Please select a row to delete");
						return;
					}
					
					removeData(index);					
				}
				
			});
			
			removeButton.setEnabled(false);
			
			spectraButtons.add(removeButton);
			topButtonPanel.add(spectraButtons);
		
			
		}
		
		getContentPane().add(bottomButtonPanel,BorderLayout.PAGE_END);
		{
			
			/*
			 * plotButton configuration
			 * plots the selection of spectra
			 */
		
			plotButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					
					//Flag for next state
					setNextState(true);
					
					//Set nextStateIndex
					setNextStateIndex(9);
					
					//SelectDialog set to be no longer visible 
					setVisible(false);
									
				}
				
			});
			
			bottomButtonPanel.add(plotButton);
			plotButton.setEnabled(false);
			
			
			/*
			 * eucdButton configuration
			 * calculates euclidean distance between two spectra
			 */
			eucdButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					
					//Flag for next state
					setNextState(true);
					
					//Set nextStateIndex
					setNextStateIndex(10);
					
					if(selectedSpectra == null)
						selectedSpectra = new ArrayList<SpectraData>();
					else
						selectedSpectra.clear();
					
					//Retrieve chosen files and store them in imgData
					selectedSpectra.addAll(spectraTableModel.getData());
							
					//SelectDialog set to be no longer visible 
					setVisible(false);
									
				}
				
			});
			
			bottomButtonPanel.add(eucdButton);
			eucdButton.setEnabled(false);
			
			/*
			 * loadButton configuration
			 * calculates euclidean distance between two spectra
			 */
			loadButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					//Flag for next state
					setNextState(true);
					
					//Set nextStateIndex
					setNextStateIndex(11);
							
					//SelectDialog set to be no longer visible 
					setVisible(false);
													
				}
				
			});
			
			bottomButtonPanel.add(loadButton);
			loadButton.setEnabled(true);
			
			/*
			 * saveButton configuration
			 */
			saveButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					
					//Flag for next state
					setNextState(true);
					
					//Set nextStateIndex
					setNextStateIndex(12);
							
					//SelectDialog set to be no longer visible 
					setVisible(false);
																			
				}
				
			});
			
			bottomButtonPanel.add(saveButton);
			saveButton.setEnabled(false);
			
		}	
		
	}
	
	
	public void addData(SpectraData input){
		
		spectraTableModel.addRow(input);
		spectraTableModel.sortTable();
		spectraTableModel.fireTableDataChanged();
		resizeColumnWidth();
		
		//If there's there's at least one row available, enable the confirm button
		if(spectraTableModel.getRowCount() > 0)
			plotButton.setEnabled(true);
		
		//If there's at least two rows available, enable eucdButton
		if(spectraTableModel.getRowCount() > 1)
			eucdButton.setEnabled(true);
	}
	
	public void removeData(int index){
		
		spectraTableModel.removeRow(index);	
		spectraTableModel.sortTable();
		spectraTableModel.fireTableDataChanged();
		resizeColumnWidth();
		
		//If there's less than one row available, disable the plot button
		if(spectraTableModel.getRowCount() < 1)
			plotButton.setEnabled(false);
		
		if(spectraTableModel.getRowCount() < 2)
			eucdButton.setEnabled(false);
	}
	
	public ArrayList<SpectraData> getSelectedSpectra() {
		return spectraTableModel.getData();
	}
	
	public Rectangle getSelectedRegion() {
		return selection;
	}
	
	
	/* Listens to changes in spectraTable */
	class SpectraListSelectionListener implements ListSelectionListener{
		
		//Called when value of selection changes
		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			
			if(lsm.isSelectionEmpty()) {
				removeButton.setEnabled(false);
				saveButton.setEnabled(false);
			}
			else {
				int noOfChoices = spectraTable.getSelectedRows().length;
		
				switch(noOfChoices) {
					case 1:
						saveButton.setEnabled(true);
						removeButton.setEnabled(true);
						break;
					
					default:
						saveButton.setEnabled(false);
						removeButton.setEnabled(false);
						break;
				}
								
			}
				
		}
		
	}
	
	/*
	 * Fits columns to width of data
	 * Source: https://stackoverflow.com/questions/17627431/auto-resizing-the-jtable-column-widths
	 */
	public void resizeColumnWidth() {
	    final TableColumnModel columnModel = spectraTable.getColumnModel();
	    for (int column = 0; column < spectraTable.getColumnCount(); column++) {
	        int width = 15; // Min width
	        for (int row = 0; row < spectraTable.getRowCount(); row++) {
	            TableCellRenderer renderer = spectraTable.getCellRenderer(row, column);
	            Component comp = spectraTable.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        if(width > 300)
	            width=300;
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}
	
}