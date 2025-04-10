package entity_temp;

import utils.ObjectPool;

// Abstract class for managing components of the same type via object pooling.

public abstract class AbstractComponentPool<T extends GameComponent> extends ObjectPool<T> implements ComponentPoolInterface<T>{
	
	
	
	public AbstractComponentPool(int initialSize, int maxPoolSize) {
		super(initialSize, maxPoolSize);
	}
	
	
	@Override
	public abstract Class<T> getComponentClass();

	@Override
	public T provideComponent() {
		return borrow();
	}
	
	@Override
	public void returnComponent(Object component) {
		give(component);
	}
	

	
	protected abstract T create();
	protected abstract void reset(T component);
	protected abstract void activate(T component);

}
