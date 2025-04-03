package collision;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import collision.ColliderShape2D.Capsule2D;
import collision.ColliderShape2D.Circle2D;
import collision.ColliderShape2D.ConvexPolygon2D;
import collision.ColliderShape2D.Rect2D;


public class ColliderShapeFactory {

	
	
	public static Circle2D createCircle(float radius) {
		return new Circle2D(0, 0, radius);
	}
	
	public static Circle2D createCircle(float cx, float cy, float radius) {
		return new Circle2D(cx, cy, radius);
	}
	
	
	
	public static Capsule2D createCapsule(float radius, float circleDistance) {
		return new Capsule2D(0, 0, radius, circleDistance, 0);
	}
	
	public static Capsule2D createCapsule(float cx, float cy, float radius, float circleDistance, double tiltAngleRadians) {
		return new Capsule2D(cx, cy, radius, circleDistance, tiltAngleRadians);
	}
	
	
	
	public static Rect2D createRectangle(float width, float height) {
		return new Rect2D(0, 0, width, height, 0);
	}
	
	public static Rect2D createRectangle(float cx, float cy, float width, float height, double tiltAngleRadians) {
		return new Rect2D(cx, cy, width, height, tiltAngleRadians);
	}
	
	
	
	
	protected static List<ColliderPoint2D> temp = new ArrayList<>();
	
	public static List<ConvexPolygon2D> createConvexPolygon(ColliderPoint2D[] vertices) {
		return setConvexVertices(vertices);
	}
	
