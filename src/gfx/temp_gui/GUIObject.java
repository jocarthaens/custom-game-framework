package gfx.temp_gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
//import java.util.List;

import maths.Vector2D;

public abstract class GUIObject {
	private final Vector2D uiPosition;
	private final SizeUI size;
	private final SpacingUI padding;
	private final SpacingUI margin;
	private boolean isVisible;
	
	private Color bgColor;
	private BufferedImage UIImage;
	
	private GUIContainer parent;
	//private List<GUIContainer> children;
	
	private boolean isDirty;
	//private boolean positionDirty;
	protected final static Color TRANSPARENT = new Color(0,0,0,0);
	
	public GUIObject() {
		uiPosition = new Vector2D();
		size = new SizeUI(0,0);
		padding = new SpacingUI(0);
		margin = new SpacingUI(0);
		isVisible = true;
		bgColor = TRANSPARENT;
		isDirty = true;}
	
	// Place all size and position related updates here
	protected abstract void updateExtents();
	
	protected void updateLogic() {
		if (isDirty) {
			setUIImage(generateUIImage());
			isDirty = false;}
	}
	
	public final void uiUpdate() {
		updateExtents();
		updateLogic();}
	
	
	public void render(Graphics g) {
		if (isVisible) {
			if (UIImage == null && (bgColor == null 
					|| (bgColor != null && bgColor.getAlpha() != 0))) {
				g.setColor(bgColor);
				g.fillRect(uiPosition.intX(), uiPosition.intY(), 
						size.width(), size.height());}
			else {
				Color color = (bgColor != null) ?
						bgColor : TRANSPARENT;
				g.drawImage(UIImage, uiPosition.intX(),
						uiPosition.intY(), color, null);
			}
		}
	}
	
	public abstract BufferedImage generateUIImage();
	
	
	
	
	
	// PARAMETERS RELATING TO UICOMPONENT DIMENSIONS
	
	public GUIObject setUIPos(int x, int y) {
		if (uiPosition.intX() != x || uiPosition.intY() != y) {
			uiPosition.set(x,y);
			//isDirty = true; // remove isDirty or not?
		}
		return this;}
	
	public Vector2D getUIPos(Vector2D outPos) {
		return outPos.set(uiPosition.x, uiPosition.y);}
	
	public Vector2D uiPosition() {
		return uiPosition;}

	
	
	public GUIObject setSize(int width, int height) {
		if (size.width() != width || size.height() != height) {
			size.resize(width, height);
			isDirty = true;}
		return this;}
	
	public SizeUI getSize(SizeUI outSize) {
		return outSize.resize(size.width(), size.height());}
	
	public SizeUI size() {
		return size;}
	
	
	
	public GUIObject setMargin(int top, int right, int bottom, int left) {
		if (margin.top() != top || margin.right() != right 
				|| margin.bottom() != bottom || margin.left() != left) {
			margin.set(top, right, bottom, left);
			isDirty = true;}
		return this;}
	
	public GUIObject setMargin(int horizontal, int vertical) {
		return setMargin(vertical, horizontal, vertical, horizontal);}
	
	public GUIObject setMargin(int spacing) {
		return setMargin(spacing, spacing, spacing, spacing);}
	
	public GUIObject setMargin(SpacingUI margin) {
		return setMargin(margin.top(), margin.right(), 
				margin.bottom(), margin.left());}
	
	public SpacingUI getMargin(SpacingUI outMargin) {
		return outMargin.set(margin.top(), margin.right(),
				margin.bottom(), margin.left());}
	
	public SpacingUI margin() {
		return margin;}
	
	
	
	public GUIObject setPadding(int top, int right, int bottom, int left) {
		if (padding.top() != top || padding.right() != right
				|| padding.bottom() != bottom || padding.left() != left) {
			padding.set(top, right, bottom, left);
			isDirty = true;}
		return this;}
	
	public GUIObject setPadding(int horizontal, int vertical) {
		return setPadding(vertical, horizontal, vertical, horizontal);}
	
	public GUIObject setPadding(int spacing) {
		return setPadding(spacing, spacing, spacing, spacing);}
	
	public GUIObject setPadding(SpacingUI padding) {
		return setPadding(padding.top(), padding.right(),
				padding.bottom(), padding.left());}
	
	public SpacingUI getPadding(SpacingUI outPadding) {
		return outPadding.set(padding.top(), padding.right(),
				padding.bottom(), padding.left());}
	
	public SpacingUI padding() {
		return padding;}
	
	
	
	// NOT DIRECTLY RELATED TO DIMENSION PARAMETERS, BUT IT'S DIMENSION PARAMETERS ARE SOMETIMES USED BY CHILD COMPONENTS
	public GUIObject setParent(GUIContainer parent) {
		if (this.parent != parent) {
			this.parent = parent;
			isDirty = true;}
		return this;}
	
	public GUIContainer parent() {
		return parent;}
	
	
	
	
	
	
	
	// UIComponent's Visual Parameters
	
	public GUIObject setVisibility(boolean visible) {
		if (isVisible != visible) {
			isVisible = visible;
			isDirty = true;}
		return this;}
	
	public boolean isVisible() {
		return isVisible;}
	
	
	public GUIObject setBGColor(Color color) {
		if ( (bgColor == null && bgColor != color) 
				|| (bgColor != null && !bgColor.equals(color)) ) {
			bgColor = color != null ? color : TRANSPARENT;
			isDirty = true;}
		return this;}
	
	public Color BGColor() {
		return bgColor;}
	
	/*
	 * SetUIImage() can only be used within the class hierarchy.
	 * It is designed for setting the UIImage per update using generateUIImage() method.
	 */
	protected GUIObject setUIImage(BufferedImage image) {
		if (this.UIImage != image) {
			this.UIImage = image;
			isDirty = true;}
		return this;}

	public BufferedImage UIImage() {
		return UIImage;}
	

	
	
	
	
	// Dirty flags useful for tracking changes in component properties
	
	public void setDirty(boolean dirty) {
		isDirty = dirty;}
	
	public void dirty() {
		isDirty = true;}
	
	public boolean isDirty() {
		return isDirty;}
	
}
