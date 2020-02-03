package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.util.ArrayList;
import java.util.Hashtable;
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
import org.scijava.service.Service;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;

import io.scif.services.DatasetIOService;
import io.scif.services.FormatService;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;


/* Invoked when user selects plugin from menu */
@Plugin(type = Command.class, headless = false,menuPath = "Porthole>Load Images")
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
	private static PortholeToolPanelDialog toolPanelDialog = null;
		
	/* Declare class variables */
	private static FalseRGBConverter rgbConverter = null;
	private ArrayList<ImgPlusMeta> imgData = new ArrayList<ImgPlusMeta>();
	private Hashtable<String,Service> services = new Hashtable<String,Service>();
	private ImgPlusMeta rgbImg;
	int currentState = 1;
	boolean running = true;
	
	public void run() {
		
		/* Collate services with hashtable for easier initialisation of dialogs */
		services.put("OpService", ops);
		services.put("LogService", log);
		services.put("UIService", ui);
		services.put("CommandService", cmd);
		services.put("StatusService", status);
		services.put("ThreadService", thread);
		services.put("IOService", io);
		services.put("DatasetIOService", dsIO);
		services.put("DatasetService", ds);
		services.put("FormatService", formatService);
		
		/* Initialise FalseRGBConverter */
		rgbConverter = new FalseRGBConverter();
		rgbConverter.setServices(services);
			
		/*
		 * Declare and initialise dialogs
		 */
		SwingUtilities.invokeLater(() -> {
			
			//Initialise select file dialog
			if (selectFileDialog == null) {
				selectFileDialog = new PortholeSelectFileDialog();
				
				//Register services for selectFileDialog
				selectFileDialog.setServices(services);
				
				selectFileDialog.setTitle("Porthole - Select Files");
				
				//Add listener for closing of selectFileDialog
				selectFileDialog.addComponentListener(new ComponentAdapter() {		
					public void componentHidden(ComponentEvent e){
						//On setVisible(false) of selectDialog, 
						//if nextState is true, 
						if(selectFileDialog.getNextState()) {
							//Add all chosen files to imgData
							imgData.addAll(selectFileDialog.getImgData());
							//Clear chosen files from dialog
							selectFileDialog.clearImgData();
							//Declere and Initialise iterator for images
							Iterator<ImgPlusMeta> imgItr = imgData.iterator();
							//Iterate through images, retrieve them
							while(imgItr.hasNext()) {
								imgItr.next().initImg();
							}				
							changeState(2);
						}	
						else
						{
							disposeAllUI();
						}
					}
				});			
			}	
			
			if (toolPanelDialog == null) {
				toolPanelDialog = new PortholeToolPanelDialog();
				toolPanelDialog.setTitle("Porthole - Tools");
				toolPanelDialog.addComponentListener(new ComponentAdapter() {		
					public void componentHidden(ComponentEvent e){
						disposeAllUI();
					}
				});			
			}
			
			changeState(1);
	   });	
				
		
	}
	
	public void changeState(int nextState) {
		
		switch(nextState) {
			
			case 1:
				/* Place UI in 1st state */
				selectFileDialog.setVisible(true);
				break;
		
			case 2:
				/* Convert imgData to falseRGB */
				rgbImg = rgbConverter.convert(imgData);
				/* Show false RGB image for user manipulation */
				ui.show("FalseRGB", rgbImg.getImg());
				changeState(3);
				break;
				
			case 3:
				toolPanelDialog.setVisible(true);
				break;
				
			default:
				ui.show("State out of bounds, value:"+nextState);
				break;

		}
		
	}
	
	/*
	 * Cleans up all Java Swing components
	 */
	public void disposeAllUI() {
		
		if(selectFileDialog != null)
			selectFileDialog.dispose();
		
		if(toolPanelDialog !=  null)
			toolPanelDialog.dispose();
	
	}
}


