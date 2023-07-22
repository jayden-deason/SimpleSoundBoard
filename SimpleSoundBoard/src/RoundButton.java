import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.JButton;

public class RoundButton extends JButton {
    Shape shape;

    public RoundButton() {
        Dimension size = this.getPreferredSize();
        size.width = size.height = Math.max(size.width, size.height);
        this.setPreferredSize(size);
        this.setContentAreaFilled(false);
    }

    protected void paintComponent(Graphics g) {
        if (this.getModel().isArmed()) {
            g.setColor(this.getBackground().darker());
        } else {
            g.setColor(this.getBackground());
        }

        g.fillRoundRect(0, 0, this.getSize().width - 1, this.getSize().height - 1, this.getSize().width / 8, this.getSize().width / 8);
        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
        g.setColor(this.getForeground());
        g.drawRoundRect(0, 0, this.getSize().width - 1, this.getSize().height - 1, this.getSize().width / 8, this.getSize().width / 8);
    }

    public boolean contains(int x, int y) {
        if (this.shape == null || !this.shape.getBounds().equals(this.getBounds())) {
            this.shape = new Ellipse2D.Float(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight());
        }

        return this.shape.contains((double)x, (double)y);
    }
}
