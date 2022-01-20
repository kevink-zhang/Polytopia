package org.cis120.polytopia;

import java.awt.*;

import static org.cis120.polytopia.GameBody.USER;

public class CircleButton {
    public Point pos;
    public final int radius;
    // public Polygon hitbox;
    public Color c = new Color(100, 100, 100);
    public String label = "";
    public Image icon;
    public Point idim;
    public int cost = -1;

    public String direction;

    public CircleButton(String s, int x, int y, int r, Image I, int ix, int iy) {
        label = s;
        pos = new Point(x, y);
        radius = r;
        icon = I;
        idim = new Point(ix, iy);

        if (label.equals("ARROW")) {
            c = new Color(230, 230, 230, 240);
        } else {
            c = new Color(122, 174, 255, 230);
        }
    }

    public boolean click(Point cp) {
        if (cp == null) {
            return false;
        }
        return cp.distance(pos) <= radius;
    }

    public void draw(Graphics g, ImageLoader il, FontLoader fl) {

        if (label.equals("ARROW")) {
            g.setColor(c);
            g.fillOval(pos.x - radius, pos.y - radius, 2 * radius, 2 * radius);
            g.setColor(new Color(20, 20, 20));
            ((Graphics2D) g).setStroke(new BasicStroke(4));
            if (direction.equals("BACK")) {
                g.drawLine(pos.x - radius / 4, pos.y, pos.x + radius / 4, pos.y - 2 * radius / 4);
                g.drawLine(pos.x - radius / 4, pos.y, pos.x + radius / 4, pos.y + 2 * radius / 4);
            } else if (direction.equals("DOWN")) {
                g.drawLine(pos.x, pos.y + radius / 4, pos.x - 2 * radius / 4, pos.y - radius / 4);
                g.drawLine(pos.x, pos.y + radius / 4, pos.x + 2 * radius / 4, pos.y - radius / 4);
            }
            ((Graphics2D) g).setStroke(new BasicStroke(1));
        } else {
            g.setColor(c);
            g.fillOval(pos.x - radius, pos.y - radius, 2 * radius, 2 * radius);
            ((Graphics2D) g).setStroke(new BasicStroke(2));
            if(USER!= null && cost> USER.money){
                g.setColor(Color.red);
            }
            else{
                g.setColor(Color.white);
            }

            g.drawOval(pos.x - radius, pos.y - radius, 2 * radius, 2 * radius);
            ((Graphics2D) g).setStroke(new BasicStroke(1));
            g.drawImage(icon, pos.x - idim.x / 2, pos.y - idim.y / 2, idim.x, idim.y, null);
            g.setFont(fl.text2);
            int stringwidth = g.getFontMetrics().stringWidth(label);
            //g.setColor(Color.WHITE);
            g.drawString(label, pos.x - stringwidth / 2, (int) (pos.y + 1.5 * radius));
            if(cost>-1){
                stringwidth = g.getFontMetrics().stringWidth(cost+" ");
                g.drawString(cost + " ", pos.x-3*radius/4-stringwidth, pos.y-3*radius/4);
                g.drawImage(il.star, pos.x-3*radius/4-2, pos.y-3*radius/4-9, 10, 10, null);
            }
        }
    }
}
