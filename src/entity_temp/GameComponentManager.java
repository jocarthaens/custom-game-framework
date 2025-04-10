package entity_temp;

import java.util.HashMap;

import utils.BitVector;

// Manages all types of game components. This class provides different types of GameComponents 
// that are registered on its repository, and stores it to the corresponding component pool
// who provided the specified component, when user returns it back.

public class GameComponentManager {
	protected HashMap<Class<? extends GameComponent>, ComponentPoolInterface<?>> componentMap;
	protected HashMap<Class<?>, Integer> assignedComponentTypes;
	protected ComponentRequestHandler handler;
	protected int nextIndex = 0;
	
	
	public GameComponentManager() {
		componentMap = new HashMap<>();
		assignedComponentTypes = new HashMap<>();
		handler = new ComponentRequestHandler();
	}
	
	
	
	public void registerComponentType(ComponentPoolInterface<?> pool) {
		Class<? extends GameComponent> compClass = pool.getComponentClass();
		if (!componentMap.containsKey(compClass)) {
			componentMap.putIfAbsent(compClass, pool);
			assignedComponentTypes.putIfAbsent(compClass, nextIndex++);
		}
	}
	
	
	public ComponentRequestHandler getHandler() {
		return handler;
	}
	
	
	
	
	
	
	public class ComponentRequestHandler {
		
		
		protected ComponentRequestHandler() {}
		
		
		@SuppressWarnings("unchecked")
		public <T extends GameComponent> T provideComponent(Class<T> compClass) {
			if (componentMap.containsKey(compClass)) {
				return (T) componentMap.get(compClass).provideComponent();
			}
			return null;
		}
		
		public <T> void returnComponent(T component) {
			Class<?> compClass = component.getClass();
			if (componentMap.containsKey(compClass)) {
				componentMap.get(compClass).returnComponent(component);
			}
		}
		
		// CREATE CUSTOM BITSET CLASS FOR MORE FUCTIONALITY (ContainsAll())
		public <T extends GameComponent> BitVector getComponentBits(Class<T>[] componentClasses) {
			BitVector compBits = new BitVector();
			for (int i = 0; i < componentClasses.length; i++) {
				if (!componentMap.containsKey(componentClasses[i])) continue;
				compBits.set(assignedComponentTypes.get(componentClasses[i]));
			}
			return compBits;
		}
		
		public <T extends GameComponent> int getBitPosition(Class<T> componentClass) {
			int bit = assignedComponentTypes.containsKey(componentClass) ? assignedComponentTypes.get(componentClass) : -1;
			return bit;
		}
		
	}
	
}
