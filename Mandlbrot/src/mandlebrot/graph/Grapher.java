package mandlebrot.graph;

import static mandlebrot.display.Display.HEIGHT;
import static mandlebrot.display.Display.WIDTH;

import java.awt.Color;
import java.util.function.BiFunction;
import java.util.function.Function;

import mandlebrot.complex.ComplexNumber;
import mandlebrot.display.Display;

public class Grapher {

	private static final int DEPTH = 1_000;
	private static final double ZOOM_SENSITIVITY = 5.0;

	/**
	 * Left bound is -width / 2 Right bound is width / 2;
	 */
	private double width;
	/**
	 * Bottom bound is -width / 2 Top bound is width / 2;
	 */
	private double height;
	private double centerX;
	private double centerY;
	private int zoom;

	private boolean complexPow;
	private ComplexNumber pow;

	private boolean julia;
	private ComplexNumber c;

	private int pixelsCompleated;

	private float colorShift;
	private BiFunction<Integer, Integer, Integer> colors;

	public Grapher(double width, double height) {
		this.width = width;
		this.height = height;
		this.centerX = 0;
		this.centerY = 0;
		this.zoom = 0;
		this.complexPow = false;
		this.pow = new ComplexNumber(2.0, 0);
		this.julia = false;
		this.c = new ComplexNumber();
		this.colorShift = 0.0f;
		colors = logHue;
	}

	public Grapher(double width, double height, ComplexNumber c) {
		this.width = width;
		this.height = height;
		this.centerX = 0;
		this.centerY = 0;
		this.zoom = 0;
		this.pow = new ComplexNumber(2.0, 0);
		this.julia = true;
		this.c = c;
		this.colorShift = 0.0f;
	}

	public String toString() {
		String out = "";
		out += "Aspect ratio: " + width + "x" + height;
		out += "\nCenter: (" + centerX + "," + centerY + ")";
		out += "\nZoom: " + zoom;
		out += "\nPower: " + pow;
		out += "\nJulian: " + julia;
		if (julia) {
			out += "\nc=" + c;
		}
		out += "\nColor shift: " + colorShift;
		return out;
	}

	public int getZoom() {
		return zoom;
	}

