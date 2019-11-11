package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;

import net.imagej.ops.OpService;

/* Invoked when user selects plugin from menu */
@Plugin(type = Command.class, headless = true,menuPath = "Plugins>Porthole>Load Images")
public class PortholeCommand implements Command{

	//Ask context for access to services
	@Parameter
	OpService ops;

	@Parameter
	LogService log;

	@Parameter
	UIService ui;

	@Parameter
	CommandService cmd;

	@Parameter
	StatusService status;

	@Parameter
	ThreadService thread;
	
	@Parameter
	IOService io;
	
	private static PortholeSelectDialog dialogS = null;
	private static PortholeClassifyDialog dialogC = null;
	private static List<File> fileList = new LinkedList<File>();


	public void run() {
		
		/*
		 * Declare and initialise dialogs
		 */
		
		SwingUtilities.invokeLater(() -> {
			if (dialogS == null) {
				dialogS = new PortholeSelectDialog();
			}
			
			dialogS.setOps(ops);
			dialogS.setLog(log);
			dialogS.setStatus(status);
			dialogS.setCommand(cmd);
			dialogS.setThread(thread);
			dialogS.setUi(ui);
			dialogS.setIO(io);
			dialogS.setFileList(fileList);
			dialogS.setTitle("Porthole");
			
							 	  		    
			if (dialogC == null) {
				dialogC = new PortholeClassifyDialog();
			}
			
			dialogC.setOps(ops);
			dialogC.setLog(log);
			dialogC.setStatus(status);
			dialogC.setCommand(cmd);
			dialogC.setThread(thread);
			dialogC.setUi(ui);
			dialogC.setIO(io);
			dialogC.setFileList(fileList);	
			dialogC.setTitle("Porthole");
			
			/*
			 * State machine to handle dialog flow
			 */
			
			/* 1st state */
			dialogS.addWindowListener(new WindowAdapter() {		
				
				//On close of selectDialog, 
				//if nextState is true, open dialogS, else disposeAllUI
				public void windowClosing(WindowEvent e){
					if(dialogS.getNextState()) {
						dialogC.setVisible(true);
						dialogS.dispose();
					}
					else{
						disposeAllUI();
					}					
				}
				
			});
					
			//Place UI in 1st state
			dialogS.setVisible(true);
		 	  		    
	   });	
				
		
	}
	
	/*
	 * Cleans up all Java Swing components
	 */
	public void disposeAllUI() {
		dialogS.dispose();
		dialogC.dispose();
	}
}


