package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.util.List;

import org.scijava.plugin.Parameter;

import net.imagej.DatasetService;
import net.imglib2.converter.RealLUTConverter;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class FalseRGBMerger< T extends RealType<T> & NativeType<T> >{
	
	@Parameter
    private DatasetService datasetService;
		
	List<RealLUTConverter<T>> converters;
}