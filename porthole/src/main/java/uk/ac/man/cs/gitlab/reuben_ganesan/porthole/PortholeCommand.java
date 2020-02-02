package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.SwingUtilities;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
	private static PortholeSelectFileDialog selectFileDialog = null;
	
	/* Declare class variables */
	private ArrayList<ImgWaveType> imgData = new ArrayList<ImgWaveType>();
	
	public void run() {
		
		/*
		 * Declare and initialise dialogs
		 */
		SwingUtilities.invokeLater(() -> {
			if (selectFileDialog == null) {
				selectFileDialog = new PortholeSelectFileDialog();
			}
			

			selectFileDialog.setOpsService(ops);
			selectFileDialog.setLogService(log);
			selectFileDialog.setStatusService(status);
			selectFileDialog.setCommandService(cmd);
			selectFileDialog.setThreadService(thread);
			selectFileDialog.setUIService(ui);
			selectFileDialog.setIOService(io);
			selectFileDialog.setDatasetIOService(dsIO);
			selectFileDialog.setDatasetService(ds);
			selectFileDialog.setTitle("Porthole - Select Files");
								
			/*
			 * State machine to handle dialog flow
			 */
			
			/* 1st state */
			selectFileDialog.addComponentListener(new ComponentAdapter() {		
				
				//On setVisible(false) of selectDialog, 
				//if nextState is true, 
				public void componentHidden(ComponentEvent e){
					if(selectFileDialog.getNextState()) {
						
						//Add all chosen files to imgData
						imgData.addAll(selectFileDialog.getImgData());
						
						//Clear chosen files from dialog
						selectFileDialog.clearImgData();
						
						//Declere and Initialise iterator for images
						Iterator<ImgWaveType> imgItr = imgData.iterator();
						
						//Iterate through images, retrieve them
						while(imgItr.hasNext()) {
							imgItr.next().initImg();
						}
							
					}
					else{
						disposeAllUI();
					}					
				}
				
			});
			
			
					
			//Place UI in 1st state
			selectFileDialog.setVisible(true);
		 	  		    
	   });	
				
		
	}
	
	/*
	 * Cleans up all Java Swing components
	 */
	public void disposeAllUI() {
		
		if(selectFileDialog != null)
			selectFileDialog.dispose();
	
	}
}


