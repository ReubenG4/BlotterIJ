package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.RealMatrix;

import ij.IJ;

public class BlotterPcaRender{
	
	//Declare class variables
	ArrayList<PcaFeature> selectedFeatures;
	
	RealMatrix flattenedData;
	RealMatrix featureVector;
	RealMatrix rowDataAdjust;
	
	int noOfWavelengths;
	int noOfFeatures;
	
	double mean[];
	
	public BlotterPcaRender(PcaData pcaData, ArrayList<PcaFeature> input){
		
		flattenedData = pcaData.getFlattenedData();
		mean = pcaData.getMean();
		selectedFeatures = input;
		
		noOfWavelengths = pcaData.getNoOfWavelengths();
		noOfFeatures = input.size();
		
		IJ.showMessage("Calculating Feature Vector...");
		assembleRowFeatureVector();
		IJ.showMessage("Calculating means-adjusted data");
		assembleRowDataAdjust();
		IJ.showMessage("Ready for final transformation...");
	}
		
	public void assembleRowFeatureVector() {
		
		//Place vectors as columns of feature vector, in descending order of eigenvalue
		featureVector = new Array2DRowRealMatrix(noOfWavelengths,noOfFeatures);
		Iterator<PcaFeature> itr = selectedFeatures.iterator();
		int index = 0;
		while(itr.hasNext())
			featureVector.setColumnVector(index++, itr.next().getVector());
		
		//Transpose feature vector
		featureVector = featureVector.transpose();
		
	}
	
	public void assembleRowDataAdjust() {
		
		//Adjust data to produce mean-adjusted data
		rowDataAdjust = flattenedData.copy();
		int noOfPixels = rowDataAdjust.getRowDimension();
		
		for(int indexRow=0; indexRow < noOfWavelengths; indexRow++) {
			//Get row, subtract mean of the row from each pixel, set adjusted row back
			double[] rowAdjusted = rowDataAdjust.getRow(indexRow);
			
			for(int indexPxl=0; indexPxl < noOfPixels; indexPxl++)
				rowAdjusted[indexPxl] -= mean[indexRow];
			
			rowDataAdjust.setRow(indexRow, rowAdjusted);
		}
		
		//Transpose mean-adjusted data
		rowDataAdjust = rowDataAdjust.transpose();
	}
	
}