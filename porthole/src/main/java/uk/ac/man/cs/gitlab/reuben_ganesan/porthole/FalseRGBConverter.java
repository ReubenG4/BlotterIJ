package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.util.ArrayList;
import java.util.Iterator;

public class FalseRGBConverter extends PortholeFunction {
	
	public ImgPlusMeta convert(ArrayList<ImgPlusMeta> input) {
		
		/* Declare and initalise class variables */
		imgData = new ArrayList<ImgPlusMeta>();	
		ArrayList<ImgPlusMeta> rImgData = new ArrayList<ImgPlusMeta>();
		ArrayList<ImgPlusMeta> gImgData = new ArrayList<ImgPlusMeta>();
		ArrayList<ImgPlusMeta> bImgData = new ArrayList<ImgPlusMeta>();
		
		//Chosen RGB data
		ImgPlusMeta rImgDataChosen;
		ImgPlusMeta gImgDataChosen;
		ImgPlusMeta bImgDataChosen;
		
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
		Iterator<ImgPlusMeta> imgItr = imgData.iterator();
		
		/* Check input size, return if not enough */
		if(imgData == null)
			return null;
		
		if(imgData.size() < 3) {
			return imgData.get(0);
		}
		
		/* 
		 * Iterate through imgData
		 * Sort them by RGB wavebands 
		 */
		while(imgItr.hasNext()){
			
			ImgPlusMeta imgCurr = imgItr.next();
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
			return imgData.get(0);
		}
		
		/*
		 * Iterate through sorted RGBdata
		 * Choose an ImgPlusMeta closest to the median of its respective waveband
		 */
		Iterator<ImgPlusMeta> rItr = rImgData.iterator(); 
		Iterator<ImgPlusMeta> gItr = gImgData.iterator(); 
		Iterator<ImgPlusMeta> bItr = bImgData.iterator(); 
		
		rImgDataChosen = rItr.next();
		rWaveDiff = Math.abs(rImgDataChosen.getWavelength() - rWaveMedian);
		
		gImgDataChosen = gItr.next();
		gWaveDiff = Math.abs(gImgDataChosen.getWavelength() - gWaveMedian);
		
		bImgDataChosen = bItr.next();
		bWaveDiff = Math.abs(bImgDataChosen.getWavelength() - bWaveMedian);
		
		while(rItr.hasNext()) {
			
			ImgPlusMeta imgCurr = rItr.next();
			int newWaveDiff = Math.abs(imgCurr.getWavelength() - rWaveMedian);
			
			if(newWaveDiff < rWaveDiff) {
				rWaveDiff = newWaveDiff;
				rImgDataChosen = imgCurr;
			}
			
		}
		
		while(gItr.hasNext()) {
			
			ImgPlusMeta imgCurr = gItr.next();
			int newWaveDiff = Math.abs(imgCurr.getWavelength() - gWaveMedian);
			
			if(newWaveDiff < gWaveDiff) {
				gWaveDiff = newWaveDiff;
				gImgDataChosen = imgCurr;
			}
			
		}

	
		while(bItr.hasNext()) {
			
			ImgPlusMeta imgCurr = bItr.next();
			int newWaveDiff = Math.abs(imgCurr.getWavelength() - bWaveMedian);
			
			if(newWaveDiff < bWaveDiff) {
				bWaveDiff = newWaveDiff;
				bImgDataChosen = imgCurr;
			}
			
		}
		
		
		
		
		return imgData.get(0);
		
	}

}
