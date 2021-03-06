package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.math4.linear.Array2DRowRealMatrix;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.SeriesMarkers;
import ij.IJ;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/* Assembles SpectraData from PxlData and plot SpectraData */
public class BlotterSpectraMain <T extends RealType<T> & NativeType<T>>extends BlotterFunction{
	
	String lastOpenDirectory = null;
	
	Array2DRowRealMatrix normalisationData = null;
	
	double calibRatio = 1;
	
	/* Constructor */
	public BlotterSpectraMain() {
	
	}
	
	//Assembles SpectraData from PxlData
	public SpectraData<T> calcSpectra(ArrayList<ImgWrapper<T>> imgData, Rectangle selection) {
		
		//If no normalisation data available, use constructor that omits normalisationData and calibration ratio
		if(normalisationData == null)
			return new SpectraData(imgData,selection);
		else
			return new SpectraData(imgData, selection, normalisationData, calibRatio);
	}
	
	public void calcNormalisationSpectra(ArrayList<ImgWrapper<T>> imgData, Rectangle selection, double calibRatio) {
		normalisationData = new SpectraData(imgData,selection).getRawData();
		this.calibRatio = calibRatio;
	}
	
	//Saves the chosen spectra
	public void saveSpectra(SpectraData<T> output) {
	
		//Declare variables
		JFileChooser jfc;
		
		//Initialise fileChooser and filefilter
		//If no last open directory, use default directory
		if(lastOpenDirectory == null)
			jfc = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
		else
			jfc = new JFileChooser(lastOpenDirectory);
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".spec", "spec");
		jfc.setFileFilter(filter);
		
		//Set filename to default
		String defaultFilename = output.getName() + ".spec";
		jfc.setSelectedFile(new File(defaultFilename));
		
		//Show save dialog
		int returnValue = jfc.showSaveDialog(null);
		File selectedFile;
		
		//If no value given, return
		//Else pass on selectedFile
		if (returnValue == JFileChooser.APPROVE_OPTION) 
			selectedFile = jfc.getSelectedFile();
		else 
			return;
		
		//If extension is not 'spec', append it
		if (!FilenameUtils.getExtension(selectedFile.getName()).equalsIgnoreCase("spec")) 
		    selectedFile = new File(selectedFile.getParentFile(), FilenameUtils.getBaseName(selectedFile.getName())+".spec"); 
		
		// Write object to file
		try {
			FileOutputStream f = new FileOutputStream(selectedFile);
			ObjectOutputStream o = new ObjectOutputStream(f);

			o.writeObject(output);

			o.close();
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		}
		
		//Set last open directory
		lastOpenDirectory = selectedFile.getPath();
	}
	
	public ArrayList<SpectraData<T>> loadSpectra() throws IOException, ClassNotFoundException {
		//Declare function variables
		FileInputStream fi = null;
		ObjectInputStream oi;
		JFileChooser jfc;
		
		//Initialise data structures
		File[] selectedFiles = null;
		ArrayList<SpectraData<T>> inputData = new ArrayList<SpectraData<T>>();
		
		//Retrieve chosen files
		
		//Initalise file chooser and file filter
		//If no last open directory, use default directory
		if(lastOpenDirectory == null)
			jfc = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
		else
			jfc = new JFileChooser(lastOpenDirectory);
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".spec", "spec");
		jfc.setFileFilter(filter);
		jfc.setMultiSelectionEnabled(true);
				
		//Show load dialog
		int returnValue = jfc.showOpenDialog(null);
		
		//If no value given, return
		//Else pass on selectedFile
		if (returnValue == JFileChooser.APPROVE_OPTION) 
			selectedFiles = jfc.getSelectedFiles();
		else 
			return null;

		int noOfFiles = selectedFiles.length;
		
		//Iterate through selected files and cast them to Spectra
		for(int index=0; index < noOfFiles; index++) {
		
		
			fi = new FileInputStream(selectedFiles[index]);
			oi = new ObjectInputStream(fi);
			
			SpectraData<T> inputSpectra = (SpectraData<T>) oi.readObject();
			inputData.add(inputSpectra);
		}
		
		//set last open directory
		if(noOfFiles > 0)
			lastOpenDirectory = selectedFiles[0].getPath();
		
		//Return spectra
		return inputData;
	}

	
	
	public XYChart plotSpectra(ArrayList<SpectraData<T>> input) {
		
		Iterator<SpectraData<T>> itr = input.iterator();
		
		//Prepare chart
		XYChart chart = new XYChartBuilder().width(800).height(600).theme(ChartTheme.Matlab).build();
		chart.setTitle("");
		chart.setXAxisTitle("Wavelength");
		chart.setYAxisTitle("Spectral Reflectance");
		
		//Iterate through ArrayList input
		while(itr.hasNext()) {
			
			//Retrieve data and place it in arrays
			SpectraData<T> curr = itr.next();
			
			Array2DRowRealMatrix currData = curr.getNormalisedData();
			
			int rowIndex = currData.getRowDimension();
			
			//Prepare series for plotting
			double[] xData = new double[rowIndex];
			double[] yData = new double[rowIndex];
			
			for(int index=0; index < rowIndex; index++) {
				xData[index] = currData.getRow(index)[0];
				yData[index] = currData.getRow(index)[1];
			}
				
			//Add series to chart
			XYSeries series = chart.addSeries(curr.getName(), xData, yData);
		    series.setMarker(SeriesMarkers.NONE);
		}
		
		return chart;
	}
	
	public double euclideanDistance(SpectraData<T> spectra1, SpectraData<T> spectra2) {
		
		//Check to see if both spectras have an equal amount of datapoints
		if(spectra1.getNoOfWavelengths() != spectra2.getNoOfWavelengths()) {
			IJ.showMessage("Spectras chosen must have an equal amount of wavelengths!");
			return -1;
		}
		
		Array2DRowRealMatrix data1 = spectra1.getNormalisedData();
		Array2DRowRealMatrix data2 = spectra2.getNormalisedData();
	
		int rowIndex = data1.getRowDimension();
		double distance = 0;
		
		for(int index = 0; index < rowIndex; index++) {
			
			if(data1.getRow(index)[0] != data2.getRow(index)[0]) {
				IJ.showMessage("Wavelengths recorded for Spectra do not match!");
				return -1;
			}
				
			//Get xy coordinates
			double y1 = data1.getRow(index)[1];
			double y2 = data2.getRow(index)[1];
			
			//sum up euclidean distance
			distance += Math.pow(y1 - y2, 2); 
		}
		
		return Math.sqrt(distance);
		
	}
	
}