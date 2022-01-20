package org.cis120.polytopia;

import org.cis120.polytopia.Bots.HardBot;
import org.cis120.polytopia.GameAction.*;
import org.cis120.polytopia.Server.ServerPacket;
import org.cis120.polytopia.Tile.*;
import org.cis120.polytopia.Unit.Stat;
import org.cis120.polytopia.Unit.Unit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

import static org.cis120.polytopia.GameBody.*;

public class GameBoard {
    public List<GameTile> board = new LinkedList<>();
    public static Set<GameTile> VISION = new HashSet<>();
    //public static Color FOG = new Color(200, 200, 210);
    public GameMap gamemap;
    public List<Tribe> players = new LinkedList<>();

    private boolean gameoutcome = false;

    public int turn = 0;
    public int round = 0;

    public List<Color> pcolors = new LinkedList<>();
    public Queue<GameAction> ACTIONS = new ArrayDeque<>();

    public GameTile capital_lock = null; // for relocking camera onto capital
    // User UI interactions
    public GameTile tile_mouse_down; // gametile where the mouse is pressed on
    public GameTile last_tile_down; // gametile where the last
    public Point pt_mouse_down; // point where mouse is held down
    public Point cam_mouse_down; // point where camera is at at mouse down
    private GameTile last_selected; // for uniqueness
    private boolean last_selected_b; // tileorunit boolean
    public Point CAMERA;
    public double ZOOM;

    public Set<List<Double>> stars = new HashSet<>();

    public PopUp poppers = new PopUp("title","this is a really long body so i hope that a line break will occur somewhere yay ty");

    public CircleButton exitgame;
    public CircleButton techtree;
    public CircleButton endturn;

    public CircleButton exittech;

    // Game constants

    public InfoBox infobox = new InfoBox(0, PANEL_HEIGHT, 0, -80, PANEL_WIDTH, 80);

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 20;

    public GameBoard() {
        // initializes camera variables
        CAMERA = new Point(-PANEL_WIDTH / 2, -PANEL_HEIGHT / 2);
        ZOOM = 1;
        tile_mouse_down = null;

        // makes stars
        for (int i = 0; i < 100; i++) {
            List<Double> l = new LinkedList<>();
            l.add(Math.random());
            l.add(Math.random());
            l.add(0.0);// 1-2*Math.random());
            l.add(0.0);// 1-2*Math.random());
            stars.add(l);
        }

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        // setFocusable(true);

    }

