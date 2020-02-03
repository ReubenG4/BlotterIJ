package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class PortholeToolPanel extends PortholeDialog{
	/*
	 * Declare JComponents
	 */		
	JPanel toolPanel;
	JButton pcaButton;
	
	public < T extends RealType<T> & NativeType<T> > PortholeToolPanel() {
		setName("Porthole Tools");
		setBounds(300, 300, 100, 300);
		getContentPane().setLayout(new FlowLayout());
		
		toolPanel = new JPanel();
		pcaButton = new JButton();
		
	}
	
}