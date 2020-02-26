package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.apache.commons.math4.linear.RealMatrix;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class BlotterPcaCalc <T extends RealType<T> & NativeType<T>>extends BlotterFunction{
	
	//Declare class variables
	private PxlData<T> pxlData;
	private PcaData pcaData;
	private int width;
	private int height;
	private int depth;
	
	/* Main Function */
	public void run(ArrayList<ImgWrapper<T>> inputData, Rectangle selection) {
		
		//Retrieve dimensions
		setWidth(selection.width);
		setHeight(selection.height);
		setDepth(inputData.size());
		
		//Extract pixel data
		pxlData = new PxlData<T>(inputData, selection);
		
		//Calculate eigenvectors and eigenvalues
		pcaData = new PcaData(pxlData);
	
	}
	
	public RealMatrix getFlattenedPxlData() {
		return getPcaData().getFlattenedData();
	}
	
	public PxlData<T> getPxlData() {
		return pxlData;
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