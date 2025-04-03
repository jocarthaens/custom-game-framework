package entity_temp;


public interface ComponentPoolInterface<T extends GameComponent> {
	
	public abstract Class<T> getComponentClass();
	
	public abstract T provideComponent();
	
	public abstract void returnComponent(Object component);
}
