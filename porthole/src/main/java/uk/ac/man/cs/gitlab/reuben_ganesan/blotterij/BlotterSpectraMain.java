package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/* Assembles SpectraData from PxlData and plot SpectraData */
public class BlotterSpectraMain <T extends RealType<T> & NativeType<T>>extends BlotterFunction{
	
	//Declare class variables
	private ArrayList<SpectraData> spectraList;
	
	/* Constructor */
	public BlotterSpectraMain() {
		
		//Initialise ArrayLists for holding spectra and wavelengths
		spectraList = new ArrayList<SpectraData>();
		
	}
	
	//Assembles SpectraData from PxlData
	public SpectraData calcSpectra(ArrayList<ImgWrapper> imgData, Rectangle selection) {
		return new SpectraData(imgData,selection);
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