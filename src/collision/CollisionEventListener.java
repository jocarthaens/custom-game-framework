package collision;

import collision.CollisionSystem.Collider;

// Interface for objects that wants to listen for collision events

public interface CollisionEventListener {
	
	public static enum CollisionEvent {
		ENTER, STAY, EXIT
	}
	
	public void onCollisionEnter(Collider source, Collider colliding, ManifoldData data);
	public void onCollisionStay(Collider source, Collider colliding, ManifoldData data);
	public void onCollisionExit(Collider source, Collider colliding);
	public void onCollisionResolved(Collider colliderA, Collider colliderB, ManifoldData data);
}
