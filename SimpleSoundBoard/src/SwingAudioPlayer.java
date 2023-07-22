import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * A Swing-based audio player program.
 * NOTE: Can play only WAVE (*.wav) file.
 * @author www.codejava.net
 *
 */
public class SwingAudioPlayer extends JPanel implements ActionListener {
	private AudioPlayer player = new AudioPlayer();
	private Thread playbackThread;
	private PlayingTimer timer;
	private boolean isPlaying = false;
	private boolean isPause = false;
	private String audioFilePath;
	private String lastOpenPath;
	private JLabel labelFileName = new JLabel("Playing File:");
	private JLabel labelTimeCounter = new JLabel("00:00:00");
	private JLabel labelDuration = new JLabel("00:00:00");
	private JButton buttonOpen = new JButton("Open");
	private JButton buttonPlay = new JButton("Play");
	private JButton buttonPause = new JButton("Pause");
	private JSlider sliderTime = new JSlider();
	private ImageIcon iconOpen;
	private ImageIcon iconPlay;
	private ImageIcon iconStop;
	private ImageIcon iconPause;

	public SwingAudioPlayer() {
		this.iconOpen = new ImageIcon("." + File.separator + "images" + File.separator + "Open.png");
		this.iconPlay = new ImageIcon("." + File.separator + "images" + File.separator + "Play.png");
		this.iconStop = new ImageIcon("." + File.separator + "images" + File.separator + "Stop.png");
		this.iconPause = new ImageIcon("." + File.separator + "images" + File.separator + "Pause.png");
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.anchor = 17;
		this.buttonPlay.setFont(new Font("Sans", 1, 14));
		this.buttonPlay.setIcon(this.iconPlay);
		this.buttonPlay.setEnabled(false);
		this.buttonPause.setFont(new Font("Sans", 1, 14));
		this.buttonPause.setIcon(this.iconPause);
		this.buttonPause.setEnabled(false);
		this.labelTimeCounter.setFont(new Font("Sans", 1, 12));
		this.labelDuration.setFont(new Font("Sans", 1, 12));
		this.sliderTime.setPreferredSize(new Dimension(400, 20));
		this.sliderTime.setEnabled(false);
		this.sliderTime.setValue(0);
		JPanel panelButtons = new JPanel(new GridLayout(1, 1, 10, 10));
		panelButtons.add(this.buttonPlay);
		panelButtons.add(this.buttonPause);
		this.add(panelButtons, constraints);
		this.buttonPlay.addActionListener(this);
		this.buttonPause.addActionListener(this);
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof JButton button) {
			if (button == this.buttonPlay) {
				if (!this.isPlaying) {
					this.playBack();
				} else {
					this.stopPlaying();
				}
			} else if (button == this.buttonPause) {
				if (!this.isPause) {
					this.pausePlaying();
				} else {
					this.resumePlaying();
				}
			}
		}

	}

	public void openFile(String url) {
		this.audioFilePath = url;
		if (this.isPlaying || this.isPause) {
			this.stopPlaying();

			while(this.player.getAudioClip().isRunning()) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException var3) {
					var3.printStackTrace();
				}
			}
		}

		this.playBack();
	}

	private void playBack() {
		this.timer = new PlayingTimer(this.labelTimeCounter, this.sliderTime);
		this.timer.start();
		this.isPlaying = true;
		this.playbackThread = new Thread(new Runnable() {
			public void run() {
				try {
					SwingAudioPlayer.this.buttonPlay.setText("Stop");
					SwingAudioPlayer.this.buttonPlay.setIcon(SwingAudioPlayer.this.iconStop);
					SwingAudioPlayer.this.buttonPlay.setEnabled(true);
					SwingAudioPlayer.this.buttonPause.setText("Pause");
					SwingAudioPlayer.this.buttonPause.setEnabled(true);
					SwingAudioPlayer.this.player.load(SwingAudioPlayer.this.audioFilePath);
					SwingAudioPlayer.this.timer.setAudioClip(SwingAudioPlayer.this.player.getAudioClip());
					SwingAudioPlayer.this.labelFileName.setText("Playing Sound");
					SwingAudioPlayer.this.sliderTime.setMaximum((int)SwingAudioPlayer.this.player.getClipSecondLength());
					SwingAudioPlayer.this.labelDuration.setText(SwingAudioPlayer.this.player.getClipLengthString());
					SwingAudioPlayer.this.player.play();
					SwingAudioPlayer.this.resetControls();
				} catch (UnsupportedAudioFileException var2) {
					JOptionPane.showMessageDialog(SwingAudioPlayer.this, "The audio format is unsupported!", "Error", 0);
					SwingAudioPlayer.this.resetControls();
					var2.printStackTrace();
				} catch (LineUnavailableException var3) {
					JOptionPane.showMessageDialog(SwingAudioPlayer.this, "Could not play the audio file because line is unavailable!", "Error", 0);
					SwingAudioPlayer.this.resetControls();
					var3.printStackTrace();
				} catch (IOException var4) {
					JOptionPane.showMessageDialog(SwingAudioPlayer.this, "I/O error while playing the audio file!", "Error", 0);
					SwingAudioPlayer.this.resetControls();
					var4.printStackTrace();
				}

			}
		});
		this.playbackThread.start();
	}

	public void stopPlaying() {
		this.isPause = false;
		this.buttonPause.setText("Pause");
		this.buttonPause.setEnabled(false);
		this.timer.reset();
		this.timer.interrupt();
		this.player.stop();
		this.playbackThread.interrupt();
	}

	private void pausePlaying() {
		this.buttonPause.setText("Resume");
		this.isPause = true;
		this.player.pause();
		this.timer.pauseTimer();
		this.playbackThread.interrupt();
	}

	private void resumePlaying() {
		this.buttonPause.setText("Pause");
		this.isPause = false;
		this.player.resume();
		this.timer.resumeTimer();
		this.playbackThread.interrupt();
	}

	private void resetControls() {
		this.timer.reset();
		this.timer.interrupt();
		this.buttonPlay.setText("Play");
		this.buttonPlay.setIcon(this.iconPlay);
		this.buttonPause.setEnabled(false);
		this.isPlaying = false;
	}
}