    /*
     * Called for input events
     */
    public void mouseReleased(MouseEvent e) {
        if(poppers.show){
            if(poppers.OK(e.getPoint())){
                GameAction ga = null;
                String t = poppers.title;
                GameTile spot = poppers.tile;
                if (t.equals("hunting") && USER.money >= 3) {
                    spot.resource = null;
                    spot.city.addPopulation(2, true);
                    ga = new Build(spot);
                    USER.money -= 3;
                } else if (t.equals("archer") && USER.money >= 3) {
                    ga = new Spawn("archer", spot);
                    USER.money -= 3;
                } else if (t.equals("grow forest") && USER.money >= 5) {
                    spot.terrain = "forest";
                    spot.resource = null;
                    ga = new Build(spot);
                    USER.money -= 5;
                } else if (t.equals("forest temple") && USER.money >= 30) {
                    spot.building = new Building("forest temple", spot);
                    spot.resource = null;
                    ga = new Build(spot);
                    USER.money -= 30;
                } else if (t.equals("lumber hut") && USER.money >= 3) {
                    spot.building = new Building("lumber hut", spot);
                    spot.city.addPopulation(2, true);
                    spot.resource = null;
                    ga = new Build(spot);
                    USER.money -= 3;
                } else if (t.equals("catapult") && USER.money >= 8) {
                    ga = new Spawn("catapult", spot);
                    USER.money -= 8;
                } else if (t.equals("sawmill") && USER.money >= 5) {
                    spot.building = new Building("sawmill", spot);
                    spot.resource = null;
                    ga = new Build(spot);
                    USER.money -= 5;
                } else if (t.equals("rider") && USER.money >= 3) {
                    ga = new Spawn("rider", spot);
                    USER.money -= 3;
                } else if (t.equals("roads") && USER.money >= 2) {
                    spot.roads = true;
                    ga = new Build(spot);
                    USER.money -= 2;
                } else if (t.equals("customs house") && USER.money >= 5) {
                    spot.building = new Building("customs house", spot);
                    spot.resource = null;
                    ga = new Build(spot);
                    USER.money -= 5;
                } else if (t.equals("temple") && USER.money >= 30) {
                    spot.building = new Building("temple", spot);
                    spot.resource = null;
                    ga = new Build(spot);
                    USER.money -= 30;
                } else if (t.equals("clear forest")) {
                    spot.terrain = "land";
                    spot.resource = null;
                    USER.money += 2;
                    ga = new Build(spot);
                } else if (t.equals("retire")) {
                    USER.money += spot.unit.stats.cost;
                    ga = new Death(spot.unit);
                } else if (t.equals("knight") && USER.money >= 10) {
                    ga = new Spawn("knight", spot);
                    USER.money -= 10;
                } else if (t.equals("burn forest") && USER.money >= 2) {
                    spot.terrain = "land";
                    spot.resource = "crop";
                    ga = new Build(spot);
                    USER.money -= 2;
                } else if (t.equals("harvest") && USER.money >= 3) {
                    spot.resource = null;
                    spot.city.addPopulation(2, true);
                    ga = new Build(spot);
                    USER.money -= 3;
                } else if (t.equals("farm") && USER.money >= 5) {
                    spot.resource = null;
                    spot.building = new Building("farm", spot);
                    spot.city.addPopulation(3, true);
                    ga = new Build(spot);
                    USER.money -= 5;
                } else if (t.equals("windmill") && USER.money >= 5) {
                    spot.resource = null;
                    spot.building = new Building("windmill", spot);
                    ga = new Build(spot);
                    USER.money -= 5;
                } else if (t.equals("destroy")) {
                    spot.building.destroy();
                    spot.building = null;
                    ga = new Build(spot);
                } else if (t.equals("defender") && USER.money >= 3) {
                    ga = new Spawn("defender", spot);
                    USER.money -= 3;
                } else if (t.equals("guard tower") && USER.money >= 5) {
                    spot.building = new Building("guard tower", spot);
                    spot.resource = null;
                    ga = new Build(spot);
                    USER.money -= 5;
                } else if (t.equals("mine") && USER.money >= 5) {
                    spot.building = new Building("mine", spot);
                    spot.resource = null;
                    spot.city.addPopulation(3, true);
                    ga = new Build(spot);
                    USER.money -= 5;
                } else if (t.equals("swordsman") && USER.money >= 5) {
                    ga = new Spawn("swordsman", spot);
                    USER.money -= 5;
                } else if (t.equals("forge") && USER.money >= 5) {
                    spot.building = new Building("forge", spot);
                    spot.resource = null;
                    ga = new Build(spot);
                    USER.money -= 5;
                } else if (t.equals("mountain temple") && USER.money >= 30) {
                    spot.building = new Building("mountain temple", spot);
                    spot.resource = null;
                    ga = new Build(spot);
                    USER.money -= 30;
                } else if (t.equals("mind bender") && USER.money >= 5) {
                    ga = new Spawn("mind bender", spot);
                    USER.money -= 5;
                } else if (t.equals("fish") && USER.money >= 3) {
                    spot.resource = null;
                    spot.city.addPopulation(2, true);
                    ga = new Build(spot);
                    USER.money -= 3;
                } else if (t.equals("ship") && USER.money >= 5) {
                    ga = new Upgrade(spot.unit, false, "ship");
                    USER.money -= 5;
                } else if (t.equals("port") && USER.money >= 10) {
                    spot.building = new Building("port", spot);
                    spot.city.addPopulation(3, true);
                    spot.resource = null;
                    ga = new Build(spot);
                    USER.money -= 10;
                } else if (t.equals("battleship")) {
                    ga = new Upgrade(spot.unit, false, "battleship");
                    USER.money -= 15;
                } else if (t.equals("whale")) {
                    USER.money += 15;
                    spot.resource = null;
                    ga = new Build(spot);
                } else if (t.equals("water temple") && USER.money >= 30) {
                    spot.building = new Building("water temple", spot);
                    ga = new Build(spot);
                    USER.money -= 30;
                } else if (t.equals("warrior") && USER.money >= 2) {
                    ga = new Spawn("warrior", spot);
                    USER.money -= 2;
                } else if (t.equals("capture")) {
                    spot.city.convert(spot.unit.team);
                    spot.unit.movephase = 3;
                    spot.unit.setMoves();
                    ga = new Build(spot);
                } else if (t.equals("heal")) {
                    ga = new Upgrade(spot.unit, true, spot.unit.stats.type);
                    spot.unit.movephase = 3;
                }
                if (ga != null) {
                    ACTIONS.add(ga);
                    CLIENT.send(new ServerPacket("action", ga.encode()));
                }
            }
            else if(poppers.CLOSE(e.getPoint())){

            }
            return;
        }
        if (scene.equals("in game") && ACTIONS.isEmpty()) {
            if (infobox != null && infobox.close != null) {
                if (infobox.close.click(e.getPoint())) {
                    infobox.updateTile(null, false);
                    return;
                }
                for (CircleButton actionicon : infobox.actionlist) {
                    GameTile spot = infobox.display;
                    if (actionicon.click(e.getPoint())) {
                        String t = actionicon.label;
                        poppers.title = t;
                        poppers.tile = spot;
                        poppers.show = true;
                        if(actionicon.cost > USER.money){
                            poppers.single = true;
                        }
                        else{
                            poppers.single = false;
                        }

                        if (t.equals("hunting")) {
                            poppers.body = "hunt to gain 2 population. consumes resource";
                        } else if (t.equals("archer")) {
                            poppers.body = "train an archer on "+ spot.village.name;
                        } else if (t.equals("grow forest")) {
                            poppers.body = "converts land tile into a forest tile";
                        } else if (t.equals("forest temple")) {
                            poppers.body = "builds a forest temple. forest temples provide friendly units in forests with stat bonuses. stacks 3 times";
                        } else if (t.equals("lumber hut")) {
                            poppers.body = "builds a lumber hut and gives 2 population. consumes resource";
                        } else if (t.equals("catapult")) {
                            poppers.body = "train a catapult on "+ spot.village.name;
                        } else if (t.equals("sawmill")) {
                            poppers.body = "builds a sawmill on this tile. sawmills give 2 additional population for every adjacent lumber hut";
                        } else if (t.equals("rider")) {
                            poppers.body = "train a rider on "+ spot.village.name;
                        } else if (t.equals("roads")) {
                            poppers.body = "build a road on this tile. friendly and neutral roads reduce movement costs in half and allow movement in forest tiles";
                        } else if (t.equals("customs house")) {
                            poppers.body = "builds a customs house on this tile. customs houses give 2 additional star income for every adjacent port";
                        } else if (t.equals("temple")) {
                            poppers.body = "builds a temple. temples provide friendly land units with stat bonuses. stacks 3 times";
                        } else if (t.equals("clear forest")) {
                            poppers.body = "clear forest to gain 2 stars. converts forest tile into land tile";
                        } else if (t.equals("retire")) {
                            poppers.body = "retire a unit for half its base cost. retiring a "+spot.unit+ " will give you "+ spot.unit.stats.cost/2 + " stars";
                        } else if (t.equals("knight")) {
                            poppers.body = "train a knight on "+ spot.village.name;
                        } else if (t.equals("burn forest")) {
                            poppers.body = "converts forest tile to a land tile with crop resource";
                        } else if (t.equals("harvest")) {
                            poppers.body = "harvest fruit to gain 2 population. consumes resource";
                        } else if (t.equals("farm")) {
                            poppers.body = "builds a farm and gives 3 population. consumes resource";
                        } else if (t.equals("windmill")) {
                            poppers.body = "builds a windmill on this tile. windmills give 2 additional population for every adjacent farm";
                        } else if (t.equals("destroy")) {
                            poppers.body = "destroys the "+spot.building.name+", and removes all benefits from the building";
                        } else if (t.equals("defender")) {
                            poppers.body = "train a defender on "+ spot.village.name;
                        } else if (t.equals("guard tower")) {
                            poppers.body = "build a guard tower on this tile. guard towers give 2 radius of vision, and have an additional vision bonus on mountains";
                        } else if (t.equals("mine")) {
                            poppers.body = "builds a mine and gives 3 population. consumes resource";
                        } else if (t.equals("swordsman")) {
                            poppers.body = "train a swordsman on "+ spot.village.name;
                        } else if (t.equals("forge")) {
                            poppers.body = "builds a forge on this tile. forges give 2 additional population for every adjacent mine";
                        } else if (t.equals("mountain temple")) {
                            poppers.body = "builds a mountain temple. mountain temples provide friendly units on mountains with stat bonuses. stacks 3 times";
                        } else if (t.equals("mind bender")) {
                            poppers.body = "train a mind bender on "+ spot.village.name;
                        } else if (t.equals("fish")) {
                            poppers.body = "fish to gain 2 population. consumes resource";
                        } else if (t.equals("ship")) {
                            poppers.body = "upgrades this boat to a ship";
                        } else if (t.equals("port")) {
                            poppers.body = "builds a port on this water tile. ports allow land units to use boats";
                        } else if (t.equals("battleship")) {
                            poppers.body = "upgrades this ship to a battleship";
                        } else if (t.equals("whale")) {
                            poppers.body = "whale to gain 15 stars. consumes resource";
                        } else if (t.equals("water temple")) {
                            poppers.body = "builds a water temple. water temples provide friendly water naval units with stat bonuses. stacks 3 times";
                        } else if (t.equals("warrior")) {
                            poppers.body = "train a warrior on "+ spot.village.name;
                        } else if (t.equals("capture")) {
                            poppers.body = "capture this city for your empire! unit cannot move after capture";
                        } else if (t.equals("heal")) {
                            poppers.body = "heals the unit 4 hp if on friendly territory, and 2 hp on enemy territory. unit cannot move after heal";
                        }
                        return;
                    }
                }
            }
            if (exitgame != null && exitgame.click(e.getPoint())) {
                leaveGame();
                return;
            } else if (techtree != null && techtree.click(e.getPoint())) {
                scene = "tech tree";
                CAMERA = new Point(0, 0);
                ZOOM = 1;
                return;
            } else if (endturn != null && turn == USER.index && endturn.click(e.getPoint())) {
                for (GameTile gt : board) {
                    gt.select(false);
                }
                nextTurn();
                CLIENT.send(new ServerPacket("end turn", null));

                last_selected = null;
                return;
            }

            cleanTargets();

            // if a tile is selected, thats the last tile selected
            for (GameTile gt : board) {
                if (gt.selected) {
                    last_tile_down = gt;
                }
            }
            boolean newaction = false;
            if (last_tile_down != null && last_tile_down.unit != null && tile_mouse_down != null) {
                if (!last_tile_down.tileorunit) {
                    if (last_tile_down.unit.moveset.contains(tile_mouse_down)) {
                        for (GameAction foo : last_tile_down.unit
                                .move(findPath(last_tile_down, tile_mouse_down))) {
                            ACTIONS.add(foo);
                            CLIENT.send(new ServerPacket("action", foo.encode()));
                        }
                        newaction = true;
                    } else if (last_tile_down.unit.attackset.contains(tile_mouse_down)) {
                        for (GameAction foo : last_tile_down.unit.attack(tile_mouse_down.unit)) {
                            ACTIONS.add(foo);
                            CLIENT.send(new ServerPacket("action", foo.encode()));
                        }
                        newaction = true;
                    }
                }
            }
            if (last_tile_down == tile_mouse_down && last_tile_down != null) {
                tile_mouse_down.select(true);
                tile_mouse_down = null;
            } else {
                for (GameTile gt : board) {
                    gt.select(false);
                }
                if (tile_mouse_down != null && !newaction) {
                    if (!VISION.contains(tile_mouse_down)) {
                        // TO CHANGE: bug occurs when fog tile is selected, it gets yoinked out of
                        // existence
                        tile_mouse_down.selected = true;
                        tile_mouse_down.tileorunit = true;
                    } else {
                        tile_mouse_down.select(true);
                    }
                }
                last_tile_down = tile_mouse_down;
                tile_mouse_down = null;
            }
            pt_mouse_down = null;
            cam_mouse_down = null;
            // offsquare = false;
        }
    }

