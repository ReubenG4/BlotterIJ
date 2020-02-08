package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.util.ArrayList;
import java.util.Hashtable;

import org.scijava.app.StatusService;
import org.scijava.command.CommandService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.service.Service;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;

import io.scif.services.DatasetIOService;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;

/*
 * Parent class for functions 
 */


public class BlotterFunction {
	
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
	protected ArrayList<ImgWrapper> imgData;
	
	    /* State */
		public boolean getNextState() {
			return nextState;
		}
			
		public void setNextState(boolean input) {
			this.nextState = input;
		}
			
		/* Image Data */
		public ArrayList<ImgWrapper> getImgData(){
			return imgData;
		}
		
		public void setImgData(ArrayList<ImgWrapper> input) {
		    
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

		public StatusService getStatusService() {
			return status;
		}

		public void setStatusService(final StatusService status) {
			this.status = status;
		}

		public CommandService getCommandService() {
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
		
		public void setServices(Hashtable<String,Service> services) {
			setOpsService((OpService) services.get("OpsService"));
			setLogService((LogService) services.get("LogService"));
			setUIService((UIService) services.get("UIService"));
			setCommandService((CommandService) services.get("CommandService"));
			setStatusService((StatusService) services.get("StatusService"));
			setThreadService((ThreadService) services.get("ThreadService"));
			setIOService((IOService) services.get("IOService"));
			setDatasetIOService((DatasetIOService) services.get("DatasetIOService"));
			setDatasetService((DatasetService) services.get("DatasetService"));		
		}

}
