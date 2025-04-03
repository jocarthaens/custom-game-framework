package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyInput implements KeyListener{
	private static final Map<Integer, String> keyCodeMap = new HashMap<>();
	private final Map<String, KeyStatus> keyCodeStatus;
	private final Map<Integer, Boolean> keyCodePressed;
	//private List<Integer> typedKeyBuffer;
	private List<KeyEvent> typedKeyEventBuffer;
	
	public KeyInput() {
		keyCodeStatus = new HashMap<String, KeyStatus>();
		keyCodePressed = new HashMap<Integer, Boolean>();
		//typedKeyBuffer = new ArrayList<Integer>();
		typedKeyEventBuffer = new ArrayList<KeyEvent>();
		initializeMapping();
	}

	private final void initializeMapping() {
		Field[] fields = KeyEvent.class.getDeclaredFields();
		for (Field field : fields) {
			if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
				try {
					String name = field.getName();
					int value = field.getInt(null);
					
					if (name.startsWith("VK_")) {
						keyCodeMap.put(value, name);
						keyCodeStatus.put(name, KeyStatus.NONE);
						keyCodePressed.put(value, false);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
            }
        }
	}
	
	protected void updateEvents() {
		for (int keyCode: keyCodePressed.keySet()) {
			String key = keyCodeMap.get(keyCode);
			boolean pressed = keyCodePressed.get(keyCode);
			KeyStatus status = keyCodeStatus.get(key);
			
			if (pressed && status == KeyStatus.NONE) {
				keyCodeStatus.replace(key, KeyStatus.PRESSED);
			}
			else if (pressed && status == KeyStatus.PRESSED) {
				keyCodeStatus.replace(key, KeyStatus.HELD);
			}
			else if (!pressed && (status == KeyStatus.PRESSED || status == KeyStatus.HELD)) {
				keyCodeStatus.replace(key, KeyStatus.RELEASED);
			}
			else if (!pressed && status == KeyStatus.RELEASED) {
				keyCodeStatus.replace(key, KeyStatus.NONE);
			}
		}
	}
	
	protected void clearEvents() {
		keyCodeStatus.clear();
		keyCodePressed.clear();
		//typedKeyBuffer.clear();
		typedKeyEventBuffer.clear();
	}
	
	
	public final boolean isKeyJustPressed(int keyCode) {
		if (!keyCodeMap.containsKey(keyCode)) {
			throw new IllegalArgumentException("KeyCode is invalid.");}
		return keyCodeStatus.get(keyCodeMap.get(keyCode)) == KeyStatus.PRESSED;
	}
	
	public final boolean isKeyPressed(int keyCode) {
		if (!keyCodeMap.containsKey(keyCode)) {
			throw new IllegalArgumentException("KeyCode is invalid.");}
		KeyStatus status = keyCodeStatus.get(keyCodeMap.get(keyCode));
		return status == KeyStatus.HELD || status == KeyStatus.PRESSED;
	}

	public final boolean isKeyJustReleased(int keyCode) {
		if (!keyCodeMap.containsKey(keyCode)) {
			throw new IllegalArgumentException("KeyCode is invalid.");
		}
		return keyCodeStatus.get(keyCodeMap.get(keyCode)) == KeyStatus.RELEASED;
	}
	
	
	
	
	//public List<Integer> getTypedKeyBuffer() {
		//return typedKeyBuffer;
	//}
	
	public List<KeyEvent> getTypedKeyEventBuffer() {
		return typedKeyEventBuffer;
	}
	
	
	
	
	
	
	public boolean isValidKeyCode(int keyCode) {
		return keyCodeMap.containsKey(keyCode);
	}
	
	public List<Integer> getValidKeyCodes(List<Integer> out) {
		for (Integer keyCode: keyCodeMap.keySet()) {
			out.add(keyCode);
		}
		return out;
	}
	
	public Map<Integer, String> getValidKeys(Map<Integer, String> out) {
		out.putAll(keyCodeMap);
		return out;
	}
	
	
	
	
	
	
	
	// KEYBOARD LISTENER EVENTS
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		typedKeyEventBuffer.add(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCodeMap.containsKey(keyCode)) {
			keyCodePressed.replace(keyCode, true);
			//typedKeyBuffer.add(keyCode);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCodeMap.containsKey(keyCode)) {
			keyCodePressed.replace(keyCode, false);
			
		}
	}
	
	
	protected enum KeyStatus {
		PRESSED,
		HELD,
		RELEASED,
		NONE,
	}


}
