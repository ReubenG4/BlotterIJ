package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class BlotterPCA <T extends RealType<T> & NativeType<T>>extends BlotterFunction{
	
	
	/* Main Function */
	public Img<T> run(ArrayList<ImgWrapper<T>> inputData, Rectangle selection) {
		
		double[][][] pxlData = extractPixelData(inputData,selection);
		
		
		
		return null;
		
	}
	
	/* Extracts Pixel Data from selected region of input data */	
	public double[][][] extractPixelData(ArrayList<ImgWrapper<T>> inputData, Rectangle selection) {
		
		//Initialise Interval 
		FinalInterval region = FinalInterval.createMinSize(selection.x,selection.y,
														   selection.width,selection.height);
				
		//Initialise Iterator
		Iterator<ImgWrapper<T>> itr = inputData.iterator();

		//Initialise bounds for target array to hold pixel data
		int width = selection.width;
		int height = selection.height;

		//Initialise target array to hold pixel data
		double[][][] pxlData = new double[width][height][inputData.size()];

		//Iterate through inputData
		int zIndex = 0;
		while(itr.hasNext()) {

			//Retrieve image
			ImgWrapper<T> curr = itr.next();

			//Initialise cursor for image, cursor iterates within specified region/interval
			RandomAccess<T> cursor = curr.getImg().randomAccess(region);

			//Declare pointer to hold return object from cursor
			T value;

			//Initialise initial values for cursor
			int xIndex = 0;
			int yIndex = 0;
			
			//Iterate through y-axis of image
			while(yIndex < height) {		
				xIndex = 0;

				//Iterate through x-axis of image
				while(xIndex < width) {
					//Set position of cursor
					cursor.setPosition(xIndex,0);
					cursor.setPosition(yIndex,1);
		
					//Get value of pixel and store it in pxlData
					value = cursor.get();
					pxlData[xIndex][yIndex][zIndex] = value.getRealDouble();
					xIndex++;
				}//while x-axis end

				//move cursor 1 pixel on y-axis
				yIndex++;
			}//while y-axis end

			//Update zIndex to point to next z of pxlData
			zIndex++;
			//Null currently accessed image pointer to allow for garbage collection
			curr.nullImg();
		}
		
		return pxlData;	
	}
	
	
	public class CovarianceData{
		
	}
}