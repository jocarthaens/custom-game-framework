package collision;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import collision.CollisionSystem.Collider;

public class SpatialHashIndexer {
	protected float cellSize;
	protected float invertedCellSize;
	protected Map<Long, Set<Collider>> gridMap;
	protected Map<Collider, Set<Long>> colliderCells;
	protected Map<Collider, CellBounds> colliderBounds;
	protected Stack<BoundingBox> boxCache;
	
	protected SpatialHashIndexer(float cellSize) {
		this.cellSize = cellSize;
		this.invertedCellSize = 1f / cellSize;
		gridMap = new HashMap<>();
		colliderCells = new HashMap<>();
		colliderBounds = new HashMap<>();
	}
	
//	protected float getObjectMinX(Collider collider);
//	protected float getObjectMinY(Collider collider);
//	protected float getObjectWidth(Collider collider);
//	protected float getObjectHeight(Collider collider);
	
	public long cellKey(int x, int y) {
		return ( ((long) x) << 32 ) | (y & 0xffffffffL);
	}
	
	public String keyToCell(long key) {
		int x = (int) (key >>> 32);
		int y = (int) key;
		return "Cell: ("+x+", "+y+")";
	}
	
	public int getCellX(float posX) {
		return (int) (posX * invertedCellSize);
	}
	
	public int getCellY(float posY) {
		return (int) (posY * invertedCellSize);
	}
	
	public float getCellSize() {
		return cellSize;
	}
	
	
	public void insert(Collider collider) {
		if (collider.isDeactivated()) return;
		Set<Long> occupiedCells = getCoveredCells(collider);
		
		for (Long key : occupiedCells) {
        	if (gridMap.containsKey(key)) {
        		gridMap.get(key).add(collider);
	    	} else {
	    		Set<Collider> set = new HashSet<>();
	    		set.add(collider);
	    		gridMap.put(key, set);
	    	}
		}
		colliderCells.put(collider, occupiedCells);
		
		BoundingBox box = requestBox();
		collider.getBoundingBox(box);
		
		int minCellX = (int) Math.floor(box.minX() * invertedCellSize);
    	int minCellY = (int) Math.floor(box.minY() * invertedCellSize);
    	int widthCell = (int) Math.floor(box.width() * invertedCellSize);
    	int heightCell = (int) Math.floor(box.height() * invertedCellSize);
    	
    	CellBounds bounds = colliderBounds.get(collider);
    	if (bounds == null) {
    		bounds = new CellBounds();
    		colliderBounds.put(collider, bounds);
    	}
    	bounds.minX = minCellX;
		bounds.minY = minCellY;
		bounds.width = widthCell;
		bounds.height = heightCell;
		
		storeBox(box);
	}
	
	public void remove(Collider collider) {
		Set<Long> cells = colliderCells.remove(collider);
		if (cells != null) {
			for (Long key: cells) {
				Set<Collider> set = gridMap.get(key);
				set.remove(collider);
				if (set.isEmpty())
					gridMap.remove(key);
			}
			colliderBounds.remove(collider);
		}
		
	}
	
	public void update(Collider collider) {
		BoundingBox box = requestBox();
		collider.getBoundingBox(box);
		
		int minCellX = (int) Math.floor(box.minX() * invertedCellSize);
    	int minCellY = (int) Math.floor(box.minY() * invertedCellSize);
    	int widthCell = (int) Math.floor(box.width() * invertedCellSize);
    	int heightCell = (int) Math.floor(box.height() * invertedCellSize);
    	
    	CellBounds bounds = colliderBounds.get(collider);
    	
    	if (bounds != null && bounds.minX == minCellX 
    			&& bounds.minY == minCellY 
    			&& bounds.width == widthCell
    			&& bounds.height == heightCell) {
    		storeBox(box);
    		return;
    	}
    	
    	remove(collider);
    	insert(collider);
    	storeBox(box);
	}
	
	public Set<Collider> query(float minX, float minY, float width, float height, Set<Collider> out) {
		int minCellX = (int) Math.floor(minX * invertedCellSize);
    	int minCellY = (int) Math.floor(minY * invertedCellSize);
    	int maxCellX = (int) Math.floor((minX + width) * invertedCellSize);
    	int maxCellY = (int) Math.floor((minY + height) * invertedCellSize);
    	
    	for (int x = minCellX; x <= maxCellX; x++) {
    		for (int y = minCellY; y <= maxCellY; y++) {
    			long key = cellKey(x, y);
    			Set<Collider> cellObjects = gridMap.get(key);
    			if (cellObjects != null) {
    				out.addAll(cellObjects);
    			}
    		}
    	}
    	return out;
    }
	
	public boolean has(Collider collider) {
		return collider != null && colliderCells.containsKey(collider);
	}
	
	public Set<Collider> get(int cellX, int cellY, Set<Collider> out) {
		out.addAll( gridMap.get(cellKey(cellX, cellY)) );
		return out;
	}
    
	public void clear() {
		gridMap.clear();
		colliderCells.clear();
		colliderBounds.clear();
		boxCache.clear();
	}
	
	
	
	
	public Set<Collider> allObjects(Set<Collider> out) {
		for (Collider collider: colliderCells.keySet()) {
			out.add(collider);
		}
		return out;
	}
	
	public Set<Collider> getObjectsFromCell(int cellX, int cellY, Set<Collider> out) {
		long key = cellKey(cellX, cellY);
		out.addAll(gridMap.get(key));
		return out;
	}
	
	public Set<Long> getCoveredCells(Collider collider) {
		BoundingBox box = requestBox();
		collider.getBoundingBox(box);
		
		float minX = box.minX();
    	float minY = box.minY();
    	float width = box.width();
    	float height = box.height();
		
		int minCellX = (int) Math.floor(minX * invertedCellSize);
    	int minCellY = (int) Math.floor(minY * invertedCellSize);
    	int maxCellX = (int) Math.floor((minX + width) * invertedCellSize);
    	int maxCellY = (int) Math.floor((minY + height) * invertedCellSize);
    	
    	Set<Long> cells = new HashSet<Long>();
    	
    	for (int x = minCellX; x <= maxCellX; x++) {
    		for (int y = minCellY; y <= maxCellY; y++) {
    			cells.add(cellKey(x, y));
    		}
    	}
    	
    	storeBox(box);
    	
    	return cells;
	}
	
	
	
	
	protected BoundingBox requestBox() {
		BoundingBox data = null;
		if (!boxCache.isEmpty()) {
			data = boxCache.pop();
		}
		else {
			data = new BoundingBox(0, 0, 1, 1);
		}
		return data;
	}
	
	protected void storeBox(BoundingBox data) {
		data.set(0, 0, 1, 1);
		boxCache.push(data);
	}
	
	
	
	
	
	
	
    protected class CellBounds {
    	protected int minX, minY;
    	protected int width, height;
    }
}
