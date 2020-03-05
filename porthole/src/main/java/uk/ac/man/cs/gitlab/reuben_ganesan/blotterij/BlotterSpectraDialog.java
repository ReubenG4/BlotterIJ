package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;

public class BlotterSpectraDialog extends BlotterDialog{

	protected ArrayList<SpectraData> spectraData;
	
	/* PcaFeature Data */
	public ArrayList<SpectraData> getSpectraData(){
		return spectraData;
	}
	
	public void addSpectraData(ArrayList<SpectraData> input) {
	    
	    if(!spectraData.isEmpty())
	    	spectraData.clear();
	    
	    spectraData.addAll(input); 
	}
	
	public void clearSpectraData() {
		spectraData.clear();
	}


}