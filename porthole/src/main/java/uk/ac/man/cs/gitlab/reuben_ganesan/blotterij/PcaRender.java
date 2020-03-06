package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;
import org.apache.commons.math4.linear.RealMatrix;
import ij.IJ;
import net.imglib2.Cursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;

public class PcaRender extends BlotterFunction{
	
	//Declare class variables
	RealMatrix finalData;
	PcaData pcaData;
	
	int width;
	int height;
	int size;
	
	int noOfWavelengths;
	int noOfFeatures;
	
	double mean[];
	
	public PcaRender(PcaData pcaData,Rectangle selection){
		
		this.pcaData = pcaData;
		width = selection.width;
		height = selection.height;
		size = width * height;
		
	}
	
	public ArrayImg renderImg(ArrayList<PcaFeature> selectedFeatures) {
		
		IJ.showStatus("Calculating final data");
		finalData = pcaData.calcFinalData(selectedFeatures);
		IJ.showStatus("Converting final data to image...");
		
		
		ArrayImg<DoubleType,DoubleArray> newImg = ArrayImgs.doubles(width,height); 
		Cursor<DoubleType> curs = newImg.cursor();
		
		int endProgress = finalData.getRowDimension();
		
		int rowIndex=0;
		double[] row = finalData.getRow(0);
		
		while(curs.hasNext()) {
			curs.fwd();
			curs.get().set(row[rowIndex++]);
			IJ.showProgress(rowIndex,endProgress);
		}
		
		IJ.showStatus("Rendering image...");
		
	  return newImg;
   }
	
}