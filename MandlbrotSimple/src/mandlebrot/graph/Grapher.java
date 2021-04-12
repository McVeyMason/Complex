package mandlebrot.graph;

import static mandlebrot.display.Display.HEIGHT;
import static mandlebrot.display.Display.WIDTH;

import java.awt.Color;
import java.util.function.BiFunction;
import java.util.function.Function;

import mandlebrot.display.Display;

public class Grapher {

	private static final int DEPTH = 1_000;
	private static final double ZOOM_SENSITIVITY = 5.0;

	private static final int[] PALLET1 = { 0xa09ebb, 0xa8aec1, 0xb5d2cb, 0xbfffbc, 0xA6ffa1 };
	private static final int[] PALLET2 = { 0x0061FF, 0x60EFFF };
	private static final int[] PALLET3 = { 0x98CE00, 0x16E0BD, 0x78C3FB, 0x89A6FB, 0x98838F };
	private static final int[] PALLET4 = { 0x03071E, 0x370617, 0x6A040F, 0x9D0208, 0xD00000, 0xDC2F02, 0xE85D04,
			0xF48C06, 0xFAA307, 0xFFBA08 };
	private static final int[] PALLET4_MIRROR = { 0x03071E, 0x370617, 0x6A040F, 0x9D0208, 0xD00000, 0xDC2F02, 0xE85D04,
			0xF48C06, 0xFAA307, 0xFFBA08, 0xFAA307, 0xF48C06, 0xE85D04, 0xDC2F02, 0xD00000, 0x9D0208, 0x6A040F,
			0x370617 };
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

	private double x0;
	private double y0;
	private double a = 2.9825000000000434;

	private int numThreads = 30;
	private boolean[] threadsDone;

	private int pixelsCompleated;

	private float colorShift;

	private Color[] colors;
	private int[] colorsRGB;

	public Grapher(double width, double height) {
		this.width = width;
		this.height = height;
		this.centerX = 0;
		this.centerY = 0;
		this.zoom = 0;
		generateC();
		// colors = generateColors(2048);
		// colors = generateColorsGrey(2048);
		colors = generateColorsPallet(2048, PALLET4_MIRROR);
		colorsRGB = new int[colors.length];
		for (int i = 0; i < colors.length; i++) {
			colorsRGB[i] = colors[i].getRGB();
		}
	}

