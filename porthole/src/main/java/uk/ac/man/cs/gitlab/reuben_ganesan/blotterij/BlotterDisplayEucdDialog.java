package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class BlotterDisplayEucdDialog extends BlotterDialog{
	
	/*
	 * Declare JComponents
	 */		
	JTable eucdTable;
	JPanel infoPanel;
	JScrollPane tableScroller;
	EucdTableModel eucdTableModel;
	
	/*
	 * Dialog used to display Euclidean Distance as table
	 */
	public < T extends RealType<T> & NativeType<T> > BlotterDisplayEucdDialog(ArrayList<String> rowColNames, ArrayList<double[]> data) {
		setName("BlotterShowEucd");
		setSize(500, 450);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		
		eucdTableModel = new EucdTableModel(rowColNames,data);
		eucdTable = new JTable(eucdTableModel);
		infoPanel = new JPanel();
		
		getContentPane().add(infoPanel,BorderLayout.CENTER);
		{			
		    eucdTable.setRowSelectionAllowed(false);
		    eucdTable.setColumnSelectionAllowed(false);
			tableScroller = new JScrollPane(eucdTable);
			eucdTable.setFillsViewportHeight(true);
			infoPanel.add(tableScroller);
		}
	}
	
	/*
	 * Fits columns to width of data
	 * Source: https://stackoverflow.com/questions/17627431/auto-resizing-the-jtable-column-widths
	 */
	public void resizeColumnWidth() {
	    final TableColumnModel columnModel = eucdTable.getColumnModel();
	    for (int column = 0; column < eucdTable.getColumnCount(); column++) {
	        int width = 15; // Min width
	        for (int row = 0; row < eucdTable.getRowCount(); row++) {
	            TableCellRenderer renderer = eucdTable.getCellRenderer(row, column);
	            Component comp = eucdTable.prepareRenderer(renderer, row, column);
	            width = Math.max(comp.getPreferredSize().width +1 , width);
	        }
	        if(width > 300)
	            width=300;
	        columnModel.getColumn(column).setPreferredWidth(width);
	    }
	}
}