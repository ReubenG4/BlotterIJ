package uk.ac.man.cs.gitlab.reuben_ganesan.porthole;
/* PCA plugin for ImageJ
 * 
 * This plugin uses statistics to calculate an orientation vector
 * for each color in the image. Stacks are considered 3D images.
 * 
 * Theory of operation
 * Calculates the greyscalevalue weighted orientation of all colours in the image
 * by finding the eigenvector associated with the largest eigenvalue of
 * the covariance matrix of the x,y,z coordinates of each colour.
 * Jama (Java matrix package, public domain) is used for extracting the 
 * eigenvalues and eigenvectors from the covariance matrix.
 * The calculation of the covariance matrix uses the fact that 
 * S_ij = E{Xi-E[Xi][Xj-E[Xj]} = E[Xi*Xj] -E[Xi]*E[Xj]
 * 
 * Author: Margret Keuper
 * License: public domain. Free for all use.
 */
import ij.plugin.*;
import ij.*;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import java.text.*;
import java.util.HashMap;
import java.lang.Math;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import ij.gui.*;

public class PCAc_ implements PlugIn {

	private class CovarianceData {
		private long N = 0;
		private double sum_x = 0;
		private double sum_y = 0;
		private double sum_z = 0;
		private double sum_xx = 0;
		private double sum_zz = 0;
		private double sum_yy = 0;
		private double sum_xy = 0;
		private double sum_xz = 0;
		private double sum_yz = 0;
		private boolean empty = false;

		private Matrix covariance = null;
		
		double[] mean(ImageStack stack, int depth, int height, int width) {
			ImageProcessor sip = null;
			for (int z = 1; z <= depth; z++) {
				sip = stack.getProcessor(z);
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int label = sip.get(x, y);
						N+=label;
						sum_x += x*label;
						sum_y += y*label;
						sum_z += (z-1)*label;
					}
				}
			}
			double[] meanValues= new double[3];
			if(N==0){

			empty=true;
			meanValues[0] = 0; // E[X]
			meanValues[1] = 0; // E[Y]
			meanValues[2] = 0; // E[Z]
			}
			else{
			meanValues[0] = sum_x / N; // E[X]
			meanValues[1] = sum_y / N; // E[Y]
			meanValues[2] = sum_z / N; // E[Z]
			}

