package collision;

import utils.Copyable;

// Used to determine the bounding radius of collider shapes from its centroid.

public class BoundingCircle implements Copyable<BoundingCircle> {
	private float cx, cy = 0;
	private float radius = 0;
	
	public BoundingCircle(float x, float y, float radius) {
		this.cx = x;
		this.cy = y;
		this.radius = radius;
	}
	
	public BoundingCircle set(float x, float y, float radius) {
		this.cx = x;
		this.cy = y;
		this.radius = radius;
		return this;}
	
	public BoundingCircle reposition(float x, float y) {
		this.cx = x;
		this.cy = y;
		return this;}
	
	public BoundingCircle setX(float x) {
		this.cx = x;
		return this;}
	
	public BoundingCircle setY(float y) {
		this.cy = y;
		return this;}
	
	public BoundingCircle setRadius(float radius) {
		this.radius = radius;
		return this;}
	
	
	
	
	
	
	
	
	/*
	
	public BoundingCircle merge(BoundingCircle other) {
		float newX = this.cx + other.cx;
		float newY = this.cy + other.cy;
		return this;
	}
	
	public BoundingCircle intersect(BoundingCircle other) {
		
		return this;
	}
	
	*/
	
	
	
	
	
	public float cenX() {
		return cx;}
	
	public float cenY() {
		return cy;}
	
	public float radius() {
		return radius;}
	
	
	
	
	
	
	
	
	public boolean intersectsWith(BoundingCircle other) {
		float dx = Math.abs(other.cx - this.cx);
		float dy = Math.abs(other.cy - this.cy);
		float dist = other.radius + this.radius;
		boolean intersects = (dx * dx + dy * dy) < dist * dist; // check if correct
		return intersects;
	}
	
	
	
	
	

	
	
	@Override
	public String toString() {
		return "BoundingCircle {" +  "Center: (" + this.cx + " , " + this.cy + ")" 
				+ " | Radius: " + this.radius + "}";
	}

	@Override
	public BoundingCircle copy() {
		return new BoundingCircle(cx, cy, radius);
	}
}
