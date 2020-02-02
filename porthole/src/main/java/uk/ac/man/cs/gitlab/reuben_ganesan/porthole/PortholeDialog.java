package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import javax.swing.JDialog;

import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import org.scijava.app.StatusService;
import org.scijava.command.CommandService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;

import io.scif.services.DatasetIOService;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

/* 
 * Parent class for dialogs
 */

public class PortholeDialog extends JDialog implements ActionListener{

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
	private DatasetIOService dsIO;
	private DatasetService ds; 
	
	private boolean nextState = false;
	protected ArrayList<ImgPlusMeta> imgData;
	
	
	/**
	 * Create the dialog.
	 */
	public PortholeDialog() {	
				
		
	}

	/**
	 * Accessor and mutator methods
	 * 
	 */
	
	/* State */
	public boolean getNextState() {
		return nextState;
	}
		
	public void setNextState(boolean input) {
		this.nextState = input;
	}
		
	/* Image Data */
	public ArrayList<ImgPlusMeta> getImgData(){
		return imgData;
	}
	
	public void setImgData(ArrayList<ImgPlusMeta> input) {
	    
	    if(!imgData.isEmpty())
	    	imgData.clear();
	    
	    imgData.addAll(input); 
	}
	
	public void clearImgData() {
		imgData.clear();
	}
	
	
	/* Services */
	public OpService getOpsService() {
		return ops;
	}

	public void setOpsService(final OpService ops) {
		this.ops = ops;
	}

	public LogService getLogService() {
		return log;
	}

	public void setLogService(final LogService log) {
		this.log = log;
	}

	public StatusService getStatus() {
		return status;
	}

	public void setStatusService(final StatusService status) {
		this.status = status;
	}

	public CommandService getCommand() {
		return cmd;
	}

	public void setCommandService(final CommandService command) {
		this.cmd = command;
	}

	public ThreadService getThreadService() {
		return thread;
	}

	public void setThreadService(final ThreadService thread) {
		this.thread = thread;
	}

	public UIService getUIService() {
		return ui;
	}

	public void setUIService(final UIService ui) {
		this.ui = ui;
	}
	
	public IOService getIOService() {
		return io;
	}

	public void setIOService(final IOService io) {
		this.io = io;	
	}
	
	public DatasetIOService getDatasetIOService() {
		return dsIO;
	}

	public void setDatasetIOService(DatasetIOService dsIO) {
		this.dsIO = dsIO;
	}
	
	
	public DatasetService getDatasetService() {
		return ds;
	}

	public void setDatasetService(DatasetService ds) {
		this.ds = ds;
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
