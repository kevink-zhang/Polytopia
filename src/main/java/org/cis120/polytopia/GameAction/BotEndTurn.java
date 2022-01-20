package org.cis120.polytopia.GameAction;

import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Unit.Unit;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BotEndTurn implements GameAction {

    public BotEndTurn() {

    }

    public boolean tick() {
        end();
        return true;
    }

    public void end() {
        return;
    }

    public Map<String, Object> encode() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("type", "botendturn");
        return ret;
    }

    public void draw(Graphics g, ImageLoader il) {

    }
}
