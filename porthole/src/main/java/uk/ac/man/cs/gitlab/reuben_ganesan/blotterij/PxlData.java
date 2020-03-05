package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.apache.commons.math4.linear.BlockRealMatrix;
import org.apache.commons.math4.linear.RealMatrix;

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
		private ArrayList<Integer> wavelengths;
		
		public PxlData(ArrayList<ImgWrapper<T>> inputData, Rectangle selection) {
			
			//Retrieve dimensions
			width = selection.width;
			height = selection.height;
			depth = inputData.size();
			
			//Retrive wavelengths associated with inputData
			wavelengths = new ArrayList<Integer>();
			
			Iterator<ImgWrapper<T>> itr  = inputData.iterator();
			
			while(itr.hasNext())
				wavelengths.add(itr.next().getWavelength());
			
			//Extract pixel data from the selected area
			extract(inputData,selection);
			
		}
		
		/* flatten data from 2D to 1D */
		public RealMatrix flatten() {
			
			int noOfPixels = width*height;
			int noOfWavelengths = depth;
			
			//Initialise Matrix to hold flattened pxlData
			RealMatrix flattenedData = new BlockRealMatrix(noOfPixels, noOfWavelengths);
			
			/*
			 * Flatten pxlData[z][y][x] to produce flattenedData[z][p] 
			 */
			for (int index=0; index < noOfWavelengths; index++) {
					flattenedData.setColumn(index, Stream.of(data[index]).flatMapToDouble(DoubleStream::of).toArray());		
			}
						
			return flattenedData;
			
		}
	
		
		/* Extract data from selection */
		public double[][][] extract(ArrayList<ImgWrapper<T>> inputData, Rectangle selection) {
	
		//Initialise Interval 
		FinalInterval region = FinalInterval.createMinSize(selection.x,selection.y,
														   selection.width,selection.height);
				
		//Initialise Iterator
		Iterator<ImgWrapper<T>> itr = inputData.iterator();


		//Initialise target array to hold pixel data
		data = new double[depth][height][width];
		
		//Iterate through inputData
		//Initialise initial values for cursor and array indexer
		int xIndex = 0;
		int yIndex = 0;
		int xCursor = selection.x;
		int yCursor = selection.y;
		
		int zIndex = 0;
		
		ImgWrapper<T> curr;
		RandomAccess<T> cursor;
		
		while(itr.hasNext()) {
			//Show progress bar
			IJ.showStatus("Extracting pixel data...");
			IJ.showProgress(zIndex,depth);

			//Retrieve image
			curr = itr.next();

			//Initialise cursor for image, cursor iterates within specified region/interval
			cursor = curr.getImg().randomAccess(region);
			
			//Declare pointer to hold return object from cursor
			T value;
			
			//Reset cursor and array indexer values
			xIndex = 0;
			yIndex = 0;
			xCursor = selection.x;
			yCursor = selection.y;
			
			//Iterate through y-axis of image
			while(yIndex < height) {
				
				//set y-position of cursor
				cursor.setPosition(yCursor,1);
				
				//Iterate through x-axis of image
				while(xIndex < width) {
					//Set x-position of cursor
					cursor.setPosition(xCursor,0);
		
					//Get value of pixel and store it in pxlData
					value = cursor.get();
					data[zIndex][yIndex][xIndex] = value.getRealDouble();
					
					//Increment x-position
					xIndex++;
					xCursor++;
				}

				//Reset x position
				xCursor = selection.x;
				xIndex = 0;
				
				//Increment y-position
				yIndex++;
				yCursor++;
			}
			
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

	public ArrayList<Integer> getWavelengths(){
		return wavelengths;
	}
}