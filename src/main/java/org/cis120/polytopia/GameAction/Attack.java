package org.cis120.polytopia.GameAction;

import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Unit.Unit;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Attack implements GameAction {
    public final int speed = 10;
    public Unit origin;
    public Unit target;
    public double damage;
    public int direction = 1;
    public boolean kill;

    public Attack(Unit o, Unit t, double d) {
        origin = o;
        target = t;
        damage = d;
        kill = d == t.stats().hp();
    }

    public boolean tick() {
        Point delta = new Point(
                target.pos.center.x - origin.pos.center.x,
                target.pos.center.y - origin.pos.center.y
        );
        int mag = (int) delta.distance(new Point(0, 0));
        if (mag > 0)
            delta = new Point((speed * delta.x) / mag, (speed * delta.y) / mag);

        if (direction == 1 && target.pos.center.distance(
                origin.pos.center.x + origin.delta.x,
                origin.pos.center.y + origin.delta.y
        ) < 4 * speed / 1.5) {
            direction = -1;

            target.takeDamage((int) damage);
            if (kill) {
                end();
                return true;
            }
        } else if (direction == -1 && origin.pos.center.distance(
                origin.pos.center.x + origin.delta.x,
                origin.pos.center.y + origin.delta.y
        ) < speed / 1.5) {
            end();
            return true;
        }
        origin.delta.x += direction * delta.x;
        origin.delta.y += direction * delta.y;

        return false;
    }

    public void end() {
        if (kill) {
//             origin.pos.unit = null;
//             origin.pos = target.pos;
//             origin.pos.unit = origin;
            origin.kills++;
        }
        origin.delta = new Point(0, 0);
        if (kill && origin.stats().persist) {
            origin.pos.tileorunit = false;
            origin.pos.selected = true;
            origin.setMoves();

            origin.movephase = 1;
        }
        if (origin.stats().escape) {
            origin.pos.tileorunit = false;
            origin.pos.selected = true;
            origin.setMoves();
            origin.movephase = 2;
        } else {
            origin.movephase = 3;
        }
    }

    public Map<String, Object> encode() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("type", "attack");
        ret.put("origin", this.origin.pos.id.toString());
        ret.put("target", this.target.pos.id.toString());
        ret.put("damage", this.damage);
        return ret;
    }

    public void draw(Graphics g, ImageLoader il) {

    }
}
