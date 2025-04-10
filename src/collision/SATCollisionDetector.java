package collision;

import java.util.ArrayList;
import java.util.Stack;

import collision.ColliderShape2D.Capsule2D;
import collision.ColliderShape2D.Circle2D;
import collision.ColliderShape2D.ConvexPolygon2D;
import collision.ColliderShape2D.Rect2D;

// Nearphase collision detection where it detects overlap between 2 collider shapes in greater detail,
// and determines the Minimum Translation Vector if an overlap do exist between 2 shapes.

public class SATCollisionDetector {
	protected Stack<ColliderPoint2D> cachedVectors; // caches vectors for reuse
	protected ArrayList<ColliderPoint2D> axes; // temporary storage of vectors for axes
	protected Interval itvA; // interval for Shape A
	protected Interval itvB; // interval for Shape B
	//protected ArrayList<ColliderShape2D<?>> shapesA; // temporary storage of sourceCollider's shapes
	//protected ArrayList<ColliderShape2D<?>> shapesB; // temporary storage of coollidingCollider's shapes
	protected ArrayList<ColliderPoint2D> vecListA; // temporary storage of vertices for sourceCollider's shapes
	protected ArrayList<ColliderPoint2D> vecListB; // temporary storage of vertices for sourceCollider's shapes
	
	public SATCollisionDetector() {
		axes = new ArrayList<>(5);
		cachedVectors = new Stack<>();
		itvA = new Interval();
		itvB = new Interval();
		//shapesA = new ArrayList<>(5);
		//shapesB = new ArrayList<>(5);
		vecListA = new ArrayList<>();
		vecListB = new ArrayList<>();
	}
	
	
	
	
	
	public boolean collide(ColliderShape2D<?> shapeA, ColliderShape2D<?> shapeB, 
			ColliderTransform2D transA, ColliderTransform2D transB, ManifoldData outManifold) {
		
		findAxes(shapeA, shapeB, transA, transB, axes);
		findAxes(shapeB, shapeA, transB, transA, axes);
		float minDepth = Float.MAX_VALUE;
		
		ColliderPoint2D depthAxis = new ColliderPoint2D(); 
		
		for (ColliderPoint2D axis : axes) {
			projectShape(shapeA, transA, axis, itvA);
			projectShape(shapeB, transB, axis, itvB);
			
			if (hasOverlap(itvA, itvB) == false) {
				//outManifold.affectedShape = shapeA;
				//outManifold.collidingShape = shapeB;
				outManifold.affectedCollider = shapeA.collider;
				outManifold.collidingCollider = shapeB.collider;
				outManifold.depthAxis = depthAxis.set(0,0);
				outManifold.penetrationDepth = -1;
				outManifold.isColliding = false;
				_flush();
				return false;
			}
			else {
				float depth = Math.min(itvA.getMax() - itvB.getMin(), itvB.getMax() - itvA.getMin());
				
				// ensures that depthAxis points from shapeA (source) to shapeB (colliding)
				int negateAxis = (itvB.getMax() - itvA.getMin() < itvB.getMax() - itvB.getMin()) ? -1: 1; 
				
				// check if a shape is inside another and make adjustments (I need to understand why)
				if (contained(itvA, itvB)) { 
					float mins = Math.abs(itvA.getMin() - itvB.getMin());
					float maxs = Math.abs(itvA.getMax() - itvB.getMax());
					
					if (mins < maxs) {
						axis.mult(-1);
						depth += mins;
					}
					else {
						depth += maxs;
					}
				}
				
				if (depth < minDepth) {
					minDepth = depth;
					depthAxis.set(axis.x, axis.y).mult(negateAxis);
				}
			}
		}
		
		//outManifold.affectedShape = shapeA;
		//outManifold.collidingShape = shapeB;
		outManifold.affectedCollider = shapeA.collider;
		outManifold.collidingCollider = shapeB.collider;
		outManifold.depthAxis = depthAxis;
		outManifold.penetrationDepth = minDepth;
		outManifold.isColliding = true;
		
		_flush();
		return true;
	}


	
	
	
	
	
	
