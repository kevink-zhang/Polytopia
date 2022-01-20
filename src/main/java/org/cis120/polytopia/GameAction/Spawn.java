package org.cis120.polytopia.GameAction;

import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Server.ServerPacket;
import org.cis120.polytopia.Tile.GameTile;
import org.cis120.polytopia.Unit.Stat;
import org.cis120.polytopia.Unit.Unit;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Spawn implements GameAction {
    public GameTile orig;
    public String uname;

    public Spawn(String u, GameTile o) {
        uname = u;
        orig = o;
    }

    public boolean tick() {
        end();
        return true;
    }

    public void end() {
        if (orig.village == null)
            return;
        Unit uwu = new Unit(new Stat(uname), orig.village, orig.village.team, orig);
        if (orig.unit != null) { // pushes other unit away, should be reworked to move if i have
                                 // time
            for (GameTile a : orig.adj) {
                if (a.unit == null) {
                    a.unit = orig.unit;
                    break;
                }
            }
        }
        orig.unit = uwu;
        orig.village.units.add(uwu);
    }

    public Map<String, Object> encode() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("type", "spawn");
        ret.put("origin", this.orig.id.toString());
        ret.put("troop", this.uname);
        return ret;
    }

    public void draw(Graphics g, ImageLoader il) {

    }
}
