package gfx.gui;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import gfx.gui.GUIManager.GUIManagerProxy;

// Base GUI Object where all GUI Objects will inherit from

public abstract class GUIObject {
	
	public static enum WindowAlignment {
		BEGIN,
		MIDDLE,
		END,
	}
	
	int relativeX, relativeY; // topLeft position
	int width = 1, height = 1;	// size
	int marginTop, marginBottom, marginLeft, marginRight; // adds spacing outside object's rect dimensions;
	int paddingTop, paddingBottom, paddingLeft, paddingRight; // adds spacing inside object's rect dimensions;
	
	// aligns only when parent is null, uses the window size for position calculations if not null
	WindowAlignment windowHorizontal = null;
	WindowAlignment windowVertical = null;
	
	// offset position used by parent GUIObjects to align its child GUIObjects
	int offsetX, offsetY;
	
	// add dimensionChanged boolean?
	//boolean needsRedraw = true; // ??
	boolean visible = true;
	
	boolean enabled = true; // update for changes?
	boolean isFocusable = true; // update for changes?
	//boolean focused = false; // occurs when mouse clicked or selected by keyboard input
	
	GUIManagerProxy proxy;
	
	private GUIObject parent = null; // update for changes
	private boolean editChildren = true;
	private ArrayList<GUIObject> children = new ArrayList<GUIObject>(1);
	
	
	
	
	
	
	
	
	
	
	
	public boolean addChild(GUIObject child) {
		if (this.canEditChildren() == false) return false;///////
		if (child == null || child == this 
				|| child.parent != null || isAncestorOf(child))
			return false;
		children.add(child);
		child.parent = this;
		return true;
	}
	
	public boolean removeChild(GUIObject child) {
		if (this.canEditChildren() == false) return false;//////
		if (child == null || children.remove(child) == false)
			return false;
		child.parent = null;
		child.clearOffset();
		return true;
	}
	
	public static boolean moveChild(GUIObject child, GUIObject newParent) {////
		if (child == null || newParent == null || child == newParent) return false;
		if ( child.isAncestorOf(newParent) ) return false;
		if (child.parent != null)
			if (child.parent.canEditChildren() == false)
				return false;
			child.parent.removeChild(child);
		return newParent.addChild(child);
	}
	
	public boolean moveTo(GUIObject newParent) {
	    return GUIObject.moveChild(this, newParent);
	}
	
	public boolean isAncestorOf(GUIObject node) {////
		GUIObject current = node;
		while (current != null) {
			if (current == this) return true;
			current = current.parent;
		}
		return false;
	}
	
	public boolean isDescendantOf(GUIObject ancestor) {////
		return ancestor.isAncestorOf(this);
	}
	
	public GUIObject getParent() {
		return parent;
	}
	
	public List<GUIObject> getChildren(List<GUIObject> out) {
		out.addAll(children);
		return out;
	}
	
	protected void swapChildrenOrder(int a, int b) {
		if (this.canEditChildren() == false) return;
		GUIObject objA = children.get(a);
		GUIObject objB = children.set(b, objA);
		children.set(a, objB);
	}
	
	public void clearChildren() {
		if (this.canEditChildren() == false) return;
		for (GUIObject child: children) {
			removeChild(child);
		}
		children.clear();
	}
	
	
	
	
	protected void setEditChildren(boolean edit) {
		this.editChildren = edit;
	}
	
