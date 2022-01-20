package org.cis120.polytopia.Tile;

import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Pair;
import org.cis120.polytopia.Unit.Stat;
import org.cis120.polytopia.Unit.Unit;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static org.cis120.polytopia.GameBoard.VISION;
import static org.cis120.polytopia.GameBody.USER;

public class GameTile {
    public final Object id; // identifying id of the object
    public Polygon hitbox; // hitbox object of the tile, usually going to be a polygon
    public Point center; // center point of the tile
    public String style; /*
                          * what style to draw the tile as
                          * current styles include:
                          * hex - hexagon
                          */
    public String tstyle; // what tribe style to draw the tile info as
    public Tribe ttstyle; // same as above

    public String terrain; // what the tile terrain is (land, water, mountain, ocean)
    public City village; // if the tile has a village.
                         // village overrides any other tile attributes to defaults
    public String resource; // the resource on this tile
    public Building building; // the building on this tile
    public boolean roads = false; // whether or not the tile has roads on it

    public Tribe team; // which tribe has the tile occupied
    public City city; // which city this tile belongs to

    public boolean selected; // if the current tile is selected in the board
    public boolean tileorunit; // for toggling selection of same tile
    public int targetselector; // 0: nothing,
                               // 1: draws a blue movement target on this tile,
                               // 2: draws a red movement target on this tile

    public Unit unit; // the current unit on this tile
    public Set<GameTile> adj; // set of adjacent tiles
    public List<Edge> outline; // edge outline of tile

    public final double SHEAR = 0.84; // squishing of y axis for 2.5D look

    public GameTile(Object inid, String s, Polygon p) {
        id = inid;
        adj = new HashSet<>();
        outline = new LinkedList<>();
        style = s;
        tstyle = null;
        center = new Point(-1, -1);
        targetselector = 0;
        team = null;

        village = null;
        terrain = "land";
        resource = null;
        roads = false;

        if (style.equals("hex")) {
            hitbox = p;
            int tx = 0;
            int ty = 0;
            for (int i = 0; i < p.npoints; i++) {
                tx += p.xpoints[i];
                ty += p.ypoints[i];
            }
            if (p.npoints > 0) {
                center.x = tx / p.npoints;
                center.y = ty / p.npoints;
            }
        }
    }

    public GameTile() {
        id = null;
        adj = new HashSet<GameTile>();
        tstyle = null;
    }

    // for encoding to json
    public Map<String, String> encodeSpawn() {
        Map<String, String> encoding = new HashMap<>();
        encoding.put("terrain", this.terrain);
        encoding.put("style", this.style);
        if (this.ttstyle != null)
            encoding.put("ttstyle", this.ttstyle.userid);
        else
            encoding.put("ttstyle", null);
        encoding.put("resource", this.resource);

        if (this.village != null) {
            if (this.village.capital) {
                encoding.put("village", this.village.team.userid);
                encoding.put("city name", this.village.name);
            } else {
                encoding.put("village", "123");
            }
        } else {
            encoding.put("village", null);
        }
        return encoding;
    }

    public void addEdge(GameTile gt, Edge e) {
        if (gt != null)
            adj.add(gt);
        if (!outline.contains(e)) {
            outline.add(e);
        }
    }

    public void sortEdges() {
        List<Edge> ltemp = new LinkedList<>(outline);
        int nnn = hitbox.npoints;
        for (int i = 0; i < nnn; i++) {
            for (Edge ee : ltemp) {
                if (ee.similar(
                        new Point(hitbox.xpoints[i], hitbox.ypoints[i]),
                        new Point(hitbox.xpoints[i % nnn], hitbox.ypoints[i % nnn])
                )) {
                    outline.add(i, ee);
                    break;
                }
            }
        }
        for (int i = 0; i < nnn; i++) {
            outline.get(i).adj.add(outline.get((i + 1) % nnn));
            outline.get(i).adj.add(outline.get((i - 1 + nnn) % nnn));
        }
    }

    // hitbox interactions
    public String objtype() {
        return style;
    }

