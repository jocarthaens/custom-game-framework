package audio;

public class AudioSettings {
	protected float musicVolume ;
	protected float soundVolume;
	
	public AudioSettings() {
		musicVolume = 0.8f;
	    soundVolume = 0.75f;
	}

	public float getMusicVolume() {
    	return musicVolume;
	}

	public void setMusicVolume(float musicVolume) {
    	this.musicVolume = musicVolume;
	}

	public float getSoundVolume() {
    	return soundVolume;
    }

	public void setSoundVolume(float soundVolume) {
    	this.soundVolume = soundVolume;
	}
		
}
