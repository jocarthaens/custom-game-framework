package gfx;

import java.awt.image.BufferedImage;
import java.util.Objects;


public class SpriteSheet implements Sprite {
	protected BufferedImage[] sprites;
	
	
	public SpriteSheet(BufferedImage[] images) {
		Objects.requireNonNull(images);
		for (int i = 0; i < images.length; i++) {
			Objects.requireNonNull(images[i]);
		}
		this.sprites = images;
	}
	
	
	
	@Override
	public int spriteWidth(int index) {
		return index < sprites.length ? sprites[index].getWidth() : -1;
	}
	
	@Override
	public int spriteHeight(int index) {
		return index < sprites.length ? sprites[index].getHeight() : -1;
	}
	
	@Override
	public int count() {
		return sprites.length;
	}
	
	
	
	@Override
	public BufferedImage getImage(int index) {
		return index < sprites.length ? sprites[index] : null;
	}
	
}
