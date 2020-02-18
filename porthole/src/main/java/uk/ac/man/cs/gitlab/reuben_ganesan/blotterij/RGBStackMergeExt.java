package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Color;
import java.awt.image.IndexColorModel;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.CompositeConverter;
import ij.plugin.ZProjector;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.LUT;

/** Adapted legacy code from ij.plugin.RGBStackMerge  
 *  Reproduced mostly wholesale due to time constraints
 *  Used to produce FalseRGB image from dataset
 *  Overloading of mergeStacks to allow code to be used headlessly
 * */
public class RGBStackMergeExt {
	private static String none = "*None*";
	private static int maxChannels = 7;
	private static String[] colors = {"red", "green", "blue", "gray", "cyan", "magenta", "yellow"};
	private static boolean staticCreateComposite = true;
	private static boolean staticKeep;
	private static boolean staticIgnoreLuts;
	private ImagePlus imp;
	private byte[] blank;
	private boolean ignoreLuts;
	private boolean autoFillDisabled;
	private String firstChannelName;
 
	
	public static ImagePlus mergeChannels(ImagePlus[] images, boolean keepSourceImages) {
		RGBStackMergeExt rgbsm = new RGBStackMergeExt();
		return rgbsm.mergeHyperstacks(images, keepSourceImages);
	}

	/** Combines up to seven grayscale stacks into one RGB or composite stack. */
	public ImagePlus mergeStacks(ImagePlus red, ImagePlus green, ImagePlus blue) {
		
		ImagePlus[] input = {red,green,blue};
		
		boolean createComposite = true;
		boolean keep= false;
		ignoreLuts = false;
		
		staticCreateComposite = createComposite;
		staticKeep = keep;
		staticIgnoreLuts = ignoreLuts;
		
		ImagePlus[] images = new ImagePlus[maxChannels];
		int stackSize = 0;
		int width = 0;
		int height = 0;
		int bitDepth = 0;
		int slices = 0;
		int frames = 0;
		for (int i=0; i<maxChannels; i++) {
			//IJ.log(i+"  "+index[i]+"	"+titles[index[i]]+"  "+wList.length);
			if (i<input.length) {
				images[i] = input[i];
				if (width==0) {
					width = images[i].getWidth();
					height = images[i].getHeight();
					stackSize = images[i].getStackSize();
					bitDepth = images[i].getBitDepth();
					slices = images[i].getNSlices();
					frames = images[i].getNFrames();
				}
			}
		}
		if (width==0) {
			error("There must be at least one source image or stack.");
			return null;
		}
		
		boolean mergeHyperstacks = false;
		for (int i=0; i<maxChannels; i++) {
			ImagePlus img = images[i];
			if (img==null) continue;
			if (img.getStackSize()!=stackSize) {
				error("The source stacks must have the same number of images.");
				return null;
			}
			if (img.isHyperStack()) {
				if (img.isComposite()) {
					CompositeImage ci = (CompositeImage)img;
					if (ci.getMode()!=IJ.COMPOSITE) {
						ci.setMode(IJ.COMPOSITE);
						img.updateAndDraw();
						if (!IJ.isMacro()) IJ.run("Channels Tool...");
						return null;
					}
				}
				if (bitDepth==24) {
					error("Source hyperstacks cannot be RGB.");
					return null;
				}
				if (img.getNChannels()>1) {
					error("Source hyperstacks cannot have more than 1 channel.");
					return null;
				}
				if (img.getNSlices()!=slices || img.getNFrames()!=frames) {
					error("Source hyperstacks must have the same dimensions.");
					return null;
				}
				mergeHyperstacks = true;
			} // isHyperStack
			if (img.getWidth()!=width || images[i].getHeight()!=height) {
				error("The source images or stacks must have the same width and height.");
				return null;
			}
			if (createComposite && img.getBitDepth()!=bitDepth) {
				error("The source images must have the same bit depth.");
				return null;
			}
		}

		ImageStack[] stacks = new ImageStack[maxChannels];
		for (int i=0; i<maxChannels; i++)
			stacks[i] = images[i]!=null?images[i].getStack():null;
		ImagePlus imp2;
		boolean fourOrMoreChannelRGB = false;
		for (int i=3; i<maxChannels; i++) {
			if (stacks[i]!=null) {
				if (!createComposite)	
					fourOrMoreChannelRGB=true;
				createComposite = true;
			}
		}
		if (fourOrMoreChannelRGB)
			createComposite = true;
		boolean isRGB = false;
		int extraIChannels = 0;
		for (int i=0; i<maxChannels; i++) {
			if (images[i]!=null) {
				if (i>2)
					extraIChannels++;
				if (images[i].getBitDepth()==24)
					isRGB = true;
			}
		}
		if (isRGB && extraIChannels>0) {
			imp2 = mergeUsingRGBProjection(images, createComposite);
		} else if ((createComposite&&!isRGB) || mergeHyperstacks) {
			imp2 = mergeHyperstacks(images, keep);
			if (imp2==null) return null;
		} else {
			ImageStack rgb = mergeStacks(width, height, stackSize, stacks[0], stacks[1], stacks[2], keep);
			imp2 = new ImagePlus("RGB", rgb);
			if (createComposite) {
				imp2 = CompositeConverter.makeComposite(imp2);
				imp2.setTitle("Composite");
			}
		}
		for (int i=0; i<images.length; i++) {
			if (images[i]!=null) {
				imp2.setCalibration(images[i].getCalibration());
				break;
			}
		}
		if (fourOrMoreChannelRGB) {
			if (imp2.getNSlices()==1&&imp2.getNFrames()==1) {
				imp2 = imp2.flatten();
				imp2.setTitle("RGB");
			}
		}
		//imp2.show();
		if (!keep) {
			for (int i=0; i<maxChannels; i++) {
				if (images[i]!=null) {
					images[i].changes = false;
					images[i].close();
				}
			}			
		}
		
		return imp2;
	 }
	 
