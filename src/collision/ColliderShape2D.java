package collision;

import java.awt.Graphics2D;
import java.util.List;

import collision.ColliderShapeFactory.PolyUtils;
import collision.CollisionSystem.Collider;

public abstract class ColliderShape2D<T extends ColliderShape2D<? extends T>> {
	protected float cx = 0, cy = 0;
	protected float rotateX = 1, rotateY = 0; // right-facing
	
	protected Collider collider;
	//protected boolean isEnabled = true; 
	//protected boolean isSensor = false;
	//protected Object data;
	//add event listeners?, custom filters
	

	ColliderShape2D() {}
	
	
	
	
	
	public void setPosition(float x, float y) {
		this.cx = x;
		this.cy = y;
		
		notifyChanges();
	}
	
	public float getX() {
		return cx;
	}
	
	public float getY() {
		return cy;
	}
	
	
	
	
	
	
	public void setRotation(double radians) {
		this.rotateX = (float) Math.cos(radians);
		this.rotateY = (float) Math.sin(radians);
		
		notifyChanges();
	}
	
	public void setRotation(float cos, float sin) {
		// if value is greater than epsilon or less than 0,
		// divide cos and sin by its length before applying rotation
		if ( Math.abs( (cos * cos + sin * sin) - 1) > 1e10-6 ) { 
			float len = (float) Math.sqrt(cos * cos + sin * sin);
			len = len >= 1e10-6 ? len : 1;
			cos = cos / len;
			sin = sin / len;
		}
		this.rotateX = cos;
		this.rotateY = sin;
		
		notifyChanges();
	}
	
	public float getRotation() {
		return (float) Math.atan2(rotateY, rotateX);
	}
	
	public float getRotationDegrees() {
		return (float) Math.toDegrees( Math.atan2(rotateY, rotateX) );
	}
	
	public float getRotationCos() {
		return this.rotateX;
	}
	
	public float getRotationSin() {
		return this.rotateY;
	}
	
	
	
	
	
	
	public void translate(float x, float y) {
		this.cx += x;
		this.cy += y;
		
		notifyChanges();
	}
	
	
	
	public void rotate(double radians) {
		this.rotate( (float) Math.cos(radians), (float) Math.sin(radians) );
	}
	
	
	
	public void rotateAroundPoint(double radians, float pointX, float pointY) {
		this.rotate(radians);
		this.rotateCenterAroundPoint(Math.cos(radians), Math.sin(radians), pointX, pointY);
	}
	
