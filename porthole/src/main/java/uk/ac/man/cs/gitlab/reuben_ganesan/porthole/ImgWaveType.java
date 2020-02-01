package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.io.File;

import io.scif.config.SCIFIOConfig;
import io.scif.config.SCIFIOConfig.ImgMode;
import io.scif.img.ImgOpener;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/*
 * Object acting as a wrapper for Img
 *  Includes reference to file holding IMG and metadata specific to plugin
 */
class ImgWaveType < T extends RealType< T > & NativeType< T > >{
	private File file;
	private Img<T> img;
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
	
	public void setImg(Img<T> img) {
		this.img = img;
	}
	
	public  void initImg() {
		ImgOpener imgOpener = new ImgOpener();
		SCIFIOConfig config = new SCIFIOConfig();
		config.imgOpenerSetImgModes( ImgMode.CELL );
		img = (Img<T>) imgOpener.openImgs(file.getAbsolutePath(),config).get(0);	
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