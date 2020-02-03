package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class PortholeToolPanelDialog extends PortholeDialog{
	/*
	 * Declare JComponents
	 */		
	JPanel toolPanel;
	JButton pcaButton;
	
	public < T extends RealType<T> & NativeType<T> > PortholeToolPanelDialog() {
		setName("PortholeTools");
		setSize(125, 300);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new FlowLayout());
		
		toolPanel = new JPanel();
		pcaButton = new JButton("PCA");
		
		imgData = new ArrayList<ImgPlusMeta>();
		
		getContentPane().add(toolPanel);
		{
			toolPanel.add(pcaButton);
		}
		
	}
	
}