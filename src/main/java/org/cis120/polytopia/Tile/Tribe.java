package org.cis120.polytopia.Tile;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static org.cis120.polytopia.GameBody.il;

public class Tribe {
    public final String user;
    public final String userid;
    public int index = 0;
    public boolean active = true;
    public String tribe = "Imperius";
    public List<Tech> techtree = new LinkedList<>();

    public Color color;
    public Image head;
    public Color landc;
    public Color mountainc;
    public int money = 0;

    public int income = 0;
    public Set<City> cities = new HashSet<>();

    public String initunit = "Warrior"; // initial unit
    public int initpop = 0; // initial population

    public Map<String, Double> spawnmods = new HashMap<>();

    public Tribe(String tribename, String u, String id, Color c) {
        user = u;
        userid = id;
        color = c;
        head = il.getHead(tribename);
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("files/gamedata/tribedata/" + tribename + ".txt")
            );
            try {
                tribe = br.readLine();
                String starttech = br.readLine().split(":")[1];
                if (br.readLine().split(":")[1].equals("default")) {
                    BufferedReader br2 = new BufferedReader(
                            new FileReader("files/gamedata/tribedata/tech tree.txt")
                    );

                    String nline = br2.readLine();
                    String iconline = br2.readLine();
                    Tech roottech = new Tech(tribe, 0, true);
                    roottech.hitbox.icon = head;
                    techtree.add(roottech);
                    Tech[] hier = { roottech, null, null, null, null };
                    while (nline != null) {
                        // System.out.println(nline);
                        int tt = nline.split(":")[0].length() + 1;
                        String tname = nline.split(":")[1];

                        Tech t = new Tech(tname, tt, tname.equals(starttech));
                        for (String tttt : iconline.split(",")) {
                            t.techicons.add(tttt);
                        }

                        hier[tt] = t;

                        t.addParent(hier[tt - 1]);
                        hier[tt - 1].addChild(t);

                        techtree.add(t);

                        nline = br2.readLine();
                        iconline = br2.readLine();
                    }
                    br2.close();

                    roottech.addPos(new Point(0, 0));

                    int a1 = 210;

                    for (Tech t : techtree) {
                        if (t.tier == 1) {
                            t.addPos(
                                    new Point(
                                            (int) (1 * 100 * Math.cos(Math.toRadians(a1))),
                                            (int) (100 * Math.sin(Math.toRadians(a1)))
                                    )
                            );
                            a1 += 72;
                        }
                    }
                    a1 = 192;
                    for (Tech t : techtree) {
                        if (t.tier == 2) {
                            t.addPos(
                                    new Point(
                                            (int) (1 * 200 * Math.cos(Math.toRadians(a1))),
                                            (int) (200 * Math.sin(Math.toRadians(a1)))
                                    )
                            );
                            a1 += 36;
                        }
                    }
                    a1 = 192;
                    for (Tech t : techtree) {
                        if (t.tier == 3) {
                            t.addPos(
                                    new Point(
                                            (int) (1 * 300 * Math.cos(Math.toRadians(a1))),
                                            (int) (300 * Math.sin(Math.toRadians(a1)))
                                    )
                            );
                            a1 += 36;
                        }
                    }
                } else {
                    // special tribes not implemented
                }

                money = Integer.parseInt(br.readLine().split(":")[1]);
                initunit = br.readLine().split(":")[1];
                initpop = Integer.parseInt(br.readLine().split(":")[1]);

                spawnmods.put("fruit", Double.parseDouble(br.readLine().split(":")[1]));
                spawnmods.put("crop", Double.parseDouble(br.readLine().split(":")[1]));
                spawnmods.put("forest", Double.parseDouble(br.readLine().split(":")[1]));
                spawnmods.put("animal", Double.parseDouble(br.readLine().split(":")[1]));
                spawnmods.put("mountains", Double.parseDouble(br.readLine().split(":")[1]));
                spawnmods.put("ore", Double.parseDouble(br.readLine().split(":")[1]));
                spawnmods.put("fish", Double.parseDouble(br.readLine().split(":")[1]));

                String sssss = br.readLine();
                landc = new Color(
                        Integer.parseInt((sssss.split(":")[1]).split(", ")[0]),
                        Integer.parseInt((sssss.split(":")[1]).split(", ")[1]),
                        Integer.parseInt((sssss.split(":")[1]).split(", ")[2])
                );
                sssss = br.readLine();
                mountainc = new Color(
                        Integer.parseInt((sssss.split(":")[1]).split(", ")[0]),
                        Integer.parseInt((sssss.split(":")[1]).split(", ")[1]),
                        Integer.parseInt((sssss.split(":")[1]).split(", ")[2])
                );

                br.close();
            } catch (IOException e) {
                System.out.println("error in reading tribe data?");
            }
        } catch (FileNotFoundException e) { // defaults to a dummy unit
            System.out.println("tribe data not found");

        }
    }

    public Map<String, Boolean> getTech() {
        Map<String, Boolean> ret = new HashMap<>();
        for (Tech t : techtree) {
            ret.put(t.name.toLowerCase(), t.status);
        }
        return ret;
    }

    public void calcIncome() {
        income = 0;
        for (City c : cities) {
            if (!(c.src.unit != null && c.src.unit.team != this)) { // no enemy occupying
                if (c.capital)//bonus income from capital
                    income++;
                if(c.level > 1)//bonus income from 1st upgrade
                    income++;
                income += c.level;
            }
            for (GameTile gt : c.land) {
                if (gt.building != null && gt.building.name.equals("customs house")) {
                    income += 2*gt.building.level;
                }
            }
        }
    }

    public String getUser() {
        return user;
    } // idk why this is used and im too lazy to change it

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tribe))
            return false;
        return ((Tribe) o).getUser().equals(getUser());
    }
}
