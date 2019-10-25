package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

@Plugin(type = Command.class, headless = true,menuPath = "Porthole")
public class Porthole implements Command{

	//Ask context for access to services
	@Parameter
	private LogService logService;
	
	@Parameter
	private UIService uiService;

	public void run() {
		// TODO Auto-generated method stub
		
	}

}
