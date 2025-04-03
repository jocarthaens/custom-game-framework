package gfx.temp_gui;

public class SizeUI {
	private int width;
	private int height;
	
	
	public SizeUI(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	
	public SizeUI setWidth(int width) {
		this.width = width;
		return this;}
	
	public SizeUI setHeight(int height) {
		this.height = height;
		return this;}
	
	public SizeUI resize(int width, int height) {
		this.width = width;
		this.height = height;
		return this;}

	
	public int width() {
		return width;}

	public int height() {
		return height;}
}
