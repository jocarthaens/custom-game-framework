package gfx.sprite_rendering;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;


public class SpriteRenderingSystem {
	protected HashMap<RenderUID, RenderContainer> rendererMap;
	protected HashSet<Renderable> uniqueRenderables;
	protected ArrayList<RenderContainer> visibleObjects; // objects that can be rendered inside camera
	protected Stack<RenderContainer> reserved;
	protected ArrayList<RenderUID> removeQueue;
	
	protected Comparator<RenderContainer> compareYSort;
	protected Comparator<RenderContainer> compareZIndex;
	protected RenderRequestHandler renderServer;
	
	
	public SpriteRenderingSystem() {
		rendererMap = new HashMap<>();
		uniqueRenderables = new HashSet<>();
		visibleObjects = new ArrayList<>();
		reserved = new Stack<>();
		removeQueue = new ArrayList<>();
		compareYSort = new Comparator<RenderContainer>() {
			@Override
			public int compare(RenderContainer r1, RenderContainer r2) {
				int result = Integer.compare(r1._ySort, r2._ySort);
				return result;
			}
		};
		compareZIndex = new Comparator<RenderContainer>() {
			@Override
			public int compare(RenderContainer r1, RenderContainer r2) {
				int result = Integer.compare(r1.zIndex, r2.zIndex);
				return result;
			}
		};
		renderServer = new RenderRequestHandler();
	}
	

	
	
	
	public RenderRequestHandler getRequestHandler() {
		return renderServer;
	}
	
	
	/// 
	protected RendererProxy addRenderObject(float cx, float cy, float width, float height, 
					int zIndex, int ySortOffset, Renderable render) {
		
		if (uniqueRenderables.contains(render) == false) {
			RenderUID idKey = new RenderUID();
			RendererProxy proxy = new RendererProxy(idKey);
			RenderContainer renderObj = provideContainer(cx, cy, width, height, zIndex, ySortOffset, render, proxy);
			rendererMap.putIfAbsent(idKey, renderObj);
			uniqueRenderables.add(render);
			return proxy;
		}
		try {
			throw new IllegalArgumentException("Renderable object is already added in the system.");
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected RenderContainer provideContainer(float cx, float cy, float width, float height, 
					int zIndex, int ySortOffset, Renderable render, RendererProxy proxy) {
		if (!reserved.isEmpty()) {
			RenderContainer renderObj = reserved.pop();
			renderObj.isEnabled = true;
			renderObj.zIndex = zIndex;
			renderObj.ySortOffset = ySortOffset;
			renderObj.cx = cx;
			renderObj.cy = cy;
			renderObj.width = width;
			renderObj.height = height;
			renderObj._ySort = (int) (ySortOffset + cy);
			renderObj.renderable = render;
			renderObj.proxy = proxy;
			return renderObj;
		}
		return new RenderContainer(cx, cy, width, height, zIndex, ySortOffset, render, proxy);
	}
	
	protected void removeRenderObject(RenderUID id) {
		RenderContainer cont = rendererMap.get(id);
		if (cont != null) {
			cont.proxy.deactivate();
		}
	}
	
	protected void deferredRemove(RenderUID id) {
		if (id != null)
			removeQueue.add(id);
	}
	
	protected RendererProxy getRenderProxy(RenderUID id) {
		if (id != null && rendererMap.containsKey(id)) {
			RenderContainer renderObj = rendererMap.get(id);
			return renderObj.proxy;
		}
		return null;
	}
	
	protected RendererProxy getRenderProxy(Renderable render) { // implement faster way to obtain proxy given renderable
		if (render != null) {
			for (RenderContainer container: rendererMap.values()) {
				if (container.renderable == render) {
					return container.proxy;
				}
			}
		}
		return null;
	}

	protected boolean hasProxy(RenderUID id) {
		if (id != null && rendererMap.containsKey(id)) {
			return true;
		}
		return false;
	}
	
	protected boolean hasProxy(Renderable render) {
		if (render != null) {
			for (RenderContainer container: rendererMap.values()) {
				if (container.renderable == render) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected void reset() { // unfinished
		for (RenderContainer renderObj: rendererMap.values()) {
			renderObj.proxy.deactivate();
		}
	}
	
	

	
	
	
	
	
	
	
	
	public void update(SpriteCamera spriteCamera) {
		
		// removal of deactivated objects
		for (RenderUID id: removeQueue) {
			if (rendererMap.containsKey(id)) {
				RenderContainer renderObj = rendererMap.remove(id);
				renderObj.isEnabled = false;
				renderObj.zIndex = 0;
				renderObj.ySortOffset = 0;
				renderObj.cx = 0;
				renderObj.cy = 0;
				renderObj.width = 1;
				renderObj.height = 1;
				renderObj._ySort = 0;
				uniqueRenderables.remove(renderObj.renderable);
				renderObj.renderable = null;
				renderObj.proxy = null;
				reserved.add(renderObj);
			}
		}
		removeQueue.clear();
		
		// adding list of objects viewable from camera to visibleList
		for (RenderContainer renderObj: rendererMap.values()) {
			renderObj._ySort = (int) (renderObj.ySortOffset + renderObj.cy);
			int minX = (int) (renderObj.cx - renderObj.width * 0.5);
			int minY = (int) (renderObj.cy - renderObj.height * 0.5);
			
			if (spriteCamera.withinView(minX, minY, renderObj.width, renderObj.height))
				visibleObjects.add(renderObj);
		}
		
		// sorting of visible objects by YSort and ZIndex
		visibleObjects.sort(compareYSort);
		visibleObjects.sort(compareZIndex);
		
	}
	
	public void render(Graphics2D g2d) {
		Composite origComp = g2d.getComposite();
		
		for (RenderContainer renderObj: visibleObjects) {
			renderObj.renderable.render(g2d);
			g2d.setComposite(origComp);
		}
		visibleObjects.clear();
	}
	
	
	
	
	
	protected class RenderContainer {
		protected boolean isEnabled = true;
		protected float cx, cy;
		protected float width = 1, height = 1;
		protected int zIndex;
		protected int ySortOffset;
		protected Renderable renderable;
		protected RendererProxy proxy;
		protected int _ySort;
		
		
		RenderContainer(float cx, float cy, float width, float height, int zIndex, int ySortOffset, Renderable render, RendererProxy proxy) {
			this.cx = cx;
			this.cy = cy;
			this.width = width;
			this.height = height;
			this.zIndex = zIndex;
			this.ySortOffset = ySortOffset;
			this._ySort = (int) (ySortOffset + cy);
			this.renderable = render;
			this.proxy = proxy;
		}
	}
	
	
	
	
	
	
	
	public class RendererProxy {
		protected RenderUID assignID;
		
		RendererProxy(RenderUID assignID) {
			this.assignID = assignID;
		}
		
		public void setEnabled(boolean enabled) {
			checkDeactivation();
			RenderContainer render = rendererMap.get(assignID);
			render.isEnabled = enabled;
		}
		
		
		public void setPosition(float x, float y) {
			checkDeactivation();
			RenderContainer render = rendererMap.get(assignID);
			render.cx = x;
			render.cy = y;
		}
		
		public void movePosition(float x, float y) {
			checkDeactivation();
			RenderContainer render = rendererMap.get(assignID);
			render.cx += x;
			render.cy += y;
		}
		
		public void setZIndex(int zIndex) {
			checkDeactivation();
			rendererMap.get(assignID).zIndex = zIndex;
		}
		
		public void setYSortOffset(int yOffset) {
			checkDeactivation();
			rendererMap.get(assignID).ySortOffset = yOffset;
		}
		
		public void setSize(float width, float height) {
			checkDeactivation();
			RenderContainer render = rendererMap.get(assignID);
			render.width = width;
			render.height = height;
		}
		
		
		
		
		
		public boolean isEnabled() {
			checkDeactivation();
			return rendererMap.get(assignID).isEnabled;
		}
		
		
		
		
		
		public float getX() {
			checkDeactivation();
			return rendererMap.get(assignID).cx;
		}
		
		public float getY() {
			checkDeactivation();
			return rendererMap.get(assignID).cy;
		}

		
		
		
		public int getZIndex() {
			checkDeactivation();
			return rendererMap.get(assignID).zIndex;
		}
		
		public int getYSortOffset() {
			checkDeactivation();
			return rendererMap.get(assignID).ySortOffset;
		}
		
		public float getWidth() {
			checkDeactivation();
			return rendererMap.get(assignID).width;
		}
		
		public float getHeight() {
			checkDeactivation();
			return rendererMap.get(assignID).height;
		}
		
		public float getRectXPosition() {
			checkDeactivation();
			RenderContainer render = rendererMap.get(assignID);
			return (render.cx - render.width * 0.5f);
		}
		
		public float getRectYPosition() {
			checkDeactivation();
			RenderContainer render = rendererMap.get(assignID);
			return (render.cy - render.height * 0.5f);
		}
		
		public RenderUID getID() {
			return this.assignID;
		}
		
		//@Override
		public void deactivate() {
			if (assignID != null) {
				deferredRemove(assignID);
				assignID = null;
			}
		}

		//@Override
		public boolean isDeactivated() {
			return assignID == null;
		}
		
		//@Override
		protected void checkDeactivation() {
			if (isDeactivated()) {
				throw new IllegalStateException("This proxy is deactivated and can't be used further.");
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public class RenderRequestHandler {
		
		protected RenderRequestHandler() {}
		
		public RendererProxy addRenderObject(float cx, float cy, float width, float height, int zIndex, int ySortOffset, Renderable render) {
			return SpriteRenderingSystem.this.addRenderObject(cx, cy, width, height, zIndex, ySortOffset, render);
		}
		
		public RendererProxy addRenderObject(float cx, float cy, float width, float height, Renderable render) {
			return SpriteRenderingSystem.this.addRenderObject(cx, cy, width, height, 0, 0, render);
		}
		
		public RendererProxy addRenderObject(Renderable render) {
			return SpriteRenderingSystem.this.addRenderObject(0, 0, 1, 1, 0, 0, render);
		}
		
		public void removeRenderObject(RenderUID id) {
			SpriteRenderingSystem.this.removeRenderObject(id);
		}
		
		public RendererProxy getRenderProxy(RenderUID id) {
			return SpriteRenderingSystem.this.getRenderProxy(id);
		}
		
		public RendererProxy getRenderProxy(Renderable render) {
			return SpriteRenderingSystem.this.getRenderProxy(render);
		}

		public boolean hasProxy(RenderUID id) {
			return SpriteRenderingSystem.this.hasProxy(id);
		}
		
		public boolean hasProxy(Renderable render) {
			return SpriteRenderingSystem.this.hasProxy(render);
		}
		
		public void reset() {
			SpriteRenderingSystem.this.reset();
		}
		
	}
}
