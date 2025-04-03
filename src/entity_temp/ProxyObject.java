package entity_temp;

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
