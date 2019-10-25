package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import net.imagej.ops.OpService;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.scijava.app.StatusService;
import org.scijava.command.CommandService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JToolBar;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/* Invoked by porthole command if images are to be selected */
public class PortholeSelectDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9139535193568501417L;
	private OpService ops;
	private LogService log;
	private IOService io;
	private StatusService status;
	private CommandService cmd;
	private ThreadService thread;
	private UIService ui;
	
	private final JPanel contentPanel = new JPanel();
	final JFileChooser fc = new JFileChooser();

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		try {
			final PortholeSelectDialog dialog = new PortholeSelectDialog();
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PortholeSelectDialog() {
		setTitle("Select Images");
		
		List<Img<UnsignedShortType>> rawImages = new LinkedList<Img<UnsignedShortType>>();
		getContentPane().setLayout(new BorderLayout());
		DefaultListModel exampleModel = new DefaultListModel();
				
		JList scrollIMGList = new JList(new ScrlIMGModel(rawImages));	
		scrollIMGList.setVisibleRowCount(14);
		scrollIMGList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollIMGList.setLayoutOrientation(JList.VERTICAL);
		
		JScrollPane scrlIMGPane = new JScrollPane(scrollIMGList);
		scrlIMGPane.setViewportBorder(UIManager.getBorder("InternalFrame.border"));
		scrlIMGPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrlIMGPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scrlIMGPane,BorderLayout.LINE_END);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		scrlIMGPane.setColumnHeaderView(toolBar);		
		
		JButton btnAdd = new JButton("+");
		btnAdd.addActionListener(this);
		btnAdd.setFont(new Font("Tahoma", Font.BOLD, 15));
		toolBar.add(btnAdd);
		
		JButton btnRemove = new JButton("-");
		btnRemove.setFont(new Font("Tahoma", Font.BOLD, 15));
		toolBar.add(btnRemove);
		
		JPanel infoPanel = new JPanel();
		getContentPane().add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		
		JLabel fileinfo = new JLabel("fileinfo");
		infoPanel.add(fileinfo);
		
		JLabel metadata = new JLabel("metadata");
		infoPanel.add(metadata);
		
		JPanel proceedPanel = new JPanel();
		getContentPane().add(proceedPanel, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("Ok");
		proceedPanel.add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		proceedPanel.add(btnCancel);			
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
	}
	

	/**
	 * Accessor and mutator methods
	 * 
	 */

	public OpService getOps() {
		return ops;
	}

	public void setOps(final OpService ops) {
		this.ops = ops;
	}

	public LogService getLog() {
		return log;
	}

	public void setLog(final LogService log) {
		this.log = log;
	}

	public StatusService getStatus() {
		return status;
	}

	public void setStatus(final StatusService status) {
		this.status = status;
	}

	public CommandService getCommand() {
		return cmd;
	}

	public void setCommand(final CommandService command) {
		this.cmd = command;
	}

	public ThreadService getThread() {
		return thread;
	}

	public void setThread(final ThreadService thread) {
		this.thread = thread;
	}

	public UIService getUi() {
		return ui;
	}

	public void setUi(final UIService ui) {
		this.ui = ui;
	}

	public void setIO(final IOService io) {
		this.io = io;	
	}


}

class ScrlIMGModel extends AbstractListModel{

	/**
	 * ListModel method for JScrollPane scrlIMG
	 */
	private static final long serialVersionUID = 7969288241220013206L;
	List<Img<UnsignedShortType>> imgList;
	
	public ScrlIMGModel(List<Img<UnsignedShortType>> arg) {
		imgList = arg;
	}
	
	@Override
	public int getSize() {
		return imgList.size();
	}

	@Override
	public Object getElementAt(int index) {
		return imgList.get(index);
	}
	
}


