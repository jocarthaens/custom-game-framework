package gfx.temp_gui;


public class GridContainer extends GUIContainer {

	protected int columns;
	
	public GridContainer() {
		super();
		resizeByContents = true;
	}
	
	@Override
	protected void calculateSizeWithContents() {
        int widestChildrenWidth = 0;
        int totalChildrenHeight = 0;
        
        int childrenWidth = 0;
        int tallestChildHeight = 0;
        int colCount = 0;
        
        for (GUIObject component: children) {
        	childrenWidth += component.size().width() + component.margin().getHorizontal();
        	if (component.size().height() + component.margin().getVertical() > tallestChildHeight) {
        		tallestChildHeight = component.size().height() + component.margin().getVertical();}
        	
        	colCount++;
        	if (colCount == columns) {
        		if (childrenWidth > widestChildrenWidth) {
        			widestChildrenWidth = childrenWidth;}
        		totalChildrenHeight += tallestChildHeight;
        		
        		childrenWidth = 0;
        		tallestChildHeight = 0;
        		colCount = 0;
        	}
		}
        
        widestChildrenWidth += padding().getHorizontal();
        totalChildrenHeight += padding().getVertical();
        
        setSize(widestChildrenWidth, totalChildrenHeight);
	}

	@Override
	protected void calculateContentPosition() {
		int defX = padding().left() + uiPosition().intX();
		int defY = padding().top() + uiPosition().intY();
		int currX = defX;
		int currY = defY;
		
		int tallestChildHeight = 0;
		int colCount = 0;
		
		for (GUIObject component: children) {
			currX += component.margin().left();
			if (component.uiPosition().intX() != currX || component.uiPosition().intY() != currY) {
				component.setUIPos(currX, currY);}
			if (component.size().height() + component.margin().getVertical() > tallestChildHeight) {
        		tallestChildHeight = component.size().height() + component.margin().getVertical();}
			currX += component.size().width();
			currX += component.margin().right();
			
			colCount++;
			if (colCount == columns) {
				currX = defX;
				currY += tallestChildHeight;
				tallestChildHeight = 0;
				colCount = 0;
			}
		}
	}
	
	public GridContainer setColumns(int columns) {
		if (this.columns != columns) {
			this.columns = columns;
			dirty();}
		return this;}
	
	public int columns() {
		return columns;}
	
	@Override
	public GUIContainer resizeByContents(boolean resize) {
		this.resizeByContents = false;
		return this;}
}
