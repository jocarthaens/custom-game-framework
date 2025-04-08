package utils;

import java.util.HashSet;
import java.util.LinkedList;

// Initializes and manages its own pool of objects

public abstract class ObjectPool<T> {
	protected LinkedList<T> pooledObjects;
	protected HashSet<T> usedObjects; 
	protected int maxPoolSize;
	protected int initialSize;
	
	public ObjectPool(int initialSize, int maxPoolSize){
		this.initialSize = initialSize;
		this.maxPoolSize = maxPoolSize;
		pooledObjects = new LinkedList<T>();
		usedObjects = new HashSet<T>();
		_initialize();
	}
	
	public final int initialPoolSize() {
		return initialSize;
	}
	
	public final int maxPoolSize() {
		return maxPoolSize;
	}
	
	public final int reservesCount() {
		return pooledObjects.size();
	}
	
	public final int utilizedCount() {
		return usedObjects.size();
	}
	
	public final int totalCount() {
		return usedObjects.size() + pooledObjects.size();
	}
	
	
	
	
	
	protected final T borrow() {
		T data = null;
		if (!pooledObjects.isEmpty()) {
			data = pooledObjects.pop();
			activate(data);}
		else {
			data = create();}
		usedObjects.add(data);
		return data;
	}
	
	@SuppressWarnings("unchecked")
	protected final boolean give(Object component) {
		if (usedObjects.contains(component)) {
			usedObjects.remove(component);
			reset((T) component);
			if (pooledObjects.size() < maxPoolSize) {
				pooledObjects.addLast((T) component);
			}
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	public final void clearReserves() {
		pooledObjects.clear();
		_initialize();
	}
	
	@SuppressWarnings("unchecked")
	public final void resetAndReturnAll() {
		T[] objects = (T[]) usedObjects.toArray();
		for( int i = 0; i < objects.length; i++) {
			reset(objects[i]);
			if (pooledObjects.size() < maxPoolSize) {
				pooledObjects.addLast(objects[i]);
			}
		}
	}
	
	public final void clearAll() {
		resetAndReturnAll();
		pooledObjects.clear();
		_initialize();
	}
	
	private final void _initialize() {
		for (int i = 0; i < this.initialSize; i++) {
			pooledObjects.push(create());
		}
	}
	
	
	
	
	
	
	protected abstract void reset(T data);
	protected abstract void activate(T data);
	protected abstract T create();
}