	protected void findAxes(ColliderShape2D<?> source, ColliderShape2D<?> colliding, 
			ColliderTransform2D transSource, ColliderTransform2D transCollide, 
			ArrayList<ColliderPoint2D> outList) {
		//depends on shape
		
		Class<?> sourceClass = source.getClass();
		String srcClassName = sourceClass.getSimpleName();
		Class<?> collidingClass = colliding.getClass();
		
		ColliderPoint2D minVec = requestVector();
		
		ColliderPoint2D rotTransSrc = requestVector()
				.set(transSource.rotateX, transSource.rotateY);
		ColliderPoint2D rotTransCld = requestVector()
				.set(transCollide.rotateX, transCollide.rotateY);
		ColliderPoint2D posSrc = requestVector()
				.set(source.cx + transSource.posX, source.cy + transSource.posY)
				.rotateVecAround(rotTransSrc, transSource.posX, transSource.posY);
		ColliderPoint2D posCld = requestVector()
				.set(colliding.cx + transCollide.posX, colliding.cy + transCollide.posY)
				.rotateVecAround(rotTransCld, transCollide.posX, transCollide.posY);
		
		ColliderPoint2D shapeRotateA = requestVector();
		shapeRotateA.set(source.rotateX, source.rotateY).rotateVec(rotTransSrc);
		
		switch (srcClassName) {
		
			case "Circle2D":
			case "Rect2D":
			case "Capsule2D":
			case "ConvexPolygon2D":
				if (sourceClass == Circle2D.class) {
					vecListA.add(requestVector().set(posSrc.x, posSrc.y));
				}
				else if (sourceClass == Rect2D.class) {
					
					float srcHalfWidth = ((Rect2D) source).getWidth() * 0.5f;
					float srcHalfHeight = ((Rect2D) source).getHeight() * 0.5f;
					
					vecListA.add(requestVector().set(posSrc.x - srcHalfWidth, posSrc.y - srcHalfHeight)
							.rotateVec(shapeRotateA));
					vecListA.add(requestVector().set(posSrc.x + srcHalfWidth, posSrc.y - srcHalfHeight)
							.rotateVec(shapeRotateA));
					vecListA.add(requestVector().set(posSrc.x + srcHalfWidth, posSrc.y + srcHalfHeight)
							.rotateVec(shapeRotateA));
					vecListA.add(requestVector().set(posSrc.x - srcHalfWidth, posSrc.y + srcHalfHeight)
							.rotateVec(shapeRotateA));
				}
				else if (sourceClass == Capsule2D.class) {
					
					float distance = ((Capsule2D) colliding).getDistance();
					float radius = ((Capsule2D) colliding).getRadius();
					
					// capsule rect coordinates
					vecListA.add(requestVector().set(posSrc.x - distance * 0.5f, posSrc.y - radius)
							.rotateVec(shapeRotateA));
					vecListA.add(requestVector().set(posSrc.x + distance * 0.5f, posSrc.y - radius)
							.rotateVec(shapeRotateA));
					vecListA.add(requestVector().set(posSrc.x + distance * 0.5f, posSrc.y + radius)
							.rotateVec(shapeRotateA));
					vecListA.add(requestVector().set(posSrc.x - distance * 0.5f, posSrc.y + radius)
							.rotateVec(shapeRotateA));
					
					// capsule circles coordinates
					vecListA.add(requestVector().set(posSrc.x + distance * 0.5f, posSrc.y)
							.rotateVec(shapeRotateA));
					vecListA.add(requestVector().set(posSrc.x - distance * 0.5f, posSrc.y)
							.rotateVec(shapeRotateA));
				}
				
				else if (sourceClass == ConvexPolygon2D.class) {
					
					ColliderPoint2D[] boundsA = ((ConvexPolygon2D) colliding).bounds;
					for (int i = 0; i < boundsA.length; i++) {
						ColliderPoint2D vertex = requestVector().
								set(boundsA[i].x + posSrc.x, boundsA[i].y + posSrc.y)
								.rotateVec(shapeRotateA);
						vecListA.add(vertex);
					}
				}
				
				
				
				
				
				if (collidingClass == Circle2D.class) {
					
					float minDistSquare = Float.MAX_VALUE;
					float minX = Float.MAX_VALUE;
					float minY = Float.MAX_VALUE;
					
					for (ColliderPoint2D vertexA: vecListA) {
						float dx = posCld.x - vertexA.x;
						float dy = posCld.y - vertexA.y;
						float distSquare = dx * dx + dy * dy;
						if (distSquare < minDistSquare) {
							minDistSquare = distSquare;
							minX = vertexA.x;
							minY = vertexA.y;
						}
					}
					
					minVec.set(posCld.x - minX, posCld.y - minY);
					minVec.rotate90(false).norm();
					outList.add(minVec);
				}
				
				else if (collidingClass == Rect2D.class) {
					
					float minDistSquare = Float.MAX_VALUE;
					float minAX = Float.MAX_VALUE;
					float minAY = Float.MAX_VALUE;
					float minBX = Float.MAX_VALUE;
					float minBY = Float.MAX_VALUE;
					
					ColliderPoint2D shapeRotateB = requestVector();
					shapeRotateB.set(colliding.rotateX, colliding.rotateY).rotateVec(rotTransCld);
					
					float halfWidth = ((Rect2D) colliding).getWidth() * 0.5f;
					float halfHeight = ((Rect2D) colliding).getHeight() * 0.5f;
					
					vecListB.add(requestVector().set(posCld.x - halfWidth, posCld.y - halfHeight)
							.rotateVec(shapeRotateB));
					vecListB.add(requestVector().set(posCld.x + halfWidth, posCld.y - halfHeight)
							.rotateVec(shapeRotateB));
					vecListB.add(requestVector().set(posCld.x + halfWidth, posCld.y + halfHeight)
							.rotateVec(shapeRotateB));
					vecListB.add(requestVector().set(posCld.x - halfWidth, posCld.y + halfHeight)
							.rotateVec(shapeRotateB));
					
					for (ColliderPoint2D vertexA: vecListA) {
						for (ColliderPoint2D vertexB: vecListB) {
							float dx = vertexB.x - vertexA.x;
							float dy = vertexB.y - vertexA.y;
							float distSquare = dx * dx + dy * dy;
							if (distSquare < minDistSquare) {
								minDistSquare = distSquare;
								minAX = vertexA.x;
								minAY = vertexA.y;
								minBX = vertexB.x;
								minBY = vertexB.y;
							}
						}
					}
					
					for (ColliderPoint2D vertexB: vecListB) {
						storeVector(vertexB);
					}
					vecListB.clear();
					storeVector(shapeRotateB);
					
					minVec.set(minBX - minAX, minBY - minAY);
					minVec.rotate90(false).norm();
					outList.add(minVec);
				}
				
				else if (collidingClass == Capsule2D.class) {
					
					float minDistSquare = Float.MAX_VALUE;
					float minAX = Float.MAX_VALUE;
					float minAY = Float.MAX_VALUE;
					float minBX = Float.MAX_VALUE;
					float minBY = Float.MAX_VALUE;
					
					ColliderPoint2D shapeRotateB = requestVector();
					shapeRotateB.set(colliding.rotateX, colliding.rotateY).rotateVec(rotTransCld);
					
					float distance = ((Capsule2D) colliding).getDistance();
					float radius = ((Capsule2D) colliding).getRadius();
					
					// capsule rect coordinates
					vecListB.add(requestVector().set(posCld.x - distance * 0.5f, posCld.y - radius)
							.rotateVec(shapeRotateB));
					vecListB.add(requestVector().set(posCld.x + distance * 0.5f, posCld.y - radius)
							.rotateVec(shapeRotateB));
					vecListB.add(requestVector().set(posCld.x + distance * 0.5f, posCld.y + radius)
							.rotateVec(shapeRotateB));
					vecListB.add(requestVector().set(posCld.x - distance * 0.5f, posCld.y + radius)
							.rotateVec(shapeRotateB));
					
					// capsule circles coordinates
					vecListB.add(requestVector().set(posCld.x + distance * 0.5f, posCld.y)
							.rotateVec(shapeRotateB));
					vecListB.add(requestVector().set(posCld.x - distance * 0.5f, posCld.y)
							.rotateVec(shapeRotateB));
					
					for (ColliderPoint2D vertexA: vecListA) {
						for (ColliderPoint2D vertexB: vecListB) {
							float dx = vertexB.x - vertexA.x;
							float dy = vertexB.y - vertexA.y;
							float distSquare = dx * dx + dy * dy;
							if (distSquare < minDistSquare) {
								minDistSquare = distSquare;
								minAX = vertexA.x;
								minAY = vertexA.y;
								minBX = vertexB.x;
								minBY = vertexB.y;
							}
						}
					}
					
					for (ColliderPoint2D vertexB: vecListB) {
						storeVector(vertexB);
					}
					vecListB.clear();
					storeVector(shapeRotateB);
					
					minVec.set(minBX - minAX, minBY - minAY);
					minVec.rotate90(false).norm();
					outList.add(minVec);
				}
				
				else if (collidingClass == ConvexPolygon2D.class) {
					
					float minDistSquare = Float.MAX_VALUE;
					float minAX = Float.MAX_VALUE;
					float minAY = Float.MAX_VALUE;
					float minBX = Float.MAX_VALUE;
					float minBY = Float.MAX_VALUE;
					
					ColliderPoint2D shapeRotateB = requestVector();
					shapeRotateB.set(colliding.rotateX, colliding.rotateY).rotateVec(rotTransCld);
					
					ColliderPoint2D[] boundsB = ((ConvexPolygon2D) colliding).bounds;
					for (int i = 0; i < boundsB.length; i++) {
						ColliderPoint2D vertex = requestVector().
								set(boundsB[i].x + posCld.x, boundsB[i].y + posCld.y)
								.rotateVec(shapeRotateB);
						vecListB.add(vertex);
					}
					
					for (ColliderPoint2D vertexA: vecListA) {
						for (ColliderPoint2D vertexB: vecListB) {
							float dx = vertexB.x - vertexA.x;
							float dy = vertexB.y - vertexA.y;
							float distSquare = dx * dx + dy * dy;
							if (distSquare < minDistSquare) {
								minDistSquare = distSquare;
								minAX = vertexA.x;
								minAY = vertexA.y;
								minBX = vertexB.x;
								minBY = vertexB.y;
							}
						}
					}
					
					for (ColliderPoint2D vertexB: vecListB) {
						storeVector(vertexB);
					}
					vecListB.clear();
					storeVector(shapeRotateB);
					
					minVec.set(minBX - minAX, minBY - minAY);
					minVec.rotate90(false).norm();
					outList.add(minVec);
				}
				
				for (ColliderPoint2D vertex: vecListA) {
					storeVector(vertex);
				}
				vecListA.clear();
				
				break;
			
			default:
				break;
		}
		
		storeVector(rotTransSrc);
		storeVector(rotTransCld);
		storeVector(posSrc);
		storeVector(posCld);
		storeVector(shapeRotateA);
	}
	
