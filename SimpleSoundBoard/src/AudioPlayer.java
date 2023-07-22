

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineEvent.Type;

/**
 * A utility class for playing back audio files using Java Sound API.
 * @author www.codejava.net
 */
public class AudioPlayer implements LineListener {
	private static final int SECONDS_IN_HOUR = 3600;
	private static final int SECONDS_IN_MINUTE = 60;
	private boolean playCompleted;
	private boolean isStopped;
	private boolean isPaused;
	private Clip audioClip;

	public AudioPlayer() {
	}

	public void load(String audioFilePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		File audioFile = new File(audioFilePath);
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		AudioFormat format = audioStream.getFormat();
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		this.audioClip = (Clip)AudioSystem.getLine(info);
		this.audioClip.addLineListener(this);
		this.audioClip.open(audioStream);
	}

	public long getClipSecondLength() {
		return this.audioClip.getMicrosecondLength() / 1000000L;
	}

	public String getClipLengthString() {
		String length = "";
		long hour = 0L;
		long minute = 0L;
		long seconds = this.audioClip.getMicrosecondLength() / 1000000L;
		if (seconds >= 3600L) {
			hour = seconds / 3600L;
			length = String.format("%02d:", hour);
		} else {
			length = length + "00:";
		}

		minute = seconds - hour * 3600L;
		if (minute >= 60L) {
			minute /= 60L;
			length = length + String.format("%02d:", minute);
		} else {
			minute = 0L;
			length = length + "00:";
		}

		long second = seconds - hour * 3600L - minute * 60L;
		length = length + String.format("%02d", second);
		return length;
	}

	void play() throws IOException {
		this.audioClip.start();
		this.playCompleted = false;
		this.isStopped = false;

		while(!this.playCompleted) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException var2) {
				if (this.isStopped) {
					this.audioClip.stop();
					break;
				}

				if (this.isPaused) {
					this.audioClip.stop();
				} else {
					this.audioClip.start();
				}
			}
		}

		this.audioClip.close();
	}

	public void stop() {
		this.isStopped = true;
	}

	public void pause() {
		this.isPaused = true;
	}

	public void resume() {
		this.isPaused = false;
	}

	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();
		if (type == Type.STOP && (this.isStopped || !this.isPaused)) {
			this.playCompleted = true;
		}

	}

	public Clip getAudioClip() {
		return this.audioClip;
	}
}