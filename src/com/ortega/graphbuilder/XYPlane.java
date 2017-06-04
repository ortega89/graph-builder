package com.ortega.graphbuilder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import static com.ortega.graphbuilder.Constants.*;

@SuppressWarnings("serial")
public class XYPlane extends JPanel {
	private double originX01;
	private double originY01;
	
	private int unitWidth;
	private int unitHeight;
	
	private int markFreqX = 1;
	private int markFreqY = 1;
	
	private int labelFreqX = 1;
	private int labelFreqY = 1;
	
	private List<Series> graphs = new ArrayList<>();
	
	public XYPlane(double originX01, double originY01, int unitWidth, int unitHeight) {
		this.originX01 = originX01;
		this.originY01 = originY01;
		this.unitWidth = unitWidth;
		this.unitHeight = unitHeight;
	}
	
	public void setMarkFrequency(int freqX, int freqY) {
		markFreqX = freqX;
		markFreqY = freqY;
	}
	
	public void setLabelFrequency(int freqX, int freqY) {
		labelFreqX = freqX;
		labelFreqY = freqY;
	}
	
	public void addGraph(Series series) {
		graphs.add(series);
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		prepareGraphics(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		drawCoordinateSystem(g);
		
		for (Series s : graphs) {
			drawGraph(s, g);
		}
	}
	
	private Graphics2D prepareGraphics(Graphics g) {
		Graphics2D gg = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	    gg.setRenderingHints(rh);
	    return gg;
	}
	
	private void drawGraph(Series series, Graphics g) {
		Rectangle bounds = getBounds();
		
		g.setColor(series.getColor());
		Map<Double, Double> points = series.getPoints();
		Point2D prevPoint = null;		
		
		Point p1 = null;
		Point p2 = null;
		
		List<Point> polyline = new ArrayList<>();
		
		for (Map.Entry<Double, Double> p : points.entrySet()) {
			Point2D newPoint = new Point2D.Double(p.getKey(), p.getValue());
			p2 = getDrawingPoint(newPoint);
			if (prevPoint != null && p1 != null) {
				if (p1 != null)
					polyline.add(p1);
				if (p2 == null || (!bounds.contains(p1) && !bounds.contains(p2))) {
					if (p2 != null)
						polyline.add(p2);
					drawPolyline(polyline, g);
					polyline.clear();
				}
			}
			prevPoint = newPoint;
			p1 = p2;
		}
		
		if (p2 != null) {
			polyline.add(p2);
			drawPolyline(polyline, g);
		}
	}
	
	private void drawPolyline(List<Point> polyline, Graphics g) {
		if (polyline.size() < 2)
			return;
		int[] x = new int[polyline.size()];
		int[] y = new int[polyline.size()];
		for (int i = 0; i < polyline.size(); i++) {
			Point p = polyline.get(i);
			x[i] = p.x;
			y[i] = p.y;
		}
		g.drawPolyline(x, y, polyline.size());
	}
	
	private void drawCoordinateSystem(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		Point origin = getOrigin();
		g.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(LINE_WIDTH));
		g.drawLine(0, origin.y, getWidth(), origin.y);
		g.drawLine(origin.x, 0, origin.x, getHeight());
		
		write("0", origin.x-8, origin.y+3, HAlign.LEFT, VAlign.BOTTOM, g);
		
		drawMarks(g, origin);
	}
	
	private void drawMarks(Graphics g, Point origin) {
		((Graphics2D) g).setStroke(new BasicStroke(2));
		Rectangle bounds = getBounds();
		
		for (int y = markFreqY; ; y += markFreqY) {
			Point p1 = getDrawingPoint(0, y);
			Point p2 = getDrawingPoint(0, -y);
			
			boolean mustDrawLabel = y % labelFreqY == 0;
			boolean drawn = false;
			
			if (bounds.contains(p1)) {
				putHorizontalMark(p1, g);
				if (mustDrawLabel)
					write(String.valueOf(y), p1.x-8, p1.y, HAlign.LEFT, VAlign.MIDDLE, g);
				drawn = true;
			}
			if (bounds.contains(p2)) {
				putHorizontalMark(p2, g);
				if (mustDrawLabel)
					write(String.valueOf(-y), p2.x-8, p2.y, HAlign.LEFT, VAlign.MIDDLE, g);
				drawn = true;
			}
			if (!drawn)
				break;
		}
				
		for (int x = markFreqX; ; x += markFreqX) {
			Point p1 = getDrawingPoint(x, 0);
			Point p2 = getDrawingPoint(-x, 0);
			
			boolean mustDrawLabel = x % labelFreqX == 0;
			boolean drawn = false;
			
			if (bounds.contains(p1)) {
				putVerticalMark(p1, g);
				if (mustDrawLabel)
					write(String.valueOf(x), p1.x, p1.y+3, HAlign.CENTER, VAlign.BOTTOM, g);
				drawn = true;
			}
			if (bounds.contains(p2)) {
				putVerticalMark(p2, g);
				if (mustDrawLabel)
					write(String.valueOf(-x), p2.x, p2.y+3, HAlign.CENTER, VAlign.BOTTOM, g);
				drawn = true;
			}
			if (!drawn)
				break;
		}
	}
	
	private void putHorizontalMark(Point p, Graphics g) {
		g.drawLine(p.x - MARK_SIZE, p.y, p.x + MARK_SIZE, p.y);
	}
	
	private void putVerticalMark(Point p, Graphics g) {
		g.drawLine(p.x, p.y - MARK_SIZE, p.x, p.y + MARK_SIZE);
	}
	
	private Point getOrigin() {
		return getDrawingPoint(0, 0);
	}
	
	private Point getDrawingPoint(Point2D p) {
		return getDrawingPoint(p.getX(), p.getY());
	}
	
	private Point getDrawingPoint(double graphX, double graphY) {
		if (Double.isNaN(graphX) || Double.isNaN(graphY))
			return null;
		
		int x = (int) Math.round(this.getWidth() * originX01);
		int y = (int) Math.round(this.getHeight() * originY01);
		
		x += graphX * unitWidth;
		y += graphY * unitHeight;
		
		return new Point(x, y);
	}
	
	private void write(String text, int x, int y, HAlign hAlign, VAlign vAlign,
			Graphics g) {
		Font f = g.getFont();
		Rectangle2D rect = f.getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
		
		int textWidth = (int) rect.getWidth();
		int textHeight = (int) rect.getHeight();
		
		switch (hAlign) {
		case LEFT:   x -= textWidth; break;
		case CENTER: x -= textWidth / 2; break;
		default: break;
		}
		
		switch (vAlign) {
		case MIDDLE: y += textHeight / 2; break;
		case BOTTOM: y += textHeight; break;
		default: break;
		}
		
		g.drawString(text, x, y);
	}
	
	public static enum HAlign {
		LEFT,
		CENTER,
		RIGHT;
	}
	
	public static enum VAlign {
		TOP,
		MIDDLE,
		BOTTOM;
	}
}
