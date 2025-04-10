package entity_temp;

import java.util.HashMap;
import java.util.List;

import entity_temp.GameComponentManager.ComponentRequestHandler;
import utils.Emitter;
import utils.BitVector;

// The main GameObject class; Contains a map of GameComponents of different types that will be modified by different game systems.
// Also includes a list of registered listeners that will listen for specific components that are added or removed from the entity instance.

public final class Entity {
	HashMap<Class<? extends GameComponent>, GameComponent> components;
	BitVector componentSet;
	ComponentRequestHandler compHandler;
	Emitter<Class<? extends GameComponent>> addListener;
	Emitter<Class<? extends GameComponent>> removeListener;
	//UID<Entity> id; //include entity uid
	
	public Entity() {
		components = new HashMap<>();
		componentSet = new BitVector();
		addListener = new Emitter<Class<? extends GameComponent>>("onAddComponent");
		removeListener = new Emitter<Class<? extends GameComponent>>("onRemoveComponent");
		//id where?
		//how to set componentHandler
	}

	
	
	public <T extends GameComponent>  void addComponent(Class<T> componentClass) {
		int bit = compHandler.getBitPosition(componentClass);
		
		if (bit == -1) {
			throw new IllegalArgumentException("Component type "+componentClass+" cannot be added because it is not registered.");
		}
		
		if (componentSet.get(bit) == false) { //only add if entity_temp contains the specified component type
			T gameComponent = (T) compHandler.provideComponent(componentClass);
			components.putIfAbsent(componentClass, gameComponent);
			componentSet.set(bit);
			
			addListener.emit(componentClass);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends GameComponent>  void  removeComponent(Class<T> componentClass) {
		int bit = compHandler.getBitPosition(componentClass);
		
		if (bit == -1) {
			throw new IllegalArgumentException("Component type "+componentClass+" doesn't exist in the registry.");
		}
		
		T gameComponent = (T) components.remove(componentClass);
		if (gameComponent == null) {
			return;
		}
		
		componentSet.clear(bit);
		compHandler.returnComponent(gameComponent);
		
		removeListener.emit(componentClass);
	}
	
	public boolean hasComponent(Class<? extends GameComponent> componentClass) {
		return components.containsKey(componentClass);
	}

	public GameComponent getComponent(Class<? extends GameComponent> componentClass) {
		return components.get(componentClass);
	}
	
	public void getAllComponents(List<GameComponent> container) {
		for (GameComponent comp: components.values()) {
			container.add(comp);
		}
	}
	
	public void clear() {
		for (GameComponent comp: components.values()) {
			compHandler.returnComponent(comp);
		}
		components.clear();
	}
	
	
	
	
	public Emitter<Class<? extends GameComponent>> getAddComponentEmitter() {
		return addListener;
	}
	
	public Emitter<Class<? extends GameComponent>> getRemoveComponentEmitter() {
		return removeListener;
	}
	
	
	
	
	
	
	//dispose()
	
	
	
}
