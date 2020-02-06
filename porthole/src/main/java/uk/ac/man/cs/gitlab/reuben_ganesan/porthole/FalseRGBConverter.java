package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.util.ArrayList;
import java.util.Iterator;

import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.RGBStackMerge;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;


public class FalseRGBConverter extends PortholeFunction {
	
	public < T extends RealType<T> & NativeType<T> >ImagePlus convert(ArrayList<ImgWrapper> input) {
		
		/* Declare and initalise class variables */
		imgData = new ArrayList<ImgWrapper>();	
		ArrayList<ImgWrapper> rImgData = new ArrayList<ImgWrapper>();
		ArrayList<ImgWrapper> gImgData = new ArrayList<ImgWrapper>();
		ArrayList<ImgWrapper> bImgData = new ArrayList<ImgWrapper>();
		
		//Chosen RGB data
		ImgWrapper rImgDataChosen;
		ImgWrapper gImgDataChosen;
		ImgWrapper bImgDataChosen;
		
		//Difference between chosen RGB data wavelength and waveband median
		int rWaveDiff;
		int gWaveDiff;
		int bWaveDiff;
		
		//RGB waveband median
		int rWaveMedian = 740 - ((740 - 625) / 2);
		int gWaveMedian = 565 - ((565 - 500) / 2);
		int bWaveMedian = 485 - ((485 - 450) / 2);
		
		/* Initialise imgData and iterator */
		imgData.addAll(input);
		Iterator<ImgWrapper> imgItr = imgData.iterator();
		
		/* Check input size, return if not enough */
		if(imgData == null)
			return null;
		
		if(imgData.size() < 3) 
			return null;
		
		
		/* 
		 * Iterate through imgData
		 * Sort them by RGB wavebands 
		 */
		while(imgItr.hasNext()){
			
			ImgWrapper imgCurr = imgItr.next();
			int wavelength = imgCurr.getWavelength();
			
			if( wavelength >= 625 && wavelength <= 740) {
				rImgData.add(imgCurr);
			} else if (wavelength >= 500 && wavelength <= 565) {
				gImgData.add(imgCurr);
			} else if(wavelength >= 450 && wavelength <= 485) {
				bImgData.add(imgCurr);
			}	
		}
		
		/* If not enough r,g or b data present, show first loaded image */
		if(rImgData.size() == 0 || gImgData.size() == 0 || bImgData.size() == 0) {
			getUIService().showDialog("Not enough RGB data present, showing first loaded image");
			return null;
		}
		
		/*
		 * Iterate through sorted RGBdata
		 * Choose an Img closest to the median of its respective waveband
		 */
		Iterator<ImgWrapper> rItr = rImgData.iterator(); 
		Iterator<ImgWrapper> gItr = gImgData.iterator(); 
		Iterator<ImgWrapper> bItr = bImgData.iterator(); 
		
		rImgDataChosen = rItr.next();
		rWaveDiff = Math.abs(rImgDataChosen.getWavelength() - rWaveMedian);
		
		gImgDataChosen = gItr.next();
		gWaveDiff = Math.abs(gImgDataChosen.getWavelength() - gWaveMedian);
		
		bImgDataChosen = bItr.next();
		bWaveDiff = Math.abs(bImgDataChosen.getWavelength() - bWaveMedian);
		
		while(rItr.hasNext()) {
			ImgWrapper imgCurr = rItr.next();
			int newWaveDiff = Math.abs(imgCurr.getWavelength() - rWaveMedian);
			
			if(newWaveDiff < rWaveDiff) {
				rWaveDiff = newWaveDiff;
				rImgDataChosen = imgCurr;
			}		
		}
		
		while(gItr.hasNext()) {
			ImgWrapper imgCurr = gItr.next();
			int newWaveDiff = Math.abs(imgCurr.getWavelength() - gWaveMedian);
			
			if(newWaveDiff < gWaveDiff) {
				gWaveDiff = newWaveDiff;
				gImgDataChosen = imgCurr;
			}
		}

		while(bItr.hasNext()) {
			ImgWrapper imgCurr = bItr.next();
			int newWaveDiff = Math.abs(imgCurr.getWavelength() - bWaveMedian);
			
			if(newWaveDiff < bWaveDiff) {
				bWaveDiff = newWaveDiff;
				bImgDataChosen = imgCurr;
			}	
		}
	
		//RGBStackMergeExt uses legacy ImagePlus, conversion needed
		ImagePlus rImgChosen = ImageJFunctions.wrap(rImgDataChosen.getImg(),"red");
		ImagePlus gImgChosen = ImageJFunctions.wrap(gImgDataChosen.getImg(), "green");
		ImagePlus bImgChosen = ImageJFunctions.wrap(bImgDataChosen.getImg(),"blue");
		
		RGBStackMergeExt rgbsm = new RGBStackMergeExt();
		ImagePlus rgb = rgbsm.mergeStacks(rImgChosen, gImgChosen, bImgChosen);
		
		return rgb;
		
	}

}
