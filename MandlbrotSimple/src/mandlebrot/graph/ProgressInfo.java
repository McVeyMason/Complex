package mandlebrot.graph;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class ProgressInfo extends Canvas {

	private static final int WIDTH = 200;
	private static final int HEIGHT = 100;

	private static final long serialVersionUID = -6880647534477952882L;

	private JFrame jFrame;
	private Grapher grapher;

	public ProgressInfo(Grapher grapher) {
		this.jFrame = new JFrame();
		this.grapher = grapher;

		jFrame.add(this);
		jFrame.setTitle("Progress");
		jFrame.setLocationRelativeTo(null);
		jFrame.setLocation(0, 0);
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		jFrame.setResizable(true);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setVisible(true);
		jFrame.pack();
		this.setBackground(Color.white);
	}

	public void startTimer(int total, boolean[] threads) {
		long firstTime = System.nanoTime();
		long lastTime = System.nanoTime();
		long currentTime = System.nanoTime();
		int lastCompleated = 0;
		long sleepTime = 500l;
		while (grapher.getPixelsCompleated() < total) {
			currentTime = System.nanoTime();
			double elapsed = (currentTime - firstTime) / 1.0E9;
			double percentProcessed = grapher.getPixelsCompleated() / (double) total * 100.0;

			lastTime = System.nanoTime();
			lastCompleated = countThreads(threads);

			
			String out = "";
			out += "\n" + (grapher.getPixelsCompleated() / (double) total * 100.0) + "%";
			out += "\nTotal time elapsed: " + toTime(elapsed);
			// System.out.println(out);
			render(percentProcessed, elapsed, lastCompleated, threads.length);

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (lastCompleated == threads.length) {
				break;
			}
		}
	}
	
	private static int countThreads(boolean[] threads) {
		int count = 0;
		for (boolean b : threads) {
			if (b)
				count++;
		}
		return count;
	}

	private static String toTime(double timeElapsed) {
		if (timeElapsed > 60.0) {
			timeElapsed /= 60.0;
			if (timeElapsed > 60.0) {
				timeElapsed /= 60.0;
				return String.format("%.2f", timeElapsed) + "h";
			} else {
				return String.format("%.2f", timeElapsed) + "m";
			}
		} else {
			return String.format("%.2f", timeElapsed) + "s";
		}
	}

	private void render(double percent, double timeElapsed, int compleated, int total) {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setFont(new Font("bold", 1, getHeight() / 3));

		g.setColor(Color.red);
		g.fillRect(0, 0, (int) (getWidth() * percent / 100.0), g.getFont().getSize());

		g.setColor(Color.blue);
		String percentRounded = String.format("%.2f", percent);
		g.drawString(percentRounded + "%", 0, g.getFont().getSize());

		g.drawString(toTime(timeElapsed), 0, 2 * g.getFont().getSize());
		
		g.drawString(compleated + "/" + total, 0, 3 * g.getFont().getSize());

		g.dispose();
		bs.show();
	}
}
