package gfx.temp_gui;

public class AlignUI {
	
	public enum Align { // Start, center, end vs -> top center bottom, left, center, right
		START,
		CENTER,
		END,
	}
	
	
	protected Align horizontal;
	protected Align vertical;
	
	public AlignUI(Align horizontal, Align vertical) {
		this.horizontal = horizontal;
		this.vertical = vertical;
	}
	
	public AlignUI() {
		this(Align.START, Align.START);
	}
	
	
	public void setHorizontal(Align horizontal) {
		this.horizontal = horizontal;}
	
	public void setVertical(Align vertical) {
		this.vertical = vertical;}
	
	public Align getHorizontal() {
		return horizontal;}
	
	public Align getVertical() {
		return vertical;}
}
