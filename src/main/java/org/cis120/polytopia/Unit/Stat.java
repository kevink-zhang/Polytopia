package org.cis120.polytopia.Unit;

import org.cis120.polytopia.Tile.GameTile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Stat {
    public double atk;
    public double def;
    public double move;
    public int range;
    public int maxHP;
    public int curHP;

    public String type = ""; // what unit it is
    public int cost = -1; // cost of unit

    public boolean carry;
    public Unit carrydata;
    public boolean convert;
    public boolean dash;
    public boolean escape;
    public boolean sail; // renamed from float since... float is a keyword
    public boolean fortify;
    public boolean heal;
    public boolean persist;
    public boolean scout;

    public String proj = "none"; // projectile image path, if not specified then none

    public Stat(String troopname) {
        this("files/gamedata/unitdata/" + troopname.toLowerCase() + ".txt", true);
    }

    public Stat(String filepath, boolean doesnothing) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            try {
                type = br.readLine().split(":")[1];
                cost = Integer.parseInt(br.readLine().split(":")[1]);

                atk = Double.parseDouble(br.readLine().split(":")[1]);
                def = Double.parseDouble(br.readLine().split(":")[1]);
                move = Double.parseDouble(br.readLine().split(":")[1]);
                range = Integer.parseInt(br.readLine().split(":")[1]);
                curHP = maxHP = Integer.parseInt(br.readLine().split(":")[1]);

                carry = Boolean.parseBoolean(br.readLine().split(":")[1]);
                carrydata = null;
                convert = Boolean.parseBoolean(br.readLine().split(":")[1]);
                dash = Boolean.parseBoolean(br.readLine().split(":")[1]);
                escape = Boolean.parseBoolean(br.readLine().split(":")[1]);
                sail = Boolean.parseBoolean(br.readLine().split(":")[1]);
                fortify = Boolean.parseBoolean(br.readLine().split(":")[1]);
                heal = Boolean.parseBoolean(br.readLine().split(":")[1]);
                persist = Boolean.parseBoolean(br.readLine().split(":")[1]);
                scout = Boolean.parseBoolean(br.readLine().split(":")[1]);

                proj = br.readLine();
                br.close();
            } catch (IOException e) {
                System.out.println("error in reading unit data");
                atk = 1;
                def = 1;
                move = 1;
                range = 1;
                curHP = maxHP = 10;
                carry = false;
                carrydata = null;
                convert = false;
                dash = false;
                escape = false;
                sail = false;
                fortify = false;
                heal = false;
                persist = false;
                scout = false;
            }

        } catch (FileNotFoundException e) { // defaults to a dummy unit
            System.out.println("Unit data not found");
            atk = 1;
            def = 1;
            move = 1;
            range = 1;
            curHP = maxHP = 10;
            carry = false;
            carrydata = null;
            convert = false;
            dash = false;
            escape = false;
            sail = false;
            fortify = false;
            heal = false;
            persist = false;
            scout = false;
        }
    }

    public int hp() {
        return curHP;
    }

    public void damagehp(int a) {
        curHP -= a;
    }

    public int range() {
        return range;
    }

    public void updateTile(GameTile gt) {

    }

    public void promote() {
        maxHP += 5;
        curHP = maxHP;
    }

    public List<Double> DMGCalc(Stat s2) {
        List<Double> ret = new LinkedList<Double>();
        double effatk = ((double) curHP) / ((double) maxHP) * atk;
        double effdef = ((double) s2.curHP) / ((double) s2.maxHP) * s2.def;

        ret.add(4.5 * effatk *atk / (effatk + effdef));
        ret.add(4.5 * effdef *s2.def / (effatk + effdef));

        return ret;
    }
}
