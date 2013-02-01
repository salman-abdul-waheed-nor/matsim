/* *********************************************************************** *
 * project: org.matsim.*
 * CGAL.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package playground.gregor.sim2d_v4.cgal;

/**
 * This class provides basic computational geometry algorithms
 * @author laemmel
 *
 */
public abstract class CGAL {

	private static final float EPSILON = 0.0001f;


	/**
	 * tests whether coordinate x0,y0 is located left of the infinite vector that runs from x1,y1  to x2,y2
	 * @param x0 the x-coordinate to test
	 * @param y0 the y-coordinate to test
	 * @param x1 first x-coordinate of the vector
	 * @param y1 first y-coordinate of the vector
	 * @param x2 second x-coordinate of the vector
	 * @param y2 second y-coordinate of the vector
	 * @return >0 if coordinate is left of the vector
	 * 		  ==0 if coordinate is on the vector
	 * 		   <0 if coordinate is right of the vector
	 */
	public static float isLeftOfLine(float x0, float y0, float x1, float y1, float x2, float y2) {
		return (x2 - x1)*(y0 - y1) - (x0 - x1) * (y2 - y1);
	}


	/**
	 * tests whether coordinate x0,y0 is located on the infinite vector defined by coordinates x1,y1 and x2,y2
	 * @param x0 the x-coordinate to test
	 * @param y0 the y-coordinate to test
	 * @param x1 first x-coordinate of the vector
	 * @param y1 first y-coordinate of the vector
	 * @param x2 second x-coordinate of the vector
	 * @param y2 second y-coordinate of the vector
	 * @return true if coordinate is on the vector
	 */
	public static boolean isOnVector(float x0, float y0, float x1, float y1, float x2, float y2) {
		float left = isLeftOfLine(x0,y0,x1,y1,x2,y2);
		return left*left < EPSILON;
	}

	/**
	 * calculates the signed distance of a point to a line (given by a vector)
	 * a negative value indicates that the point is on the left side of the defining vector 
	 * and a positive value indicates that the point is on right side of the defining vector 
	 * 
	 * @param px point's x-coordinate
	 * @param py point's y-coordinate
	 * @param lx0 x-coordinate of the vector's origin
	 * @param ly0 y-coordinate of the vector's origin
	 * @param dxl normalized vector's x-direction
	 * @param dyl normalized vector's y-direction
	 * @return signed distance
	 */
	public static float signDistPointLine(float px, float py, float lx0, float ly0, float dxl, float dyl) {
		//		float r = (px - lx0) * dxl + (py - ly0) * dyl;
		float s = ((ly0 - py) * dxl - (lx0 - px) * dyl);

		return s;
	}

	/**
	 * calculates the coefficient r by which a given vector has to be multiplied to get the perpendicular projection on the line 
	 * defined by the vector for a given point
	 * @param x x-coordinate of the point
	 * @param y y-coordinate of the point
	 * @param v0x first x-coordinate of the vector
	 * @param v0y first y-coordinate of the vector
	 * @param v1x second x-coordinate of the vector
	 * @param v1y second y-coordinate of the vector
	 * @return the coefficient by which the vector has to be multiplied
	 */
	public static float vectorCoefOfPerpendicularProjection(float x, float y, float v0x, float v0y, float v1x, float v1y) {
		float vdx = v1x - v0x;
		float vdy = v1y - v0y;
		float numerator = (x-v0x) * vdx + (y - v0y) * vdy;
		float denomenator  = vdx * vdx + vdy * vdy; //TODO in most sim2d cases this value can be precalculated [gl Jan'13] 
		float r = numerator/denomenator;

		return r;

	}

	/**
	 * calculates the coefficient r by which a given normalized vector has to be multiplied to get the perpendicular projection on the line 
	 * defined by the vector for a given point
	 * @param x x-coordinate of the point
	 * @param y y-coordinate of the point
	 * @param v0x first x-coordinate of the vector
	 * @param v0y first y-coordinate of the vector
	 * @param vdx x-direction of the vector
	 * @param vdy y-direction of the vector
	 * @return the coefficient by which the vector has to be multiplied
	 */
	public static float normVectorCoefOfPerpendicularProjection(float x, float y, float v0x, float v0y, float vdx, float vdy) {
		float numerator = (x-v0x) * vdx + (y - v0y) * vdy;
		float denomenator  = vdx * vdx + vdy * vdy; //TODO in most sim2d cases this value can be precalculated [gl Jan'13] 
		float r = numerator/denomenator;

		return r;

	}

	/**
	 * dot product of two 2d vectors
	 * @param x0 the x-coordinate of the first vector
	 * @param y0 the y-coordinate of the first vector
	 * @param x1 the x-coordinate of the second vector
	 * @param y1 the y-coordinate of the second vector
	 * @return the dot product
	 */
	public static float dot(float x0, float y0, float x1, float y1) {
		return x0 * x1 + y0  * y1;
	}

	/**
	 * determinate of 2x2 square matrix    
	 * @param x0
	 * @param x1
	 * @param y0
	 * @param y1
	 * @return determinante
	 */
	public static float det(float x0, float x1,
			float y0, float y1) {

		return x0*y1 - y0 * x1;
	}

}
