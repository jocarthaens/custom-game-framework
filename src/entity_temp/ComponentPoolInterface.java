package entity_temp;

// Base interface for all ComponentPools that manages a pool of GameComponents of the same type.

public interface ComponentPoolInterface<T extends GameComponent> {
	
	public abstract Class<T> getComponentClass();
	
	public abstract T provideComponent();
	
	public abstract void returnComponent(Object component);
}
