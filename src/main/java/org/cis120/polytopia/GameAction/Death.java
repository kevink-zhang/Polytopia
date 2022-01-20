package org.cis120.polytopia.GameAction;

import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Unit.Unit;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Death implements GameAction {
    public Unit self;

    public Death(Unit s) {
        self = s;
    }

    public boolean tick() {
        end();
        return true;
    }

    public void end() {
        self.origin.units.remove(self);
        self.pos.unit = null;
    }

    public Map<String, Object> encode() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("type", "death");
        ret.put("self", this.self.pos.id.toString());
        return ret;
    }

    public void draw(Graphics g, ImageLoader il) {

    }
}
