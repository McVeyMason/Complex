package mandlebrot;

import java.awt.event.KeyEvent;
import java.util.Scanner;

import mandlebrot.display.Display;
import mandlebrot.display.Listener;
import mandlebrot.graph.Grapher;

public class Main {

	private static Display display;
	private static Grapher grapher;
	private static Listener listener;

	public static void main(String[] args) {
		display = new Display();
		grapher = new Grapher(3, 1.94, false);// 1.6875
		listener = display.getListener();

		grapher.setCenter(-1.04883146, -.25168183);
		grapher.setZoom(-94);
		if (Display.SAVE_IMG) {
			// testRenders();
			renderImage();
		} else {
			start();
		}
	}

	public static void renderImage() {
		System.out.println("Image Start");
		grapher.setPixelsThread(display.getPixels());
		display.renderImage(grapher.getCenterX(), grapher.getCenterY(), 1.0 / grapher.getZoomAmount(), grapher);
		System.out.println(grapher.getZoom());
	}

	public static void start() {
		new Thread(() -> {
			while (true) {
				// grapher.setPixels(display.getPixels());
				grapher.setPixelsThread(display.getPixels());
			}
		}).start();

		new Thread(() -> {
			Scanner scanner = new Scanner(System.in);
			while (true) {
				boolean printInfo = scanner.nextBoolean();
				if (printInfo) {
					System.out.println(grapher);
				}
			}
		}).start();
		// grapher.setPixels(display.getPixels());

		long lastTime = System.nanoTime();
		while (true) {
			if ((System.nanoTime() - lastTime) / 1.0E9 > 1 / 60.0) {
				lastTime = System.nanoTime();
				display.render();
				processKeys();
			}
			// System.out.println("graphed");
//			if (display.getListener().getMousePressed()) {
//				int dx = display.getListener().getXDiff();
//				int dy = display.getListener().getYDiff();
//				display.translatePixels(dx, dy);
//				grapher.changeAener(-dx, -dy);
//				display.getListener().clearDiff();
//				//System.out.println("mousePressed");
//			}
//			if (display.getListener().isScrolling()) {
//				grapher.changeZoom(displayWrapper.get(0).getListener().getScollAmount());
//				display.getListener().clearScrollAmount();
//			}
		}
	}

	public static void processMouseMovement(int dx, int dy) {
		display.translatePixels(dx, dy);
		grapher.changeCener(-dx, -dy);
	}

	public static void processScroll(int clicks) {
		grapher.changeZoom(clicks);
	}

	/**
	 * This method processes key inputs at 60 FPS. The controls are as follows:<br>
	 * p = save screen shot, <br>
	 * F1 = change to Mandelbrot set, <br>
	 * F2 = change to julia set, <br>
	 * Arrow keys = change the c value for julia sets,<br>
	 * WASD = change the power,<br>
	 * Page up/down = change the color shift<br>
	 */
	private static void processKeys() {
		synchronized (grapher) {
			if (listener.keys[KeyEvent.VK_P]) {
				new Thread(() -> renderImage()).start();
				listener.keys[KeyEvent.VK_P] = false;
			}
			// changes julia c with arrow keys
			if (listener.keys[KeyEvent.VK_SHIFT]) {
				if (listener.keys[KeyEvent.VK_CONTROL]) {
					// Changing c value in the julia set
					if (listener.keys[KeyEvent.VK_UP]) {
						grapher.changeJuliaIm(0.00001);
					}
					if (listener.keys[KeyEvent.VK_DOWN]) {
						grapher.changeJuliaIm(-0.00001);
					}
					if (listener.keys[KeyEvent.VK_RIGHT]) {
						grapher.changeJuliaRe(0.00001);
					}
					if (listener.keys[KeyEvent.VK_LEFT]) {
						grapher.changeJuliaRe(-0.00001);
					}
				} else {
					// Changing c value in the julia set
					if (listener.keys[KeyEvent.VK_UP]) {
						grapher.changeJuliaIm(0.001);
					}
					if (listener.keys[KeyEvent.VK_DOWN]) {
						grapher.changeJuliaIm(-0.001);
					}
					if (listener.keys[KeyEvent.VK_RIGHT]) {
						grapher.changeJuliaRe(0.001);
					}
					if (listener.keys[KeyEvent.VK_LEFT]) {
						grapher.changeJuliaRe(-0.001);
					}
				}
			} else if (listener.keys[KeyEvent.VK_CONTROL]) {
				// Changing c value in the julia set
				if (listener.keys[KeyEvent.VK_UP]) {
					grapher.changeJuliaIm(0.0001);
				}
				if (listener.keys[KeyEvent.VK_DOWN]) {
					grapher.changeJuliaIm(-0.0001);
				}
				if (listener.keys[KeyEvent.VK_RIGHT]) {
					grapher.changeJuliaRe(-0.0001);
				}
				if (listener.keys[KeyEvent.VK_LEFT]) {
					grapher.changeJuliaRe(-0.0001);
				}
			} else {
				// Changing c value in the julia set
				if (listener.keys[KeyEvent.VK_UP]) {
					grapher.changeJuliaIm(0.01);
				}
				if (listener.keys[KeyEvent.VK_DOWN]) {
					grapher.changeJuliaIm(-0.01);
				}
			}
			if (listener.keys[KeyEvent.VK_F1]) {
				grapher.isJulia = false;
			}
			if (listener.keys[KeyEvent.VK_F2]) {
				grapher.isJulia = true;
				grapher.setJuliaToMandlbrot();
			}
		}
	}

	private static void mapArray(int[][] ints, int[] to) {
		int index = 0;
		for (int y = 0; y < ints[0].length; y++) {
			for (int x = 0; x < ints.length; x++) {
				to[index] = ints[x][y];
			}
		}
	}

}
