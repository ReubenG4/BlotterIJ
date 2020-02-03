package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

public class FalseRGBConverter extends PortholeFunction {
	
	public ImgPlusMeta convert() {
		
		if(imgData == null || imgData.size() < 3 )
			return null;
		else
			return imgData.get(0);
		
	}

}