	public String toString() {
		String out = "";
		out += "Aspect ratio: " + width + "x" + height;
		out += "\nCenter: (" + centerX + "," + centerY + ")";
		out += "\nZoom: " + zoom;
		out += "\nColor shift: " + colorShift;
		out += "\na = " + a;
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

	public float getColorShift() {
		return colorShift;
	}

	public void setColorShift(float colorShift) {
		this.colorShift = colorShift;
	}

	public void changeColorShift(float change) {
		this.colorShift += change;
	}

	public int getPixelsCompleated() {
		return pixelsCompleated;
	}

	public void changeA(double da) {
		a += da;
		generateC();
	}

	private void generateC() {
		x0 = 0.7885 * Math.cos(a);
		y0 = 0.7885 * Math.sin(a);
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

	public void setPixelsThread(int[] pixels) {
		pixelsCompleated = 0;
		threadsDone = new boolean[numThreads];
		Thread timer = new Thread(() -> startTimer(pixels));
		if (Display.SAVE_IMG) {
			timer.start();
			System.out.println("Timer Started");
		}
		for (int i = 0; i < numThreads; i++) {
			final int j = i;
			new Thread(() -> setPixels(pixels, j)).start();
		}
		while (Display.SAVE_IMG) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			boolean done = true;
			for (int i = 0; i < numThreads; i++) {
				if (threadsDone[i] == false) {
					done = false;
					break;
				}
			}
			if (done) {
				// timer.stop();
				System.out.println("done rendering");
				break;
			}
		}

	}

	private void setPixels(int[] pixels, int num) {
		for (int y = num; y < HEIGHT; y += numThreads) {
			for (int x = 0; x < WIDTH; x++) {
				pixels[y * WIDTH + x] = isInMandlebrotNum(getGraphX(x), getGraphY(y));
				pixelsCompleated++;
//				if (num == 0 && x == 0) {
//					System.out.println(y);
//					System.out.println(pixels[y * WIDTH + x]);
//				}
			}
//			if (num == 0) {
//				System.out.println(y);
//			}
		}
		threadsDone[num] = true;
	}

	private void startTimer(int[] pixels) {

		ProgressInfo info = new ProgressInfo(this);
		info.startTimer(pixels.length, threadsDone);
	}

	private int isInMandlebrotNum(double x0, double y0) {

		double x = 0.0, y = 0.0;
		double iteration = 0;
		int maxIteration = 5000;

		while (x * x + y * y <= 0x10000000 && iteration < maxIteration) {
			double xTemp = x * x - y * y + x0;
			y = 2 * x * y + y0;
			x = xTemp;
			iteration++;
		}
		if (iteration < maxIteration) {
			double logz = Math.log(x * x + y * y) / 2.0;
			double nu = Math.log(logz / Math.log(2)) / Math.log(2);
			iteration = iteration + 1 - nu;
		} else {
			return 0;
		}
		int colorI = (int) (Math.sqrt(iteration) * 256) % 2048;
		return colorsRGB[colorI];
		// return Color.HSBtoRGB((float) (Math.sqrt(iteration / maxIteration) * 4f), 1f,
		// 1f);
	}

	/**
	 * Position = 0.0 Color = ( 0, 7, 100) <br>
	 * Position = 0.16 Color = ( 32, 107, 203)<br>
	 * Position = 0.42 Color = (237, 255, 255) <br>
	 * Position = 0.6425 Color = (255, 170, 0) <br>
	 * Position = 0.8575 Color = ( 0, 2, 0)
	 * 
	 * @param length
	 * @return
	 */
	private Color[] generateColors(int length) {
		Color[] out = new Color[length];
		for (int i = 0; i < length; i++) {
			double pos = i / (double) length;
			if (0.0 <= pos && pos < 0.16) {
				double r = 32.0 / 0.16 * (pos - 0.0) + 0;
				double g = 100.0 / 0.16 * (pos - 0.0) + 7;
				double b = 103.0 / 0.16 * (pos - 0.0) + 100;
				out[i] = new Color((int) r, (int) g, (int) b);
			} else if (0.16 <= pos && pos < 0.42) {
				double r = 205.0 / 0.26 * (pos - 0.16) + 32;
				double g = 148.0 / 0.26 * (pos - 0.16) + 107;
				double b = 52.0 / 0.26 * (pos - 0.16) + 203;
				out[i] = new Color((int) r, (int) g, (int) b);
			} else if (0.42 <= pos && pos < 0.6425) {
				double r = 18.0 / 0.2225 * (pos - 0.42) + 237;
				double g = -85.0 / 0.2225 * (pos - 0.42) + 255;
				double b = -255.0 / 0.2225 * (pos - 0.42) + 255;
				out[i] = new Color((int) r, (int) g, (int) b);
			} else if (0.6425 <= pos && pos < 0.8575) {
				double r = -255.0 / 0.215 * (pos - 0.6425) + 255;
				double g = -168.0 / 0.215 * (pos - 0.6425) + 170;
				double b = 0.0 / 0.215 * (pos - 0.6425) + 0;
				out[i] = new Color((int) r, (int) g, (int) b);
			} else {
				double r = 0.0 / 0.1425 * (pos - 0.8575) + 0;
				double g = 5.0 / 0.1425 * (pos - 0.8575) + 2;
				double b = 100.0 / 0.1425 * (pos - 0.8575) + 0;
				out[i] = new Color((int) r, (int) g, (int) b);
			}
		}
		return out;
	}

	private Color[] generateColorsGrey(int length) {
		Color[] out = new Color[length];
		for (int i = 0; i < length; i++) {
			double pos = i / (double) length;
			if (0.0 <= pos && pos < 0.5) {
				double r = 255.0 / 0.5 * (pos - 0.0) + 0;
				double g = 255.0 / 0.5 * (pos - 0.0) + 0;
				double b = 255.0 / 0.5 * (pos - 0.0) + 0;
				out[i] = new Color((int) r, (int) g, (int) b);
			} else if (0.5 <= pos && pos < 1.0) {
				double r = -255.0 / 0.5 * (pos - 0.5) + 255;
				double g = -255.0 / 0.5 * (pos - 0.5) + 255;
				double b = -255.0 / 0.5 * (pos - 0.5) + 255;
				out[i] = new Color((int) r, (int) g, (int) b);
			}
		}
		return out;
	}

	public static Color[] generateColorsPallet(int length, int[] pallet) {
		Color[] out = new Color[length];
		for (int i = 0; i < length; i++) {
			double pos = i / (double) length;
			for (int j = 0; j < pallet.length; j++) {
				if (j / pallet.length <= pos && pos < (j + 1.0) / pallet.length) {
					int color = pallet[(int) j];
					int colorNext = (j + 1 == pallet.length) ? pallet[0] : pallet[j + 1];
					double b = (colorNext % 0x100 - color % 0x100) * (double) pallet.length
							* (pos - j / (double) pallet.length) + (color % 0x100);
					color /= 0x100;
					colorNext /= 0x100;
					double g = (colorNext % 0x100 - color % 0x100) * (double) pallet.length
							* (pos - j / (double) pallet.length) + (color % 0x100);
					color /= 0x100;
					colorNext /= 0x100;
					double r = (colorNext % 0x100 - color % 0x100) * (double) pallet.length
							* (pos - j / (double) pallet.length) + (color % 0x100);
					out[i] = new Color((int) r, (int) g, (int) b);
					break;
				}
			}
		}
		return out;
	}

}