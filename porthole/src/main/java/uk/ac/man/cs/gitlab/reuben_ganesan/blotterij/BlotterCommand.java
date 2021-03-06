package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import org.apache.commons.lang3.math.NumberUtils;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
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

import ij.IJ;
import ij.ImagePlus;
import io.scif.services.DatasetIOService;
import io.scif.services.FormatService;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.display.imagej.ImageJFunctions;


/* Invoked when user selects plugin from menu */
@Plugin(type = Command.class, headless = false,menuPath = "Plugins>Analyze>BlotterIJ")
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
	private static BlotterSelectSpectraDialog selectSpectraDialog = null;
	private static BlotterDisplayEucdDialog displayEucdDialog = null;
		
	/* Declare class variables */
	private Hashtable<String,Service> services;

	private ImagePlus rgbImg;
	Rectangle regionOfInterest;
	private ArrayList<ImgWrapper> imgData;
	private PcaFeature selectedFeature;
	
	private static FalseRGBConverter rgbConverter = null;
	private static BlotterPcaMain pcaMain = null;
	private static BlotterSpectraMain spectraMain = null;
	
	/* Declare SwingWorker objects */
	StateWorker2 stateWorker2;
	StateWorker4 stateWorker4;
	StateWorker6 stateWorker6;
	StateWorker8 stateWorker8;
	StateWorker9 stateWorker9;
	StateWorker10 stateWorker10;
	StateWorker11 stateWorker11;
	StateWorker12 stateWorker12;
	StateWorker13 stateWorker13;
	
	public void run() {
		
		
		/* Collate services with hashtable for easier initialisation of dialogs and functions */
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
		pcaMain = new BlotterPcaMain();
		pcaMain.setServices(services);
		
		/* Initialise BlotterSpectra */
		spectraMain = new BlotterSpectraMain();
		spectraMain.setServices(services);
		
		/* Initialise remaining ArrayLists for holding data */
		imgData = new ArrayList<ImgWrapper>();
		
		SwingUtilities.invokeLater(() -> {
					
			//Initialise JDialogs
			initSelectFileDialog();
			initToolPanelDialog();
			initSelectFeatureDialog();
			initSelectSpectraDialog();
			
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
				/* --> State 3 */
				break;
				
				
			case 3:
				/* State 3: Make tool panel visible */
				toolPanelDialog.setVisible(true);
				toolPanelDialog.toFront();		
				/* --> State 4,7 */
				break;
				
				
			case 4:
				/* State 4: Perform PCA, produce eigenvectors and eigenvalues, save mean adjusted data */
				stateWorker4 = new StateWorker4();
				stateWorker4.execute();	
				/* --> State 5 */
				break;
				
			case 5:
				/* State 5: Display features found by PCA for selection */
				selectFeatureDialog.addFeatureData(pcaMain.getPcaData().getFeatureList());
				selectFeatureDialog.prepareForDisplay();
				
				selectFeatureDialog.setVisible(true);
				selectFeatureDialog.toFront();
				/* --> State 6 */
				break;
				
			case 6:
				/* State 6: Render image from selected features */
				stateWorker6 = new StateWorker6();
				stateWorker6.execute();
				/* --> State 5 */
				break;
					
			case 7:
				/* State 7: Display selected Spectra */
				selectSpectraDialog.setVisible(true);
				selectSpectraDialog.toFront();
				/* --> State 8,9,10,11,12,13 */
				break;
					
			case 8:
				/* State 8: Retrieves spectra data of the selected region of interest */
				stateWorker8 = new StateWorker8();
				stateWorker8.execute();
				/* --> State 7 */
				break;
				
			case 9:
				/* State 9: Plot the spectraData */
				stateWorker9 = new StateWorker9();
				stateWorker9.execute();
				/* --> State 7 */
				break;
				
			case 10:
				/* State 10: Calculate Euclidean distance between spectra*/
				stateWorker10 = new StateWorker10();
				stateWorker10.execute();
				/* --> State 7 */
				break;
				
			case 11:
				/* State 11: Load Spectra */
				stateWorker11 = new StateWorker11();
				stateWorker11.execute();
				/* --> State 7 */
				break;
				
			case 12:
				/* State 12: Save Spectra */
				stateWorker12 = new StateWorker12();
				stateWorker12.execute();
				/* --> State 7 */
				break;
				
			case 13:
				/* State 13: Retrieve normalisation Spectra for calibration */
				stateWorker13 = new StateWorker13();
				stateWorker13.execute();
				/* --> State 7 */
				break;
				
			default:
				ui.showDialog("State index out of bounds, value:"+nextState);
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
			
			pcaMain.pca(imgData,regionOfInterest);
			return null;
			
		}
			
		@Override
		protected void done(){
			changeState(5);
		}
		
	}
	
	
	/*
	 * SwingWorker for State 6 
	 * Renders a feature as an image
	 * Changes to state 5 when done
	 */
	
	class StateWorker6 extends SwingWorker{

		@Override
		protected Object doInBackground() throws Exception {
			
			ArrayImg newImg = pcaMain.renderImg(selectedFeature);
			String name = "Component "+selectedFeature.getIndex();
			ui.show(name,newImg);
			return null;
			
		}
			
		@Override
		protected void done(){	
			changeState(5);
		}
		
	}
	
	/*
	 * SwingWorker for State 8 
	 * Produces a plot of selected spectra
	 * Changes to state 7 when done
	 */
	
	class StateWorker8 extends SwingWorker{

		@Override
		protected Object doInBackground() throws Exception {
			Rectangle selection = selectSpectraDialog.getSelectedRegion();
			SpectraData spectraData = spectraMain.calcSpectra(imgData, selection);
			selectSpectraDialog.addData(spectraData);
			return null;			
		}
			
		@Override
		protected void done(){	
			changeState(7);
		}
		
	}
	
	
	/*
	 * SwingWorker for State 9
	 * Changes to state 7 when done
	 */
	class StateWorker9 extends SwingWorker{

		@Override
		protected Object doInBackground() throws Exception {
			
			ArrayList<SpectraData> spectraList = selectSpectraDialog.getData();
			XYChart chart = spectraMain.plotSpectra(spectraList);
			new SwingWrapper<XYChart>(chart).displayChart().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			return null;
			
		}
			
		@Override
		protected void done(){	
			changeState(7);
		}
		
	}
	
	/*
	 * SwingWorker for State 10
	 * Changes to state 7 when done
	 */
	class StateWorker10 extends SwingWorker{

		@Override
		protected Object doInBackground() throws Exception {
			
			/* State 10: Calculate euclidean distance */
			ArrayList<SpectraData> selectedSpectra = selectSpectraDialog.getData();
			ArrayList<String> rowColNames = new ArrayList<String>();
			ArrayList<double[]> data = new ArrayList<double[]>(); 
			
			//Index tracks which value to omit
			int zeroIndex = 0;
			int noOfSpectra = selectedSpectra.size();
			
			//Iterate through selected spectra and calculate eucldiean distance between it and other spectra
			while(zeroIndex < noOfSpectra) {
				
				int index = 0;
				double[] calculatedEucd = new double[noOfSpectra];
				
				while(index < noOfSpectra) {
					
					if(index != zeroIndex)
						calculatedEucd[index] = spectraMain.euclideanDistance(selectedSpectra.get(zeroIndex), selectedSpectra.get(index));
					else
						calculatedEucd[index] = 0;
						
					index++;
				}
				
				data.add(calculatedEucd);
				
				zeroIndex++;
			}
			
			//Iterate through selected spectra and retrieve names
			
			rowColNames.add("X");
			
			for(SpectraData thisData: selectedSpectra)
				rowColNames.add(thisData.getName());
		
			initDisplayEucdDialog(rowColNames,data);
			
			displayEucdDialog.setVisible(true);
			return null;
			
		}
			
		@Override
		protected void done(){	
			changeState(7);
		}
		
	}
	
	/*
	 * SwingWorker for State 11
	 * Changes to state 7 when done
	 */
	class StateWorker11 extends SwingWorker{

		/* Load Spectra and add it to selectSpectraDialog */
		@Override
		protected Object doInBackground() throws Exception {
			ArrayList<SpectraData> input = null;
			
			try {
				input = spectraMain.loadSpectra();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(input != null) {
			
				Iterator<SpectraData> inputItr = input.iterator();

				while(inputItr.hasNext())
					selectSpectraDialog.addData(inputItr.next());
			}
			
			return null;
		}

		@Override
		protected void done(){	
			changeState(7);
		}
	}
	
	/*
	 * SwingWorker for State 12
	 * Changes to state 7 when done
	 */
	class StateWorker12 extends SwingWorker{

		/* Save selected Spectra from selectSpectraDialog */
		@Override
		protected Object doInBackground() throws Exception {
			SpectraData toBeSaved = selectSpectraDialog.getSelectedData();
			if(toBeSaved != null)
				spectraMain.saveSpectra(toBeSaved);
			return null;
		}

		@Override
		protected void done(){	
			changeState(7);
		}
	}
	
	/*
	 * SwingWorker for State 13
	 * Changes to state 7 when done
	 */
	class StateWorker13 extends SwingWorker{

		/* Retrieve selected region and extra Spectra Data for use as normalisation data */
		@Override
		protected Object doInBackground() throws Exception {
			
			//Retrieve selected region
			Rectangle selection = selectSpectraDialog.getSelectedRegion();
			
			//Retrieve calibration ratio
			String input = JOptionPane.showInputDialog("Set calibration ratio", 1.00);
			
			//If input is valid number
			if(NumberUtils.isCreatable(input)) {
				
				//Retrieve normalisation data
				spectraMain.calcNormalisationSpectra(imgData, selection, Double.parseDouble(input));
				
				//Enable adding of Spectra
				selectSpectraDialog.addButton.setEnabled(true);
			} else {
				ui.showDialog("Invalid input, please enter a number!");
			}	
			
			return null;
		}

		@Override
		protected void done(){	
			changeState(7);
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
						//Clear any previously chosen files
						imgData.clear();
						
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
						
						//retrieve nextStateIndex
						int nextStateIndex = toolPanelDialog.getNextStateIndex();					
						
						//Switch block to handle toolPanel buttons
						switch(nextStateIndex) {

							case 4:
								/* Handles pcaButton and changes state to 4 if successful */
								//Retrieve selection and clear it
								regionOfInterest = toolPanelDialog.getSelection();
								toolPanelDialog.clearSelection();

								//Reset nextState flag and state transition
								toolPanelDialog.setNextState(false);
								changeState(4);
								break;

							case 7:
								/* Handles spectraButton and changes state to 7 is successful */
								
								//Reset nextState flag and state transition
								toolPanelDialog.setNextState(false);
								changeState(7);
								break;

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
						selectedFeature = selectFeatureDialog.getSelected();
						selectFeatureDialog.setNextState(false);
						
						//Retrieve desired next state
						changeState(selectFeatureDialog.getNextStateIndex());			
					}					
				}		
			});
			
			selectFileDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			
		}//if selectFileDialog == null	
	}
	
	
	/*
	 * Initialises selectSpectraDialog
	 */
	public void initSelectSpectraDialog() {
		
		//Initialise selectSpectraDialog
		selectSpectraDialog = new BlotterSelectSpectraDialog();
		
		//Register services for selectSpectraDialog
		selectSpectraDialog.setServices(services);
		
		//Set title
		selectSpectraDialog.setTitle("Blotter - Select Spectra");
		
		//Add listener for closing of selectFeatureDialog
		selectSpectraDialog.addComponentListener(new ComponentAdapter() {		
			public void componentHidden(ComponentEvent e){
				//On setVisible(false) of selectFeatureDialog, 
				//if nextState is true, 
				if(selectSpectraDialog.getNextState()) {
					//Reset nextState flag
					selectSpectraDialog.setNextState(false);
					
					//Retrieve desired next state
					changeState(selectSpectraDialog.getNextStateIndex());			
				}					
			}		
		});
		
	}
	
	/*
	 * Initialises displayEucdDialog
	 */
	public void initDisplayEucdDialog(ArrayList<String> rowColNames, ArrayList<double[]> data) {
		
		//Initialise displayEucdDialog
		displayEucdDialog = new BlotterDisplayEucdDialog(rowColNames, data);
		
		//Set services
		displayEucdDialog.setServices(services);
		
		//Set title
		displayEucdDialog.setTitle("Euclidean distance between spectra");
		
		displayEucdDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
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


