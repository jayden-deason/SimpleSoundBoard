import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SoundButton extends RoundButton implements ActionListener {
    String fileURL;
    String name;
    JMenuItem remove;
    JMenuItem open;
    JMenuItem record;
    JPopupMenu rightClick;
    SwingAudioPlayer player;
    JFrame temp;
    String recName;

    public SoundButton() {
        this.fileURL = "";
        this.rightClick = new JPopupMenu();
        this.open = new JMenuItem("Add Sound");
        this.record = new JMenuItem("Record New Sound");
        this.remove = new JMenuItem("Delete Sound");
        this.rightClick.add(this.open);
        this.rightClick.add(this.record);
        this.rightClick.add(this.remove);
        this.open.addActionListener(this);
        this.remove.addActionListener(this);
        this.record.addActionListener(this);
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    this.doPop(e);
                }

            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    this.doPop(e);
                }

            }

            private void doPop(MouseEvent e) {
                SoundButton.this.rightClick.show(e.getComponent(), e.getX(), e.getY());
            }
        });
        this.setFont(new Font("Arial", 0, 40));
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!SoundButton.this.fileURL.isBlank()) {
                    SoundButton.this.player.openFile(SoundButton.this.fileURL);
                }

            }
        });
    }

    public SoundButton(String url, String text) {
        this();
        this.fileURL = url;
        this.setText(text);
    }

    public SoundButton(String url) {
        this();
        this.fileURL = url;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.open) {
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Sound Files", new String[]{"mp3", "wav"});
            fc.setFileFilter(filter);
            fc.setCurrentDirectory(new File("." + File.separator + "res" + File.separator + this.name + "res"));
            int i = fc.showOpenDialog(this.temp);
            if (i == 0) {
                File fil = fc.getSelectedFile();
                this.fileURL = fil.getPath();
            }

            String input = JOptionPane.showInputDialog(this, "Enter name");
            if (input != null) {
                this.setText(input);
            }
        }

        if (e.getSource() == this.remove) {
            this.setText("");
            this.fileURL = "";
        }

        if (e.getSource() == this.record) {
            String input = JOptionPane.showInputDialog(this, "Enter name");
            (new AudioRecorder(this.name, input)).setVisible(true);
            if (input != null) {
                this.setText(input);
            }

            this.addSound("." + File.separator + "res" + File.separator + this.name + "res" + File.separator + "rec" + input + ".wav");
        }

    }

    public void addSound(String url) {
        this.fileURL = url;
    }

    public void giveFrame(JFrame f) {
        this.temp = f;
    }

    public void givePlayer(SwingAudioPlayer p) {
        this.player = p;
    }

    public void giveName(String name2) {
        this.name = name2;
    }

    public File getFile() {
        return new File(this.fileURL);
    }
}