	public void rotateAroundPoint(float cos, float sin, float pointX, float pointY) {
		if ( Math.abs( (cos * cos + sin * sin) - 1) > 1e10-6 ) { 
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
	}
	
	// adds rotational value to the rotational component
	public void rotate(float cos, float sin) {
		// if value is greater than epsilon or less than 0,
		// divide cos and sin by its length before applying rotation
		//float val = (cos * cos + sin * sin);
		//if ( val - 1 > 1e10-6 || val - 1 < 0) { 
		if ( Math.abs( (cos * cos + sin * sin) - 1) > 1e10-6 ) { 
			float len = (float) Math.sqrt(cos * cos + sin * sin);
			len = len >= 1e10-6 ? len : 1;
			cos = cos / len;
			sin = sin / len;
		}
		
		float tx = this.rotateX;
		float ty = this.rotateY;
		
		this.rotateX = (float) (tx * cos - ty * sin);
		this.rotateY = (float) (tx * sin + ty * cos);
		
		notifyChanges();
	}
	
	// rotates positional coordinates around a point(px, py)
	protected void rotateCenterAroundPoint(double cos, double sin, double px, double py) {
		double tx = (this.cx - px);
		double ty = (this.cy - py);
		
		this.cx = (float) (tx * cos - ty * sin + px);
		this.cy = (float) (tx * sin + ty * cos + py);
		
		notifyChanges();
	}

	
	
	
	
	
	/*
	public void setEnabled(boolean enable) {
		this.isEnabled = enable;
		
		notifyChanges();
	}
	
	public boolean isEnabled() {
		return this.isEnabled;
	}
	
	
	
	public void setSensor(boolean sensor) {
		this.isSensor = sensor;
		
		notifyChanges();
	}
	
	public boolean isSensor() {
		return this.isSensor;
	}
	
	
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public Object getData() {
		return this.data;
	}
	*/
	
	
	public ColliderUID getOwnerID() {
		return collider == null ? null : collider.assignID;
	}
	
	
	
	
	
	protected void notifyChanges() {
		if (collider != null || collider.isDeactivated() == true) {
			collider.notifyIfModified();
		}
	}
	
	
	
	
	
	
	public abstract float getArea();
	public abstract float getPerimeter();
	public abstract BoundingBox getBoundingBox(ColliderTransform2D  transform, 
			BoundingBox outBoundingBox);
	public abstract BoundingCircle getBoundingCircle(ColliderTransform2D  transform, 
			BoundingCircle outBoundingCircle);
	public abstract T copy();
	public abstract void render(ColliderTransform2D transform, Graphics2D g2);
	//add toString as well
	
	
	
	
	
	
	
	
	
	
	
	public static class Circle2D extends ColliderShape2D<Circle2D> {
		protected float radius;
		
		
		Circle2D(float x, float y, float radius) {
			cx = x;
			cy = y;
			this.setRadius(radius);
		}
		
		public void setRadius(float radius) {
			if (radius <= 0.0)
				throw new IllegalArgumentException("Radius must be greater than 0. Radius = "+radius+".");
			this.radius = radius;
			
			notifyChanges();
		}
		
		public float getRadius() {
			return radius;
		}
		
		
		
		
		@Override
		public float getArea() {
			return (float) (Math.PI * radius * radius);
		}

		@Override
		public float getPerimeter() {
			return (float) (2 * Math.PI * radius);
		}

		@Override
		public BoundingBox getBoundingBox(ColliderTransform2D transform, BoundingBox outBoundingBox) {
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			final float nx = pos.x;
			final float ny = pos.y;
			return outBoundingBox.set(nx - radius/2, ny - radius/2, radius, radius);
		}

		@Override
		public BoundingCircle getBoundingCircle(ColliderTransform2D transform, BoundingCircle outBoundingCircle) {
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			final float nx = pos.x;
			final float ny = pos.y;
			return outBoundingCircle.set(nx, ny, radius);
		}
		
		
		@Override
		public Circle2D copy() {
			return new Circle2D(cx, cy, radius);
		}

		@Override
		public void render(ColliderTransform2D transform, Graphics2D g2) {
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			g2.drawArc((int) (pos.x - radius), (int) (pos.y - radius), 
					(int) radius * 2, (int) radius * 2, 0, 360);
			
		}
		
		@Override
		public String toString() {
			return 	this.getClass().getName() +"@" + Integer.toHexString(System.identityHashCode(this)) 
					+ ": \n{" + " Relative Center: (" + this.cx + " , " + this.cy + "), \n" 
					+ "Radius: " + this.radius 
					+ " }";
		}
	
	}
	
	
	
	
	
	public static class Rect2D extends ColliderShape2D<Rect2D> {
		
		protected float width, height;
		
		Rect2D(float cx, float cy, float width, float height, double angleRadians) {
			this.setWidth(width);
			this.setHeight(height);
			this.setPosition(cx, cy);
			this.setRotation(angleRadians);
		}
		
		
		
		
		public void setWidth(float width) {
			if (width <= 0.0) 
				throw new IllegalArgumentException("Width must be greater than 0. Width = "+width+".");
			this.width = width;
			
			notifyChanges();
		}
		
		public void setHeight(float height) {
			if (height <= 0.0) 
				throw new IllegalArgumentException("Height must be greater than 0. Height = "+height+".");
			this.height = height;
			
			notifyChanges();
		}
		
		
		
		public float getWidth() {
			return width;
		}
		
		public float getHeight() {
			return height;
		}
		
		
		
		
		
		
		
		@Override
		public float getArea() {
			return width * height;
		}

		@Override
		public float getPerimeter() {
			return 2 * (width + height);
		}

		@Override
		public BoundingBox getBoundingBox(ColliderTransform2D transform, BoundingBox outBoundingBox) {
			
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			final float posX = pos.x;
			final float posY = pos.y;
			
			final float cos = rotateX * transform.getRotationCos() - rotateY * transform.getRotationSin();
			final float sin = rotateX * transform.getRotationSin() + rotateY * transform.getRotationCos();
			
			float minX = 0;
			float minY = 0;
			float maxX = 0;
			float maxY = 0;
			
			float halfWidth = width / 2;
			float halfHeight = height / 2;
			
			for (int i = 0; i < 4; i++) { // corner order: bot-right, bot-left, top-left, top-right
				float halfX = (i == 0 || i == 3) ? halfWidth: -halfWidth;
				float halfY = (i < 2) ? halfHeight: -halfHeight;
				
				float vx = (halfX * cos - halfY * sin) + posX;
				float vy = (halfX * sin + halfY * cos) + posY;
				
				minX = minX < vx ? minX : vx;
				minY = minY < vy ? minY : vy;
				maxX = maxX > vx ? maxX : vx;
				maxY = maxY > vy ? maxY : vy;
			}
			
			return outBoundingBox.set(minX, minY, Math.abs((maxX - minX)), Math.abs((maxY - minY)));
		}

		@Override
		public BoundingCircle getBoundingCircle(ColliderTransform2D transform, BoundingCircle outBoundingCircle) {
			
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			final float posX = pos.x;
			final float posY = pos.y;
			
			final float cos = rotateX * transform.getRotationCos() - rotateY * transform.getRotationSin();
			final float sin = rotateX * transform.getRotationSin() + rotateY * transform.getRotationCos();
			
			float minX = 0;
			float minY = 0;
			float maxX = 0;
			float maxY = 0;
			
			float halfWidth = width / 2;
			float halfHeight = height / 2;
			
			for (int i = 0; i < 4; i++) { // corner order: bot-right, bot-left, top-left, top-right
				float halfX = (i == 0 || i == 3) ? halfWidth: -halfWidth;
				float halfY = (i < 2) ? halfHeight: -halfHeight;
				
				float vx = (halfX * cos - halfY * sin) + posX;
				float vy = (halfX * sin + halfY * cos) + posY;
				
				minX = minX < vx ? minX : vx;
				minY = minY < vy ? minY : vy;
				maxX = maxX > vx ? maxX : vx;
				maxY = maxY > vy ? maxY : vy;
			}
			
			float radius = (float) (Math.sqrt((maxX - minX) * (maxX - minX) + (maxY - minY) * (maxY - minY)) / 2.0);
			
			return outBoundingCircle.set(posX, posY, radius);
		}
		
		@Override
		public Rect2D copy() {
			return new Rect2D(cx, cy, width, height, getRotation());
		}




		@Override
		public void render(ColliderTransform2D transform, Graphics2D g2) {
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			final float posX = pos.x;
			final float posY = pos.y;

			float halfWidth = this.width / 2;
			float halfHeight = this.height / 2;
			
			g2.rotate(rotTrans.getDirectionRad(), posX, posY);
			g2.drawRect((int) (posX - halfWidth), (int) (posX - halfHeight), (int) this.width, (int) this.height);
			g2.rotate(-rotTrans.getDirectionRad(), posX, posY);
			
		}
		
		@Override
		public String toString() {
			return 	this.getClass().getName() +"@" + Integer.toHexString(System.identityHashCode(this)) 
					+ ": \n{" + " Relative Center: (" + this.cx + " , " + this.cy + "), " 
					+ " Relative Rotation: (" + this.rotateX + " , " + this.rotateY + "), \n" 
					+ "Width: " + this.width + ", "
					+ "Height: " + this.height + ", "
					+ " }";
		}

	}
	
	
	
	
	
	
	
	// A horizontal 2D capsule by default
	public static class Capsule2D extends ColliderShape2D<Capsule2D> {
		
		protected float distance;
		protected float radius;
		
		
		Capsule2D(float cx, float cy, float radius, float distance, double tiltAngleRadians) {
			this.setDistance(distance);
			this.setRadius(radius);
			this.setPosition(cx, cy);
			this.setRotation(tiltAngleRadians);
		}
		
		
		
		
		public void setDistance(float distance) {
			if (distance <= 0.0) 
				throw new IllegalArgumentException("Distance between 2 circles must be greater than 0. Distance = "+distance+".");
			this.distance = distance;
			
			notifyChanges();
		}
		
		public void setRadius(float radius) {
			if (radius <= 0.0) 
				throw new IllegalArgumentException("Radius of capsule must be greater than 0. Radius = "+radius+".");
			this.radius = radius;
			
			notifyChanges();
		}
		
		
		
		public float getDistance() {
			return distance;
		}
		
		public float getRadius() {
			return radius;
		}
		
		
		
		@Override
		public float getArea() {
			return (float) (Math.PI * radius * radius + distance * radius);
		}


		@Override
		public float getPerimeter() {
			return (float) (2 * (Math.PI * radius + distance + radius));
		}


		@Override
		public BoundingBox getBoundingBox(ColliderTransform2D transform, BoundingBox outBoundingBox) {
			
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			final float posX = pos.x;
			final float posY = pos.y;
			
			final float cos = rotateX * transform.getRotationCos() - rotateY * transform.getRotationSin();
			final float sin = rotateX * transform.getRotationSin() + rotateY * transform.getRotationCos();
			
			float halfDist = distance / 2;
			
			float rx1 = (posX - halfDist) * cos - (posY) * sin;
			float ry1 = (posX - halfDist) * sin + (posY) * cos;
			float rx2 = (posX + halfDist) * cos - (posY) * sin;
			float ry2 = (posX + halfDist) * sin + (posY) * cos;
			
			
			float minX = Math.min(rx1, rx2) - radius;
			float minY = Math.min(ry1, ry2) - radius;
			float maxX = Math.max(rx1, rx2) + radius;
			float maxY = Math.max(rx1, rx2) + radius;
			
			
			return outBoundingBox.set(minX, minY, Math.abs((maxX - minX)), Math.abs((maxY - minY)));
		}


		@Override
		public BoundingCircle getBoundingCircle(ColliderTransform2D transform, BoundingCircle outBoundingCircle) {
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			final float posX = pos.x;
			final float posY = pos.y;
			
			float cr = (distance + 2 * radius) * 0.5f;
			return outBoundingCircle.set(posX, posY, cr);
		}
		
		
		
		
		
		@Override
		public Capsule2D copy() {
			return new Capsule2D(cx, cy, radius, distance, this.getRotation());
		}




		@Override
		public void render(ColliderTransform2D transform, Graphics2D g2) {
			
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			final float posX = pos.x;
			final float posY = pos.y;
			
			final float dist = this.distance;
			final float radius = this.radius;
			
			g2.rotate(rotTrans.getDirectionRad(), posX, posY);
			
			
			g2.drawArc((int) (posX - (dist * 0.5f + radius)), (int) (posY - radius),
					(int) radius, (int) radius, 90, 270);
			g2.drawLine((int) (posX - (dist * 0.5f)), (int) (posY - radius),
					(int) (posX + (dist * 0.5f)), (int) (posY - radius));
			g2.drawLine((int) (posX - (dist * 0.5f)), (int) (posY + radius),
					(int) (posX + (dist * 0.5f)), (int) (posY + radius));
			g2.drawArc((int) (posX + dist * 0.5f - radius), (int) (posY - radius),
					(int) radius, (int) radius, 270, 90);
			
			
			g2.rotate(rotTrans.getDirectionRad(), posX, posY);
			
		}
		
		
		@Override
		public String toString() {
			return 	this.getClass().getName() +"@" + Integer.toHexString(System.identityHashCode(this)) 
					+ ": \n{" + " Relative Center: (" + this.cx + " , " + this.cy + "), " 
					+ " Relative Rotation: (" + this.rotateX + " , " + this.rotateY + "), \n" 
					+ "Circle Distance: " + this.distance + ", "
					+ "Circle Radius: " + this.radius + ", "
					+ " }";
		}
	}
	
	
	
	
	
	public static class ConvexPolygon2D extends ColliderShape2D<ConvexPolygon2D>  {
		protected ColliderPoint2D[] bounds;
		
		ConvexPolygon2D(ColliderPoint2D[] vertices) {
			this.bounds = vertices;
		}
		
		/*
		protected void rotatePolygon(double cos, double sin, double x, double y) {
			
			for(int i = 0; i < vertices.length; i += 2) {
				
				double tx = (vertices[i].x - x);
				double ty = (vertices[i].y - y);
				
				vertices[i].x = (float) (tx * cos - ty * sin + x);
				vertices[i].y = (float) (tx * sin + ty * cos + y);
			}
		}
		*/
		
		
		
		
		public List<ColliderPoint2D> getVerticesCopy(List<ColliderPoint2D> out) {
			for (ColliderPoint2D vec: bounds) {
				out.add(vec.copy());}
			return out;
		}
		
		
		public float getArea() {
			return PolyUtils.fastGetArea(bounds);
		}
		
		
		public float getPerimeter() {
			float perimeter = 0;
			int size = bounds.length;
			for (int i = 0; i < size; i++) {
				ColliderPoint2D v1 = bounds[i];
				ColliderPoint2D v2 = bounds[(i+1) % size];
				perimeter += Math.sqrt((v2.x - v1.x) * (v2.x - v1.x) + (v2.y - v1.y) * (v2.y - v1.y));
			}
			return perimeter;
		}
		
		
		@Override
		public BoundingBox getBoundingBox(ColliderTransform2D transform, BoundingBox outBoundingBox) {
			
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			int size = bounds.length;
			float minX = 0;
			float minY = 0;
			float maxX = 0;
			float maxY = 0;
			
			// combine rotations and positions
			
			final float cos = rotateX * transform.getRotationCos() - rotateY * transform.getRotationSin();
			final float sin = rotateX * transform.getRotationSin() + rotateY * transform.getRotationCos();
			final float posX = pos.x;
			final float posY = pos.y;
			
			for (int i = 0; i < size; i++) {
				
				float tx = bounds[i].x;
				float ty = bounds[i].y;
				
				float x = (tx * cos - ty * sin) + posX;
				float y = (tx * sin + ty * cos) + posY;
				
				minX = minX < x ? minX : x;
				minY = minY < y ? minY : y;
				maxX = maxX > x ? maxX : x;
				maxY = maxY > y ? maxY : y;
			}
			
			
			return outBoundingBox.set(minX, minY, Math.abs((maxX - minX)), Math.abs((maxY - minY)));
		}

		@Override
		public BoundingCircle getBoundingCircle(ColliderTransform2D transform, BoundingCircle outBoundingCircle) {
			
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			int size = bounds.length;
			float minX = 0;
			float minY = 0;
			float maxX = 0;
			float maxY = 0;
			
			// combine rotations and positions
			
			final float cos = rotateX * transform.getRotationCos() - rotateY * transform.getRotationSin();
			final float sin = rotateX * transform.getRotationSin() + rotateY * transform.getRotationCos();
			final float posX = pos.x;
			final float posY = pos.y;
			
			for (int i = 0; i < size; i++) {
				
				float tx = bounds[i].x;
				float ty = bounds[i].y;
				
				float x = (tx * cos - ty * sin) + posX;
				float y = (tx * sin + ty * cos) + posY;
				
				minX = minX < x ? minX : x;
				minY = minY < y ? minY : y;
				maxX = maxX > x ? maxX : x;
				maxY = maxY > y ? maxY : y;
			}
			
			float radius = (float) (Math.sqrt((maxX - minX) * (maxX - minX) + (maxY - minY) * (maxY - minY)) / 2.0);
			
			return outBoundingCircle.set(posX, posY, radius);
		}

		
		public ConvexPolygon2D copy() {
			return new ConvexPolygon2D(bounds.clone());
		}

		@Override
		public void render(ColliderTransform2D transform, Graphics2D g2) {
			
			ColliderPoint2D rotTrans = new ColliderPoint2D()
					.set(transform.rotateX, transform.rotateY);
			
			ColliderPoint2D pos =  new ColliderPoint2D()
					.set(this.cx + transform.posX, this.cy + transform.posY)
					.rotateVecAround(rotTrans, transform.posX, transform.posY);
			
			int size = bounds.length;
			
			// combine rotations and positions
			final float cos = rotateX * transform.getRotationCos() - rotateY * transform.getRotationSin();
			final float sin = rotateX * transform.getRotationSin() + rotateY * transform.getRotationCos();
			final float posX = pos.x;
			final float posY = pos.y;
			
			for (int i = 0; i < size; i++) {
				
				int i2 = (i + 1) < size ? i + 1 : 0;
				
				float tx1 = bounds[i].x;
				float ty1 = bounds[i].y;
				
				float tx2 = bounds[i2].x;
				float ty2 = bounds[i2].y;
				
				float x1 = (tx1 * cos - ty1 * sin) + posX;
				float y1 = (tx1 * sin + ty1 * cos) + posY;
				
				float x2 = (tx2 * cos - ty2 * sin) + posX;
				float y2 = (tx2 * sin + ty2 * cos) + posY;
				
				g2.drawLine( (int) x1, (int) y1, (int) x2, (int) y2);
			}
		}
		
		@Override
		public String toString() {
			StringBuilder string = new StringBuilder();
			for (int i = 0; i < bounds.length; i++) {
				if (i == 0) string.append("[ ");
				string.append(bounds[i].toString());
				if (i < bounds.length - 1) string.append(", ");
			}
			string.append(" ]");
			
			return 	this.getClass().getName() +"@" + Integer.toHexString(System.identityHashCode(this)) 
					+ ": \n{" + " Relative Center: (" + this.cx + " , " + this.cy + "), " 
					+ " Relative Rotation: (" + this.rotateX + " , " + this.rotateY + "), \n" 
					+ string.toString()
					+ " }";
		}
		
	}
	
}
