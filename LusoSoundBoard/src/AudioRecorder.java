
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class AudioRecorder extends JFrame {
    private JButton btn_refresh;
    private JButton btn_start;
    private JButton btn_stop;
    private JComboBox cmb_bits;
    private JComboBox cmb_file_format;
    private JComboBox cmb_monoORStereo;
    private JComboBox cmb_sample;
    private JComboBox cmb_targetdatalines;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    ArrayList<AudInputLine> lines = new ArrayList();
    TargetDataLine inputline;
    File audoutput;
    boolean start = false;
    AudioFormat format;
    Mixer.Info[] mixerInfo;
    AudioInputStream ais;
    String filename;
    AudioFileFormat.Type fileformat;
    String[] exts = new String[]{"wav", "au", "aiff", "aifc", "snd"};
    File directory;
    String name;
    String input;
    Thread startRec = new Thread() {
        public void run() {
            while(true) {
                if (AudioRecorder.this.start) {
                    AudioRecorder.this.record();
                } else {
                    try {
                        Thread.sleep(300L);
                    } catch (Exception var2) {
                    }
                }
            }
        }
    };

    public AudioRecorder(String userName, String jinput) {
        this.name = userName;
        this.initComponents();
        this.filename = "." + File.separator + "res" + File.separator + this.name + "res";
        this.setDefaultCloseOperation(2);
        this.setAlwaysOnTop(true);
        this.RefreshInputs();
        File fold = new File(this.filename);
        if (!fold.exists()) {
            fold.mkdir();
        }

        this.directory = new File(fold.getPath());
        this.filename = this.filename + File.separator + "rec";
        this.startRec.start();
        this.input = jinput;
    }

    public String getName() {
        return this.input;
    }

    public void RefreshInputs() {
        this.lines.clear();
        this.mixerInfo = AudioSystem.getMixerInfo();
        Mixer.Info[] var5;
        int var4 = (var5 = this.mixerInfo).length;

        for(int var3 = 0; var3 < var4; ++var3) {
            Mixer.Info m = var5[var3];
            Line.Info[] targlines = AudioSystem.getMixer(m).getTargetLineInfo();
            Line.Info[] var9 = targlines;
            int var8 = targlines.length;

            for(int var7 = 0; var7 < var8; ++var7) {
                Line.Info ln = var9[var7];
                AudInputLine tail = new AudInputLine();
                tail.lineInfo = ln;
                tail.mixer = AudioSystem.getMixer(m);
                tail.name = tail.mixer.getMixerInfo().toString();
                this.lines.add(tail);
            }
        }

        for(int i = 0; i < this.lines.size(); ++i) {
            try {
                if (((DataLine.Info)((AudInputLine)this.lines.get(i)).lineInfo).getFormats().length < 1) {
                    this.lines.remove(i);
                    --i;
                }
            } catch (Exception var11) {
                this.lines.remove(i);
                --i;
            }
        }

        this.cmb_targetdatalines.removeAllItems();
        Iterator var13 = this.lines.iterator();

        while(var13.hasNext()) {
            AudInputLine dinf = (AudInputLine)var13.next();
            this.cmb_targetdatalines.addItem(dinf);
        }

    }

    public void RefreshAudioFormats() {
        int[] bits = new int[]{24, 16, 8};
        float[] sampling = new float[]{8000.0F, 11025.0F, 16000.0F, 22050.0F, 44100.0F, 48000.0F, 96000.0F, 192000.0F};
        AudInputLine taud = (AudInputLine)this.cmb_targetdatalines.getSelectedItem();
        this.cmb_sample.removeAllItems();

        int i;
        AudioFormat aftemp;
        for(i = 0; i < sampling.length; ++i) {
            aftemp = new AudioFormat(sampling[i], 8, 1, false, true);
            if (taud.lineInfo instanceof DataLine.Info && ((DataLine.Info)taud.lineInfo).isFormatSupported(aftemp)) {
                this.cmb_sample.addItem(Float.toString(sampling[i]));
                if (sampling[i] == 44100.0F || sampling[i] == 48000.0F) {
                    this.cmb_sample.setSelectedIndex(i);
                }
            }
        }

        this.cmb_bits.removeAllItems();

        for(i = 0; i < bits.length; ++i) {
            aftemp = new AudioFormat(8000.0F, bits[i], 1, bits[i] != 8, true);
            if (taud.lineInfo instanceof DataLine.Info && ((DataLine.Info)taud.lineInfo).isFormatSupported(aftemp)) {
                this.cmb_bits.addItem(Integer.toString(bits[i]));
            }
        }

        /* AudioFormat */ aftemp = new AudioFormat(8000.0F, 8, 2, false, true);
        this.cmb_monoORStereo.removeAllItems();
        if (taud.lineInfo instanceof DataLine.Info && ((DataLine.Info)taud.lineInfo).isFormatSupported(aftemp)) {
            this.cmb_monoORStereo.addItem("Stereo");
        }

        this.cmb_monoORStereo.addItem("Mono");
    }

    public void record() {
        try {
            this.start = false;
            this.inputline.open(this.format);
            this.inputline.start();
            this.ais = new AudioInputStream(this.inputline);
            AudioSystem.write(this.ais, this.fileformat, this.audoutput);
        } catch (Exception var2) {
            JOptionPane.showMessageDialog(this, var2.getMessage());
            this.buttonsEnable(true);
        }

    }

    public void buttonsEnable(boolean f) {
        this.cmb_targetdatalines.setEnabled(f);
        this.cmb_bits.setEnabled(f);
        this.cmb_file_format.setEnabled(f);
        this.cmb_monoORStereo.setEnabled(f);
        this.cmb_sample.setEnabled(f);
        this.btn_stop.setEnabled(!f);
        this.btn_start.setEnabled(f);
    }

    private void initComponents() {
        this.btn_stop = new JButton();
        this.btn_start = new JButton();
        this.cmb_targetdatalines = new JComboBox();
        this.jScrollPane1 = new JScrollPane();
        this.jTextArea1 = new JTextArea();
        this.jLabel6 = new JLabel();
        this.cmb_file_format = new JComboBox();
        this.jLabel7 = new JLabel();
        this.btn_refresh = new JButton();
        this.jLabel2 = new JLabel();
        this.jLabel1 = new JLabel();
        this.jLabel3 = new JLabel();
        this.cmb_sample = new JComboBox();
        this.cmb_bits = new JComboBox();
        this.cmb_monoORStereo = new JComboBox();
        this.setDefaultCloseOperation(2);
        this.btn_stop.setText("Stop");
        this.btn_stop.setEnabled(false);
        this.btn_stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AudioRecorder.this.btn_stopActionPerformed(evt);
            }
        });
        this.btn_start.setText("Start");
        this.btn_start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AudioRecorder.this.btn_startActionPerformed(evt);
            }
        });
        this.cmb_targetdatalines.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AudioRecorder.this.cmb_targetdatalinesActionPerformed(evt);
            }
        });
        this.jTextArea1.setEditable(false);
        this.jTextArea1.setColumns(20);
        this.jTextArea1.setFont(new Font("Monospaced", 0, 11));
        this.jTextArea1.setLineWrap(true);
        this.jTextArea1.setRows(5);
        this.jTextArea1.setWrapStyleWord(true);
        this.jScrollPane1.setViewportView(this.jTextArea1);
        this.jLabel6.setText("Sample Size in bits");
        this.cmb_file_format.setModel(new DefaultComboBoxModel(new String[]{"WAVE", "AU", "AIFF", "AIFF-C", "SND"}));
        this.jLabel7.setText("Select Input Source");
        this.btn_refresh.setText("Refresh Inputs");
        this.btn_refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AudioRecorder.this.btn_refreshActionPerformed(evt);
            }
        });
        this.jLabel2.setText("Sample Rate");
        this.jLabel1.setText("Mono/Stereo");
        this.jLabel3.setText("File Format");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.cmb_targetdatalines, 0, -1, 32767).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED).addComponent(this.jLabel7).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(Alignment.TRAILING, false).addGroup(layout.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createSequentialGroup().addComponent(this.btn_start, -2, 85, -2)).addGroup(layout.createParallelGroup(Alignment.TRAILING, false).addGroup(layout.createSequentialGroup().addGap(10, 10, 10).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.btn_stop, -2, 80, -2).addGroup(layout.createParallelGroup(Alignment.LEADING, false).addGap(0, 0, 32767))).addContainerGap())))))))))));
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel7).addPreferredGap(ComponentPlacement.RELATED).addComponent(this.cmb_targetdatalines, -2, -1, -2).addPreferredGap(ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(Alignment.LEADING, false).addGroup(layout.createParallelGroup(Alignment.BASELINE).addGroup(layout.createParallelGroup(Alignment.BASELINE).addGap(18, 18, 18).addGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.btn_stop, -1, 39, 32767).addComponent(this.btn_start, -1, -1, 32767)).addGap(6, 6, 6))))));
        this.pack();
    }

    private void btn_stopActionPerformed(ActionEvent evt) {
        this.inputline.stop();
        this.inputline.close();

        try {
            this.dispose();
        } catch (Exception var3) {
        }

        this.buttonsEnable(true);
    }

    private void btn_startActionPerformed(ActionEvent evt) {
        new Date();
        this.audoutput = new File(this.filename + this.input + "." + this.exts[this.cmb_file_format.getSelectedIndex()]);
        switch (this.cmb_file_format.getSelectedIndex()) {
            case 0:
                this.fileformat = AudioFileFormat.Type.WAVE;
                break;
            case 1:
                this.fileformat = AudioFileFormat.Type.AU;
                break;
            case 2:
                this.fileformat = AudioFileFormat.Type.AIFF;
                break;
            case 3:
                this.fileformat = AudioFileFormat.Type.AIFC;
                break;
            case 4:
                this.fileformat = AudioFileFormat.Type.SND;
        }

        AudInputLine tau = (AudInputLine)this.cmb_targetdatalines.getSelectedItem();
        this.format = new AudioFormat(Float.parseFloat((String)this.cmb_sample.getSelectedItem()), Integer.parseInt((String)this.cmb_bits.getSelectedItem()), (this.cmb_monoORStereo.getSelectedIndex() + 1) % 2 + 1, Integer.parseInt((String)this.cmb_bits.getSelectedItem()) != 8, true);
        AudInputLine taud = (AudInputLine)this.cmb_targetdatalines.getSelectedItem();
        if (taud.lineInfo instanceof DataLine.Info && !((DataLine.Info)taud.lineInfo).isFormatSupported(this.format)) {
            this.format = new AudioFormat(Float.parseFloat((String)this.cmb_sample.getSelectedItem()), Integer.parseInt((String)this.cmb_bits.getSelectedItem()), (this.cmb_monoORStereo.getSelectedIndex() + 1) % 2 + 1, Integer.parseInt((String)this.cmb_bits.getSelectedItem()) != 8, false);
        }

        try {
            this.inputline = AudioSystem.getTargetDataLine(this.format, tau.mixer.getMixerInfo());
        } catch (LineUnavailableException var6) {
            JOptionPane.showMessageDialog(this, var6.getMessage());
        }

        this.buttonsEnable(false);
        this.start = true;
    }

    private void cmb_targetdatalinesActionPerformed(ActionEvent evt) {
        if (this.cmb_targetdatalines.getItemCount() > 0) {
            AudInputLine tau = (AudInputLine)this.cmb_targetdatalines.getSelectedItem();
            this.jTextArea1.setText(tau.mixer.getMixerInfo().toString() + ".\n" + tau.lineInfo.toString());
            this.RefreshAudioFormats();
        }

    }

    private void btn_refreshActionPerformed(ActionEvent evt) {
        this.cmb_targetdatalines.removeAllItems();
        this.RefreshInputs();
    }
}