	 private String[] getInitialNames(String[] titles) {
	 	String[] names = new String[maxChannels];
	 	for (int i=0; i<maxChannels; i++)
	 		names[i] = getName(i+1, titles);
	 	return names;
	 }
	 
	 private String getName(int channel, String[] titles) {
	 	if (autoFillDisabled)
	 		return none;
	 	String str = "C"+channel;
	 	String name = null;
		for (int i=titles.length-1; i>=0; i--) {
			if (titles!=null && titles[i].startsWith(str) && (firstChannelName==null||titles[i].contains(firstChannelName))) {
				name = titles[i];
	 			if (channel==1) {
	 				if (name==null || name.length()<3)
	 					return none;
	 				firstChannelName = name.substring(3);
	 			}
				break;
			}
		}
	 	if (name==null) {
			for (int i=titles.length-1; i>=0; i--) {
				int index = titles[i].indexOf(colors[channel-1]);
				if (titles!=null && index!=-1 && (firstChannelName==null||titles[i].contains(firstChannelName))) {
					name = titles[i];
	 				if (channel==1 && index>0) 
	 					firstChannelName = name.substring(0, index-1);
					break;
				}
			}
		}
	 	if (channel==1 && name==null)
	 		autoFillDisabled = true;
	 	if (name!=null)
	 		return name;
	 	else
	 		return none;
	 }
	 	
	public ImagePlus mergeHyperstacks(ImagePlus[] images, boolean keep) {
		int n = images.length;
		int channels = 0;
		for (int i=0; i<n; i++) {
			if (images[i]!=null) channels++;
		}
		if (channels<2) return null;
		ImagePlus[] images2 = new ImagePlus[channels];
		Color[] defaultColors = {Color.red,Color.green,Color.blue,Color.white,Color.cyan,Color.magenta,Color.yellow};
		Color[] colors = new Color[channels];
		int j = 0;
		for (int i=0; i<n; i++) {
			if (images[i]!=null) {
				images2[j] = images[i];
				if (i<defaultColors.length)
					colors[j] = defaultColors[i];
				j++;
			}
		}
		images = images2;
		ImageStack[] stacks = new ImageStack[channels];
		for (int i=0; i<channels; i++) {
			ImagePlus imp2 = images[i];
			if (isDuplicate(i,images))
				imp2 = imp2.duplicate();
			stacks[i] = imp2.getStack();
		}
		ImagePlus imp = images[0];
		int w = imp.getWidth();
		int h = imp.getHeight();
		int slices = imp.getNSlices();
		int frames = imp.getNFrames();
		ImageStack stack2 = new ImageStack(w, h);
		//IJ.log("mergeHyperstacks: "+w+" "+h+" "+channels+" "+slices+" "+frames);
		int[] index = new int[channels];
		for (int t=0; t<frames; t++) {
			for (int z=0; z<slices; z++) {
				for (int c=0; c<channels; c++) {
					ImageProcessor ip = stacks[c].getProcessor(index[c]+1);
					if (keep)
						ip = ip.duplicate();
					stack2.addSlice(null, ip);
					if (keep)
						index[c]++;
					else
						stacks[c].deleteSlice(1);
				}
			}
		}
		String title = imp.getTitle();
		if (title.startsWith("C1-"))
			title = title.substring(3);
		else
			title = frames>1?"Merged":"Composite";
		ImagePlus imp2 = new ImagePlus(title, stack2);
		imp2.setDimensions(channels, slices, frames);
		imp2 = new CompositeImage(imp2, IJ.COMPOSITE);
		boolean allGrayLuts = true;
		for (int c=0; c<channels; c++) {
			if (images[c].getProcessor().isColorLut()) {
				allGrayLuts = false;
				break;
			}
		}
		for (int c=0; c<channels; c++) {
			ImageProcessor ip = images[c].getProcessor();
			IndexColorModel cm = (IndexColorModel)ip.getColorModel();
			LUT lut = null;
			if (c<colors.length && colors[c]!=null && (ignoreLuts||allGrayLuts)) {
				lut = LUT.createLutFromColor(colors[c]);
				lut.min = ip.getMin();
				lut.max = ip.getMax();
			} else
				lut =  new LUT(cm, ip.getMin(), ip.getMax());
			((CompositeImage)imp2).setChannelLut(lut, c+1);
		}
		imp2.setOpenAsHyperStack(true);
		return imp2;
	}
	
