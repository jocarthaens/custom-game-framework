package maths;

import utils.Copyable;

public class AABB implements Copyable<AABB> {
	private float lowerX = 0;
	private float lowerY = 0;
	private float upperX = 0;
	private float upperY = 0;
	
	public AABB(float x, float y, float width, float height) {
		this.lowerX = x;
		this.lowerY = y;
		this.upperX = x + width;
		this.upperY = y + height;
	}
	
	public AABB(double centerX, double centerY, float width, float height) {
		width /= 2;
		height /= 2;
		this.upperX = (float) (centerX + width);
		this.upperY = (float) (centerX + height);
		this.lowerX = (float) (centerX - width);
		this.lowerY = (float) (centerX - height);
	}
	
	public AABB(float width, float height) {
		this(0, 0, width, height);
	}
	
	public AABB(float size) {
		this(size, size);
	}
	
	
	public AABB set(float x, float y, float width, float height) {
		this.lowerX = x;
		this.lowerY = y;
		this.upperX = x + width;
		this.upperY = y + height;
		return this;}
	
	public AABB setWithCenter(float cx, float cy, float width, float height) {
		width /= 2;
		height /= 2;
		this.upperX = cx + width;
		this.upperY = cy + height;
		this.lowerX = cx - width;
		this.lowerY = cy - height;
		return this;}
	
	public AABB resize(float width, float height) {
		this.upperX = lowerX + width;
		this.upperY = lowerY + height;
		return this;}
	
	public AABB reposition(float newLowerX, float newLowerY) {
		this.upperX = upperX - lowerX + newLowerX;
		this.upperY = upperY - lowerY + newLowerY;
		this.lowerX = newLowerX;
		this.lowerY = newLowerY;
		return this;}
	
	public AABB repositionWithCenter(float newCenterX, float newCenterY) {
		float halfWidth = (upperX - lowerX) / 2;
		float halfHeight = (upperY - lowerY) / 2;
		this.upperX = newCenterX + halfWidth;
		this.upperY = newCenterY + halfHeight;
		this.lowerX = newCenterX - halfWidth;
		this.lowerY = newCenterY - halfHeight;
		return this;}
	
	public AABB translate(float nudgeX, float nudgeY) {
		this.upperX += nudgeX;
		this.upperY += nudgeY;
		this.lowerX += nudgeX;
		this.lowerY += nudgeY;
		return this;}
	
	public AABB setX(float x) {
		this.upperX = upperX - lowerX + x;
		this.lowerX = x;
		return this;}
	
	public AABB setY(float y) {
		this.upperY = upperY - lowerY + y;
		this.lowerY = y;
		return this;}
	
	public AABB setCenterX(float centerX) {
		float halfWidth = (upperX - lowerX) / 2;
		this.upperX = centerX + halfWidth;
		this.lowerX = centerX - halfWidth;
		return this;}
	
	public AABB setCenterY(float centerY) {
		float halfHeight = (upperY - lowerY) / 2;
		this.upperY = centerY + halfHeight;
		this.lowerY = centerY - halfHeight;
		return this;}
	
	public AABB setWidth(float width) {
		this.upperX = lowerX + width;
		return this;}
	
	public AABB setHeight(float height) {
		this.upperY = lowerY + height;
		return this;}
	
	
	
	
	
	
	
	
	
	
	public AABB merge(AABB other) {
		float minX = Math.min(this.minX(), other.minX());
		float minY = Math.min(this.minY(), other.minY());
		float width = Math.max(this.maxX() - minX, other.maxX() - minX);
		float height = Math.max(this.maxY() - minY, other.maxY() - minY());
		
		this.set(minX, minY, width, height);
		
		return this;
	}
	
	public AABB intersect(AABB other) {
		float iMinX = Math.max(this.minX(), other.minX());
		float iMinY = Math.max(this.minY(), other.minY());
		
		float iMaxX = Math.min(this.maxX(), other.maxX());
		float iMaxY = Math.min(this.maxY(), other.maxY());
		
		if (iMaxX < iMinX || iMaxY < iMinY) {
			this.set(0, 0, 0, 0);
			return this;
		}
		
		this.set(iMinX, iMinY, iMaxX - iMinX, iMaxY - iMinY);
		return this;
	}
	
	
	
	
	
	
	
	
	

	
	public float minX() {
		return lowerX;}
	
	public float minY() {
		return lowerY;}
	
	public float maxX() {
		return upperX;}
	
	public float maxY() {
		return upperY;}
	
	public float cenX() {
		return lowerX + width()/2;}
	
	public float cenY() {
		return lowerY + height()/2;}
	
	public float width() {
		return upperX - lowerX;}

	public float height() {
		return upperY - lowerY;}
	
	
	
	
	public int intMinX() {
		return (int) lowerX;}
	
	public int intMinY() {
		return (int) lowerY;}
	
	public int intMaxX() {
		return (int) upperX;}
	
	public int intMaxY() {
		return (int) upperY;}
	
	public int intCenX() {
		return (int) (lowerX + width()/2);}
	
	public int intCenY() {
		return (int) (lowerY + height()/2);}
	
	public int intWidth() {
		return (int) (upperX - lowerX);}

	public int intHeight() {
		return (int) (upperY - lowerY);}

	
	
	
	
	public boolean intersectsWith(AABB other) {
		boolean intersects = other.lowerX < this.lowerX + this.width()	
				&& other.lowerX + other.width() > this.lowerX							
				&& other.lowerY < this.lowerY + this.height()
				&& other.lowerY + other.height() >this.lowerY;	
		return intersects;
	}
	
	
	
	
	
	
	
	@Override
	public String toString() {
		return "BoundingBox {" + "lowerX: " + this.lowerX + "," 
						+ " lowerY: " + this.lowerY + " |" 
						+ " upperX: " + this.upperX + "," 
						+ " upperY: " + this.upperY + "}";
	}

	@Override
	public AABB copy() {
		return new AABB(minX(), minY(), width(), height());
	}
}
