package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;
import org.apache.commons.math4.linear.RealMatrix;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.LegendPosition;

import ij.IJ;
import net.imglib2.Cursor;
import net.imglib2.histogram.Histogram1d;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;

public class BlotterPcaRender extends BlotterFunction{
	
	//Declare class variables
	RealMatrix finalData;
	PcaData pcaData;
	
	int width;
	int height;
	int size;
	
	int noOfWavelengths;
	int noOfFeatures;
	
	double mean[];
	
	public BlotterPcaRender(PcaData pcaData,Rectangle selection){
		
		this.pcaData = pcaData;
		width = selection.width;
		height = selection.height;
		size = width * height;
		
	}
	
	public Histogram1d histogram(ArrayList<PcaFeature> selectedFeatures) {
		
		return getOpsService().image().histogram(renderImg(selectedFeatures),256);
	}
	
	
	public XYChart plot(ArrayList<PcaFeature> selectedFeatures) {
		
		//Calculate data for plot
		IJ.showStatus("Calculating final data");
		finalData = pcaData.calcFinalData(selectedFeatures);
		IJ.showStatus("Converting final data to plot...");
		
		//Prepare series data
		double[] yData = finalData.getRow(0);
			
		 // Create Chart
		XYChart chart = new XYChartBuilder().width(800).height(600).build();
		
		// Customize Chart
		 chart.getStyler().setChartTitleVisible(true);
		 chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
		 chart.getStyler().setXAxisLabelRotation(45);
	     chart.getStyler().setMarkerSize(1);
		 
		 
	    //Add series to chart
	    XYSeries series = chart.addSeries("Vector 0", yData);
	    
	    chart.setYAxisTitle("Pixel Value");
	    chart.setXAxisTitle("Location");
		
		return chart;
	}
	
	
	public Img renderImg(ArrayList<PcaFeature> selectedFeatures) {
		
		IJ.showStatus("Calculating final data");
		finalData = pcaData.calcFinalData(selectedFeatures);
		IJ.showStatus("Converting final data to image...");
		
		
		ArrayImg<DoubleType,DoubleArray> newImg = ArrayImgs.doubles(width,height); 
		Cursor<DoubleType> curs = newImg.cursor();
		
		int endProgress = finalData.getRowDimension();
		
		int rowIndex=0;
		double[] row = finalData.getRow(0);
		
		while(curs.hasNext()) {
			curs.fwd();
			curs.get().set(row[rowIndex++]);
			IJ.showProgress(rowIndex,endProgress);
		}
		
		IJ.showStatus("Rendering image...");
		
	  return newImg;
   }
	
}