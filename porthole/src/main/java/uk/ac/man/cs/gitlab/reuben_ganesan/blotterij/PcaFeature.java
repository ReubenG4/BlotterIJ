package uk.ac.man.cs.gitlab.reuben_ganesan.blotterij;

import org.apache.commons.math4.linear.RealVector;

public class PcaFeature{
	RealVector eigenvector;
	double eigenvalue;
	
	PcaFeature(RealVector eigenvector, double eigenvalue ){
		this.eigenvector = eigenvector;
		this.eigenvalue = eigenvalue;
	}
	

	public double getValue() {
		return eigenvalue;
	}
	
	public RealVector getVector() {
		return eigenvector;
	}

}