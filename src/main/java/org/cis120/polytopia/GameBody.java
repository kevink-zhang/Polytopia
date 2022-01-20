package org.cis120.polytopia;

import org.cis120.polytopia.GameAction.*;
import org.cis120.polytopia.Server.Client;
import org.cis120.polytopia.Server.Server;
import org.cis120.polytopia.Server.ServerPacket;
import org.cis120.polytopia.Tile.Building;
import org.cis120.polytopia.Tile.GameMap;
import org.cis120.polytopia.Tile.GameTile;
import org.cis120.polytopia.Tile.Tribe;
import org.cis120.polytopia.Unit.Unit;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class GameBody extends JPanel {
    public static GameBoard GAME;

    public static Server SERVER;
    public static Client CLIENT;

    public static String JOINCODE = "";
    public static String incorrectjoin = null;

    public static int GAMESIZE;

    public static Tribe USER; // TO CHANGE, initialized in run needs to be removed

    public static String USERNAME = "User " + String.valueOf(Math.random()).substring(2, 5);
    public static String TRIBE = "Imperius";
    public final static String IP = new Server().getIP();
    public final static String ID = String.valueOf(Math.random());

    // the state of the game logic
    // private JLabel status; // Current status text, i.e. "Running..."

    // scenes and loading
    public static String scene = "loading";
    private static int transtime = 0;
    private static String newscene = "loading";
    private boolean rulesout = false;

    private Button singleplayer;
    private CircleButton customize;
    private Map<String, CircleButton> tribe_picker = new HashMap<>();
    private Button multiplayer;

    private Button joingame;
    private Button joinsubmit;

    private Button hostgame;
    private CircleButton menu_back;

    private List<Button> people;
    private Button startgame;

    private PopUp poppers = null;

    // Game constants
    public static int PANEL_WIDTH = 1000;
    public static int PANEL_HEIGHT = 550;
    public static ImageLoader il;
    public static FontLoader fl = new FontLoader();

    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 25;

    // DEBUGGING TO CHANGE
    Point mousedebugger = new Point(0, 0);

    public GameBody(JPanel foo) {
        // initializes the game
        GAME = new GameBoard();
        //GAME.test_init(null);

        // initializes buttons
        singleplayer = new Button("SINGLEPLAYER", 0, 0, 250, 50);
        multiplayer = new Button("PLAY", 0, 100, 150, 50);

        hostgame = new Button("HOST GAME", 0, 0, 250, 50);
        joingame = new Button("JOIN GAME", 0, 100, 250, 50);
        joinsubmit = new Button(">", 0, 0, 50, 50);
        menu_back = new CircleButton("ARROW", 40, 40, 20, null, 0, 0);
        menu_back.direction = "BACK";

        // creates a new timer
        java.util.Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            @Override
            public void run() {
                tick();
                if (SERVER != null) {
                    SERVER.tick();
                }
            }
        };
        timer.schedule(timertask, 0, INTERVAL);

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        // This key listener allows the square to move as long as an arrow key
        // is pressed, by changing the square's velocity accordingly. (The tick
        // method below actually moves the square.)
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                char c = e.getKeyChar();
                if (scene.equals("input join")) {
                    if (JOINCODE.length() > 0 && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        JOINCODE = JOINCODE.substring(0, JOINCODE.length() - 1);
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        joinGame(JOINCODE.replace("-", "."));
                    } else if (JOINCODE.length() < 20 && e.getKeyCode() != KeyEvent.VK_BACK_SPACE
                            && e.getKeyCode() != KeyEvent.VK_SPACE) {
                        JOINCODE += c;
                    }
                }
            }

            public void keyReleased(KeyEvent e) {

            }

        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out
                        .println(new Point(e.getX() - mousedebugger.x, e.getY() - mousedebugger.y));
                mousedebugger = e.getPoint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (poppers != null) {
                    if (poppers.OK(e.getPoint())) {

                    } else if (poppers.CLOSE(e.getPoint())) {
                        poppers = null;
                    }
                }
                // if (scene.equals("main menu")) {
                //
                // } else if (scene.equals("multiplayer")) {
                //
                // } else if (scene.equals("create game")) {
                //
                // } else if (scene.equals("lobby")) {

                if (scene.equals("in game") || scene.equals("tech tree")) {
                    GAME.mousePressed(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (scene.equals("main menu")) {
                    // if(singleplayer.click(e.getPoint())){
                    //
                    // }
                    if (customize.click(e.getPoint())) {
                        scene = "customize";
                    }
                    if (multiplayer.click(e.getPoint())) {
                        // transtime = 2000/INTERVAL;
                        scene = "multiplayer";
                    }
                } else if (scene.equals("customize")) {
                    if (menu_back.click(e.getPoint())) {
                        scene = "main menu";
                    }
                    for (String key : tribe_picker.keySet()) {
                        CircleButton cb = tribe_picker.get(key);
                        if (cb.click(e.getPoint())) {
                            tribe_picker.get(TRIBE).c = new Color(122, 174, 255, 230);
                            cb.c = new Color(19, 210, 70, 230);
                            TRIBE = key;
                            return;
                        }
                    }
                } else if (scene.equals("multiplayer")) {
                    if (hostgame.click(e.getPoint())) {
                        hostNewGame();
                        scene = "create game";
                    }
                    if (joingame.click(e.getPoint())) {
                        JOINCODE = "";
                        incorrectjoin = null;
                        scene = "input join";
                    }
                    if (menu_back.click(e.getPoint())) {
                        scene = "main menu";
                    }
                } else if (scene.equals("create game")) {
                    if (startgame.click(e.getPoint())) {
                        startNewGame();
                    }
                    if (menu_back.click(e.getPoint())) {
                        leaveGame();
                        scene = "multiplayer";
                    }
                } else if (scene.equals("input join")) {
                    if (menu_back.click(e.getPoint())) {
                        scene = "multiplayer";
                    }
                    if (joinsubmit.click(e.getPoint())) {
                        joinGame(JOINCODE.replace("-", "."));
                    }
                } else if (scene.equals("lobby")) {
                    if (menu_back.click(e.getPoint())) {
                        leaveGame();
                        scene = "multiplayer";
                    }
                } else if (scene.equals("in game")) {
                    GAME.mouseReleased(e);
                } else if (scene.equals("tech tree")) {
                    GAME.techReleased(e);
                }
            }

            public void mouseEntered(MouseEvent e) {

            }

            public void mouseExited(MouseEvent e) {

            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                GAME.mouseDragged(e);
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                GAME.mouseWheelMoved(e);
            }
        });

    }

    /*
     * tick function
     */
    void tick() {
        PANEL_WIDTH = getWidth();
        PANEL_HEIGHT = getHeight();
        if (scene.equals("main menu") && !rulesout) {
            rulesout = true;
            Object[] options = { "OK" };
            JOptionPane.showOptionDialog(
                    null, "Polytopia is " +
                            "a turn-based multiplayer strategy game.\n" +
                            "Upgrade your economy, train troops, and " +
                            "fight enemies to conquer The Square!\n"
                            +
                            "Win by capturing all enemy capitals.\n" +
                            "The full ruleset can be found at\n" +
                            "https://polytopia.fandom.com/wiki" +
                            "/The_Battle_of_Polytopia_Wiki\n",
                    "Welcome!",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]
            );
        }
        // if (scene.equals("main menu")) {
        //
        // } else if (scene.equals("create game")) {
        //
        // } else if (scene.equals("lobby")) {
        //
        // } else if (scene.equals("in game")) {
        //
        // }
        GAME.tick();
        repaint();
        if (il != null && !il.loaded) { // transitions to main menu once images are loaded in
            il.load();
            transtime = 2000 / INTERVAL;
            newscene = "main menu";
            customize = new CircleButton(
                    "", PANEL_WIDTH / 2, PANEL_HEIGHT / 2 - 50, 50, il.getHead(TRIBE), 70, 70
            );
            String[] tribes = { "Ai-Mo", "Bardur", "Hoodrick", "Imperius",
                "Kickoo", "Luxidoor", "Oumaji", "Quetzali",
                "Vengir", "Xin-xi", "Yadakk", "Zebasi" };
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    tribe_picker.put(
                            tribes[4 * j + i],
                            new CircleButton(
                                    tribes[4 * j + i], PANEL_WIDTH / 2 - 150 + 100 * i,
                                    200 + 120 * j, 40,
                                    il.getHead(tribes[4 * j + i]), 56, 56
                            )
                    );
                }
            }
            tribe_picker.get(TRIBE).c = new Color(19, 210, 70, 230);
        }
    }

    // SERVER THINGS

    /*
     * reads server input serverpackets
     */
    public static void PARSE_SERVER(ServerPacket sp) {
        if (sp.type.equals("action")) {
            Map<String, Object> information = (Map<String, Object>) sp.information;
            if (information.get("type").equals("attack")) {
                Object gtid1 = new Pair().fromString(information.get("origin"));
                Object gtid2 = new Pair().fromString(information.get("target"));
                double damage = (double) information.get("damage");
                GAME.ACTIONS.add(
                        new Attack(
                                GAME.gamemap.coods.get(gtid1).unit,
                                GAME.gamemap.coods.get(gtid2).unit, damage
                        )
                );
            } else if (information.get("type").equals("move")) {
                Unit tomove = GAME.gamemap.coods
                        .get(new Pair().fromString(information.get("self"))).unit;
                List<Object> tileids = (List<Object>) information.get("path");
                List<GameTile> parsedpath = new LinkedList<>();
                for (Object tid : tileids) {
                    parsedpath.add(GAME.gamemap.coods.get(new Pair().fromString(tid)));
                }
                GAME.ACTIONS.add(new Move(tomove, parsedpath));
            } else if (information.get("type").equals("death")) {
                Unit todie = GAME.gamemap.coods
                        .get(new Pair().fromString(information.get("self"))).unit;
                GAME.ACTIONS.add(new Death(todie));
            } else if (information.get("type").equals("spawn")) {
                GameTile ooc = GAME.gamemap.coods
                        .get(new Pair().fromString(information.get("origin")));
                GAME.ACTIONS.add(new Spawn((String) information.get("troop"), ooc));
            } else if (information.get("type").equals("upgrade")) {
                Unit toup = GAME.gamemap.coods
                        .get(new Pair().fromString(information.get("self"))).unit;
                boolean toheal = (boolean) information.get("heal");
                System.out.println(toheal);
                String tonewunit = (String) information.get("new unit");
                GAME.ACTIONS.add(new Upgrade(toup, toheal, tonewunit));
            } else if (information.get("type").equals("build")) {
                GameTile ooc = GAME.gamemap.coods
                        .get(new Pair().fromString(information.get("self")));
                String trn = (String) information.get("terrain");
                String rsc = (String) information.get("resource");
                String bld = (String) information.get("building");
                // sets terrain and resources to be the same
                ooc.terrain = trn;
                ooc.resource = rsc;
                if (ooc.building != null && bld.equals("")) {// destroy
                    ooc.building.destroy();
                    ooc.building = null;
                } else if (ooc.building == null && !bld.equals("")) { // build
                    ooc.building = new Building(bld, ooc);
                }
                if (ooc.building != null) {
                    ooc.building.update();
                }

                if (information.get("village").equals(true)) {
                    if (!information.get("team").equals("")) { // checks if village was switched
                        Tribe tttt = null;
                        for (Tribe foo : GAME.players) {
                            if (foo.userid.equals(information.get("team"))) {
                                tttt = foo;
                                break;
                            }
                        }
                        if (tttt != null) {
                            ooc.city.convert(tttt);
                        }
                    }
                }
            } else if (information.get("type").equals("popchange")) {
                GameTile ooc = GAME.gamemap.coods
                        .get(new Pair().fromString(information.get("self")));
                ooc.city.addPopulation((int) ((double) information.get("pop")), false);
            }

        } else if (sp.type.equals("end turn")) {
            GAME.nextTurn();
        } else if (sp.type.equals("init")) {
            GAME.decodeSpawn((List<Object>) sp.information);
            transtime = 2000 / INTERVAL;
            newscene = "in game";
        } else if (sp.type.equals("log out")) {
            for (Tribe t : GAME.players) {
                if (t.userid.equals((sp.information))) {
                    t.active = false;
                }
            }
        } else if (sp.type.equals("end game")) {
            if (CLIENT != null) {
                CLIENT.stop();
            }
            CLIENT = null;
            scene = "multiplayer";
        } else if (sp.type.equals("confirm")) {
            System.out.println("confirming with server...");
            List<String> l = new LinkedList<>();
            l.add(USERNAME);
            l.add(TRIBE);
            l.add(ID);
            CLIENT.send(new ServerPacket("user info", l));
            if (SERVER == null) {
                scene = "lobby";
            } else {
                scene = "create game";
            }
        }
    }

    /*
     * Creates a new game
     */
    public void hostNewGame() {
        if (SERVER != null || CLIENT != null) { // already hosting or in a game you dingus
            return;
        }
        SERVER = new Server(6666);
        joinGame("localhost");
    }

    /*
     * Starts the game
     */
    public void startNewGame() {
        if (SERVER == null || CLIENT == null) {
            return; // you aren't running a server
        }

        System.out.println(SERVER.playerinfo.size());

        for (List<String> l : SERVER.playerinfo) {
            if (il.light) {
                il.light_start = false;
                il.light_end = false;
                while (!il.light_end) {
                    if (!il.light_start) {
                        System.out.println("call?");
                        il.reload(l.get(1));
                    }
                }
            }
        }

        for (List<String> l : SERVER.playerinfo) {
            if (l.size() == 3) {
                GAME.addPlayer(l.get(0), l.get(1), l.get(2));
            }
        }
        //GAME.addBot("hard", "Imperius");

        GameMap gm = new GameMap(GAME.players);
        GAMESIZE = 50;//(1 + GAME.players.size() / 2) * 120;
        gm.hex_init(GAMESIZE); // TO CHANGE
        gm.hex_terrain();

        GAME.createSpawn(gm);

        SERVER.acceptnew = false;
        CLIENT.send(new ServerPacket("init", GAME.encodeSpawn()));

        // transitions host to in game
        transtime = 2000 / INTERVAL;
        newscene = "in game";

    }

    /*
     * Joins a new game
     */
    public void joinGame(String ip) {
        if (CLIENT != null) {
            return; // already in a game you dingus
        }
        try {
            CLIENT = new Client(ip);
            CLIENT.run();
        } catch (Exception e) {
            System.out.println("join failed!");
            incorrectjoin = JOINCODE;
            try {
                if (CLIENT != null) {
                    CLIENT.close();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            e.printStackTrace();
            CLIENT = null;
        }
    }

    /*
     * Leaves game
     */
    public static void leaveGame() {
        if (CLIENT == null) {
            return; // no client
        }
        if (SERVER != null) {
            CLIENT.send(new ServerPacket("end game", null));
            SERVER.close();
            SERVER = null;
        } else {
            CLIENT.send(new ServerPacket("log out", ID));
        }
        CLIENT.interrupt();
        CLIENT.close();
        CLIENT = null;
        scene = "main menu";
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // transition screen update
        if (transtime == 1000 / INTERVAL) {
            scene = newscene;
        }
        if (transtime > 0) {
            transtime--;
        }

        if (scene.equals("loading")) {
            try {
                g.drawImage(
                        ImageIO.read(new File("files/assets/loading.jpeg")),
                        0, 0, PANEL_WIDTH, PANEL_HEIGHT, null
                );
            } catch (IOException e) {
                System.out.println("Missing assets");
            }
        } else if (scene.equals("main menu")) {
            g.drawImage(
                    il.background,
                    0, -2 * PANEL_HEIGHT / 3, PANEL_WIDTH, 3 * PANEL_HEIGHT, null
            );
            g.setFont(fl.title1);
            g.setColor(Color.WHITE);
            int stringwidth = g.getFontMetrics().stringWidth("HOME") / 2;
            g.drawString("HOME", PANEL_WIDTH / 2 - stringwidth, 120);
            // singleplayer.draw(g,il,fl);
            customize.icon = il.getHead(TRIBE);
            customize.draw(g, il, fl);
            multiplayer.draw(g, il, fl);
        } else if (scene.equals("customize")) {
            g.drawImage(
                    il.background,
                    0, -2 * PANEL_HEIGHT / 3, PANEL_WIDTH, 3 * PANEL_HEIGHT, null
            );

            g.setFont(fl.title1);
            g.setColor(Color.WHITE);
            int stringwidth = g.getFontMetrics().stringWidth("CHOOSE A TRIBE") / 2;
            g.drawString("CHOOSE A TRIBE", PANEL_WIDTH / 2 - stringwidth, 120);

            for (CircleButton cb : tribe_picker.values()) {
                cb.draw(g, il, fl);
            }

            menu_back.draw(g, il, fl);
        } else if (scene.equals("multiplayer")) {
            g.drawImage(
                    il.background,
                    0, -2 * PANEL_HEIGHT / 3, PANEL_WIDTH, 3 * PANEL_HEIGHT, null
            );

            g.setFont(fl.title1);
            g.setColor(Color.WHITE);
            int stringwidth = g.getFontMetrics().stringWidth("MULTIPLAYER") / 2;
            g.drawString("MULTIPLAYER", PANEL_WIDTH / 2 - stringwidth, 120);
            hostgame.draw(g, il, fl);
            joingame.draw(g, il, fl);
            menu_back.draw(g, il, fl);
        } else if (scene.equals("create game")) {
            g.drawImage(
                    il.background,
                    0, -2 * PANEL_HEIGHT / 3, PANEL_WIDTH, 3 * PANEL_HEIGHT, null
            );

            g.setFont(fl.title1);
            g.setColor(Color.WHITE);
            int stringwidth = g.getFontMetrics().stringWidth("LOBBY") / 2;
            g.drawString("LOBBY", PANEL_WIDTH / 2 - stringwidth, 120);

            g.setFont(fl.head2);
            stringwidth = g.getFontMetrics().stringWidth("JOIN CODE: " + IP.replace(".", "-")) / 2;
            g.drawString("JOIN CODE: " + IP.replace(".", "-"), PANEL_WIDTH / 2 - stringwidth, 170);

            people = new LinkedList<>();
            int index = 0;
            for (List<String> l : SERVER.playerinfo) {
                people.add(new Button(l.get(0), 0, 250 + index * 80 - PANEL_HEIGHT / 2, 300, 60));
                index++;
            }
            for (Button b : people) {
                b.draw(g, il, fl);
            }
            startgame = new Button("START GAME", 0, 270 + index * 80 - PANEL_HEIGHT / 2, 300, 60);
            startgame.draw(g, il, fl);

            menu_back.draw(g, il, fl);
        } else if (scene.equals("input join")) {
            g.drawImage(
                    il.background,
                    0, -2 * PANEL_HEIGHT / 3, PANEL_WIDTH, 3 * PANEL_HEIGHT, null
            );

            g.setFont(fl.title1);
            g.setColor(Color.WHITE);
            int stringwidth = g.getFontMetrics().stringWidth("JOIN A GAME") / 2;
            g.drawString("JOIN A GAME", PANEL_WIDTH / 2 - stringwidth, 120);

            if (incorrectjoin != null && incorrectjoin.equals(JOINCODE)) {
                g.setColor(Color.RED);
            }
            ((Graphics2D) g).drawRoundRect(
                    PANEL_WIDTH / 2 - stringwidth, 200, stringwidth * 2 - 60, 50, 20, 20
            );
            joinsubmit.pos.x = stringwidth - 50 + 25;
            joinsubmit.pos.y = -PANEL_HEIGHT / 2 + 200 + 25;

            g.setFont(fl.head1);
            if (JOINCODE.length() <= 0) {
                g.drawString("Enter host code here", PANEL_WIDTH / 2 - stringwidth + 10, 240);
            } else {
                g.drawString(JOINCODE, PANEL_WIDTH / 2 - stringwidth + 10, 240);
            }

            joinsubmit.draw(g, il, fl);

            menu_back.draw(g, il, fl);
        } else if (scene.equals("lobby")) {
            g.drawImage(
                    il.background,
                    0, -2 * PANEL_HEIGHT / 3, PANEL_WIDTH, 3 * PANEL_HEIGHT, null
            );

            g.setFont(fl.title1);
            g.setColor(Color.WHITE);
            int stringwidth = g.getFontMetrics().stringWidth("LOBBY") / 2;
            g.drawString("LOBBY", PANEL_WIDTH / 2 - stringwidth, 120);

            g.setFont(fl.head2);
            stringwidth = g.getFontMetrics().stringWidth("WAITING FOR HOST TO START GAME") / 2;
            g.drawString("WAITING FOR HOST TO START GAME", PANEL_WIDTH / 2 - stringwidth, 250);

            menu_back.draw(g, il, fl);
        } else if (scene.equals("in game")) {
            GAME.draw(g);
        } else if (scene.equals("tech tree")) {
            GAME.drawtech(g);
        }

        // loading white screen

        float opa = Math.abs(((float) transtime * INTERVAL) / 1000 - 1);
        g.setColor(new Color(1, 1, 1, 1 - opa));
        g.fillRect(-10, -10, PANEL_WIDTH * 2, PANEL_HEIGHT * 2);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1000, 550);
    }

    public void run() {
        TimeUnit time = TimeUnit.SECONDS;
        try {
            time.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        il = new ImageLoader();
    }

    class CustomComparator implements Comparator<GameTile> {
        @Override
        public int compare(GameTile a, GameTile b) {
            return a.center.y - b.center.y;
        }
    }
}
