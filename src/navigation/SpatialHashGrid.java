package navigation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// This class is used for mapping instances with spatial properties in cells using hash maps instead of multi-dimensional arrays.
// Utilizes efficient use of Long to store x and y coordinates of a cell.

public abstract class SpatialHashGrid<T> { // what to do if T is mutable?
	protected float cellSize;
	protected float invertedCellSize;
	protected Map<Long, Set<T>> gridMap;
	protected Map<T, Set<Long>> objCells;
	protected Map<T, CellBounds> objBounds;
	
	public SpatialHashGrid(float cellSize) {
		this.cellSize = cellSize;
		this.invertedCellSize = 1f / cellSize;
		gridMap = new HashMap<>();
		objCells = new HashMap<>();
		objBounds = new HashMap<>();
	}
	
	
	protected abstract float getObjectMinX(T obj);
	protected abstract float getObjectMinY(T obj);
	protected abstract float getObjectWidth(T obj);
	protected abstract float getObjectHeight(T obj);
	
	
	public long cellKey(int x, int y) {
		return ( ((long) x) << 32 ) | (y & 0xffffffffL);
	}
	
	public String keyToCell(long key) {
		int x = (int) (key >>> 32);
		int y = (int) key;
		return "("+x+", "+y+")";
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
	
	
	public void insert(T obj) {
		Set<Long> occupiedCells = getCoveredCells(obj);
		
		for (Long key : occupiedCells) {
        	if (gridMap.containsKey(key)) {
        		gridMap.get(key).add(obj);
	    	} else {
	    		Set<T> set = new HashSet<>();
	    		set.add(obj);
	    		gridMap.put(key, set);
	    	}
		}
		objCells.put(obj, occupiedCells);
		
		int minCellX = (int) Math.floor(getObjectMinX(obj) * invertedCellSize);
    	int minCellY = (int) Math.floor(getObjectMinY(obj) * invertedCellSize);
    	int widthCell = (int) Math.floor(getObjectWidth(obj) * invertedCellSize);
    	int heightCell = (int) Math.floor(getObjectHeight(obj) * invertedCellSize);
    	
    	CellBounds bounds = objBounds.get(obj);
    	if (bounds == null) {
    		bounds = new CellBounds();
    		objBounds.put(obj, bounds);
    	}
    	bounds.minX = minCellX;
		bounds.minY = minCellY;
		bounds.width = widthCell;
		bounds.height = heightCell;
	}
	
	public void remove(T obj) {
		Set<Long> cells = objCells.remove(obj);
		if (cells != null) {
			for (Long key: cells) {
				Set<T> set = gridMap.get(key);
				set.remove(obj);
				if (set.isEmpty())
					gridMap.remove(key);
			}
			objBounds.remove(obj);
		}
		
	}
	
	public void update(T obj) {
		int minCellX = (int) Math.floor(getObjectMinX(obj) * invertedCellSize);
    	int minCellY = (int) Math.floor(getObjectMinY(obj) * invertedCellSize);
    	int widthCell = (int) Math.floor(getObjectWidth(obj) * invertedCellSize);
    	int heightCell = (int) Math.floor(getObjectHeight(obj) * invertedCellSize);
    	
    	CellBounds bounds = objBounds.get(obj);
    	
    	if (bounds != null && bounds.minX == minCellX 
    			&& bounds.minY == minCellY 
    			&& bounds.width == widthCell
    			&& bounds.height == heightCell) {
    		return;
    	}
    	
    	remove(obj);
    	insert(obj);
	}
	
	public Set<T> query(float minX, float minY, float width, float height, Set<T> out) {
		int minCellX = (int) Math.floor(minX * invertedCellSize);
    	int minCellY = (int) Math.floor(minY * invertedCellSize);
    	int maxCellX = (int) Math.floor((minX + width) * invertedCellSize);
    	int maxCellY = (int) Math.floor((minY + height) * invertedCellSize);
    	
    	for (int x = minCellX; x <= maxCellX; x++) {
    		for (int y = minCellY; y <= maxCellY; y++) {
    			long key = cellKey(x, y);
    			Set<T> cellObjects = gridMap.get(key);
    			if (cellObjects != null) {
    				out.addAll(cellObjects);
    			}
    		}
    	}
    	return out;
    }
	
	public boolean has(T obj) {
		return obj != null && objCells.containsKey(obj);
	}
	
	public Set<T> get(int cellX, int cellY, Set<T> out) {
		out.addAll( gridMap.get(cellKey(cellX, cellY)) );
		return out;
	}
    
	public void clear() {
		gridMap.clear();
		objCells.clear();
		objBounds.clear();
	}
	
	
	
	
	public Set<T> allObjects(Set<T> out) {
		for (T obj: objCells.keySet()) {
			out.add(obj);
		}
		return out;
	}
	
	public Set<T> getObjectsFromCell(int cellX, int cellY, Set<T> out) {
		long key = cellKey(cellX, cellY);
		out.addAll(gridMap.get(key));
		return out;
	}
	
	public Set<Long> getCoveredCells(T obj) {
		float minX = getObjectMinX(obj);
    	float minY = getObjectMinY(obj);
    	float width = getObjectWidth(obj);
    	float height = getObjectHeight(obj);
		
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
    	return cells;
	}
	
	
	
	
	
	
    protected class CellBounds {
    	protected int minX, minY;
    	protected int width, height;
    }
}
