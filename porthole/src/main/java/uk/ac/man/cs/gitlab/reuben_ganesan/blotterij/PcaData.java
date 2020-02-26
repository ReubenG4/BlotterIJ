package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

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
	private RealMatrix flattenedData = null;
	
	private int noOfWavelengths;
	private int noOfPixels;
	
	PcaData(PxlData input){
		this(input.getData(),input.getWidth(),input.getHeight(), input.getDepth());
	}
	
	PcaData(double[][][] input, int width, int height, int noOfWavelengths){
		
		//Find out number of pixels in a single dataset
		this.noOfPixels = width * height;
		
		this.noOfWavelengths = noOfWavelengths;
		
		//Initialise Matrix to hold flattened pxlData
		flattenedData = new BlockRealMatrix(noOfWavelengths, noOfPixels);
		
		//Initialise Matrix to hold covariance
		covariance = new Array2DRowRealMatrix(noOfWavelengths,noOfWavelengths);
		
		//Initialise array to hold mean for each dataset
		mean = new double[noOfWavelengths];
		
		IJ.showStatus("Flattening pixel data...");
		
		/* Flatten pxlData[z][y][x] to produce flattenedData[z][p]*/
		for (int index=0; index < noOfWavelengths; index++) {
			flattenedData.setRow(index, Stream.of(input[index]).flatMapToDouble(DoubleStream::of).toArray());
		}
		
		//Calculate the mean of each dataset
		calcMean();
		
		//Calculate the covariance of datasets
		calcCovariance();
		
		//Calculate eigenvectors and eigenvalues
		IJ.showStatus("Calculating Eigen decomposition...");
		eigenData = new EigenDecomposition(covariance);
		IJ.showStatus("Eigen decomposition calculated...");
	
				
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
		
	public ArrayList<PcaFeature> getFeatureList(){
		
		ArrayList<PcaFeature> features = new ArrayList<PcaFeature>();
		
		for(int index = 0; index < noOfWavelengths; index++) {
			features.add(new PcaFeature(getEigenvector(index),getEigenvalue(index)));
		}
		
		
		return features;
		
	}
	
		
	public void calcMean() {	
		
		//Declare function variables
		double xSum;
		
		/*
		 * Let y be row position,dimension
		 * 	   x be column position, pixel value
		 */
		
		//Initialise progress bar
		IJ.showStatus("Calculating Mean of Datasets...");
		IJ.showProgress(0, noOfWavelengths);
		
		/* Calculate Mean
		 *	Mean of pixels across a dimension
		 */
	
		for(int yIndex=0; yIndex<noOfWavelengths; yIndex++) {
			//Reset values of xSum
			xSum = 0;
			
			for(int xIndex=0; xIndex<noOfPixels; xIndex++) {
				//Sum up value of all pixels in this dimension
				xSum += flattenedData.getEntry(yIndex,xIndex);
			}
			//Find mean for pixel value in this dimension
			mean[yIndex] = xSum / noOfPixels;
			
			//Update progress bar
			IJ.showStatus("Calculating Mean of Datasets...");
			IJ.showProgress(yIndex, noOfWavelengths);	
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
				for(int xIndex=0; xIndex < noOfPixels; xIndex++) {
					sumDim1Dim2 += ( flattenedData.getEntry(dim1,xIndex) - mean[dim1] ) *
								   ( flattenedData.getEntry(dim2,xIndex) - mean[dim2] );
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
	
	
}