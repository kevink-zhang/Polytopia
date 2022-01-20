package org.cis120.polytopia.Tile;

import org.cis120.polytopia.FontLoader;
import org.cis120.polytopia.GameAction.*;
import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Server.ServerPacket;
import org.cis120.polytopia.Unit.Stat;
import org.cis120.polytopia.Unit.Unit;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.cis120.polytopia.GameBody.*;

public class City {
    public GameTile src;
    public Set<GameTile> land;
    public Polygon border = new Polygon();
    public Set<Unit> units = new HashSet<>();
    public int unitcap = 0;

    public String name;
    public Tribe team;
    public boolean capital;

    public int level;
    public int population;

    // special buildings one per city
    public boolean windmill = false;
    public boolean forge = false;
    public boolean sawmill = false;
    public boolean customhouse = false;

    public City(GameTile gt, Tribe t, boolean b) {
        land = new HashSet<>();
        land.add(gt);
        gt.city = this;
        src = gt;
        team = null;
        if (t != null) {
            generateName(t);
            if (src.unit != null) {
                src.unit.origin.units.remove(src.unit);
                units.add(src.unit);
            }
            team = t;
            team.cities.add(this);
            unitcap = 3;
        }
        else{
            unitcap = 0;
        }
        capital = b;
        level = 1;
    }

    public City(GameTile gt, Tribe t) {
        this(gt, t, false);
    }

    public void convert(Tribe t) {
        if (team == t)
            return; // same team no conversion
        if (team == null) {
            team = t;
            generateName(t);
            if (src.unit != null) {
                src.unit.origin.units.remove(src.unit);
                units.add(src.unit);
            }
            expand();
            unitcap = 3;
        } else {
            team.cities.remove(this);
        }
        team = t;
        if(!t.cities.contains(this)) t.cities.add(this);
        for(GameTile gt: land){
            gt.team = t;
        }
    }

    public void expand() {
        calcBorders(false);
        Set<GameTile> toadd = new HashSet<>();
        for (GameTile gt : land) {
            for (GameTile a : gt.adj) {
                if (a.team == null) {
                    toadd.add(a);
                }
            }
        }
        for (GameTile gt : toadd) {
            land.add(gt);
            gt.city = this;
        }
        for (GameTile gt : land) {
            gt.team = team;
        }
        calcBorders(true);
    }

    public void addPopulation(int pnum, boolean broadcast) {
        if (broadcast && pnum != 0) {
            CLIENT.send(new ServerPacket("action", new PopChange(src, pnum).encode()));
        }
        for (int i = 0; i < pnum; i++) {
            population++;
            // TimeUnit time = TimeUnit.MILLISECONDS;
            // try {
            // time.sleep(500);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
            while (population >= level + 2) {
                if (level == 3) {
                    expand();
                }
                if (level >= 5) { // adds a giant
                    if(src.unit!=null){
                        boolean moveable = false;
                        for(GameTile a: src.adj){
                            if(src.cost(a, src.unit)>-1){
                                List<GameTile> minimove = new LinkedList<>();
                                minimove.add(src); minimove.add(a);
                                GameAction ga=new Move(src.unit, minimove);
                                GAME.ACTIONS.add(ga);
                                CLIENT.send(new ServerPacket("action", ga.encode()));
                                moveable = true;
                                break;
                            }
                        }
                        if(!moveable){
                            GameAction ga=new Death(src.unit);
                            GAME.ACTIONS.add(ga);
                            CLIENT.send(new ServerPacket("action", ga.encode()));
                        }
                    }
                    GameAction ga=new Spawn("giant", src);
                    GAME.ACTIONS.add(ga);
                    CLIENT.send(new ServerPacket("action", ga.encode()));
                }
                population -= level + 2;
                level++;
                unitcap++;
            }
        }
    }

    /*
     * Finds the border of edges around a city and colors them accordingly
     * toggle: true if coloring to city color, false if coloring to default
     */
    public void calcBorders(boolean toggle) {
        if (this.team == null)
            return; // empty village
        Set<Edge> ped = new HashSet<>();
        for (GameTile gt : land) {
            for (Edge e : gt.outline) {
                GameTile oth = e.other(gt);
                if (oth == null || !land.contains(oth)) {
                    if (toggle) {
                        e.c = team.color;
                        ped.add(e);
                    } else {
                        e.c = null;
                    }
                }
            }
        }
        border = new Polygon();
        Point tolink = new Point();
        while (!ped.isEmpty()) {
            for (Edge e : ped) {
                if (border.npoints == 0) {
                    border.addPoint(e.p1.x, e.p1.y);
                    tolink = e.p2;
                    break;
                } else {
                    if (e.dist(tolink) < 3) {
                        Point pp = e.other(tolink);
                        border.addPoint(pp.x, pp.y);
                        tolink = pp;
                        ped.remove(e);
                        break;
                    }
                }
            }
        }
        border.addPoint(border.xpoints[0], border.ypoints[0]);
    }

