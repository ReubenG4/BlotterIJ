package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.RealMatrix;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import ij.IJ;

public class BlotterPcaRender{
	
	//Declare class variables
	ArrayList<PcaFeature> selectedFeatures;
	
	RealMatrix flattenedData;
	RealMatrix featureVector;
	RealMatrix rowDataAdjust;
	RealMatrix finalData;
	
	int noOfWavelengths;
	int noOfFeatures;
	
	double mean[];
	
	public BlotterPcaRender(PcaData pcaData, ArrayList<PcaFeature> input){
		
		flattenedData = pcaData.getFlattenedData();
		mean = pcaData.getMean();
		selectedFeatures = input;
		
		noOfWavelengths = pcaData.getNoOfWavelengths();
		noOfFeatures = input.size();
		
		
		assembleRowFeatureVector();
		assembleRowDataAdjust();
		IJ.showStatus("Ready for final transformation...");
		
		finalData = featureVector.multiply(rowDataAdjust);
		IJ.showMessage("Final data calculated");
		
		renderPlot();
	}
		
	public void assembleRowFeatureVector() {
		
		//Place vectors as columns of feature vector, in descending order of eigenvalue
		featureVector = new Array2DRowRealMatrix(noOfWavelengths,noOfFeatures);
		Iterator<PcaFeature> itr = selectedFeatures.iterator();
		int index = 0;
		while(itr.hasNext()) {
			IJ.showStatus("Calculating Feature Vector...");
			IJ.showProgress(index,noOfFeatures);
			featureVector.setColumnVector(index++, itr.next().getVector());
		}
		IJ.showProgress(1,1);
		//Transpose feature vector
		featureVector = featureVector.transpose();
		
	}
	
	public void assembleRowDataAdjust() {
		
		//Adjust data to produce mean-adjusted data
		rowDataAdjust = flattenedData.copy();
		int noOfPixels = rowDataAdjust.getColumnDimension();
		
		for(int indexCol=0; indexCol < noOfWavelengths; indexCol++) {
			IJ.showStatus("Calculating means-adjusted data");
			IJ.showProgress(indexCol, noOfWavelengths);
			
			//Get row, subtract mean of the row from each pixel, set adjusted row back
			double[] dataAdjusted = rowDataAdjust.getColumn(indexCol);
			
			for(int indexPxl=0; indexPxl < noOfPixels; indexPxl++)
				dataAdjusted[indexPxl] -= mean[indexCol];
			
			rowDataAdjust.setColumn(indexCol, dataAdjusted);
		}
		
		IJ.showProgress(1,1);
		//Transpose mean-adjusted data
		rowDataAdjust = rowDataAdjust.transpose();
	}
	
	
	public void renderPlot() {
		double yData[] = finalData.getRow(0);
		double xData[] = new double[finalData.getColumnDimension()];
		
		for(int index = 0; index < finalData.getColumnDimension(); index++)
			xData[index] = index;
		
		
		XYChart chart = QuickChart.getChart("Final Data", "Pixels", "Value", "FinalData", xData, yData);
		
		new SwingWrapper(chart).displayChart();
	}
	
}