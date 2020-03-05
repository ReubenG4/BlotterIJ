package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import org.apache.commons.math4.linear.RealMatrix;

public class SpectraData{
	
	private RealMatrix flattenedData = null;
	private int noOfPixels;
	private int noOfWavelengths;
	
	SpectraData(PxlData input){
		
		//Find out number of pixels in a single dataset
		this.noOfPixels = input.getWidth() * input.getHeight();
		
		this.noOfWavelengths = input.getDepth();
		
		//Initialise Matrix to hold flattened pxlData
		flattenedData = input.flatten();	
	
				
	}
	
	
	
}