    public Polygon hitbox() {
        return hitbox;
    }

    public void select(boolean b) {
        if (b) {
            if (!VISION.contains(this)) { // cannot see
                selected = true;
                tileorunit = true;
                return;
            }
            if (!selected) {
                tileorunit = true;
            }
            if (tileorunit && unit != null && unit.team == USER) {
                tileorunit = false;
                unit.setMoves();
            } else if (!tileorunit) {
                tileorunit = true;
            }

        } else {
            tileorunit = true;
        }
        selected = b;
    }

    // unit interactions
    public void replaceUnit(Unit u) {
        unit = u;
    }

    public double cost(GameTile gt, Unit u) { // returns movement cost of moving from this tile onto
                                              // gt for unit u
        if (u == null)
            return -1;
        Stat s = u.stats();
        if (s == null)
            return 1; // catch null stat unit
        if (u.team == USER && !VISION.contains(gt))
            return -1; // cannot see tile

        if (u.stats().sail && !gt.terrain.equals("water") && !gt.terrain.equals("ocean")) { // landing
                                                                                            // ships
                                                                                            // onto
            // land
            if(gt.terrain.equals("mountains") && !u.team.getTech().get("climbing")){
                return -1;
            }
            return 0;
        }
        double m = 1;
        if (gt.roads && roads && (gt.team == u.team || gt.team == null)
                && (team == u.team || team == null)) { // roads connect WIP TO
            // CHANGE DETECT IF TILE IS
            // ENEMY OR NEUTRAL/FRIENDLY
            // ROADS
            m = 0.5;
        }
        if (gt.unit != null) { // you cant move onto other units
            return -1;
        }
        for (GameTile a : gt.adj) { // enemy units impede movement on adjacent nodes
            if (a.unit != null && a.unit.team != u.team)
                m = 0;
        }
        // units that cannot sail
        if (gt.terrain.equals("water") || gt.terrain.equals("ocean")) {
            if (!u.team.getTech().get("sailing")) {
                return -1;
            } else if (!u.team.getTech().get("navigation") && gt.terrain.equals("ocean")) {
                return -1;
            }
            if (s.sail) {
                return m;
            }
            if (gt.building != null && gt.building.name.equals("port")) {
                return 0;
            } else {
                return -1; // cannot move here
            }
        }
        // units that can naturally move > 1 tiles have hampered movement on mountains
        // and forests
        if (gt.terrain.equals("mountains")) {
            if (!u.team.getTech().get("climbing"))
                return -1;
            if (s.move > 1) {
                return 0;
            } else {
                return m;
            }
        }

        if (gt.terrain.equals("forest")) {
            if (s.move > 1 && m >= 1) { // no movement bonus
                return 0;
            } else {
                return m;
            }
        }
        return m;
    }

    /*
     * Does a BFS search at the tile with the unit on the tile
     * coston: toggles cost function for movement
     * based: base distance to search
     * mark: marking for BFSed tiles
     * f: tile passes condition f to be marked
     */
    public Set<GameTile> BFS(
            boolean coston, double based, int mark, Function<GameTile, Boolean> f
    ) {
        Queue<Pair<GameTile, Double>> q = new ArrayDeque<>();
        q.add(new Pair(this, based));
        Set<GameTile> ret = new HashSet<>();
        while (!q.isEmpty()) {
            Pair<GameTile, Double> pr = q.remove();
            GameTile gt = pr.first;
            if (f.apply(gt))
                gt.targetselector = mark;
            ret.add(gt);
            double dist = pr.second;
            for (GameTile neigh : gt.adj) {
                double c = 1;
                if (coston)
                    c = gt.cost(neigh, unit);
                if (neigh.targetselector != mark && dist > 0) {
                    if (c == -1) {
                        // cannot move onto this tile
                    } else if (c == 0) { // use remaining stamina
                        q.add(new Pair(neigh, 0.0));
                    } else { // normal movement
                        q.add(new Pair(neigh, dist - c));
                    }
                }
            }
        }
        ret.remove(this); // removes source from moveset
        return ret;
    }

