package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;

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
	
	
}