package entity_temp;

import java.util.Iterator;
import java.util.List;

import utils.BitVector;

public class EntityManager {
	//pools entities and emits signals on add/remove/replace components and add/remove entities
	EntityPool entityPool;
	GameComponentManager componentManager;
	
	
	public EntityManager() {
		componentManager = new GameComponentManager();
		entityPool = new EntityPool(50, 500, componentManager.getHandler());
	}
	
	
	
	
	
	public Entity provideNewEntity() {
		return entityPool.provideEntity();
	}
	
	public void returnEntity(Entity entity) {
		entityPool.returnEntity(entity);
	}
	
	//provide entity_temp list filtered by provided component Bitset
	//provide component Bitset given an array of component classes
	//replace list with custom made data structures
	public void getEntitiesWithBits(BitVector allComponentBits, List<Entity> entitiesContainer) {
		Iterator<Entity> iterate = entityPool.getRegisteredEntities().iterator();
		while(iterate.hasNext()) {
			Entity entity = iterate.next();
			if (entity.componentSet.containsAll(allComponentBits)) {
				entitiesContainer.add(entity);
			}
		}
	 }
	 
	public void getAllEntities(List<Entity> entitiesContainer) {
		Iterator<Entity> iterate = entityPool.getRegisteredEntities().iterator();
		while(iterate.hasNext()) {
			entitiesContainer.add(iterate.next());
		}
	 }
		 
	public boolean isEntityRegistered(Entity entity) {
		return entityPool.getRegisteredEntities().contains(entity);
	}
	
	
	

	
	
	
	
	
	
	public  void  registerComponent(ComponentPoolInterface<?> componentPool) {
		componentManager.registerComponentType(componentPool);
	}
	
	public <T extends GameComponent>  int  getBitPosition(Class<T> componentClass) {
		return componentManager.getHandler().getBitPosition(componentClass);
	}
	
	public <T extends GameComponent> BitVector getComponentBits(Class<T>[] componentClasses) {
		return componentManager.getHandler().getComponentBits(componentClasses);
	}
	
}
