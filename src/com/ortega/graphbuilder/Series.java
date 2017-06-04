package com.ortega.graphbuilder;

import java.awt.Color;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.DoubleFunction;

public class Series {
	private SortedMap<Double, Double> points;
	private Color color;
	
	public Series(Color color) {
		this.points = new TreeMap<>();
		this.color = color;
	}
	
	public Series(Color color, DoubleFunction<Double> func, double min, double max, double step) {
		this(color);
		for (double q = min; q <= max; q += step) {
			q = Math.round(q * 1000000) / 1000000.0;
			double f = func.apply(q);
			points.put(q, f);
		}
	}
	
	public SortedMap<Double, Double> getPoints() {
		return points;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void addPoint(double x, double y) {
		points.put(x, y);
	}
}
