package audio;

import javax.sound.sampled.Clip;

// Audio clip that only plays once; Used for sound effects.

public class SoundClip extends AudioClip {
	
	public SoundClip(Clip clip) {
		super(clip);
	}
	
	@Override
	protected float getVolume(AudioSettings audioSettings) {
		return audioSettings.getSoundVolume();
	}
}
