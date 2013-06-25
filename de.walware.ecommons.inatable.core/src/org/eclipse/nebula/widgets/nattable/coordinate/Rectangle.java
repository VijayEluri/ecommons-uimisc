/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.nattable.coordinate;

import static org.eclipse.nebula.widgets.nattable.coordinate.Orientation.HORIZONTAL;


/**
 * Instances of this class represent rectangular areas in an
 * (x, y) coordinate system. The top left corner of the rectangle
 * is specified by its x and y values, and the extent of the
 * rectangle is specified by its width and height.
 * <p>
 * The coordinate space for rectangles and points is considered
 * to have increasing values downward and to the right from its
 * origin making this the normal, computer graphics oriented notion
 * of (x, y) coordinates rather than the strict mathematical one.
 * </p>
 * <p>
 * The hashCode() method in this class uses the values of the public
 * fields to compute the hash value. When storing instances of the
 * class in hashed collections, do not modify these fields after the
 * object has been inserted.  
 * </p>
 *
 * @see Point
 */

public final class Rectangle {
	
	/**
	 * the x coordinate of the rectangle
	 */
	public long x;
	
	/**
	 * the y coordinate of the rectangle
	 */
	public long y;
	
	/**
	 * the width of the rectangle
	 */
	public long width;
	
	/**
	 * the height of the rectangle
	 */
	public long height;
	
	
	/**
	 * Construct a new instance of this class given the 
	 * x, y, width and height values.
	 *
	 * @param x the x coordinate of the origin of the rectangle
	 * @param y the y coordinate of the origin of the rectangle
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 */
	public Rectangle (final long x, final long y, final long width, final long height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Destructively replaces the x, y, width and height values
	 * in the receiver with ones which represent the union of the
	 * rectangles specified by the receiver and the given rectangle.
	 * <p>
	 * The union of two rectangles is the smallest single rectangle
	 * that completely covers both of the areas covered by the two
	 * given rectangles.
	 * </p>
	 *
	 * @param rect the rectangle to merge with the receiver
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the argument is null</li>
	 * </ul>
	 */
	public void add (final Rectangle rect) {
		if (rect == null) {
			throw new NullPointerException();
		}
		final long left = this.x < rect.x ? this.x : rect.x;
		final long top = this.y < rect.y ? this.y : rect.y;
		long lhs = this.x + this.width;
		long rhs = rect.x + rect.width;
		final long right = lhs > rhs ? lhs : rhs;
		lhs = this.y + this.height;
		rhs = rect.y + rect.height;
		final long bottom = lhs > rhs ? lhs : rhs;
		this.x = left;  this.y = top;  this.width = right - left;  this.height = bottom - top;
	}
	
	/**
	 * Returns <code>true</code> if the point specified by the
	 * arguments is inside the area specified by the receiver,
	 * and <code>false</code> otherwise.
	 *
	 * @param x the x coordinate of the point to test for containment
	 * @param y the y coordinate of the point to test for containment
	 * @return <code>true</code> if the rectangle contains the point and <code>false</code> otherwise
	 */
	public boolean contains (final long x, final long y) {
		return (x >= this.x) && (y >= this.y) && x < (this.x + this.width) && y < (this.y + this.height);
	}
	
	/**
	 * Returns <code>true</code> if the given point is inside the
	 * area specified by the receiver, and <code>false</code>
	 * otherwise.
	 *
	 * @param pt the point to test for containment
	 * @return <code>true</code> if the rectangle contains the point and <code>false</code> otherwise
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the argument is null</li>
	 * </ul>
	 */
	public boolean contains (final Point pt) {
		if (pt == null) {
			throw new NullPointerException();
		}
		return contains(pt.x, pt.y);
	}
	
	/**
	 * Compares the argument to the receiver, and returns true
	 * if they represent the <em>same</em> object using a class
	 * specific comparison.
	 *
	 * @param object the object to compare with this object
	 * @return <code>true</code> if the object is the same as this object and <code>false</code> otherwise
	 *
	 * @see #hashCode()
	 */
	@Override
	public boolean equals (final Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof Rectangle)) {
			return false;
		}
		final Rectangle r = (Rectangle)object;
		return (r.x == this.x) && (r.y == this.y) && (r.width == this.width) && (r.height == this.height);
	}
	
	/**
	 * Returns an integer hash code for the receiver. Any two 
	 * objects that return <code>true</code> when passed to 
	 * <code>equals</code> must return the same value for this
	 * method.
	 *
	 * @return the receiver's hash
	 *
	 * @see #equals(Object)
	 */
	@Override
	public int hashCode () {
		int h = (int) (this.x ^ (this.x >>> 32));
		h ^= Integer.rotateLeft((int) (this.width ^ (this.width >>> 32)), 3);
		h = Integer.rotateRight(h, 15);
		h ^= (int) (this.y ^ (this.y >>> 32));
		h ^= Integer.rotateLeft((int) (this.height ^ (this.height >>> 32)), 3);
		return h ^ (h >>> 7);
	}
	
	/**
	 * Destructively replaces the x, y, width and height values
	 * in the receiver with ones which represent the intersection of the
	 * rectangles specified by the receiver and the given rectangle.
	 *
	 * @param rect the rectangle to intersect with the receiver
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the argument is null</li>
	 * </ul>
	 * 
	 * since 3.0
	 */
	public void intersect (final Rectangle rect) {
		if (rect == null) {
			throw new NullPointerException();
		}
		if (this == rect) {
			return;
		}
		final long left = this.x > rect.x ? this.x : rect.x;
		final long top = this.y > rect.y ? this.y : rect.y;
		long lhs = this.x + this.width;
		long rhs = rect.x + rect.width;
		final long right = lhs < rhs ? lhs : rhs;
		lhs = this.y + this.height;
		rhs = rect.y + rect.height;
		final long bottom = lhs < rhs ? lhs : rhs;
		this.x = right < left ? 0 : left;
		this.y = bottom < top ? 0 : top;
		this.width = right < left ? 0 : right - left;
		this.height = bottom < top ? 0 : bottom - top;
	}
	
	/**
	 * Returns a new rectangle which represents the intersection
	 * of the receiver and the given rectangle. 
	 * <p>
	 * The intersection of two rectangles is the rectangle that
	 * covers the area which is contained within both rectangles.
	 * </p>
	 *
	 * @param rect the rectangle to intersect with the receiver
	 * @return the intersection of the receiver and the argument
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the argument is null</li>
	 * </ul>
	 */
	public Rectangle intersection (final Rectangle rect) {
		if (rect == null) {
			throw new NullPointerException();
		}
		if (this == rect) {
			return new Rectangle (this.x, this.y, this.width, this.height);
		}
		final long left = this.x > rect.x ? this.x : rect.x;
		final long top = this.y > rect.y ? this.y : rect.y;
		long lhs = this.x + this.width;
		long rhs = rect.x + rect.width;
		final long right = lhs < rhs ? lhs : rhs;
		lhs = this.y + this.height;
		rhs = rect.y + rect.height;
		final long bottom = lhs < rhs ? lhs : rhs;
		return new Rectangle (
			right < left ? 0 : left,
			bottom < top ? 0 : top,
			right < left ? 0 : right - left,
			bottom < top ? 0 : bottom - top);
	}
	
	/**
	 * Returns <code>true</code> if the rectangle described by the
	 * arguments intersects with the receiver and <code>false</code>
	 * otherwise.
	 * <p>
	 * Two rectangles intersect if the area of the rectangle
	 * representing their intersection is not empty.
	 * </p>
	 *
	 * @param x the x coordinate of the origin of the rectangle
	 * @param y the y coordinate of the origin of the rectangle
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @return <code>true</code> if the rectangle intersects with the receiver, and <code>false</code> otherwise
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the argument is null</li>
	 * </ul>
	 *
	 * @see #intersection(Rectangle)
	 * @see #isEmpty()
	 * 
	 * @since 3.0
	 */
	public boolean intersects (final long x, final long y, final long width, final long height) {
		return (x < this.x + this.width) && (y < this.y + this.height) &&
			(x + width > this.x) && (y + height > this.y);
	}
	
	/**
	 * Returns <code>true</code> if the given rectangle intersects
	 * with the receiver and <code>false</code> otherwise.
	 * <p>
	 * Two rectangles intersect if the area of the rectangle
	 * representing their intersection is not empty.
	 * </p>
	 *
	 * @param rect the rectangle to test for intersection
	 * @return <code>true</code> if the rectangle intersects with the receiver, and <code>false</code> otherwise
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the argument is null</li>
	 * </ul>
	 *
	 * @see #intersection(Rectangle)
	 * @see #isEmpty()
	 */
	public boolean intersects (final Rectangle rect) {
		if (rect == null) {
			throw new NullPointerException();
		}
		return rect == this || intersects (rect.x, rect.y, rect.width, rect.height);
	}
	
	public Range getRange(final Orientation orientation) {
		return (orientation == HORIZONTAL) ?
				new Range(this.x, this.x + this.width) :
				new Range(this.y, this.y + this.height);
	}
	
	public Rectangle switchOrientation() {
		return new Rectangle(this.y, this.y, this.height, this.width);
	}
	
	
	/**
	 * Returns <code>true</code> if the receiver does not cover any
	 * area in the (x, y) coordinate plane, and <code>false</code> if
	 * the receiver does cover some area in the plane.
	 * <p>
	 * A rectangle is considered to <em>cover area</em> in the 
	 * (x, y) coordinate plane if both its width and height are 
	 * non-zero.
	 * </p>
	 *
	 * @return <code>true</code> if the receiver is empty, and <code>false</code> otherwise
	 */
	public boolean isEmpty () {
		return (this.width <= 0) || (this.height <= 0);
	}
	
	/**
	 * Returns a string containing a concise, human-readable
	 * description of the receiver.
	 *
	 * @return a string representation of the rectangle
	 */
	@Override
	public String toString () {
		return "Rectangle {" + this.x + ", " + this.y + ", " + this.width + ", " + this.height + "}"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	}
	
	/**
	 * Returns a new rectangle which represents the union of
	 * the receiver and the given rectangle.
	 * <p>
	 * The union of two rectangles is the smallest single rectangle
	 * that completely covers both of the areas covered by the two
	 * given rectangles.
	 * </p>
	 *
	 * @param rect the rectangle to perform union with
	 * @return the union of the receiver and the argument
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the argument is null</li>
	 * </ul>
	 *
	 * @see #add(Rectangle)
	 */
	public Rectangle union (final Rectangle rect) {
		if (rect == null) {
			throw new NullPointerException();
		}
		final long left = this.x < rect.x ? this.x : rect.x;
		final long top = this.y < rect.y ? this.y : rect.y;
		long lhs = this.x + this.width;
		long rhs = rect.x + rect.width;
		final long right = lhs > rhs ? lhs : rhs;
		lhs = this.y + this.height;
		rhs = rect.y + rect.height;
		final long bottom = lhs > rhs ? lhs : rhs;
		return new Rectangle (left, top, right - left, bottom - top);
	}
	
}
