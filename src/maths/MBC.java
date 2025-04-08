package maths;

import utils.Copyable;

// Minimum Bounding Circle

public class MBC implements Copyable<MBC> {
	private float cx, cy = 0;
	private float radius = 0;
	
	public MBC(float x, float y, float radius) {
		this.cx = x;
		this.cy = y;
		this.radius = radius;
	}
	
	public MBC set(float x, float y, float radius) {
		this.cx = x;
		this.cy = y;
		this.radius = radius;
		return this;}
	
	public MBC reposition(float x, float y) {
		this.cx = x;
		this.cy = y;
		return this;}
	
	public MBC setX(float x) {
		this.cx = x;
		return this;}
	
	public MBC setY(float y) {
		this.cy = y;
		return this;}
	
	public MBC setRadius(float radius) {
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
	
	
	

	
	
	@Override
	public String toString() {
		return "BoundingCircle {" +  "Center: (" + this.cx + " , " + this.cy + ")" 
				+ " | Radius: " + this.radius + "}";
	}

	@Override
	public MBC copy() {
		return new MBC(cx, cy, radius);
	}
}
