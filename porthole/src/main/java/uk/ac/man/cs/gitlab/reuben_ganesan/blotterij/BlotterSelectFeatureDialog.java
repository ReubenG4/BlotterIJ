package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.commons.math4.linear.RealVector;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class  BlotterSelectFeatureDialog extends BlotterFeatureDialog {
	
	/*
	 * Declare and initialise class variables
	 */
	PcaFeature selectedFeature;
	
	/*
	 * Declare JComponents
	 */		
	JPanel buttonPanel;
	JPanel centerPanel;
	JScrollPane tableScroller;
	JTable featuresTable;
	JButton renderButton;
	JButton plotButton;
	FeaturesTableModel featuresTableModel;
	ListSelectionModel featuresListSelectionModel;
	
	/*
	 * Dialog used to select feature to display 
	 */
	
	public < T extends RealType<T> & NativeType<T> > BlotterSelectFeatureDialog() {
		setName("BlotterSelectFeatures");
		setSize(500, 450);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		
		renderButton = new JButton("Render");
		plotButton = new JButton("Plot");
		buttonPanel = new JPanel();
		centerPanel = new JPanel();
		featuresTableModel = new FeaturesTableModel();
		featuresTable = new JTable(featuresTableModel);
		featuresListSelectionModel =  featuresTable.getSelectionModel();
		 
		featureData = new ArrayList<PcaFeature>();
		 
		getContentPane().add(centerPanel,BorderLayout.CENTER);
		{			
			featuresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		    featuresTable.setRowSelectionAllowed(true);
		    featuresTable.setColumnSelectionAllowed(false);
		    featuresTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		    featuresTable.setSize(800,600);
		    featuresTable.setDefaultRenderer(RealVector.class, new FeaturesTableCellRenderer());
		    featuresListSelectionModel.addListSelectionListener(new FeaturesListSelectionListener());
		    
			tableScroller = new JScrollPane(featuresTable);
			featuresTable.setFillsViewportHeight(true);
			centerPanel.add(tableScroller);
		}
		
		getContentPane().add(buttonPanel,BorderLayout.PAGE_END);
		{
			
			/*
			 * renderButton configuration
			 * renders selected feature
			 */
		
			renderButton.addActionListener(new ActionListener(){

				//If confirm is clicked and selection is valid
				@Override
				public void actionPerformed(final ActionEvent arg0) {
					setNextState(true);
					setNextStateIndex(6);
					setVisible(false);
				}
				
			});
			
			buttonPanel.add(renderButton);
			renderButton.setEnabled(false);
	
		}	
		
	}
	
	public void prepareForDisplay() {
		featuresTableModel.clear();
		featureData.sort(new FeatureComparator());
		featuresTableModel.addData(featureData);
		featuresTableModel.fireTableDataChanged();
		resizeColumnWidth();
		pack();
	}
	
	public PcaFeature getSelected() {
		return selectedFeature;
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

	class FeaturesListSelectionListener implements ListSelectionListener{
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if(lsm.isSelectionEmpty()) {
				renderButton.setEnabled(false);
				plotButton.setEnabled(false);
			}
			else {
				int choice = featuresTable.getSelectedRow();

				if(choice != -1) {
					selectedFeature = featureData.get(choice);
					renderButton.setEnabled(true);
					plotButton.setEnabled(true);
				}
					
				else {
					selectedFeature = null;
					renderButton.setEnabled(false);
					plotButton.setEnabled(false);
				}
					
					
			}
				
		}
		
	}
	
	class FeatureComparator implements Comparator<PcaFeature>{
	
		@Override
		public int compare(PcaFeature o1, PcaFeature o2) {
			double o1Value = o1.getValue();
			double o2Value = o2.getValue();
			
			return Double.compare(o2Value, o1Value);
		}
		
	}

	
}