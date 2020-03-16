package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class BlotterToolPanelDialog extends BlotterDialog{
	/*
	 * Declare JComponents
	 */		
	JPanel toolPanel;
	JButton pcaButton;
	JButton specButton;
	
	/*
	 * Declare Class variables
	 */
	Rectangle selection = null;
	
	public < T extends RealType<T> & NativeType<T> > BlotterToolPanelDialog() {
		setName("BlotterTools");
		setSize(160, 300);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
		
		toolPanel = new JPanel();
		pcaButton = new JButton("PC. Analysis");
		pcaButton.setPreferredSize(new Dimension(125,30));
		specButton = new JButton("Spec. Analysis");
		specButton.setPreferredSize(new Dimension(125,30));
		
		getContentPane().add(toolPanel);
		{
			pcaButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					/* Retrieve region of interest from imageJ UI */
					
					//Declare and initalise variables
					ImagePlus imp = IJ.getImage();
					ImageProcessor ip = imp.getProcessor();
					Roi roi = imp.getRoi();
					
					String errorMsg = "Rectangular area selection of FalseRGB required";
					
					//Retrieve image title
					String title = imp.getTitle();
					
					//Check if an area has been selected
					if ((roi==null||!roi.isArea())) {
						IJ.error(errorMsg);
						return;
					}
					
					//Check if the correct image has been selected
					if(title.compareTo("FalseRGB") != 0) {
						IJ.error(errorMsg);
						return;
					}
					
					//With selection verified, get the rectangle
					selection = roi.getBounds();
					
					//Set flag for next state
					setNextState(true);
					setNextStateIndex(4);
					setVisible(false);
				}
				
			});
			
			specButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					//Set flag for next state
					setNextState(true);
					setNextStateIndex(7);
					setVisible(false);
				}
				
			});
			toolPanel.add(pcaButton);
			toolPanel.add(specButton);
		}
		
	}
	
	public Rectangle getSelection() {
		return selection;
	}

	public void clearSelection() {
		selection = null;	
	}
	
}