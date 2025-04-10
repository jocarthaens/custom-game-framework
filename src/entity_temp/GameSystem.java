package entity_temp;

import game.Game;

// Interface for Systems that will modify Entities and their components.

public interface GameSystem {
	
	public abstract void update(Game game);
}
