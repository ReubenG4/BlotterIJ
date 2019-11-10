package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.imagej.ops.OpService;
import org.scijava.app.StatusService;
import org.scijava.command.CommandService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;
import org.scijava.widget.FileWidget;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.ActionEvent;

/* Invoked by porthole command if images are to be selected */
public class PortholeSelectDialog extends JDialog implements ActionListener {

	/**
	 * Declare and initialise class variables
	 */
	private static final long serialVersionUID = -9139535193568501417L;
	private OpService ops;
	private LogService log;
	private IOService io;
	private StatusService status;
	private CommandService cmd;
	private ThreadService thread;
	private UIService ui;
	
	private JPanel buttonPanel = new JPanel();
	private JPanel dataPanel = new JPanel();
	private DefaultListModel<File> imageListModel = new DefaultListModel<File>();
	private JList<File> imageList = new JList<File>(imageListModel);
	
	/**
	 * Launch the dialog.
	 */
	public static void main(final String[] args) {
		try {
			final PortholeSelectDialog dialog = new PortholeSelectDialog();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PortholeSelectDialog() {	
		setName("Porthole");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		buttonPanel.setLayout(new FlowLayout());
				
		getContentPane().add(dataPanel, BorderLayout.CENTER);
		{
			/*
			 * imageList
			 * list to display chosen files
			 */
			imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			imageList.setLayoutOrientation(JList.VERTICAL);
			imageList.setVisibleRowCount(-1);
			imageList.setCellRenderer(new FileCellRenderer());
		    		
			JScrollPane listScroller = new JScrollPane(imageList);
			listScroller.setPreferredSize(new Dimension(380, 200));
			
			
			dataPanel.add(listScroller);
		}
				
		getContentPane().add(buttonPanel, BorderLayout.PAGE_START);
		{
			JToolBar fileBar = new JToolBar("");
			
			/*
			 * addButton
			 * calls UI to prompt user to choose a file
			 * adds chosen file to list
			 */
			JButton addButton = new JButton("Add");
			addButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					
				    List<File>initialValue = new LinkedList<File>();
					final List<File>fileList = ui.chooseFiles(null , initialValue, new ImageFileFilter(), FileWidget.OPEN_STYLE);
					Iterator<File> fileItr = fileList.iterator();
							
					while (fileItr.hasNext()) {
						imageListModel.addElement(fileItr.next());
					}
				}
			});
			
			fileBar.add(addButton);
			
			
			/*
			 * removeButton
			 * removes selected file from list when clicked
			 */
			JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(final ActionEvent arg0) {
					int index = imageList.getSelectedIndex();
					imageListModel.remove(index);	
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
		
		
		
		
	}
	
	
	

	/**
	 * Accessor and mutator methods
	 * 
	 */

	public OpService getOps() {
		return ops;
	}

	public void setOps(final OpService ops) {
		this.ops = ops;
	}

	public LogService getLog() {
		return log;
	}

	public void setLog(final LogService log) {
		this.log = log;
	}

	public StatusService getStatus() {
		return status;
	}

	public void setStatus(final StatusService status) {
		this.status = status;
	}

	public CommandService getCommand() {
		return cmd;
	}

	public void setCommand(final CommandService command) {
		this.cmd = command;
	}

	public ThreadService getThread() {
		return thread;
	}

	public void setThread(final ThreadService thread) {
		this.thread = thread;
	}

	public UIService getUi() {
		return ui;
	}

	public void setUi(final UIService ui) {
		this.ui = ui;
	}

	public void setIO(final IOService io) {
		this.io = io;	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	


}




