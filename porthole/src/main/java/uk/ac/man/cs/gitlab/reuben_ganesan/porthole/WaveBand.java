package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.io.File;

public class WaveBand{
	int wavelength;
	char type;
	
	public WaveBand(int wavelength, char type) {
		this.wavelength = wavelength;
		this.type = type;
	}
	
	public WaveBand (File input) {
		this.wavelength= 100;
		this.type = 'N';
	}
}