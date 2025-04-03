package utils;

import java.util.HashSet;

public class Emitter<T> {
    protected HashSet<Receiver<T>> listeners;
    protected String eventName;
    
    public Emitter(String eventName) {
    	this.eventName = eventName;
    	this.listeners = new HashSet<>();
    }

    public boolean connect(Receiver<T> listener) {
    	//if (eventName != null)
    	return listeners.add(listener);
    }

    public boolean disconnect(Receiver<T> listener) {
    	//if (eventName != null)
    	return listeners.remove(listener);
    }
 
    public void emit(T eventData) {
    	//if (eventName != null) {
    	for (Receiver<T> listener : listeners) {
    		listener.onSignalEmitted(eventName, eventData);
    	}
    	//}
    }
    
    public void clearListeners() {
    	listeners.clear();
    }
    /*
    public void deactivate() {
    	listeners.clear();
    	eventName = null;
    }
    */
}
