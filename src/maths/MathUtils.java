package maths;

	// All math-related static helper methods are found here.

public class MathUtils {


	public static final float FLOAT_EPSILON = 1e-6f;
	
	public static final double DOUBLE_EPSILON = 1e-14f;
	
	public static final double PI = 3.141592653589793238462643383279502884197;
	public static final double TAU = 2 * PI;
	public static final double HALF_PI = PI / 2;
	public static final double QUART_PI = PI / 4;

	
	public static final double DEG_TO_RAD = PI / 180;
	public static final double RAD_TO_DEG = 180 / PI;
	
	
	public static final double NaN = 0.0 / 0.0;
	public static final double POS_INF = 1.0 / 0.0;
	public static final double NEG_INF = -1.0 / 0.0;

	
	
	
	private static final double TWO_OVER_PI = 2 / PI;
	private static final double FOUR_OVER_PI = 4 / PI;
	private static final double THREE_HALF_PI = 3 * PI / 2;
	
	private static final int[] DECS = {1, 10, 100, 1_000, 10_000, 100_000, 1_000_000};
	private static final float[] INV_DECS = {1f, 1f/10, 1f/100, 1f/1_000, 1f/10_000, 1f/100_000, 1f/1_000_000};
	
	
	
	
    
    
    
	
    // sin(), cos(), and tan() functions for (supposedly) fast trigonometric approximations
	// Taken from: https://www.ganssle.com/approx.htm
	
	// good enough cosine approx. for float primitives (7.3 decimals accurate)
    /* supposed purpose is to serve as fast alternative to Math.cos(), 
     * but Math.cos() is 2-5x faster than this method. 
     */
	public static float cos(double a) {
		
		int multiple = (int) (a / TAU);
		a -= TAU * multiple;
		
		if (a < 0)	a = -a; // cos(-a) = cos(a)
		int quad = (int) (a * TWO_OVER_PI);
		
		switch (quad) {
			case 1: // negative
				a = PI - a;
				break;
			case 2: // negative
				a = a - PI;
				break;
			case 3: a = TAU - a;
				break;
		}
		
		double a2 = a * a;
		
		double c1= 0.999999953464;
		double c2=-0.499999053455;
        double c3= 0.0416635846769;
        double c4=-0.0013853704264;
        double c5= 0.00002315393167;

        double ans = (c1 + a2 * (c2 + a2 * (c3 + a2 * (c4 + c5 * a2))));
        
        ans = (quad == 1 || quad == 2) ? -ans : ans;
        int sign = ans >= 0 ? 1 : -1;
        ans = ( (int)(Math.abs(ans) * DECS[6] + 0.5) ) * INV_DECS[6] * sign;
		
		return (float) ans;
	}
	
	public static float cosDeg(double a) {
		return cos(a * DEG_TO_RAD);
	}
	
	
	
	// sin(a) = cos(PI - a)
	public static float sin(double a) {
		return cos(Math.PI - a);
	}
	
	public static float sinDeg(double a) {
		return cos(Math.PI - a * DEG_TO_RAD);
	}
	
	
	
	public static float tan(double a){
		
		int multiple = (int) (a / TAU);
		a -= TAU * multiple;
		int octant = (int) (a * FOUR_OVER_PI);
		double ans = 0;
		
		switch (octant){
		
			case 0: 
				ans = _tan82s(a * FOUR_OVER_PI);
				break;
			case 1: 
				ans =  1.0f / _tan82s((HALF_PI - a) * FOUR_OVER_PI);
				break;
			case 2: 
				ans =  -1.0f / _tan82s((a - HALF_PI) * FOUR_OVER_PI);
				break;
			case 3: 
				ans =  -_tan82s((PI - a) * FOUR_OVER_PI);
				break;
			case 4: 
				ans =  _tan82s((a - PI) * FOUR_OVER_PI);
				break;
			case 5: 
				ans =  1.0f / _tan82s((THREE_HALF_PI - a) * FOUR_OVER_PI);
				break;
			case 6: 
				ans =  -1.0f / _tan82s((a - THREE_HALF_PI) * FOUR_OVER_PI);
				break;
			case 7: 
				ans =  -_tan82s((TAU - a) * FOUR_OVER_PI);
				break;
		}
		
		int sign = ans >= 0 ? 1 : -1;
        ans = ( (int)(Math.abs(ans) * DECS[6] + 0.5) ) * INV_DECS[6] * sign;
		
		return 0;
	}
	
