package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import ij.IJ;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/*
 * Retrieves and represents pixel data of region selected  
 */
public class PxlData<T extends RealType<T> & NativeType<T>>{
		private int width = 0;
		private int height = 0;
		private int depth = 0;
		private double[][][] data;
		
		public PxlData(ArrayList<ImgWrapper<T>> inputData, Rectangle selection) {
			
			//Retrieve dimensions
			width = selection.width;
			height = selection.height;
			depth = inputData.size();
			
			extract(inputData,selection);
			
		}
	

		public double[][][] extract(ArrayList<ImgWrapper<T>> inputData, Rectangle selection) {
	
		//Initialise Interval 
		FinalInterval region = FinalInterval.createMinSize(selection.x,selection.y,
														   selection.width,selection.height);
				
		//Initialise Iterator
		Iterator<ImgWrapper<T>> itr = inputData.iterator();


		//Initialise target array to hold pixel data
		data = new double[depth][height][width];
		
		//Iterate through inputData
		//Initialise initial values for cursor
		int xIndex = 0;
		int yIndex = 0;
		int zIndex = 0;
		
		while(itr.hasNext()) {
			//Show progress bar
			IJ.showStatus("Extracting pixel data...");
			IJ.showProgress(zIndex,depth);

			//Retrieve image
			ImgWrapper<T> curr = itr.next();

			//Initialise cursor for image, cursor iterates within specified region/interval
			RandomAccess<T> cursor = curr.getImg().randomAccess(region);
			//Declare pointer to hold return object from cursor
			T value;

			//Iterate through y-axis of image
			while(yIndex < height) {
				
				//set y-position of cursor
				cursor.setPosition(yIndex,1);
				
				//Iterate through x-axis of image
				while(xIndex < width) {
					//Set x-position of cursor
					cursor.setPosition(xIndex,0);
		
					//Get value of pixel and store it in pxlData
					value = cursor.get();
					data[zIndex][yIndex][xIndex] = value.getRealDouble();
					
					//Increment x-position
					xIndex++;
				}//while x-axis end

				//set x-position to zero
				xIndex = 0;
	
				//Increment y-position
				yIndex++;
			}//while y-axis end
			
			IJ.showProgress(zIndex,depth);
			//Update zIndex to point to next z of pxlData
			zIndex++;
			//Null currently accessed image pointer to allow for garbage collection
			curr.nullImg();
		}
		
		IJ.showProgress(depth,depth);
		return data;	
	}
		
	public double[][][] getData() {
		return data;
	}

	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public int getDepth(){
		return depth;
	}

}