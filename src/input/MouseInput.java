package input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;


public class MouseInput implements MouseListener, MouseMotionListener {
	
	private float mouseX;
	private float mouseY;
	
	// clicked -> happens when pressed and then released
	// pressed -> button is pressed for long
	// released -> button is released after pressing;
	
	private boolean leftClicked;
	private boolean leftPressed;
	private boolean leftReleased;
	
	private boolean rightClicked;
	private boolean rightPressed;
	private boolean rightReleased;
	
	private boolean wheelClicked;
	private boolean wheelPressed;
	private boolean wheelReleased;

	
	
	public MouseInput() {
		mouseX = -1;
		mouseY = -1;
	}
	
	
	protected void clearEvents() {
		leftClicked = false;
		leftReleased = false;
		
		rightClicked = false;
		rightReleased = false;
		
		wheelClicked = false;
		wheelReleased = false;
	}
	
	
	public float getMouseX() {
		return mouseX;
	}
	
	public float getMouseY() {
		return mouseY;
	}
	
	
	public boolean isLeftClicked() {
		return leftClicked;}
	
	public boolean isLeftPressed() {
		return leftPressed;}
	
	public boolean isLeftReleased() {
		return leftReleased;}
	
	
	public boolean isRightClicked() {
		return rightClicked;}
	
	public boolean isRightPressed() {
		return rightPressed;}
	
	public boolean isRightReleased() {
		return rightReleased;}
	
	
	public boolean isWheelClicked() {
		return wheelClicked;}
	
	public boolean isWheelPressed() {
		return wheelPressed;}
	
	public boolean isWheelReleased() {
		return wheelReleased;}
	
	

	// MOUSE LISTENER METHODS
	@Override
	public void mouseClicked(MouseEvent e) {}

	
	@Override
	public void mousePressed(MouseEvent e) {
		leftPressed = SwingUtilities.isLeftMouseButton(e);
		rightPressed = SwingUtilities.isRightMouseButton(e);
		wheelPressed = SwingUtilities.isMiddleMouseButton(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			leftClicked = true;
			leftPressed = false;
			leftReleased = true;}
		
		if (SwingUtilities.isRightMouseButton(e)) {
			rightClicked = true;
			rightPressed = false;
			rightReleased = true;}
		
		if (SwingUtilities.isMiddleMouseButton(e)) {
			wheelClicked = true;
			wheelPressed = false;
			wheelReleased = true;}
	}

	
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	
	
	// MOUSE MOTION LISTENER METHODS
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = (float) e.getPoint().getX();
		mouseY = (float) e.getPoint().getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = (float) e.getPoint().getX();
		mouseY = (float) e.getPoint().getY();
	}

	
	
}
