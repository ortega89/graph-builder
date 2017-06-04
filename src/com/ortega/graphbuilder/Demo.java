package com.ortega.graphbuilder;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Demo implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Demo());
	}

	@Override
	public void run() {
		JFrame frame = new JFrame("Graph Builder Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		
		XYPlane plane = new XYPlane(0.5, 0.5, 100, -10);
		plane.setMarkFrequency(1, 2);
		plane.setLabelFrequency(1, 10);
		
		plane.addGraph(new Series(Color.RED, d -> d*d, -10, 10, 0.2));
		plane.addGraph(new Series(Color.BLUE, d -> (d == 0 ? Double.NaN : 1.0/d), -10, 10, 0.02));
		
		Series custom = new Series(Color.GREEN);
		custom.addPoint(-3, 5);
		custom.addPoint(-2, -12);
		custom.addPoint(-1, 7);
		custom.addPoint(0, 15);
		custom.addPoint(1, 22);
		custom.addPoint(2, 16);
		custom.addPoint(3, 18);
		plane.addGraph(custom);
		
		frame.add(plane);
		
		frame.setVisible(true);
	}

}
