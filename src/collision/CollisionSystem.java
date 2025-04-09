package collision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import collision.CollisionEventListener.CollisionEvent;

// Manages its collider objects; Checks and resolves collisions per frame.

public class CollisionSystem {
	
	protected HashMap<ColliderUID, ColliderContainer> registeredColliders;
	protected LinkedList<ColliderContainer> reservedColliders;
	
	// stores new collider containers to be in the registered colliders
	protected List<ColliderContainer> addBuffer;
	// stores deactivated colliders that will be transferred to delete buffer on the next frame
	protected List<ColliderUID> removeBuffer;
	// stores transferred deactivated colliders to be finally deleted in batches
	protected List<ColliderUID> deleteBuffer;
	
	protected Set<Collider> modified; // used for storing dirty colliders to be updated in broadphase
	
	protected Set<ColliderUID> objectColliders; // objects will be collision-checked every frame
	protected Set<ColliderUID> tileColliders; // tiles won't be checked on for collisions
	
	protected HashMap<ColliderPairKey, ManifoldData> resolvableManifolds;
	
	protected SpatialHashIndexer broadphase;
	protected SATCollisionDetector narrowphase;
	
	
	public CollisionSystem(float cellSize) {
		registeredColliders = new HashMap<>();
		reservedColliders = new LinkedList<>();
		
		addBuffer = new ArrayList<>();
		removeBuffer = new ArrayList<>();
		modified = new HashSet<>();
		
		objectColliders = new HashSet<>();
		tileColliders = new HashSet<>();
		
		resolvableManifolds = new HashMap<>();
		
		broadphase = new SpatialHashIndexer(cellSize);
		narrowphase = new SATCollisionDetector();
	}
	
	
	
	
	public Collider requestTileCollider(ColliderShape2D<?> shape) {
		ColliderContainer container = getCollider(shape, true);
		addBuffer.add(container);
		tileColliders.add(container.proxy.assignID);
		container.isTile = true;
		container.isSensor = false;
		return container.proxy;
	}
	
	public Collider requestObjectCollider(ColliderShape2D<?> shape, boolean isStatic) {
		ColliderContainer container = getCollider(shape, isStatic);
		addBuffer.add(container);
		objectColliders.add(container.proxy.assignID);
		container.collidingManifolds = new HashMap<>(2);
		container.collidingObjects = new HashSet<>(2);
		container.colliderStatuses = new HashMap<>(2);
		container.listeners = new HashSet<>(2);
		return container.proxy;
	}
	
	protected ColliderContainer getCollider(ColliderShape2D<?> shape, boolean isStatic) {
		
		ColliderContainer container = null;
		if (reservedColliders.isEmpty()) {
			container = new ColliderContainer();
			container.transform = new ColliderTransform2D();
			container.linearVelocity = new ColliderPoint2D();
		}
		else {
			container = reservedColliders.pop();
		}
		container.isStatic = isStatic;
		container.shape = shape;
		container.isEnabled = true;
		
		Collider proxy = new Collider();
		ColliderUID id = new ColliderUID();
		proxy.assignID = id;
		container.proxy = proxy;
		
		//container.shapes = new HashSet<ColliderShape2D<?>>(2);
		//container.collidingManifolds = new HashMap<>(2);
		
		//container.collidingObjects = new HashSet<>(2);
		//container.colliderStatuses = new HashMap<>(2);
		//container.listeners = new ArrayList<>(2);
		
		//proxy.addShapes(shapes);
		
		return container;
	}
	
	
	
	
	protected void colliderModified(Collider collider) {
		modified.add(collider);
	}
	