    /*
     * Does a BFS search at the tile, but now it returns the set of visited
     * gametiles given a no-cost search
     */

    public Set<GameTile> BFS2(int based) {
        Queue<Pair<GameTile, Integer>> q = new ArrayDeque<>();
        q.add(new Pair(this, based));
        Set<GameTile> ret = new HashSet<>();
        ret.add(this);
        while (!q.isEmpty()) {
            Pair<GameTile, Integer> pp = q.remove();
            GameTile gt = pp.first;
            if (pp.second > 0) {
                for (GameTile a : gt.adj) {
                    if (!ret.contains(a)) {
                        ret.add(a);
                        q.add(new Pair(a, pp.second - 1));
                    }
                }
            }
        }
        return ret;
    }

    /*
     * Checks for actions on this tile
     * t: player tribe selecting tile
     */
    public List<String> getActions(Tribe t) {
        List<String> ret = new LinkedList<>();
        if (t == null)
            return ret;
        if (!VISION.contains(this))
            return ret; // cannot do actions on tiles that cannot be seen
        if (unit != null && unit.team != USER)
            return ret; // cannot do actions with enemy occupation

        if (village != null && village.team != USER && unit != null && unit.team == USER
                && unit.movephase == 0) {
            ret.add("capture");
        }
        // not your tile dingus
        Map<String, Boolean> ttree = t.getTech();
        if (t != team) {
            if(unit!=null && unit.team == t && unit.movephase == 0){
                ret.add("heal");
            }
            if ((terrain.equals("land")||terrain.equals("forest") )&& !roads && village==null && ttree.get("roads") && team == null) {
                ret.add("roads");
            }
            return ret;
        }
        // tile selected

        if (tileorunit) {
            // resource actions
            if (resource == null) {

            } else if (resource.equals("animals") && ttree.get("hunting")) {
                ret.add("hunting");
            } else if (resource.equals("fruit") && ttree.get("organization")) {
                ret.add("harvest");
            } else if (resource.equals("fish") && ttree.get("fishing")) {
                ret.add("fish");
            } else if (resource.equals("whale") && ttree.get("whaling")) {
                ret.add("whale");
            } else if (resource.equals("crop") && ttree.get("farming")) {
                ret.add("farm");
            } else if (resource.equals("ore") && ttree.get("mining")) {
                ret.add("mine");
            }
            // special actions
            if (building == null) {
                if (village != null && village.team == USER && unit == null) { // unoccupied your
                    // city
                    if (village.units.size() >= village.unitcap) {
                        return ret;
                    }

                    ret.add("warrior");
                    if (ttree.get("archery")) {
                        ret.add("archer");
                    }
                    if (ttree.get("mathematics")) {
                        ret.add("catapult");
                    }
                    if (ttree.get("riding")) {
                        ret.add("rider");
                    }
                    if (ttree.get("chivalry")) {
                        ret.add("knight");
                    }
                    if (ttree.get("shields")) {
                        ret.add("defender");
                    }
                    if (ttree.get("smithery")) {
                        ret.add("swordsman");
                    }
                    if (ttree.get("philosophy")) {
                        ret.add("mind bender");
                    }
                }
                if(village==null){
                     if (terrain.equals("land")) {
                        if (ttree.get("construction")) {
                            for (GameTile gt : adj) {
                                if (gt.team == team && gt.building != null
                                        && gt.building.name.equals("farm") && !city.windmill) {
                                    ret.add("windmill");
                                    break;
                                }
                            }
                        }
                        if (ttree.get("smithery")) {
                            for (GameTile gt : adj) {
                                if (gt.team == team && gt.building != null
                                        && gt.building.name.equals("mine") && !city.forge) {
                                    ret.add("forge");
                                    break;
                                }
                            }
                        }
                        if (ttree.get("mathematics")) {
                            for (GameTile gt : adj) {
                                if ((gt.team == team) && gt.building != null
                                        && gt.building.name.equals("lumber hut") && !city.sawmill) {
                                    ret.add("sawmill");
                                    break;
                                }
                            }
                        }
                        if (ttree.get("trade")) {
                            for (GameTile gt : adj) {
                                if (gt.team == team && gt.building != null
                                        && gt.building.name.equals("port") && !city.customhouse) {
                                    ret.add("customs house");
                                    break;
                                }
                            }
                        }
                        if (ttree.get("free spirit")) {
                            ret.add("temple");
                        }
                        if (ttree.get("spiritualism") && resource == null) {
                            ret.add("grow forest");
                        }
                        if (ttree.get("engineering")) {
                            ret.add("guard tower");
                        }
                    } else if (terrain.equals("forest")) {
                        if (ttree.get("forestry")) {
                            ret.add("lumber hut");
                        }
                        if (ttree.get("free spirit")) {
                            ret.add("clear forest");
                        }
                        if (ttree.get("chivalry")) {
                            ret.add("burn forest");
                        }
                        if (ttree.get("spiritualism")) {
                            ret.add("forest temple");
                        }
                    } else if (terrain.equals("mountains")) {
                        if (ttree.get("meditation")) {
                            ret.add("mountain temple");
                        }
                        if (ttree.get("engineering")) {
                            ret.add("guard tower");
                        }
                    } else if (terrain.equals("water")) {
                        if (ttree.get("sailing")) {
                            ret.add("port");
                        }
                        if (ttree.get("aquatism")) {
                            ret.add("water temple");
                        }
                    } else if (terrain.equals("ocean")) {
                        if (ttree.get("aquatism")) {
                            ret.add("water temple");
                        }
                    }
                     }
            } else {
                if (ttree.get("construction")) {
                    ret.add("destroy");
                }
            }
            if(village==null && terrain.equals("land") || terrain.equals("forest")){
                if(ttree.get("roads") && !roads){
                    ret.add("roads");
                }
            }
        }
        // unit selected
        else if (unit != null) {
            if (unit.movephase == 0) {
                if (ttree.get("free spirit")) {
                    ret.add("retire");
                }
                if (!unit.veteran && unit.kills >= 3 && unit.stats().type.toLowerCase() != "giant"
                        &&
                        unit.stats().type.toLowerCase() != "battleship" &&
                        unit.stats().type.toLowerCase() != "ship" &&
                        unit.stats().type.toLowerCase() != "boat") {
                    // ret.add("promote"); disabled for now :(
                }
                if (unit.stats().hp() < unit.stats().maxHP && unit.movephase == 0) {
                    ret.add("heal");
                }
            }
        }
        return ret;
    }

