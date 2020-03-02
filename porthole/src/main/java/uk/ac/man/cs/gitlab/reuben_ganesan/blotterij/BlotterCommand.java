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
import net.imglib2.img.Img;
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
	private static BlotterSelectFeatureDialog selectFeatureDialog = null;
		
	/* Declare class variables */
	private Hashtable<String,Service> services;

	private Img imageToRender;
	private ImagePlus rgbImg;
	Rectangle regionOfInterest;
	private PcaData pcaData;
	private ArrayList<ImgWrapper> imgData;
	private ArrayList<PcaFeature> selectedFeatures;

	private static FalseRGBConverter rgbConverter = null;
	private static BlotterPcaCalc pcaCalc = null;
	private static BlotterPcaRender pcaRender = null;
	
	/* Declare SwingWorker objects */
	StateWorker2 stateWorker2;
	StateWorker4 stateWorker4;
	StateWorker6 stateWorker6;
	
	public void run() {
		
		
		/* Collate services with hashtable for easier initialisation of dialogs */
		services = new Hashtable<String,Service>();
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
		pcaCalc = new BlotterPcaCalc();
		pcaCalc.setServices(services);
		
		/* Initialise remaining ArrayLists for holding data */
		imgData = new ArrayList<ImgWrapper>();
		selectedFeatures = new ArrayList<PcaFeature>();
			
		SwingUtilities.invokeLater(() -> {
					
			//Initialise JDialogs
			initSelectFileDialog();
			initToolPanelDialog();
			initSelectFeatureDialog();
			
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
				stateWorker2 = new StateWorker2();
				stateWorker2.execute();
				break;
				
				
			case 3:
				/* State 3: Make tool panel visible */
				toolPanelDialog.setVisible(true);
				toolPanelDialog.toFront();			
				break;
				
				
			case 4:
				/* State 4: Perform PCA, produce eigenvectors and eigenvalues, save mean adjusted data */
				stateWorker4 = new StateWorker4();
				stateWorker4.execute();	
				break;
				
			case 5:
				/* State 5: Display features found by PCA for selection */
				if(selectFeatureDialog.getFeatureData().size() == 0) {
					selectFeatureDialog.addFeatureData(pcaData.getFeatureList());
					selectFeatureDialog.prepareForDisplay();
				}
				selectFeatureDialog.setVisible(true);
				selectFeatureDialog.toFront();
				break;
				
			case 6:
				/* State 6: Render image from selected features */
				stateWorker6 = new StateWorker6();
				stateWorker6.execute();
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
	class StateWorker2 extends SwingWorker{

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
	
	}
	
	/*
	 * SwingWorker for State 4 
	 * Runs PCA
	 * Changes to state 5 when done
	 */
	class StateWorker4 extends SwingWorker {
		@Override
		protected Object doInBackground() throws Exception {
			pcaCalc.run(imgData,regionOfInterest);
			pcaData = pcaCalc.getPcaData();
			return null;
		}
			
		@Override
		protected void done(){
			changeState(5);
		}
		
	}
	
	
	/*
	 * SwingWorker for State 6 
	 * Calculate FinalData from selectedFeatures and dataMeanAdjust
	 * Renders it as an image
	 * Changes to state 5 when done
	 */
	
	class StateWorker6 extends SwingWorker{

		@Override
		protected Object doInBackground() throws Exception {
			if(pcaRender == null)
				pcaRender = new BlotterPcaRender(pcaData,regionOfInterest);
			
			Img newImg = pcaRender.render(selectedFeatures);
			String name = "Component "+selectedFeatures.get(0).getIndex();
			ui.show(name,newImg);
			return null;
		}
			
		@Override
		protected void done(){	
			changeState(5);
		}
		
	}
	
	/* Following code governs how JDialogs interact with event-dispatcher thread */
	
	
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
						
						//Reset nextState flag and state transition
						selectFileDialog.setNextState(false);
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
							regionOfInterest = toolPanelDialog.getSelection();
							
							//Reset nextState flag and state transition
							toolPanelDialog.setNextState(false);
							changeState(4);
						}
					}		
				}
			});		
			
			toolPanelDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
		}//if toolPanelDialog==null
	}
	
	
	/* 
	 * Initialises selectFeatureDialog 
	 */
	public void initSelectFeatureDialog() {
		//Initialise selectFeaturedialog
		if (selectFeatureDialog == null) {
			selectFeatureDialog = new BlotterSelectFeatureDialog();
			
			//Register services for selectFeatureDialog
			selectFeatureDialog.setServices(services);

			selectFeatureDialog.setTitle("Blotter - Select Feature");
			
			//Add listener for closing of selectFeatureDialog
			selectFeatureDialog.addComponentListener(new ComponentAdapter() {		
				public void componentHidden(ComponentEvent e){
					//On setVisible(false) of selectFeatureDialog, 
					//if nextState is true, 
					if(selectFeatureDialog.getNextState()) {
						//Retrieve selected features
						selectedFeatures = selectFeatureDialog.getSelected();
						selectFeatureDialog.setNextState(false);
						changeState(6);			
					}					
				}		
			});
			
			selectFileDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
		}//if selectFileDialog == null	
	}
	
	
	/*
	 * Cleans up all Java Swing components
	 */
	public void disposeAllUI() {
		
		if(selectFileDialog != null)
			selectFileDialog.dispose();
		
		if(toolPanelDialog !=  null)
			toolPanelDialog.dispose();
	
		if(selectFeatureDialog != null)
			selectFeatureDialog.dispose();
		
	}
}


