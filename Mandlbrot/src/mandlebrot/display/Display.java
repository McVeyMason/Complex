package mandlebrot.display;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Display extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3427381498790975542L;

	public static final boolean SAVE_IMG = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 135;
	private static final String TITLE = "Mandlebrot";

	private JFrame jFrame;
	private BufferedImage img;
	private int[] pixels;
	Listener listener;

	public Display() {
		this.jFrame = new JFrame();
		this.img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.pixels = ((DataBufferInt) this.img.getRaster().getDataBuffer()).getData();
		this.listener = new Listener();
		if (!SAVE_IMG) {
			addMouseListener(listener);
			addKeyListener(listener);
			addFocusListener(listener);
			addMouseMotionListener(listener);
			addMouseWheelListener(listener);

			jFrame.add(this);
			jFrame.setTitle(TITLE);
			jFrame.setLocationRelativeTo(null);
			jFrame.setLocation(0, 0);
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
			jFrame.setResizable(false);
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setVisible(true);
			jFrame.pack();
		}
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.drawImage(img, 0, 0, WIDTH, HEIGHT, null);
		g.dispose();
		bs.show();
	}

	public void renderImage(double x, double y, double zoomAmount) {
//		BufferStrategy bs = this.getBufferStrategy();
//		if (bs == null) {
//			createBufferStrategy(3);
//			return;
//		}
		Graphics g = img.createGraphics();
		g.drawImage(img, 0, 0, WIDTH, HEIGHT, null);
		g.dispose();
		File file = new File("generated\\mandlbrot" + "(" + x + "," + y + ")" +  +  zoomAmount + "x" + WIDTH + "x" + HEIGHT+ ".png");
		System.out.println(file.getAbsolutePath());
		try {
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}

	public int[] getPixels() {
		return pixels;
	}

	public Listener getListener() {
		return listener;
	}

	public void translatePixels(int dx, int dy) {
		int currentPixel, translatedPixel;
		if (dx > 0) {
			for (int x = WIDTH - dx - 1; x >= 0; x--) {
				if (dy > 0) {
					for (int y = HEIGHT - dy - 1; y >= 0; y--) {
						currentPixel = x + y * WIDTH;
						translatedPixel = x + dx + (y + dy) * WIDTH;
						pixels[translatedPixel] = pixels[currentPixel];
					}
				} else {
					for (int y = -dy; y < HEIGHT; y++) {
						currentPixel = x + y * WIDTH;
						translatedPixel = x + dx + (y + dy) * WIDTH;
						pixels[translatedPixel] = pixels[currentPixel];
					}
				}
			}
		} else {
			for (int x = -dx; x < WIDTH; x++) {
				if (dy > 0) {
					for (int y = HEIGHT - dy - 1; y >= 0; y--) {
						currentPixel = x + y * WIDTH;
						translatedPixel = x + dx + (y + dy) * WIDTH;
						pixels[translatedPixel] = pixels[currentPixel];
					}
				} else {
					for (int y = -dy; y < HEIGHT; y++) {
						currentPixel = x + y * WIDTH;
						translatedPixel = x + dx + (y + dy) * WIDTH;
						pixels[translatedPixel] = pixels[currentPixel];
					}
				}
			}
		}
	}
}
