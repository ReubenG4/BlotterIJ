package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.util.ArrayList;

public class BlotterImgFunction extends BlotterFunction{
	protected ArrayList<ImgWrapper> imgData;
	
	/* Image Data */
	public ArrayList<ImgWrapper> getImgData(){
		return imgData;
	}
	
	public void setImgData(ArrayList<ImgWrapper> input) {
	    
	    if(!imgData.isEmpty())
	    	imgData.clear();
	    
	    imgData.addAll(input); 
	}
	
	public void clearImgData() {
		imgData.clear();
	}
	
}