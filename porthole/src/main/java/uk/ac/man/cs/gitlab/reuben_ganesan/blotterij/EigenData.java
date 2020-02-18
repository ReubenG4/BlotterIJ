package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;


public class EigenData{
	
	private RealMatrix covariance = null;
	private RealMatrix flattenedData = null;
	private EigenDecomposition eigenDecomp = null;
	
	EigenData(double[][][] pxlData, int width, int height, int depth){
		
		//Initialise variables to hold pxlData dimensions
		int noOfPixels = width * height;
		
		flattenedData = new Array2DRowRealMatrix(depth, noOfPixels);
		
		for (int index=0; index < depth; index++)
			flattenedData.setRow(index, Stream.of(pxlData[index]).flatMapToDouble(DoubleStream::of).toArray());
		
		
		Covariance covProcess = new Covariance(flattenedData,false);
		
		
	}
	
}