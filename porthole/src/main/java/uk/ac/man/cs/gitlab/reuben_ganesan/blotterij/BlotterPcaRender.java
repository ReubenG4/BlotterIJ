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
	RealMatrix finalData;
	PcaData pcaData;
	
	int width;
	int height;
	
	int noOfWavelengths;
	int noOfFeatures;
	
	double mean[];
	
	public BlotterPcaRender(PcaData pcaData,Rectangle selection){
		
		this.pcaData = pcaData;
		width = selection.width;
		height = selection.height;
		
	}
	
		
	
	public void scatterPlot() {
		
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
	
	public Img render(ArrayList<PcaFeature> selectedFeatures) {
		
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