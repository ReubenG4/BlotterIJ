package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.io.File;

import io.scif.config.SCIFIOConfig;
import io.scif.config.SCIFIOConfig.ImgMode;
import io.scif.img.ImgOpener;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/*
 * Object acting as a wrapper for ImgPlus
 *  Includes reference to file and metadata specific to plugin
 */
class ImgPlusMeta < T extends RealType< T > & NativeType< T > >{
	private File file;
	private ImgPlus<T> img;
	private int wavelength;
	private char type;
	
	public ImgPlusMeta(File file, int wavelength, char type) {
		this.file = file;
		this.wavelength = wavelength;
		this.type = type;
	}	
	
	/*
	 * Retrieves ImgPlus from file. 
	 * Plugin may not neccessarily want to load large files into memory at initialisation of object
	 */ 
	public  void initImg() {
		ImgOpener imgOpener = new ImgOpener();
		SCIFIOConfig config = new SCIFIOConfig();
		config.imgOpenerSetImgModes( ImgMode.CELL );
		Img<T> preImg =  (Img<T>) imgOpener.openImgs(file.getAbsolutePath(),config).get(0);	
		img = new ImgPlus(preImg, file.getName(), new AxisType[]{Axes.X, Axes.Y, Axes.TIME});
	}
	
	public String getFilePath() {
		return file.getPath();
	}
	
	public ImgPlus<T> getImg() {
		return img;
	}
	
	public void setImg(ImgPlus<T> img) {
		this.img = img;
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