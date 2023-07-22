import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.Properties;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Soundboard implements ActionListener {
	JFrame f;
	JPanel p;
	JMenuBar mb;
	JMenu file;
	JPopupMenu rightClick;
	SwingAudioPlayer player;
	JMenu help;
	JMenuItem Hhelp;
	JMenuItem aboutUs;
	JMenuItem open;
	JMenuItem remove;
	JMenuItem resume;
	JMenuItem pause;
	JMenuItem rename;
	JMenuItem record;
	JButton clickedButton;
	ArrayList<File> files = new ArrayList();
	ArrayList<Integer> buttonNumber = new ArrayList();
	Properties properties = new Properties();
	static int numButton = 0;
	int tempNumButton;
	SoundButton[] buttons = new SoundButton[9];
	private static final Insets insetsData = new Insets(2, 2, 2, 2);
	String name;
	Clip clip;
	long clipTime;

	Soundboard(String Iname) {
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (Exception var5) {
			var5.printStackTrace();
		}

		File res = new File("." + File.separator + "res");
		if (!res.isFile()) {
			res.mkdir();
		}

		File images = new File("." + File.separator + "images");
		if (!images.isFile()) {
			images.mkdir();
		}

		this.name = Iname;
		this.f = new JFrame("Soundboard");
		this.p = new JPanel();
		this.p.setBounds(20, 20, 285, 245);
		this.p.setBackground(Color.white);
		this.remove = new JMenuItem("Remove");
		this.rename = new JMenuItem("Rename");
		this.help = new JMenu("Help");
		this.help.addActionListener(this);
		this.Hhelp = new JMenuItem("Help");
		this.aboutUs = new JMenuItem("About Us");
		this.Hhelp.addActionListener(this);
		this.aboutUs.addActionListener(this);
		this.remove.addActionListener(this);
		this.rename.addActionListener(this);
		this.rightClick = new JPopupMenu();
		this.mb = new JMenuBar();
		this.mb.setBounds(0, 0, 800, 20);
		this.mb.add(this.help);
		this.help.add(this.Hhelp);
		this.rightClick.add(this.remove);
		this.rightClick.add(this.rename);
		this.player = new SwingAudioPlayer();
		this.player.setVisible(true);
		this.f.setLayout(new BorderLayout());
		this.f.add(this.mb, "North");
		this.f.add(this.p, "Center");
		this.f.add(this.player, "South");
		this.f.setDefaultCloseOperation(3);
		this.p.setBorder(BorderFactory.createEmptyBorder(this.p.getSize().height / 25, this.p.getSize().width / 25, this.p.getSize().height / 25, this.p.getSize().width / 25));
		this.p.setLayout(new GridLayout(3, 3, this.p.getSize().width / 25, this.p.getSize().height / 25));
		this.f.setSize(500, 500);
		this.f.setVisible(true);
		this.initButtons();

		for(int i = 0; i < this.buttons.length; ++i) {
			this.p.add(this.buttons[i]);
			this.setButtonColors(i);
		}

		this.loadConfigs();
		this.f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Soundboard.this.saveConfigs();
			}
		});
	}

	public void loadConfigs() {
		if ((new File("." + File.separator + "userConfig" + File.separator + this.name + ".properties")).isFile()) {
			try {
				FileInputStream fin = new FileInputStream("." + File.separator + "userConfig" + File.separator + this.name + ".properties");
				this.properties.load(fin);
				fin.close();
			} catch (IOException var6) {
				System.err.println("Ooops!");
			}

			int counter = 0;
			SoundButton[] var5;
			int var4 = (var5 = this.buttons).length;

			for(int var3 = 0; var3 < var4; ++var3) {
				SoundButton b = var5[var3];
				b.setText(this.properties.getProperty("buttonName" + counter));
				b.addSound(this.properties.getProperty("buttonFile" + counter));
				++counter;
			}

			SwingUtilities.updateComponentTreeUI(this.f);
			this.properties.clear();
		}

	}

	public void saveConfigs() {
		int counter = 0;
		SoundButton[] var5;
		int var4 = (var5 = this.buttons).length;

		for(int var3 = 0; var3 < var4; ++var3) {
			SoundButton b = var5[var3];
			this.files.add(b.getFile());
		}

		for(Iterator var10 = this.files.iterator(); var10.hasNext(); ++counter) {
			File tempFile = (File)var10.next();
			this.properties.setProperty("buttonFile" + counter, "" + tempFile);
		}

		for(int i = 0; i < 9; ++i) {
			this.properties.setProperty("buttonName" + i, this.buttons[i].getText());
		}

		try {
			FileOutputStream fout = new FileOutputStream("." + File.separator + "userConfig" + File.separator + this.name + ".properties");
			this.properties.store(fout, (String)null);
			fout.close();
		} catch (IOException var6) {
		}

	}

	public void actionPerformed(ActionEvent e) {
		String filepath = "";
		if (e.getSource() == this.open) {
			JFileChooser fc = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Sound Files", new String[]{"mp3", "wav"});
			fc.setFileFilter(filter);
			fc.setCurrentDirectory(new File("." + File.separator + "res" + File.separator + this.name + "res"));
			int i = fc.showOpenDialog(this.f);
			if (i == 0) {
				File fil = fc.getSelectedFile();
				filepath = fil.getPath();
			}
		}

		if (e.getSource() == this.Hhelp) {
			new HelpMenu("SimpleSoundboard only accepts .wav files");
		}

		SwingUtilities.updateComponentTreeUI(this.f);
	}

	private void initButtons() {
		for(int i = 0; i < 9; ++i) {
			this.buttons[i] = new SoundButton();
			this.buttons[i].giveFrame(this.f);
			this.buttons[i].givePlayer(this.player);
			this.buttons[i].giveName(this.name);
		}

	}

	private void addButton(String url, String text) {
		for(int i = 0; i < 9; ++i) {
			this.buttons[i] = new SoundButton(url, text);
			this.buttons[i].giveFrame(this.f);
			this.buttons[i].givePlayer(this.player);
			this.buttons[i].giveName(this.name);
		}

	}

	private void setButtonColors(int g) {
		for(int i = 0; i <= g; ++i) {
			switch (i) {
				case 0:
					this.buttons[0].setBackground(new Color(72, 82, 107));
					break;
				case 1:
					this.buttons[1].setBackground(new Color(224, 130, 117));
					break;
				case 2:
					this.buttons[2].setBackground(new Color(160, 202, 207));
					break;
				case 3:
					this.buttons[3].setBackground(new Color(160, 202, 207));
					break;
				case 4:
					this.buttons[4].setBackground(new Color(72, 82, 107));
					break;
				case 5:
					this.buttons[5].setBackground(new Color(224, 130, 117));
					break;
				case 6:
					this.buttons[6].setBackground(new Color(224, 130, 117));
					break;
				case 7:
					this.buttons[7].setBackground(new Color(160, 202, 207));
					break;
				case 8:
					this.buttons[8].setBackground(new Color(72, 82, 107));
			}
		}

	}

	public JFrame getFrame() {
		return this.f;
	}

	public Optional<String> getExtensionByStringHandling(String filename) {
		return Optional.ofNullable(filename).filter((f) -> {
			return f.contains(".");
		}).map((f) -> {
			return f.substring(filename.lastIndexOf(".") + 1);
		});
	}
}
