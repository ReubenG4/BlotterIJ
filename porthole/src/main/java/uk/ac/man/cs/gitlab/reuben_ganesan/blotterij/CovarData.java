package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.BlockRealMatrix;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.stat.correlation.Covariance;

import ij.IJ;

public class CovarData{
	
	private RealMatrix covariance = null;
	private RealMatrix flattenedData = null;
	private double[] mean = null;
	
	private int width;
	private int height;
	private int noOfWavelengths;
	private int noOfPixels;
	
	CovarData(double[][][] pxlData, int width, int height, int noOfWavelengths){
		
		//Initialise class variables
		this.width = width;
		this.height = height;
		
		//Find out number of pixels in a single dataset
		noOfPixels = width * height;
		
		//Initialise Matrix to hold flattened pxlData
		flattenedData = new BlockRealMatrix(noOfWavelengths, noOfPixels);
		
		//Initialise Matrix to hold covariance
		covariance = new Array2DRowRealMatrix(noOfWavelengths,noOfWavelengths);
		
		//Initialise array to hold mean
		mean = new double[noOfWavelengths];
		
		IJ.showStatus("Flattening pixel data...");
		
		/* Flatten pxlData[z][x][y] to produce flattenedData[z][p], where p are the pixels visited in row order*/
		for (int index=0; index < noOfWavelengths; index++) {
			flattenedData.setRow(index, Stream.of(pxlData[index]).flatMapToDouble(DoubleStream::of).toArray());
		}
			
		//Calculate the mean of each dataset
		calcMean();
		
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
		 *	Mean of pixels across a datasets 
		 */
	
		for(int yIndex=0; yIndex<noOfWavelengths; yIndex++) {
			//Reset value of xSum
			xSum = 0;
		
			for(int xIndex=0; xIndex<noOfPixels; xIndex++) {
				//Sum up value of all pixels in this dataset
				xSum += flattenedData.getEntry(yIndex,xIndex);
			}
			//Find mean for pixel value in this dataset
			mean[yIndex] += xSum / noOfPixels;
			
			//Update progress bar
			IJ.showStatus("Calculating Mean of Datasets...");
			IJ.showProgress(yIndex, noOfWavelengths);	
		}
		
		//Reset progress bar
		IJ.showProgress(1,1);
		IJ.showStatus("Mean calculation done...");
	}
	
	public void calcCovariance() {
		return;
	}
	
	public RealMatrix getCovariance() {
		//Calculate covariance
		return covariance;
	}
	
	
	
}