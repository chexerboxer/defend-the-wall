import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.*;

public class Sfx {
		private Clip clip;
		
		public Sfx(String sound) throws LineUnavailableException, IOException, UnsupportedAudioFileException, InterruptedException {
			URL url = Main.class.getResource("/" + sound);
			
			if(url==null) {
				url = new URL("file:" + sound);
			}
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);  
		    clip = AudioSystem.getClip();
		    clip.open(audioIn);
		}

		//When called, a clip is played and reset for the next use
		public void play(){
		    clip.start();
		    clip.setFramePosition(0);
		}
		
	}