			return meanValues;

		}
		
		void update(double x, double y, double z, int label) {


			sum_xx += x * x * label;
			sum_yy += y * y * label;
			sum_xy += x * y * label;

			sum_zz += z * z * label;
			sum_yz += y * z * label;
			sum_xz += x * z * label;
		}

		Matrix getMatrix(int dimension) {


			double cov_xx = 0 ;
			double cov_yy = 0 ;
			double cov_zz = 0 ;

			double cov_xy = 0 ;
			double cov_xz = 0 ;
			double cov_zy = 0 ;


			if(empty==true){			

			}
			else{

			cov_xx = sum_xx / N ;
			cov_yy = sum_yy / N ;
			cov_zz = sum_zz / N ;

			cov_xy = sum_xy / N ;
			cov_xz = sum_xz / N ;
			cov_zy = sum_yz / N ;
			}
			if (dimension == 2) {
				double[] tmp = { cov_xx, cov_xy, cov_xy, cov_yy };
				covariance = new Matrix(tmp, dimension);
			} else {
				double[] tmp = { cov_xx, cov_xy, cov_xz, cov_xy, cov_yy,
						cov_zy, cov_xz, cov_zy, cov_zz };
				covariance = new Matrix(tmp, dimension);
			}
			return covariance;
		}
	}

	private ImagePlus[] image=new ImagePlus[3];

	public void run(String arg) {


		 int[] wList = WindowManager.getIDList();
        	if (wList==null) {
            		IJ.error("No images are open.");
            	return;
        	}

        	String[] ititles = new String[wList.length+1];
        	for (int i=0; i<wList.length; i++) {
            		ImagePlus imp = WindowManager.getImage(wList[i]);
            		ititles[i] = imp!=null?imp.getTitle():"";
        	}
        	String none = "*None*";
        	ititles[wList.length] = none;
		
        	GenericDialog gdi = new GenericDialog("PCA");
        	gdi.addChoice("channel1:", ititles, ititles[0]);
		gdi.addNumericField("Theshold:",0,0);
        	gdi.addChoice("channel2:", ititles, ititles[1]);
		gdi.addNumericField("Theshold:",0,0);
        	String title3 = ititles.length>2?ititles[2]:none;
        	gdi.addChoice("reference channel:", ititles, title3);
		gdi.addNumericField("Theshold:",0,0);
        	gdi.showDialog();
        	if (gdi.wasCanceled())
        		return;
		int[] iindex = new int[3];
		double[] thresholds=new double[3];
        	iindex[0] = gdi.getNextChoiceIndex();
        	iindex[1] = gdi.getNextChoiceIndex();
        	iindex[2] = gdi.getNextChoiceIndex();
		thresholds[0]=gdi.getNextNumber();
		thresholds[1]=gdi.getNextNumber();
		thresholds[2]=gdi.getNextNumber();
        	int depth = 0;
        	int width = 0;
        	int height = 0;
        	for (int i=0; i<3; i++) {
            	if (iindex[i]<wList.length) {
                image[i] = WindowManager.getImage(wList[iindex[i]]);
                width = image[i].getWidth();
                height = image[i].getHeight();
                depth = image[i].getStackSize();
            	}
        	}
		if (width==0) {
        	IJ.error("There must be at least one source image or stack.");
        	return;
        	}
        for (int i=0; i<3; i++) {
            ImagePlus img = image[i];
            if (img!=null) {
                if (img.getStackSize()!=depth) {
                    IJ.error("The source stacks must all have the same number of slices.");
                    return;
                }

                if (img.getWidth()!=width || image[i].getHeight()!=height) {
                    IJ.error("The source images or stacks must have the same width and height.");
                    return;
                }
            }
        }

		ImageStack redStack = image[0]!=null?image[0].getStack():null;
		ImageStack greenStack = image[1]!=null?image[1].getStack():null;
		ImageStack blueStack = image[2]!=null?image[2].getStack():null;

		redStack=applyThreshold(redStack, width, height, depth,(int)thresholds[0]);
		greenStack=applyThreshold(greenStack, width, height, depth, (int)thresholds[1]);
		blueStack=applyThreshold(blueStack,  width, height, depth,(int)thresholds[2]);

		ij.measure.Calibration cal = image[0].getCalibration();
		
		double pd = cal.pixelDepth;
		double pw = cal.pixelWidth;
		double ph = cal.pixelHeight;






		ImageProcessor ipc;





		GenericDialog gd = new GenericDialog("PCA Measures");
		gd.addNumericField("Voxelsize x:",pw,3);
		gd.addNumericField("Voxelsize y:",ph,3);
		gd.addNumericField("Voxelsize z:",pd,3);

        	gd.addCheckbox("Principle Components", true);
		gd.addCheckbox("Visualize PCA", false);
        	gd.addCheckbox("Aspect Ratios", true);
        	gd.addCheckbox("Angles to Optical Plane", true);

        	gd.addCheckbox("Angles of all channels to PCs of reference channel", true);
        	gd.addCheckbox("Angles of ReferenceAxis", true);

		



		
        	gd.showDialog();
        	if (gd.wasCanceled())
            	return;
		pw=gd.getNextNumber();
		ph=gd.getNextNumber();
		pd=gd.getNextNumber();
        	boolean[] index = new boolean[6];
        	index[0] = gd.getNextBoolean();
        	index[1] = gd.getNextBoolean();
        	index[2] = gd.getNextBoolean();
        	index[3] = gd.getNextBoolean();
		int chr=0;

        	index[4] = gd.getNextBoolean();
		index[5] = gd.getNextBoolean();

        	int nuc = 2;

		int dimension = 2;
		if (depth > 1) {
			dimension = 3;
		}


		CovarianceData redlabelData   = new CovarianceData();
		CovarianceData greenlabelData = new CovarianceData();
		CovarianceData bluelabelData  = new CovarianceData();

		double[] redMeanValues   = new double[3];
		double[] greenMeanValues = new double[3];
		double[] blueMeanValues  = new double[3];
		double[][] redPCA = new double[dimension][dimension+1];
		double[][] greenPCA = new double[dimension][dimension+1];
		double[][] bluePCA = new double[dimension][dimension+1];

		
		ImageStack rgbEllipses=new ImageStack(width, height);
		
		//Measures
		double[] redAR = new double[3];
		double[] greenAR = new double[3];
		double[] blueAR = new double[3];
		double[] redCellAngles   = new double[3];
		double[] greenCellAngles = new double[3];
		double[] blueCellAngles  = new double[3];
		double[] redOptAngles    = new double[3];
		double[] greenOptAngles  = new double[3];
		double[] blueOptAngles   = new double[3];
		double[] redAnglesToCA   = new double[3];
		double[] greenAnglesToCA   = new double[3];
		double[] blueAnglesToCA   = new double[3];



		int outputsize=0;
		if(index[0]==true){
		outputsize=5;
		}

		int prog;
		int rgb;
		//Is Image RGB or  GreyScale Image?
		//if(gray_rgb==0){//RGB Image - compute PCA of every Colour
		prog=0;
		rgb=3;
		//PCA of red channel

		//mean values in voxels
		redMeanValues= redlabelData.mean(redStack,depth, height, width);
		//convert to um
		redMeanValues[0]=redMeanValues[0]*pw;
		redMeanValues[1]=redMeanValues[1]*ph;
		redMeanValues[2]=redMeanValues[2]*pd;
		redPCA=calcPCA(redStack, width, height, depth,redMeanValues,dimension, pd, ph, pw, redlabelData, prog, rgb);
		//VISUALIZE
		
		prog=1;
		//mean values in voxels
		greenMeanValues = greenlabelData.mean(greenStack,depth, height, width);
		//convert to um
		greenMeanValues[0]=greenMeanValues[0]*pw;
		greenMeanValues[1]=greenMeanValues[1]*ph;
		greenMeanValues[2]=greenMeanValues[2]*pd;
		//PCA of green channel
		greenPCA=calcPCA(greenStack, width, height, depth,greenMeanValues,dimension, pd, ph, pw, greenlabelData, prog, rgb);
		
		prog=2;
		//mean values in voxels
		blueMeanValues  = bluelabelData.mean(blueStack,depth, height, width);
		//convert to um
		blueMeanValues[0]=blueMeanValues[0]*pw;
		blueMeanValues[1]=blueMeanValues[1]*ph;
		blueMeanValues[2]=blueMeanValues[2]*pd;


		//PCA of blue channel
		bluePCA=calcPCA(blueStack, width, height, depth,blueMeanValues,dimension, pd, ph, pw, bluelabelData, prog, rgb);

		if(index[1]==true){
			//VISUALIZE
			redStack=drawEllipsoid(redPCA, redStack, width, height, depth, redMeanValues,dimension, pd, ph, pw);
			greenStack=drawEllipsoid(greenPCA, greenStack, width, height, depth, greenMeanValues,dimension, pd, ph, pw);
			blueStack=drawEllipsoid(bluePCA, blueStack, width, height, depth, blueMeanValues, dimension, pd, ph, pw);
		
			rgbEllipses= mergeStacks(width, height, depth, redStack, greenStack, blueStack);
			}
		if(index[2]==true){
			redAR= aspectRatio(redPCA);
			greenAR= aspectRatio(greenPCA);
			blueAR= aspectRatio(bluePCA);
			outputsize+=1;
		}
		if(index[3]==true){
		redOptAngles= anglesToOptPlane(redPCA);
		greenOptAngles= anglesToOptPlane(greenPCA);
		blueOptAngles= anglesToOptPlane(bluePCA);
		outputsize+=1;
		}
		if(index[4]==true){
		outputsize+=1;
		if(nuc==0){
		redCellAngles= anglesToCellPlane(redPCA, redPCA);
		greenCellAngles= anglesToCellPlane(greenPCA, redPCA);
		blueCellAngles= anglesToCellPlane(bluePCA, redPCA);
		}
		if(nuc==1){
		redCellAngles= anglesToCellPlane(redPCA, greenPCA);
		greenCellAngles= anglesToCellPlane(greenPCA, greenPCA);
		blueCellAngles= anglesToCellPlane(bluePCA, greenPCA);
		}
		if(nuc==2){
		redCellAngles= anglesToCellPlane(redPCA, bluePCA);
		greenCellAngles= anglesToCellPlane(greenPCA, bluePCA);
		blueCellAngles= anglesToCellPlane(bluePCA, bluePCA);
		}
		}
		if(index[5]==true){
		outputsize+=1;
		if(nuc==0){
		redAnglesToCA[0]=0;
		redAnglesToCA[1]=0;
		redAnglesToCA[2]=0;
		greenAnglesToCA=angeltocenteraxis(greenPCA, greenMeanValues, redMeanValues);
		blueAnglesToCA=angeltocenteraxis(bluePCA, blueMeanValues, redMeanValues);
		}
		if(nuc==1){
		redAnglesToCA=angeltocenteraxis(redPCA, redMeanValues, greenMeanValues);
		greenAnglesToCA[0]=0;
		greenAnglesToCA[1]=0;
		greenAnglesToCA[2]=0;
		blueAnglesToCA=angeltocenteraxis(bluePCA, blueMeanValues, greenMeanValues);
		}
		if(nuc==2){
		redAnglesToCA=angeltocenteraxis(redPCA, redMeanValues, blueMeanValues);
		greenAnglesToCA=angeltocenteraxis(greenPCA, greenMeanValues, blueMeanValues);
		blueAnglesToCA[0]=0;
		blueAnglesToCA[1]=0;
		blueAnglesToCA[2]=0;
		}
		}
		
		

		//write out results and show visualization
		String table=" ";
		String tableG="";
		String tableB="";		
		int outsize=0;

		outsize=3*outputsize;


		int redoutsize=0;
		double[] out = new double[3*outsize];
		double[] redOut = new double[3*outputsize];
		double[] greenOut = new double[3*outputsize];
		double[] blueOut = new double[3*outputsize];

		if(index[0]==true){
		table=table+"\tch1_Eigenvector_1\t \t \tch1_Eigenvector_2\t \t \tch1_Eigenvector_3\t \t \tch1_Eigenvalue_1\tch1_Eigenvalue_2\tch1_Eigenvalue_3\tch1_C1\tch1_C2\tch1_C3";
		tableG=tableG+"\tch2_Eigenvector_1\t \t \tch2_Eigenvector_2\t \t \tch2_Eigenvector_3\t \t \tch2_Eigenvalue_1\tch2_Eigenvalue_2\tch2_Eigenvalue_3\tch2_C1\tch2_C2\tch2_C3";
		tableB=tableB+"\tch3_Eigenvector_1\t \t \tch3_Eigenvector_2\t \t \tch3_Eigenvector_3\t \t \tch3_Eigenvalue_1\tch3_Eigenvalue_2\tch3_Eigenvalue_3\tch3_C1\tch3_C2\tch3_C3";
		redoutsize+=4;
		for(int i = 0; i < 4; i++){
		redOut[i*3+0]=redPCA[0][i];
		redOut[i*3+1]=redPCA[1][i];
		redOut[i*3+2]=redPCA[2][i];
		greenOut[i*3+0]=greenPCA[0][i];
		greenOut[i*3+1]=greenPCA[1][i];
		greenOut[i*3+2]=greenPCA[2][i];
		blueOut[i*3+0]=bluePCA[0][i];
		blueOut[i*3+1]=bluePCA[1][i];
		blueOut[i*3+2]=bluePCA[2][i];
		}
		redOut[12]=redMeanValues[0];
		redOut[13]=redMeanValues[1];
		redOut[14]=redMeanValues[2];
		greenOut[12]=greenMeanValues[0];
		greenOut[13]=greenMeanValues[1];
		greenOut[14]=greenMeanValues[2];
		blueOut[12]=blueMeanValues[0];
		blueOut[13]=blueMeanValues[1];
		blueOut[14]=blueMeanValues[2];


		}
		if(index[2]==true){
		table=table+"\tch1_AspectRatio_2_1\tch1_AspectRatio_3_1\tch1_AspectRatio_3_2";
		tableG=tableG+"\tch2_AspectRatio_2_1\tch2_AspectRatio_3_1\tch2_AspectRatio_3_2";
		tableB=tableB+"\tch3_AspectRatio_2_1\tch3_AspectRatio_3_1\tch3_AspectRatio_3_2";

		redoutsize+=1;
		redOut[redoutsize*3+0]=redAR[0];
		redOut[redoutsize*3+1]=redAR[1];
		redOut[redoutsize*3+2]=redAR[2];
		greenOut[redoutsize*3+0]=greenAR[0];
		greenOut[redoutsize*3+1]=greenAR[1];
		greenOut[redoutsize*3+2]=greenAR[2];
		blueOut[redoutsize*3+0]=blueAR[0];
		blueOut[redoutsize*3+1]=blueAR[1];
		blueOut[redoutsize*3+2]=blueAR[2];

		}
		if(index[3]==true){
		table=table+"\tch1_AnglesOP_1\tch1_AnglesOP_2\tch1_AnglesOP_3";
		tableG=tableG+"\tch2_AnglesOP_1\tch2_AnglesOP_2\tch2_AnglesOP_3";
		tableB=tableB+"\tch3_AnglesOP_1\tch3_AnglesOP_2\tch3_AnglesOP_3";
		redoutsize+=1;
		redOut[redoutsize*3+0]=redOptAngles[0];
		redOut[redoutsize*3+1]=redOptAngles[1];
		redOut[redoutsize*3+2]=redOptAngles[2];
		greenOut[redoutsize*3+0]=greenOptAngles[0];
		greenOut[redoutsize*3+1]=greenOptAngles[1];
		greenOut[redoutsize*3+2]=greenOptAngles[2];
		blueOut[redoutsize*3+0]=blueOptAngles[0];
		blueOut[redoutsize*3+1]=blueOptAngles[1];
		blueOut[redoutsize*3+2]=blueOptAngles[2];
		}		
		if(index[4]==true){
		table=table+"\tch1_AnglesRP_1\tch1_AnglesRP_2\tch1_AnglesRP_3";
		tableG=tableG+"\tch2_AnglesRP_1\tch2_AnglesRP_2\tch2_AnglesRP_3";
		tableB=tableB+"\tch3_AnglesRP_1\tch3_AnglesRP_2\tch3_AnglesRP_3";	
		redoutsize+=1;
		redOut[redoutsize*3+0]=redCellAngles[0];
		redOut[redoutsize*3+1]=redCellAngles[1];
		redOut[redoutsize*3+2]=redCellAngles[2];

		greenOut[redoutsize*3+0]=greenCellAngles[0];
		greenOut[redoutsize*3+1]=greenCellAngles[1];
		greenOut[redoutsize*3+2]=greenCellAngles[2];
		
		blueOut[redoutsize*3+0]=blueCellAngles[0];
		blueOut[redoutsize*3+1]=blueCellAngles[1];
		blueOut[redoutsize*3+2]=blueCellAngles[2];
		}		
		if(index[5]==true){
		table=table+"\tch1_AnglesRAxis_1\tch1_AnglesRAxis_2\tch1_AnglesRAxis_3";
		tableG=tableG+"\tch2_AnglesRAxis_1\tch2_AnglesRAxis_2\tch2_AnglesRAxis_3";
		tableB=tableB+"\tch3_AnglesRAxis_1\tch3_AnglesRAxis_2\tch3_AnglesRAxis_3";	
		redoutsize+=1;
		redOut[redoutsize*3+0]=redAnglesToCA[0];
		redOut[redoutsize*3+1]=redAnglesToCA[1];
		redOut[redoutsize*3+2]=redAnglesToCA[2];

		greenOut[redoutsize*3+0]=greenAnglesToCA[0];
		greenOut[redoutsize*3+1]=greenAnglesToCA[1];
		greenOut[redoutsize*3+2]=greenAnglesToCA[2];
		
		blueOut[redoutsize*3+0]=blueAnglesToCA[0];
		blueOut[redoutsize*3+1]=blueAnglesToCA[1];
		blueOut[redoutsize*3+2]=blueAnglesToCA[2];
		}
		for(int i = 0; i < 3*outputsize; i++) {
		out[i]=redOut[i];
		out[i+3*outputsize]=greenOut[i];
		out[i+2*3*outputsize]=blueOut[i];

		}
		table=table+tableG+tableB;
		
		

		if (!table.equals(IJ.getTextPanel().getColumnHeadings())) {
			if(!IJ.getTextPanel().getText().equals("")){
			GenericDialog gd_newTable = new GenericDialog("New Table");
			gd_newTable.addCheckbox("save results table prior to overwriting?", true);
			gd_newTable.showDialog();
		
        		if (gd_newTable.wasCanceled()){

            			return;
				}

			if(gd_newTable.getNextBoolean()==true){


				IJ.getTextPanel().saveAs("");
				
			}
			}
			IJ.setColumnHeadings(table);
		}


		IJ.write("R_" + image[2].getTitle()+"\t"+printMatrix(out));


		if(index[1]==true){
		ImagePlus imp2=new ImagePlus(image[2].getTitle()+"_PCA",rgbEllipses);
		imp2.setCalibration(cal);
		imp2.show();
		}
	}

	private double[][] getOrientation(Matrix covariance) {
		int dimension = covariance.getColumnDimension();
		
		// Get eigenvalues and associated eigenvectors
		EigenvalueDecomposition ed = covariance.eig();
		double[][] ev = new double[3][4];
		if(dimension==3){
		ev[0][0]=ed.getV().get(0,2);
		ev[0][1]=ed.getV().get(0,1);
		ev[0][2]=ed.getV().get(0,0);
		ev[1][0]=ed.getV().get(1,2);
		ev[1][1]=ed.getV().get(1,1);
		ev[1][2]=ed.getV().get(1,0);		
		ev[2][0]=ed.getV().get(2,2);
		ev[2][1]=ed.getV().get(2,1);
		ev[2][2]=ed.getV().get(2,0);
		ev[0][3]=Math.sqrt(ed.getD().get(2,2));
		ev[1][3]=Math.sqrt(ed.getD().get(1,1));
		ev[2][3]=Math.sqrt(ed.getD().get(0,0));
		}
		else{
		ev[0][0]=ed.getV().get(0,1);
		ev[0][1]=ed.getV().get(0,0);
		ev[0][2]=0;
		ev[1][0]=ed.getV().get(1,1);
		ev[1][1]=ed.getV().get(1,0);
		ev[1][2]=0;
		ev[1][3]=Math.sqrt(ed.getD().get(0,0));	
		ev[0][3]=Math.sqrt(ed.getD().get(1,1));

		ev[2][0]=0;
		ev[2][1]=0;
		ev[2][2]=0;
		ev[2][3]=0;
		}
		
		//if there is 2D data in a 3D stack, assume eigenvalue to be half of the voxelsize
		//if(dimension==3&&ev[0][3]==0){
		//	ev[0][3]=pw/2;
		//}
		//if(dimension==3&&ev[1][3]==0){
		//	ev[1][3]=ph/2;
		//}
		//if(dimension==3&&ev[2][3]==0){
		//	ev[2][3]=pd/2;
		//}
		return ev;
	}
	private ImageStack applyThreshold(ImageStack stack, int width, int height, int depth,int t){
		ImageProcessor sip = null;

		ImageStack stackt=new ImageStack(width, height);

		for (int z = 1; z <= depth; z++) {
			sip = stack.getProcessor(z);
		ByteProcessor sipc =new ByteProcessor(width, height);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
				int label = sip.get(x, y);
				label=label-t;
				if(label<0){
				label=0;
				}
				sipc.set(x,y,label);
				}
			}
		stackt.addSlice(null,sipc);
		}
		return stackt;

		}


	private double[] normalizeVector(double[] input) {
		double sum = 0;
		final int length = input.length;
		for (int i = 0; i < length; i++) {
			sum += input[i] * input[i];
		}
		sum = Math.sqrt(sum);
		double[] output = new double[length];
		for (int i = 0; i < length; i++) {
			output[i] = input[i] / sum;
		}
		return output;
	}

	private double[][] calcPCA(ImageStack stack, int width, int height, int depth, double[] meanValues, int dimension, double pd, double ph, double pw,CovarianceData labelData, int prog, int rgb){

	ImageProcessor sip = null;
	double[][] PCA = new double[dimension][dimension+1];
	Matrix covariance = new Matrix(dimension, dimension);
	double z_pd = 1;		
	double y_ph = 1;
	double x_pw = 1;
	for (int z = 1; z <= depth; z++) {
		IJ.showProgress(prog*depth/rgb+z/rgb, depth);
		sip = stack.getProcessor(z);
		z_pd = (z-1)*pd-meanValues[2];
		for (int x = 0; x < width; x++) {
			x_pw = x*pw-meanValues[0];
			for (int y = 0; y < height; y++) {
				int label = sip.get(x, y);
				y_ph = y*ph-meanValues[1];
				labelData.update(x_pw, y_ph, z_pd, label);
			}
		}

	}
	
	covariance   = labelData.getMatrix(dimension);
	PCA   = getOrientation(covariance);
	return PCA;

	}


	private ImageStack drawEllipsoid(double[][] ev, ImageStack stackc, int width, int height, int depth, double[] MV, int dimension, double pd, double ph, double pw) {
		//Visualization - draw ellipsiods in a copy of the original Image Stack
		ImageStack stack=new ImageStack(width, height);
		stack=stackc;
		ImageStack Ellipse=new ImageStack(width, height);
		ImageProcessor sip = null;
		ImageProcessor sipc = null;	
		double[] ev1=new double[3];
		double[] ev2=new double[3];
		double[] ev3=new double[3];
		ev1[0]=ev[0][0];
		ev1[1]=ev[1][0];
		ev1[2]=ev[2][0];
		ev2[0]=ev[0][1];
		ev2[1]=ev[1][1];
		ev2[2]=ev[2][1];
		ev3[0]=ev[0][2];
		ev3[1]=ev[1][2];
		ev3[2]=ev[2][2];
		ev1=normalizeVector(ev1);
		ev2=normalizeVector(ev2);
		ev3=normalizeVector(ev3);

		double[] ed=new double[3];
		ed[0]=ev[0][3];
		ed[1]=ev[1][3];
		ed[2]=ev[2][3];

		for (int z = 1; z <= depth; z++) {
			sip = stack.getProcessor(z);
			sipc=sip;
			if(ed[0]>0 && ed[1]>0 && ed[2]>0){
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int label = sipc.get(x, y);
					
					
					double x1=0.5*(ev1[0]*(x*pw-MV[0])+ev1[1]*(y*ph-MV[1])+ev1[2]*((z-1)*pd-MV[2]))/ed[0];
					double y1=0.5*(ev2[0]*(x*pw-MV[0])+ev2[1]*(y*ph-MV[1])+ev2[2]*((z-1)*pd-MV[2]))/ed[1];
					double z1=0.5*(ev3[0]*(x*pw-MV[0])+ev3[1]*(y*ph-MV[1])+ev3[2]*((z-1)*pd-MV[2]))/ed[2];
					
					double x2=0.5*(ev1[0]*((x+1)*pw-MV[0])+ev1[1]*((y+1)*ph-MV[1])+ev1[2]*(z*pd-MV[2]))/ed[0];
					double y2=0.5*(ev2[0]*((x+1)*pw-MV[0])+ev2[1]*((y+1)*ph-MV[1])+ev2[2]*(z*pd-MV[2]))/ed[1];


					if((x1*x1+y1*y1+z1*z1<=1)&&(x2*x2+y1*y1+z1*z1>=1)||(x1*x1+y1*y1+z1*z1>=1)&&(x2*x2+y1*y1+z1*z1<=1)||       (x1*x1+y1*y1+z1*z1<=1)&&(x1*x1+y2*y2+z1*z1>=1)||(x1*x1+y1*y1+z1*z1>=1)&&(x1*x1+y2*y2+z1*z1<=1)||       (x1*x1+y1*y1+z1*z1<=1)&&(x2*x2+y2*y2+z1*z1>=1)||(x1*x1+y1*y1+z1*z1>=1)&&(x2*x2+y2*y2+z1*z1<=1)){
						sipc.set(x,y,255);	
					}
					else{
						sipc.set(x,y,9*label/10);
					}
				}
			}
			}
			else{
				if(ed[0]>0 && ed[1]>0 ){
				for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int label = sipc.get(x, y);
			
					
					double x1=0.5*(ev1[0]*(x*pw-MV[0])+ev1[1]*(y*ph-MV[1]))/ed[0];
					double y1=0.5*(ev2[0]*(x*pw-MV[0])+ev2[1]*(y*ph-MV[1]))/ed[1];
					double z1=0.5*(z-1-MV[2])*pd;
					
					double x2=0.5*(ev1[0]*((x+1)*pw-MV[0])+ev1[1]*((y+1)*ph-MV[1]))/ed[0];
					double y2=0.5*(ev2[0]*((x+1)*pw-MV[0])+ev2[1]*((y+1)*ph-MV[1]))/ed[1];


					if((x1*x1+y1*y1+z1*z1<=1)&&(x2*x2+y1*y1+z1*z1>=1)||(x1*x1+y1*y1+z1*z1>=1)&&(x2*x2+y1*y1+z1*z1<=1)||       (x1*x1+y1*y1+z1*z1<=1)&&(x1*x1+y2*y2+z1*z1>=1)||(x1*x1+y1*y1+z1*z1>=1)&&(x1*x1+y2*y2+z1*z1<=1)||       (x1*x1+y1*y1+z1*z1<=1)&&(x2*x2+y2*y2+z1*z1>=1)||(x1*x1+y1*y1+z1*z1>=1)&&(x2*x2+y2*y2+z1*z1<=1)){
						sipc.set(x,y,255);	
					}
					else{
						sipc.set(x,y,9*label/10);
					}
				}
			}
			}
			}
			Ellipse.addSlice(null,sipc);
		}
		return Ellipse;
	}

	private double[] angeltocenteraxis(double[][] ev, double[] c1, double[] c2) {	
	//calculates the aspect ratio between the Eigenvalues

 
	double[] ra=new double[3];
	ra[0]=c1[0]-c2[0];
	ra[1]=c1[1]-c2[1];
	ra[2]=c1[2]-c2[2];
	double[] aca=new double[3];
	if(ra[0]!=0||ra[1]!=0||ra[2]!=0){
	ra=normalizeVector(ra);

	
	aca[0]=Math.acos(Math.abs(ra[0]*ev[0][0]+ra[1]*ev[1][0]+ra[2]*ev[2][0])-0.0000000000000005);
	aca[1]=Math.acos(Math.abs(ra[0]*ev[0][1]+ra[1]*ev[1][1]+ra[2]*ev[2][1])-0.0000000000000005);
	aca[2]=Math.acos(Math.abs(ra[0]*ev[0][2]+ra[1]*ev[1][2]+ra[2]*ev[2][2])-0.0000000000000005);
	}
	else{
	aca[0]=0;
	aca[1]=0;
	aca[2]=0;
	}
	return aca;
	}

	private double[] aspectRatio(double[][] ev) {	
	//calculates the aspect ratio between the Eigenvalues 
	double[] ar=new double[3];
	if(ev[0][3]>0){
	ar[0]=ev[1][3]/ev[0][3];//second largest by largest Eigenvalue
	ar[1]=ev[2][3]/ev[0][3];//third largest by largest Eigenvalue
	}
	else{
	ar[0]=0;
	ar[1]=0;
	}
	if(ev[1][3]>0){
	ar[2]=ev[2][3]/ev[1][3];//third largest by second largest Eigenvalue
	}
	else{

	ar[2]=0;
	}
	return ar;
	}
	
	private double[] anglesToCellPlane(double[][] ev1, double[][] ev2 ) {
	//calculates the angle between the plane defined by the two longest 
	//eigenvectors of in ev1 and every eigenvector in ev2 

	double[] angles      = new double[3];
	//plane normal of ev1
	double[] planeNormal = new double[3];
	planeNormal[0]=ev1[1][0]*ev1[2][1]-ev1[2][0]*ev1[1][1];
	planeNormal[1]=ev1[2][0]*ev1[0][1]-ev1[0][0]*ev1[2][1];
	planeNormal[2]=ev1[0][0]*ev1[1][1]-ev1[1][0]*ev1[0][1];
	planeNormal=normalizeVector(planeNormal);

	angles[0]=3.14159/2-Math.acos(Math.abs(planeNormal[0]*ev2[0][0]+planeNormal[1]*ev2[1][0]+planeNormal[2]*ev2[2][0])-0.0000000000000005);
	angles[1]=3.14159/2-Math.acos(Math.abs(planeNormal[0]*ev2[0][1]+planeNormal[1]*ev2[1][1]+planeNormal[2]*ev2[2][1])-0.0000000000000005);
	angles[2]=3.14159/2-Math.acos(Math.abs(planeNormal[0]*ev2[0][2]+planeNormal[1]*ev2[1][2]+planeNormal[2]*ev2[2][2])-0.0000000000000005);

	return angles;
	}

	private double[] anglesToOptPlane(double[][] ev ) {
	//calculates the angle between  every eigenvector in ev and the x-y-plane 
	double[] angles      = new double[3];
	
	angles[0]=3.14159/2-Math.acos(Math.abs(ev[2][0])-0.0000000000000005);
	angles[1]=3.14159/2-Math.acos(Math.abs(ev[2][1])-0.0000000000000005);
	angles[2]=3.14159/2-Math.acos(Math.abs(ev[2][2])-0.0000000000000005);
	return angles;
	}

	private String printMatrix(double[] input) {
		double[][] wrap = new double[1][];
		wrap[0] = input;
		return printMatrix(wrap);
	}

	private String printMatrix(double[][] input) {
		final DecimalFormat df5 = new DecimalFormat("#0.00000");
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[i].length; j++) {
				out.append(df5.format(input[i][j]));
				if (j != input[i].length - 1) {
					out.append("\t");
				}
			}
			out.append("\n");
		}
		return new String(out);
	}

    	public ImageStack mergeStacks(int w, int h, int d, ImageStack red, ImageStack green, ImageStack blue) {
        ImageStack rgb = new ImageStack(w, h);
        ColorProcessor cp;

        ImageProcessor redPixels, greenPixels, bluePixels;

        for (int i=1; i<=d; i++) {
         	cp = new ColorProcessor(w, h);
		redPixels=red.getProcessor(1);
		redPixels = redPixels.convertToByte(true);
		greenPixels=green.getProcessor(1);
		greenPixels = greenPixels.convertToByte(true);
		bluePixels=blue.getProcessor(1);
		bluePixels = bluePixels.convertToByte(true);
                cp.setRGB((byte[])redPixels.getPixels(), (byte[])greenPixels.getPixels(),(byte[])bluePixels.getPixels() );
    

              	red.deleteSlice(1);
                green.deleteSlice(1);
                blue.deleteSlice(1);
            rgb.addSlice(null, cp);

            }

        return rgb;
	}


	//public int setup(String arg, final ImagePlus[] image) {
//		this.image = image;
//		if (arg.equals("about")) {
//			showAbout();
//			return DONE;
//		}
//		return DOES_16 + DOES_8G + DOES_RGB + NO_CHANGES;
//	}

//	private void showAbout() {
//		IJ.showMessage("Calculates and visualizes the PCA (Principle Component Analysis ) of an RGB or Greyscale image\n"
//			+ "by finding the eigenvector associated with the largest eigenvalue of\n"
//			+ "the covariance matrix of the x,y,z coordinates of colour.");
//	}
}
