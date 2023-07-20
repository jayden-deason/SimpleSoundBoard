import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.Border;

import com.formdev.flatlaf.FlatLightLaf;

import jaco.mp3.player.MP3Player;    

public class Soundboard implements ActionListener{    
JFrame f;
JPanel p;
JMenuBar mb;
JMenu file;
JPopupMenu rightClick;
SwingAudioPlayer player;
JMenuItem open;
JMenuItem remove;
JMenuItem resume;
JMenuItem pause;
JMenuItem rename;
JMenuItem record;
JButton clickedButton;
ArrayList<File> files = new ArrayList<File>();
ArrayList<Integer> buttonNumber = new ArrayList<Integer>();
Properties properties = new Properties();
static int numButton = 0;
int tempNumButton;
JButton[] buttons = new JButton[9];
String name;
Clip clip;
long clipTime;
Soundboard(String Iname){    
	try {
		UIManager.setLookAndFeel(new FlatLightLaf());
		//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
		e.printStackTrace();
		}
	if(new File("." + File.separator + "res" + File.separator + name + "res").isFile()) {
		new File("." + File.separator + "res" + File.separator + name + "res");
	}
	
	name = Iname;
	f=new JFrame("Soundboard"); 
	p=new JPanel();
	//p.setBounds(0,20,285,245);
	p.setBounds(20,20,285,245);
    p.setBackground(Color.white);
	open = new JMenuItem("Add Sound");
	remove = new JMenuItem("Remove");
    rename = new JMenuItem("Rename");
    record = new JMenuItem("Record");
    //resume = new JMenuItem("Resume");
   // pause = new JMenuItem("Pause");
	open.addActionListener(this);
	remove.addActionListener(this);
    rename.addActionListener(this);
    record.addActionListener(this);
    //resume.addActionListener(this);
   // pause.addActionListener(this);
	file = new JMenu("Add Sound");
	rightClick = new JPopupMenu();
	file.add(open);
	mb = new JMenuBar();
	mb.setBounds(0,0,800,20);
	mb.add(file);
	file.add(record);
	rightClick.add(remove);
	rightClick.add(rename);
	//rightClick.add(resume);
	//rightClick.add(pause);
	player = new SwingAudioPlayer();
	player.setVisible(true);
	f.setLayout(new BorderLayout());
	f.add(mb, BorderLayout.NORTH);
	f.add(p, BorderLayout.CENTER);
	f.add(player, BorderLayout.SOUTH);
	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
    p.setLayout(new GridLayout(3,3,10,10));
    
    f.setSize(300,300);    
    f.setVisible(true);    
    loadConfigs();
    f.addWindowListener(new WindowAdapter() {
  	  public void windowClosing(WindowEvent e) {
  		  saveConfigs();
  	  }
  	  
  	  });
}    

// Loads config file from userConfigs in application folder
public void loadConfigs() {
	if(new File("." + File.separator + "userConfig" + File.separator + name + ".properties").isFile()) {
	try {
		  FileInputStream fin = new FileInputStream("." + File.separator + "userConfig" + File.separator + name + ".properties");
	      properties.load(fin);
	      fin.close();
	    } catch (IOException l) {
	      System.err.println("Ooops!");
	    }
	tempNumButton = 0;
	tempNumButton = Integer.parseInt(properties.getProperty("activeButtons"));
	if(tempNumButton != 0) {
	for(int i = 0; i < tempNumButton; i++) {
		addButton(properties.getProperty("buttonFile" + i),properties.getProperty("buttonName" + i));
	}
	}
	SwingUtilities.updateComponentTreeUI(f);
	properties.clear();
	numButton = tempNumButton;
	}
}

// Saves config file into userConfig
public void saveConfigs() {
	properties.setProperty("activeButtons","" +numButton);
	int counter = 0;
	for(File tempFile: files) {
		properties.setProperty("buttonFile" + counter,"" +tempFile);
		counter++;
	}
	
	for(int i = 0; i < numButton; i++) {
		properties.setProperty("buttonName" + i, buttons[i].getText());
		
	}
	try {
	  FileOutputStream fout = new FileOutputStream("." + File.separator + "userConfig" + File.separator + name + ".properties");
	  properties.store(fout, null);
	  fout.close();
	  } catch (IOException l) {
	} 
 }

// Defines actions for various buttons used in application
public void actionPerformed(ActionEvent e) {
	String filepath = "";
	if(e.getSource()==open) {
		JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Sound Files","mp3", "wav");
		fc.setFileFilter(filter);
		
		
		fc.setCurrentDirectory(new File("." + File.separator + "res" + File.separator + name + "res"));
		int i = fc.showOpenDialog(f);
		if(i==JFileChooser.APPROVE_OPTION) {
			File fil = fc.getSelectedFile();
			filepath = fil.getPath();
			
		    
			}
		
		}
	if(e.getSource()==remove) {
		p.remove(clickedButton);
		numButton -= 1;
	}
	if(e.getSource()==record) {
		new AudioRecorder(name).setVisible(true);

	}
	if(e.getSource()==pause) {
		 clipTime= clip.getMicrosecondPosition();
		 clip.stop();
		 
	}
	if(e.getSource()==resume) {
		clip.setMicrosecondPosition(clipTime);
		clip.start();
	}
	if(e.getSource()==rename) {
		String input = JOptionPane.showInputDialog(clickedButton,"Enter new name");
		clickedButton.setText(input);
	}
	if(filepath != "") {
	addButton(filepath);
	}
	SwingUtilities.updateComponentTreeUI(f);
}



private void addButton(String url) {
	
	Optional<String> filetype = getExtensionByStringHandling(url);
	if(numButton <= 9);{
	buttons[numButton] = new JButton("" + url);
    setButtonColors();
	buttons[numButton].addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
//    		try {
//            	AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(url));
//                clip = AudioSystem.getClip();
//                clip.open(audioIn);
//                
//            } catch (UnsupportedAudioFileException l) {
//                l.printStackTrace();
//            } catch (IOException l) {
//               l.printStackTrace();
//            } catch (LineUnavailableException l) {
//               l.printStackTrace();
//            }
    		if(filetype.get().equals("mp3")) {
    			new MP3Player(new File(url)).play();
    		}
			if(filetype.get().equals("wav")) {
	
				player.openFile(url);
				//clip.start();
			
            
			}
			
    	}
    });
	buttons[numButton].setMnemonic(numButton + 97);
	buttons[numButton].addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		    if (e.isPopupTrigger())
		        doPop(e);
		}
		public void mouseReleased(MouseEvent e) {
		    if (e.isPopupTrigger())
		        doPop(e);  
		}
		private void doPop(MouseEvent e) {
		    rightClick.show(e.getComponent(), e.getX(), e.getY());
		    clickedButton = (JButton)e.getSource();  
		}
	});
	buttons[numButton].setFont(new Font("Serif", Font.PLAIN, 40));

	p.add(buttons[numButton]);
	String input = JOptionPane.showInputDialog(buttons[numButton],"Enter name");
	buttons[numButton].setText(input);
	
	}
	files.add(new File(url));
	numButton += 1;
}
private void addButton(String url, String text) {
	Optional<String> filetype = getExtensionByStringHandling(url);
	if(numButton <= 9);{
	buttons[numButton] = new JButton(text);
	setButtonColors();
	buttons[numButton].addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
//    		try {
//            	AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(url));
//                clip = AudioSystem.getClip();
//                clip.open(audioIn);
//                
//            } catch (UnsupportedAudioFileException l) {
//                l.printStackTrace();
//            } catch (IOException l) {
//               l.printStackTrace();
//            } catch (LineUnavailableException l) {
//               l.printStackTrace();
//            }
    		if(filetype.get().equals("mp3")) {
    			new MP3Player(new File(url)).play();
    		}
			if(filetype.get().equals("wav")) {
				//clip.start();
			
				player.openFile(url);
			
            
			}
			
    	}
    });
	buttons[numButton].setMnemonic(numButton + 97);
	buttons[numButton].addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		    if (e.isPopupTrigger())
		        doPop(e);
		}
		public void mouseReleased(MouseEvent e) {
		    if (e.isPopupTrigger())
		        doPop(e);  
		}
		private void doPop(MouseEvent e) {
		    rightClick.show(e.getComponent(), e.getX(), e.getY());
		    clickedButton = (JButton)e.getSource();  
		}
	});
	buttons[numButton].setFont(new Font("Serif", Font.PLAIN, 40));
	
	p.add(buttons[numButton]);
	
	}
	files.add(new File(url));
	numButton += 1;
}

private void setButtonColors()
{
	for(int i =0; i<=numButton; i++)
	{
		switch(i)
		{
		case 0:
			buttons[0].setBackground(new Color(198,92,205));
			break;
		case 1:
			buttons[1].setBackground(new Color(241,91,181));
			break;
		case 2: 
			buttons[2].setBackground(new Color(247,142,141));
			break;
		case 3:
			buttons[3].setBackground(new Color(251,185,103));
			break;
		case 4:
			buttons[4].setBackground(new Color(254,228,64));
			break;
		case 5:
			buttons[5].setBackground(new Color(183,217,117));
			break;
		case 6:
			buttons[6].setBackground(new Color(127,208,157));
			break;
		case 7:
			buttons[7].setBackground(new Color(0,187,249));
			break;
		case 8:
			buttons[8].setBackground(new Color(0,216,231));
			break;
			
		}
	}
}

public JFrame getFrame() {
	return f;
}
public Optional<String> getExtensionByStringHandling(String filename) {
    return Optional.ofNullable(filename)
      .filter(f -> f.contains("."))
      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
}
}