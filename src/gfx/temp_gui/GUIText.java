package gfx.temp_gui;

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.List;

import gfx.GraphicsUtils;
import gfx.TextFormat;


public class GUIText extends GUIObject{
	protected String text;
	protected final TextFormat textFormat;
	protected float lineWidth;
	
	public GUIText(String text) {
		super();
		this.text = text;
		this.lineWidth = -1;
		this.textFormat = new TextFormat();
		
		calculateSize();
	}
	
	@Override
	protected void updateExtents() {
		calculateSize();
	}

	@Override
	protected void updateLogic() {
		if (isDirty()  || textFormat.isDirty()) {
			setUIImage(generateUIImage());
			setDirty(false);
			textFormat.setDirty(false);}
	}
	
	@Override
	public BufferedImage generateUIImage() {
		BufferedImage image = GraphicsUtils.createCompatibleImage(size().width(), size().height(), Transparency.TRANSLUCENT);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		
		g2.setFont(textFormat.getFont());
		int x = padding().left();
		int y = (int) (padding().top() + textFormat.getFontSize());
		
		List<String> strings = GraphicsUtils.splitString(text, lineWidth, textFormat.getFontMetric());
		int yy = y;
		
		if (textFormat.getShadowColor().getAlpha() != TRANSPARENT.getAlpha()) {
			g2.setColor(textFormat.getShadowColor());
			for (String string: strings) {
				int sX = x + textFormat.getShadowX();
				int sY = yy + textFormat.getShadowY();
				GraphicsUtils.drawTextOutline(image, string, textFormat.getFont(), sX, sY, 
						(int) textFormat.getShadowThickness(), textFormat.getShadowColor());
				g2.drawString(string, sX, sY);
				yy += textFormat.getFontSize();}
		}
		
		if (textFormat.getOutlineColor().getAlpha() != TRANSPARENT.getAlpha()) {
			yy = y;
			g2.setColor(textFormat.getOutlineColor());
			for (String string: strings) {
				GraphicsUtils.drawTextOutline(image, string, textFormat.getFont(), x, yy, 
						(int) textFormat.getOutlineThickness(), textFormat.getOutlineColor());
				yy += textFormat.getFontSize();}
		}
		
		yy = y;
		g2.setColor(textFormat.getFontColor());
		for (String string: strings) {
	        g2.drawString(string, x, yy);
			yy += textFormat.getFontSize();}
		
		g2.dispose();

		return image;
	}
	
	
	protected void calculateSize() {
		if (isDirty() || textFormat.isDirty()) {
			List<String> strings = GraphicsUtils.splitString(text, lineWidth, textFormat.getFontMetric());
			
			int width = (int) lineWidth + padding().getHorizontal();
			if (lineWidth < 0 || lineWidth == Float.MAX_VALUE) {
				width = textFormat.getFontMetric().stringWidth(text) + padding().getHorizontal();}
			
			int height = textFormat.getFontMetric().getHeight();
			height *= strings.size();
			height += padding().getVertical();
			
			if (textFormat.getShadowColor().getAlpha() != TRANSPARENT.getAlpha()) {
				width += textFormat.getShadowX();
				height += textFormat.getShadowY();}
			
			setSize(width, height);
		}
	}
	
	
	public GUIText setText(String text) {
		if (this.text.compareTo(text) != 0) {
			this.text = text;
			dirty();}
		return this;}
	
	/**
	 * If splitting of text into multiple lines is not desired, 
	 * set lineWidth to any negative number or Float.MAX_VALUE.
	 */
	public GUIText setLineWidth(float lineWidth) {
		if (this.lineWidth != lineWidth) {
			this.lineWidth = lineWidth;
			dirty();}
		return this;}
	
	
	
	public String getText() {
		return text;}
	
	public float getLineWidth() {
		return lineWidth;}
	
	public TextFormat getTextFormat() {
		return textFormat;}
	
}
