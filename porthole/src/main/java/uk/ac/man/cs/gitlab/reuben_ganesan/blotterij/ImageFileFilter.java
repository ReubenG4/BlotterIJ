package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.io.File;
import java.io.FileFilter;

public class ImageFileFilter implements FileFilter
{
	
 
  private final String[] okFileExtensions = new String[] {"tiff","tif"};
  private String description= ".tif, .tiff";

  public boolean accept(File file)
  {
    for (String extension : okFileExtensions)
    {
      if (file.getName().toLowerCase().endsWith(extension))
      {
        return true;
      }
    }
    return false;
  }
  
  public String getDescription() {
	    return description;
 }


}