	// how to deal with colliders depending on exit events of removed collider
	protected void deferredRemove(ColliderUID id) { 
		removeBuffer.remove(id);
	}
	
	
	protected void batchRemoval() {
		for (ColliderUID id: deleteBuffer) {
			ColliderContainer container = registeredColliders.remove(id);
			Collider collider = container.proxy;
			
			broadphase.remove(collider);
			modified.remove(collider);
			tileColliders.remove(id);
			objectColliders.remove(id);
			
			/*for (ColliderShape2D<?> shape: container.shapes) {
				shape.collider = null;
				shape.isEnabled = false;
			}
			container.shapes.clear();
			container.shapes = null;*/
			//container.shape.isEnabled = false;
			container.shape.collider = null;
			container.shape = null;
			
			if (container.isTile == false) {
				container.collidingManifolds.clear();
				container.collidingObjects.clear();
				container.colliderStatuses.clear();
				container.listeners.clear();
			}
			container.collidingManifolds = null;
			container.collidingObjects = null;	
			container.colliderStatuses = null;
			container.listeners = null;
			
			container.linearVelocity.set(0,0);
			container.isStatic = false;
			container.transform.reset();
			container.data = null;
			container.proxy.assignID = null;
			container.proxy = null;
			container.isEnabled = false;
			container.collisionLayer = 1L;
			container.collisionMask = 1L;
			
			reservedColliders.add(container);
		}
		deleteBuffer.clear();
		
		for (ColliderUID id: removeBuffer) {
			ColliderContainer container = registeredColliders.get(id);
			Collider collider = container.proxy;
			
			broadphase.remove(collider);
			modified.remove(collider);
			tileColliders.remove(collider.assignID);
			objectColliders.remove(collider.assignID);
			
			container.isEnabled = false;
			
			deleteBuffer.add(id);
		}
		removeBuffer.clear();
	}
	
	
	protected void batchAddColliders() {
		for (ColliderContainer container: addBuffer) {
			Collider col = container.proxy;
			if (col != null && col.assignID != null && col.deactivated == false) {
				container.isEnabled = true;
				registeredColliders.put(container.proxy.assignID, container);
				modified.add(container.proxy);
			}
		}
		addBuffer.clear();
	}
	
	
	protected void updateBroadphase() {
		for (Collider collider: modified) {
			broadphase.update(collider);
		}
	}
	
	
	protected void updateCollision(Collider collider) {
		ColliderContainer ownCont = this.registeredColliders.get(collider.assignID);
		if (ownCont == null || ownCont.isTile == true) return;
		
		HashMap<ColliderUID, CollisionEvent> colStatuses = ownCont.colliderStatuses;
		HashSet<ColliderUID> colObjs = ownCont.collidingObjects;
		HashMap<ColliderUID, ManifoldData> colData = ownCont.collidingManifolds;
		colData.clear(); ////////
		
		//insert collider exit event removal
		Iterator<ColliderUID> oldColIDs = colStatuses.keySet().iterator();
		while (oldColIDs.hasNext()) {
			ColliderUID cuid = oldColIDs.next();
			if (colStatuses.get(cuid) == CollisionEvent.EXIT) {
				oldColIDs.remove();
			}
		}
		
		
		
		List<ColliderUID> newColIDs = new ArrayList<>(2); ////// stores successfully colliding object's ID
		
		if (ownCont.isEnabled == true && ownCont.collisionMask != 0) {
			
			BoundingBox box = new BoundingBox(0, 0, 1, 1); //////
			ownCont.shape.getBoundingBox(ownCont.transform, box);
			
			
			Set<Collider> collidingObjects = new HashSet<>(); //////
			broadphase.query(box.minX(), box.minY(), box.width(), box.height(), collidingObjects);
			
			for (Collider colliding: collidingObjects) {
				ColliderUID collidingID = colliding.getID();
				ColliderContainer colCont = this.registeredColliders.get(collidingID);
				
				if (colliding == collider || colCont == ownCont 
						|| colCont.isEnabled == false || colCont.collisionLayer == 0) continue;
				
				if ( (colCont.collisionLayer & ownCont.collisionMask) != 0) {
					ManifoldData data = new ManifoldData(); //////
					boolean isColliding = narrowphase.collide(ownCont.shape, colCont.shape,
							ownCont.transform, colCont.transform, data);
					colData.put(collidingID, data);
					if (isColliding) {
						newColIDs.add(collidingID);
						resolvableManifolds.putIfAbsent(new ColliderPairKey(collider, colliding), data.copy());
					}
				}
			}
		}
		
		
		
		for (ColliderUID colID: newColIDs) {
			if (colObjs.contains(colID) == false) {
				colObjs.add(colID);
				colStatuses.put(colID, CollisionEvent.ENTER);
			} else {
				colStatuses.put(colID, CollisionEvent.STAY);
			}
		}
		
		while (oldColIDs.hasNext()) {
			ColliderUID colIdA = oldColIDs.next();
			ColliderContainer colContA = this.registeredColliders.get(colIdA);
			// if colliderA doesn't exist anymore
			// or colliderA (colliding object from previous frame) does not exist in the current list
			// or colliderA is disabled this current frame 
			// or colliderA's collision layer does not intersect with currently checked collider
			if (colContA == null) {
				oldColIDs.remove();
				colObjs.remove(colIdA);
			}
			else if (newColIDs.contains(colIdA) == false || (colContA.isEnabled == false)
					|| (colContA.collisionLayer & ownCont.collisionMask) == 0) {
				colStatuses.put(colIdA, CollisionEvent.EXIT);
				colObjs.remove(colIdA);
			}
		}
	}
	
