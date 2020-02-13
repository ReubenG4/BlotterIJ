package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import Jama.Matrix;

public class CovarianceData<T>{
	private long n = 0;
	private double xSum = 0;
	private double ySum = 0;
	private double aSum = 0;
	
	private double xMean = 0;
	private double yMean = 0;
	
	private Matrix covariance = null;
	private double[][][] pxlData = null;
	
	//Calculates the mean for X, Y
	public double[] calcMean(double[][][] pxlData,int width, int height, int depth) {
		//Initialise local pixel data
		this.pxlData = pxlData;
		
		//Iterate through pixelData
		for(int y=0; y < height; y++) {
			for(int x=0; x <width; x++) {
					
			}
		}
		
		
		
		
		
		return null;
		
	}
	
	
	
	
}