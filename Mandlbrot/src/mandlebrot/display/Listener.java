package mandlebrot.display;

import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import mandlebrot.Main;

public class Listener extends MouseMotionAdapter
		implements KeyListener, MouseListener, FocusListener, MouseMotionListener, MouseWheelListener {

	public boolean[] keys = new boolean[0xFFFF];
	
	private boolean mousePressed;
	private Point mouseStart;
	private Point mouseAt;
	

	public Listener() {
		mousePressed = false;
		mouseStart = new Point();
		mouseAt = new Point();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
//		if(e.getKeyCode() == KeyEvent.VK_P) {
//			Main.renderImage();
//		}
		//System.out.println(e.getKeyCode());
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			//mousePressed = false;
			// System.out.println("clicked");
			// mouseStart = e.getPoint();
			// mouseAt = e.getPoint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			//System.out.println("pressed");
			mouseStart = e.getPoint();
			mouseAt = e.getPoint();
			mousePressed = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			mousePressed = false;
			// System.out.println("relaesed");
			mouseStart = mouseAt;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (mousePressed) {
			// System.out.println("drag");
			mouseAt = e.getPoint();
			int dx = mouseAt.x - mouseStart.x;
			int dy = mouseAt.y - mouseStart.y;
			Main.processMouseMovement(dx, dy);
			mouseStart = mouseAt;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//System.out.println("Scrolling: " +  e.getWheelRotation());
		int scrollAmount = e.getWheelRotation();
		Point scrollAt = e.getPoint();
		Main.processScroll(scrollAmount);
	}

}
