package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.io.File;

import io.scif.config.SCIFIOConfig;
import io.scif.config.SCIFIOConfig.ImgMode;
import io.scif.img.ImgOpener;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/*
 * Object acting as a wrapper for Img
 *  Includes reference to file and metadata specific to plugin
 */
class ImgWrapper < T extends RealType< T > & NativeType< T > >{
	private File file = null;
	private Img<T> img = null;
	private int wavelength;
	private char type;
	
	public ImgWrapper(File file, int wavelength, char type) {
		this.file = file;
		this.wavelength = wavelength;
		this.type = type;
	}	
	
	public ImgWrapper(Img<T> img, int wavelength, char type) {
		this.img = img;
		this.wavelength = wavelength;
		this.type = type;
	}	
	
	/*
	 * Retrieves Img from file. 
	 * Plugin may not neccessarily want to load large files into memory at initialisation of object
	 */ 
	public  void initImg() {
		ImgOpener imgOpener = new ImgOpener();
		SCIFIOConfig config = new SCIFIOConfig();
		config.imgOpenerSetImgModes( ImgMode.AUTO );
		img =  (Img<T>) imgOpener.openImgs(file.getAbsolutePath(),config).get(0);		
	}
	
	/*
	 * Sets img pointer to null.
	 * Plugin may want to not hold references to large files.
	 */
	public void  nullImg() {
		img = null;
	}
	
	public String getFilePath() {
		return file.getPath();
	}
	
	public Img<T> getImg() {
		if(img == null && file != null )
			initImg();
		
		return img;
	}
	
	public void setImg(Img<T> img) {
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