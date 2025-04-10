package entity_temp;

// Intended for use to lightweight proxy objects that processes requests 
// on behalf of the memory-heavy systems that grants the requests.

public interface ProxyObject {

	abstract void deactivate();
	abstract boolean isDeactivated();
	
	/**
	 * Intended for use by classes implementing this interface.
	 * If proxy is deactivated, it throws an error.
	 */
	default void checkDeactivation() {
		if (isDeactivated()) {
			throw new IllegalStateException("This proxy is deactivated and can't be used further.");
		}
	}
}