	private boolean isDuplicate(int index, ImagePlus[] images) {
		int count = 0;
		for (int i=0; i<index; i++) {
			if (images[index]==images[i])
				return true;
		}
		return false;
	}

	/** Deprecated; replaced by mergeChannels(). */
	public ImagePlus createComposite(int w, int h, int d, ImageStack[] stacks, boolean keep) {
		ImagePlus[] images = new ImagePlus[stacks.length];
		for (int i=0; i<stacks.length; i++)
			images[i] = new ImagePlus(""+i, stacks[i]);
		return mergeHyperstacks(images, keep);
	}

	public static ImageStack mergeStacks(ImageStack red, ImageStack green, ImageStack blue, boolean keepSource) {
        RGBStackMergeExt merge = new RGBStackMergeExt();
		return merge.mergeStacks(red.getWidth(), red.getHeight(), red.getSize(), red, green, blue, keepSource);
	}

	public ImageStack mergeStacks(int w, int h, int d, ImageStack red, ImageStack green, ImageStack blue, boolean keep) {
		ImageStack rgb = new ImageStack(w, h);
		int inc = d/10;
		if (inc<1) inc = 1;
		ColorProcessor cp;
		int slice = 1;
		blank = new byte[w*h];
		byte[] redPixels, greenPixels, bluePixels;
		boolean invertedRed = red!=null?red.getProcessor(1).isInvertedLut():false;
		boolean invertedGreen = green!=null?green.getProcessor(1).isInvertedLut():false;
		boolean invertedBlue = blue!=null?blue.getProcessor(1).isInvertedLut():false;
		try {
			for (int i=1; i<=d; i++) {
				cp = new ColorProcessor(w, h);
				redPixels = getPixels(red, slice, 0);
				greenPixels = getPixels(green, slice, 1);
				bluePixels = getPixels(blue, slice, 2);
				if (invertedRed) redPixels = invert(redPixels);
				if (invertedGreen) greenPixels = invert(greenPixels);
				if (invertedBlue) bluePixels = invert(bluePixels);
				cp.setRGB(redPixels, greenPixels, bluePixels);
				if (keep)
					slice++;
				else {
					if (red!=null) red.deleteSlice(1);
					if (green!=null &&green!=red) green.deleteSlice(1);
					if (blue!=null&&blue!=red && blue!=green) blue.deleteSlice(1);
				}
				rgb.addSlice(null, cp);
				if ((i%inc) == 0) IJ.showProgress((double)i/d);
			}
		IJ.showProgress(1.0);
		} catch(OutOfMemoryError o) {
			IJ.outOfMemory("Merge Stacks");
			IJ.showProgress(1.0);
		}
		return rgb;
	}
	
	private ImagePlus mergeUsingRGBProjection(ImagePlus[] images, boolean createComposite) {
		ImageStack stack = new ImageStack(imp.getWidth(),imp.getHeight());
		for (int i=0; i<images.length; i++) {
			if (images[i]!=null)
				stack.addSlice(images[i].getProcessor());
		}
		ImagePlus imp2 = new ImagePlus("temp", stack);
		ZProjector zp = new ZProjector(imp2);
		zp.setMethod(ZProjector.MAX_METHOD);
		zp.doRGBProjection();
		imp2 = zp.getProjection();
		if (createComposite) {
			imp2 = CompositeConverter.makeComposite(imp2);
			imp2.setTitle("Composite");
		} else
			imp2.setTitle("RGB");
		return imp2;
	}

	byte[] getPixels(ImageStack stack, int slice, int color) {
		 if (stack==null)
			return blank;
		Object pixels = stack.getPixels(slice);
		if (!(pixels instanceof int[])) {
			if (pixels instanceof byte[])
				return (byte[])pixels;
			else {
				ImageProcessor ip = stack.getProcessor(slice);
				ip = ip.convertToByte(true);
				return (byte[])ip.getPixels();
			}
		} else { //RGB
			ColorProcessor cp = (ColorProcessor)stack.getProcessor(slice);
			return ((ColorProcessor)cp).getChannel(color+1);
		}
	}

	byte[] invert(byte[] pixels) {
		byte[] pixels2 = new byte[pixels.length];
		System.arraycopy(pixels, 0, pixels2, 0, pixels.length);
		for (int i=0; i<pixels2.length; i++)
			pixels2[i] = (byte)(255-pixels2[i]&255);
		return pixels2;
	}
	
	void error(String msg) {
		IJ.error("Merge Channels", msg);
	}

}
