package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;
import org.apache.commons.math4.linear.RealMatrix;

import ij.IJ;

public class SpectraData{
	
	private String name;
	private int noOfPixels;
	private int noOfWavelengths;
	private ArrayList<Double> mean;
	private Rectangle selection;
	
	SpectraData(PxlData input){
		
		//Find out number of pixels in a single dataset
		this.noOfPixels = input.getWidth() * input.getHeight();
		
		//Get number of wavelengths
		this.noOfWavelengths = input.getDepth();
		
		//Initialise array to hold mean
		mean = new ArrayList<Double>();
		
		//Initialise selection
		selection = input.getSelection();
		
		//Initialise Matrix to hold flattened pxlData
		RealMatrix flattenedData = input.flatten();	
		
		//Calculate the mean of pixel data at each wavelength
		calcMean(flattenedData);
		
				
	}
	
	public void calcMean(RealMatrix flattenedData) {	
		
		//Declare function variables
		double pxlSum;
		
		/*
		 * Let x be row position, pixel values
		 * 	   y be column position, wavelength dimension
		 */
		
		//Initialise progress bar
		IJ.showStatus("Calculating Mean of Datasets...");
		IJ.showProgress(0, noOfWavelengths);
		
		/* Calculate Mean
		 *	Mean of pixels across a dimension
		 */
		
		for(int yIndex = 0; yIndex > noOfWavelengths; yIndex++) {
			
			pxlSum = 0;
			for(int xIndex = 0; xIndex > noOfPixels; xIndex++) {
				pxlSum += flattenedData.getEntry(xIndex, yIndex);
			}
			mean.add( pxlSum / noOfPixels);	
		}
			
		//Reset progress bar
		IJ.showProgress(1,1);
		IJ.showStatus("Mean calculation done...");
	}
	
	public ArrayList<Double> getVector() {
		return mean;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Rectangle getSelection() {
		return selection;
	}

	public void setSelection(Rectangle selection) {
		this.selection = selection;
	}
	
}