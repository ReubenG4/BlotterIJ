package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;

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

import org.apache.commons.math4.linear.RealVector;

import ij.IJ;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class  BlotterSelectFeatureDialog extends BlotterFeatureDialog {
	
	/*
	 * Declare and initialise class variables
	 */
	ArrayList<PcaFeature> selectedFeatures = new ArrayList<PcaFeature>();
	
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
	JButton renderButton;
	FeaturesTableModel featuresTableModel;
	ListSelectionModel featuresListSelectionModel;
	
	
	
	/*
	 * Dialog used to select feature to display 
	 */
	
	public < T extends RealType<T> & NativeType<T> > BlotterSelectFeatureDialog() {
		setName("BlotterFeatures");
		setSize(500, 450);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		
		
		 renderButton = new JButton("Render");
		 buttonPanel = new JPanel();
		 infoPanel = new JPanel();
		 featuresPanel = new JPanel();
		 featuresTableModel = new FeaturesTableModel();
		 featuresTable = new JTable(featuresTableModel);
		 featuresListSelectionModel =  featuresTable.getSelectionModel();
		 
		 featureData = new ArrayList<PcaFeature>();
		 
		getContentPane().add(infoPanel,BorderLayout.CENTER);
		{			
			featuresTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		    featuresTable.setRowSelectionAllowed(true);
		    featuresTable.setColumnSelectionAllowed(false);
		    featuresTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		    featuresTable.setSize(800,600);
		    featuresTable.setDefaultRenderer(RealVector.class, new FeaturesTableCellRenderer());
		    featuresListSelectionModel.addListSelectionListener(new featuresListSelectionHandler());
		    
			tableScroller = new JScrollPane(featuresTable);
			featuresTable.setFillsViewportHeight(true);
			infoPanel.add(tableScroller);
		}
		
		getContentPane().add(buttonPanel,BorderLayout.PAGE_END);
		{
			
			/*
			 * confirmButton configuration
			 * confirms selected features
			 */
		
			renderButton.addActionListener(new ActionListener(){

				//If confirm is clicked and selection is valid
				@Override
				public void actionPerformed(final ActionEvent arg0) {
					setNextState(true);
					setVisible(false);
				}
				
			});
			
			buttonPanel.add(renderButton);
			renderButton.setEnabled(false);
			
		}	
		
	}
	
	public void prepareForDisplay() {
		featureData.sort(new featureComparator());
		featuresTableModel.addData(featureData);
		featuresTableModel.fireTableDataChanged();
		resizeColumnWidth();
		pack();
	}
	
	public ArrayList<PcaFeature> getSelected() {
		selectedFeatures.sort(new featureComparator());
		return selectedFeatures;
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

	class featuresListSelectionHandler implements ListSelectionListener{
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			if(lsm.isSelectionEmpty()) {
				renderButton.setEnabled(false);
			}
			else {
				selectedFeatures.clear();
				int[] choices = featuresTable.getSelectedRows();
				if(choices.length > 0) {
					for(int index=0; index < choices.length; index++)
						selectedFeatures.add(featureData.get(choices[index]));
				}
				renderButton.setEnabled(true);
			}
				
		}
		
	}
	
	class featureComparator implements Comparator<PcaFeature>{
	
		@Override
		public int compare(PcaFeature o1, PcaFeature o2) {
			double o1Value = o1.getValue();
			double o2Value = o2.getValue();
			
			return Double.compare(o2Value, o1Value);
		}
		
	}

	
}