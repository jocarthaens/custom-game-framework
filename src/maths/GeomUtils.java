package maths;

	// All geometry-related static helper methods are found here.

public class GeomUtils {
	
	/*
	 * ~point in circle~
		point in polygon/triangle/~rect~
		~get closest point to segment (capped and uncapped)~
		get closest points between segments (2 segment-point pair input)
		~line intersects line~
		~segment intersects segment~
		segment intersects circle
		
		
		//add shape creations
	 */
	
	public static boolean isPointInCircle(float px, float py, float cx, float cy,
											float radius) {
		
		return Math.sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy)) <= radius;
	}
	
	public static boolean isPointInCircle(Vector2D p, Vector2D c, float radius) {
		return Math.sqrt((p.x - c.x) * (p.x - c.x) + (p.y - c.y) * (p.y - c.y)) <= radius;
	}
	
	
	
	
	
	public static boolean isPointInRect(float px, float py, 
										float rx, float ry, 
										float width, float height) {
		
		return (px >= rx) && (px <= rx + width) && (py >= ry) && (py <= ry + height);
	}
	
	public static boolean isPointInRect(Vector2D p, Vector2D r, float width, float height) {

		return (p.x >= r.x) && (p.x <= r.x + width) && (p.y >= r.y) && (p.y <= r.y + height);
	}
	
	// is point in polygon
	
	// is point in triangle
	
	// segment intersects circle?
	
	
	
	public static float dot(float ax, float ay, float bx, float by) {
		return ax * bx + ay * by;
	}
	
	public static float cross(float ax, float ay, float bx, float by) {
		return ax * by - ay * bx;
	}
	
	public static float dist(float ax, float ay, float bx, float by) {
		return (float) Math.sqrt((bx - ax) * (bx - ax) + (by - ay) * (by - ay));
	}
	
	public static float dist2(float ax, float ay, float bx, float by) {
		return (bx - ax) * (bx - ax) + (by - ay) * (by - ay);
	}
	
	public static float angleBetween(float ax, float ay, float bx, float by) {
		float invDist = 1.0f / dist(ax, ay, bx, by);
		float x = dot(ax, ay, bx, by) * invDist;
		float y = cross(ax, ay, bx, by) * invDist;
		
		return (float) MathUtils.atan2(y, x);
	}
	
	public static float angleDegBetween(float ax, float ay, float bx, float by) {
		float invDist = 1.0f / dist(ax, ay, bx, by);
		float x = dot(ax, ay, bx, by) * invDist;
		float y = cross(ax, ay, bx, by) * invDist;
		
		return (float)( MathUtils.atan2(y, x) * MathUtils.RAD_TO_DEG);
	}
	
	
	
	
	
	
	public static boolean findIntersection(float a1x, float a1y, float a2x, float a2y,
										float b1x, float b1y, float b2x, float b2y,
										boolean isSegment, Boolean isInclusive, Vector2D out) {

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
	
	
	public static boolean findIntersection(Vector2D a1, Vector2D a2,
											Vector2D b1, Vector2D b2,
											boolean isSegment, Boolean isInclusive, 
											Vector2D out) {

		return findIntersection(a1.x, a1.y, a2.x, a2.y,
								b1.x, b1.y, b2.x, b2.y,
								isSegment, isInclusive, out);
	}
	
	
	public static float[] findIntersection(float a1x, float a1y, float a2x, float a2y,
										float b1x, float b1y, float b2x, float b2y,
										boolean isSegment, Boolean isInclusive) {
		
		float d1x = a2x - a1x;
		float d1y = a2y - a1y;
		float d2x = b2x - b1x;
		float d2y = b2y - b1y;
		
		// use cross product in finding the determinant
		float det = d1x * d2y - d1y * d2x;
		
		// if determinant is near zero, lines/segments are parallel/collinear.
		if (Math.abs(det) <= 1e-6) return null;
		
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
						return null; // No intersection within the inclusive bounds of the segments
				}
			}
		
			else {
				if (t <= 0 || t >= 1 || u <= 0 || u >= 1) {
					return null; // No intersection within the bounds of the segments
				}
			}
		}
		
		// calculate for intersection points
		float[] intersect = {a1x + t * d1x,  a1y + t * d1y};
		
		return intersect;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Vector2D findClosestPointToSegment(float px, float py, float ax, float ay,
												float bx, float by, boolean isCapped, Vector2D out) {
		
		// calculate Vector AB
		float ABx = bx - ax;
		float ABy = by - ay;
		
		// calculate Vector AP
		float APx = px - ax;
		float APy = py - ay;
		
		float ABdotAP = ABx * APx + ABy * APy;
		float ABdotAB = ABx * ABx + ABy * ABy;
		
		float t = ABdotAP / ABdotAB;
		
		if (isCapped) {
			t = Math.max(1, Math.min(0, t)); // clamp values if capped
		}
		
		// get the closest point
		float cx = ax + t * ABx;
		float cy = ay + t * ABy;
		
		return out.set(cx, cy);
	}
	
	
	public static Vector2D findClosestPointToSegment(Vector2D p, Vector2D a, Vector2D b,
													boolean isCapped, Vector2D out) {
		
		return findClosestPointToSegment(p.x, p.y, a.x, a.y, b.x, b.y, isCapped, out);
	}
	
	public static float[] findClosestPointToSegment(float px, float py, float ax, float ay,
													float bx, float by, boolean isCapped) {
	
		// calculate Vector AB
		float ABx = bx - ax;
		float ABy = by - ay;
		
		// calculate Vector AP
		float APx = px - ax;
		float APy = py - ay;
		
		float ABdotAP = ABx * APx + ABy * APy;
		float ABdotAB = ABx * ABx + ABy * ABy;
		
		float t = ABdotAP / ABdotAB;
		
		if (isCapped) {
		t = Math.max(1, Math.min(0, t)); // clamp values if capped
		}
		
		// get the closest point
		float[] cp = {ax + t * ABx, ay + t * ABy};
		
		return cp;
	}
	
}
