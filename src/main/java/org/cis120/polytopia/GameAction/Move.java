package org.cis120.polytopia.GameAction;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Tile.GameTile;
import org.cis120.polytopia.Unit.Unit;

public class Move implements GameAction {
    public final int speed = 10;
    public Unit self;
    public List<GameTile> path;
    public GameTile orig = null;
    public GameTile dest = null;

    public Move(Unit s, List<GameTile> p) {
        self = s;
        path = p;
        if (path.size() > 0) {
            orig = path.get(0); // saves origin tile
            dest = path.get(path.size() - 1); // saves destination tile
        }

    }

    public boolean tick() {
        Point target = path.get(0).center;
        Point at = new Point(
                self.pos.center.x + self.delta.x,
                self.pos.center.y + self.delta.y
        );
        Point delta = new Point(target.x - at.x, target.y - at.y);
        int mag = (int) delta.distance(0, 0);
        if (mag > 0) {
            delta = new Point((speed * delta.x) / mag, (speed * delta.y) / mag);
            if (at.distance(target) > speed / 1.5) {
                self.delta.x += delta.x;
                self.delta.y += delta.y;
            } else {
                self.pos.unit = null;
                self.pos = path.remove(0);
                self.pos.unit = self;
                self.delta.x = 0;
                self.delta.y = 0;
            }
        } else {
            self.pos.unit = null;
            self.pos = path.remove(0);
            self.pos.unit = self;
            self.delta.x = 0;
            self.delta.y = 0;
        }

        if (path.isEmpty()) {
            end();
            return true;
        }
        return false;
    }

    public void end() {
        self.delta = new Point(0, 0);

        if (dest != null) {
            // self.pos = dest;
            // dest.unit = self;
            // orig.unit = null;
            orig.select(false);

            // sets tile selection to unit
            self.pos.tileorunit = false;
            self.pos.selected = true;
            self.setMoves();
        }
        if (!orig.terrain.equals("ocean") && !orig.terrain.equals("water")
                && dest.terrain.equals("water")) {
            self.boatAction("boat");
        } else if ((orig.terrain.equals("ocean") || orig.terrain.equals("water"))
                && (!dest.terrain.equals("ocean") && !dest.terrain.equals("water"))) {
            self.beach();
        }
    }

    public Map<String, Object> encode() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("type", "move");
        ret.put("self", this.self.pos.id.toString());
        List<Object> convertedPath = new LinkedList<>();
        for (GameTile gt : path) {
            convertedPath.add(gt.id.toString());
        }
        ret.put("path", convertedPath);
        return ret;
    }

    public void draw(Graphics g, ImageLoader il) {

    }
}
