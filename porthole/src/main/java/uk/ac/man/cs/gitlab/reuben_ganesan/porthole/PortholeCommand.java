package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.SwingUtilities;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;

import io.scif.services.DatasetIOService;
import io.scif.services.FormatService;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;

/* Invoked when user selects plugin from menu */
@Plugin(type = Command.class, headless = true,menuPath = "Plugins>Porthole>Load Images")
public class PortholeCommand implements Command{

	/* Ask context for access to services */
	@Parameter
	OpService ops;

	@Parameter
	LogService log;

	@Parameter
	UIService ui;

	@Parameter
	CommandService cmd;

	@Parameter
	StatusService status;

	@Parameter
	ThreadService thread;
	
	@Parameter
	IOService io;
	
	@Parameter
	DatasetIOService dsIO;
	
	@Parameter
	DatasetService ds;
	
    @Parameter
    FormatService formatService;

	/* Declare JDialogs */
	private static PortholeSelectDialog dialogS = null;
	
	/* Declare class variables */
	private ArrayList<ImgWaveType> imgData = new ArrayList<ImgWaveType>();
	
	public void run() {
		
		/*
		 * Declare and initialise dialogs
		 */
		SwingUtilities.invokeLater(() -> {
			if (dialogS == null) {
				dialogS = new PortholeSelectDialog();
			}
			

			dialogS.setOpsService(ops);
			dialogS.setLogService(log);
			dialogS.setStatusService(status);
			dialogS.setCommandService(cmd);
			dialogS.setThreadService(thread);
			dialogS.setUIService(ui);
			dialogS.setIOService(io);
			dialogS.setDatasetIOService(dsIO);
			dialogS.setDatasetService(ds);
			dialogS.setTitle("Porthole - Select Files");
								
			/*
			 * State machine to handle dialog flow
			 */
			
			/* 1st state */
			dialogS.addComponentListener(new ComponentAdapter() {		
				
				//On setVisible(false) of selectDialog, 
				//if nextState is true, open dialogS, else disposeAllUI
				public void componentHidden(ComponentEvent e){
					if(dialogS.getNextState()) {
						
						imgData.addAll(dialogS.getImgData());
						Iterator<ImgWaveType> imgItr = imgData.iterator();
						
						while(imgItr.hasNext()) {
							imgItr.next().initImg();
							ui.showDialog("Successful");
						}
							
					}
					else{
						disposeAllUI();
					}					
				}
				
			});
			
			
					
			//Place UI in 1st state
			dialogS.setVisible(true);
		 	  		    
	   });	
				
		
	}
	
	/*
	 * Cleans up all Java Swing components
	 */
	public void disposeAllUI() {
		
		if(dialogS != null)
			dialogS.dispose();
	
	}
}


