package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.SeriesMarkers;

import ij.IJ;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/* Assembles SpectraData from PxlData and plot SpectraData */
public class BlotterSpectraMain <T extends RealType<T> & NativeType<T>>extends BlotterFunction{
	
	//Declare class variables
	private ArrayList<SpectraData> spectraList;
	
	/* Constructor */
	public BlotterSpectraMain() {
	
	}
	
	//Assembles SpectraData from PxlData
	public SpectraData calcSpectra(ArrayList<ImgWrapper> imgData, Rectangle selection) {
		return new SpectraData(imgData,selection);
	}
	
	
	public XYChart plotSpectra(ArrayList<SpectraData> input) {
		spectraList = input;
		
		Iterator<SpectraData> itr = input.iterator();
		
		//Prepare chart
		XYChart chart = new XYChartBuilder().width(800).height(600).theme(ChartTheme.Matlab).build();
		chart.setTitle("");
		chart.setXAxisTitle("Wavelength");
		chart.setYAxisTitle("Mean Pixel Value");
		
		//Iterate through ArrayList input
		while(itr.hasNext()) {
			
			//Retrieve data and place it in arrays
			SpectraData curr = itr.next();
			
			Array2DRowRealMatrix currData = curr.getData();
			
			int rowIndex = currData.getRowDimension();
			
			//Prepare series for plotting
			double[] xData = new double[rowIndex];
			double[] yData = new double[rowIndex];
			
			for(int index=0; index < rowIndex; index++) {
				xData[index] = currData.getRow(index)[0];
				yData[index] = currData.getRow(index)[1];
			}
				
			//Add series to chart
			XYSeries series = chart.addSeries(curr.getName(), xData, yData);
		    series.setMarker(SeriesMarkers.NONE);
		}
		
		return chart;
	}
	
	public double euclideanDistance(SpectraData spectra1, SpectraData spectra2) {
		
		
		return 0;
		
	}
	
	public void setSpectraList(ArrayList<SpectraData> input) {
		spectraList = input;
	}
	
	public ArrayList<SpectraData> getSpectraList() {
		return spectraList;
	}
	
	public int getNumberOfSpectra() {
		return spectraList.size();
	}

	
}