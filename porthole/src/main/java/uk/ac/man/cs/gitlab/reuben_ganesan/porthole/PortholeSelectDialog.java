package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.scijava.widget.FileWidget;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.ActionEvent;

/* Invoked by porthole command if images are to be selected */
public class PortholeSelectDialog extends PortholeDialog {

	/**
	 * Declare and initialise class variables
	 */	
	private JPanel buttonPanel = new JPanel();
	private JPanel dataPanel = new JPanel();
	private JPanel confirmPanel = new JPanel();
	private DefaultListModel<File> imageListModel = new DefaultListModel<File>();
	private JList<File> imageList = new JList<File>(imageListModel);

	/**
	 * Create the dialog.
	 */
	public PortholeSelectDialog() {	
		setName("Porthole");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		buttonPanel.setLayout(new FlowLayout());
		
		/*
		 * Declare JComponents
		 */
		JScrollPane listScroller;
		JToolBar fileBar = new JToolBar("");
		JButton addButton = new JButton("Add");
		JButton removeButton = new JButton("Remove");
		JButton confirmButton = new JButton("Confirm");
				
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
				
		getContentPane().add(buttonPanel, BorderLayout.PAGE_START);
		{
						
			/*
			 * addButton configuration
			 * calls UI to prompt user to choose a file
			 * adds chosen file to list
			 */
			
			addButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					
				    List<File>initialValue = new LinkedList<File>();
					List<File>inputList = getUi().chooseFiles(null , initialValue, new ImageFileFilter(), FileWidget.OPEN_STYLE);
					if(inputList == null) {
						return;
					}
					Iterator<File> fileItr = inputList.iterator();
							
					while (fileItr.hasNext()) {
						imageListModel.addElement(fileItr.next());
					}
					
					if(imageListModel.size() > 0)
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
					int index = imageList.getSelectedIndex();
					imageListModel.remove(index);	
					
					if (imageListModel.size() < 1)
						confirmButton.setEnabled(false);
				}
			});			
			fileBar.add(removeButton);	
			removeButton.setEnabled(false);
			
			/*
			 * ListSelectionListener
			 * Determines if remove button should be enabled 
			 */			
			imageList.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					
					 if (e.getValueIsAdjusting() == false) {
						 
						 if (imageList.getSelectedIndex() == -1)
							 removeButton.setEnabled(false);
						 else
							 removeButton.setEnabled(true);

					 }
				}    	
		    		    	
		    });
						
			fileBar.setFloatable(false);
	        fileBar.setRollover(true);		
	        
			buttonPanel.add(fileBar);
		}
				
		getContentPane().add(confirmPanel, BorderLayout.PAGE_END);
		{
			/*
			 * confirmButton configuration
			 * confirms the selection of files
			 */
			confirmButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					
					int lastIndex = imageListModel.getSize();
					for(int i=0; i <lastIndex; i++) {
						getFileList().add(imageListModel.get(i));
					}
					
					setNextState(true);
					setVisible(false);
					dispatchEvent(new WindowEvent(PortholeSelectDialog.this,WindowEvent.WINDOW_CLOSING));
					    					
				}
				
			});
			
			confirmPanel.add(confirmButton);
			confirmButton.setEnabled(false);
		}
	}
	
	public static void main(final String[] args) {
		try {
			final PortholeSelectDialog dialog = new PortholeSelectDialog();
			//dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}




	

}




