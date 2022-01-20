package org.cis120.polytopia;

import org.cis120.polytopia.GameAction.Build;
import org.cis120.polytopia.GameAction.Death;
import org.cis120.polytopia.GameAction.Spawn;
import org.cis120.polytopia.GameAction.Upgrade;
import org.cis120.polytopia.Tile.Building;
import org.cis120.polytopia.Tile.GameTile;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.cis120.polytopia.GameBoard.VISION;
import static org.cis120.polytopia.GameBody.USER;
import static org.cis120.polytopia.GameBody.PANEL_HEIGHT;
import static org.cis120.polytopia.GameBody.PANEL_WIDTH;

public class InfoBox {
    public GameTile display;
    public GameTile target;
    public CircleButton close;
    public boolean toru; // tile or unit
    public boolean target2;
    public int drift;
    public List<CircleButton> actionlist = new LinkedList<>(); // list of action buttons

    int x;
    int y;
    int dx;
    int dy;
    int w;
    int h;

    public InfoBox(int posx, int posy, int deltax, int deltay, int width, int height) {
        display = null;
        target = null;
        toru = true;
        target2 = true;
        drift = 0;

        x = posx;
        y = posy;
        dx = deltax;
        dy = deltay;
        w = width;
        h = height;
    }

    public void updateTile(GameTile gt, boolean b) {
        target = gt;
        if (gt != null)
            target2 = b;
    }

    public void tick() {
        y = PANEL_HEIGHT;
        w = PANEL_WIDTH;
        // set settings to open if infobox is already closed
        if (display == null && target != null) {
            display = target;
            toru = target2;
        }
        // closing infobox with existing target
        if (display != target || toru != target2) {
            if (drift <= 0) {
                display = target;
                toru = target2;
            } else {
                drift -= 10;
            }
        }
        // opening infobox with new target
        else if (display != null &&
                (drift < 50 || (display.getActions(USER).size() > 0 && drift < 100))) {
            drift += 10;
        }
    }

    public boolean displaying() {
        return display != null;
    }

