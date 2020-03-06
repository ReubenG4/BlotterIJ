package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/* Assembles pxlData and pcaData */
public class BlotterPcaMain <T extends RealType<T> & NativeType<T>>extends BlotterFunction{
	
	//Declare class variables
	private PcaData pcaData;
	private PcaRender pcaRender;
	private int width;
	private int height;
	private int depth;
	Rectangle roi;
	
	/* Main Function */
	public void run(ArrayList<ImgWrapper<T>> inputData, Rectangle roi) {
		
		//Retrieve dimensions
		setWidth(roi.width);
		setHeight(roi.height);
		setDepth(inputData.size());
		
		//Extract pixel data
		PxlData pxlData = new PxlData(inputData, roi);
		
		//Calculate eigenvectors and eigenvalues
		pcaData = new PcaData(pxlData);
		
		//Null pixel data for garbage collection
		pxlData = null;
		
		this.roi = roi;
		
		pcaRender = new PcaRender(pcaData, roi);
	
	}
	
	public ArrayImg renderImg(PcaFeature selectedFeature) {
		
		return pcaRender.renderImg(selectedFeature);
		
	}
	
	
	public PcaData getPcaData() {
		return pcaData;
	}


	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	public int getDepth() {
		return depth;
	}


	public void setDepth(int depth) {
		this.depth = depth;
	}
	
}