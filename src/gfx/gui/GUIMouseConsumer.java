package gfx.gui;

import java.awt.event.MouseEvent;

public interface GUIMouseConsumer {

	public abstract void onClick(MouseEvent event);
	public abstract void onDrag(MouseEvent event);
	public abstract void onRelease(MouseEvent event);
}
