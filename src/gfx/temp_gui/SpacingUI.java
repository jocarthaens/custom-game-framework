package gfx.temp_gui;

public class SpacingUI {
	private int top;
	private int right;
	private int bottom;
	private int left;
	
	
	public SpacingUI(int top, int right, int bottom, int left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}
	
	public SpacingUI(int horizontal, int vertical) {
		this(vertical, horizontal, vertical, horizontal);
	}
	
	public SpacingUI(int spacing) {
		this(spacing, spacing);
	}
	
	
	
	public SpacingUI set(int top, int right, int bottom, int left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
		return this;}
	
	public SpacingUI set(int horizontal, int vertical) {
		return set(vertical, horizontal, vertical, horizontal);}
	
	public SpacingUI set(int spacing) {
		return set(spacing, spacing);}
	
	
	
	
	public int top() {
		return top;}
	
	public int right() {
		return right;}
	
	public int bottom() {
		return bottom;}
	
	public int left() {
		return left;}
	
	public int getHorizontal() {
		return right + left;}
	
	public int getVertical() {
		return top + bottom;}
}