    public void techReleased(MouseEvent e) {
        Point ingame_pt = convertRawPt(e.getPoint());
        if (exittech.click(e.getPoint())) {
            scene = "in game";
            CAMERA = new Point(-capital_lock.center.x, -capital_lock.center.y);
            ZOOM = 1;
            last_tile_down = null;
            pt_mouse_down = null;
            cam_mouse_down = null;
        }
        for (Tech t : USER.techtree) {
            if (t.hitbox.click(ingame_pt)) {
                if(!t.status && t.parent!=null && t.parent.status && USER.money>=t.cost(USER.cities.size())){
                    t.status = true;
                    USER.money -= t.cost(USER.cities.size());
                }
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (scene.equals("in game") || scene.equals("tech tree")) {
            // Point ingame_pt = convertRawPt(e.getPoint());

            if (cam_mouse_down != null && pt_mouse_down != null) {
                CAMERA = new Point(
                        (int) (cam_mouse_down.x + (e.getX() - pt_mouse_down.x) / ZOOM),
                        (int) (cam_mouse_down.y + (e.getY() - pt_mouse_down.y) / ZOOM)
                );
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        if ((scene.equals("tech tree") || scene.equals("in game")) && ACTIONS.isEmpty()) {
            if (exitgame != null && exitgame.click(e.getPoint())) {
                return;
            } else if (exitgame != null && techtree.click(e.getPoint())) {
                return;
            } else if (exitgame != null && endturn.click(e.getPoint())) {
                return;
            }
            if (infobox != null) {
                if (infobox.close != null && infobox.close.click(e.getPoint())) {
                    return;
                }
                for (CircleButton cb : infobox.actionlist) {
                    if (cb.click(e.getPoint())) {
                        return;
                    }
                }
            }

            cleanTargets();

            Point ingame_pt = convertRawPt(e.getPoint());

            tile_mouse_down = findTile(ingame_pt.x, ingame_pt.y);

            pt_mouse_down = e.getPoint();
            cam_mouse_down = new Point((int) CAMERA.getX(), (int) CAMERA.getY());
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (scene.equals("in game") || scene.equals("tech tree"))
            ZOOM *= (1 + e.getPreciseWheelRotation() / 100);
    }

    /*
     * Converts coordinates on JPanel into in game coordinates
     */
    public Point convertRawPt(Point p) {
        return new Point(
                (int) ((p.x - PANEL_WIDTH / 2) / ZOOM) - CAMERA.x,
                (int) ((p.y - PANEL_HEIGHT / 2) / ZOOM) - CAMERA.y
        );
    }

    /*
     * Adds a player to the game
     * u: user id
     * t: tribe
     */
    public boolean addPlayer(String u, String t, String id) {
        t = t.toLowerCase();
        for (Tribe oth : players) {
            if (oth.user.equals(u)) {
                return false;
            }
        }

        Collections.shuffle(pcolors);
        if (pcolors.size() == 0) {
            int tries = 20;
            for (int failsafe = 0; failsafe < tries; failsafe++) {
                int r = new Random().nextInt(255);
                int g = new Random().nextInt(255);
                int b = new Random().nextInt(255);
                boolean canuse = true;
                for (Tribe ttt : players) {
                    Color asd = ttt.color;
                    if (Math.abs(asd.getRed() - r) < 50 &&
                            Math.abs(asd.getGreen() - g) < 50 &&
                            Math.abs(asd.getBlue() - b) < 50) {
                        canuse = false;
                        break;
                    }
                }
                if (r + b + g > 180 * 3) { // too light
                    canuse = false;
                }
                if (canuse || failsafe == tries - 1) {
                    pcolors.add(new Color(r, g, b));
                    break;
                }
            }
        }
        players.add(new Tribe(t, u, id, pcolors.remove(0)));

        return true;
    }

    /*
    Adds a bot to the game
     */
    public boolean addBot(String diff, String t){
        Collections.shuffle(pcolors);
        if (pcolors.size() == 0) {
            int tries = 20;
            for (int failsafe = 0; failsafe < tries; failsafe++) {
                int r = new Random().nextInt(255);
                int g = new Random().nextInt(255);
                int b = new Random().nextInt(255);
                boolean canuse = true;
                for (Tribe ttt : players) {
                    Color asd = ttt.color;
                    if (Math.abs(asd.getRed() - r) < 50 &&
                            Math.abs(asd.getGreen() - g) < 50 &&
                            Math.abs(asd.getBlue() - b) < 50) {
                        canuse = false;
                        break;
                    }
                }
                if (r + b + g > 180 * 3) { // too light
                    canuse = false;
                }
                if (canuse || failsafe == tries - 1) {
                    pcolors.add(new Color(r, g, b));
                    break;
                }
            }
        }
        if(diff.equals("hard")) {
            players.add(new HardBot(t, pcolors.remove(0)));
            return true;
        }
        return false;
    }
    /*
     * Increments the turn
     */
    public void nextTurn() {
        //turns off infobox glitch
        infobox.display = null;
        infobox.toru = false;

        turn = (turn + 1) % players.size();
        if (turn == 0)
            round++;

        if (round > 0) {// earn income
            players.get(turn).calcIncome();
            players.get(turn).money += players.get(turn).income;
        }

        for (GameTile gt : board) {
            if (gt.unit != null && gt.unit.team == players.get(turn)) {
                gt.unit.movephase = 0;
            } else if (gt.unit != null) {
                gt.unit.movephase = 3;
            }
        }
        if (!players.get(turn).active) {
            nextTurn();
        }
        //bot start turns
        if(players.get(turn) instanceof HardBot){
            ((HardBot) players.get(turn)).startTurn();
        }
    }

    /*
     * Initializes the board with a given game map class
     */
    public void createSpawn(GameMap gm) {
        board = new LinkedList<>();
        gamemap = gm;

        // adds game tiles to board
        for (GameTile gt : gm.coods.values()) {
            board.add(gt);
        }

        // sorts tiles into a drawing order
        Collections.sort(board, new CustomComparator());

        // initializes USER
        int counter = 0;
        for (Tribe t : players) {
            counter++;
            if (t.userid.equals(ID)) {
                t.index = counter - 1;
                USER = t;
                System.out.println("Player " + counter);
                break;
            }
        }

        turn = 0;
        // adds a troop on top of each capital for each tribe
        for (GameTile gt : board) {
            if (gt.village != null && gt.village.capital) {
                if (gt.team == USER) {
                    capital_lock = gt;
                }
                Unit uwu = new Unit(
                        new Stat(gt.village.team.initunit), gt.village, gt.village.team, gt
                );
                gt.unit = uwu;
                gt.village.units.add(uwu);
                if (gt.village.team.userid.equals(players.get(0).userid)) {
                    uwu.movephase = 0;
                } else {
                    uwu.movephase = 3;
                }
                gt.village.addPopulation(gt.team.initpop, false);
            }
        }
        // nextTurn();
        // player 0 turn

        System.out.println(
                "Board of size: "
                        + gm.coods.values().size() + " initialized"
        );
    }

    // special initialization from JSON

    public void decodeSpawn(List<Object> input) {
        // extracts players
        List<List<String>> l = (List<List<String>>) input.get(0);
        for (List<String> ll : l) {
            if (il.light)
                il.reload(ll.get(0));
        }
        for (List<String> ll : l) {
            players.add(
                    new Tribe(
                            ll.get(0), ll.get(1), ll.get(2),
                            new Color(
                                    Integer.parseInt(ll.get(3)), Integer.parseInt(ll.get(4)),
                                    Integer.parseInt(ll.get(5))
                            )
                    )
            );
        }

        for (Tribe t : players) {
            if (t.userid.equals(ID)) {
                USER = t;
                break;
            }
        }

        Map<Object, Object> m = (Map<Object, Object>) input.get(1);
        GameMap gm = new GameMap(players);
        gm.fromEncoding(m);
        createSpawn(gm);
    }

    public List<Object> encodeSpawn() {
        List<Object> ret = new LinkedList<>();
        // encodes players
        List<List<String>> encodeplayers = new LinkedList<>();
        for (Tribe t : players) {
            List<String> foop = new LinkedList<>();
            foop.add(t.tribe);
            foop.add(t.user);
            foop.add(t.userid);
            foop.add(String.valueOf(t.color.getRed()));
            foop.add(String.valueOf(t.color.getGreen()));
            foop.add(String.valueOf(t.color.getBlue()));
            encodeplayers.add(foop);
        }
        ret.add(encodeplayers);
        // encodes game map
        ret.add(gamemap.toStringMap());

        return ret;
    }

    // decoding of game actions is done in GameBody

    /*
     * Cleans up target markings on all tiles after a unit is deselected
     */
    public void cleanTargets() {
        for (GameTile gt : board) {
            gt.targetselector = 0;
        }
    }

    /*
     * Finds the id of the game tile selected by a mouse click on x,y
     */
    public GameTile findTile(int x, int y) {
        int foo = 0;
        for (GameTile gt : board) {
            if (gt.objtype().equals("hex")) {
                if (((Polygon) gt.hitbox()).contains(x, y))
                    return gt;
                foo++;

            }
        }
        return null;
    }

    /*
     * Finds the best path for a unit from one tile to a destination
     */
    public List<GameTile> findPath(GameTile src, GameTile tar) {
        Map<GameTile, GameTile> par = new HashMap<>();
        par.put(src, null);
        Queue<GameTile> q = new ArrayDeque<>();
        q.add(src);
        boolean istar = false;
        while (!q.isEmpty() && !istar) {
            GameTile gt = q.remove();
            if (par.get(gt) == null || par.get(gt).cost(gt, src.unit) > 0) {
                for (GameTile a : gt.adj) {
                    if (!par.containsKey(a) && gt.cost(a, src.unit) > -1) { // checks that the node
                        // is unvisited and
                        // valid
                        q.add(a);
                        par.put(a, gt);
                    }
                    if (a == tar) {
                        istar = true;
                    }
                }
            }
        }
        if (!istar) {
            System.out.println("src and tar not linked");
            return null; // target and source are not linked
        }
        List<GameTile> ret = new LinkedList<>();
        GameTile curr = tar;
        while (curr != src) {
            ret.add(0, curr);
            curr = par.get(curr);
        }
        ret.add(0, src);
        return ret;
    }

    /*
     * tick function
     */
    void tick() {
        //bot actions on host
        if(players.size()>0 && players.get(turn) instanceof HardBot && SERVER!=null){
            if(ACTIONS.isEmpty()) {
                if (!((HardBot) players.get(turn)).unitTurn()) {
                    ((HardBot) players.get(turn)).endturn();
                    //nextTurn();
                }
            }
        }

        if (il != null && il.loaded && exitgame == null) { // transitions to main menu once images
            // are loaded in
            exitgame = new CircleButton(
                    "Exit", PANEL_WIDTH / 2 - 100, PANEL_HEIGHT - 70, 30, il.exit, 40, 40
            );
            techtree = new CircleButton(
                    "Tech Tree", PANEL_WIDTH / 2, PANEL_HEIGHT - 70, 30, il.science, 45, 45
            );
            endturn = new CircleButton(
                    "End Turn", PANEL_WIDTH / 2 + 100, PANEL_HEIGHT - 70, 30, il.ok, 45, 45
            );

            exittech = new CircleButton("ARROW", 40, 40, 20, null, 0, 0);
            exittech.direction = "BACK";
        }

        for (List l : stars) {
            double x = (double) l.get(0);
            double y = (double) l.get(1);
            x = x + (double) l.get(2) * 0.0001;
            y = y + (double) l.get(3) * 0.0001;
            x = Math.max(0, Math.min(1, x));
            y = Math.max(0, Math.min(1, y));

            l.set(0, x);
            l.set(1, y);

            if (x <= 0 || x >= 1) {
                double vx = (double) l.get(2);
                l.set(2, vx * -1 + 0.25 * (1 - 2 * Math.random()));
            }
            if (y <= 0 || y >= 1) {
                double vy = (double) l.get(3);
                l.set(3, vy * -1 + 0.25 * (1 - 2 * Math.random()));
            }
        }
        infobox.tick();
        GameAction ga = ACTIONS.peek();
        if (ga != null && ga.tick()) {
            if(ga instanceof BotEndTurn){
                nextTurn();
                CLIENT.send(new ServerPacket("end turn", null));
            }
            ACTIONS.remove();
        }

        // updates buildings
        for (GameTile gt : board) {
            if (gt.building != null) {
                gt.building.update();
            }
        }

        // calculates vision
        VISION.clear();
//        for(GameTile gt: board){
//            VISION.add(gt);
//        }
        for (GameTile gt : board) {
            if (gt.building != null && gt.building.name.equals("guard tower")) {
                int basev = 2;
                if (gt.terrain.equals("mountains"))
                    basev = 3;
                for (GameTile a : gt.BFS2(basev)) {
                    VISION.add(a);
                }
            } else {
                // you can see cities you own
                if (gt.village != null && gt.village.team == USER) {
                    for (GameTile a : gt.village.land) {
                        VISION.add(a);
                    }
                }
                // units have vision
                if (gt.unit != null && gt.unit.team == USER) {
                    int basev = 1;
                    if (gt.unit.stats().scout || gt.terrain.equals("mountains"))
                        basev = 2;
                    for (GameTile a : gt.BFS2(basev)) {
                        VISION.add(a);
                    }
                }
            }
        }
        // calculates income
        if (USER != null)
            USER.calcIncome();

        // updates wincon
        if (round >= 1) {
            Map<Tribe, Boolean> hasCity = new HashMap<>();
            for (GameTile gt : board) {
                if (gt.village != null) {
                    if (gt.village.team != null) {
                        if (!hasCity.containsKey(gt.village.team)
                                || !hasCity.get(gt.village.team)) {
                            hasCity.put(gt.village.team, gt.village.capital);
                        }
                    }
                }
            }
            for (Tribe t : players) {
                if (!hasCity.containsKey(t)) {
                    t.active = false;
                    for (GameTile gt : board) {
                        if (gt.unit != null && gt.unit.team == t) {
                            ACTIONS.add(new Death(gt.unit));
                            gt.unit = null;
                        }
                    }
                    if (!gameoutcome && t == USER) {
                        gameoutcome = true;
                        Object[] options = { "OK" };
                        JOptionPane.showOptionDialog(
                                null, "Your cities were captured! Press OK to spectate", "Defeat",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                                null, options, options[0]
                        );
                    }
                }
            }
            int nuniqcap = 0;
            for (boolean b : hasCity.values()) {
                if (b) {
                    nuniqcap++;
                }
            }
            if (nuniqcap <= 1) {
                for (Tribe t : hasCity.keySet()) {
                    if (!gameoutcome && hasCity.get(t) && t == USER) {
                        gameoutcome = true;
                        Object[] options = { "OK" };
                        JOptionPane.showOptionDialog(
                                null, "You've captured all capitals!", "Victory",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                                null, options, options[0]
                        );
                    } else if (!gameoutcome && !hasCity.get(t) && t == USER) {
                        gameoutcome = true;
                        Object[] options = { "OK" };
                        JOptionPane.showOptionDialog(
                                null, "Your capital was captured! Press OK to spectate", "Defeat",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                                null, options, options[0]
                        );
                    }
                }
            }
        }
    }

    public void draw(Graphics g) {
        if (il == null || fl == null)
            return; // imageloader and fontloader needs to be prepared

        if (board == null)
            return;
        // draws the background
        g.setColor(new Color(20, 20, 20));
        g.fillRect(-10, -10, 2 * PANEL_WIDTH, 2 * PANEL_HEIGHT);
        g.setColor(Color.WHITE);
        for (List l : stars) {
            g.drawOval(
                    (int) (((double) l.get(0)) * PANEL_WIDTH),
                    (int) (((double) l.get(1)) * PANEL_HEIGHT),
                    1, 1
            );
        }

        g.translate(PANEL_WIDTH / 2, PANEL_HEIGHT / 2);
        ((Graphics2D) g).scale(ZOOM, ZOOM);
        g.translate(CAMERA.x, CAMERA.y);

        for (GameTile gt : board) {
            gt.draw(g, il);
        }
        for (GameTile gt : board) {
            if (gt.village != null && gt.village.team != null) {
                gt.village.draw2(g, il);
            }
        }
        for (GameTile gt : board) {
            if (gt.selected) {
                if (gt.tileorunit) {
                    ((Graphics2D) g).setStroke(new BasicStroke(3));
                    g.setColor(new Color(100, 140, 255));
                    g.drawPolygon(gt.hitbox());
                    g.setColor(new Color(100, 140, 255, 50));
                    g.fillPolygon(gt.hitbox());
                } else {
                    ((Graphics2D) g).setStroke(new BasicStroke(2));
                    g.setColor(new Color(100, 140, 255, 50));
                    g.fillPolygon(gt.hitbox());
                }
                ((Graphics2D) g).setStroke(new BasicStroke(1));
            } else if (gt.unit != null) {
                if (gt.unit.team != USER) {
                    g.setColor(new Color(255, 100, 100, 50));
                    g.fillPolygon(gt.hitbox());
                }
//                } else if (gt.unit.movephase < 3) {
//                    g.setColor(new Color(180, 255, 100, 50));
//                    g.fillPolygon(gt.hitbox());
//                }
            }
        }
        for (GameTile gt : board) {
            gt.draw2(g, il);
        }

        for (GameTile gt : board) {
            if (gt.unit != null) {
                gt.unit.draw(g, il);
            }
        }

        if (ACTIONS.size() > 0) {
            ACTIONS.peek().draw(g, il);
        }

        // refogs area over units
        for (GameTile gt : board) {
            if (!VISION.contains(gt)) {
                gt.draw(g, il);
                ((Graphics2D) g).setStroke(new BasicStroke(3));
                // g.setColor(FOG);
                // g.drawPolygon(gt.hitbox);
                ((Graphics2D) g).setStroke(new BasicStroke(1));
            } else {
                if (gt.unit == null) {
                    gt.draw2(g, il);
                }
            }
        }

        for (GameTile gt : board) {
            if (gt.village != null && VISION.contains(gt)) {
                gt.village.draw4(g, il, fl);
            }
        }

        g.translate(-CAMERA.x, -CAMERA.y);
        ((Graphics2D) g).scale(1 / ZOOM, 1 / ZOOM);
        g.translate(-PANEL_WIDTH / 2, -PANEL_HEIGHT / 2);
        // draws income and generation
        g.setColor(new Color(20, 20, 20, 155));
        g.setFont(fl.head1);
        int stringwidth = g.getFontMetrics().stringWidth(USER.money + "  (+" + USER.income + ")");
        g.fillRect(PANEL_WIDTH / 2 - stringwidth / 2 - 3, 10, stringwidth + 30, 28);
        g.setColor(Color.WHITE);
        g.drawString(
                USER.money + "  (+" + USER.income + ")", PANEL_WIDTH / 2 - stringwidth / 2, 30
        );
        g.drawImage(il.star, PANEL_WIDTH / 2 + stringwidth / 2 + 5, 13, 20, 20, null);

        // adds the selected tile into the info box
        if(turn == USER.index) {
            boolean exists = false;
            for (GameTile gt : board) {
                if (gt.selected && (last_selected != gt || last_selected_b != gt.tileorunit)) {
                    infobox.updateTile(gt, gt.tileorunit);
                    last_selected = gt;
                    last_selected_b = gt.tileorunit;
                    exists = true;
                    break;
                } else if (gt.selected) {
                    exists = true;
                }
            }
            if (!exists) {
                infobox.updateTile(null, false);
            }
        }
        else{
            infobox.updateTile(null, false);
        }

        if (infobox.displaying() || ACTIONS.size() > 0) {
            infobox.draw(g, il, fl);
        } else {
            exitgame.draw(g, il, fl);
            techtree.draw(g, il, fl);
            if (turn == USER.index) {
                endturn.c = new Color(19, 210, 70, 230);
            } else {
                endturn.c = new Color(122, 174, 255, 230);
            }
            endturn.draw(g, il, fl);
        }

        poppers.draw(g,il,fl);

        g.setColor(Color.black);
        // g.drawString(String.valueOf(turn), 50, 50);

    }

    public void drawtech(Graphics g) {
        // draws background
        g.setColor(new Color(20, 20, 20));
        g.fillRect(-10, -10, 2 * PANEL_WIDTH, 2 * PANEL_HEIGHT);
        g.setColor(Color.WHITE);
        for (List l : stars) {
            g.drawOval(
                    (int) (((double) l.get(0)) * PANEL_WIDTH),
                    (int) (((double) l.get(1)) * PANEL_HEIGHT),
                    1, 1
            );
        }

        // draws tech tree
        g.translate(PANEL_WIDTH / 2, PANEL_HEIGHT / 2);
        ((Graphics2D) g).scale(ZOOM, ZOOM);
        g.translate(CAMERA.x, CAMERA.y);
        for (Tech t : USER.techtree) {
            t.draw(g, il, fl);
        }

        g.translate(-CAMERA.x, -CAMERA.y);
        ((Graphics2D) g).scale(1 / ZOOM, 1 / ZOOM);
        g.translate(-PANEL_WIDTH / 2, -PANEL_HEIGHT / 2);
        // draws back button
        exittech.draw(g, il, fl);

        poppers.draw(g,il,fl);
    }

    // TO CHANGE
    public void test_init(GameMap gm) {
        // TEST
        Stat s = new Stat("swordsman");

        Stat s2 = new Stat("rider");
        System.out.println(s2.DMGCalc(s));

        // Unit uwu = new Unit(s, null, players.get(0));
        // Unit owo = new Unit(s2, null, players.get(1));
        // List<Integer> lkj= new LinkedList<Integer>();
        // lkj.add(0);
        // lkj.add(0);
        // GameTile gt = gm.coods.get(lkj);
        // uwu.pos = gt;
        // gt.replaceUnit(uwu);
        // List<Integer> fgh= new LinkedList<Integer>();
        // fgh.add(1);
        // fgh.add(1);
        // GameTile gt2 = gm.coods.get(fgh);
        // owo.pos = gt2;
        // gt2.replaceUnit(owo);
    }

    class CustomComparator implements Comparator<GameTile> {
        @Override
        public int compare(GameTile a, GameTile b) {
            return a.center.y - b.center.y;
        }
    }
}
