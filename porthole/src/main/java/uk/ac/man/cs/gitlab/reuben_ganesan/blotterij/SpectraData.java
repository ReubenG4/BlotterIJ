package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.RealMatrix;

import ij.IJ;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class SpectraData<T extends RealType<T> & NativeType<T>> implements Serializable{
	
	private String name;
	private int noOfPixels;
	private int noOfWavelengths;
	
	//Hardcoded for now
	private double normalisationRatio = 1;
	
	private Array2DRowRealMatrix data;
	private Array2DRowRealMatrix normalisationData;
	private Rectangle selection;
	
	SpectraData(PxlData2<T> input){
		
		//Find out number of pixels in a single dataset
		this.noOfPixels = input.getWidth() * input.getHeight();
		
		//Get number of wavelengths
		this.noOfWavelengths = input.getDepth();
		
		//Initialise array to hold data
		data = new Array2DRowRealMatrix(noOfWavelengths,2);
		
		//Initialise selection
		selection = input.getSelection();
		
		//Initialise name
		name = null;
		
		//Initialise Matrix to hold flattened pxlData
		RealMatrix flattenedData = input.flatten();	
		
		//Initialise arraylist to hold wavelengths
		ArrayList<Integer> wavelengths = input.getWavelengths();
		
		//Calculate the mean of pixel data at each wavelength
		calc(flattenedData, wavelengths);
				
	}
	
	SpectraData(PxlData2<T> input, Array2DRowRealMatrix calibrationData){
		this(input);
		this.normalisationData = calibrationData;
	}
	
	SpectraData(ArrayList<ImgWrapper<T>> imgData, Rectangle selection){
		this(new PxlData2(imgData,selection));
	}
	
	public void calc(RealMatrix flattenedData, ArrayList<Integer> wavelengths) {	
		
		//Declare function variables
		double pxlSum;
		
		/*
		 * Let x be row position, pixel values
		 * 	   y be column position, wavelength dimension
		 */
		
		//Initialise progress bar
		IJ.showStatus("Calculating mean of Spectra...");
		IJ.showProgress(0, noOfWavelengths);
		
		/* Calculate Mean
		 *	Mean of pixels across a dimension
		 */
		
		for(int yIndex = 0; yIndex < noOfWavelengths; yIndex++) {
			
			pxlSum = 0;
			for(int xIndex = 0; xIndex < noOfPixels; xIndex++) {
				pxlSum += flattenedData.getEntry(xIndex, yIndex);
			}
			
			//Prepare data for row
			double[] newData = new double[2];
			newData[0] = wavelengths.get(yIndex);
			newData[1] = pxlSum / noOfPixels;
			
			data.setRow(yIndex, newData);
		}
			
		//Reset progress bar
		IJ.showProgress(1,1);
		IJ.showStatus("Spectra calculation done...");
	}
	
	//Return value after calibration
	public RealMatrix getCalibratedData() {
		
		//declare variables
		double dataVal;
		double calibrateVal;
		double finalVal;
	
		//final value = (recorded data value / recorded calibration value) * calibration ratio
		RealMatrix returnObj = data.copy();
		
		//iterate through data and 
		for(int rowIndex=0; rowIndex < noOfWavelengths; rowIndex++) {

			dataVal = data.getRow(rowIndex)[1];
			calibrateVal = normalisationData.getRow(rowIndex)[1];
			finalVal = (dataVal / calibrateVal) * normalisationRatio;

			returnObj.setEntry(rowIndex, 1, finalVal);
		}

		return returnObj;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getNoOfWavelengths() {
		return this.noOfWavelengths;
	}
	
	public int getNoOfPixels() {
		return this.noOfPixels;
	}
	
	
	public Rectangle getSelection() {
		return selection;
	}

	public void setSelection(Rectangle selection) {
		this.selection = selection;
	}

	public Array2DRowRealMatrix getRawData() {
		return data;
	}

	public void setRawData(Array2DRowRealMatrix data) {
		this.data = data;
	}

	public void setNoOfPixels(int value) {
		this.noOfPixels = value;
	}
	
	public void setNoOfWavelengths(int value) {
		this.noOfWavelengths = value;
	}
	
}