    // draw 1: base layer for drawing
    public void draw(Graphics g, ImageLoader il) {
        if (style.equals("hex")) {
            Polygon hbox = hitbox;

            if (!VISION.contains(this)) {
                // g.setColor(FOG);// 224, 201, 75));
                g.drawImage(
                        il.fog, center.x - 33, (int) (center.y - SHEAR * 26 - 2), 65,
                        (int) (52 * SHEAR + 5), null
                );
                // g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
                return;
            }
            if (village != null) {
                g.setColor(ttstyle.landc);// 224, 201, 75));
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            } else if (terrain.equals("water")) {
                g.setColor(new Color(105, 203, 245));
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            } else if (terrain.equals("ocean")) {
                g.setColor(new Color(75, 147, 255));
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            } else if (terrain.equals("land")) {
                g.setColor(ttstyle.landc);
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            } else if (terrain.equals("forest")) {
                g.setColor(ttstyle.landc);
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            } else if (terrain.equals("mountains")) {
                g.setColor(ttstyle.mountainc);
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            }

            // draws in roads
            g.setColor(new Color(143, 111, 52, 223));
            if (roads || (village != null && village.team != null)) {
                for (GameTile a : adj) {
                    if (a.roads || (a.village != null && a.village.team != null)) {
                        ((Graphics2D) g).setStroke(new BasicStroke(4));
                        g.drawLine(center.x, center.y, a.center.x, a.center.y);
                    }
                }
            }
            // draws the border around cells
            for (Edge e : outline) {
                e.draw(g);
            }

        }
    }

