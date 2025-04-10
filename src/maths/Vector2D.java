package maths;

import utils.Copyable;

// Stores x and y position; Has various vector-related functions to aid in manipulating itself. 
// Can also be used for storing polar coordinates to lessen the use of expensive trigonometric functions.

public class Vector2D implements Copyable<Vector2D> {
	protected static final float FLOAT_EPSILON = 1e-6f;
	protected static final double PI = 3.141592653589793238462643383279502884197;
	protected static final double HALF_PI = PI * 0.5;
	protected static final double DEG_TO_RAD = PI / 180;
	protected static final double RAD_TO_DEG = 180 / PI;
	public float x;
	public float y;
	
	
	public Vector2D() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2D(Vector2D v) {
		this.x = v.x;
		this.y = v.y;
	}
	
	public Vector2D(double angle, boolean isDegrees) {
		if (isDegrees) angle *= DEG_TO_RAD;
		this.x = (float) Math.cos(angle);
		this.y = (float) Math.sin(angle);
	}

	
	
	
	
	
	
	public Vector2D set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;}
	
	public Vector2D set(Vector2D v) {
		this.x = v.x;
		this.y = v.y;
		return this;}
	
	public Vector2D set(double angle, boolean isDegrees) {
		if (isDegrees) angle *= DEG_TO_RAD;
		this.x = (float) Math.cos(angle);
		this.y = (float) Math.sin(angle);
		return this;}

	
	
	
	
	public int intX() {
		return (int) x;}
	
	public int intY() {
		return (int) y;}

	
	
	
	
	
	public Vector2D add(Vector2D v) {
		x += v.x;
		y += v.y;
		return this;}
	
	public Vector2D add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;}
	
	
	
	
	
	
	public Vector2D sub(Vector2D v) {
		x -= v.x;
		y -= v.y;
		return this;}

	public Vector2D sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;}
	
	
	
	
	
	
	public Vector2D setLength(float length) {
		if ( Math.abs(length) <= FLOAT_EPSILON ) {
			x = 0;
			y = 0;
			return this;
		}
		if ( Math.abs(length * length - len2()) <= FLOAT_EPSILON ) {
			return this;
		}
		double mag = length / len();
		x *= mag;
		y *= mag;
		return this;
	}
	
	public Vector2D mult(float s) {
		x *= s;
		y *= s;
		return this;}
	
	public Vector2D negate() {
		x *= -1;
		y *= -1;
		return this;}
	
	public Vector2D norm() {
		if (!isUnit()) {
			double len = len(); ////
			double div = len >= 1e10-6 ? 1 / len() : 1; ///
			x *= div;
			y *= div;
		}
		return this;}
	
	public boolean isUnit() { 
		return Math.abs(len2() - 1) <= FLOAT_EPSILON;}
	
	
	
	
	
	
	
	public Vector2D setZero() {
		x = 0;
		y = 0;
		return this;}
	
	public boolean isZero() {
		return Math.abs(len2()) <= FLOAT_EPSILON;}
	
	
	
	
	
	
	
	
	public float len() {
		return (float) Math.sqrt(x * x + y * y);}
	
	public float len2() {
		return (x * x + y * y);}
	
	
	
	
	
	
	
	public float dist(float vx, float vy) {
		float d_x = vx - x;
		float d_y = vy - y;
		return (float) Math.sqrt(d_x * d_x + d_y * d_y);}
	
	public float dist(Vector2D v) {
		float d_x = v.x - x;
		float d_y = v.y - y;
		return (float) Math.sqrt(d_x * d_x + d_y * d_y);}
	
	
	
	public float dist2(float vx, float vy) {
		float d_x = vx - x;
		float d_y = vy - y;
		return (d_x * d_x + d_y * d_y);}
	
	public float dist2(Vector2D v) {
		float d_x = v.x - x;
		float d_y = v.y - y;
		return (d_x * d_x + d_y * d_y);}

	
	
	
	
	
	
	
	public float dot(float vx, float vy) {
		return this.x * vx + this.y * vy;}
	
	public float dot(Vector2D v) {
		return this.x * v.x + this.y * v.y;}

	
	public float cross(float vx, float vy) {
		return this.x * vy - this.y * vx;}
	
	public float cross(Vector2D v) {
		return this.x * v.y - this.y * v.x;}

	
	
	
	
	
	
	
	// all orientation functions starts here:

	
	// 2 types of rotations: rotation around origin and around reference point
	// main rotation method
	protected Vector2D rotate(double cos, double sin, double x, double y) {
		double tx = (this.x - x);
		double ty = (this.y - y);
		
		this.x = (float) (tx * cos - ty * sin + x);
		this.y = (float) (tx * sin + ty * cos + y);
		
		return this;
	}
	
	
	
	public float cos() { // improve
		if (isUnit()) {
			return x;}
		return x / len();
	}
	
	public float sin() { // improve
		if (isUnit()) {
			return y;}
		return y / len();
	}
	
	
	
	
	
	
	
	
	
	public Vector2D setAngleRad(double radians) {
		set(len(), 0);
		return rotateRad(radians);
	}
	
	public Vector2D setAngleDeg(double degrees) {
		set(len(), 0);
		return rotateDeg(degrees);
	}
	
	public Vector2D setAngleVec(Vector2D dir) {
		if (dir.isUnit()) {
			x = dir.x;
			y = dir.y;
			return this;
		}
		set(len(), 0);
		return rotateVec(dir);
	}
	
	
	
	
	
	
	public Vector2D rotateRad(double radians) { // improve
		double cos = (float) Math.cos(radians);
		double sin = (float) Math.sin(radians);
		
		return rotate(cos, sin, 0, 0);
	}
	
	public Vector2D rotateDeg(double degrees) {
		return rotateRad(degrees * DEG_TO_RAD);
	}
	
	public Vector2D rotateVec(Vector2D dir) { // improve
		if (dir.isUnit()) {
			return rotate(dir.x, dir.y, 0, 0);}
		
		return rotate(dir.cos(), dir.sin(), 0, 0);
	}
	
	
	
	
	
	
	public Vector2D rotate90(boolean isClockwise) {
		float oldX = x;
		if (isClockwise == true){
			this.x = y;
			this.y = -oldX;
			return this;
		}
		else {
			this.x = -y;
			this.y = oldX;;
			return this;
		}
	}
	
	
	
	
	
	
	public Vector2D rotateRadAround(double radians, float rx, float ry) {
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		
		return rotate(cos, sin, rx, ry);
	}
	
	public Vector2D rotateRadAround(double radians, Vector2D ref) {
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		
		return rotate(cos, sin, ref.x, ref.y);
	}
	
	
	
	public Vector2D rotateDegAround(double degrees, float rx, float ry) {
		return rotateRadAround((degrees * DEG_TO_RAD), rx, ry);
	}
	
	public Vector2D rotateDegAround(double degrees, Vector2D ref) {
		return rotateRadAround((degrees * DEG_TO_RAD), ref);
	}
	
	
	
	public Vector2D rotateVecAround(Vector2D dir, float rx, float ry) { // improve
		if (dir.isUnit()) {
			return rotate(dir.x, dir.y, rx, ry);}
		
		return rotate(dir.cos(), dir.sin(), rx, ry);
	}
	
	public Vector2D rotateVecAround(Vector2D dir, Vector2D ref) { // improve
		if (dir.isUnit()) {
			return rotate(dir.x, dir.y, ref.x, ref.y);}
		
		return rotate(dir.cos(), dir.sin(), ref.x, ref.y);
	}
	
	
	
	
	
	
	
	
	
	
	// direction functions, add epsilon for checking?
	
	public boolean isOrthogonalTo(Vector2D v) {
		return Math.abs(this.dot(v)) <= FLOAT_EPSILON;
	}
	
	public boolean isOrthogonalTo(float vx, float vy) {
		return Math.abs(this.dot(vx, vy)) <= FLOAT_EPSILON;
	}
	
	
	
	public boolean isCollinearTo(Vector2D v) { ////
		return Math.abs(this.cross(v)) <= FLOAT_EPSILON;
	}
	
	public boolean isCollinearTo(float vx, float vy) { ////
		return Math.abs(this.cross(vx, vy)) <= FLOAT_EPSILON;
	}
	
	
	
	public boolean isOppositeDir(Vector2D v) {
		return this.dot(v) < 0.0f && this.isCollinearTo(v);
	}
	
	public boolean isOppositeDir(float vx, float vy) {
		return this.dot(vx, vy) < 0.0f && this.isCollinearTo(vx, vy);
	}
	
	
	
	public boolean isSameDir(Vector2D v) {
		return this.dot(v) > 0.0f && this.isCollinearTo(v);
	}
	
	public boolean isSameDir(float vx, float vy) {
		return this.dot(vx, vy) > 0.0f && this.isCollinearTo(vx, vy);
	}
	
	
	
	
	
	
	public double getDirectionRad() { // improve
		return atan2(sin(), cos());}
	
	public double getDirectionDeg() { // improve
		return (getDirectionRad() * RAD_TO_DEG);}
	
	public Vector2D getDirectionVec(Vector2D out) { // improve
		return out.set(this.cos(), this.sin());}
	
	
	
	
	
	
	public double angleRadBetween(Vector2D v) {
		if (this.isUnit() && v.isUnit()) {
			return atan2(this.cross(v), this.dot(v));
		}
		
		double div = 1 / (this.len() * v.len());
		double x = this.cross(v) * div;
		double y = this.dot(v) * div;
		
		return atan2(x, y);
	}
	
	public double angleDegBetween(Vector2D v) {
		return (angleRadBetween(v) * RAD_TO_DEG);
	}	
	
	
	
	

	
	public boolean isEquals(Vector2D v) {
		return isEquals(v, (float) FLOAT_EPSILON);}
	
	public boolean isEquals(float vx, float vy) {
		return isEquals(vx, vy, FLOAT_EPSILON);}
	
	
	
	public boolean isEquals(Vector2D v, double tolerance) {
		return (Math.abs(this.x - v.x) < tolerance) && (Math.abs(this.y - v.y) < tolerance);}
	
	public boolean isEquals(float vx, float vy, double tolerance) {
		return (Math.abs(this.x - vx) < tolerance) && (Math.abs(this.y - vy) < tolerance);}
	
	
	
	
	
	
	
	
	
	
	// 4-5 decimals accurate, guaranteed faster than Math.atan2()
	// Taken from: https://github.com/erincatto/box2d/blob/main/src/math_functions.c
    protected static double atan2(double y, double x) {
		boolean swap = Math.abs(x) < Math.abs(y);
		double atanInput = ( swap ? x : y ) / ( swap ? y : x );

		// Approximate atan
		double res = atan( atanInput );

		// If swapped, adjust atan output
		res = swap ? ( atanInput >= 0.0f ? HALF_PI : -HALF_PI ) - res : res;
		
		// Adjust quadrants
		//if ( x >= 0.0f && y >= 0.0f ) {} // 1st quadrant
		if ( x < 0.0f && y >= 0.0f ) { // 2nd quadrant
			res = PI + res;
		}
		else if ( x < 0.0f && y < 0.0f ) { // 3rd quadrant
			res = -PI + res;
		}
		//else if ( x >= 0.0f && y < 0.0f ) {} // 4th quadrant 
		
		//int sign = res >= 0 ? 1 : -1;
		//res = ( (int)(Math.abs(res) * DECS[6] + 0.5) ) * INV_DECS[6] * sign;

		return res;
	}

    // 4-5 decimals accurate, supposedly serves as fast alternative to Math.atan()
 	// Taken from: https://mazzo.li/posts/vectorized-atan2.html
    protected static double atan(double v) { 
 		double c1 = 0.99997726;
 		double c3 = -0.33262347;
 		double c5 = 0.19354346;
 		double c7 = -0.11643287;
 		double c9 = 0.05265332;
 		double c11 = -0.01172120;

 		double x2 = v * v;
 		return v * ( c1 + x2 * ( c3 + x2 * ( c5 + x2 * ( c7 + x2 * ( c9 + x2 * c11 ) ) ) ) );
 	}
	
	
	
    
    
    
	
	
	
	
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}

	
	@Override
	public Vector2D copy() {
		return new Vector2D(this.x, this.y);
	}
	
}
