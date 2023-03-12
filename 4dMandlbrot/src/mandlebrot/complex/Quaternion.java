package mandlebrot.complex;

public class Quaternion extends ComplexNumber {

	protected double c;
	protected double d;

	public Quaternion() {
		super();
		c = 0;
		d = 0;
	}

	public Quaternion(double a, double b, double c, double d) {
		super(a, b);
		this.c = c;
		this.d = d;
	}

	public String toString() {
		return super.toString() + "+" + c + "j+" + d + "k";
	}

	/**
	 * 
	 * @return The j component
	 */
	public double getC() {
		return c;
	}

	/**
	 * 
	 * @return The k component
	 */
	public double getD() {
		return d;
	}

	public Quaternion getConjagate() {
		return new Quaternion(a, -b, -c, -d);
	}

	public Quaternion getInverse() {
		return this.getConjagate().times(1 / (a * a + b * b + c * c + d * d));
	}

	public double getDistance() {
		return Math.sqrt(a * a + b * b + c * c + d * d);
	}

	public Quaternion plus(Quaternion q) {
		return new Quaternion(a + q.a, b + q.b, c + q.c, d + q.d);
	}

	@Deprecated
	public void plusEquals(Quaternion q) {
		this.a += q.a;
		this.b += q.b;
		this.c += q.c;
		this.d += q.d;
	}

	public Quaternion minus(Quaternion q) {
		return new Quaternion(a - q.a, b - q.b, c - q.c, d - q.d);
	}

	@Deprecated
	public void minusEquals(Quaternion q) {
		this.a -= q.a;
		this.b -= q.b;
		this.c -= q.c;
		this.d -= q.d;
	}

	public Quaternion times(double num) {
		return new Quaternion(a * num, b * num, c * num, d * num);
	}

	public Quaternion times(Quaternion q) {
		double r = (this.a * q.a) - (this.b * q.b) - (this.c * q.c) - (this.d * q.d);
		double i = (this.a * q.b) + (this.b * q.a) + (this.c * q.d) - (this.d * q.c);
		double j = (this.a * q.c) - (this.b * q.d) + (this.c * q.a) + (this.d * q.b);
		double k = (this.a * q.d) + (this.b * q.c) - (this.c * q.b) + (this.d * q.a);
		return new Quaternion(r, i, j, k);
	}

	@Deprecated
	public void timesEquals(Quaternion q) {
		double r = (this.a * q.a) - (this.b * q.b) - (this.c * q.c) - (this.d * q.d);
		double i = (this.a * q.b) + (this.b * q.a) + (this.c * q.d) - (this.d * q.c);
		double j = (this.a * q.c) - (this.b * q.d) + (this.c * q.a) + (this.d * q.b);
		double k = (this.a * q.d) + (this.b * q.c) - (this.c * q.b) + (this.d * q.a);
		this.a = r;
		this.b = i;
		this.c = j;
		this.d = k;
	}

	public Quaternion dividedBy(Quaternion q) {
		return this.times(q.getInverse());
	}

}
