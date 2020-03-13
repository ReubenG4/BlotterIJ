package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import ij.IJ;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;


/* Implementation of PxlData that does not null accessed Img pointer
 * Used when ImgWrapper should retain Img for quicker sequential access
 */
public class PxlData2<T extends RealType<T> & NativeType<T>> extends PxlData<T>{

	public PxlData2(ArrayList<ImgWrapper<T>> inputData, Rectangle selection) {
		super(inputData, selection);
	}
	
	/* Extract data from selection */
	@Override
	protected double[][][] extract(ArrayList<ImgWrapper<T>> inputData) {

		//Initialise Interval 
		FinalInterval region = FinalInterval.createMinSize(roi.x,roi.y,
				roi.width,roi.height);

		//Initialise Iterator
		Iterator<ImgWrapper<T>> itr = inputData.iterator();

		//Initialise target array to hold pixel data
		data = new double[depth][height][width];

		//Iterate through inputData
		//Initialise initial values for cursor and array indexer
		int xIndex = 0;
		int yIndex = 0;
		int xCursor = roi.x;
		int yCursor = roi.y;

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
			xCursor = roi.x;
			yCursor = roi.y;

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
				xCursor = roi.x;
				xIndex = 0;

				//Increment y-position
				yIndex++;
				yCursor++;
			}

			IJ.showProgress(zIndex,depth);
			//Update zIndex to point to next z of pxlData
			zIndex++;
		}

		IJ.showProgress(depth,depth);
		return data;	
	}
}