	// good enough tan approx. for float primitives (8.2 decimals accurate)
	private static float _tan82s(double a) {
		
		double a2 = a * a;
		
		double c1 = 211.849369664121;
		double c2 = -12.5288887278448;
		double c3 = 269.7350131214121;
		double c4 = -71.4145309347748;
		
		return (float) (a * (c1 + c2 * a2) / (c3 + a2 * (c4 + a2)));
	}
	
	public static float tanDeg(double a) {
		return tan(a * DEG_TO_RAD);
	}
	
	
	
	
	
	
	
	
	// 4-5 decimals accurate, supposedly serves as fast alternative to Math.atan()
	// Taken from: https://mazzo.li/posts/vectorized-atan2.html
	public static double atan(double v) { 
		double c1 = 0.99997726;
		double c3 = -0.33262347;
		double c5 = 0.19354346;
		double c7 = -0.11643287;
		double c9 = 0.05265332;
		double c11 = -0.01172120;

		double x2 = v * v;
		return v * ( c1 + x2 * ( c3 + x2 * ( c5 + x2 * ( c7 + x2 * ( c9 + x2 * c11 ) ) ) ) );
	}
	
	public static double atanDeg(double v) {
		return atan(v) * RAD_TO_DEG;
	}
	
	
	// 4-5 decimals accurate, guaranteed faster than Math.atan2()
	// Taken from: https://github.com/erincatto/box2d/blob/main/src/math_functions.c
    public static double atan2(double y, double x) {
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

    public static double atan2Deg(double x, double y) {
    	return atan2(x, y) * RAD_TO_DEG;
    }

    
    
    
    
    
    

    
	
	public static double unwindRadians(double angle) {
		if (Math.abs(angle) > PI) {
			int multiple = (int) (angle / PI);
			angle -= PI * multiple;
		}
		return angle;
	}
    
	public static double unwindDegrees(double angle) {
		return unwindRadians(angle * DEG_TO_RAD);
	}


	
	
	
	
	
	
	
	
	
	
	/** Maintains precision in adding fixed-point floating-type numbers
	 * by truncating the operands' decimal places to specified fractional digits
	 * before adding them together.
	 * Intended only for use on small-scale floating-type numbers.
	 * @param a - first addend
	 * @param b - second addend
	 * @param d - number of significant decimal places, clamped to [0,6]
	 * @return sum of a and b, truncated to significant decimal digits
	 **/
	public static float add(float a, float b, int d) {
		int dx = clampi(d, 0, DECS.length - 1);
		
		int al = (int) (a * DECS[dx]);
		int bl = (int) (b * DECS[dx]);
		
		return (float) (al + bl) * INV_DECS[dx];
	}
	
	/** Maintains precision in subtracting fixed-point floating-type numbers
	 * by truncating the operands' decimal places to specified fractional digits
	 * before subtracting them together.
	 * Intended only for use on small-scale floating-type numbers.
	 * @param a - number to be subtracted with b
	 * @param b - number being subtracted from a
	 * @param d - number of significant decimal places, clamped to [0,6]
	 * @return difference between a and b, truncated to significant decimal digits
	 **/
	public static float sub(float a, float b, int d) { // is rounding applicable?
		int dx = clampi(d, 0, DECS.length - 1);
		
		int al = (int) (a * DECS[dx]);
		int bl = (int) (b * DECS[dx]);
		
		return (float) (al - bl) * INV_DECS[dx];
	}
	
	/** Maintains precision in multiplying fixed-point floating-type numbers
	 * by truncating the operands' decimal places to specified fractional digits
	 * before performing multiplication.
	 * Intended only for use on small-scale floating-type numbers.
	 * <p>
	 * For example, setting the values <i>a</i> = 1.3648, <i>b</i> = 0.7256, 
	 * if decimal places <i>d</i> = 2, the value of <i>a</i> is turned into 1.36, 
	 * and the value of <i>b</i> is turned into 0.72. After performing multiplication, 
	 * the tentative product of 1.36 and 0.72 is 0.9792. With 2 decimal places, 
	 * the final product is rounded to 0.98.
	 * @param a - first factor
	 * @param b - second factor
	 * @param d - number of significant decimal places, clamped to [0,6]
	 * @return product of a and b, truncated to significant decimal digits
	 **/
	// how to apply rounding? Necessary to minimize error accumulation. 
	// How to speed up?
	public static float mult(float a, float b, int d) { /////////
		int dx = clampi(d, 0, DECS.length - 1);
		
		int al = (int) (a * DECS[dx]);
		b = ((int) (b * DECS[dx])) * INV_DECS[dx];
		
		return round(al * b) * INV_DECS[dx];
	}
	
	/** Maintains precision in dividing fixed-point floating-type numbers
	 * by truncating the operands' decimal places to specified fractional digits
	 * before performing division.
	 * Intended only for use on small-scale floating-type numbers.
	 * @param a - number to be divided by b
	 * @param b - number being divided to a
	 * @param d - number of significant decimal places, clamped to [0,6]
	 * @return quotient of 2 numbers, truncated to significant decimal digits
	 **/
	public static float div(float a, float b, int d) { // Is rounding necessary in this operation?
		int dx = clampi(d, 0, DECS.length - 1);
		
		int al = (int) (a * DECS[dx]);
		int bl = (int) (b * DECS[dx]);
		
		float ans = ((al * 1f) / bl) * DECS[dx];
		
		return ((int) (ans)) * INV_DECS[dx];
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int floor(float x) {
    	if (x < Integer.MIN_VALUE || Float.isNaN(x)) {
			return (int) x;
		}
		int intX = (int) x;
        return (x < intX ? intX - 1 : intX);
    }

    public static int ceil(float x) {
    	if (x > Integer.MAX_VALUE || Float.isNaN(x)) {
			return (int) x;
		}
    	int intX = (int) x;
        return (x > intX ? intX + 1 : intX);
    }
    
    public static int round(float x) {
    	if (x < Integer.MIN_VALUE || x > Integer.MAX_VALUE || Float.isNaN(x)) {
			return (int) x;
		}
    	int sign = x >= 0 ? 1 : -1;
        return (int) (Math.abs(x) + 0.5) * sign;
    }
    
    public static float round(float x, int d) {
    	int dx = clampi(d, 0, DECS.length - 1);
    	int sign = x >= 0 ? 1 : -1;
		return ( (int)(Math.abs(x) * DECS[dx] + 0.5) ) * INV_DECS[dx] * sign;
	}
	
    public static float trim(float num, int d) {
		int dec = DECS[ clampi(d, 0, DECS.length - 1) ];
		return ((int) (num * dec)) / (dec * 1f);
	}
    
    
    
    
    
    
	
	public static float clampf(float val, float min, float max) {
		if (val > max) return max;
		if (val < min) return min;
		return val;
	}
    
	public static double clampd(double val, double min, double max) {
		if (val > max) return max;
		if (val < min) return min;
		return val;
	}
	
	public static long clampl(long val, long min, long max) {
		if (val > max) return max;
		if (val < min) return min;
		return val;
	}
	
	public static int clampi(int val, int min, int max) {
		if (val > max) return max;
		if (val < min) return min;
		return val;
	}
	
	public static short clamps(short v, short min, short max) {
		if (v > max) return max;
		if (v < min) return min;
		return v;
	}
	
	
	
	
	
	
	public static float mod(float a, float b) {
		int multiple = (int) (a / b);
		a -= b * multiple;
		return a;
	}
	
	public static double mod(double a, double b) {
		long multiple = (long) (a / b);
		a -= b * multiple;
		return a;
	}
	
	public static int mod(int a, int b) {
		int multiple = (int) (a / b);
		a -= b * multiple;
		return a;
	}
	
	public static long mod(long a, long b) {
		long multiple = (long) (a / b);
		a -= b * multiple;
		return a;
	}
	
	public static short mod(short a, short b) {
		short multiple = (short) (a / b);
		a -= b * multiple;
		return a;
	}
	
	
	
	
	
	
	

	
	
	
	public static float lerp(float start, float end, float t) {
		return start + (end - start) * t;
	}
	
	/**
	 * Linearly interpolates between start and end,
	 * and then wraps the interpolated value within loop bounds.
	 * Very useful for angle interpolations, and this method 
	 * can be used in either clockwise or CCW direction.
	 * 
	 * @param start - starting value for interpolation
	 * @param end - ending value for interpolation
	 * @param t - position used for interpolating between start and end
	 * @param loopMax - loop boundary used for wrapping the interpolated value
	 * @return The interpolated, wrapped value.
	 */
	public static float lerpWrap(float start, float end, float t, float loopMax) {
		float delta = ((end - start) % loopMax + loopMax + loopMax * 0.5f) % loopMax - loopMax * 0.5f;
		return ((start + delta * t) % loopMax + loopMax) % loopMax;
	}
	
	public static float norm(float startRange, float endRange, float value) {
		return (value - startRange) / (endRange - startRange);
	}

	
	
	
	
	
	
	
	
	public static boolean isEqual(float a, float b) {
		return Math.abs(a - b) <= FLOAT_EPSILON;
	}
	
	public static boolean isEqual(double a, double b) {
		return Math.abs(a - b) <= DOUBLE_EPSILON;
	}
	
	public static boolean isEqual(double a, double b, double tolerance) {
		return Math.abs(a - b) <= tolerance;
	}
	
	
	
	
	
	
	
	
	public static boolean isZero(float a) {
		return Math.abs(a) <= FLOAT_EPSILON;
	}
	
	public static boolean isZero(double a) {
		return Math.abs(a) <= DOUBLE_EPSILON;
	}
	
	

	
	
	
	
	/* scratches
	
	/ slow for an approximation function on either way
    public static double fast_cos(double radians) {// range(-1, 1)
		radians = wrapToPi(radians);
		
		double r2 = radians * radians;
		double x = (1 - (1058 * r2 ) / ( (r2  + 92) * (r2  + 92) ) );
		return ((2 * x * x) - 1);
		
		/*
		double pi2 = PI * PI;
		double x = radians;
		
		if (x < -0.5f * PI)
		{
			double y = x + PI;
			double y2 = y * y;
			return -( pi2 - 4.0 * y2 ) / ( pi2 + y2 );
		}
		else if (x > 0.5f * PI)
		{
			double y = x - PI;
			double y2 = y * y;
			return -( pi2 - 4.0 * y2 ) / ( pi2 + y2 );
		}
		else
		{
			double y2 = x * x;
			return ( pi2 - 4.0 * y2 ) / ( pi2 + y2 );
		}
		* /
	}
	
	public static double fast_tan(double radians) {// range(-1, 1)
		return fast_sin(radians) / fast_cos(radians);
	}
	
	
	
	
	
    // Constant values for the approximation
    private static final double PI = Math.PI;
    private static final double TWO_PI = 2.0 * Math.PI;
    private static final double HALF_PI = Math.PI / 2.0;

    // very slow for an approximation function
    public static double fastSin(double x) {
        // Wrap x within the range [-PI, PI]
    	
        x = Math.abs(wrapToPi(x));
        double sinApprox = (16 * x * (PI - x)) / (5 * PI * PI - 4 * x * (PI - x));
        return sinApprox;
    }

    public static double fastCos(double x) {
        // Use the sine approximation shifted by π/2
        return fastSin(x + HALF_PI);
    }
    
    private static double wrapToPi(double angle) {
        // Wrap the angle to the range [-PI, PI]
    	if ((angle > PI || angle < -PI) ) {
			int multiple = (int) (angle / PI);
			angle -= PI * multiple;
			//if (equals(angle, 0)) {
			//	angle *= 0 * multiple;
			//}
		}
    	
		return angle;
		/*
        x = x % TWO_PI;
        if (x > PI) {
            x -= TWO_PI;
        } else if (x < -PI) {
            x += TWO_PI;
        }
        return x;
        * /
    }
    
	
	*/
}