	protected void resolveCollision(ManifoldData data) {
		if (data == null) return;
		
		Collider affected = data.getAffectedCollider();
		Collider colliding = data.getCollidingCollider();

		if (affected == null || colliding == null) return;
		
		
		ColliderContainer affCont = this.registeredColliders.get(affected.assignID);
		ColliderContainer colCont = this.registeredColliders.get(colliding.assignID);
		
		if (affCont == null || colCont == null) return;
		
		
		boolean isAffectedStatic = affCont.isStatic;
		boolean isCollidingStatic = colCont.isStatic;
		
		// collision resolution wont happen if either colliders are sensors or both are static colliders
		if (isAffectedStatic && isCollidingStatic || affCont.isSensor || colCont.isSensor) return;
		
		float depth = (isAffectedStatic & isCollidingStatic) == true
				? data.penetrationDepth : data.penetrationDepth * 0.5f;
		float normalX = data.getDepthAxisX();
		float normalY = data.getDepthAxisY();
		
		
		if (isAffectedStatic == false) {
			affCont.transform.translate(normalX * depth, normalY * depth);
			//newVel = oldVel - normal * dot(normal, oldVel)
			float lx = affCont.linearVelocity.x;
			float ly = affCont.linearVelocity.y;
			float dot = lx * normalX + ly * normalY;
			
			float newVelX = lx - normalX * dot;
			float newVelY = ly - normalY * dot;
			
			affCont.linearVelocity.set(newVelX, newVelY);
			affCont.transform.translate(newVelX, newVelY);
		}
		
		if (isCollidingStatic == false) {
			colCont.transform.translate(normalX * depth * -1, normalY * depth * -1);
			
			float lx = colCont.linearVelocity.x;
			float ly = colCont.linearVelocity.y;
			float dot = lx * normalX + ly * normalY;
			
			float newVelX = lx - normalX * dot;
			float newVelY = ly - normalY * dot;
			
			colCont.linearVelocity.set(newVelX, newVelY);
			colCont.transform.translate(newVelX, newVelY);
		}
	}
	
	protected void emitResolvedCollision(ManifoldData data) { //////
		if (data == null) return;
		
		Collider affected = data.getAffectedCollider();
		Collider colliding = data.getCollidingCollider();

		if (affected == null || colliding == null) return;
		
		ColliderContainer affCont = this.registeredColliders.get(affected.assignID);
		ColliderContainer colCont = this.registeredColliders.get(colliding.assignID);
		
		if (affCont == null || colCont == null) return;
		
		// collision resolution wont happen if either colliders are sensors or both are static colliders
		if (affCont.isStatic && colCont.isStatic || affCont.isSensor || colCont.isSensor) return;
		
		HashSet<CollisionEventListener> affListeners = affCont.listeners;
		HashSet<CollisionEventListener> colListeners = colCont.listeners;
		
		
		for (CollisionEventListener listen: affListeners) {
			listen.onCollisionResolved(affected, colliding, data);
		}
		
		for (CollisionEventListener listen: colListeners) {
			listen.onCollisionResolved(affected, affected, data);
		}
	}
	