    // draw 2: top layer for drawing
    public void draw2(Graphics g, ImageLoader il) {
        // draws additional terrain sprites
        if (!VISION.contains(this))
            return;

        Map<String, Boolean> ttree = USER.getTech();

        // TO CHANGE: ADD RESOURCE VISIBILITY FOR CLIENT
        if (terrain.equals("mountains")) {
            g.drawImage(
                    il.terrain(tstyle, "mountains"),
                    center.x - 28, center.y - 30, 56, 50, null
            );
        } else if (terrain.equals("forest")) {
            g.drawImage(
                    il.terrain(tstyle, "forest"),
                    center.x - 23, center.y - 18, 46, 34, null
            );
        }
        // draws resource sprites on terrain sprite
        if (resource == null) {

        } else if (resource.equals("animals")) {
            int hhh = 15 * il.terrain(tstyle, "animals").getHeight(null)
                    / il.terrain(tstyle, "animals").getWidth(null);
            g.drawImage(
                    il.terrain(tstyle, "animals"),
                    center.x - 7, center.y - hhh / 2, 15, hhh, null
            );
        } else if ((ttree.get("farming") || ttree.get("organization")) && resource.equals("crop")) {
            g.drawImage(
                    il.terrain(tstyle, "crop"),
                    center.x - 20, center.y - 19, 40, 40, null
            );
        } else if (resource.equals("fish")) {
            g.drawImage(
                    il.terrain(tstyle, "fish"),
                    center.x - 28, center.y - 30, 56, 50, null
            );
        } else if (resource.equals("fruit")) {
            int hhh = 25 * il.terrain(tstyle, "fruit").getHeight(null)
                    / il.terrain(tstyle, "fruit").getWidth(null);
            g.drawImage(
                    il.terrain(tstyle, "fruit"),
                    center.x - 25 / 2, center.y - hhh / 2, 25, hhh, null
            );
        } else if (ttree.get("climbing") && resource.equals("ore")) {
            g.drawImage(
                    il.terrain(tstyle, "ore"),
                    center.x - 32, center.y - 30, 60, 50, null
            );
        } else if (ttree.get("fishing") && resource.equals("whale")) {
            g.drawImage(
                    il.terrain(tstyle, "whale"),
                    center.x - 28, center.y - 27, 56, 50, null
            );
        }
        // draws the city, if one exists
        if (village != null) {
            village.draw(g, il);
        } else if (building != null) {
            Image bi;
            if (building.name.equals("guard tower")) {
                bi = il.unit(team.tribe, "guard tower");
            } else {
                bi = il.building(building.name, building.level);
            }
            int hhh = 35 * bi.getHeight(null) / bi.getWidth(null);
            g.drawImage(bi, center.x - 35 / 2, center.y - hhh / 2, 35, hhh, null);
        }

        // draws target selectors
        if (targetselector == 1) {
            g.drawImage(
                    il.bullseye,
                    center.x - 20, center.y - (int) (20 * SHEAR), 40, (int) (40 * SHEAR), null
            );
        } else if (targetselector == 2) {
            g.drawImage(
                    il.bullseye2,
                    center.x - 20, center.y - (int) (20 * SHEAR), 40, (int) (40 * SHEAR), null
            );
        }
    }

