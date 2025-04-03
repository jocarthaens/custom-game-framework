package gfx.gui;

import java.awt.Graphics2D;

public class GUISlider extends GUIObject{

	protected float value;
	protected float minValue;
	protected float maxValue;
	
	public GUISlider(int sliderWidth, int sliderHeight, int minValue, int maxValue) {
		this.setSize(sliderWidth, sliderHeight);
		this.setFocusable(true);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public void onFocusLost() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFocusGained() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBeingFocused() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selfManagePositioning() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selfManageExtents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends GUIManager> void updateSelf(T manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawSelf(int renderX, int renderY, int renderWidth, int renderHeight, Graphics2D g2) {
		// TODO Auto-generated method stub
		g2.fillRect(renderX, renderY, renderWidth, renderHeight);
		float scale = Math.max(this.minValue, Math.min(this.value, this.maxValue));
		scale /= this.maxValue;
		g2.fillRect(renderX, renderY, (int) (renderWidth * scale), renderHeight);
		
	}
	
}
