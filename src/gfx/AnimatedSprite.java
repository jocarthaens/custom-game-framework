package gfx;

import java.awt.image.BufferedImage;
import java.util.Objects;


public class AnimatedSprite implements Sprite {
	protected BufferedImage[] sprites;
	protected float[] times;
	protected float totalDuration = 0;
	
	
	
	public AnimatedSprite(BufferedImage[] images, float totalDuration) {
		Objects.requireNonNull(images);
		if (totalDuration <= 0) {
			throw new IllegalArgumentException(
					"Total duration "+totalDuration+" is invalid. "
							+ "Values must be greater than 0.");
		}
		
		this.sprites = images;
		float[] array = new float[images.length];
		float time = totalDuration / sprites.length;
		for (int i = 0; i < sprites.length; i++) {
			Objects.requireNonNull(images[i]);
			array[i] = time;
		}
		this.times = array;
		this.totalDuration = totalDuration;
	}
	
	public AnimatedSprite(BufferedImage[] images, float[] frameTimes) {
		Objects.requireNonNull(images);
		
		if (images.length != times.length) {
			throw new IllegalArgumentException(
					"The length of images "
					+ "and frameTimes must be equal.");
		}
		
		for (int i = 0; i < images.length; i++) {
			Objects.requireNonNull(images[i]);
			if (frameTimes[i] <= 0) {
				throw new IllegalArgumentException(
						"The value "+frameTimes[i]+"of Index "+i+" is invalid."
								+ "Values must be greater than 0.");
			}
			this.totalDuration += frameTimes[i];
		}
		
		this.sprites = images;
		this.times = frameTimes;
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
	
	public BufferedImage getImage(float duration, boolean isLooping) {
		if (duration <= 0) {
			throw new IllegalArgumentException(
					"Duration "+duration+" is invalid. "
							+ "Values must be greater than 0.");
		}
		
		float tDuration = duration;
		float totalTime = 0;
		int index = 0;
		
		if (isLooping && duration > totalDuration) tDuration /= totalDuration;
		
		for (int i = 0; i < times.length; i++) {
			totalTime += times[i];
			int compare = Float.compare(totalTime, tDuration) ;
			
			if (compare > 0) { // if totalTime > tDuration (happens when in previous loop, totalTime < tDuration)
				index = i > 0 ? i - 1: 0;
				break;
			}
			if (compare == 0) { // if totalTime == tDuration
				index = i;
				break;
			}
			index = i; // if all conditions fail and function reaches end of loop
		}
		
		return sprites[index];
	}
	
	public float getFrameTime(int index) {
		return index < times.length ? times[index] : -1;
	}
	
	public float getDuration() {
		return totalDuration;
	}
	
}
