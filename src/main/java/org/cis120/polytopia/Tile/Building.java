package org.cis120.polytopia.Tile;

public class Building {
    public int level = 0;
    public String name;
    public GameTile pos;

    public Building(String nnn, GameTile ppp) {
        name = nnn;
        pos = ppp;

        update();
    }

    public void destroy() {
        pos.building = null;

        // reenables special building for city
        if (name.equals("windmill")) {
            pos.city.addPopulation(-level, true);
            pos.city.windmill = false;
        } else if (name.equals("forge")) {
            pos.city.addPopulation(-2 * level, true);
            pos.city.forge = false;
        } else if (name.equals("sawmill")) {
            pos.city.addPopulation(-level, true);
            pos.city.sawmill = false;
        } else if (name.equals("customs house")) {
            pos.city.customhouse = false;
        }
        else if(name.equals("port")){

        }

        for (GameTile a : pos.adj) {
            if (a.building != null) {
                a.building.update();
            }
        }
    }

    public void update() {
        int lastlevel = level;
        level = 0;
        if (name.equals("windmill")) {
            pos.city.windmill = true;
            for (GameTile gt : pos.adj) {
                if (gt.team == pos.team && gt.building != null && gt.building.name == "farm") {
                    level++;
                }
            }
            pos.city.addPopulation(level - lastlevel, false);
        } else if (name.equals("forge")) {
            pos.city.forge = true;
            for (GameTile gt : pos.adj) {
                if (gt.team == pos.team && gt.building != null && gt.building.name.equals("mine")) {
                    level++;
                }
            }
            pos.city.addPopulation(2 * (level - lastlevel), false);
        } else if (name.equals("sawmill")) {
            pos.city.sawmill = true;
            for (GameTile gt : pos.adj) {
                if (gt.team == pos.team && gt.building != null
                        && gt.building.name.equals("lumber hut")) {
                    level++;
                }
            }
            pos.city.addPopulation(2*(level - lastlevel), false);
        } else if (name.equals("customs house")) {
            pos.city.customhouse = true;
            for (GameTile gt : pos.adj) {
                if (gt.team == pos.team && gt.building != null && gt.building.name.equals("port")) {
                    level++;
                }
            }
        }
    }
}
