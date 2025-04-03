package maths;


public class Transform2D {
	protected static final float FLOAT_EPSILON = 1e-6f;
	protected static final double PI = 3.141592653589793238462643383279502884197;
	protected static final double DEG_TO_RAD = PI / 180;
	protected static final double RAD_TO_DEG = 180 / PI;
	float posX = 0, posY = 0;
	float rotateX = 1, rotateY = 0;  // right-facing
	
	
	

	public Transform2D setPosition(float x, float y) {
		this.posX = x;
		this.posY = y;
		return this;
	}
	
	public float getX() {
		return posX;
	}
	
	public float getY() {
		return posY;
	}
	
	
	
	
	public Transform2D setRotation(double radians) {
		this.rotateX = (float) Math.cos(radians);
		this.rotateY = (float) Math.sin(radians);
		return this;
	}
	
	public Transform2D setRotation(float cos, float sin) {
		// if value is greater than epsilon or less than 0,
		// divide cos and sin by its length before applying rotation
		if ( Math.abs( (cos * cos + sin * sin) - 1) > FLOAT_EPSILON ) { 
			float len = (float) Math.sqrt(cos * cos + sin * sin);
			len = len >= 1e10-6 ? len : 1;
			cos = cos / len;
			sin = sin / len;
		}
		this.rotateX = cos;
		this.rotateY = sin;
		return this;
	}
	
	public float getRotation() {
		return (float) Math.atan2(rotateY, rotateX);
	}
	
	public float getRotationDegrees() {
		return (float) ( Math.atan2(rotateY, rotateX) * RAD_TO_DEG);
	}
	
	public float getRotationCos() {
		return this.rotateX;
	}
	
	public float getRotationSin() {
		return this.rotateY;
	}
	
	
	
	
	
	
	
	
	
	
	public Transform2D translate(float x, float y) {
		this.posX += x;
		this.posY += y;
		return this;
	}
	
	
	

	public Transform2D rotate(double radians) {
		return this.rotate( (float) Math.cos(radians), (float) Math.sin(radians) );
	}
	
	public Transform2D rotateAroundPoint(double radians, float pointX, float pointY) {
		this.rotate(radians);
		this.rotateCenterAroundPoint(Math.cos(radians), Math.sin(radians), pointX, pointY);
		
		return this;
	}
	
	public Transform2D rotateAroundPoint(float cos, float sin, float pointX, float pointY) {
		// if value is greater than epsilon or less than 0,
		// divide cos and sin by its length before applying rotation
		if ( Math.abs( (cos * cos + sin * sin) - 1) > FLOAT_EPSILON ) { 
			float len = (float) Math.sqrt(cos * cos + sin * sin);
			len = len >= 1e10-6 ? len : 1;
			cos = cos / len;
			sin = sin / len;
		}
		
		float tx = this.rotateX;
		float ty = this.rotateY;
		
		this.rotateX = (float) (tx * cos - ty * sin);
		this.rotateY = (float) (tx * sin + ty * cos);
		
		this.rotateCenterAroundPoint(cos, sin, pointX, pointY);
		return this;
	}
	
	// adds rotational value to the rotational component
	public Transform2D rotate(float cos, float sin) {
		// if value is greater than epsilon or less than 0,
		// divide cos and sin by its length before applying rotation
		//float val = (cos * cos + sin * sin);
		//if ( val - 1 > 1e10-6 || val - 1 < 0) { 
		if ( Math.abs( (cos * cos + sin * sin) - 1) > FLOAT_EPSILON ) { 
			float len = (float) Math.sqrt(cos * cos + sin * sin);
			len = len >= 1e10-6 ? len : 1;
			cos = cos / len;
			sin = sin / len;
		}
		float tx = this.rotateX;
		float ty = this.rotateY;
		
		this.rotateX = (float) (tx * cos - ty * sin);
		this.rotateY = (float) (tx * sin + ty * cos);
		return this;
	}
	
	// rotates positional coordinates around a point(px, py)
	protected void rotateCenterAroundPoint(double cos, double sin, double px, double py) {
		double tx = (this.posX - px);
		double ty = (this.posY - py);
		
		this.posX = (float) (tx * cos - ty * sin + px);
		this.posY = (float) (tx * sin + ty * cos + py);
	}
	
	
	
	
	
	public void reset() {
		this.posX = 0;
		this.posY = 0;
		this.rotateX = 1;
		this.rotateY = 0;
	}
	
	
	
	
	@Override
	public String toString() {
		return 	this.getClass().getName() +"@" + Integer.toHexString(this.hashCode()) 
				+ ": \n{" + " Center: (" + this.posX + " , " + this.posY + "), " 
				+ " Rotation: (" + this.rotateX + " , " + this.rotateY + ")" 
				+ " }";
	}

}
