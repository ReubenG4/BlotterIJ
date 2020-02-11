package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.Rectangle;
import java.util.ArrayList;

import net.imagej.ImgPlus;
import net.imglib2.FinalInterval;
import net.imglib2.img.Img;

public class BlotterPCA extends BlotterFunction{
	
	
	public Img run(ArrayList<ImgWrapper> inputData, Rectangle selection) {
		
		
		//Initialise interval 
		FinalInterval region = FinalInterval.createMinSize(selection.x,selection.y,
														   selection.width,selection.height);
		
		
		
		//Call Ops service to crop image along region specified, doesn't work
		//ImgPlus result = (ImgPlus)getOpsService().run("crop",new ImgPlus(input),region);
		
		
		
		
		return null;
		
	}
}