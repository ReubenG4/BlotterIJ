package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;

import org.knowm.xchart.XYChart;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/* Assemble SpectraData from PxlData and collate it for plotting */
public class BlotterSpectraMain <T extends RealType<T> & NativeType<T>>extends BlotterFunction{
	
	//Declare class variables
	private ArrayList<SpectraData> spectraList;
	private ArrayList<Integer> wavelengths;
	
	/* Constructor */
	public BlotterSpectraMain() {
		
		//Initialise ArrayLists for holding spectra and wavelengths
		spectraList = new ArrayList<SpectraData>();
		wavelengths = new ArrayList<Integer>();
		
	}
	
	//Retrieves SpectraData from PxlData and add it to spectraList
	public void addSpectra(PxlData inputData) {
		spectraList.add(new SpectraData(inputData));
	}
	
	public XYChart plot() {
		
		return null;
		
	}
	
	public int getNumberOfSpectra() {
		return spectraList.size();
	}

	public ArrayList<Integer> getWavelengths() {
		return wavelengths;
	}

	public void setWavelengths(ArrayList<Integer> wavelengths) {
		this.wavelengths = wavelengths;
	}
	
}