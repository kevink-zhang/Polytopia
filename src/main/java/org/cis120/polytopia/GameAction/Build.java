package org.cis120.polytopia.GameAction;

import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Tile.GameTile;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Build implements GameAction {
    public GameTile self;

    public Build(GameTile gt) {
        self = gt;
    }

    public boolean tick() {
        end();
        return true;
    }

    public void end() {
        if (self.building != null) {
            self.building.update();
        }
    }

    public Map<String, Object> encode() {
        Map<String, Object> ret = new HashMap<>();

        ret.put("type", "build");
        ret.put("self", self.id.toString());
        if (self.building != null)
            ret.put("building", self.building.name);
        else
            ret.put("building", "");
        ret.put("terrain", self.terrain);
        ret.put("resource", self.resource);
        if (self.village != null) {
            ret.put("village", true);
            if (self.village.team != null) {
                ret.put("name", self.village.name);
                ret.put("team", self.village.team.userid);
                // ret.put("capital", self.village.capital);
            } else {
                ret.put("team", "");
            }
        } else {
            ret.put("village", false);
        }
        return ret;
    }

    public void draw(Graphics g, ImageLoader il) {

    }
}