    // draw 3: set location drawing
    public void draw3(Graphics g, ImageLoader il, Point p) {
        if (style.equals("hex")) {
            Polygon hbox = hitbox;
            for (int i = 0; i < 6; i++) {
                hbox.xpoints[i] -= center.x - p.x;
                hbox.ypoints[i] -= center.y - p.y;
            }
            // no vision, FOGGGG
            if (!VISION.contains(this)) {
                // g.setColor(FOG);
                // g.fillPolygon(hbox);
                g.drawImage(
                        il.fog, p.x - 33, (int) (p.y - SHEAR * 26 - 2), 65, (int) (52 * SHEAR + 5),
                        null
                );
                for (int i = 0; i < 6; i++) {
                    hbox.xpoints[i] += center.x - p.x;
                    hbox.ypoints[i] += center.y - p.y;
                }
                return;
            }

            if (village != null) {
                g.setColor(ttstyle.landc);
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            } else if (terrain.equals("water")) {
                g.setColor(new Color(105, 203, 245));
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            } else if (terrain.equals("ocean")) {
                g.setColor(new Color(75, 147, 255));
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            } else if (terrain.equals("land")) {
                g.setColor(ttstyle.landc);
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            } else if (terrain.equals("forest")) {
                g.setColor(ttstyle.landc);
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            } else if (terrain.equals("mountains")) {
                g.setColor(ttstyle.mountainc);
                g.fillPolygon(hbox.xpoints, hbox.ypoints, 6);
            }

            ((Graphics2D) g).setStroke(new BasicStroke(2));
            g.setColor(new Color(100, 140, 255));
            g.drawPolygon(hbox);
            ((Graphics2D) g).setStroke(new BasicStroke(1));

            Map<String, Boolean> ttree = USER.getTech();

            // draws additional terrain sprites
            if (terrain.equals("mountains")) {
                g.drawImage(
                        il.terrain(tstyle, "mountains"),
                        p.x - 28, p.y - 30, 56, 50, null
                );
            } else if (terrain.equals("forest")) {
                g.drawImage(
                        il.terrain(tstyle, "forest"),
                        p.x - 23, p.y - 18, 46, 34, null
                );
            }

            if (tileorunit) {
                // draws resource sprites on terrain sprite
                if (resource == null) {

                } else if (resource.equals("animals")) {
                    int hhh = 15 * il.terrain(tstyle, "animals").getHeight(null)
                            / il.terrain(tstyle, "animals").getWidth(null);
                    g.drawImage(
                            il.terrain(tstyle, "animals"),
                            p.x - 7, p.y - hhh / 2, 15, hhh, null
                    );
                } else if ((ttree.get("farming") || ttree.get("organization"))
                        && resource.equals("crop")) {
                    g.drawImage(
                            il.terrain(tstyle, "crop"),
                            p.x - 20, p.y - 19, 40, 40, null
                    );
                } else if (resource.equals("fish")) {
                    g.drawImage(
                            il.terrain(tstyle, "fish"),
                            p.x - 28, p.y - 30, 56, 50, null
                    );
                } else if (resource.equals("fruit")) {
                    int hhh = 25 * il.terrain(tstyle, "fruit").getHeight(null)
                            / il.terrain(tstyle, "fruit").getWidth(null);
                    g.drawImage(
                            il.terrain(tstyle, "fruit"),
                            p.x - 25 / 2, p.y - hhh / 2, 25, hhh, null
                    );
                } else if (ttree.get("climbing") && resource.equals("ore")) {
                    g.drawImage(
                            il.terrain(tstyle, "ore"),
                            p.x - 25, p.y - 30, 60, 50, null
                    );
                } else if (ttree.get("fishing") && resource.equals("whale")) {
                    g.drawImage(
                            il.terrain(tstyle, "whale"),
                            p.x - 28, p.y - 27, 56, 50, null
                    );
                }
                // draws the city, if one exists
                if (village != null) {
                    village.draw3(g, il, p);
                } else if (building != null) {
                    Image bi;
                    if (building.name.equals("guard tower")) {
                        bi = il.unit(team.tribe, "guard tower");
                    } else {
                        bi = il.building(building.name, building.level);
                    }
                    int hhh = 35 * bi.getHeight(null) / bi.getWidth(null);
                    g.drawImage(bi, p.x - 35 / 2, p.y - hhh / 2, 35, hhh, null);
                }
            } else {
                // draws the unit, if one exists
                // if(unit!=null){
                // unit.draw(g, il, p);
                // }
            }

            for (int i = 0; i < 6; i++) {
                hbox.xpoints[i] += center.x - p.x;
                hbox.ypoints[i] += center.y - p.y;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameTile))
            return false;
        return id.equals(((GameTile) obj).id);
    }
}
