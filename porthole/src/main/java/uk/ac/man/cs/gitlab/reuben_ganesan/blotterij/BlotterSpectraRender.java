package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;
import java.util.Iterator;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.LegendPosition;

import ij.IJ;

public class BlotterSpectraRender extends BlotterFunction{
	
	//Declare class variables	
	int noOfWavelengths;
	ArrayList<SpectraData> spectra;
	ArrayList<Integer> wavelengths;
	
	public BlotterSpectraRender(SpectraData spectra, ArrayList<Integer> wavelengths){
		
		//Initialise spectra 
		this.spectra = new ArrayList<SpectraData>();
		this.spectra.add(spectra);
		
		//Initialise wavelengths
		this.wavelengths = wavelengths;
		
	}

	public void addSpectra(SpectraData spectra) {
		this.spectra.add(spectra);
	}
	
	public XYChart plot() {
		
		IJ.showStatus("Converting spectra data to plot...");
			
		 // Create Chart
		XYChart chart = new XYChartBuilder().width(800).height(600).build();
		
		// Customize Chart
		chart.getStyler().setChartTitleVisible(true);
		chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
		chart.getStyler().setXAxisLabelRotation(45);
	    chart.getStyler().setMarkerSize(1);
		 
		 
	    //Add series to chart
	    Iterator<SpectraData> itr = spectra.iterator();
	    
	    while(itr.hasNext()) {
	    	
	    	SpectraData thisSpectra = itr.next();
	    	chart.addSeries(thisSpectra.getName(), wavelengths, thisSpectra.getVector());
	    }
	    
	    chart.setYAxisTitle("Pixel Value");
	    chart.setXAxisTitle("Wavelength");
		
		return chart;
	}
	
}