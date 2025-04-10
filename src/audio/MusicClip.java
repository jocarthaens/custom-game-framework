package audio;

import javax.sound.sampled.Clip;

// Audio clip that will be played continuously, until stopped.

public class MusicClip extends AudioClip {
	
	public MusicClip(Clip clip) {
		super(clip);
	}

	@Override
	protected float getVolume(AudioSettings audioSettings) {
		return audioSettings.getMusicVolume();
	}
}
