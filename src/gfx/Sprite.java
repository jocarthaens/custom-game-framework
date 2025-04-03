package gfx;

import java.awt.image.BufferedImage;

public interface Sprite {

	abstract BufferedImage getImage(int index);
	abstract int spriteWidth(int index);
	abstract int spriteHeight(int index);
	abstract int count();
}
