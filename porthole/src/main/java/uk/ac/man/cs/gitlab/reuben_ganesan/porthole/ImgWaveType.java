package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.io.File;
import net.imglib2.img.Img;

/*
 * Object acting as a wrapper for Img
 *  Includes reference to file holding IMG and metadata specific to plugin
 */
class ImgWaveType{
	private File file;
	private Img<?> img;
	private int wavelength;
	private char type;
	
	public ImgWaveType(File file, int wavelength, char type) {
		this.file = file;
		this.wavelength = wavelength;
		this.type = type;
	}
	
	public String getFilePath() {
		return file.getPath();
	}
	
	public void setImg(Img<?> img) {
		this.img = img;
	}
	
	public Img<?> getImg() {
		return img;
	}
	
	public void setWavelength(int wavelength) {
		this.wavelength = wavelength;
	}
	
	public int getWavelength() {
		return wavelength;
	}
	
	public void setType(char type) {
		this.type = type;
	}
	
	public char getType() {
		return type;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
}