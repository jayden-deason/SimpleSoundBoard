import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class HelpMenu {
    private static JDialog d;

    HelpMenu(String input) {
        JFrame f = new JFrame();
        d = new JDialog(f, "Helpful Advice", true);
        d.setLayout(new FlowLayout());
        JButton b = new JButton("Thank You Austin Perez, Matthew Burgdorf, Tung Nguyen, and Jayden!");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                HelpMenu.d.dispose();
            }
        });
        d.add(new JLabel(input));
        d.add(b);
        d.setSize(450, 100);
        d.setVisible(true);
    }
}
