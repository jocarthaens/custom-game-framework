package gfx.sprite_rendering;

public class SpriteCamera {
	public final int offCameraSpace ;//16 * 2;
	private float camCx, camCy;
	private float camWidth, camHeight;
	//private float screenCx, screenCy;
	private float screenWidth, screenHeight;
	
	
	public SpriteCamera(float width, float height, float screenWidth, float screenHeight, int offCameraSpace) {
		
		this.camCx = width * 0.5f;
		this.camCy = height * 0.5f;
		this.camWidth = width;
		this.camHeight = height;
		
		//this.screenCx = screenWidth * 0.5f;
		//this.screenCy = screenHeight * 0.5f;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		this.offCameraSpace = offCameraSpace;
	}
	
	
	
	
	
	
	public void setCameraSize(float width, float height) {
		this.camWidth = width;
		this.camHeight = height;
	}
	
	public void setCameraCenter(float x, float y) {
		this.camCx = x;
		this.camCy = y;
	}
	
	public void moveCamera(float x, float y) {
		this.camCx += x;
		this.camCy += y;
	}
	
	public void setScreenSize(float width, float height) {
		this.screenWidth = width;
		this.screenWidth = height;
	}
	
	
	
	
	
	public boolean withinView(float topleftX, float topleftY, float width, float height) {
		boolean inView = topleftX < topLeftX() + width() + offCameraSpace * 2	
				&& topleftX + width > topLeftX()							
				&& topleftY < topLeftY() + height() + offCameraSpace * 2		
				&& topleftY + height > topLeftY();							
				
		return inView;
	}
	
	
	
	
	//public void worldToScreenCoordinates() {}
	
	
	
	
	
	public float topLeftX() {
		return camCx - camWidth * 0.5f - offCameraSpace;
	}
	
	public float topLeftY() {
		return camCy - camHeight * 0.5f - offCameraSpace;
	}
	
	public float width() {
		return camWidth;
	}
	
	public float height() {
		return camHeight;
	}
	
	public float centerX() {
		return camCx;
	}
	
	public float centerY() {
		return camCy;
	}
	
	
	public float topLeftScreenX() {
		return topLeftX() + (camWidth - screenWidth) * 0.5f;}
	
	public float topLeftScreenY() {
		return topLeftY() + (camHeight - screenHeight) * 0.5f;}
	
}
