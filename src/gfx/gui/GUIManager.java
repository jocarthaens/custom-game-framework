package gfx.gui;

import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

// GUIManager manages GUI Objects that are registered in its list by updating and rendering them each frame.
// Also manages focusable GUI Objects, especially in navigating which focusable object is to be given focus.

public class GUIManager {
	
	public enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT,
	}
	
	protected GUIObject focusedUI = null;
	protected Set<GUIObject> rootUI;
	protected GUIManagerProxy proxy;
	protected GUIMapper focusableMap;
	protected int windowWidth, windowHeight;
	
	
	public GUIManager() {
		this.rootUI = new HashSet<>();
	}
	
	
	
	public GUIManagerProxy getProxy() {
		return this.proxy;
	}
	
	public GUIMapper getFocusableMap() {
		return this.focusableMap;
	}
	
	
	
	
	
	
	public boolean registerRootGUI(GUIObject obj) { /////
		if (obj == null || obj.getParent() != null)
			return false;
		this.rootUI.add(obj);
		return true;
	}
	
	public boolean unregisterRootGUI(GUIObject obj) { /////
		if (this.isFocused(obj)) {
			this.unregisterFocusable(obj);
		}
		return this.rootUI.remove(obj);
	}
	
	
	
	
	
	
	
	public boolean registerFocusable(GUIObject obj) {
		if (obj == null || obj.isFocusable() == false) return false;
		if (this.getFocusableMap().has(obj)) return true;
		this.getFocusableMap().insert(obj);
		return true;
	}
	
	public boolean unregisterFocusable(GUIObject obj) {
		if (obj == null) return false;
		boolean isPresent = this.getFocusableMap().has(obj);
		if (this.isFocused(obj)) {
			this.clearFocus();
		}
		this.getFocusableMap().remove(obj);
		return isPresent;
	}
	
	public boolean requestFocus(GUIObject obj) {
		if (obj == null || !this.getFocusableMap().has(obj) ) return false;
		if (this.focusedUI != null) this.focusedUI.onFocusLost();
		this.focusedUI = obj;
		this.focusedUI.onFocusGained();
		return true;
	}
	
	public void clearFocus() {
		if (this.focusedUI != null) this.focusedUI.onFocusLost();
		this.focusedUI = null;
	}
	
	public GUIObject getFocusedUI() {
		return this.focusedUI;
	}
	
	public boolean isFocused(GUIObject obj) {
		return obj != null && this.focusedUI == obj;
	}
	
	
	
	// add focus navigation
	public void onFocusNavigatePointer(int mouseX, int mouseY) {
		Set<GUIObject> set = new HashSet<>(1);
		float bestDist = Float.MAX_VALUE;
		float cellSize = this.getFocusableMap().getCellSize();
		int cellX = (int) (mouseX / cellSize);
		int cellY = (int) (mouseY / cellSize);
		GUIObject nearest = null;
		
		for (GUIObject focus: this.getFocusableMap().get(cellX, cellY, set)) {
			int fx = focus.globalCenterX();
			int fy = focus.globalCenterY();
			int deltaX = fx - mouseX;
			int deltaY = fy - mouseY;
			float distSquare = deltaX * deltaX + deltaY * deltaY;
			if (distSquare <= bestDist && focus.containsPoint(mouseX, mouseY)) {
				bestDist = distSquare;
				nearest = focus;
			}
		}
		
		this.requestFocus(nearest);
	}
	
	
	public void onFocusNavigateDirectional(int posX, int posY, Direction dir, float queryDist) {
		Set<GUIObject> set = this.getFocusableMap().query(posX, posY, queryDist, queryDist, new HashSet<>());
		GUIObject nearest = null;
		int refX = 0;		// reference direction x
		int refY = 0;		// reference direction y
		switch (dir) {
			case UP:
				refX = 0;
				refY = -1;
				break;
			case DOWN:
				refX = 0;
				refY = 1;
				break;
			case LEFT:
				refX = -1;
				refY = 0;
				break;
			case RIGHT:
				refX = 1;
				refY = 0;
				break;
			default:
				break;
		}
		double cos45 = Math.cos(Math.PI * 0.25); 	// conical angle range (cos(45) or (0.7071))
		double refLength = 1.0;						// length of reference direction
		double nearestDot = Float.MAX_VALUE;		// nearest dot must be > cos45 and <= 1
		double bestDist = Float.MAX_VALUE;
		
		
		for (GUIObject focus: set) {
			int fx = focus.globalCenterX();
			int fy = focus.globalCenterY();
			int deltaX = fx - posX;
			int deltaY = fy - posY;
			double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			double dot = (refX * deltaX + refY * deltaY) / (dist * refLength);
			
			// focus is qualified if within conical range and more parallel with direction
			if ( ( dot >= cos45 && (refLength - dot) < nearestDot ) && dist <= bestDist) {
				bestDist = dist;
				nearest = focus;
				nearestDot = dot;
			}
		}

		this.requestFocus(nearest);
	}
	
	
	
	
	
	
	public void clear() {
		rootUI.clear();
		this.getFocusableMap().clear();
		clearFocus();
	}
	
	
	
	
	
	
	
	// update flow: position, extents, update, <optional position and extents re-updates> render
	// or: <optional position and extents initialization>, update, position, extents, render
	
	
	public void updatePositioning() {
		for (GUIObject root: rootUI) {
			root.managePositioning();
		}
	}
	
	public void updateExtents() {
		for (GUIObject root: rootUI) {
			root.manageExtents();
		}
	}
	
	public void update() {
		for (GUIObject root: rootUI) {
			root.update(this);
		}
	}
	
	public void render(Graphics2D g2) {
		for (GUIObject root: rootUI) {
			root.render(g2);
		}
	}
	
	
	
	
	
	
	protected void setWindowSize(int width, int height) {
		this.windowHeight = height;
		this.windowWidth = width;
	}
	
	public int getWindowWidth() {
		return this.windowWidth;
	}
	
	public int getWindowHeight() {
		return this.windowHeight;
	}
	
	
	
	
	
	
	
	
	
	protected abstract class GUIManagerProxy {
		
		
		public boolean registerFocusable(GUIObject obj) {
			return GUIManager.this.registerFocusable(obj);
		}
		
		public boolean unregisterFocusable(GUIObject obj) {
			return GUIManager.this.unregisterFocusable(obj);
		}
		
		public void requestFocus(GUIObject obj) {
			GUIManager.this.requestFocus(obj);
		}
		
		public void clearFocus() {
			GUIManager.this.clearFocus();
		}
		
		public GUIObject getFocusedUI() {
			return GUIManager.this.getFocusedUI();
		}
		
		public boolean isFocused(GUIObject obj) {
			return GUIManager.this.isFocused(obj);
		}
		
		
		
		
		public int getWindowHeight() {
			return GUIManager.this.getWindowHeight();
		}
		
		public int getWindowWidth() {
			return GUIManager.this.getWindowWidth();
		}
	}
	
	
}
