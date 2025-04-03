package input;

public class MouseHandler {
	
	private MouseConsumer activeMouseConsumer;
	
	protected void update(MouseInput mouseInput) {
		if (mouseInput.isLeftPressed() == false 
				&& mouseInput.isRightPressed() == false 
				&& mouseInput.isWheelPressed() == false) {
			this.activeMouseConsumer = null;
		}
	}
	
	public void setActiveConsumer(MouseConsumer consumer) {
		if (this.activeMouseConsumer == null) {
			this.activeMouseConsumer = consumer;
		}
	}
	
	public MouseConsumer getActiveConsumer() {
		return this.activeMouseConsumer;
	}
}
