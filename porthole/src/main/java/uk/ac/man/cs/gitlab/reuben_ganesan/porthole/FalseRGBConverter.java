package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.util.ArrayList;

public class FalseRGBConverter extends PortholeFunction {
	
	public ImgPlusMeta convert(ArrayList<ImgPlusMeta> input) {
		
		imgData = input;
		
		if(imgData == null || imgData.size() < 3 )
			return null;
		else
			return imgData.get(0);
		
	}

}
