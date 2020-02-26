package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;

public class BlotterFeatureDialog extends BlotterDialog{
	protected ArrayList<PcaFeature> featureData;
	
	/* PcaFeature Data */
	public ArrayList<PcaFeature> getFeatureData(){
		return featureData;
	}
	
	public void setFeatureData(ArrayList<PcaFeature> input) {
	    
	    if(!featureData.isEmpty())
	    	featureData.clear();
	    
	    featureData.addAll(input); 
	}
	
	public void clearFeatureData() {
		featureData.clear();
	}
	
}