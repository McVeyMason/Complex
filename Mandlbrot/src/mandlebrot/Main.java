package mandlebrot;

import java.awt.event.KeyEvent;
import java.util.Scanner;

import mandlebrot.complex.ComplexNumber;
import mandlebrot.display.Display;
import mandlebrot.display.Listener;
import mandlebrot.graph.Grapher;
import mandlebrot.graph.Grapher.ColorStyle;

public class Main {

	private static Display display;
	private static Grapher grapher;
	private static Listener listener;

	public static void main(String[] args) {
		display = new Display();
		grapher = new Grapher(16, 9);
		listener = display.getListener();
		grapher.setCenter(-0.6423704543370716, 0.1687860153558362);
		grapher.setZoom(-35);
		grapher.setPower(new ComplexNumber(2, 0));
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
		display.renderImage(grapher.getCenterX(), grapher.getCenterY(), 1.0 / grapher.getZoomAmount());
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

	private static void processKeys() {
		if (listener.keys[KeyEvent.VK_P]) {
			new Thread(() -> renderImage()).start();
			listener.keys[KeyEvent.VK_P] = false;
		}
		if (listener.keys[KeyEvent.VK_F1]) {
			grapher.setJulian(false);
		}
		if (listener.keys[KeyEvent.VK_F2]) {
			grapher.setJulian(true);
		}

		// changes julia c with arrow keys
		if (grapher.getJulian()) {
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
					grapher.changePower(0, 0.0001);
				}
				if (listener.keys[KeyEvent.VK_S]) {
					grapher.changePower(0, -0.0001);
				}
				if (listener.keys[KeyEvent.VK_D]) {
					grapher.changePower(0.0001, 0);
				}
				if (listener.keys[KeyEvent.VK_A]) {
					grapher.changePower(-0.0001, 0);
				}
			} else {
				// Changing the power for the set
				if (listener.keys[KeyEvent.VK_W]) {
					grapher.changePower(0, 0.01);
				}
				if (listener.keys[KeyEvent.VK_S]) {
					grapher.changePower(0, -0.01);
				}
				if (listener.keys[KeyEvent.VK_D]) {
					grapher.changePower(0.01, 0);
				}
				if (listener.keys[KeyEvent.VK_A]) {
					grapher.changePower(-0.01, 0);
				}
			}
		} else if (listener.keys[KeyEvent.VK_CONTROL]) {
			// Changing the power for the set
			if (listener.keys[KeyEvent.VK_W]) {
				grapher.changePower(0, 0.001);
			}
			if (listener.keys[KeyEvent.VK_S]) {
				grapher.changePower(0, -0.001);
			}
			if (listener.keys[KeyEvent.VK_D]) {
				grapher.changePower(0.001, 0);
			}
			if (listener.keys[KeyEvent.VK_A]) {
				grapher.changePower(-0.001, 0);
			}
		} else {
			// Changing the power for the set
			if (listener.keys[KeyEvent.VK_W]) {

				System.out.println("VK_W");
				grapher.changePower(0, 0.1);
			}
			if (listener.keys[KeyEvent.VK_S]) {
				grapher.changePower(0, -0.1);
			}
			if (listener.keys[KeyEvent.VK_D]) {
				grapher.changePower(0.1, 0);
			}
			if (listener.keys[KeyEvent.VK_A]) {
				grapher.changePower(-0.1, 0);
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
			grapher.setColors(ColorStyle.BLACK_AND_WHITE);
		}
		if (listener.keys[KeyEvent.VK_NUMPAD1]) {
			grapher.setColors(ColorStyle.GRAY_SCALE);
		}
		if (listener.keys[KeyEvent.VK_NUMPAD2]) {
			grapher.setColors(ColorStyle.LINEAR_HUE_WRAP);
		}
		if (listener.keys[KeyEvent.VK_NUMPAD3]) {
			grapher.setColors(ColorStyle.LINEAR_HUE);
		}
		if (listener.keys[KeyEvent.VK_NUMPAD4]) {
			grapher.setColors(ColorStyle.SQRT_HUE);
		}
	}

}
