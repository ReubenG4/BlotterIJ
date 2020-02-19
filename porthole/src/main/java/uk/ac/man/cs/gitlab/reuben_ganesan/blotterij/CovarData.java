package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import org.apache.commons.math4.linear.EigenDecomposition;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.stat.correlation.Covariance;

public class CovarData{
	
	private RealMatrix[] covariance = null;
	private int n;
	
	CovarData(double[][][] pxlData, int width, int height, int depth){
		
		
		
		//Initialise value for n
		n = width * height;
		
		//Initialise covariance matrices array
		covariance = new RealMatrix[depth];
		
		Covariance covProcessor;
		
		for(int index=0; index<depth; index++) {
			covProcessor = new Covariance(pxlData[index]);
			covariance[index] = covProcessor.getCovarianceMatrix();
		}
		
		
		//Covariance covProcessor = new Covariance(flattenedData);
		
		
	}
	
}