package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import javax.swing.JDialog;
import net.imagej.ops.OpService;
import org.scijava.app.StatusService;
import org.scijava.command.CommandService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.awt.event.ActionEvent;

/* Classifies images into different wavelength bands */
public class PortholeDialog extends JDialog implements ActionListener {

	/**
	 * Declare and initialise class variables
	 */
	private OpService ops;
	private LogService log;
	private IOService io;
	private StatusService status;
	private CommandService cmd;
	private ThreadService thread;
	private UIService ui;
	private boolean nextState = false;

	private List<File> fileList;

	/**
	 * Create the dialog.
	 */
	public PortholeDialog() {	
				
		
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
	
	public IOService getIO() {
		return io;
	}

	public void setIO(final IOService io) {
		this.io = io;	
	}
	
	public List<File> getFileList(){
		return fileList;
	}
	
	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}
	
	public boolean getNextState() {
		return nextState;
	}
	
	public void setNextState(boolean input) {
		this.nextState = input;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub		
	}
	
	
	public static void main(final String[] args) {
		try {
			final PortholeDialog dialog = new PortholeDialog();
			//dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}




	

}
