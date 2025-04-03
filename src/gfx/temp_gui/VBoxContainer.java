package gfx.temp_gui;


public class VBoxContainer extends GUIContainer {
	
	public VBoxContainer() {
		super();
		resizeByContents = true;
	}

	@Override
	protected void calculateSizeWithContents() {
		int childrenHeight = 0;
        int widestChildWidth = 0;
        
        for (GUIObject component: children) {
        	childrenHeight += component.size().height() + component.margin().getVertical();
        	if (component.size().width() > widestChildWidth) {
        		widestChildWidth = component.size().width();}
		}
        
        childrenHeight += padding().getVertical();
        widestChildWidth += padding().getHorizontal();
        
        setSize(widestChildWidth, childrenHeight);
	}

	@Override
	protected void calculateContentPosition() {
		int currX = padding().left() + uiPosition().intX();
		int currY = padding().top() + uiPosition().intY();
		
		for (GUIObject component: children) {
			currY += component.margin().top();
			if (component.uiPosition().intX() != currX || component.uiPosition().intY() != currY) {
				component.setUIPos(currX, currY);
			}
			currY += component.size().height();
			currY += component.margin().bottom();
		}
	}
	
	@Override
	public GUIContainer resizeByContents(boolean resize) {
		this.resizeByContents = false;
		return this;}
}
