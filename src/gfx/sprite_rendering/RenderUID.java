package gfx.sprite_rendering;

import java.util.UUID;

// Unique Renderable ID

public class RenderUID {
	public final UUID id;
	
	RenderUID() {
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
