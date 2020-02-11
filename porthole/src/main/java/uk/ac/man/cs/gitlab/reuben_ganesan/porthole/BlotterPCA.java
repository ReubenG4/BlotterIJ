package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import net.imagej.ImgPlus;
import net.imglib2.FinalInterval;
import net.imglib2.img.Img;

public class BlotterPCA extends BlotterFunction{
	
	
	public Img run(ArrayList<ImgWrapper> inputData, Rectangle selection) {
		
		//Initialise imgData
		imgData = new ArrayList<ImgWrapper>();
		
		//Initialise Interval 
		FinalInterval region = FinalInterval.createMinSize(selection.x,selection.y,
														   selection.width,selection.height);
		
		//Initialise Iterator
		Iterator<ImgWrapper> itr = inputData.iterator();
		
		
		//Iterate through inputData
		while(itr.hasNext()) {
			ImgWrapper curr = itr.next();
			//Crop the image
			ImgPlus croppedImg = (ImgPlus)getOpsService().run("crop", new ImgPlus(curr.getImg()), region);
			//Add image and its corresponding metadata to imgData
			imgData.add(new ImgWrapper(croppedImg.getImg(), curr.getWavelength(), curr.getType()));
			//Null Img pointer in inputData
			curr.nullImg();
		}
				
		getUIService().show(imgData.get(0).getImg());
					
		return null;
		
	}
}