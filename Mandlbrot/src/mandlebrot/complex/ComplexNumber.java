package mandlebrot.complex;

public class ComplexNumber {

	public static final ComplexNumber R = new ComplexNumber(1.0, 0);
	public static final ComplexNumber I = new ComplexNumber(0, 1.0);

	/**
	 * The real component
	 */
	protected double a;
	/**
	 * The i component
	 */
	protected double b;

	public ComplexNumber() {
		a = 0;
		b = 0;
	}

	/**
	 * a + b*i
	 * 
	 * @param a The real component
	 */
	public ComplexNumber(double a) {
		this.b = a;
		b = 0;
	}

	/**
	 * a + b*i
	 * 
	 * @param a The real component
	 * @param b The i component
	 */
	public ComplexNumber(double a, double b) {
		this.a = a;
		this.b = b;
	}

	public String toString() {
		return a + " " + b + "i";
	}

	/**
	 * 
	 * @return The real component
	 */
	public double getA() {
		return a;
	}

	/**
	 * 
	 * @return The i component
	 */
	public double getB() {
		return b;
	}
	
	/**
	 * Using re^(theta*i) format, this computes the theta value.
	 * @return The theta value
	 */
	public double getTheta() {
		double theta;
		if (a > 0) {
			theta = Math.atan(b/a);
		} else if (a == 0) {
			if (b > 0) {
				theta = Math.PI / 2.0;
			} else if (b == 0) {
				theta = 0.0;
			} else {
				theta = -Math.PI / 2.0;
			}
		} else {
			if (b > 0) {
				theta = Math.PI +  Math.atan(b/a);
			} else if (b == 0) {
				theta = Math.PI;
			} else {
				theta = -Math.PI + Math.atan(b/a);
			}
		}
		return theta;
	}

	public ComplexNumber getConjagate() {
		return new ComplexNumber(this.a, -this.b);
	}

	public double getDistance() {
		return Math.sqrt(a * a + b * b);
	}

	public ComplexNumber plus(ComplexNumber complex) {
		return new ComplexNumber(this.a + complex.a, this.b + complex.b);
	}

	public void plusEquals(ComplexNumber complex) {
		this.a += complex.a;
		this.b += complex.b;
	}
	
	public ComplexNumber minus(double d) {
		return new ComplexNumber(this.a - d, this.b);
	}
	
	public ComplexNumber minus(ComplexNumber complex) {
		return new ComplexNumber(this.a - complex.a, this.b - complex.b);
	}

	public ComplexNumber times(double d) {
		return new ComplexNumber(d * a, d * b);
	}

	public ComplexNumber times(ComplexNumber complex) {
		double a = (this.a * complex.a) - (this.b * complex.b);
		double b = (this.a * complex.b) + (this.b * complex.a);
		return new ComplexNumber(a, b);
	}

	public void timesEquals(double d) {
		this.a *= d;
		this.b *= d;
	}

	public void timesEquals(ComplexNumber complex) {
		this.a = (this.a * complex.a) - (this.b * complex.b);
		this.b = (this.a * complex.b) + (this.b * complex.a);
	}

	public ComplexNumber dividedBy(double d) {
		return new ComplexNumber(this.a / d, this.b / d);
	}

	public void dividedByEquals(double d) {
		this.a /= d;
		this.b /= d;
	}

	public ComplexNumber dividedBy(ComplexNumber complex) {
		return this.times(complex.getConjagate()).dividedBy(complex.times(complex.getConjagate()).getA());
	}

	public void dividedByEquals(ComplexNumber complex) {
		ComplexNumber result = this.dividedBy(complex);
		this.a = result.a;
		this.b = result.b;
	}

	public ComplexNumber pow(double power) {
		return exp(log(this).times(power));
	}
	
	public ComplexNumber pow(ComplexNumber power) {
		return exp(log(this).times(power));
	}
	

	@Deprecated
	public ComplexNumber powLoop(int p) {
		if (p >= 0) {
			ComplexNumber result = new ComplexNumber(1);
			for (int i = 0; i < p; i++) {
				result.timesEquals(this);
			}
			return result;
		} else {
			ComplexNumber result = new ComplexNumber(1);
			for (int i = 0; i > p; i--) {
				result.dividedByEquals(this);
			}
			return result;
		}
	}

	@Deprecated
	public ComplexNumber powRecursive(int p) {
		if (p == 0) {
			return new ComplexNumber(1);
		} else if (p > 0) {
			return this.times(this.powRecursive(p - 1));
		}
		// p must be < 0
		return R.dividedBy(this.powRecursive(-p));
	}
	

	// Static math if I feel like it
	/**
	 * Computes e to the power of a complex number
	 * @param p The complex power
	 * @return e to the complex power p
	 */
	public static ComplexNumber exp(ComplexNumber p) {
		return new ComplexNumber(Math.exp(p.a) * Math.cos(p.b), Math.exp(p.a) * Math.sin(p.b));
	}
	
	/**
	 * Computes the natural logarithm of a complex number
	 * @param p
	 * @return
	 */
	public static ComplexNumber log(ComplexNumber p) {
		return new ComplexNumber(Math.log(p.getDistance()), p.getTheta());
	}

	public static ComplexNumber add(ComplexNumber c1, ComplexNumber c2) {
		return new ComplexNumber(c1.a + c2.a, c1.b + c2.b);
	}

	public static ComplexNumber mulitply(ComplexNumber c1, ComplexNumber c2) {
		double a = (c1.a * c2.a) - (c1.b * c2.b);
		double b = (c1.a * c2.b) + (c1.b * c2.a);
		return new ComplexNumber(a, b);
	}
}
