package audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

// Base class for audio clips that can be paused, played, and stopped.

public abstract class AudioClip {
	private final Clip clip;
	private long clipTime;
	
	public AudioClip(Clip clip) {
		if (clip == null) {
			throw new IllegalArgumentException("Null objects are not accepted.");
		}
		this.clip = clip;
		this.clip.start();
		clipTime = 0;
	}

    public boolean isFinished() {
        return !clip.isRunning() && clipTime == 0;
    }

    public void close() {
        clip.close();
    }
	
	public void pause() {
		clipTime = clip.getMicrosecondPosition() % clip.getMicrosecondLength();
		clip.stop();
	}
	
	public void play() {
		clip.setMicrosecondPosition(clipTime);
		clip.start();
	}
	
	public void stop() {
		clipTime = 0;
		clip.stop();
	}
	
	
	public void update(AudioSettings audioSettings) {
		
	}
	
	protected void setVolume(AudioSettings audioSettings) {
		FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float range = control.getMaximum() - control.getMinimum();
		float gain = range * this.getVolume(audioSettings) + control.getMinimum();
		
		control.setValue(gain);
	}
	
	protected abstract float getVolume(AudioSettings audioSettings);
}