	protected static List<ConvexPolygon2D> setConvexVertices(ColliderPoint2D[] vertices) {
		// checks if vertices parameter is null
		if (vertices == null) {
			throw new IllegalArgumentException("Vertices must not be null.");
		}
		
		// checks if length is less than 3
		int size = vertices.length;
		if (size < 3) {
			throw new IllegalArgumentException("Vertices must have a minimum length of 3.\n"
					+ " Vertices size: "+size+" .");}
		
		// checks for null vertices elements
		for (int i = 0; i < size; i++) {
			if (vertices[i] == null) throw new NullPointerException("Null vertices are not allowed.");
		}
		
		// checks for CCW winding, 0 winding means degenerate polygon
		float winding = PolyUtils.fastGetWinding(vertices);
		if (winding == 0) throw new IllegalArgumentException("Vertices forms a degenerate polygon. Not valid.");
		if (winding < 0) PolyUtils.fastReverseVertices(vertices);
		
		// creates a new copy of vertices and adds it to the list for further processing
		temp.clear();
		for (int i = 0; i < size; i++) {
			temp.add(vertices[i].copy());
		}
		
		// removes polygon's co-incident and co-linear vertices.
		PolyUtils.purifyVertices(temp);
		
		// Moves the purified polygon's center to origin (0, 0)
		ColliderPoint2D[] bounds = (ColliderPoint2D[]) temp.toArray();
		ColliderPoint2D center = new ColliderPoint2D();
		PolyUtils.fastGetCentroid(bounds, center);
		if (!center.isZero()) {
			center.negate();
			for (int i = 0; i < bounds.length; i++) {
				bounds[i].add(center);
			}
		}
		
		// Decomposes the polygon if vertices is greater than 3 and is non-convex, else create a convexPolygon instance.
		List<ConvexPolygon2D> polys = null;
		if (bounds.length < 4) {
			polys = new ArrayList<>();
			ConvexPolygon2D convex = new ConvexPolygon2D(bounds.clone());
			polys.add(convex);
		}
		else {
			 polys = PolyUtils.decompose(temp);
		}
		temp.clear();
		
		return polys;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected static class PolyUtils {
		
		// if winding > 0, winding is CCW; if winding < 0, winding is clockwise; if winding == 0, polygon is degenerate
		protected static float fastGetWinding(ColliderPoint2D[] vertices) {
			
			int size = vertices.length;
			float winding = 0;
			
			for (int i = 0; i < size; i++) {
				ColliderPoint2D a = vertices[ (i - 1) < 0 ? (size - 1) : (i - 1) ];
				ColliderPoint2D b = vertices[i];
				ColliderPoint2D c = vertices[(i + 1) % size];
				
				winding += (b.x - a.x) * (b.y - c.y) - (b.y - a.y) * (b.x - c.x);
			}
			
			return winding;
		}
		
		
		protected static void fastReverseVertices(ColliderPoint2D[] vertices) {
			
			int size = vertices.length;
			if (size == 0 || size == 1) return;
			
			ColliderPoint2D temp = null;
			int i = 0;
			int j = size - 1;
			
			while (i < j) {
				temp = vertices[i];
				vertices[i] = vertices[j];
				vertices[j] = temp;
				i++;
				j--;
			}	
		}
		
		
		// removes all co-incident and co-linear vertices
		protected static void purifyVertices(List<ColliderPoint2D> vertices) {
			int size = vertices.size();
			
			
			List<Integer> target = new ArrayList<Integer>();
			
			for (int i = 0; i < size; i++) {
				ColliderPoint2D a = vertices.get( (i - 1) < 0 ? (size - 1) : (i - 1) );
				ColliderPoint2D b = vertices.get(i);
				ColliderPoint2D c = vertices.get((i + 1) % size);
				
				float cross = (b.x - a.x) * (b.y - c.y) - (b.y - a.y) * (b.x - c.x);
				
				if (Math.abs(cross) <= 1e-6 || b.isEquals(c)) { //checks for all co-incident and co-linear vertices
					target.add(i);
				}
			}
			
			for (int i = 0; i < target.size(); i++)	{
				int r = target.get(i);
				vertices.remove(r);
			}
		}
		
		
		
		
		
		
		// Methods that skips vertices checks for faster calculation.
		
		
		protected static ColliderPoint2D fastGetCentroid(ColliderPoint2D[] vertices, ColliderPoint2D out) {
			
			float centroidX = 0;
			float centroidY = 0;
			float area = 0;
			float _factor = 0;
			
			int size = vertices.length;
			
			for (int i = 0; i < size; i++) {
				ColliderPoint2D v1 = vertices[i];
				ColliderPoint2D v2 = vertices[(i + 1) % size];
				
				_factor += (v1.x * v2.y) - (v2.x * v1.y);
				
				area += _factor;
				centroidX += (v1.x + v2.x) * _factor;
				centroidY += (v1.y + v2.y) * _factor;
			}
			
			return out.set(centroidX / (6 * area), centroidY / (6 * area));
		}
		
		
		
		protected static float fastGetArea(ColliderPoint2D[] vertices) {
			
			int size = vertices.length;
			
			float winding = 0;
			
			for (int i = 0; i < size; i++) {
				ColliderPoint2D a = vertices[ (i - 1) < 0 ? (size - 1) : (i - 1) ];
				ColliderPoint2D b = vertices[i];
				ColliderPoint2D c = vertices[(i + 1) % size];
				
				winding += (b.x - a.x) * (b.y - c.y) - (b.y - a.y) * (b.x - c.x);
			}
			
			return Math.abs(winding) / 2;
		}
		
		
		
		
		
		public static boolean findIntersection(float a1x, float a1y, float a2x, float a2y,
				float b1x, float b1y, float b2x, float b2y,
				boolean isSegment, Boolean isInclusive, ColliderPoint2D out) {
			
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
			
	        // If checking for segments, ensure the intersection is within the segment bounds
			if (isSegment) {
				if (isInclusive == true) {
					if (t < 0 || t > 1 || u < 0 || u > 1) {
						return false; // No intersection within the inclusive bounds of the segments
					}
				}
				
				else {
					if (t <= 0 || t >= 1 || u <= 0 || u >= 1) {
						return false; // No intersection within the bounds of the segments
					}
				}
			}
			
			// calculate for intersection points
			float ix = a1x + t * d1x;
	        float iy = a1y + t * d1y;
	        
	        if (out != null) {
	        	out.set(ix, iy);
	        }
			
			return true;
		}
		
		public static boolean findIntersection(ColliderPoint2D a1, ColliderPoint2D a2,
				ColliderPoint2D b1, ColliderPoint2D b2,
				boolean isSegment, Boolean isInclusive, 
				ColliderPoint2D out) {

			return findIntersection(a1.x, a1.y, a2.x, a2.y,
				b1.x, b1.y, b2.x, b2.y,
				isSegment, isInclusive, out);
		}

		
		
		
		
		protected static List<ConvexPolygon2D> decompose(List<ColliderPoint2D> vertices) { 
			return PolygonDecomposer.decompose(vertices);
		}
		
		

		// Bayazit Polygon2D Decomposition Algorithm
		// Adapted from: https://github.com/dyn4j/dyn4j/blob/master/src/main/java/org/dyn4j/maths/decompose/Bayazit.java
		
		
		protected static class PolygonDecomposer {
			
			protected static List<ConvexPolygon2D> decompose(List<ColliderPoint2D> vertices) {
				
				if (vertices.size() < 4) {
					throw new IllegalArgumentException("Vertices must have a minimum length of 4.\n"
							+ " Vertices size: "+vertices.size()+" .");
				}
				
				List<List<ColliderPoint2D>> validConvexVertices = new ArrayList<>();
				Stack<List<ColliderPoint2D>> verticesStack = new Stack<>();
				verticesStack.push(vertices);
				
				while (!verticesStack.isEmpty()) {
					List<ColliderPoint2D> lowerVertices = new ArrayList<>(); 
					List<ColliderPoint2D> upperVertices = new ArrayList<>();
					double lowerDist = Float.MAX_VALUE;
					double upperDist = Float.MAX_VALUE;
					double closestDist = Float.MAX_VALUE;
					int lowerIndex = 0;
					int upperIndex = 0;
					int closestIndex = 0;
					ColliderPoint2D upperIntersect = new ColliderPoint2D();
					ColliderPoint2D lowerIntersect = new ColliderPoint2D();
					
					List<ColliderPoint2D> currVertices = verticesStack.pop();
					
					int size = currVertices.size();
					boolean isConvex = true;
					
					for (int i = 0; i < size; i++) {
						
						// get current vertex
						ColliderPoint2D p = currVertices.get(i);
						// get adjacent vertices
						ColliderPoint2D p0 = currVertices.get((i - 1 < 0) ? (size - 1) : (i - 1));
						ColliderPoint2D p1 = currVertices.get((i + 1 == size) ? (0) : (i + 1));
						
						if ( isReflex(p0, p, p1)) {
							
							isConvex = false;
							for (int j = 0; j < size; j++) {
								// get current vertex
								ColliderPoint2D q = currVertices.get(j);
								// get adjacent vertices
								ColliderPoint2D q0 = currVertices.get((j - 1 < 0) ? (size - 1) : (j - 1));
								ColliderPoint2D q1 = currVertices.get((j + 1 == size) ? (0) : (j + 1));
								// 
								ColliderPoint2D str = new ColliderPoint2D();
								
								if (isLeft(p0, p, q) && isRightOn(p0, p, q0)) {
									
									// get intersection point and check if intersection exists
									boolean intersects = PolyUtils.findIntersection(p0, p, q, q0, false, null, str);
									
									if (intersects) {
										//
										if (isRight(p1, p, str)) {
											double dist = p.dist2(str);
											
											if (dist < lowerDist) {
												lowerDist = dist;
												lowerIntersect.set(str);
												lowerIndex = j;
											}
										}
									}
								}
								
								if (isLeft(p1, p, q1) && isRightOn(p1, p, q)) {
									
									// get intersection point and check if intersection exists
									boolean intersects = PolyUtils.findIntersection(p1, p, q, q1, false, null, str);
									
									if (intersects) {
										// 
										if (isLeft(p0, p, str)) {
											double dist = p.dist2(str);
											
											if (dist < upperDist) {
												upperDist = dist;
												upperIntersect.set(str);
												upperIndex = j;
											}
										}
									}
								}
							}
							
							// if the lower index and upper index are equal then this means
							// that the range of p only included an edge (both extended previous 
							// and next edges of p only intersected the same edge, therefore no
							// point exists within that range to connect to)
							if (lowerIndex == (upperIndex + 1) % size) {
								
								// create steiner point in between 2 vertices
								ColliderPoint2D stnr = upperIntersect.copy().add(lowerIntersect).mult(0.5f);
								
								// partition the polygon
								if (i < upperIndex) {
									lowerVertices.addAll(currVertices.subList(i, upperIndex + 1));
									lowerVertices.add(stnr);
									upperVertices.add(stnr.copy());
									if (lowerIndex != 0) upperVertices.addAll(currVertices.subList(lowerIndex, size));
									upperVertices.addAll(currVertices.subList(0, i + 1));
								} 
								else {
									if (i != 0) lowerVertices.addAll(currVertices.subList(i, size));
									lowerVertices.addAll(currVertices.subList(0, upperIndex + 1));
									lowerVertices.add(stnr);
									upperVertices.add(stnr.copy());
									upperVertices.addAll(currVertices.subList(lowerIndex, i + 1));
								}
							}
							// find closest visible vertex
							else {
								
								if (lowerIndex > upperIndex) {
									upperIndex += size;}
								
								closestIndex = lowerIndex;
								
								// find the closest visible point
								for (int j = lowerIndex; j <= upperIndex; j++) {
									int jmod = j % size;
									ColliderPoint2D q = currVertices.get(jmod);
									
									if (q == p || q == p0 || q == p1) continue;
									
									// check the distance first, since this is generally
									// a much faster operation than checking if its visible
									double dist = p.dist2(q);
									if (dist < closestDist) {
										if (isVisible(i, jmod, currVertices)) {
											closestDist = dist;
											closestIndex = jmod;
										}
									}
								}
								
								
								// once we find the closest partition the polygon
								if (i < closestIndex) {
									lowerVertices.addAll(currVertices.subList(i, closestIndex + 1));
									if (lowerIndex != 0) upperVertices.addAll(currVertices.subList(closestIndex, size));
									upperVertices.addAll(currVertices.subList(0, i + 1));
								} 
								else {
									if (i != 0) lowerVertices.addAll(currVertices.subList(i, size));
									lowerVertices.addAll(currVertices.subList(0, closestIndex + 1));
									upperVertices.addAll(currVertices.subList(closestIndex, i + 1));
								}
								
							}
							
							// push the vertices with bigger size first, then the smaller one
							// so the vertices with smaller size will be processed first.
							if (lowerVertices.size() < upperVertices.size()) {
								verticesStack.push(upperVertices);
								verticesStack.push(lowerVertices);
							}
							else {
								verticesStack.push(lowerVertices);
								verticesStack.push(upperVertices);
							}
							
							// exit after processing the first concave vertex to process the next sub-polygon
							break;
						}
					}
					
					// if current polygon vertices aren't containing concave vertices, it will be added to the list
					if (isConvex) {
						validConvexVertices.add(currVertices);
					}
				}
				
				List<ConvexPolygon2D> polygons = new ArrayList<>();
				
				for (List<ColliderPoint2D> polygonVertices: validConvexVertices) {
					if (polygonVertices.size() < 3)
						throw new IllegalArgumentException("Given polygon vertices is self-intersecting and is not supported.");
					PolyUtils.purifyVertices(polygonVertices);
					polygons.add(	new ConvexPolygon2D( ((ColliderPoint2D[]) polygonVertices.toArray()) ) );
				}
				
				return polygons;
			}
			
			
			
			
			
			
			private static boolean isReflex(ColliderPoint2D a, ColliderPoint2D b, ColliderPoint2D c) {
				return isRight(a, b, c);
			}
			
			
			private static boolean isLeft(ColliderPoint2D a, ColliderPoint2D b, ColliderPoint2D c) {
				return triangleSignedArea(a, b, c) > 0;
			}
			
			private static boolean isRight(ColliderPoint2D a, ColliderPoint2D b, ColliderPoint2D c) {
				return triangleSignedArea(a, b, c) < 0;
			}
			
			private static boolean isLeftOn(ColliderPoint2D a, ColliderPoint2D b, ColliderPoint2D c) {
				return triangleSignedArea(a, b, c) >= 0;
			}
			
			private static boolean isRightOn(ColliderPoint2D a, ColliderPoint2D b, ColliderPoint2D c) {
				return triangleSignedArea(a, b, c) <= 0;
			}
			
			private static float triangleSignedArea(ColliderPoint2D a, ColliderPoint2D b, ColliderPoint2D c) {
				return (b.x - a.x) * (b.y - c.y) - (b.y - a.y) * (b.x - c.x);
			}
			
			
			
			private static boolean isVisible (int i, int j, List<ColliderPoint2D> vertices) {
				
				int s = vertices.size();
				ColliderPoint2D iv0, iv, iv1;
				ColliderPoint2D jv0, jv, jv1;
				
				iv0 = vertices.get(i == 0 ? s - 1 : i - 1);
				iv = vertices.get(i);
				iv1 = vertices.get(i + 1 == s ? 0 : i + 1);
				
				jv0 = vertices.get(j == 0 ? s - 1 : j - 1);
				jv = vertices.get(j);
				jv1 = vertices.get(j + 1 == s ? 0 : j + 1);
				
				// can i see j
				if (isReflex(iv0, iv, iv1)) {
					if (isLeftOn(iv, iv0, jv) && isRightOn(iv, iv1, jv)) return false;
				} else {
					if (isRightOn(iv, iv1, jv) || isLeftOn(iv, iv0, jv)) return false;
				}
				// can j see i
				if (isReflex(jv0, jv, jv1)) {
					if (isLeftOn(jv, jv0, iv) && isRightOn(jv, jv1, iv)) return false;
				} else {
					if (isRightOn(jv, jv1, iv) || isLeftOn(jv, jv0, iv)) return false;
				}
				// make sure the segment from i to j doesn't intersect any edges
				for (int k = 0; k < s; k++) {
					int ki1 = k + 1 == s ? 0 : k + 1;
					if (k == i || k == j || ki1 == i || ki1 == j) continue;
					ColliderPoint2D k1 = vertices.get(k);
					ColliderPoint2D k2 = vertices.get(ki1);
					
					boolean intersects = PolyUtils.findIntersection(iv, jv, k1, k2, true, true, null);
					if (intersects == true) return false;
				}
				
				return true;
			}
		}
	}
	
	
}
