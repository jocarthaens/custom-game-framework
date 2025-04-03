package gfx.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;;

public class GUIMapper {
	protected float GUICellSize;
	protected float invertedGUICellSize;
	protected Map<Long, Set<GUIObject>> gridMap;
	protected Map<GUIObject, Set<Long>> uiCells;
	protected Map<GUIObject, GUICellBounds> uiBounds;
	
	public GUIMapper(float GUICellSize) {
		this.GUICellSize = GUICellSize;
		this.invertedGUICellSize = 1f / GUICellSize;
		gridMap = new HashMap<>();
		uiCells = new HashMap<>();
		uiBounds = new HashMap<>();
	}
	
	
	protected float getGUIMinX(GUIObject ui) {
		return ui.globalX();
	}
	
	protected float getGUIMinY(GUIObject ui) {
		return ui.globalY();
	}
	
	protected float getGUIWidth(GUIObject ui) {
		return ui.getWidth();
	}
	
	protected float getGUIHeight(GUIObject ui) {
		return ui.getHeight();
	}
	
	
	public long cellKey(int x, int y) {
		return ( ((long) x) << 32 ) | (y & 0xffffffffL);
	}
	
	public String keyToCell(long key) {
		int x = (int) (key >>> 32);
		int y = (int) key;
		return "("+x+", "+y+")";
	}
	

	public int getCellX(float posX) {
		return (int) (posX * invertedGUICellSize);
	}
	
	public int getCellY(float posY) {
		return (int) (posY * invertedGUICellSize);
	}
	
	public float getCellSize() {
		return GUICellSize;
	}
	
	
	public void insert(GUIObject ui) {
		Set<Long> occupiedCells = getCoveredCells(ui);
		
		for (Long key : occupiedCells) {
        	if (gridMap.containsKey(key)) {
        		gridMap.get(key).add(ui);
	    	} else {
	    		Set<GUIObject> set = new HashSet<>();
	    		set.add(ui);
	    		gridMap.put(key, set);
	    	}
		}
		uiCells.put(ui, occupiedCells);
		
		int minCellX = (int) Math.floor(getGUIMinX(ui) * invertedGUICellSize);
    	int minCellY = (int) Math.floor(getGUIMinY(ui) * invertedGUICellSize);
    	int widthCell = (int) Math.floor(getGUIWidth(ui) * invertedGUICellSize);
    	int heightCell = (int) Math.floor(getGUIHeight(ui) * invertedGUICellSize);
    	
    	GUICellBounds bounds = uiBounds.get(ui);
    	if (bounds == null) {
    		bounds = new GUICellBounds();
    		uiBounds.put(ui, bounds);
    	}
    	bounds.minX = minCellX;
		bounds.minY = minCellY;
		bounds.width = widthCell;
		bounds.height = heightCell;
	}
	
	public void remove(GUIObject ui) {
		Set<Long> cells = uiCells.remove(ui);
		if (cells != null) {
			for (Long key: cells) {
				Set<GUIObject> set = gridMap.get(key);
				set.remove(ui);
				if (set.isEmpty())
					gridMap.remove(key);
			}
			uiBounds.remove(ui);
		}
		
	}
	
	public void update(GUIObject ui) {
		int minCellX = (int) Math.floor(getGUIMinX(ui) * invertedGUICellSize);
    	int minCellY = (int) Math.floor(getGUIMinY(ui) * invertedGUICellSize);
    	int widthCell = (int) Math.floor(getGUIWidth(ui) * invertedGUICellSize);
    	int heightCell = (int) Math.floor(getGUIHeight(ui) * invertedGUICellSize);
    	
    	GUICellBounds bounds = uiBounds.get(ui);
    	
    	if (bounds != null && bounds.minX == minCellX 
    			&& bounds.minY == minCellY 
    			&& bounds.width == widthCell
    			&& bounds.height == heightCell) {
    		return;
    	}
    	
    	remove(ui);
    	insert(ui);
	}
	
	public Set<GUIObject> query(float minX, float minY, float width, float height, Set<GUIObject> out) {
		int minCellX = (int) Math.floor(minX * invertedGUICellSize);
    	int minCellY = (int) Math.floor(minY * invertedGUICellSize);
    	int maxCellX = (int) Math.floor((minX + width) * invertedGUICellSize);
    	int maxCellY = (int) Math.floor((minY + height) * invertedGUICellSize);
    	
    	for (int x = minCellX; x <= maxCellX; x++) {
    		for (int y = minCellY; y <= maxCellY; y++) {
    			long key = cellKey(x, y);
    			Set<GUIObject> cellObjects = gridMap.get(key);
    			if (cellObjects != null) {
    				out.addAll(cellObjects);
    			}
    		}
    	}
    	return out;
    }
	
	public boolean has(GUIObject ui) {
		return ui != null && uiCells.containsKey(ui);
	}
	
	public Set<GUIObject> get(int cellX, int cellY, Set<GUIObject> out) {
		out.addAll( gridMap.get(cellKey(cellX, cellY)) );
		return out;
	}
    
	public void clear() {
		gridMap.clear();
		uiCells.clear();
		uiBounds.clear();
	}
	
	
	
	
	public Set<GUIObject> allObjects(Set<GUIObject> out) {
		for (GUIObject ui: uiCells.keySet()) {
			out.add(ui);
		}
		return out;
	}
	
	public Set<GUIObject> getObjectsFromCell(int cellX, int cellY, Set<GUIObject> out) {
		long key = cellKey(cellX, cellY);
		out.addAll(gridMap.get(key));
		return out;
	}
	
	public Set<Long> getCoveredCells(GUIObject ui) {
		float minX = getGUIMinX(ui);
    	float minY = getGUIMinY(ui);
    	float width = getGUIWidth(ui);
    	float height = getGUIHeight(ui);
		
		int minCellX = (int) Math.floor(minX * invertedGUICellSize);
    	int minCellY = (int) Math.floor(minY * invertedGUICellSize);
    	int maxCellX = (int) Math.floor((minX + width) * invertedGUICellSize);
    	int maxCellY = (int) Math.floor((minY + height) * invertedGUICellSize);
    	
    	Set<Long> cells = new HashSet<Long>();
    	
    	for (int x = minCellX; x <= maxCellX; x++) {
    		for (int y = minCellY; y <= maxCellY; y++) {
    			cells.add(cellKey(x, y));
    		}
    	}
    	return cells;
	}
	
	
	
	
	
	
    protected class GUICellBounds {
    	protected int minX, minY;
    	protected int width, height;
    }
}
