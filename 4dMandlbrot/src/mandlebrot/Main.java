package mandlebrot;

import java.awt.event.KeyEvent;
import java.util.Scanner;

import mandlebrot.complex.ComplexNumber;
import mandlebrot.display.Display;
import mandlebrot.display.Listener;
import mandlebrot.graph.Grapher;

public class Main {

	private static Display display;
	private static Grapher grapher;
	private static Listener listener;

	public static void main(String[] args) {
		display = new Display();
		grapher = new Grapher(16, 9);
		listener = display.getListener();
		
		grapher.setColors(grapher.colorHueSqrt);

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
	}

	public static void testRenders() {
		long lastTime = System.nanoTime();
		for (int i = 0; i < 10; i++) {
			grapher.setPixels(display.getPixels());
		}
		double elapsedNoThread = (System.nanoTime() - lastTime) / 1.0E9;
		lastTime = System.nanoTime();
		for (int i = 0; i < 10; i++) {
			grapher.setPixelsThread(display.getPixels());
		}
		double elapsedThread = (System.nanoTime() - lastTime) / 1.0E9;
		System.out.println(elapsedNoThread + " vs " + elapsedThread);
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
//				grapher.changeCener(-dx, -dy);
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
		if (listener.keys[KeyEvent.VK_P]) {
			new Thread(() -> renderImage()).start();
			listener.keys[KeyEvent.VK_P] = false;
		}
		if (listener.keys[KeyEvent.VK_F1]) {
			grapher.setJulia(false);
		}
		if (listener.keys[KeyEvent.VK_F2]) {
			grapher.setJulia(true);
		}

		// changes julia c with arrow keys
		if (grapher.getJulia()) {
			if (listener.keys[KeyEvent.VK_SHIFT]) {
				if (listener.keys[KeyEvent.VK_CONTROL]) {
					// Changing c value in the julia set
					if (listener.keys[KeyEvent.VK_UP]) {
						grapher.changeC(0, 0.00001);
					}
					if (listener.keys[KeyEvent.VK_DOWN]) {
						grapher.changeC(0, -0.00001);
					}
					if (listener.keys[KeyEvent.VK_RIGHT]) {
						grapher.changeC(0.00001, 0);
					}
					if (listener.keys[KeyEvent.VK_LEFT]) {
						grapher.changeC(-0.00001, 0);
					}
				} else {
					// Changing c value in the julia set
					if (listener.keys[KeyEvent.VK_UP]) {
						grapher.changeC(0, 0.001);
					}
					if (listener.keys[KeyEvent.VK_DOWN]) {
						grapher.changeC(0, -0.001);
					}
					if (listener.keys[KeyEvent.VK_RIGHT]) {
						grapher.changeC(0.001, 0);
					}
					if (listener.keys[KeyEvent.VK_LEFT]) {
						grapher.changeC(-0.001, 0);
					}
				}
			} else if (listener.keys[KeyEvent.VK_CONTROL]) {
				// Changing c value in the julia set
				if (listener.keys[KeyEvent.VK_UP]) {
					grapher.changeC(0, 0.0001);
				}
				if (listener.keys[KeyEvent.VK_DOWN]) {
					grapher.changeC(0, -0.0001);
				}
				if (listener.keys[KeyEvent.VK_RIGHT]) {
					grapher.changeC(0.0001, 0);
				}
				if (listener.keys[KeyEvent.VK_LEFT]) {
					grapher.changeC(-0.0001, 0);
				}
			} else {
				// Changing c value in the julia set
				if (listener.keys[KeyEvent.VK_UP]) {
					System.out.println(grapher.getC());
					grapher.changeC(0, 0.01);
				}
				if (listener.keys[KeyEvent.VK_DOWN]) {
					grapher.changeC(0, -0.01);
				}
				if (listener.keys[KeyEvent.VK_RIGHT]) {
					grapher.changeC(0.01, 0);
				}
				if (listener.keys[KeyEvent.VK_LEFT]) {
					grapher.changeC(-0.01, 0);
				}
			}
		}

		// Changes the exponent with wasd
		if (listener.keys[KeyEvent.VK_SHIFT]) {
			if (listener.keys[KeyEvent.VK_CONTROL]) {
				// Changing the power for the set
				if (listener.keys[KeyEvent.VK_W]) {
					grapher.changeJ(0.0001);
				}
				if (listener.keys[KeyEvent.VK_S]) {
					grapher.changeJ(-0.0001);
				}
				if (listener.keys[KeyEvent.VK_D]) {
					grapher.changeK(0.0001);
				}
				if (listener.keys[KeyEvent.VK_A]) {
					grapher.changeK(-0.0001);
				}
			} else {
				// Changing the power for the set
				if (listener.keys[KeyEvent.VK_W]) {
					grapher.changeJ(0.01);
				}
				if (listener.keys[KeyEvent.VK_S]) {
					grapher.changeJ(-0.01);
				}
				if (listener.keys[KeyEvent.VK_D]) {
					grapher.changeK(0.01);
				}
				if (listener.keys[KeyEvent.VK_A]) {
					grapher.changeK(-0.01);
				}
			}
		} else if (listener.keys[KeyEvent.VK_CONTROL]) {
			// Changing the power for the set
			if (listener.keys[KeyEvent.VK_W]) {
				grapher.changeJ(0.001);
			}
			if (listener.keys[KeyEvent.VK_S]) {
				grapher.changeJ(-0.001);
			}
			if (listener.keys[KeyEvent.VK_D]) {
				grapher.changeK(0.001);
			}
			if (listener.keys[KeyEvent.VK_A]) {
				grapher.changeK(-0.001);
			}
		} else {
			// Changing the power for the set
			if (listener.keys[KeyEvent.VK_W]) {
				grapher.changeJ(0.1);
			}
			if (listener.keys[KeyEvent.VK_S]) {
				grapher.changeJ(-0.1);
			}
			if (listener.keys[KeyEvent.VK_D]) {
				grapher.changeK(0.1);
			}
			if (listener.keys[KeyEvent.VK_A]) {
				grapher.changeK(-0.1);
			}
		}

		// Shifts the color spectrum with page up and page down
		if (listener.keys[KeyEvent.VK_PAGE_UP]) {
			grapher.changeColorShift(0.005f);
		}
		if (listener.keys[KeyEvent.VK_PAGE_DOWN]) {
			grapher.changeColorShift(-0.005f);
		}

		// Changes color pallet with numpad
		if (listener.keys[KeyEvent.VK_NUMPAD0]) {
			grapher.setColors(Grapher.colorBlackAndWhite);
		}
		if (listener.keys[KeyEvent.VK_NUMPAD1]) {
			grapher.setColors(Grapher.colorGrayScale);
		}
		if (listener.keys[KeyEvent.VK_NUMPAD2]) {
			grapher.setColors(grapher.colorHue);
		}
		if (listener.keys[KeyEvent.VK_NUMPAD3]) {
			grapher.setColors(grapher.colorHueConst);
		}
		if (listener.keys[KeyEvent.VK_NUMPAD4]) {
			grapher.setColors(grapher.colorHueSqrt);
		}
	}

}