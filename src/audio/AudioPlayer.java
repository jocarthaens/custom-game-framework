package audio;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	private List<AudioClip> audioClips;
	private AudioSettings audioSettings;
	
	public AudioPlayer(AudioSettings audioSettings) {
		audioClips = new ArrayList<>();
		this.audioSettings = audioSettings;
	}
	
    public void playMusic(String fileName) {
        final Clip clip = getClip(fileName);
        if (clip == null) {
        	return;
		}
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        final MusicClip music = new MusicClip(clip);
        music.setVolume(audioSettings);
        audioClips.add(music);
    }
    
    public void playSound(String fileName) {
        final Clip clip = getClip(fileName);
        if (clip == null) {
			return;
		}
        final SoundClip sound = new SoundClip(clip);
        sound.setVolume(audioSettings);
        audioClips.add(sound);
    }
	
    private Clip getClip(String fileName) {
        final URL soundFile = AudioPlayer.class.getResource("/sounds/" + fileName);
        try( AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile) ) {
            final Clip clip = AudioSystem.getClip();
            //System.out.println(clip);
            clip.open(audioInputStream);
            //System.out.println("test");
            clip.setMicrosecondPosition(0);
            return clip;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return null;
    }
    
    public void update() {
    	
    	for (AudioClip clip: audioClips) {
    		if (clip.isFinished()) {
    			clip.close();
    		}
    	}
    	
    	for (AudioClip clip: audioClips) {
    		clip.update(audioSettings);
    	}
    	
    	for (int i = 0; i < audioClips.size(); i++) {
    		if (audioClips.get(i).isFinished()) {
    			audioClips.remove(i);
    		}
    	}
    	
    	//System.out.println(audioClips.size());
    }
    
    public void clear() {
    	if (audioClips.isEmpty()) {
    		return;
    	}
    	for (AudioClip clip: audioClips) {
    		clip.close();
    	}
    	audioClips.clear();
    }
    
}