	public boolean canEditChildren() {
		return this.editChildren;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void setEnable(boolean enable) { //// include this in focus
		this.enabled = enable;
	}
	
	public void setFocusable(boolean focusable) {
		if (this.isFocusable != focusable) {
			this.isFocusable = focusable;
			if (focusable == true) {
				getProxy().registerFocusable(this);
			} else {
				getProxy().unregisterFocusable(this);
			}
		}	
	}
	
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public boolean isFocusable() {
		return this.isFocusable;	
	}
	
	
	
	
	
	
	// Focus-related events
	public abstract void onFocusLost();
	public abstract void onFocusGained();
	public abstract void onBeingFocused();
	
//	// Keyboard-related events
//	public abstract void onKeyTyped(KeyEvent unicodeKey);
//	public abstract void onKeyEntered(KeyEvent key);
//	
//	// Mouse-related events
//	public abstract void onClick(float mouseX, float mouseY);
//	public abstract void onDrag(float mouseX, float mouseY);
//	public abstract void onRelease(float mouseX, float mouseY);
	
	
	
	
	protected void setProxy(GUIManagerProxy proxy) {
		this.proxy = proxy;
	}
	
	public GUIManagerProxy getProxy() {
		return this.proxy;
	}
	
	
	
	
	
	
	// position-related methods
	
	public void setRelativePosition(int x, int y) {
		relativeX = x;
		relativeY = y;
	}
	
	public int getRelativeX() {
		return relativeX;
	}
	
	public int getRelativeY() {
		return relativeY;
	}
	
	
	public int globalX() {
		int gx = relativeX + marginLeft + offsetX;
		if (parent == null) {
			int windowWidth = getProxy().getWindowWidth();
			switch (windowHorizontal) {
			case BEGIN:
				break;
			case MIDDLE:
				gx += (windowWidth - width) * 0.5;
				break;
			case END:
				gx += windowWidth - (width + paddingRight + marginRight);
				break;
			default:
				break;
			}
		}
		GUIObject current = parent;
		while (current != null) {
			gx += current.relativeX;
			gx += current.marginLeft;
			gx += current.paddingLeft;
			gx += current.offsetX;
			if (current.parent == null) {
				int parentWindowWidth = getProxy().getWindowWidth();
				switch (current.windowHorizontal) {
				case BEGIN:
					break;
				case MIDDLE:
					gx += (parentWindowWidth - current.width) * 0.5;
					break;
				case END:
					gx += parentWindowWidth - (current.width 
							+ current.paddingRight + current.marginRight);
					break;
				default:
					break;
				}
			}
			current = current.parent;
		}
		return gx;
	}
	
	public int globalY() {
		int gy = relativeY + marginTop + offsetY;
		if (parent == null) {
			int windowHeight = getProxy().getWindowHeight();
			switch (windowVertical) {
			case BEGIN:
				break;
			case MIDDLE:
				gy += (windowHeight - height) * 0.5;
				break;
			case END:
				gy += windowHeight - (height + paddingBottom + marginBottom);
				break;
			default:
				break;
			}
		}
		GUIObject current = parent;
		while (current != null) {
			gy += current.relativeY;
			gy += current.marginTop;
			gy += current.paddingTop;
			gy += current.offsetY;
			if (current.parent == null) {
				int parentWindowHeight = getProxy().getWindowHeight();
				switch (current.windowVertical) {
				case BEGIN:
					break;
				case MIDDLE:
					gy += (parentWindowHeight - current.height) * 0.5;
					break;
				case END:
					gy += parentWindowHeight - (current.height 
							+ current.paddingBottom + current.marginBottom);
					break;
				default:
					break;
				}
			}
			current = current.parent;
		}
		return gy;
	}
	
	public int globalCenterX() {
		return (int) (this.globalX() + this.width * 0.5);
	}
	
	public int globalCenterY() {
		return (int) (this.globalY() + this.height * 0.5);
	}

	
	public void setWindowAlignment(WindowAlignment horizontal, WindowAlignment vertical) {
		this.windowHorizontal = horizontal;
		this.windowVertical = vertical;
	}
	
	public WindowAlignment getHorizontalAlignment() {
		return this.windowHorizontal;
	}
	
	public WindowAlignment getVerticalAlignment() {
		return this.windowVertical;
	}
	
	
	protected void setOffset(int x, int y) {
		this.offsetX = x;
		this.offsetY = y;
	}
	
	protected void clearOffset() {
		this.offsetX = 0;
		this.offsetY = 0;
	}
	
	public int getOffsetX() {
		return this.offsetX;
	}
	
	public int getOffsetY() {
		return this.offsetY;
	}
	
	
	
	
	
	
	
	
	
	// size, padding, margin
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public void setPadding(int top, int bottom, int left, int right) {
		this.paddingTop = top;
		this.paddingBottom = bottom;
		this.paddingLeft = left;
		this.paddingRight = right;
	}
	
	public int paddingLeft() {
		return this.paddingLeft;
	}
	
	public int paddingRight() {
		return this.paddingRight;
	}
	
	public int paddingTop() {
		return this.paddingTop;
	}
	
	public int paddingBottom() {
		return this.paddingBottom;
	}
	
	public int paddingHorizontal() {
		return this.paddingLeft + this.paddingRight;
	}
	
	public int paddingVertical() {
		return this.paddingTop + this.paddingBottom;
	}
	
	public void setMargin(int top, int bottom, int left, int right) {
		this.marginTop = top;
		this.marginBottom = bottom;
		this.marginLeft = left;
		this.marginRight = right;
	}
	
	public int marginLeft() {
		return this.marginLeft;
	}
	
	public int marginRight() {
		return this.marginRight;
	}
	
	public int marginTop() {
		return this.marginTop;
	}
	
	public int marginBottom() {
		return this.marginBottom;
	}
	
	public int marginHorizontal() {
		return this.marginLeft + this.marginRight;
	}
	
	public int marginVertical() {
		return this.marginTop + this.marginBottom;
	}
	
	
	
	
	
	
	
	
	public boolean containsPoint(int x, int y) {
		int gx = this.globalX();
		int gy = this.globalY();
		boolean contained = x >= gx && x <= gx + width 
				&& y >= gy && y <= gy + height;
		
		return contained;
	}
	
	
	
	
	
	
	
	
	// include updates for dimensions/extents/resizing 
	// add ui image generation that will be generated only with needsRedraw flag
	// include input handling for focusables
	// update flow: position update, size/layout update, logic update, <insert post-position and layout update?> render
	
	
	
	
	
	
	
	
	
	public void managePositioning() {
		this.selfManagePositioning();
		for (GUIObject child: children) {
			child.managePositioning();
		}
	}
	
	// place all position-related adjustments here
	public abstract void selfManagePositioning();
	
	
	
	
	
	
	public void manageExtents() {
		this.selfManageExtents();
		for (GUIObject child: children) {
			child.manageExtents();
		}
	}
	
	// place all size or layout-related adjustments here
	public abstract void selfManageExtents();
	
	
	
	
	
	
	
	public <T extends GUIManager> void update(T manager) {
		if (enabled == false || visible == false) return;
		this.updateSelf(manager);
		for (GUIObject child: children) {
			child.update(manager);
		}
	}
	
	public abstract <T extends GUIManager> void updateSelf(T manager);
	
	
	
	
	
	
	
	public void render(Graphics2D g2) {
		if (visible == false) return;
		this.drawSelf(this.globalX(), this.globalY(),
				this.getWidth(), this.getHeight(), g2);
		for (GUIObject child: children) {
			child.render(g2);
		}
	}
	
	public abstract void drawSelf(int renderX, int renderY, 
			int renderWidth, int renderHeight, Graphics2D g2); 
	
	
	
	// add generateUIImage? change drawSelf with redrawing
	
	//tradeoff of generating ui image then draw on canvas VS draw directly on canvas
	//drawing directly avoids additional performance incurred by drawing and storing bufferedimage, and then redrawing it on canvas
	//drawing bufferedimage completely avoids the issue of devs drawing the canvas over the ui object's boundaries,
	//		but can also limits their drawing capabilities (such as adding particles effects within borders)
	//final verdict: delegate manual drawing to devs (less computer strain + creative freedom)
	//how about borders? they will be used in other things like positioning, resizing, layouts and bounds checking
	
	// update changes once per frame or update changes on every change 
	// 		(depends on performance impact and how critical the update is)
	
}
