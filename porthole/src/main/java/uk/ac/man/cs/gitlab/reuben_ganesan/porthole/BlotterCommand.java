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

import ij.ImagePlus;
import io.scif.services.DatasetIOService;
import io.scif.services.FormatService;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.img.display.imagej.ImageJFunctions;


/* Invoked when user selects plugin from menu */
@Plugin(type = Command.class, headless = false,menuPath = "Plugins>Blotter>Load Images")
public class BlotterCommand implements Command{

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
	private static BlotterSelectFileDialog selectFileDialog = null;
	private static BlotterToolPanelDialog toolPanelDialog = null;
		
	/* Declare class variables */
	private static FalseRGBConverter rgbConverter = null;
	private ArrayList<ImgWrapper> imgData = new ArrayList<ImgWrapper>();
	private Hashtable<String,Service> services = new Hashtable<String,Service>();
	private ImagePlus rgbImg;
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
				selectFileDialog = new BlotterSelectFileDialog();
				
				//Register services for selectFileDialog
				selectFileDialog.setServices(services);
				
				selectFileDialog.setTitle("Blotter - Select Files");
				
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
							Iterator<ImgWrapper> imgItr = imgData.iterator();
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
				toolPanelDialog = new BlotterToolPanelDialog();
				toolPanelDialog.setTitle("Blotter");
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
				/* State 1: File Selection */
				selectFileDialog.setVisible(true);
				break;
		
			case 2:
				/* State 2: Produce and show FalseRGB for user view */
				rgbImg = rgbConverter.convert(imgData);
				
				/* Show false RGB image for user manipulation */
				if(rgbImg != null) {
					rgbImg.setTitle("FalseRGB");
					ui.show("FalseRGB", rgbImg);
				}
				else {
					//If no false RGB image produced, wrap and use first image loaded
					rgbImg = ImageJFunctions.wrap(imgData.get(0).getImg(),"FalseRGB");
					rgbImg.setTitle("FalseRGB");
					ui.show("FalseRGB",rgbImg);
				}
						
				changeState(3);
				break;
				
			case 3:
				/* State 3: Tool Panel */
				toolPanelDialog.setVisible(true);
				break;
				
			default:
				ui.showDialog("State out of bounds, value:"+nextState);
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