	protected void emitEvents(Collider collider) {
		if (collider == null) return;
		
		ColliderContainer ownCont = this.registeredColliders.get(collider.assignID);
		if (ownCont == null || ownCont.isTile == true) return;
		
		HashMap<ColliderUID, CollisionEvent> colStatuses = ownCont.colliderStatuses;
		HashMap<ColliderUID, ManifoldData> colData = ownCont.collidingManifolds;
		HashSet<CollisionEventListener> listeners = ownCont.listeners;
		
		List<Collider> entering = new ArrayList<>();
		List<Collider> staying = new ArrayList<>();
		List<Collider> exiting = new ArrayList<>();
		
		for (ColliderUID colID: colStatuses.keySet()) {
			Collider col = this.registeredColliders.get(colID).proxy;
			if (col == null) continue;
			CollisionEvent event = colStatuses.get(colID);
			switch (event) {
				case ENTER:
					entering.add(col);
					break;
				case STAY:
					staying.add(col);
					break;
				case EXIT:
					exiting.add(col);
					break;
				default:
					break;
			}
		}
		
		for (CollisionEventListener listen: listeners) {
			for (Collider enterCol: entering) {
				listen.onCollisionEnter(collider, enterCol, colData.get(enterCol.assignID));
			}
			for (Collider stayCol: staying) {
				listen.onCollisionStay(collider, stayCol, colData.get(stayCol.assignID));
			}
			for (Collider exitCol: exiting) {
				listen.onCollisionExit(collider, exitCol);
			}
		}
	}
	
	
	
	public void update() {
		batchRemoval();
		batchAddColliders();
		updateBroadphase();
		
		for (ColliderUID id: objectColliders) {
			Collider collider = this.registeredColliders.get(id).proxy;
			updateCollision(collider);
		}
		for (ManifoldData data: resolvableManifolds.values()) {
			resolveCollision(data);
		}
		for (ManifoldData data: resolvableManifolds.values()) {
			emitResolvedCollision(data);
		}
		for (ColliderUID id: objectColliders) {
			Collider collider = this.registeredColliders.get(id).proxy;
			emitEvents(collider);
		}
		resolvableManifolds.clear();
		
	}
	
	
	
	
	/*
	
	
	public Collider rayHitQuery(float fromX, float fromY, float toX, float toY, long collisionMask) {
		
		int x1 = broadphase.getCellX(fromX);
		int y1 = broadphase.getCellY(fromY);
		int x2 = broadphase.getCellX(toX);
		int y2 = broadphase.getCellY(toY);
		
		int xStep, yStep;
		int error;
		int errorPrev;
		int y = y1, x = x1;  // the line points
		int ddy, ddx;        // compulsory variables: the double values of dy and dx
		int dx = x2 - x1;
		int dy = y2 - y1;
		
		
		POINT (y1, x1);
		
		if (dy < 0){
			yStep = -1;
			dy = -dy;
		} else {
			yStep = 1;
		}
		
		if (dx < 0){
			xStep = -1;
			dx = -dx;
		} else {
			xStep = 1;
		}
		
		ddy = 2 * dy;  // work with double values for full precision
		ddx = 2 * dx; 
	
		
		
		
		
		
		
		if (ddx >= ddy) {  // first octant (0 <= slope <= 1)
		    // compulsory initialization (even for errorprev, needed when dx==dy)
			errorPrev = error = dx;  // start in the middle of the square
		    for (int i=0 ; i < dx ; i++) {  // do not use the first point (already done)
		    	x += xStep;
		    	error += ddy;
		    	if (error > ddx) {  // increment y if AFTER the middle ( > )
		    		y += yStep;
		    		error -= ddx;
		    	}
		        // three cases (octant == right->right-top for directions below):
		    	if (error + errorPrev < ddx)  // bottom square also
		    		POINT (y - yStep, x);
		    	else if (error + errorPrev > ddx)  // left square also
		    		POINT (y, x - xStep);
		    	else{  // corner: bottom and left squares also
		    		POINT (y - yStep, x);
		    		POINT (y, x - xStep);
		    	}
		    }
		    POINT (y, x);
		    errorPrev = error;
		}
		
		
		else {  // the same as above
			errorPrev = error = dy;
			for (int i=0 ; i < dy ; i++) {
				y += yStep;
				error += ddx;
				if (error > ddy) {
					x += xStep;
					error -= ddy;
				}
				
				if (error + errorPrev < ddy)
					POINT (y, x - xStep);
				else if (error + errorPrev > ddy)
					POINT (y - yStep, x);
				else {
					POINT (y, x - xStep);
					POINT (y - yStep, x);
				}
			}
			POINT (y, x);
			errorPrev = error;
		}
		// assert ((y == y2) && (x == x2));  // the last point (y2,x2) has to be the same with the last point of the algorithm		
		
		return null;
	}
	
	protected Collider getColliderFromRayCast(float fromX, float fromY, float toX, float toY, long collisionMask, int cellX, int cellY) {
		
		return null;
	}
	
	*/
	
	
	
	
	
	
	
	
	
	protected class ColliderPairKey {
		protected final Collider colA;
		protected final Collider colB;
		
