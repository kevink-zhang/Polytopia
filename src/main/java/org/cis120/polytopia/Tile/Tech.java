package org.cis120.polytopia.Tile;

import org.cis120.polytopia.CircleButton;
import org.cis120.polytopia.FontLoader;
import org.cis120.polytopia.ImageLoader;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.cis120.polytopia.GameBody.USER;

public class Tech {
    public String name;
    public boolean status;

    public Tech parent = null;
    public List<Tech> child = new LinkedList<>();
    public int tier = 0;

    public Point pos;
    public final int radius = 30;
    public CircleButton hitbox;
    public List<String> techicons = new LinkedList<>();

    public Tech(String n, int t, boolean b) {
        name = n;
        status = b;
        tier = t;
        pos = new Point(0, 0);
        if (n .equals("ROOT")) {
            name = ""; // root node has no name
        }
        hitbox = new CircleButton(name, 0, 0, radius, null, 25, 25);
    }

    public void addParent(Tech t) {
        parent = t;
    }

    public void addChild(Tech t) {
        child.add(t);
    }

    public void addPos(Point p) {
        pos = p;
        hitbox.pos = p;
    }

    public int cost(int c) {
        return 4 + tier * c;
    }

    public void draw(Graphics g, ImageLoader il, FontLoader fl) {
        if(!status){
            hitbox.cost = cost(USER.cities.size());
        }
        else{
            hitbox.cost = -1;
        }

        ((Graphics2D) g).setStroke(new BasicStroke(5));
        for (Tech t : child) {
            if (t.status && this.status) {
                g.setColor(new Color(230, 230, 230));
            } else {
                g.setColor(new Color(100, 100, 100));
            }
            g.drawLine(pos.x, pos.y, t.pos.x, t.pos.y);
        }
        ((Graphics2D) g).setStroke(new BasicStroke(1));

        if (status)
            hitbox.c = new Color(19, 210, 70, 230);
        hitbox.draw(g, il, fl);

        if (!techicons.isEmpty()) {
            int sx = hitbox.pos.x - (int) (hitbox.radius * 0.8);
            int counter = 0;
            for (String s : techicons) {
                int iconr = (int) (hitbox.radius * 1.6 / techicons.size());
                Point delta = new Point(0, 0);
                // modifiers to some
                if (s.equals("hunting")) {
                    iconr *= 0.5;
                    delta.x = 15;
                } else if (s.equals("archer")) {
                    iconr *= 2;
                    delta.x = -9;
                    delta.y = -6;
                } else if (s.equals("forest bonus")) {
                    iconr *= 1.2;
                    delta.x = -7;
                    delta.y = -9;
                } else if (s.equals("forest temple")) {
                    iconr *= 2.5;
                    delta.x = -55;
                    delta.y = -17;
                } else if (s.equals("grow forest")) {
                    iconr *= 1.5;
                    delta.x = 6;
                    delta.y = 7;
                } else if (s.equals("lumber hut")) {
                    iconr *= 1.5;
                    delta.x = -17;
                    delta.y = -18;
                } else if (s.equals("catapult")) {
                    iconr *= 1.5;
                    delta.y = 3;
                } else if (s.equals("sawmill")) {
                    iconr *= 1.2;
                    delta.x = -6;
                    delta.y = -8;
                } else if (s.equals("rider")) {
                    delta.x = 1;
                    delta.y = -9;
                } else if (s.equals("customs house")) {
                    iconr *= 0.8;
                    delta.x = 6;
                    delta.y = -5;
                } else if (s.equals("temple")) {
                    iconr *= 2.5;
                    delta.x = -11;
                    delta.y = 8;
                } else if (s.equals("clear forest")) {
                    iconr *= 2.5;
                    delta.x = -21;
                    delta.y = -9;
                } else if (s.equals("retire")) {
                    delta.x = -14;
                    delta.y = -2;
                } else if (s.equals("knight")) {
                    iconr *= 1.5;
                    delta.x = 0;
                    delta.y = 0;
                } else if (s.equals("burn forest")) {
                    iconr *= 1.5;
                    delta.x = -14;
                    delta.y = -13;
                } else if (s.equals("harvest")) {
                    iconr *= 0.7;
                    delta.x = 8;
                    delta.y = 4;
                } else if (s.equals("windmill")) {
                    iconr *= 1.3;
                    delta.x = 6;
                    delta.y = 8;
                } else if (s.equals("destroy")) {
                    iconr *= 1.3;
                    delta.x = -13;
                    delta.y = -11;
                } else if (s.equals("defender")) {
                    iconr *= 1.3;
                    delta.x = -4;
                    delta.y = -17;
                } else if (s.equals("guard tower")) {
                    iconr *= 0.8;
                    delta.x = 7;
                    delta.y = -4;
                } else if (s.equals("mine")) {
                    iconr *= 0.7;
                    delta.x = 7;
                    delta.y = 0;
                } else if (s.equals("forge")) {
                    iconr *= 0.8;
                    delta.x = 5;
                    delta.y = -8;
                } else if (s.equals("swordsman")) {
                    iconr *= 2;
                    delta.x = -9;
                    delta.y = -7;
                } else if (s.equals("mountain temple")) {
                    iconr *= 1.3;
                    delta.x = -4;
                    delta.y = 3;
                } else if (s.equals("mountain bonus")) {
                    iconr *= 1.2;
                    delta.x = -8;
                    delta.y = -8;
                } else if (s.equals("mind bender")) {
                    iconr *= 2;
                    delta.x = -10;
                    delta.y = -7;
                } else if (s.equals("literacy")) {
                    iconr *= 1;
                    delta.x = 3;
                    delta.y = -10;
                } else if (s.equals("fish")) {
                    iconr *= 1.5;
                    delta.x = -12;
                    delta.y = 0;
                } else if (s.equals("ship")) {
                    iconr *= 1.5;
                    delta.x = -3;
                    delta.y = 3;
                } else if (s.equals("port")) {
                    iconr *= 1.5;
                    delta.x = -12;
                    delta.y = 0;
                } else if (s.equals("water")) {
                    iconr *= 1;
                    delta.x = -2;
                    delta.y = -9;
                } else if (s.equals("battleship")) {
                    iconr *= 1.5;
                    delta.x = -2;
                    delta.y = 2;
                } else if (s.equals("ocean")) {
                    iconr *= 1;
                    delta.x = 2;
                    delta.y = -10;
                } else if (s.equals("whale")) {
                    iconr *= 1.5;
                    delta.x = -12;
                    delta.y = 0;
                } else if (s.equals("water temple")) {
                    iconr *= 1.2;
                    delta.x = 0;
                    delta.y = 6;
                } else if (s.equals("water bonus")) {
                    iconr *= 1.2;
                    delta.x = -2;
                    delta.y = 0;
                } else if (s.equals("ocean bonus")) {
                    iconr *= 1.2;
                    delta.x = -4;
                    delta.y = -6;
                }
                int h = iconr * il.getTech(s).getHeight(null) / il.getTech(s).getWidth(null);
                g.drawImage(
                        il.getTech(s), sx + counter * iconr + delta.x,
                        hitbox.pos.y - h / 2 + delta.y,
                        iconr, h, null
                );
                counter++;
            }
        }

    }
}