    public void generateName(Tribe t) {
        String[] sylls;
        if (t.tribe.equals("Ai-Mo")) {
            sylls = new String[] { " ", "dee", "fï", "kï", "lee", "lï", "nï", "po", "pï", "so",
                    "sï", "to", "tï" };
        } else if (t.tribe.equals("Bardur")) {
            sylls = new String[] { "ark", "bu", "fla", "gru", "gu", "lak", "lin", "ork", "rø",
                    "tof", "ur" };
        } else if (t.tribe.equals("Hoodrick")) {
            sylls = new String[] { "ber", "don", "go", "ick", "in", "ley", "lo", "ol", "ry", "th",
                    "wa", "we" };
        } else if (t.tribe.equals("Imperius")) {
            sylls = new String[] { "ca", "do", "ica", "ip", "lo", "lus", "ma", "mo", "mus", "nu",
                    "pi", "re", "res", "ro", "sum", "te" };
        } else if (t.tribe.equals("Kickoo")) {
            sylls = new String[] { " ", "an", "ko", "li", "lo", "lu", "ma", "no", "nu", "oki", "si",
                    "va" };
        } else if (t.tribe.equals("Luxidoor")) {
            sylls = new String[] { "'", "au", "em", "exi", "ga", "iss", "ki", "ly", "lo", "ni",
                    "ou", "pô", "uss", "ux" };
        } else if (t.tribe.equals("Oumaji")) {
            sylls = new String[] { "ba", "dor", "gh", "ha", "ji", "ke", "la", "lim", "mu", "on",
                    "si", "ye" };
        } else if (t.tribe.equals("Quetzali")) {
            sylls = new String[] { " ", "el", "ca", "cho", "chu", "ex", "ill", "ix", "ja", "qu",
                    "tal", "tek", "tz", "was", "wop", "ya" };
        } else if (t.tribe.equals("Vengir")) {
            sylls = new String[] { "ar", "bu", "ck", "cth", "dis", "gor", "he", "im", "na", "nt",
                    "pe", "rot", "rz", "st", "th", "tu", "xas" };
        } else if (t.tribe.equals("Xin-xi")) {
            sylls = new String[] { "-", "bu", "cha", "gu", "li", "po", "sha", "szu", "xi", "yo" };
        } else if (t.tribe.equals("Yadakk")) {
            sylls = new String[] { "ar", "ark", "az", "ber", "ez", "ge", "gy", "kh", "ki", "kol",
                    "ka", "mer", "ol", "sam", "sh", "st", "tja", "tsa", "ug", "urk", "ül", "üm", "an" };
        } else if (t.tribe.equals("Zebasi")) {
            sylls = new String[] { "bo", "co", "la", "mo", "wa", "ya", "za", "zan", "zim", "zu" };
        } else {
            sylls = new String[] { "polytopia" };
        }
        int numsylls = 2 + new Random().nextInt(2);

        name = "";
        for (int i = 0; i < numsylls; i++) {
            String toadd = sylls[new Random().nextInt(sylls.length)];
            if ((i == numsylls - 1 || i == 0) &&
                    (toadd.equals(" ") || toadd.equals("-") || toadd.equals("'"))) {
                i--;
            } else {
                name += toadd;
            }
        }
    }

    public void draw(Graphics g, ImageLoader il) { // drawing self
        if (team == null) {
            g.drawImage(
                    il.village,
                    src.center.x - 30, src.center.y - 18, 60, 35, null
            );
        } else {
            if (capital) {
                g.drawImage(
                        il.city(team.tribe, 0),
                        src.center.x - 15, src.center.y - 30, 30, 40, null
                );
            } else {
                g.drawImage(
                        il.city(team.tribe, Math.min((level+1) / 2, 4)),
                        src.center.x - 15, src.center.y - 20, 30, 40, null
                );
            }
        }
    }

    public void draw2(Graphics g, ImageLoader il) { // drawing area border
        ((Graphics2D) g).setStroke(new BasicStroke(3));
        g.setColor(team.color);
        // g.drawPolygon(border);
        g.drawPolygon(border);
        // looks cool, but may be disorientating :(
         g.setColor(new Color(team.color.getRed(), team.color.getGreen(),
         team.color.getBlue(), 75));
         g.fillPolygon(border);
        ((Graphics2D) g).setStroke(new BasicStroke(1));
    }

    public void draw3(Graphics g, ImageLoader il, Point p) { // for infobox
        if (team == null) {
            g.drawImage(
                    il.village,
                    p.x - 30, p.y - 18, 60, 35, null
            );
        } else {
            if (capital) {
                g.drawImage(
                        il.city(team.tribe, 0),
                        p.x - 15, p.y - 30, 30, 40, null
                );
            } else {
                g.drawImage(
                        il.city(team.tribe, Math.min((level+1) / 2, 4)),
                        p.x - 15, p.y - 20, 30, 40, null
                );
            }
        }
    }

    public void draw4(Graphics g, ImageLoader il, FontLoader fl) {
        if (name == null)
            return;

        g.setFont(fl.text3);
        g.setColor(Color.WHITE);
        int stringwidth = g.getFontMetrics().stringWidth(name) / 2;
        g.drawString(name, src.center.x - stringwidth, src.center.y + 13);

        if (team != USER)
            return;

        int maxpop = level + 2;
        int unipop = units.size();

        int mwid = 50 + level * 2;
        int dddd = mwid / maxpop;
        g.setColor(Color.black);
        for (int i = 0; i < maxpop; i++) {
            g.setColor(new Color(255, 255, 255, 230));
            g.fillRoundRect(
                    src.center.x - mwid / 2 + dddd * i, src.center.y + 15, dddd - 1, 6, 4, 4
            );
        }
        for (int i = 0; i < population; i++) {
            g.setColor(new Color(50, 154, 233, 230));
            g.fillRoundRect(
                    src.center.x - mwid / 2 + dddd * i, src.center.y + 15, dddd - 1, 6, 4, 4
            );
        }
        for (int i = 0; i < unipop; i++) {
            g.setColor(Color.BLACK);
            g.fillOval(src.center.x - mwid / 2 + dddd / 2 + dddd * i - 1, src.center.y + 17, 2, 2);
        }

        // g.setColor(Color.WHITE);
        // g.drawRoundRect(src.center.x-25, src.center.y+15,50, 6,4,4);
    }
}
