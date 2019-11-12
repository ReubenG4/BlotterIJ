package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/* Classifies images into different wavelength bands */
public class PortholeClassifyDialog extends PortholeDialog {

	/**
	 * Declare and initialise class variables
	 */
	private JPanel dataPanel = new JPanel();
	private JPanel confirmPanel = new JPanel();
	private JPanel infoPanel = new JPanel();
	private DefaultListModel<File> imageListModel = new DefaultListModel<File>();
	private JList<File> imageList = new JList<File>(imageListModel);

	/**
	 * Create the dialog.
	 */
	public PortholeClassifyDialog() {
		setName("PortholeClassify");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		dataPanel.setLayout(new FlowLayout());
		confirmPanel.setLayout(new FlowLayout());
	    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
		
		/*
		 * Declare JComponents
		 */
		JScrollPane listScroller;
		JButton confirmButton = new JButton("Confirm");
		JLabel stackName = new JLabel();
		JLabel fileName = new JLabel();
		JLabel fileBand = new JLabel();
		
		getContentPane().add(dataPanel, BorderLayout.CENTER);
		{
			/*
			 * imageList configuration
			 * list to display chosen files
			 */
			imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			imageList.setLayoutOrientation(JList.VERTICAL);
			imageList.setVisibleRowCount(-1);
			imageList.setCellRenderer(new FileCellRenderer());
		    		
			listScroller = new JScrollPane(imageList);
			listScroller.setPreferredSize(new Dimension(380, 200));
					
			dataPanel.add(listScroller);
		}
				
		
		getContentPane().add(infoPanel, BorderLayout.EAST);
		{
			
			/*
			 * infoPanel configuration
			 * Displays info about selected file
			 * stackName: name of the image stack to-be-returned from this dialog
			 * fileName: filename
			 * fileBand: wavelength depicted by the image
			 */
			stackName.setAlignmentX(Component.LEFT_ALIGNMENT);
			fileName.setAlignmentX(Component.LEFT_ALIGNMENT);
			fileBand.setAlignmentX(Component.LEFT_ALIGNMENT);
						
			infoPanel.add(stackName);
			infoPanel.add(fileName);
			infoPanel.add(fileBand);
			
		}
		
		getContentPane().add(confirmPanel, BorderLayout.SOUTH);
		{
			confirmPanel.add(confirmButton);
		}
	}

	



	

}
