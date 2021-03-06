package mandlebrot.graph;

import static mandlebrot.display.Display.HEIGHT;
import static mandlebrot.display.Display.WIDTH;

import java.awt.Color;
import java.time.LocalTime;
import java.util.Timer;

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

	private ComplexNumber pow;

	private boolean julian;
	private ComplexNumber c;

	private int pixelsCompleated;

	private float colorShift;
	private ColorStyle colors;

	public Grapher(double width, double height) {
		this.width = width;
		this.height = height;
		this.centerX = 0;
		this.centerY = 0;
		this.zoom = 0;
		this.pow = new ComplexNumber(2.0, 0);
		this.julian = false;
		this.c = new ComplexNumber();
		this.colorShift = 0.0f;
		colors = ColorStyle.SQRT_HUE;
	}

	public Grapher(double width, double height, ComplexNumber c) {
		this.width = width;
		this.height = height;
		this.centerX = 0;
		this.centerY = 0;
		this.zoom = 0;
		this.pow = new ComplexNumber(2.0, 0);
		this.julian = true;
		this.c = c;
		this.colorShift = 0.0f;
	}

	public String toString() {
		String out = "";
		out += "Aspect ratio: " + width + "x" + height;
		out += "\nCenter: (" + centerX + "," + centerY + ")";
		out += "\nZoom: " + zoom;
		out += "\nPower: " + pow;
		out += "\nJulian: " + julian;
		if (julian) {
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

	public boolean getJulian() {
		return julian;
	}

	public void setJulian(boolean julian) {
		this.julian = julian;
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
	
	public void setColors(ColorStyle colors) {
		this.colors = colors;
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
		int percent = pixels.length / 100;
		long firstTime = System.nanoTime();
		long lastTime = System.nanoTime();
		long currentTime = System.nanoTime();
		for (int i = 0; i < pixels.length; i++) {
			x = i % WIDTH;
			y = i / WIDTH;
			ComplexNumber c = new ComplexNumber(getGraphX(x), getGraphY(y));
			// System.out.println(c);
			pixels[i] = isInMandlebrotNum(c);
			if (Display.SAVE_IMG) {
				if (i % percent == 0 && i != 0) {
					currentTime = System.nanoTime();
					LocalTime t = LocalTime.now();
					double elapsed = (currentTime - firstTime) / 1.0E9;
					double elapsedPercent = (currentTime - lastTime) / 1.0E9;
					double estimateAve = elapsed / ((double) i) * (pixels.length - i);
					double estimatePercent = elapsedPercent / (double) percent * (pixels.length - i);

					String out = "";
					out += "\n" + t;
					out += "\n" + (i / (double) pixels.length * 100.0) + "%";
					out += "\nTotal time elapsed: " + toTime(elapsed);
					out += "\nTime for percent: " + toTime(elapsedPercent);
					out += "\nEstimated by average: " + toTime(estimateAve);
					out += "\nEstimate by percent: " + toTime(estimatePercent);
					System.out.println(out);

					lastTime = currentTime;
				}
			}
		}
	}

	public void setPixelsThread(int[] pixels) {
		pixelsCompleated = 0;
		if (Display.SAVE_IMG) {
			new Thread(() -> startTimer(pixels)).start();
			System.out.println("Timer Started");
		}
		new Thread(() -> setPixelsTop(pixels)).start();
		setPixelsBottom(pixels);
	}

	private void setPixelsTop(int[] pixels) {
		int x = 0;
		int y = 0;
		for (int i = 0; i < pixels.length && pixelsCompleated < pixels.length; i++, pixelsCompleated++) {
			x = i % WIDTH;
			y = i / WIDTH;
			ComplexNumber c = new ComplexNumber(getGraphX(x), getGraphY(y));
			pixels[i] = isInMandlebrotNum(c);
		}
	}

	private void setPixelsBottom(int[] pixels) {
		int x = 0;
		int y = 0;
		for (int i = pixels.length - 1; i >= 0 && pixelsCompleated < pixels.length; i--, pixelsCompleated++) {
			x = i % WIDTH;
			y = i / WIDTH;
			ComplexNumber c = new ComplexNumber(getGraphX(x), getGraphY(y));
			pixels[i] = isInMandlebrotNum(c);
		}
	}

	private void startTimer(int[] pixels) {

		long firstTime = System.nanoTime();
		long lastTime = System.nanoTime();
		long currentTime = System.nanoTime();
		int lastCompleated = 0;
		long sleepTime = (long) ((pixels.length / 25000.0 / 100.0) * 1000.0);
		System.out.println(
				"Sleep Time is " + sleepTime / 1000.0 + "\nFirst estimate is " + (pixels.length / 25000.0 / 100.0));
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (pixelsCompleated < pixels.length) {
			currentTime = System.nanoTime();
			LocalTime t = LocalTime.now();
			int pixelsProcessed = (pixelsCompleated - lastCompleated);
			double percentProcessed = pixelsProcessed / (double) pixels.length;
			double elapsed = (currentTime - firstTime) / 1.0E9;
			double elapsedPercent = (currentTime - lastTime) / 1.0E9;
			double estimateAve = elapsed / ((double) pixelsCompleated) * (pixels.length - pixelsCompleated);
			double estimatePercent = elapsedPercent / (double) pixelsProcessed * (pixels.length - pixelsCompleated);

			String out = "";
			out += "\n" + t;
			out += "\n" + (pixelsCompleated / (double) pixels.length * 100.0) + "%";
			out += "\nTotal time elapsed: " + toTime(elapsed);
			out += "\nTime for " + percentProcessed + "%: " + toTime(elapsedPercent);
			out += "\nEstimated by average: " + toTime(estimateAve);
			out += "\nEstimate by last: " + toTime(estimatePercent);
			System.out.println(out);

			lastTime = currentTime;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static String toTime(double t) {
		if (t > 60.0) {
			t /= 60.0;
			if (t > 60.0) {
				t /= 60.0;
				return t + "h";
			} else {
				return t + "m";
			}
		} else {
			return t + "s";
		}
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

	private int isInMandlebrotNum(ComplexNumber c) {
		ComplexNumber z = c;
		if (julian) {
			c = this.c;
		}
		int num = 0;
		// System.out.println( DEPTH * Math.exp(-zoom / 10.0));
		int iter = (int) (DEPTH * Math.exp(-zoom / 20.0));
		for (int i = 0; i < iter; i++) {
			z = z.pow(pow).plus(c);
			if (z.getDistance() > 4.0) {
				return color(num, iter);
			}
			num++;
		}
		return 0;
	}

	public enum ColorStyle {
		BLACK_AND_WHITE, GRAY_SCALE, LINEAR_HUE_WRAP, LINEAR_HUE, SQRT_HUE
	}

	private int color(int n, int iter) {
		switch (colors) {
		case BLACK_AND_WHITE:
			return colorBlackAndWhite(n);
		case GRAY_SCALE:
			return colorGrayScale(n);
		case LINEAR_HUE_WRAP:
			return colorHue(n, iter);
		case LINEAR_HUE:
			return colorHueConst(n, iter);
		case SQRT_HUE:
			return colorHueSqrt(n, iter);
		default:
			return 0;
		}

	}

	private static int colorBlackAndWhite(int n) {
		return (n % 2) - 1;
	}

	private static int colorGrayScale(int n) {
		return (int) (Math.abs(Math.sin((double) n / DEPTH * 10.0) * 255)) * 0x010101;
	}

	private static int colorHue(int n, int iter) {
		return Color.HSBtoRGB((float) n / DEPTH, 1f, 1f);
		// return Color.HSBtoRGB((float) Math.sqrt((float) n / DEPTH), 1f, 1f);
	}

	private int colorHueConst(int n, int iter) {
		return Color.HSBtoRGB((float) n / iter + colorShift, 1f, (float) ((iter - n) / (float) iter));
	}

	private int colorHueSqrt(int n, int iter) {
		return Color.HSBtoRGB((float) Math.sqrt(n / (float) iter) + colorShift, 1f,
				(float) Math.pow((iter - n) / (float) iter, 3));
	}

}
