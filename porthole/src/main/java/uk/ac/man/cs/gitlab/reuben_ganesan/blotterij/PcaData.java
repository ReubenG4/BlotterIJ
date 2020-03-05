package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.BlockRealMatrix;
import org.apache.commons.math4.linear.EigenDecomposition;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.linear.RealVector;
import ij.IJ;
/*
 * Calculates eigenvalues and eigenvectors from covariance matrix of pixel data
 * 
 */

public class PcaData{
	
	private EigenDecomposition eigenData = null;
	private double[] mean = null;
	private RealMatrix covariance = null;
	private RealMatrix meanDataAdjust = null;
	private RealMatrix featureVector = null;
	
	private int noOfWavelengths;
	private int noOfPixels;
	
	PcaData(PxlData input){
		
		//Find out number of pixels in a single dataset
		this.noOfPixels = input.getWidth() * input.getHeight();
		
		this.noOfWavelengths = input.getDepth();	
		
		//Initialise Matrix to hold covariance
		covariance = new Array2DRowRealMatrix(noOfWavelengths,noOfWavelengths);
		
		//Initialise array to hold mean for each dataset
		mean = new double[noOfWavelengths];
		
		//Initialise Matrix to hold flattened pxlData
		RealMatrix flattenedData = input.flatten();
	
		//Calculate the mean of each dataset
		calcMean(flattenedData);
		
		//Calculate mean adjusted data
		calcDataMeanAdjust(flattenedData);
		
		//Calculate the covariance of datasets
		calcCovariance();
		
		//Calculate eigenvectors and eigenvalues
		IJ.showStatus("Calculating Eigen decomposition...");
		eigenData = new EigenDecomposition(covariance);
		IJ.showStatus("Eigen decomposition calculated...");
	
				
	}
	
	public RealMatrix calcFinalData(ArrayList<PcaFeature> selectedFeatures) {
		
		assembleRowFeatureVector(selectedFeatures);
		return featureVector.multiply(meanDataAdjust);
	}
	
	public void assembleRowFeatureVector(ArrayList<PcaFeature> selectedFeatures) {
		
		//Place vectors as columns of feature vector, in descending order of eigenvalue
		featureVector = new Array2DRowRealMatrix(noOfWavelengths,selectedFeatures.size());
		Iterator<PcaFeature> itr = selectedFeatures.iterator();
		int index = 0;
		while(itr.hasNext()) {
			IJ.showStatus("Calculating Feature Vector...");
			IJ.showProgress(index, selectedFeatures.size());
			featureVector.setColumnVector(index++, itr.next().getVector());
		}
		IJ.showProgress(1,1);
		//Transpose feature vector
		featureVector = featureVector.transpose();
		
	}
	
	public void calcDataMeanAdjust(RealMatrix flattenedData) {
		
		//Adjust data to produce mean-adjusted data
		meanDataAdjust = flattenedData;
		
		//Null pointer to flattenedData, it is no longer relevant
		flattenedData = null;
		
		for(int indexCol=0; indexCol < noOfWavelengths; indexCol++) {
			IJ.showStatus("Calculating means-adjusted data");
			IJ.showProgress(indexCol, noOfWavelengths);
			
			//Get row, subtract mean of the row from each pixel, set adjusted row back
			RealMatrix dataAdjusted = meanDataAdjust.getColumnMatrix(indexCol);
			
			dataAdjusted.scalarAdd(-mean[indexCol]);
			
			meanDataAdjust.setColumnMatrix(indexCol, dataAdjusted);
		}
		
		IJ.showProgress(1,1);
		
		//Transpose mean-adjusted data
		meanDataAdjust = meanDataAdjust.transpose();
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
			mean[yIndex] = pxlSum / noOfPixels;	
		}
			
		//Reset progress bar
		IJ.showProgress(1,1);
		IJ.showStatus("Mean calculation done...");
	}
	
	public void calcCovariance() {
		
		//Takes advantage of cov(a,b) = cov(b,a)
		//Only calculates covariances below diagonal of covariance matrice and copies it over
		//Ceiling is lowered by one each time dim2 finishes iteration
		int ceiling = noOfWavelengths;
		int floor = noOfWavelengths;
		double sumDim1Dim2 = 0;
		double result = 0;
		int progress = 0;
		int finish = noOfWavelengths * noOfWavelengths;
		
		//Update progress bar
		IJ.showStatus("Calculating covariance of Datasets...");
		IJ.showProgress(progress, finish);	
		
		/*
		 * Let dim1,x be position of the index for dimension 1
		 * 	   dim2,x be position of the index for dimension 2
		 */
		
		//Iterate through x-axis of covariance matrix
		for(int dim1 = 0; dim1 < floor; dim1++) {
			
			//Iterate through y-axis of covariance matrix
			for(int dim2 = 0; dim2 < ceiling; dim2++) {
				
				//Reset value of sumDim1Dim2
				sumDim1Dim2 = 0;
				
				// sum[(dim1 pxl value - dim1 pxl mean)(dim2 pxl value - dim2 pxl mean)]
				for(int yIndex=0; yIndex < noOfPixels; yIndex++) {
					sumDim1Dim2 += ( meanDataAdjust.getEntry(dim1,yIndex) ) *
								   ( meanDataAdjust.getEntry(dim2,yIndex) );
				}
				
				//Divide by n-1
				result = sumDim1Dim2 / (noOfPixels - 1);
				
				//If dim1 doesn't match dim2, not on diagonal, set cov(dim1,dim2) = cov(dim2,dim1) = result
				if(dim1 != dim2) {
					covariance.setEntry(dim2,dim1, result);
					progress++;
				}
				//Set cov(dim1,dim2) = result
				covariance.setEntry(dim1,dim2,result);
				progress++;
				
				//Update progress bar
				IJ.showStatus("Calculating covariance of Datasets...");
				IJ.showProgress(progress, finish);
						
			}
			
			//Reduce height of ceiling
			ceiling--;
		}
		
		//Reset progress bar
		IJ.showProgress(1,1);
		IJ.showStatus("Covariance matrix calculated....");
		
		return;
	}
	
	public PcaFeature getFeature(int index) {
		return new PcaFeature(getEigenvector(index),getEigenvalue(index));
	}
	
	public RealVector getEigenvector(int index) {
		return eigenData.getEigenvector(index);
	}
	
	public double getEigenvalue(int index){
		return eigenData.getRealEigenvalue(index);
	}
	
	public double[] getMean() {
		return mean;
	}
	
	public int getNoOfWavelengths() {
		return noOfWavelengths;
	}
		
	public ArrayList<PcaFeature> getFeatureList(){
		
		ArrayList<PcaFeature> features = new ArrayList<PcaFeature>();
		
		for(int index = 0; index < noOfWavelengths; index++) {
			features.add(getFeature(index));
		}
		
		
		return features;
		
	}
	
}