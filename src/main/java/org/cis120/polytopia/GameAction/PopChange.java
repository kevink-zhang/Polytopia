package org.cis120.polytopia.GameAction;

import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Tile.GameTile;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PopChange implements GameAction {
    public GameTile self;
    public int popadd = 0;

    public PopChange(GameTile s, int pnum) {
        self = s;
        popadd = pnum;
    }

    public boolean tick() {
        end();
        return true;
    }

    public void end() {
        if (self.city != null) {
            self.city.addPopulation(popadd, false);
        }
    }

    public Map<String, Object> encode() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("type", "popchange");
        ret.put("self", this.self.id.toString());
        ret.put("pop", this.popadd);
        return ret;
    }

    public void draw(Graphics g, ImageLoader il) {

    }
}