		protected ColliderPairKey(Collider colA, Collider colB) {
			Objects.requireNonNull(colA, "Collider A must not be null.");
			Objects.requireNonNull(colB, "Collider B must not be null.");
			
			this.colA = colA;
			this.colB = colB;
		}
		
		public boolean equals(Object other) {
			if (this == other) return true;
			if (other == null) return false;
			ColliderPairKey oKey = (ColliderPairKey) other;
			boolean isKeyPair = oKey.colA == this.colA && oKey.colB == this.colB
					|| oKey.colA == this.colB || oKey.colB == this.colA;
			return isKeyPair;
		}
		
		public int hashCode() {
			return System.identityHashCode(colA) + System.identityHashCode(colB);
		}
		
		public String toString() {
			return 	this.getClass().getName() +"@" + Integer.toHexString(System.identityHashCode(this)) 
					+ ": ("+colA.toString() + ", " + colB.toString() + ")";
		}
	}
	
	
	
	
	
	protected class ColliderContainer {
		
		protected Collider proxy;
		// wont participate in collisions at all
		// all it previous collisions will be marked as exit
		// but will still be updated in broadphase if any changes occur
		protected boolean isEnabled = true;
		protected boolean isStatic = false; // won't be moved during collision response
		protected boolean isSensor = false; // won't influence collision response, but participates in collision detection
		// won't generate collision events, on itself nor accept listeners
		// it can't be a sensor, and is always static
		protected boolean isTile = false;	
		
		protected ColliderTransform2D transform;
		protected ColliderPoint2D linearVelocity;
		//protected HashSet<ColliderShape2D<?>> shapes;
		protected ColliderShape2D<?> shape;
		protected long collisionLayer = 1L;
		protected long collisionMask = 1L;
		
		// stores all colliders entering, staying, and exiting this collider
		protected HashMap<ColliderUID, CollisionEvent> colliderStatuses;
		// only stores all colliders entering and staying
		protected HashSet<ColliderUID> collidingObjects;
		protected HashMap<ColliderUID, ManifoldData> collidingManifolds;
		
		
		protected HashSet<CollisionEventListener> listeners;
		
		protected Object data;
		
		
		protected ColliderContainer() {}
		
	}
	
	
	public class Collider {
		protected ColliderUID assignID;
		protected boolean deactivated = false;
		
		protected Collider() {};
		
		
		public Collider setEnable(boolean enable) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.isEnabled = enable;
			
			return this;
		}
		
