package collision;

import java.util.UUID;

// Unique Ccollider ID

public class ColliderUID {
	public final UUID id;
	
	ColliderUID() {
		id = UUID.randomUUID();
	}
	
	public boolean equals(Object other) {
		return id.equals(other);
	}
	public int hashCode() {
		return id.hashCode();
	}
	public String toString() {
		return "Collider Unique ID: "+id.toString();
	}
}
