package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.RealMatrix;

import ij.IJ;

public class SpectraData implements Serializable{
	
	private String name;
	private int noOfPixels;
	private int noOfWavelengths;
	private Array2DRowRealMatrix data;
	private Rectangle selection;
	
	SpectraData(PxlData input){
		
		//Find out number of pixels in a single dataset
		this.noOfPixels = input.getWidth() * input.getHeight();
		
		//Get number of wavelengths
		this.noOfWavelengths = input.getDepth();
		
		//Initialise array to hold data
		data = new Array2DRowRealMatrix(2,noOfWavelengths);
		
		//Initialise selection
		selection = input.getSelection();
		
		//Initialise name
		name = "Region";
		
		//Initialise Matrix to hold flattened pxlData
		RealMatrix flattenedData = input.flatten();	
		
		//Initialise arraylist to hold wavelengths
		ArrayList<Integer> wavelengths = input.getWavelengths();
		
		//Calculate the mean of pixel data at each wavelength
		calc(flattenedData, wavelengths);
				
	}
	
	SpectraData(ArrayList<ImgWrapper> imgData, Rectangle selection){
		this(new PxlData(imgData,selection));
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
		
		for(int yIndex = 0; yIndex > noOfWavelengths; yIndex++) {
			
			pxlSum = 0;
			for(int xIndex = 0; xIndex > noOfPixels; xIndex++) {
				pxlSum += flattenedData.getEntry(xIndex, yIndex);
			}
			data.setRow(yIndex, new double[]{wavelengths.get(yIndex),pxlSum});
		}
			
		//Reset progress bar
		IJ.showProgress(1,1);
		IJ.showStatus("Spectra calculation done...");
	}
	
	public RealMatrix getData(int row) {
		return data.getRowMatrix(row);
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

	public RealMatrix getData() {
		return data;
	}

	public void setData(Array2DRowRealMatrix data) {
		this.data = data;
	}
	
}