	protected void projectShape(ColliderShape2D<?> shape, ColliderTransform2D trans, 
			ColliderPoint2D axis, Interval outInterval) {
		Class<?> shapeClass = shape.getClass();
		String shapeClassName = shapeClass.getSimpleName();
		
		ColliderPoint2D rotTrans = requestVector()
				.set(trans.rotateX, trans.rotateY);
		
		ColliderPoint2D pos = requestVector()
				.set(shape.cx + trans.posX, shape.cy + trans.posY)
				.rotateVecAround(rotTrans, trans.posX, trans.posY);
		
		ColliderPoint2D shapeRotate = requestVector();
		shapeRotate.set(shape.rotateX, shape.rotateY).rotateVec(rotTrans);
		
		
		float min = Float.MAX_VALUE;
		float max = -Float.MAX_VALUE;
		
		switch (shapeClassName) {
		
			case "Circle2D":
			case "Rect2D":
			case "Capsule2D":
			case "ConvexPolygon2D":
				if (shapeClass == Circle2D.class) {
					
					float r = ((Circle2D) shape).getRadius();
					
					vecListA.add(requestVector().set(pos.x + axis.x * r, pos.y + axis.y * r));
					vecListA.add(requestVector().set(pos.x - axis.x * r, pos.y - axis.y * r));
				}
				else if (shapeClass == Rect2D.class) {
					
					float halfWidth = ((Rect2D) shape).getWidth() * 0.5f;
					float halfHeight = ((Rect2D) shape).getHeight() * 0.5f;
					
					vecListA.add(requestVector().set(pos.x - halfWidth, pos.y - halfHeight)
							.rotateVec(shapeRotate));
					vecListA.add(requestVector().set(pos.x + halfWidth, pos.y - halfHeight)
							.rotateVec(shapeRotate));
					vecListA.add(requestVector().set(pos.x + halfWidth, pos.y + halfHeight)
							.rotateVec(shapeRotate));
					vecListA.add(requestVector().set(pos.x - halfWidth, pos.y + halfHeight)
							.rotateVec(shapeRotate));
				}
				else if (shapeClass == Capsule2D.class) {
					
					float r = ((Capsule2D) shape).getRadius();
					float dh = ((Capsule2D) shape).getDistance() * 0.5f;
					
					vecListA.add(requestVector().set(pos.x + dh, pos.y + dh)
							.rotateVec(shapeRotate).add(axis.x * r, axis.y * r));
					vecListA.add(requestVector().set(pos.x - dh, pos.y - dh)
							.rotateVec(shapeRotate).add(-axis.x * r, -axis.y * r));
				}
				else if (shapeClass == ConvexPolygon2D.class) {
					
					ColliderPoint2D[] bounds = ((ConvexPolygon2D) shape).bounds;
					for (int i = 0; i < bounds.length; i++) {
						ColliderPoint2D vertex = requestVector().
								set(bounds[i].x + pos.x, bounds[i].y + pos.y)
								.rotateVec(shapeRotate);
						vecListA.add(vertex);
					}
				}
				
				for (ColliderPoint2D vertexA: vecListA) {
					float dot = vertexA.x * axis.x + vertexA.y * axis.y;
					min = Math.min(min, dot);
					max = Math.max(max, dot);
					storeVector(vertexA);
				}
				vecListA.clear();
				
				outInterval.max = max;
				outInterval.min = min;
				
				break;
		}
		
		storeVector(rotTrans);
		storeVector(pos);
		storeVector(shapeRotate);
	}
	
