package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

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
@Plugin(type = Command.class, headless = true,menuPath = "Plugins>Porthole>Start")
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

	public void run() {
		
		//Run dialog box to select wanted images
		SwingUtilities.invokeLater(() -> {
			if (dialogS == null) {
				dialogS = new PortholeSelectDialog();
			}
			dialogS.setVisible(true);

			dialogS.setOps(ops);
			dialogS.setLog(log);
			dialogS.setStatus(status);
			dialogS.setCommand(cmd);
			dialogS.setThread(thread);
			dialogS.setUi(ui);
			dialogS.setIO(io);

		});
		
		
	}

}