	public double getZoomAmount() {
		return Math.exp(zoom / ZOOM_SENSITIVITY);
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public void changeZoom(int change) {
		this.zoom += change;
	}

	public void changeZoomAbout(int change, int x, int y) {
		double zoomX = getGraphX(x);
		double zoomY = getGraphY(y);

	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	/**
	 * * Changes center to screen coordinate
	 * 
	 * @param x The x coordinate on the screen
	 * @param y The y coordinate on the screen
	 */
	public void setCenter(int x, int y) {
		centerX = getGraphX(x);
		centerY = getGraphY(y);
	}

	/**
	 * Sets the center to a graph coordinate x,y
	 * 
	 * @param x The graph x coordinate
	 * @param y The graph y coordinate
	 */
	public void setCenter(double x, double y) {
		centerX = x;
		centerY = y;
	}

	/**
	 * Changes the center by screen delta x and delta y
	 * 
	 * @param x The change in the x coordinate on the screen
	 * @param y The change in the y coordinate on the screen
	 */
	public void changeCener(int dx, int dy) {
		centerX += scaleX(dx);
		centerY -= scaleY(dy);
	}

	public ComplexNumber getPower() {
		return pow;
	}

	public void setPower(ComplexNumber pow) {
		this.pow = pow;
	}

	public void changePower(ComplexNumber change) {
		this.pow.plusEquals(change);
	}

	public void changePower(double a, double b) {
		this.pow.plusEquals(new ComplexNumber(a, b));
	}

	public boolean getJulia() {
		return julia;
	}

	public void setJulia(boolean julian) {
		this.julia = julian;
	}

	public ComplexNumber getC() {
		return c;
	}

	public void setC(ComplexNumber c) {
		this.c = c;
	}

	public void changeC(double a, double b) {
		this.c.plusEquals(new ComplexNumber(a, b));
	}

	public float getColorShift() {
		return colorShift;
	}

	public void setColorShift(float colorShift) {
		this.colorShift = colorShift;
	}

	public void changeColorShift(float change) {
		this.colorShift += change;
	}

	public void setColors(BiFunction<Integer, Integer, Integer> colors) {
		this.colors = colors;
	}

	public int getPixelsCompleated() {
		return pixelsCompleated;
	}

	/**
	 * Scales the x value to the graph
	 * 
	 * @param x Screen x coordinate
	 * @return Graph x coordinate
	 */
	private double scaleX(double x) {
		return x / ((double) WIDTH) * width * getZoomAmount();
	}

	/**
	 * Scales the y value to the graph
	 * 
	 * @param y Screen y coordinate
	 * @return Graph y coordinate
	 */
	private double scaleY(double y) {
		return y / ((double) HEIGHT) * height * getZoomAmount();
	}

	private double getGraphX(int x) {
		double graphX = (double) x - (double) WIDTH / 10.0; // makes 0 at the center
		return scaleX(graphX) + centerX;
	}

	private double getGraphY(int y) {
		double graphY = (double) HEIGHT / 2.0 - (double) y; // makes 0 at the center
		return scaleY(graphY) + centerY;
	}

	public void setPixels(int[] pixels) {
		int x = 0;
		int y = 0;
		int iter = (int) (DEPTH * Math.exp(-zoom / 20.0));
		for (int i = 0; i < pixels.length; i++) {
			x = i % WIDTH;
			y = i / WIDTH;
			ComplexNumber c = new ComplexNumber(getGraphX(x), getGraphY(y));
			// System.out.println(c);
			pixels[i] = isInMandlebrotNum(c, iter);
		}
	}

	public void setPixelsThread(int[] pixels) {
		pixelsCompleated = 0;
		int iter = (int) (DEPTH * Math.exp(-zoom / 20.0));

		if (Display.SAVE_IMG) {
			new Thread(() -> startTimer(pixels)).start();
			System.out.println("Timer Started");
		}

		new Thread(() -> setPixelsTop(pixels, iter)).start();
		setPixelsBottom(pixels, iter);
	}

	private void setPixelsTop(int[] pixels, int iter) {
		int x = 0;
		int y = 0;
		for (int i = 0; i < pixels.length && pixelsCompleated < pixels.length; i++, pixelsCompleated++) {
			x = i % WIDTH;
			y = i / WIDTH;
			ComplexNumber c = new ComplexNumber(getGraphX(x), getGraphY(y));
			pixels[i] = isInMandlebrotNum(c, iter);
		}
	}

	private void setPixelsBottom(int[] pixels, int iter) {
		int x = 0;
		int y = 0;
		for (int i = pixels.length - 1; i >= 0 && pixelsCompleated < pixels.length; i--, pixelsCompleated++) {
			x = i % WIDTH;
			y = i / WIDTH;
			ComplexNumber c = new ComplexNumber(getGraphX(x), getGraphY(y));
			pixels[i] = isInMandlebrotNum(c, iter);
		}
	}

	private void startTimer(int[] pixels) {

		ProgressInfo info = new ProgressInfo(this);
		info.startTimer(pixels.length);
	}

	private boolean isInMandlebrot(ComplexNumber c) {
		// Map<Double, Double> m = new HashMap<Double, Double>();
		ComplexNumber z = new ComplexNumber();
		double sum = 0;
		for (int j = 0; j < DEPTH; j++) {
			z = z.times(z).plus(c);
			sum += z.getDistance();
			if (z.getB() > 5 || z.getB() < -5) {
				return false;
			}
			// m.put(z.getDistance(), 1.0);
		}
		double average1 = sum / (double) DEPTH;
		for (int j = 0; j < DEPTH; j++) {
			z = z.times(z).plus(c);
			sum += z.getDistance();
			if (z.getB() > 5 || z.getB() < -5) {
				return false;
			}
			// m.put(z.getDistance(), 1.0);
		}
		double average2 = sum / (double) DEPTH / 2.0;
		// return m.size() < DEPTH;
		return 5.0E-2 > Math.abs(average2 - average1);
	}

	private int isInMandlebrotNum(ComplexNumber c, int iter) {
		ComplexNumber z = c;
		if (julia) {
			c = this.c;
		}
		int num = 0;
		// System.out.println( DEPTH * Math.exp(-zoom / 10.0));
		for (int i = 0; i < iter; i++) {
			if (complexPow) {
				z = z.pow(2.0).plus(ComplexNumber.exp(ComplexNumber.I.times(this.c.getA())).times(0.7885));
				// z = z.minus((z.pow(3).minus(1)).dividedBy(z.times(z).times(3)));
			} else {
				z = z.times(z).plus(c);
			}
			if (z.getDistance() > 4.0) {
				return colors.apply(num, iter);
			}
			num++;
		}
		if (colors == colorGrayScaleSqrt) {
			return -1;
		}
		return 0;
	}

	public static final BiFunction<Integer, Integer, Integer> colorBlackAndWhite = (n, iter) -> (n % 2) - 1;

	public static final BiFunction<Integer, Integer, Integer> colorGrayScale = (n,
			iter) -> (int) (Math.abs(Math.sin((double) n / DEPTH * 10.0) * 255)) * 0x010101;

	public static final BiFunction<Integer, Integer, Integer> colorGrayScaleSqrt = (n,
			iter) -> (int) (Math.sqrt(n / (double) iter) * 255) * 0x010101;

	public final BiFunction<Integer, Integer, Integer> colorHue = (n, iter) -> Color
			.HSBtoRGB((float) n / DEPTH + colorShift, 1f, 1f);

	public final BiFunction<Integer, Integer, Integer> colorHueConst = (n, iter) -> Color
			.HSBtoRGB((float) n / iter + colorShift, 1f, (float) ((iter - n) / (float) iter));

	public final BiFunction<Integer, Integer, Integer> colorHueSqrt = (n, iter) -> Color.HSBtoRGB(
			(float) Math.sqrt(n / (float) iter) + colorShift, 1f, (float) Math.pow((iter - n) / (float) iter, 3));

	public final BiFunction<Integer, Integer, Integer> logHue = (n, nn) -> Color
			.HSBtoRGB((float) (Math.log(Math.log(n) / 0.693147) / 0.693147)  + colorShift, 1f, 1f);

}
