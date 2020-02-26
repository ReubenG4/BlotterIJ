package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.math4.linear.RealVector;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class  BlotterSelectFeatureDialog extends BlotterFeatureDialog {
	
	/*
	 * Declare and initialise class variables
	 */
	
	
	/*
	 * Declare JComponents
	 */		
	JPanel buttonPanel;
	JPanel infoPanel;
	JPanel featuresPanel;	
	JScrollPane tableScroller;
	JTable featuresTable;
	JLabel fileName;
	JPanel fileButtons;
	JButton displayButton;
	FeaturesTableModel featuresTableModel;
	
	/*
	 * Dialog used to select feature to display 
	 */
	
	public < T extends RealType<T> & NativeType<T> > BlotterSelectFeatureDialog() {
		setName("BlotterFeatures");
		setSize(500, 450);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		
		
		 displayButton = new JButton("Display");
		 buttonPanel = new JPanel();
		 infoPanel = new JPanel();
		 featuresPanel = new JPanel();
		 featuresTableModel = new FeaturesTableModel();
		 featuresTable = new JTable(featuresTableModel);
		 
		 featureData = new ArrayList<PcaFeature>();
		 
		getContentPane().add(infoPanel,BorderLayout.CENTER);
		{			
			featuresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    featuresTable.setRowSelectionAllowed(true);
		    featuresTable.setColumnSelectionAllowed(false);
		    featuresTable.setDefaultRenderer(RealVector.class, new FeaturesTableCellRenderer());
			
			tableScroller = new JScrollPane(featuresTable);
			featuresTable.setFillsViewportHeight(true);
			infoPanel.add(tableScroller);
		}
		
		getContentPane().add(buttonPanel,BorderLayout.PAGE_END);
		{
			
			/*
			 * displayButton configuration
			 * displays selected feature
			 */
		
			displayButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
								
					
									
				}
				
			});
			
			buttonPanel.add(displayButton);
			displayButton.setEnabled(false);
			
		}	
		
	}
	
	public void prepareForDisplay() {
		featuresTableModel.addData(featureData);
		featuresTableModel.sortTable();
		featuresTableModel.fireTableDataChanged();
		resizeColumnWidth();
		
		//If there's more than one row available, enable the confirm button
		if(featuresTableModel.getRowCount() > 2)
			displayButton.setEnabled(true);	
	}
	
	
	/*
	 * Fits columns to width of data
	 * Source: https://stackoverflow.com/questions/17627431/auto-resizing-the-jtable-column-widths
	 */
	public void resizeColumnWidth() {
	    final TableColumnModel columnModel = featuresTable.getColumnModel();
	    for (int column = 0; column < featuresTable.getColumnCount(); column++) {
	        int width = 15; // Min width
	        for (int row = 0; row < featuresTable.getRowCount(); row++) {
	            TableCellRenderer renderer = featuresTable.getCellRenderer(row, column);
	            Component comp = featuresTable.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        if(width > 300)
	            width=300;
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}

	
}