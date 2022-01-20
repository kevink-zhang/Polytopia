package org.cis120.polytopia.Tile;

import org.cis120.polytopia.Pair;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class GameMap {
    public Map<Pair<Integer, Integer>, GameTile> coods;
    public List<Tribe> teams;
    public Set<GameTile> inner = new HashSet<>();
    public Set<GameTile> outer = new HashSet<>();
    public Map<String, Double> innerrates = new HashMap<>();
    public Map<String, Double> outerrates = new HashMap<>();

    public final double SHEAR = 0.84; // squishing of y axis for 2.5D look
    public final int radius = 30;

    public GameMap(List<Tribe> t) {
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("files/gamedata/tribedata/resource spawn.txt")
            );
            for (int i = 0; i < 7; i++) {
                String sss = br.readLine();
                innerrates.put(
                        sss.split(" ")[0].toLowerCase(), Double.parseDouble(sss.split(" ")[1])
                );
                outerrates.put(
                        sss.split(" ")[0].toLowerCase(), Double.parseDouble(sss.split(" ")[2])
                );
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        teams = t;
    }

    // initializing from a fixed tile terrain
    public void fromEncoding(Map<Object, Object> tl) {
        hex_init(tl.size() - 1);
        for (Object kkey : tl.keySet()) {

            Pair<Integer, Integer> key = new Pair().fromString(kkey);

            Map<String, String> info = (Map<String, String>) tl.get(kkey);
            if (coods.containsKey(key)) {
                GameTile gt = coods.get(key);
                gt.terrain = info.get("terrain");
                gt.resource = info.get("resource");
                gt.style = info.get("style");
                if (info.get("ttstyle") == null) {
                    gt.ttstyle = null;
                } else {
                    for (Tribe t : teams) {
                        if (t.userid.equals(info.get("ttstyle"))) {
                            gt.ttstyle = t;
                            gt.tstyle = t.tribe;
                            break;
                        }
                    }
                }
                if (info.get("village") == null) {

                } else {
                    if (info.get("village").equals("123")) {
                        gt.village = new City(gt, null, false);
                    } else {
                        for (Tribe t : teams) {
                            if (t.userid.equals(info.get("village"))) {
                                gt.village = new City(gt, t, true);
                                gt.village.name = info.get("city name");
                                break;
                            }
                        }
                    }
                }
            } else {
                System.out.println("encoding does not match");
            }
        }
        for (GameTile gt : coods.values()) {
            if (gt.village != null && gt.village.capital) {
                gt.village.expand();
            }
        }
    }

    public Map<Object, Object> toStringMap() {
        Map<Object, Object> ret = new HashMap<>();
        for (Object key : coods.keySet()) {
            ret.put(key.toString(), coods.get(key).encodeSpawn());
        }
        return ret;
    }

    public void hex_init(int tileCount) {
        // T = List<Integer>
        int n = 1;
        int sum = 0;
        while (sum < tileCount) {
            n++;
            sum = n * n / 2 + (n - 1) * (n / 2 + 1);
        }

        // generates points for polygons

        // creates a coordinate plane for game tile neighbor linking
        coods = new HashMap<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n / 2; j++) {
                Pair<Integer, Integer> foo = new Pair(j, 2 * i);

                // System.out.println("adding "+foo);
                Polygon p = new Polygon();
                p.addPoint(hex_vertex(2 + 4 * (j), i).x, hex_vertex(2 + 4 * (j), i).y);
                p.addPoint(hex_vertex(3 + 4 * (j), i).x, hex_vertex(3 + 4 * (j), i).y);
                p.addPoint(hex_vertex(4 + 4 * (j), i).x, hex_vertex(4 + 4 * (j), i).y);
                p.addPoint(hex_vertex(5 + 4 * (j), i).x, hex_vertex(5 + 4 * (j), i).y);
                p.addPoint(hex_vertex(4 + 4 * (j), i + 1).x, hex_vertex(4 + 4 * (j), i + 1).y);
                p.addPoint(hex_vertex(3 + 4 * (j), i + 1).x, hex_vertex(3 + 4 * (j), i + 1).y);
                coods.put(foo, new GameTile(foo, "hex", p));
            }
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n / 2 + 1; j++) {
                Pair<Integer, Integer> foo = new Pair(j, 2 * i + 1);
                // System.out.println("adding "+foo);
                Polygon p = new Polygon();
                p.addPoint(hex_vertex(0 + 4 * (j), i + 1).x, hex_vertex(0 + 4 * (j), i + 1).y);
                p.addPoint(hex_vertex(1 + 4 * (j), i).x, hex_vertex(1 + 4 * (j), i).y);
                p.addPoint(hex_vertex(2 + 4 * (j), i).x, hex_vertex(2 + 4 * (j), i).y);
                p.addPoint(hex_vertex(3 + 4 * (j), i + 1).x, hex_vertex(3 + 4 * (j), i + 1).y);
                p.addPoint(hex_vertex(2 + 4 * (j), i + 1).x, hex_vertex(2 + 4 * (j), i + 1).y);
                p.addPoint(hex_vertex(1 + 4 * (j), i + 1).x, hex_vertex(1 + 4 * (j), i + 1).y);
                coods.put(foo, new GameTile(foo, "hex", p));
            }
        }

        // links tiles
        int[][] deltas = { { 0, -1 }, { 1, -1 }, { 0, 1 }, { 1, 1 }, { 0, -2 }, { 0, 2 } };
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n / 2; j++) {
                int x = j;
                int y = 2 * i;
                Pair<Integer, Integer> opos = new Pair(x, y);
                for (int[] d : deltas) {
                    int nx = x + d[0];
                    int ny = y + d[1];
                    Pair<Integer, Integer> npos = new Pair(nx, ny);

                    if (valid_cood(npos) && valid_cood(opos)) {
                        // System.out.println("connecting "+ opos + "with" + npos);
                        GameTile g1 = coods.get(opos);
                        GameTile g2 = coods.get(npos);

                        Point ap = null;
                        Point bp = null;

                        for (int a = 0; a < 6; a++) {
                            for (int b = 0; b < 6; b++) {
                                if (Point.distance(
                                        g1.hitbox().xpoints[a], g1.hitbox().ypoints[a],
                                        g2.hitbox().xpoints[b], g2.hitbox().ypoints[b]
                                ) < 5) {
                                    if (ap == null) {
                                        ap = new Point(
                                                g1.hitbox().xpoints[a], g1.hitbox().ypoints[a]
                                        );
                                    } else {
                                        bp = new Point(
                                                g1.hitbox().xpoints[a], g1.hitbox().ypoints[a]
                                        );
                                    }
                                }
                            }
                        }
                        Edge eee = new Edge(ap, bp, g1, g2);
                        if (!g1.adj.contains(g2)) {
                            g1.addEdge(g2, eee);
                        }
                        if (!g2.adj.contains(g1)) {
                            g2.addEdge(g1, eee);
                        }
                    }
                }
            }
        }

        int[][] deltas2 = { { -1, -1 }, { 0, -1 }, { -1, 1 }, { 0, 1 }, { 0, -2 }, { 0, 2 } };
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n / 2 + 1; j++) {
                int x = j;
                int y = 2 * i + 1;
                Pair<Integer, Integer> opos = new Pair(x, y);
                for (int[] d : deltas2) {
                    int nx = x + d[0];
                    int ny = y + d[1];
                    Pair<Integer, Integer> npos = new Pair(nx, ny);

                    if (valid_cood(npos) && valid_cood(opos)) {
                        // System.out.println("connecting "+ opos + "with" + npos);
                        GameTile g1 = coods.get(opos);
                        GameTile g2 = coods.get(npos);

                        Point ap = null;
                        Point bp = null;

                        for (int a = 0; a < 6; a++) {
                            for (int b = 0; b < 6; b++) {
                                if (Point.distance(
                                        g1.hitbox().xpoints[a], g1.hitbox().ypoints[a],
                                        g2.hitbox().xpoints[b], g2.hitbox().ypoints[b]
                                ) < 5) {
                                    if (ap == null) {
                                        ap = new Point(
                                                g1.hitbox().xpoints[a], g1.hitbox().ypoints[a]
                                        );
                                    } else {
                                        bp = new Point(
                                                g1.hitbox().xpoints[a], g1.hitbox().ypoints[a]
                                        );
                                    }
                                }
                            }
                        }
                        Edge eee = new Edge(ap, bp, g1, g2);
                        if (!g1.adj.contains(g2)) {
                            g1.addEdge(g2, eee);
                        }
                        if (!g2.adj.contains(g1)) {
                            g2.addEdge(g1, eee);
                        }
                    }
                }
            }
        }

        // adds edges onto the border nodes
        for (GameTile gt : coods.values()) {
            for (int i = 0; i < 6; i++) {
                boolean fed = false;
                Point a = new Point(gt.hitbox().xpoints[i], gt.hitbox().ypoints[i]);
                Point b = new Point(
                        gt.hitbox().xpoints[(i + 1) % 6], gt.hitbox().ypoints[(i + 1) % 6]
                );
                for (Edge e : gt.outline) {
                    if (e.similar(a, b)) {
                        fed = true;
                        break;
                    }
                }
                if (!fed) {
                    gt.addEdge(null, new Edge(a, b, gt, null));
                }
            }
        }

        for (GameTile gt : coods.values()) {
            gt.sortEdges();
        }
    }

    // generates terrain
    public void hex_terrain() {
        // Perlin noise generator
        Map<GameTile, Double> pn1 = Perlin(coods.values());
        Map<GameTile, Double> pn2 = Perlin(coods.values());

        // averages neighbor perlin value parity to determine water or land
        double wthresh = -0.02;
        for (GameTile gt : coods.values()) {
            double w = -1;
            if (pn1.get(gt) > wthresh) {
                w = 1;
            }
            for (GameTile a : gt.adj) {
                if (pn1.get(a) > wthresh) {
                    w += 1;
                } else {
                    w -= 1;
                }
            }
            w /= gt.adj.size() + 1;
            if (w < 0) {
                gt.terrain = "water";
            } else {
                gt.terrain = "land";
            }
        }
        // removes water pockets
        Set<GameTile> tofix = new HashSet<>();
        for (GameTile gt : coods.values()) {
            if (gt.terrain == "water") {
                int adjwater = 0;
                for (GameTile a : gt.adj) {
                    if (a.terrain == "water") {
                        adjwater++;
                    }
                }
                if (adjwater < 2) {
                    tofix.add(gt);
                }
            }
        }
        for (GameTile gt : tofix) {
            gt.terrain = "land";
        }

        // //overlays second Perlin noise to create mountains, with smaller threshhold
        // for(GameTile gt: coods.values()){
        // double w = pn2.get(gt);
        // //System.out.println(pn2.get(gt));
        // for(GameTile a: gt.adj){
        // w+=pn2.get(a);
        // }
        // w/= gt.adj.size()+1;
        // if(w>0.05 && gt.terrain == "land"){
        // gt.terrain = "mountains";
        // }
        // }
        //
        // //removes mountain clumping
        // for(GameTile gt: coods.values()){
        // if(gt.terrain == "mountains"){
        // int mcount = 0;
        // for(GameTile a: gt.adj){
        // if(a.terrain == "mountains"){
        // mcount++;
        // }
        // }
        // if(mcount >= 4) gt.terrain = "land";
        // }
        // }

        // checks for ocean tiles if all adjacent tiles are water or ocean too
        for (GameTile gt : coods.values()) {
            if (gt.terrain == "water") {
                boolean surrounded = true;
                for (GameTile a : gt.adj) {
                    if (a.terrain != "water" && a.terrain != "ocean") {
                        surrounded = false;
                        break;
                    }
                }
                if (surrounded) {
                    gt.terrain = "ocean";
                }
            }
        }

        // puts in villages
        int maxtries = 20;
        for (int failsafe = 0; failsafe < maxtries; failsafe++) {
            Set<GameTile> cities = new HashSet<>();
            int citycount = coods.values().size() / 10;
            List<GameTile> gtlist = new LinkedList<>(coods.values());

            // generates city locations
            while (citycount > 0 && gtlist.size() > 0) {
                Collections.shuffle(gtlist);
                GameTile gt = gtlist.get(0);
                if (gt.terrain == "land" &&
                        gt.adj.size() == 6 &&
                        !search(
                                (GameTile gtt) -> gtt.village != null, (GameTile gtt) -> true, gt, 2
                        )) {
                    gt.village = new City(gt, null);
                    cities.add(gt);
                    citycount--;
                }
                gtlist.remove(gt);
            }

            List<GameTile> validc = new LinkedList<>();
            for (GameTile c : cities) {
                if (search(
                        (GameTile gtt) -> gtt.village != null,
                        (GameTile gtt) -> gtt.terrain == "land", c, 3
                )) {
                    validc.add(c);
                }
            }

            List<GameTile> thechosenones = new LinkedList<>();
            Point p1 = new Point(0, 0);
            Point p2 = new Point(0, 0);
            for (GameTile gt : coods.values()) {
                Point p = gt.center;
                if (p.x + p.y < p1.x + p1.y) {
                    p1 = p;
                }
                if (p.x + p.y > p2.x + p2.y) {
                    p2 = p;
                }
            }
            int radrad = (p2.x - p1.x + p2.y - p1.y) / 2;
            int angle = 45;
            int area = coods.values().size() / teams.size() + 1;
            int tooclose = (int) (Math.sqrt(area) * 20);
            boolean valid = true;
            for (Tribe ttt : teams) {
                double mind = Integer.MAX_VALUE;
                Point focus = new Point(
                        (p1.x + p2.x) / 2 + (int) (radrad * Math.sin(Math.toRadians(angle))),
                        (p1.y + p2.y) / 2 + (int) (radrad * Math.cos(Math.toRadians(angle)))
                );
                GameTile tar = null;
                for (GameTile gt : validc) {
                    boolean socialdist = true;
                    for (GameTile oth : thechosenones) {
                        if (oth.center.distance(gt.center) < tooclose) {
                            socialdist = false;
                            break;
                        }
                    }
                    if (socialdist && !thechosenones.contains(gt)
                            && gt.center.distance(focus) < mind) {
                        mind = gt.center.distance(focus);
                        tar = gt;
                    }
                }
                if (tar == null) {
                    valid = false;
                    break;
                } else {
                    tar.village = new City(tar, ttt, true);
                    thechosenones.add(tar);
                }
                angle += 360 / teams.size();
            }

            if (!valid) { // resets
                for (GameTile gt : coods.values()) {
                    gt.village = null;
                }
                System.out.println("failed city spawning");
            } else {
                fill_map(teams, thechosenones);
                divide_map();
                System.out.println(thechosenones.size() + " cities generated");
                break;
            }
        }

        for (GameTile gt : coods.values()) {
            if (gt.village != null && gt.village.capital) {
                gt.village.expand();
                // balancing changes for each tribe
            }
        }

        // filler just in case empty tiles occur
        Tribe filler = new Tribe("Imperius", "filler", "filler", Color.BLACK);
        for (GameTile gt : coods.values()) {
            if (gt.tstyle == null) {
                System.out.println("Terrain empty tribe?");
                gt.tstyle = "Imperius";
                gt.ttstyle = filler;
            }
        }
        System.out.println("Terrain generated!");

        for (GameTile gt : coods.values()) {
            Tribe t = gt.ttstyle;
            double d = Math.random();
            double fival = 1;
            double frval = 0;
            double foval = 0;
            double crval = 0;
            double anval = 0;
            double moval = 0;
            double meval = 0;
            double fishv = 0.5 * t.spawnmods.get("fish");
            double whalev = 0.33;
            if (inner.contains(gt)) {
                fival = innerrates.get("field");
                frval = innerrates.get("fruit") / innerrates.get("field")
                        * t.spawnmods.get("fruit");
                crval = innerrates.get("crop") / innerrates.get("field") * t.spawnmods.get("crop");
                foval = innerrates.get("forest") * t.spawnmods.get("forest");
                anval = innerrates.get("animal") / innerrates.get("forest")
                        * t.spawnmods.get("animal");
                moval = innerrates.get("mountain") * t.spawnmods.get("mountains");
                meval = innerrates.get("metal") / innerrates.get("mountain")
                        * t.spawnmods.get("ore");
            }
            if (outer.contains(gt)) {
                fival = outerrates.get("field");
                frval = outerrates.get("fruit") / outerrates.get("field")
                        * t.spawnmods.get("fruit");
                crval = outerrates.get("crop") / outerrates.get("field") * t.spawnmods.get("crop");
                foval = outerrates.get("forest") * t.spawnmods.get("forest");
                anval = outerrates.get("animal") / outerrates.get("forest")
                        * t.spawnmods.get("animal");
                moval = outerrates.get("mountain") * t.spawnmods.get("mountains");
                meval = outerrates.get("metal") / outerrates.get("mountain")
                        * t.spawnmods.get("ore");
            }
            if (gt.village != null) {
                // do nothing its a village lol
                gt.terrain = "land";
                gt.resource = null;
                gt.building = null;
            } else if (gt.terrain == "land") {
                double sumt1 = fival + moval + foval;

                fival = fival / sumt1;
                moval = moval / sumt1;
                foval = foval / sumt1;
                if (d < moval) { // mountains
                    gt.terrain = "mountains";
                    if (Math.random() < meval) {
                        gt.resource = "ore";
                    }
                } else if (d < moval + foval) { // forests
                    gt.terrain = "forest";
                    if (Math.random() < anval) {
                        gt.resource = "animals";
                    }
                } else { // other
                    gt.terrain = "land";

                    double dd = Math.random();
                    if (dd < frval) {
                        gt.resource = "fruit";
                    } else if (dd < frval + crval) {
                        gt.resource = "crop";
                    }
                }
            } else if (gt.terrain == "water") {
                if (d < fishv) {
                    gt.resource = "fish";
                }
            } else if (gt.terrain == "ocean") {
                if (d < whalev) {
                    gt.resource = "whale";
                }
            }
        }
        // balancing guarantee spawns for each tribe
        for (GameTile gt : coods.values()) {
            if (gt.village != null && gt.village.capital) {
                if (gt.village.team.tribe.equals("Ai-Mo")) {
                    int rsccount = 0;
                    for (GameTile a : gt.adj) {
                        if (a.terrain.equals("mountains"))
                            rsccount++;
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 3)
                            break;
                        if (a.terrain.equals("land")) {
                            a.terrain = "mountains";
                            a.resource = null;
                            rsccount++;
                        }
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 3)
                            break;
                        if (!a.terrain.equals("mountains")) {
                            a.terrain = "mountains";
                            a.resource = null;
                            rsccount++;
                        }
                    }
                } else if (gt.village.team.tribe.equals("Bardur")) {
                    int rsccount = 0;
                    for (GameTile a : gt.adj) {
                        if (a.resource != null && a.resource.equals("animals"))
                            rsccount++;
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 2)
                            break;
                        if ((a.resource == null || !a.resource.equals("animals"))
                                && a.terrain.equals("forest")) {
                            a.resource = "animals";
                            rsccount++;
                        }
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 2)
                            break;
                        if (a.resource == null || !a.resource.equals("animals")) {
                            a.terrain = "forest";
                            a.resource = "animals";
                            rsccount++;
                        }
                    }
                } else if (gt.village.team.tribe.equals("Hoodrick")) {
                    int rsccount = 0;
                    for (GameTile a : gt.adj) {
                        if (a.terrain.equals("forest"))
                            rsccount++;
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 3)
                            break;
                        if (a.terrain.equals("land")) {
                            a.terrain = "forest";
                            a.resource = null;
                            rsccount++;
                        }
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 3)
                            break;
                        if (!a.terrain.equals("forest")) {
                            a.terrain = "forest";
                            a.resource = null;
                            rsccount++;
                        }
                    }
                } else if (gt.village.team.tribe.equals("Imperius")) {
                    int rsccount = 0;
                    for (GameTile a : gt.adj) {
                        if (a.resource != null && a.resource.equals("fruit"))
                            rsccount++;
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 2)
                            break;
                        if ((a.resource == null || !a.resource.equals("fruit"))
                                && a.terrain.equals("land")) {
                            a.resource = "fruit";
                            rsccount++;
                        }
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 2)
                            break;
                        if (a.resource == null || !a.resource.equals("fruit")) {
                            a.terrain = "land";
                            a.resource = "fruit";
                            rsccount++;
                        }
                    }
                } else if (gt.village.team.tribe.equals("Kickoo")) {
                    int rsccount = 0;
                    for (GameTile a : gt.adj) {
                        if (a.resource != null && a.resource.equals("fish"))
                            rsccount++;
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 2)
                            break;
                        if ((a.resource == null || !a.resource.equals("fish"))
                                && a.terrain.equals("water")) {
                            a.resource = "fish";
                            rsccount++;
                        }
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 2)
                            break;
                        if (a.resource == null || !a.resource.equals("fish")) {
                            a.terrain = "water";
                            a.resource = "fish";
                            rsccount++;
                        }
                    }
                } else if (gt.village.team.tribe.equals("Xin-xi")) {
                    int rsccount = 0;
                    for (GameTile a : gt.adj) {
                        if (a.terrain.equals("mountains"))
                            rsccount++;
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 3)
                            break;
                        if (a.terrain.equals("land")) {
                            a.terrain = "mountains";
                            a.resource = null;
                            rsccount++;
                        }
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 3)
                            break;
                        if (!a.terrain.equals("mountains")) {
                            a.terrain = "mountains";
                            a.resource = null;
                            rsccount++;
                        }
                    }
                } else if (gt.village.team.tribe.equals("Zebasi")) {
                    int rsccount = 0;
                    for (GameTile a : gt.adj) {
                        if (a.resource != null && a.resource.equals("crop"))
                            rsccount++;
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 2)
                            break;
                        if ((a.resource == null || !a.resource.equals("crop"))
                                && a.terrain.equals("land")) {
                            a.resource = "crop";
                            rsccount++;
                        }
                    }
                    for (GameTile a : gt.adj) {
                        if (rsccount >= 2)
                            break;
                        if (a.resource == null || !a.resource.equals("crop")) {
                            a.terrain = "land";
                            a.resource = "crop";
                            rsccount++;
                        }
                    }
                }
            }
        }
    }

    // generates a perlin map of values on a set of game tiles
    public Map<GameTile, Double> Perlin(Collection<GameTile> c) {
        Map<Integer, Double> edges = new HashMap<>();
        Map<GameTile, Integer> ids = new HashMap<>();
        int i = 0;
        for (GameTile gt : c) {
            ids.put(gt, i);
            i++;
        }
        for (GameTile gt : c) {
            for (GameTile a : gt.adj) {
                if (ids.get(gt) < ids.get(a)) {
                    edges.put(i * ids.get(gt) + ids.get(a), 2 * Math.random() - 1);
                }
            }
        }
        Map<GameTile, Double> v1 = new HashMap<>();
        for (GameTile gt : c) {
            double w = 0;
            for (GameTile a : gt.adj) {
                if (ids.get(gt) < ids.get(a)) {
                    w += edges.get(i * ids.get(gt) + ids.get(a));
                } else {
                    w -= edges.get(i * ids.get(a) + ids.get(gt));
                }
            }
            if (gt.adj.size() > 0)
                w /= gt.adj.size();
            v1.put(gt, w);
        }
        return v1;
    }

    /*
     * Finds a target gametile within a given radius of a root game tile
     * f: condition we want to find
     * g: condition for moving from tiles
     * root: start game tile
     * radius: depth of search
     */
    public boolean search(
            Function<GameTile, Boolean> f, Function<GameTile, Boolean> g, GameTile root, int radius
    ) {
        Set<GameTile> visited = new HashSet<GameTile>();
        Queue<Pair<GameTile, Integer>> q = new ArrayDeque<>();
        q.add(new Pair(root, radius));
        visited.add(root);
        while (!q.isEmpty()) {
            Pair<GameTile, Integer> top = q.remove();
            if (top.second - 1 >= 0) {
                for (GameTile gt : top.first.adj) {
                    if (f.apply(gt) && !visited.contains(gt)) {
                        return true;
                    }
                    if (!visited.contains(gt) && g.apply(gt)) {
                        visited.add(gt);
                        q.add(new Pair(gt, top.second - 1));
                    }
                }
            }
        }
        return false;
    }

    /*
     * Fills in parts of the map with all tribes at once
     */
    public void fill_map(List<Tribe> tribes, List<GameTile> root) {
        if (tribes.size() != root.size())
            return;

        Queue<GameTile> q = new ArrayDeque<>();
        Queue<Tribe> tq = new ArrayDeque<>();
        List<GameTile> left = new LinkedList<>(coods.values());
        Collections.shuffle(left); // randomize tile order

        Map<Tribe, Integer> tribetile = new HashMap<>();

        for (int i = 0; i < tribes.size(); i++) {
            tq.add(tribes.get(i));
            q.add(root.get(i));
            left.remove(root);
            tribetile.put(tribes.get(i), 1);
        }

        while (q.size() > 0) {
            Tribe t = tq.remove();
            GameTile gt = q.remove();

            gt.tstyle = t.tribe;
            gt.ttstyle = t;

            int total = tribetile.get(t) - 1;
            for (GameTile a : gt.adj) {
                if (left.contains(a)) {
                    q.add(a);
                    tq.add(t);
                    left.remove(a);
                    total++;
                }
            }
            if (total == 0) {
                if (left.size() > 0) {
                    q.add(left.remove(0));
                    tq.add(t);
                    total++;
                }
            }
            tribetile.put(t, total);
        }

    }

    /*
     * Divides vertices into inner and outer city sets
     */
    public void divide_map() {
        Set<GameTile> all = new HashSet<>(coods.values());
        Queue<GameTile> q = new ArrayDeque<>();
        Queue<Integer> dq = new ArrayDeque<>();
        for (GameTile gt : coods.values()) {
            if (gt.village != null) {
                all.remove(gt);
                q.add(gt);
                dq.add(0);
            }
        }
        while (!q.isEmpty()) {
            GameTile gt = q.remove();
            int dist = dq.remove() + 1;
            if (dist <= 2) {
                inner.add(gt);
            } else {
                outer.add(gt);
            }
            for (GameTile a : gt.adj) {
                if (all.contains(a)) {
                    all.remove(a);
                    q.add(a);
                    dq.add(dist);
                }
            }
        }
    }

    // checks if a given coordinate is valid
    public boolean valid_cood(Object incood) {
        try {
            return coods.containsKey(incood);
        } catch (Exception e) {
            return false;
        }
    }

    // generates coordinate from x-y vertex
    public Point hex_vertex(int a, int b) {
        if (a % 4 == 3 || a % 4 == 0) {
            if (a % 4 == 3) {
                return new Point(
                        ((a / 4) * 3 + 2) * radius,
                        (int) (SHEAR * radius * Math.sqrt(3) / 2.0 * (2 * b - 1))
                );
            } else {
                return new Point(
                        ((a / 4) * 3) * radius,
                        (int) (SHEAR * radius * Math.sqrt(3) / 2.0 * (2 * b - 1))
                );
            }
        } else {
            if (a % 4 == 1) {
                return new Point(
                        ((a / 4) * 3) * radius + radius / 2,
                        (int) (SHEAR * radius * Math.sqrt(3) / 2.0 * (2 * b))
                );
            } else {
                return new Point(
                        ((a / 4) * 3 + 1) * radius + radius / 2,
                        (int) (SHEAR * radius * Math.sqrt(3) / 2.0 * (2 * b))
                );
            }
        }
    }
}
