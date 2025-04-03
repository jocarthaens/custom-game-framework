package gfx.sprite_rendering;

import java.awt.Graphics2D;


public interface Renderable {
	
	abstract void render(Graphics2D g2);
	
	// useful for sprites, animations, particles, debugging (collisions, navigation, AI, etc)
	// purpose: to streamline rendering of renderable objects inside renderer with sorting order
	//potential issue:
	// violation of component principle of separation of logic and data (render() method)
	// must be used only for non-ui elements (ui elements doesn't need sorting or view boundaries)
	// what if systems want to render things along with their components? 
		// (Collision System rendering collision5000 points along their collision5000 components)
		// (Sprite System wanting to highlight viewBounds of their sprite components)
			//potential issues: makes components heavy when carrying unnecessary data just for rendering (to comply with sort order)
			//potential solution: pass objects which purpose is rendering system's data.
				//potential issue: too many objects or too heavy objects just for rendering data
		//potential solutions:
			//on using components for rendering, only add lightweight data to be used for rendering
			//use system to render all its necessary data, if sort order is not crucial. 
				//useful in debugging by overlaying on top of the screen so they can be seen more clearly
			//pass objects along with components for rendering, 
				//objects must either be not too many or has lightweight memory footprint as to not affect render performance
	// rendering priority
	// render sprites, animations and particles first, applying sorting order (non-debug)
	// then render the debug components in particular order -> collision5000, navigation, AI, others, custom)
		// others -> movement, 
		// custom -> custom components like stats, inventory, skills, states (Map<String, Object> data)
	
	// non-debug objects that are always rendered (sprites, animations, particles, ui)
	
}
