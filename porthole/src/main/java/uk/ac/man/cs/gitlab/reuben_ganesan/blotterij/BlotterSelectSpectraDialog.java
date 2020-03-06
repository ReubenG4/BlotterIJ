package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class  BlotterSelectSpectraDialog extends BlotterSpectraDialog {
	
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
	JButton plotButton;
	FeaturesTableModel spectraTableModel;
	ListSelectionModel spectraListSelectionModel;
	
	
}