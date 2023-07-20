import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;
public class ProfileMenu implements ActionListener { 
	
	{
	try {
	    UIManager.setLookAndFeel( new FlatLightLaf() );
	} catch( Exception ex ) {
	    System.err.println( "Failed to initialize LaF" );
	}
	}
	JFrame f;
	JPanel p;
	String name;
	JButton l;
	JButton clickedButton;
	JButton b;
	JMenuItem remove;
	JMenuItem rename;
	JPopupMenu rightClick;
	static ArrayList<Profile> profiles = new ArrayList<Profile>();

	
	ProfileMenu(){ try {
		UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
		} catch (Exception e) {
		e.printStackTrace();
		}
	profiles= Profile.loadProfiles();
	rightClick = new JPopupMenu();
	remove = new JMenuItem("Remove");
    rename = new JMenuItem("Rename");
	rightClick.add(remove);
	rightClick.add(rename);
	remove.addActionListener(this);
    rename.addActionListener(this);
	f=new JFrame("Profile"); 
	p=new JPanel();
	f.add(p);
	p.setBounds(0,20,285,245);    
    p.setBackground(Color.white);
    f.setSize(150,800);    
    f.setVisible(true);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    b = new JButton("Create New Profile");
	b.setMaximumSize(new Dimension(150, 80));
	b.setMinimumSize(new Dimension(150, 80));
    p.add(b);
    
    if(!profiles.isEmpty())
    {
    	for(Profile prof : profiles)
    	{
    		newButton(prof.getName());
    	}
    }
    
    b.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e)
    	{
    		String input = JOptionPane.showInputDialog("Enter Profile Name");
    	    name=input;
    	    if(!name.isBlank())
    	  {
    	    profiles.add(new Profile(name));
    	    newButton(name);
    	    Profile.saveProfiles(profiles);
    	  }
    	    
    	}
    });
    SwingUtilities.updateComponentTreeUI(f);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==remove) {
		for(int i=0; i<profiles.size(); i++)
		{
			if (profiles.get(i).getName().equals(clickedButton.getLabel()))
			{
				Profile.deleteProfile(i);
				profiles.remove(i);
				p.remove(clickedButton);
			}
		}
		Profile.saveProfiles(profiles);
		}
		if(e.getSource()==rename) {
			String input = JOptionPane.showInputDialog(clickedButton,"Enter new name");
			for(int i=0; i<profiles.size(); i++)
			{
				if (profiles.get(i).getName().equals(clickedButton.getLabel()))
				{
					profiles.remove(i);
					profiles.add(i, new Profile(input));
				}
				Profile.saveProfiles(profiles);
			}
			clickedButton.setText(input);
		}
		SwingUtilities.updateComponentTreeUI(f);
	}
	
	
	private void newButton(String name)
	{
		l = new JButton(name);
		l.setMaximumSize(new Dimension(150, 80));
		l.setMinimumSize(new Dimension(150, 80));
	    p.add(l);
	    
	    l.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		new Soundboard(name);
	    		f.dispose();	
	    	}
	    });
	    
	    l.addMouseListener(new MouseAdapter() {
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
		}});
	    
	    p.setComponentZOrder(l,0);
	    SwingUtilities.updateComponentTreeUI(f);
	}
	
	
	public static void main(String[] args) {    
	    new ProfileMenu(); 
}
}

