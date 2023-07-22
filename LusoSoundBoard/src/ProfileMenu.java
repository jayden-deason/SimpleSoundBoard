import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ProfileMenu implements ActionListener {
	JFrame f;
	JPanel p;
	String name;
	JButton l;
	JButton clickedButton;
	JButton b;
	JMenuItem remove;
	JMenuItem rename;
	JPopupMenu rightClick;
	static ArrayList<Profile> profiles = new ArrayList();

	ProfileMenu() {
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (Exception var5) {
			System.err.println("Failed to initialize LaF");
		}

		try {
			UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
		} catch (Exception var4) {
			var4.printStackTrace();
		}

		File userC = new File("." + File.separator + "userConfig");
		if (!userC.isFile()) {
			userC.mkdir();
		}

		profiles = Profile.loadProfiles();
		this.rightClick = new JPopupMenu();
		this.remove = new JMenuItem("Delete Profile");
		this.rename = new JMenuItem("Rename Profile");
		this.rightClick.add(this.remove);
		this.rightClick.add(this.rename);
		this.remove.addActionListener(this);
		this.rename.addActionListener(this);
		this.f = new JFrame("Profile");
		this.p = new JPanel();
		this.f.add(this.p);
		this.p.setBounds(0, 20, 285, 245);
		this.p.setBackground(Color.white);
		this.f.setSize(150, 800);
		this.f.setVisible(true);
		this.f.setDefaultCloseOperation(3);
		this.p.setLayout(new BoxLayout(this.p, 1));
		this.b = new JButton("Create New Profile");
		this.b.setMaximumSize(new Dimension(2000, 80));
		this.b.setMinimumSize(new Dimension(2000, 80));
		this.p.add(this.b);
		if (!profiles.isEmpty()) {
			Iterator var3 = profiles.iterator();

			while(var3.hasNext()) {
				Profile prof = (Profile)var3.next();
				this.newButton(prof.getName());
			}
		}

		this.b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Enter Profile Name");
				ProfileMenu.this.name = input;
				if (!ProfileMenu.this.name.isBlank()) {
					ProfileMenu.profiles.add(new Profile(ProfileMenu.this.name));
					ProfileMenu.this.newButton(ProfileMenu.this.name);
					Profile.saveProfiles(ProfileMenu.profiles);
				}

			}
		});
		SwingUtilities.updateComponentTreeUI(this.f);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.remove) {
			for(int i = 0; i < profiles.size(); ++i) {
				if (((Profile)profiles.get(i)).getName().equals(this.clickedButton.getLabel())) {
					Profile.deleteProfile(i);
					profiles.remove(i);
					this.p.remove(this.clickedButton);
				}
			}

			Profile.saveProfiles(profiles);
		}

		if (e.getSource() == this.rename) {
			String input = JOptionPane.showInputDialog(this.clickedButton, "Enter new name");

			for(int i = 0; i < profiles.size(); ++i) {
				if (((Profile)profiles.get(i)).getName().equals(this.clickedButton.getLabel())) {
					profiles.remove(i);
					profiles.add(i, new Profile(input));
				}

				Profile.saveProfiles(profiles);
			}

			this.clickedButton.setText(input);
		}

		SwingUtilities.updateComponentTreeUI(this.f);
	}

	private void newButton(final String name) {
		this.l = new JButton(name);
		this.l.setMaximumSize(new Dimension(2000, 80));
		this.l.setMinimumSize(new Dimension(2000, 80));
		this.p.add(this.l);
		this.l.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Soundboard(name);
				ProfileMenu.this.f.dispose();
			}
		});
		this.l.addMouseListener(new MouseAdapter() {
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
				ProfileMenu.this.rightClick.show(e.getComponent(), e.getX(), e.getY());
				ProfileMenu.this.clickedButton = (JButton)e.getSource();
			}
		});
		this.p.setComponentZOrder(this.l, 0);
		SwingUtilities.updateComponentTreeUI(this.f);
	}

	public static void main(String[] args) {
		new ProfileMenu();
	}
}