	protected boolean hasOverlap(Interval iA, Interval iB) {
		return !(iA.getMin() > iB.getMax() || iB.getMin() > iA.getMax());
	}
	
	// check for containment (one shape is inside of the other)
	protected boolean contained(Interval iA, Interval iB) {
		return (iA.getMin() >= iB.getMin() && iA.getMax() <= iB.getMin() ) 
				|| (iB.getMin() >= iA.getMin() && iB.getMin()  <= iA.getMax());
	}
	
	
	
	
	
	
	
	/*
	
	public boolean rayIntersectsShape(float fromX, float fromY, float toX, float toY,
			ColliderShape2D<?> shape, Transform2D transform, 
			ColliderPoint2D out) {
		
		Class<?> shapeClass = shape.getClass();
		String shapeClassName = shapeClass.getSimpleName();
		
		
		ColliderPoint2D from = requestVector()
				.set(fromX, fromY);
		
		ColliderPoint2D to = requestVector()
				.set(toX, toY);
		
		ColliderPoint2D rayDir = requestVector()
				.set(to).sub(from).norm();
		
		
		
		ColliderPoint2D rotTrans = requestVector()
				.set(transform.rotateX, transform.rotateY);
		
		ColliderPoint2D posShape = requestVector()
				.set(shape.cx + transform.cx, shape.cy + transform.cy)
				.rotateVecAround(rotTrans, transform.cx, transform.cy);
		
		ColliderPoint2D shapeRotate = requestVector();
		shapeRotate.set(shape.rotateX, shape.rotateY).rotateVec(rotTrans);
		
		
		switch (shapeClassName) {
		
			case "Circle2D":
				if (shapeClass == Circle2D.class) {
					
					float r = ((Circle2D) shape).getRadius();
					
					vecListA.add(requestVector().set(pos.x + axis.x * r, pos.y + axis.y * r));
					vecListA.add(requestVector().set(pos.x - axis.x * r, pos.y - axis.y * r));
				}
				break;
			case "Capsule2D":
				if (shapeClass == Capsule2D.class) {
					
					float r = ((Capsule2D) shape).getRadius();
					float dh = ((Capsule2D) shape).getDistance() * 0.5f;
					
					vecListA.add(requestVector().set(pos.x + dh, pos.y + dh)
							.rotateVec(shapeRotate).add(axis.x * r, axis.y * r));
					vecListA.add(requestVector().set(pos.x - dh, pos.y - dh)
							.rotateVec(shapeRotate).add(-axis.x * r, -axis.y * r));
				}
				break;
			case "Rect2D":
			case "ConvexPolygon2D":
				if (shapeClass == Rect2D.class) {
					
					float halfWidth = ((Rect2D) shape).getWidth() * 0.5f;
					float halfHeight = ((Rect2D) shape).getHeight() * 0.5f;
					
					vecListA.add(requestVector().set(posShape.x - halfWidth, posShape.y - halfHeight)
							.rotateVec(shapeRotate));
					vecListA.add(requestVector().set(posShape.x + halfWidth, posShape.y - halfHeight)
							.rotateVec(shapeRotate));
					vecListA.add(requestVector().set(posShape.x + halfWidth, posShape.y + halfHeight)
							.rotateVec(shapeRotate));
					vecListA.add(requestVector().set(posShape.x - halfWidth, posShape.y + halfHeight)
							.rotateVec(shapeRotate));
				}
				else if (shapeClass == ConvexPolygon2D.class) {
					
					ColliderPoint2D[] bounds = ((ConvexPolygon2D) shape).bounds;
					for (int i = 0; i < bounds.length; i++) {
						ColliderPoint2D vertex = requestVector().
								set(bounds[i].x + posShape.x, bounds[i].y + posShape.y)
								.rotateVec(shapeRotate);
						vecListA.add(vertex);
					}
				}
				
				float minDist = Float.MAX_VALUE;
				int size = vecListA.size();
				
				for (int i = 0; i < size; i++) {
					int j = i + 1 < size ? i + 1 : 0;
					
					
				}
				
				break;
				
		}
		return false;
	}
	
	
	protected boolean segmentToSegmentIntersection(float a1x, float a1y, float a2x, float a2y,
			float b1x, float b1y, float b2x, float b2y, Vector2D out) {
		
		float d1x = a2x - a1x;
		float d1y = a2y - a1y;
		float d2x = b2x - b1x;
		float d2y = b2y - b1y;
		
		// use cross product in finding the determinant
		float det = d1x * d2y - d1y * d2x;
		
		// if determinant is near zero, lines/segments are parallel/collinear.
		if (Math.abs(det) <= 1e-6) return false;
		
		// Calculate the difference between the start points of the lines/segments
		float diffX = b1x - a1x;
		float diffY = b1y - a1y;
		
		// Calculate the parameters t and u
		float t = (diffX * d2y - diffY * d2x) / det;
		float u = (diffX * d1y - diffY * d1x) / det;
		

		if (t < 0 || t > 1 || u < 0 || u > 1)
			return false; // No intersection within the inclusive bounds of the segments

		// calculate for intersection points
		float ix = a1x + t * d1x;
		float iy = a1y + t * d1y;
		
		if (out != null) {
			out.set(ix, iy);
		}
		
		return true;
	}
	
	protected int segmentToCircleIntersection(float sx1, float sy1, float sx2, float sy2,
			float cx, float cy, float radius, ColliderPoint2D out1, ColliderPoint2D out2) {
		
		int count = 0;
		float dx = sx2 - sx1;
		float dy = sy2 - sy1;
		float fx = sx1 - cx;
		float fy = sy1 - cy;
		
		float a = dx * dx + dy * dy;
		float b = 2 * (fx * dx + fy * dy);
		float c = fx * fx + fy * fy - radius * radius;
		
		float discriminant = b * b - 4 * a * c;
		if (discriminant < 0) return count;
		
		float discrimRoot = (float) Math.sqrt(discriminant);
		float t1 = (-b + discrimRoot) / (2 * a);
		float t2 = (-b - discrimRoot) / (2 * a);
		
		/////////
		if (t1 >= 0 && t1 <= 1) {
			out1.set(sx1 + t1 * dx, sy1 + t2 * dy);
			count += 1;
		}
		if (t2 >= 0 && t2 <= 1) {
			out2.set(sx1 + t2 * dx, sy1 + t2 * dy);
			count += 1;
		}
		
		return count;
	}
	
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	protected void _flush() {
		for(ColliderPoint2D vec: axes) {
			storeVector(vec);}
		axes.clear();
		itvA.reset();
		itvB.reset();
	}
	
	
	protected ColliderPoint2D requestVector() {
		ColliderPoint2D data = null;
		if (!cachedVectors.isEmpty()) {
			data = cachedVectors.pop();
		}
		else {
			data = new ColliderPoint2D();
		}
		return data;
	}
	
	protected void storeVector(ColliderPoint2D data) {
		data.set(0, 0);
		cachedVectors.push(data);
	}
	
	
	

	
	
	
	
	
	
	public static class Interval {
		protected float min = 0;
		protected float max = 0;
		
		public void set(float min, float max) {
			if (min > max) {
				throw new IllegalArgumentException("Interval error: Min must be never be greater than max. Min: "+min+", Max: "+max+".");
			}
			this.min = min;
			this.max = max;
		}
		
		public void reset() {
			min = 0;
			max = 0;
		}
		
		public float getMin() {
			return min;
		}
		
		public float getMax() {
			return max;
		}
	}
}
