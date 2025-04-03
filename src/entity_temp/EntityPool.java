package entity_temp;

import java.util.HashSet;

import entity_temp.GameComponentManager.ComponentRequestHandler;
import utils.ObjectPool;
import utils.BitVector;

public class EntityPool extends ObjectPool<Entity> {
	HashSet<Entity> registeredEntities; // added to protect original set from being modified, create immutable set instead
	ComponentRequestHandler handler;
	
	public EntityPool(int initialSize, int maxPoolSize, ComponentRequestHandler requestHandler) {
		super(initialSize, maxPoolSize);
		registeredEntities = new HashSet<>();
		handler = requestHandler;
	}
	
	public Entity provideEntity() {
		Entity newEntity = borrow();
		if (newEntity != null) {
			registeredEntities.add(newEntity);
		}
		return newEntity;
	}
	
	public boolean returnEntity(Object entity) {
		if (give(entity)) { // if specified entity_temp is part of the pool
			return registeredEntities.remove(entity);
		}
		return false;
	}
	
	protected HashSet<Entity> getRegisteredEntities() {
		return registeredEntities;
	}

	
	
	
	@Override
	protected void reset(Entity data) {
		for (GameComponent comp: data.components.values()) {
			handler.returnComponent(comp);
		}
		data.components.clear();
		data.addListener.clearListeners();
		data.removeListener.clearListeners();
		data.componentSet = null;
		data.compHandler = null;
	}

	@Override
	protected void activate(Entity data) {
		data.compHandler = handler;
		data.componentSet = new BitVector();
		// TODO Auto-generated method stub
	}

	@Override
	protected Entity create() {
		Entity entity = new Entity();
		entity.compHandler = handler;
		// TODO Auto-generated method stub
		return entity;
	}

	
}
