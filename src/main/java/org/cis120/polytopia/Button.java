package org.cis120.polytopia;

import java.awt.*;

import static org.cis120.polytopia.GameBody.PANEL_HEIGHT;
import static org.cis120.polytopia.GameBody.PANEL_WIDTH;

public class Button {
    public Point pos;
    public final Point dim;
    public Color c = new Color(100, 100, 100);
    public String label = "";

    public Button(String s, int x, int y, int w, int h) {
        label = s;
        pos = new Point(x, y);
        dim = new Point(w, h);
    }

    public boolean click(Point cp) {
        if (cp == null) {
            return false;
        }
        return cp.x > PANEL_WIDTH / 2 - dim.x / 2 + pos.x
                && cp.x < PANEL_WIDTH / 2 + dim.x / 2 + pos.x
                && cp.y > PANEL_HEIGHT / 2 - dim.y / 2 + pos.y
                && cp.y < PANEL_HEIGHT / 2 + dim.y / 2 + pos.y;
    }

    public void draw(Graphics g, ImageLoader il, FontLoader fl) {
        Point ppos = new Point(
                PANEL_WIDTH / 2 + pos.x - dim.x / 2, PANEL_HEIGHT / 2 + pos.y - dim.y / 2
        );

        Graphics2D g2 = ((Graphics2D) g);
        g.setColor(new Color(50, 154, 233));
        if (label.equals("BACK")) {
            g.setColor(new Color(126, 126, 126));
        }
        g2.fillRoundRect(ppos.x, ppos.y, dim.x, dim.y, 20, 20);
        g2.drawRoundRect(ppos.x, ppos.y, dim.x, dim.y, 20, 20);

        g.setColor(new Color(220, 220, 230));

        g.setFont(fl.head2);
        int stringwidth = g.getFontMetrics().stringWidth(label);
        g.drawString(label, ppos.x + dim.x / 2 - stringwidth / 2, ppos.y + dim.y / 2 + 10);
    }
}
