package input;

public interface MouseConsumer {

	public void onClick(int mouseX, int mouseY);
	public void onDrag(int mouseX, int mouseY);
	public void onRelease(int mouseX, int mouseY);
}
