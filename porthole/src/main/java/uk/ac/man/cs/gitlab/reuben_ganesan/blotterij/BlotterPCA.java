package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class BlotterPCA <T extends RealType<T> & NativeType<T>>extends BlotterFunction{
	
	//Declare class variables
	private PxlData pxlData;
	private PcaData pcaData;
	private int width;
	private int height;
	private int depth;
	
	/* Main Function */
	public void run(ArrayList<ImgWrapper<T>> inputData, Rectangle selection) {
		
		//Retrieve dimensions
		width = selection.width;
		height = selection.height;
		depth = inputData.size();
		
		//Extract pixel data
		pxlData = new PxlData(inputData, selection);
		
		//Calculate eigenvectors and eigenvalues
		pcaData = new PcaData(pxlData);
	
	}
	
	
	public PxlData getPxlData() {
		return pxlData;
	}
	
	public PcaData getPcaData() {
		return pcaData;
	}
	
}