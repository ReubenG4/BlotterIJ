package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.io.File;

import net.imglib2.img.Img;

//Object holding data for each row
class ImgWaveType{
	File file;
	Img img;
	int wavelength;
	char type;
	
	public ImgWaveType(File file, int wavelength, char type) {
		this.file = file;
		this.wavelength = wavelength;
		this.type = type;
	}
}