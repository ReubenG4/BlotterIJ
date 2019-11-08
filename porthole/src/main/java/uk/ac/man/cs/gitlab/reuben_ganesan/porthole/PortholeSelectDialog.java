package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import net.imagej.ops.OpService;
import org.scijava.app.StatusService;
import org.scijava.command.CommandService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;
import java.awt.FlowLayout;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

/* Invoked by porthole command if images are to be selected */
public class PortholeSelectDialog extends JDialog implements ActionListener {

	/**
	 * 
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
	final JFileChooser fc = new JFileChooser();

	/**
	 * Launch the application.
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
		Object[] data = new Object[17];
		File[] images = new File[17];
		
		
		
		
		getContentPane().add(buttonPanel, BorderLayout.PAGE_START);
		{
			JToolBar fileBar = new JToolBar("");
			
			//File Button
			JButton addButton = new JButton("Add");
			fileBar.add(addButton);
			
			JButton removeButton = new JButton("Remove");
			fileBar.add(removeButton);
			
			fileBar.setFloatable(false);
	        fileBar.setRollover(true);		
	        
			buttonPanel.add(fileBar);
		}
		
		getContentPane().add(dataPanel, BorderLayout.CENTER);
		{
			
			JList imageList = new JList(data); 
			imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			imageList.setLayoutOrientation(JList.VERTICAL);
			imageList.setVisibleRowCount(-1);	
			
			JScrollPane listScroller = new JScrollPane(imageList);
			listScroller.setPreferredSize(new Dimension(380, 200));
			
			dataPanel.add(listScroller);
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




