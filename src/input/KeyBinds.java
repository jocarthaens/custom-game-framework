package input;

import java.util.Map;

/*
	Mechanics for advanced basic action rpg controls:
	D-pad -> movement (left, right, up, down)
	A -> use tool if not carrying, otherwise use carried object
	B -> dash
	X -> swap tool; if facing carry object, carry and drop/throw object
	Y -> interact
	select -> toggle inventory
	start -> pause/settings (default)
	
	add default and current versions
	*/

public abstract class KeyBinds {
	Map<String, Integer> defaultBind;
	Map<String, Integer> currentBind;
	
	public abstract void setDefaultKeyBind();
	
	public abstract void setKeyBind();
}