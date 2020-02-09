package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
	
	public < T extends RealType<T> & NativeType<T> > BlotterToolPanelDialog() {
		setName("PortholeTools");
		setSize(125, 300);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new FlowLayout());
		
		toolPanel = new JPanel();
		pcaButton = new JButton("PCA");
		
		imgData = new ArrayList<ImgWrapper>();
		
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
					
					//Retrieve image variables
					int bitDepth = imp.getBitDepth();
					double min = ip.getMin();
					double max = ip.getMax();
					String title = imp.getTitle();
					
					//Check if an area has been selected
					if ((roi==null||!roi.isArea())) {
						IJ.error("Area selection required");
						return;
					}
					
					//Check if the correct image has been selected
					if(title.compareTo("FalseRGB") != 0) {
						IJ.error("Please use FalseRGB for area selection");
						return;
					}
				}
				
			});
			toolPanel.add(pcaButton);
		}
		
	}
	
}