    public void draw(Graphics g, ImageLoader il, FontLoader fl) {
        int yp = y + (2 * dy * drift / 100);

        if (display != null) {
            g.setColor(new Color(24, 24, 24, 210));
            g.fillRect(x + (dx * drift / 100), yp, w, h);

            List<String> gameactions = display.getActions(USER);
            Map<String, Boolean> ttree = USER.getTech();
            close = new CircleButton("ARROW", PANEL_WIDTH - 40, yp + 40, 20, null, 0, 0);
            close.direction = "DOWN";

            display.draw3(g, il, new Point(x + 60, yp + 40));
            close.draw(g, il, fl);
            g.setFont(fl.head1);
            g.setColor(Color.WHITE);
            if (!VISION.contains(display)) {
                g.drawString("Clouds", x + 110, yp + 35);
                g.setFont(fl.text1);
                g.drawString("The Fog of War...   Move closer to gain vision", x + 110, yp + 55);
            } else if (display.unit != null && !toru) {
                display.unit.draw(g, il, new Point(x + 60, yp + 40));
                g.drawString(
                        display.unit.stats().type.substring(0, 1).toUpperCase() +
                                display.unit.stats().type.substring(1),
                        x + 110, yp + 35
                );
                if (display.unit.team != USER) {
                    g.setFont(fl.text1);
                    g.drawString("This is an enemy!", x + 110, yp + 55);
                }
            } else {
                if (display.village != null) {
                    if (display.village.team == null) {
                        g.drawString("Villages", x + 110, yp + 35);
                    } else {
                        g.drawString("The city of " + display.village.name, x + 110, yp + 35);
                    }
                } else if (display.resource != null && USER == display.team) {
                    String helperhint = "";
                    String titletext = "";

                    if(display.terrain.equals("land")) {
                        titletext = "Land";
                        if(display.resource.equals("fruit")){
                            titletext = "Fruit";
                            if(!ttree.get("organization")){
                                helperhint = "Research organization to harvest fruit";
                            }
                            else{
                                helperhint = "Harvest fruit to grow your city";
                            }
                        }
                        else if((ttree.get("organization") || ttree.get("farming")) && display.resource.equals("crop")){
                            titletext = "Crop";
                            if(!ttree.get("farming")){
                                helperhint = "Research farming to build farms";
                            }
                            else{
                                helperhint = "Build farms to grow your city";
                            }
                        }
                    }
                    else if(display.terrain.equals("water")){
                        titletext = "Water";
                        if(display.resource.equals("fish")){
                            titletext = "Fish";
                            if(!ttree.get("fishing")){
                                helperhint = "Research fishing to fish";
                            }
                            else{
                                helperhint = "Fish to grow your city";
                            }
                        }
                        if(!ttree.get("sailing")){
                            helperhint = "Research sailing to move on water";
                        }
                    }
                    else if(display.terrain.equals("ocean")){
                        titletext = "Ocean";
                        if((ttree.get("fishing")||ttree.get("whaling"))&& display.resource.equals("whale")){
                            titletext = "Whale";
                            if(!ttree.get("whaling")){
                                helperhint = "Research whaling to whale";
                            }
                            else{
                                helperhint = "Whale for additional stars";
                            }
                        }
                        else {
                            helperhint = "Research navigation to move on water";
                        }
                    }
                    else if(display.terrain.equals("forest")){
                        titletext = "Forest";
                        if(display.resource.equals("animals")){
                            titletext = "Animals";
                            if(!ttree.get("hunting")){
                                helperhint = "Research hunting to hunt animals";
                            }
                            else{
                                helperhint = "Hunt animals to grow your city";
                            }
                        }
                        else{
                            if(!ttree.get("forestry")){
                                helperhint = "Research forestry to build lumber huts";
                            }
                            else{
                                helperhint = "Build lumber huts to grow your city";
                            }
                        }
                    }
                    else if(display.terrain.equals("mountains")){
                        titletext = "Mountains";
                        if((ttree.get("climbing") || ttree.get("mining")) && display.resource.equals("ore")){
                            titletext = "Ore";
                            if(!ttree.get("mining")){
                                helperhint = "Research mining to mine ore";
                            }
                            else{
                                helperhint = "Mine ore to grow your city";
                            }
                        }
                        if(!ttree.get("climbing")){
                            helperhint = "Research climbing to move on mountains";
                        }
                    }

                    if(display.unit!=null && display.unit.team!=USER){
                        helperhint = "This is an enemy! Actions are blocked on this tile";
                    }

                    g.setFont(fl.head1);
                    g.drawString(
                            titletext,
                            x + 110, yp + 35
                    );
                    g.setFont(fl.text1);
                    g.drawString(helperhint, x + 110, yp + 55);
                } else {
                    g.drawString(
                            display.terrain.substring(0, 1).toUpperCase() +
                                    display.terrain.substring(1),
                            x + 110, yp + 35
                    );
                }
            }

            // draws action buttons
            g.setColor(new Color(24, 24, 24));
            g.fillRect(x + (dx * drift / 100), yp - dy, w, h);

            actionlist.clear();
            Point initp = new Point(60, y + (2 * dy * drift / 100) - dy + 40);
            for (String s : display.getActions(USER)) {
                try {
                    //cost function
                    int ccc = -1;
                    if (s.equals("hunting")) {
                        ccc = 3;
                    } else if (s.equals("archer")) {
                        ccc = 3;
                    } else if (s.equals("grow forest")) {
                        ccc = 5;
                    } else if (s.equals("forest temple")) {
                        ccc = 30;
                    } else if (s.equals("lumber hut")) {
                        ccc = 3;
                    } else if (s.equals("catapult")) {
                        ccc = 8;
                    } else if (s.equals("sawmill")) {
                        ccc = 5;
                    } else if (s.equals("rider")) {
                        ccc = 3;
                    } else if (s.equals("roads")) {
                        ccc = 2;
                    } else if (s.equals("customs house")) {
                        ccc = 5;
                    } else if (s.equals("temple")) {
                        ccc = 30;
                    } else if (s.equals("clear forest")) {
                        ccc = -1;
                    } else if (s.equals("retire")) {
                        ccc = -1;
                    } else if (s.equals("knight")) {
                        ccc = 10;
                    } else if (s.equals("burn forest")) {
                        ccc = 2;
                    } else if (s.equals("harvest")) {
                        ccc = 3;
                    } else if (s.equals("farm")) {
                        ccc = 5;
                    } else if (s.equals("windmill")) {
                        ccc = 5;
                    } else if (s.equals("destroy")) {
                        ccc = -1;
                    } else if (s.equals("defender")) {
                        ccc = 3;
                    } else if (s.equals("guard tower")) {
                        ccc = 5;
                    } else if (s.equals("mine")) {
                        ccc = 5;
                    } else if (s.equals("swordsman")) {
                        ccc = 5;
                    } else if (s.equals("forge")) {
                        ccc = 5;
                    } else if (s.equals("mountain temple") ) {
                        ccc = 30;
                    } else if (s.equals("mind bender")) {
                        ccc = 5;
                    } else if (s.equals("fish")) {
                        ccc = 3;
                    } else if (s.equals("ship")) {
                        ccc = 5;
                    } else if (s.equals("port")) {
                        ccc = 10;
                    } else if (s.equals("battleship")) {
                        ccc = 15;
                    } else if (s.equals("whale")) {
                        ccc = -1;
                    } else if (s.equals("water temple")) {
                        ccc = 30;
                    } else if (s.equals("warrior")) {
                        ccc = 2;
                    } else if (s.equals("capture")) {
                        ccc = -1;
                    } else if (s.equals("heal")) {
                        ccc = -1;
                    }

                    actionlist.add(
                            new CircleButton(
                                    s, initp.x, initp.y, 25, il.getTech(s), 30,
                                    30 * il.getTech(s).getHeight(null)
                                            / il.getTech(s).getWidth(null)
                            )
                    );
                    actionlist.get(actionlist.size()-1).cost = ccc;
                    initp.x += 60;
                } catch (Exception e) {
                    System.out.println(s);
                }

            }
            for (CircleButton cb : actionlist) {
                cb.draw(g, il, fl);
            }
        } else {
            g.setColor(new Color(24, 24, 24, 210));
            g.fillRect(x + (dx * drift / 100), yp, w, h);
            close = null;
            actionlist.clear();
        }
    }
}
