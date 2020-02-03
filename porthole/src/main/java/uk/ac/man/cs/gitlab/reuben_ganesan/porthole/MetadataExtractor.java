package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * Helper class to extract metadata from filenames
 */
public class MetadataExtractor{
	Pattern pattern = Pattern.compile("(.*)(-)(\\d+)(\\D)(_)(.*)");
    Matcher matcher;
	
	
	public void setFilename(String input) {
		matcher = pattern.matcher(input);
	}
		
	public String getFileName() {
		if(matcher.matches())
			return matcher.group(0);
		else
			return "Filename";
	}
	
	public String getProject() {
		if(matcher.matches())
			return matcher.group(1);
		else
			return "Project";
	}
	
	public int getWavelength() {
		if(matcher.matches())
			return Integer.parseInt(matcher.group(3));
		else
			return 999;
	}
	
	
	public char getType() {
		if(matcher.matches())
			return matcher.group(4).charAt(0);
		else
			return 'X';
	}	
	
}