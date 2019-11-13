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

import io.scif.img.ImgIOException;
import net.imglib2.img.Img;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.ActionEvent;

/* Invoked by porthole command if images are to be selected */
public class  PortholeSelectDialog extends PortholeDialog {

	/*
	 * Declare and initialise class variables
	 */
	List<File> fileList = new LinkedList<File>();
	
	/*
	 * Declare JPanels
	 */		
	JPanel buttonPanel = new JPanel();
	JPanel filePanel = new JPanel();
	JPanel confirmPanel = new JPanel();
	JPanel infoPanel = new JPanel();
	
	
	/**
	 * Create the dialog.
	 */
	public  PortholeSelectDialog() throws ImgIOException {	
		setName("PortholeSelect");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
				
		buttonPanel.setLayout(new FlowLayout());
		filePanel.setLayout(new FlowLayout());
		confirmPanel.setLayout(new FlowLayout());
		
		JScrollPane listScroller;
		JToolBar fileBar = new JToolBar("");
		JButton addButton = new JButton("Add");
		JButton removeButton = new JButton("Remove");
		JButton confirmButton = new JButton("Confirm");
		
		DefaultListModel<File> imageJListModel = new DefaultListModel<File>();
		JList<File> imageJList = new JList<File>(imageJListModel);
								
		getContentPane().add(filePanel, BorderLayout.CENTER);
		{
			/*
			 * imageList configuration
			 * list to display chosen files
			 */
			imageJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			imageJList.setLayoutOrientation(JList.VERTICAL);
			imageJList.setVisibleRowCount(-1);
			imageJList.setCellRenderer(new FileCellRenderer());
		    		
			listScroller = new JScrollPane(imageJList);
			listScroller.setPreferredSize(new Dimension(380, 200));
					
			filePanel.add(listScroller);
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
					
					//Retrieve value from ui chooseFiles
				    List<File>initialValue = new LinkedList<File>();
					List<File>inputList = getUi().chooseFiles(null , initialValue, new ImageFileFilter(), FileWidget.OPEN_STYLE);
					if(inputList == null) {
						return;
					}
					
					//Iterate through list of chosen files
					Iterator<File> fileItr = inputList.iterator();							
					while (fileItr.hasNext()) {
						File current = fileItr.next();
						imageJListModel.addElement(current);			    
					}
					
					if(imageJListModel.size() > 0)
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
					int index = imageJList.getSelectedIndex();
					imageJListModel.remove(index);	
					
					if (imageJListModel.size() < 1)
						confirmButton.setEnabled(false);
					
				}
			});			
			fileBar.add(removeButton);	
			removeButton.setEnabled(false);
			
			/*
			 * ListSelectionListener
			 * Determines if remove button should be enabled 
			 */			
			imageJList.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					
					 if (e.getValueIsAdjusting() == false) {
						 
						 if (imageJList.getSelectedIndex() == -1)
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
					
					//Iterate through ListModel, place all elements in fileList
					for(int i=0; i<imageJListModel.getSize(); i++) {
						fileList.add(imageJListModel.get(i));
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

	/* Accessors and Mutators */	
	public List<File> getFileList(){
		return fileList;
	}


	public void setFileList(List<File> fileList){
		this.fileList = fileList;
	}

}




