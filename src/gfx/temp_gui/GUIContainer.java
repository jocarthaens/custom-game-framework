package gfx.temp_gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import gfx.GraphicsUtils;

public abstract class GUIContainer extends GUIObject {
	
	protected boolean resizeByContents;
	private BufferedImage bgImage;
	
	protected List<GUIObject> children;
	
	public GUIContainer() {
		super();
		children = new ArrayList<GUIObject>();
	}
	
	protected abstract void calculateSizeWithContents();
	protected abstract void calculateContentPosition();
	
	@Override
	protected void updateExtents() {
		for (GUIObject component: children) {
			component.updateExtents();}
		calculateSize();
		calculatePosition();
	}
	
	private void calculateSize() {
		if (resizeByContents) {
			calculateSizeWithContents();
		}
	}
	
	private void calculatePosition() {
		int x = margin().left();
		int y = margin().top();
		
		if (parent() != null) {
			x += parent().uiPosition().intX();
			y += parent().uiPosition().intY();
		}
		
		if (uiPosition().intX() != x || uiPosition().intY() != y) {
			setUIPos(x, y);}
		calculateContentPosition();
	}
	
	public GUIContainer resizeByContents(boolean resize) {
		if (this.resizeByContents != resize) {
			this.resizeByContents = resize;
			dirty();}
		return this;}
	
	public boolean isResizableByContents() {
		return resizeByContents;}

	
	
	
	
	
	@Override
	protected void updateLogic() {
		for (GUIObject component: children) {
			if (component.isVisible()) {
				component.updateLogic();}
		}
		super.updateLogic();
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		for (GUIObject component: children) {
			if (component.isVisible()) {
				component.render(g);}
		}
	}
	
	@Override
	public BufferedImage generateUIImage() {
		if (bgImage == null) {
			return null;}
		BufferedImage image = GraphicsUtils.ninePatchImage(bgImage, size().width(), size().height(),
				padding().left(), padding().right(), padding().top(), padding().bottom());
		return image;}

	public GUIContainer setBGImage(BufferedImage bgImage) {
		if (this.bgImage != bgImage) {
			this.bgImage = bgImage;
			dirty();}
		return this;}
	
	public BufferedImage bgImage() {
		return bgImage;}
	
	
	
	
	

	public void addUIComponent(GUIObject uiComponent) {
		if (uiComponent != null && uiComponent != this 
				&& uiComponent != parent() && !children.contains(uiComponent)) {
			children.add(uiComponent);
			uiComponent.setParent(this);}
	}
	
	public void removeUIComponent(GUIObject uiComponent) {
		boolean isChild = children.remove(uiComponent);
		if (isChild) {
			uiComponent.setParent(null);}
	}
	
	public boolean hasUIComponent(GUIObject uiComponent) {
		return children.contains(uiComponent);}

	public void clear() {
		children.clear();}
	
	public List<GUIObject> getComponents() {
		return children;}
}
