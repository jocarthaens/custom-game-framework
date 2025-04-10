package game;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import input.Input;
import maths.AABB;

// Abstract Screen Class whose role is to render everything on the window.

public abstract class Screen extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private Canvas canvas;
	private AABB windowBounds;
	
    public Screen(int width, int height, Input input) {
    	setTitle("Basic 2D Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        addKeyListener(input.getKeyInput());
        
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setFocusable(false);
        canvas.addMouseListener(input.getMouseInput());
        canvas.addMouseMotionListener(input.getMouseInput());
        add(canvas);
        pack();

        canvas.createBufferStrategy(2);
        setLocationRelativeTo(null);
        setVisible(true);
        
        windowBounds = new AABB(0, 0, width, height);
    }
    
    public void render(Game game) {
    	BufferStrategy buffer = canvas.getBufferStrategy();
    	Graphics2D g2 = (Graphics2D) buffer.getDrawGraphics();
    	
    	renderOnScreen(game);
    	
    	g2.dispose();
    	buffer.show();
    }
    
    
    public abstract void renderOnScreen(Game game);
    
    
    
    public void resize(int width, int height) {
    	windowBounds.setWidth(width).setHeight(height);}
    
    
    public AABB windowBounds() {
    	return windowBounds;}
}
