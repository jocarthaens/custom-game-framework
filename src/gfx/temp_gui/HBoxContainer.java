package gfx.temp_gui;


public class HBoxContainer extends GUIContainer {
	
	public HBoxContainer() {
		super();
		resizeByContents = true;
	}

	@Override
	protected void calculateSizeWithContents() {
		int childrenWidth = 0;
        int tallestChildHeight = 0;
        
        for (GUIObject component: children) {
        	childrenWidth += component.size().width() + component.margin().getHorizontal();
        	if (component.size().height() > tallestChildHeight) {
        		tallestChildHeight = component.size().height();}
		}
        
        childrenWidth += padding().getHorizontal();
        tallestChildHeight += padding().getVertical();
        
        setSize(childrenWidth, tallestChildHeight);
	}

	@Override
	protected void calculateContentPosition() {
		int currX = padding().left() + uiPosition().intX();
		int currY = padding().top() + uiPosition().intY();
		
		for (GUIObject component: children) {
			currX += component.margin().left();
			if (component.uiPosition().intX() != currX || component.uiPosition().intY() != currY) {
				component.setUIPos(currX, currY);
			}
			currX += component.size().width();
			currX += component.margin().right();
		}
	}
	
	@Override
	public GUIContainer resizeByContents(boolean resize) {
		this.resizeByContents = false;
		return this;}
}
