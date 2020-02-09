package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
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
					// TODO Auto-generated method stub
					
				}
				
			});
			toolPanel.add(pcaButton);
		}
		
	}
	
}