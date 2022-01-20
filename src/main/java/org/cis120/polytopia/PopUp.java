package org.cis120.polytopia;

import org.cis120.polytopia.Tile.GameTile;

import java.awt.*;

import static org.cis120.polytopia.GameBody.PANEL_HEIGHT;
import static org.cis120.polytopia.GameBody.PANEL_WIDTH;

public class PopUp {
    String title;
    GameTile tile;
    String body;

    public boolean show = false;
    public boolean single = false;

    Button YES = new Button("OK", 225, 100, 100, 40);
    Button NO = new Button("BACK", -225, 100, 100, 40);

    public PopUp(String t, String b) {
        title = t;
        body = b;
        show = false;
    }

    public boolean OK(Point p) {
        if(YES.click(p)){
            show = false;
            return true;
        }
        return false;
    }

    public boolean CLOSE(Point p) {
        if(NO.click(p)){
            show = false;
            return true;
        }
        return false;
    }

    public void draw(Graphics g, ImageLoader il, FontLoader fl) {
        if(!show) return;

        g.setColor(new Color(30, 30, 30, 240));
        g.fillRoundRect(PANEL_WIDTH/2-300, PANEL_HEIGHT/2-150, 600, 300, 10, 10);

        g.setColor(Color.white);
        g.setFont(fl.head2);
        g.drawString(title, PANEL_WIDTH/2-300+20,PANEL_HEIGHT/2-150+35);

        String todraw = "";
        int h = 0;
        g.setFont(fl.text1);
        for(String s: body.split(" ")){
            if(g.getFontMetrics().stringWidth(todraw + " " + s) > 400){
                g.drawString(todraw, PANEL_WIDTH/2-300+22, PANEL_HEIGHT/2-150+80+h);
                h+= 30;
                todraw = s;
            }
            else{
                if(todraw.equals("")){
                    todraw+= s;
                }
                else {
                    todraw += " " + s;
                }
            }
        }
        g.drawString(todraw, PANEL_WIDTH/2-300+22, PANEL_HEIGHT/2-150+80+h);

        YES.draw(g, il, fl);
        NO.draw(g, il, fl);
    }
}
