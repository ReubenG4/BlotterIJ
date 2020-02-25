package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import java.awt.Rectangle;
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
	OpService op;

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
	private ArrayList<ImgWrapper> imgData = new ArrayList<ImgWrapper>();
	private Hashtable<String,Service> services = new Hashtable<String,Service>();
	
	
	private ImagePlus rgbImg;
	Rectangle selection;
	private PxlData pxlData;
	
	
	int currentState = 1;
	boolean running = true;

	
	private static FalseRGBConverter rgbConverter = null;
	private static BlotterPCA pca = null;
	
	
	
	public void run() {
		
		/* Collate services with hashtable for easier initialisation of dialogs */
		services.put("OpService", op);
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
		
		/* Initialise BlotterPCA */
		pca = new BlotterPCA();
		pca.setServices(services);
			
		SwingUtilities.invokeLater(() -> {
					
			//Initialise JDialogs
			initSelectFileDialog();
			initToolPanelDialog();
			
			//Place FSM in first state
			changeState(1);	
			
		});	
				
		
	}
	
	/*
	 * Finite State Machine for governing program flow
	 */
	
	public void changeState(int nextState) {
		
		switch(nextState) {
			
			case 1:
				/* State 1: File Selection */
				selectFileDialog.setVisible(true);
				selectFileDialog.toFront();
				break;
		
			case 2:
				/* State 2: Produce and show FalseRGB for user view */
				stateWorker2.execute();
				break;
				
				
			case 3:
				/* State 3: Make tool panel visible */
				toolPanelDialog.setVisible(true);
				toolPanelDialog.toFront();			
				break;
				
				
			case 4:
				/* State 4: Perform PCA */
				stateWorker4.execute();
				//pca.run(imgData, selection);
				//changeState(3);
				break;
				
			default:
				ui.showDialog("State out of bounds, value:"+nextState);
				break;

		}
		
	}
	
	
	/*Declare SwingWorker Threads*/
	
	/*
	 * SwingWorker for State 2 
	 * Show false RGB image for user manipulation
	 * Changes to state 3 when done
	 */
	SwingWorker stateWorker2 = new SwingWorker() {

		@Override
		protected Object doInBackground() throws Exception {
			rgbImg = rgbConverter.convert(imgData);
			
			if(rgbImg != null) {
				rgbImg.setTitle("FalseRGB");
			}
			else {
				//If no false RGB image produced, wrap and use first image loaded
				rgbImg = ImageJFunctions.wrap(imgData.get(0).getImg(),"FalseRGB");
				rgbImg.setTitle("FalseRGB");
			}
			ui.show("FalseRGB",rgbImg);
			return null;
		}
		
		@Override
		protected void done(){
			changeState(3);
		}
	
	};
	
	/*
	 * SwingWorker for State 4 
	 * Runs PCA
	 * Changes to state 3 when done
	 */
	SwingWorker stateWorker4 = new SwingWorker() {
		@Override
		protected Object doInBackground() throws Exception {
			pca.run(imgData,selection);
			pxlData = pca.getPxlData();
			return null;
		}
			
		@Override
		protected void done(){
			changeState(3);
		}
		
	};
	
	
	/* 
	 * Initialises selectFileDialog 
	 */
	public void initSelectFileDialog() {
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
						changeState(2);
					}					
				}		
			});
			
			selectFileDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
		}//if selectFileDialog == null	
	}
	
	/* 
	 * Initialises toolPanelDialog 
	 */
	public void initToolPanelDialog() {
		
		if (toolPanelDialog == null) {
			toolPanelDialog = new BlotterToolPanelDialog();
			toolPanelDialog.setTitle("Blotter");
			toolPanelDialog.addComponentListener(new ComponentAdapter() {		
				public void componentHidden(ComponentEvent e){
					//On setVisible(false) of toolPanelDialog, 
					//if nextState is true, 
					if(toolPanelDialog.getNextState()) {
						//If Selection has been chosen
						if(toolPanelDialog.getSelection() != null) {
							//Retrieve selection
							selection = toolPanelDialog.getSelection();
							changeState(4);
						}
					}		
				}
			});		
			
			toolPanelDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
		}//if toolPanelDialog==null
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


