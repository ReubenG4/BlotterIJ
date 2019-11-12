package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

/* Classifies images into different wavelength bands */
public class PortholeClassifyDialog extends PortholeDialog {

	/**
	 * Declare and initialise class variables
	 */
	private JPanel buttonPanel = new JPanel();
	private JPanel dataPanel = new JPanel();
	private JPanel confirmPanel = new JPanel();
	private JPanel bandPanel = new JPanel();
	private DefaultListModel<File> imageListModel = new DefaultListModel<File>();
	private JList<File> imageList = new JList<File>(imageListModel);

	/**
	 * Create the dialog.
	 */
	public PortholeClassifyDialog() {
		setName("PortholeClassify");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		buttonPanel.setLayout(new FlowLayout());
		dataPanel.setLayout(new FlowLayout());
		confirmPanel.setLayout(new FlowLayout());
		bandPanel.setLayout(new FlowLayout());
				
		
	}

	



	

}