		public boolean isEnabled() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.isEnabled;
		}
		
		
		
		
		
		
		
		public Collider setSensor(boolean makeSensor) {
			if (this.isDeactivated()) return this;
			if (this.isTileCollider()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.isSensor = makeSensor;

			notifyIfModified();
			return this;
		}
		
		public boolean isSensor() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.isSensor;
		}
		
		
		
		
		
		
		
		public boolean isStatic() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.isStatic;
		}
		
		public boolean isTileCollider() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.isTile;
		}
		
		
		
		
		
		
		
		
		public Collider setPosition(float posX, float posY) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.transform.setPosition(posX, posY);

			notifyIfModified();
			return this;
		}
		
		
		
		public float getPosX() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.transform.getX();
		}
		
		public float getPosY() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.transform.getY();
		}
		
		
		
		public Collider translate(float x, float y) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.transform.translate(x, y);

			notifyIfModified();
			return this;
		}
		
		
		
		
		
		
		
		
		
		public Collider setRotation(float radians) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.transform.setRotation(radians);

			notifyIfModified();
			return this;
		}
		
		public Collider setRotation(float cos, float sin) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.transform.setRotation(cos, sin);

			notifyIfModified();
			return this;
		}
		
		
		
		public Collider rotate(double radians) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.transform.rotate(radians);

			notifyIfModified();
			return this;
		}
		
		public Collider rotate(float cos, float sin) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.transform.rotate(cos, sin);

			notifyIfModified();
			return this;
		}
		
		public Collider rotateAroundPoint(double radians, float pointX, float pointY) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.transform.rotateAroundPoint(radians, pointX, pointY);

			notifyIfModified();
			return this;
		}
		
		public Collider rotateAroundPoint(float cos, float sin, float pointX, float pointY) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.transform.rotateAroundPoint(cos, sin, pointX, pointY);

			notifyIfModified();
			return this;
		}
		
		
		
		public float getRotationRadians() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.transform.getRotation(); 
		}
		
		public float getRotationDegrees() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.transform.getRotationDegrees(); 
		}
		
		public float getRotationX() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.transform.getRotationCos(); 
		}
		
		public float getRotationY() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.transform.getRotationSin(); 
		}
		
		
		
		
		
		
		
		
		
		public Collider setLinearVelocity(float velX, float velY) {
			if (this.isDeactivated()) return this;
			if (this.isStatic() == true) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.linearVelocity.set(velX, velY);

			notifyIfModified();
			return this;
		}
		
		
		public Collider applyLineVelocity(float lineX, float lineY) {
			if (this.isDeactivated()) return this;
			if (this.isStatic() == true) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.linearVelocity.add(lineX, lineY);

			notifyIfModified();
			return this;
		}
		
		
		
		public float getLineVelX() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.linearVelocity.x;
		}
		
		public float getLineVelY() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.linearVelocity.y;
		}
		
		
		
		
		
		
		/*
		public Collider addShape(ColliderShape2D<?> shape) {
			shape.collider = this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.shapes.add(shape);
			return this;
		}
		
		public Collider addShapes(List<ColliderShape2D<?>> addShapesList) {
			Set<ColliderShape2D<?>> set = CollisionSystem.this
					.registeredColliders.get(assignID).shapes;
			for (ColliderShape2D<?> shape: addShapesList) {
				shape.collider = this;
				set.add(shape);
			}
			return this;
		}
		
		public Collider removeShape(ColliderShape2D<?> shape) {
			shape.collider = null;
			shape.isEnabled = false;
			CollisionSystem.this.registeredColliders.get(assignID)
					.shapes.remove(shape);
			return this;
		}
		
		public Collider removeShapes(List<ColliderShape2D<?>> removeShapesList) {
			Set<ColliderShape2D<?>> set = CollisionSystem.this
					.registeredColliders.get(assignID).shapes;
			for (ColliderShape2D<?> shape: removeShapesList) {
				shape.collider = null;
				shape.isEnabled = false;
				set.remove(shape);
			}
			return this;
		}
		
		public List<ColliderShape2D<?>> getShapes(List<ColliderShape2D<?>> out) {
			out.addAll( CollisionSystem.this.registeredColliders.get(assignID).shapes );
			return out;
		}
		
		public boolean hasShape(ColliderShape2D<?> shape) {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.shapes.contains(shape);
		}
		
		*/
		
		
		public Collider addShape(ColliderShape2D<?> shape) {
			if (this.isDeactivated()) return this;
			shape.collider = this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.shape = shape;

			notifyIfModified();
			return this;
		}
		
		public ColliderShape2D<?> removeShape() {
			if (this.isDeactivated()) return null;
			ColliderShape2D<?> shape = CollisionSystem.this
					.registeredColliders.get(assignID).shape;
			shape.collider = null;
			
			notifyIfModified();
			return shape;
		}
		
		public ColliderShape2D<?> replaceShape(ColliderShape2D<?> newShape) {
			if (this.isDeactivated()) return null;
			ColliderShape2D<?> shape = CollisionSystem.this
					.registeredColliders.get(assignID).shape;
			shape.collider = null;
			
			CollisionSystem.this.registeredColliders.get(assignID).shape = newShape;
			newShape.collider = this;
			
			notifyIfModified();
			return shape;
		} 
		
		public ColliderShape2D<?> getShape() {
			return CollisionSystem.this.registeredColliders.get(assignID).shape;
		}
		
		public BoundingBox getBoundingBox(BoundingBox box) {
			ColliderContainer container = CollisionSystem.this.registeredColliders.get(assignID);
			if (container.shape == null) return box;
			container.shape.getBoundingBox(container.transform, box);
			return box;
		}
		
		
		
		
		
		
		
		
		public Collider setData(Object data) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.data = data;
			return this;
		}
		
		public Object getData() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.data;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		public Collider setCollisionLayer(long layer) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.collisionLayer = layer;

			notifyIfModified();
			return this;
		}
		
		
		public Collider setBitLayer(byte index) {
			if (this.isDeactivated()) return this;
			if (index > 63 || index < 0) {
				System.out.println("Index must be within 0 to 63, inclusive. Index = "+index);
				return this;
			}
			CollisionSystem.this.registeredColliders.get(assignID)
					.collisionLayer |= 1L << (index & 0x3F);

			notifyIfModified();
			return this;
		}
		
		
		public Collider clearBitLayer(byte index) {
			if (this.isDeactivated()) return this;
			if (index > 63 || index < 0) {
				System.out.println("Index must be within 0 to 63, inclusive. Index = "+index);
				return this;
			}
			CollisionSystem.this.registeredColliders.get(assignID)
					.collisionLayer &= ~( 1L << (index & 0x3F) );

			notifyIfModified();
			return this;
		}
		
		public long getCollisionLayer() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.collisionLayer;
		}
		
		public boolean getBitFromLayer(byte index) {
			long layer = CollisionSystem.this.registeredColliders.get(assignID)
					.collisionLayer;
			return (layer & (1L << ( index & 0x3F)) ) != 0;
		}
		
		
		
		
		
		
		public Collider setCollisionMask(long mask) {
			if (this.isDeactivated()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.collisionMask = mask;
			
			notifyIfModified();
			return this;
		}
		
		
		public Collider setBitMask(byte index) {
			if (this.isDeactivated()) return this;
			if (index > 63 || index < 0) {
				System.out.println("Index must be within 0 to 63, inclusive. Index = "+index);
				return this;
			}
			CollisionSystem.this.registeredColliders.get(assignID)
					.collisionMask |= 1L << (index & 0x3F);
			
			notifyIfModified();
			return this;
		}
		
		
		public Collider clearBitMask(byte index) {
			if (this.isDeactivated()) return this;
			if (index > 63 || index < 0) {
				System.out.println("Index must be within 0 to 63, inclusive. Index = "+index);
				return this;
			}
			CollisionSystem.this.registeredColliders.get(assignID)
					.collisionMask &= ~( 1L << (index & 0x3F) );
			
			notifyIfModified();
			return this;
		}
		
		public long getCollisionMask() {
			return CollisionSystem.this.registeredColliders.get(assignID)
					.collisionLayer;
		}
		
		public boolean getBitFromMask(byte index) {
			long mask = CollisionSystem.this.registeredColliders.get(assignID)
					.collisionMask;
			return (mask & (1L << ( index & 0x3F)) ) != 0;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		public List<Collider> getCollidingObjects(List<Collider> out) {
			
			HashMap<ColliderUID, ColliderContainer> reg = CollisionSystem.this.registeredColliders;
			HashSet<ColliderUID> storedColliders = reg.get(assignID).collidingObjects;
			
			for (ColliderUID id: storedColliders) {
				out.add(reg.get(id).proxy);
			}
			
			return out;
		}
		
		
		
		
		
		
		
		
		
		public Collider addListener(CollisionEventListener listener) {
			if (this.isDeactivated()) return this;
			if (this.isTileCollider()) return this;
			CollisionSystem.this.registeredColliders.get(assignID)
					.listeners.add(listener);
			return this;
		}
		
		public boolean removeListener(CollisionEventListener listener) {
			if (this.isDeactivated()) return false;
			if (this.isTileCollider()) return false;
			return CollisionSystem.this.registeredColliders
					.get(assignID).listeners.remove(listener);
		}
		
		public boolean hasListener(CollisionEventListener listener) {
			if (this.isTileCollider()) return false;
			return CollisionSystem.this.registeredColliders
					.get(assignID).listeners.contains(listener);
		}
		
		public List<CollisionEventListener> getListeners(List<CollisionEventListener> out) {
			if (this.isTileCollider()) return out;
			HashSet<CollisionEventListener> storedListeners = CollisionSystem.this
					.registeredColliders.get(assignID).listeners;
			
			for (CollisionEventListener listener: storedListeners) {
				out.add(listener);
			}
			
			return out;
		}
		
		
		
		
		
		
		
		
		
		
		
		protected void notifyIfModified() {
			if (this.isDeactivated()) return;
			CollisionSystem.this.colliderModified(this);
		}
		
		
		
		
		
		
		
		public ColliderUID getID() {
			return this.assignID;
		}
		
		
		
		
		
		
		public void deactivate() {
			if (deactivated == false) {
				CollisionSystem.this.broadphase.remove(this); ///
				deferredRemove(assignID);
				this.deactivated = true;
			}
		}
		
		public boolean isDeactivated() {
			return this.assignID == null || this.deactivated == true;
		}
		
	}
	
}
