/**
 * 
 */
package com.lzq.lianliankan2_3_3.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

/**
 * @author Administrator
 * 
 */
public class LinkInfo {
	private List<Point> points = new ArrayList<Point>();

	public LinkInfo(Point a, Point b) {
		points.add(a);
		points.add(b);
	}

	public LinkInfo(Point a, Point b, Point c) {
		points.add(a);
		points.add(b);
		;
		points.add(c);
	}

	public LinkInfo(Point a, Point b, Point c, Point d) {
		points.add(a);
		points.add(b);
		points.add(c);
		points.add(d);
	}

	public List<Point> getPoints() {
		return points;
	}
}
