package collision;

import collision.CollisionSystem.Collider;

public class ManifoldData {
	
	protected boolean isColliding;
	protected ColliderPoint2D depthAxis;  // collision normal between 2 colliders
	protected float penetrationDepth;
	protected Collider affectedCollider;
	protected Collider collidingCollider;
	//ColliderShape2D<?> affectedShape;
	//ColliderShape2D<?> collidingShape;
	//List<ColiderPoint2D> contactPoints;
	//int contactPoints;
	
	public ManifoldData(boolean isColliding, float axisX, float axisY, float depth,
			Collider affected, Collider colliding) {
			//ColliderShape2D<?> affected, ColliderShape2D<?> colliding) {
		this.isColliding = isColliding;
		this.depthAxis = new ColliderPoint2D(axisX, axisY);
		this.penetrationDepth = depth;
		this.affectedCollider = affected;
		this.collidingCollider = colliding;
		//this.affectedShape = affected;
		//this.collidingShape = colliding;
	}
	
	protected ManifoldData() {
		this.isColliding = false;
		this.depthAxis = new ColliderPoint2D(0,0);
		this.penetrationDepth = -1;
		this.affectedCollider = null;
		this.collidingCollider = null;
		//this.affectedShape = null;
		//this.collidingShape = null;
	}
	
	
	public boolean isColliding() {
		return isColliding;
	}

	
	public float getDepthAxisX() {
		return depthAxis.x;
	}
	
	public float getDepthAxisY() {
		return depthAxis.y;
	}

	
	public float getPenetrationDepth() {
		// TODO Auto-generated method stub
		return penetrationDepth;
	}
	
	
	
	public Collider getAffectedCollider() {
		return affectedCollider;
	}
	
	
	public Collider getCollidingCollider() {
		return collidingCollider;
	}
	
	
	public ManifoldData copy() {
		return new ManifoldData(isColliding, depthAxis.x, depthAxis.y,
				penetrationDepth, affectedCollider, collidingCollider);
	}
	
	
//	public ColliderShape2D<?> getAffectedShape() {
//		return affectedShape;
//	}
//
//	
//	public ColliderShape2D<?> getCollidingShape() {
//		return collidingShape;
//	}
//
//	
//	public Object getContactPoints() { // create list
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	
//	public int getContactPointCount() {
//		// TODO Auto-generated method stub
//		return contactPoints;
//	}
}
