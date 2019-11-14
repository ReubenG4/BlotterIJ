package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.io.File;

//Object holding data for each row
class FileWaveType{
	File file;
	int wavelength;
	char type;
	
	public FileWaveType(File file, int wavelength, char type) {
		this.file = file;
		this.wavelength = wavelength;
		this.type = type;
	}
}