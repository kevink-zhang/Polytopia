package org.cis120.polytopia.GameAction;

import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Unit.Stat;
import org.cis120.polytopia.Unit.Unit;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Upgrade implements GameAction {
    public Unit self;
    public boolean heal;
    public String newunit;

    public Upgrade(Unit s, boolean h, String nu) {
        self = s;
        heal = h;
        newunit = nu;
    }

    public boolean tick() {
        end();
        return true;
    }

    public void end() {
        if (newunit != self.stats.type) {
            if (new Stat(newunit).carry) {
                self.boatAction(newunit);
            }
            return;
        }
        if (heal) {
            self.heal();
        } else {
            self.promote();
        }
    }

    public Map<String, Object> encode() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("type", "upgrade");
        ret.put("self", this.self.pos.id.toString());
        ret.put("heal", this.heal);
        ret.put("new unit", this.newunit);
        return ret;
    }

    public void draw(Graphics g, ImageLoader il) {

    }
}
