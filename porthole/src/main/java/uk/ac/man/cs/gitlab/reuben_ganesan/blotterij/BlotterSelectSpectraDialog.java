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
	ArrayList<SpectraData> spectraData;
	
	/*
	 * Declare JComponents
	 */		
	JPanel confirmPanel;
	JPanel infoPanel;
	JPanel spectraPanel;	
	JScrollPane tableScroller;
	JTable spectraTable;
	JPanel spectraButtons;
	JButton addButton;
	JButton removeButton;
	JButton confirmButton;
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
		 confirmButton = new JButton("Confirm");
		 confirmPanel = new JPanel();
		 infoPanel = new JPanel();
		 spectraPanel = new JPanel();
		 spectraTableModel = new SpectraTableModel();
		 spectraTable = new JTable(spectraTableModel);
		 
		 spectraData = new ArrayList<SpectraData>();
				
		getContentPane().add(infoPanel,BorderLayout.CENTER);
		{			
			spectraTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    spectraTable.setRowSelectionAllowed(true);
		    spectraTable.setColumnSelectionAllowed(false);
			spectraTable.setDefaultRenderer(File.class, new FileTableCellRenderer());
			
			tableScroller = new JScrollPane(spectraTable);
			spectraTable.setFillsViewportHeight(true);
			infoPanel.add(tableScroller);
		}
		
		getContentPane().add(spectraPanel, BorderLayout.PAGE_START);
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
					spectraTableModel.removeRow(index);	
					spectraTableModel.sortTable();
					spectraTableModel.fireTableDataChanged();
					resizeColumnWidth();
					
					//If there's less than one row available, disable the confirm button
					if(spectraTableModel.getRowCount() < 3)
						confirmButton.setEnabled(false);
									
				}
				
			});
			
			spectraButtons.add(removeButton);
			spectraPanel.add(spectraButtons);
		
			
		}
		
		getContentPane().add(confirmPanel,BorderLayout.PAGE_END);
		{
			
			/*
			 * confirmButton configuration
			 * confirms the selection of spectra
			 */
		
			confirmButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					//Clear any presently stored image data
					spectraData.clear();
					
					//Retrieve chosen files and store them in spectraData
					spectraData.addAll(spectraTableModel.getData());
					
					//Clear SpectraTableModel of list entries
					spectraTableModel.clear();
					
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
	
	
	public void addData(SpectraData input){
		
		spectraTableModel.addRow(input);
		spectraTableModel.sortTable();
		spectraTableModel.fireTableDataChanged();
		resizeColumnWidth();
		
		//If there's more than one row available, enable the confirm button
		if(spectraTableModel.getRowCount() > 0)
			confirmButton.setEnabled(true);			
	}
	
	public ArrayList<SpectraData> getData() {
		return spectraTableModel.getData();
	}
	
	public Rectangle getSelection() {
		return selection;
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