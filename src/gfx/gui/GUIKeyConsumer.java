package gfx.gui;

import java.awt.event.KeyEvent;

public interface GUIKeyConsumer {

	public abstract void onKeyTyped(KeyEvent unicodeKey);
	public abstract void onKeyEntered(KeyEvent key);
}
