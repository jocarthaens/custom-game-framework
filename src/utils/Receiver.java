package utils;

import java.util.function.BiConsumer;

public class Receiver<T> {
	protected BiConsumer<String, T> command;
	
	public Receiver(BiConsumer<String, T> func) {
		command = func;
	}
	
    public void onSignalEmitted(String eventName, T event) {
    	if (command != null)
    		command.accept(eventName, event);
    }
    
    public void deactivate() {
    	command = null;
    }
}
