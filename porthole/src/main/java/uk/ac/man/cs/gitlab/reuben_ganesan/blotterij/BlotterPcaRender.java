package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.RealMatrix;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;
import ij.IJ;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;

public class BlotterPcaRender extends BlotterFunction{
	
	//Declare class variables
	ArrayList<PcaFeature> selectedFeatures;
	
	RealMatrix flattenedData;
	RealMatrix featureVector;
	RealMatrix rowDataAdjust;
	RealMatrix finalData;
	
	int width;
	int height;
	
	int noOfWavelengths;
	int noOfFeatures;
	
	double mean[];
	
	public BlotterPcaRender(PcaData pcaData, ArrayList<PcaFeature> input, Rectangle selection){
		
		flattenedData = pcaData.getFlattenedData();
		mean = pcaData.getMean();
		selectedFeatures = input;
		
		
		noOfWavelengths = pcaData.getNoOfWavelengths();
		noOfFeatures = input.size();
		
		width = selection.width;
		height = selection.height;
		
	}
	
	public void run() {
		
		assembleRowFeatureVector();
		
		if(flattenedData != null)
			assembleRowDataAdjust();
		
		IJ.showStatus("Ready for final transformation...");
		
		finalData = featureVector.multiply(rowDataAdjust);
		IJ.showMessage("Final data calculated");
		
		//renderFinalData();
		//renderPlot();
		
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
		rowDataAdjust = flattenedData;
		
		//Null pointer to flattenedData to allow for garbage collection
		flattenedData = null;
		
		int noOfPixels = rowDataAdjust.getColumnDimension();
		
		for(int indexCol=0; indexCol < noOfWavelengths; indexCol++) {
			IJ.showStatus("Calculating means-adjusted data");
			IJ.showProgress(indexCol, noOfWavelengths);
			
			//Get row, subtract mean of the row from each pixel, set adjusted row back
			double[] dataAdjusted = rowDataAdjust.getColumn(indexCol);
			
			for(int indexPxl=0; indexPxl < noOfPixels; indexPxl++)
				dataAdjusted[indexPxl] = dataAdjusted[indexPxl] - mean[indexCol];
			
			rowDataAdjust.setColumn(indexCol, dataAdjusted);
		}
		
		IJ.showProgress(1,1);
		//Transpose mean-adjusted data
		rowDataAdjust = rowDataAdjust.transpose();
	}
	
	
	public void renderPlot() {
		
		//Prepare series data
		double yData[] = finalData.getRow(0);
		
		List<Double> yDataList = new ArrayList<Double>();
		
		for(int index=0; index<finalData.getColumnDimension(); index++)
			yDataList.add(yData[index]);
		
		
		List<Integer> xDataList = new ArrayList<Integer>();
		for(int index=0; index<finalData.getColumnDimension(); index++)
			xDataList.add(index);
			
		 // Create Chart
		XYChart chart = new XYChartBuilder().width(800).height(600).build();
		
		// Customize Chart
		chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
	    chart.getStyler().setChartTitleVisible(false);
	    chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
	    chart.getStyler().setMarkerSize(1);
	    
	    //Add series to chart
	    XYSeries series = chart.addSeries("Vector 0", xDataList, yDataList);
		
	    //Display chart
		new SwingWrapper<XYChart>(chart).displayChart();
	}
	
	public Img renderFinalData() {
		
		ArrayImg<DoubleType,DoubleArray> newImg = ArrayImgs.doubles(width,height); 
		Cursor<DoubleType> curs = newImg.cursor();
		
		int rowIndex=0;
		double[] row = finalData.getRow(0);
		
		while(curs.hasNext()) {
			curs.fwd();
			curs.get().set(row[rowIndex++]);
		}
		
	  return newImg;
   }
	
	public void setSelectedFeatures(ArrayList<PcaFeature> input) {
		selectedFeatures = input;
		
	}
	
}