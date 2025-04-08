package gfx;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

// Stores all text-formatting related information, which can be useful in rendering text onto the screen.

public class TextFormat {
	public static final Color TRANSPARENT = new Color(0,0,0,0); 
	private static final Canvas _canvas = new Canvas();

	private Font font;
	private FontMetrics fontMetric;
	private Color fontColor;
	
	private Color outlineColor;
	private float outlineThickness;
	
	private int shadowOffsetX, shadowOffsetY;
	private Color shadowColor;
	private float shadowThickness;
	
	private float lineSpacing;
	
	private boolean isDirty;
	private boolean fontDirty;
	
	public TextFormat() {
		font = new Font("Arial", Font.PLAIN, 16);
		fontMetric = _canvas.getFontMetrics(this.font);
		fontColor = Color.BLACK;
		
		outlineColor = TRANSPARENT;
		outlineThickness = 0;
		
		shadowOffsetX = 0;
		shadowOffsetY = 0;
		shadowColor = TRANSPARENT;
		shadowThickness = 0;
		
		lineSpacing = 0;
		
		isDirty = true;
		fontDirty = false;
	}
	
	public TextFormat setFont(Font font, int fontStyle, float fontSize) {
		if ( (this.font == null && this.font != font) || ( this.font != null && (this.font.equals(font)
				|| this.font.getStyle() != fontStyle 
				|| this.font.getSize2D() != fontSize)) ) {
			this.font = font.deriveFont(fontStyle, fontSize);
			fontDirty = true;
			isDirty = true;}
		return this;}
	
	public TextFormat setFontStyle(int fontStyle) {
		return setFont(this.font, fontStyle, this.font.getSize2D());}
	
	public TextFormat setFontSize(float fontSize) {
		return setFont(this.font, this.font.getStyle(), fontSize);}
	
	public TextFormat setFontColor(Color color) {
		if ( (fontColor == null && fontColor != color) 
				|| (fontColor != null && !fontColor.equals(color)) ) {
			fontColor = color != null ? color : TRANSPARENT;
			isDirty = true;}
		return this;}
	
	
	public TextFormat setOutlineColor(Color color) {
		if ( (outlineColor == null && outlineColor != color) 
				|| (outlineColor != null && !outlineColor.equals(color)) ) {
			outlineColor = color != null ? color : TRANSPARENT;
			isDirty = true;}
		return this;}

	public TextFormat setOutlineThickness(float thickness) {
		if (this.outlineThickness != thickness) {
			this.outlineThickness = thickness;
			isDirty = true;}
		return this;}
	
	
	
	public TextFormat setShadowOffset(int x, int y) {
		if (shadowOffsetX != x || shadowOffsetY != y) {
			shadowOffsetX = x;
			shadowOffsetY = y;
			isDirty = true;}
		
		return this;}
	
	public TextFormat setShadowColor(Color color) {
		if ( (shadowColor == null && shadowColor != color) 
				|| (shadowColor != null && !shadowColor.equals(color)) ) {
			shadowColor = color != null ? color : TRANSPARENT;
			isDirty = true;}
		return this;}
	
	public TextFormat setShadowThickness(float thickness) {
		if (this.shadowThickness != thickness) {
			this.shadowThickness = thickness;
			isDirty = true;}
		return this;}

	
	public TextFormat setLineSpacing(float spacing) {
		if (this.lineSpacing != spacing) {
			this.lineSpacing = spacing;
			isDirty = true;}
		return this;}
	
	
	
	
	
	public Font getFont() {
		return font;}
	
	public FontMetrics getFontMetric() {
		if (fontDirty) {
			fontMetric = _canvas.getFontMetrics(this.font);
			fontDirty = false;
		}
		return fontMetric;}
	
	public float getFontSize() {
		return font.getSize2D();}
	
	public int getFontStyle() {
		return font.getStyle();}
	
	public Color getFontColor() {
		return fontColor;}
	
	
	public Color getOutlineColor() {
		return outlineColor;}

	public float getOutlineThickness() {
		return outlineThickness;}

	
	
	public int getShadowX() {
		return shadowOffsetX;}
	
	public int getShadowY() {
		return shadowOffsetY;}
	
	public Color getShadowColor() {
		return shadowColor;}

	public float getShadowThickness() {
		return shadowThickness;}
	
	
	public float getLineSpacing() {
		return lineSpacing;}
	
	
	
	public void setDirty(boolean dirty) {
		isDirty = dirty;}
	
	public void dirty() {
		isDirty = true;}
	
	public boolean isDirty() {
		return isDirty;}
}
