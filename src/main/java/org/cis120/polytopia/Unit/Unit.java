package org.cis120.polytopia.Unit;

import org.cis120.polytopia.GameAction.Attack;
import org.cis120.polytopia.GameAction.Death;
import org.cis120.polytopia.GameAction.GameAction;
import org.cis120.polytopia.GameAction.Move;
import org.cis120.polytopia.ImageLoader;
import org.cis120.polytopia.Tile.City;
import org.cis120.polytopia.Tile.GameTile;
import org.cis120.polytopia.Tile.Tribe;

import java.awt.*;

import java.util.*;
import java.util.List;

import static org.cis120.polytopia.GameBody.USER;
import static org.cis120.polytopia.GameBody.fl;

public class Unit {
    public boolean veteran;
    public int kills = 0;
    public Stat stats; // unit stats
    public City origin; // origin city
    public Tribe team; // tribe it is from

    public GameTile pos; // where the unit currently is at
    public Point delta; // drawing the unit delta off its position
    public Set<GameTile> moveset = new HashSet<>(); // where the unit can move
    public Set<GameTile> attackset = new HashSet<>(); // where the unit can attack

    /*
     * A unit has 3 move phases:
     * 1. Move
     * 2. Attack (can be done to skip 1)
     * 3. Move (if unit has escape stat)
     * movephase will represent move phases as an integer 0-3
     */
    public int movephase;

    public Unit(Stat s, City o, Tribe t, GameTile oo) {
        origin = o;
        pos = oo;
        stats = s;
        movephase = 3;
        team = t;
        delta = new Point(0, 0);
    }

    public Stat stats() {
        return stats;
    }

    public void takeDamage(int a) {
        stats.damagehp(a);
    }

    public List<GameAction> attack(Unit enemy) {
        if (enemy == null) {
            System.out.println("Enemy does not exist!");
            return new LinkedList<>();
        }
        if (movephase > 1) {
            System.out.println("Unit cannot attack!");
            return new LinkedList<>();
        }
        List<Double> damage = stats.DMGCalc(enemy.stats());
        if (damage.size() != 2) {
            System.out.println("Damage calculator not working on unit: " + origin);
            return new LinkedList<>();
        }

        List<GameAction> ret = new LinkedList<>();
        double mydamage = damage.get(0);
        double urdamage = damage.get(1);

        if (enemy.stats.hp() <= mydamage) {
            mydamage = enemy.stats.hp(); // cap damage at hp
            ret.add(new Attack(this, enemy, mydamage));
            ret.add(new Death(enemy));
            Unit temphold = enemy;
            GameTile temploc = enemy.pos;
            temploc.unit = null;
            if(stats.range == 1 && pos.cost(enemy.pos, this)>-1) {
                List<GameTile> shortpath = new LinkedList<>();
                shortpath.add(pos);
                shortpath.add(enemy.pos);
                ret.add(new Move(this, shortpath));
            }
            temploc.unit = temphold;
        } else {
            ret.add(new Attack(this, enemy, mydamage));
            if (pos.BFS2(enemy.stats.range).contains(enemy.pos)) { // TO CHANGE, check actual range
                // instead of enemy range
                if (urdamage >= stats.hp()) {
                    urdamage = stats.hp();
                    ret.add(new Attack(enemy, this, urdamage));
                    ret.add(new Death(this));
                } else {
                    ret.add(new Attack(enemy, this, urdamage));
                }
            }
        }

        moveset.clear();
        attackset.clear();

        return ret;
    }

    public List<GameAction> move(List<GameTile> gt) {
        List<GameAction> ret = new LinkedList<>();
        if (movephase > 2 || (movephase > 0 && !stats.escape) || gt == null) {
            System.out.println("Unit cannot move!");
            return ret;
        }

        if (!moveset.contains(gt.get(gt.size() - 1))) {
            System.out.println("invalid move!");
            return ret;
        }
        ret.add(new Move(this, gt));
        if (stats.dash) {
            movephase++;
        } else {
            movephase = 3;
        }

        moveset.clear();
        attackset.clear();

        return ret;
    }

    public void promote() {
        if (veteran) {
            return;
        }
        veteran = true;
        stats.promote();
    }

    public void heal() {
        if (pos.team == USER) {
            stats.curHP = Math.min(stats.maxHP, stats.curHP + 4);
        } else {
            stats.curHP = Math.min(stats.maxHP, stats.curHP + 2);
        }
        movephase = 3;
    }

    public void boatAction(String b) {
        origin.units.remove(this);
        Unit tunit = this.stats.carrydata;
        if (b.equals("boat")) {
            tunit = this;
        }
        Unit uwu = new Unit(new Stat(b), origin, team, pos);
        uwu.stats.carrydata = tunit;
        uwu.stats.curHP = this.stats.curHP;
        uwu.stats.maxHP = this.stats.maxHP;

        this.pos.unit = uwu;
        origin.units.add(uwu);
    }

    public void beach() {
        origin.units.remove(this);
        Unit uwu = this.stats.carrydata;
        uwu.stats.curHP = this.stats.curHP;

        uwu.pos = pos;
        this.pos.unit = uwu;

        origin.units.add(uwu);
    }

    /*
     * Moveset for unit calculated in GameBoard, given to unit for display options
     */
    public void setMoves() {
//        if (team != USER) {
//            return; // non-team cannot move
//        }

        moveset = new LinkedHashSet<>();
        attackset = new LinkedHashSet<>();
        if (movephase == 0 || (stats.dash && movephase == 2)) {
            moveset = pos.BFS(true, stats.move, 1, (GameTile gtt) -> true);
        }
        if (movephase <= 1) {
            attackset.clear();
            for (GameTile gt : pos.BFS2(stats.range())) {
                if (gt.unit != null && gt.unit.team != team) {
                    gt.targetselector = 2;
                    attackset.add(gt);
                } else {
                    attackset.remove(gt);
                }
            }
        }
    }

    /*
     * Returns the position of the unit
     */
    public GameTile pos() {
        return pos;
    }

    public void draw(Graphics g, ImageLoader il) {
        int hhh = 56 * il.unit(team.tribe, stats.type).getHeight(null)
                / il.unit(team.tribe, stats.type).getWidth(null);
        //can move, draw an outline
        if(movephase < 3 && delta.x == 0 && delta.y == 0){
            int foof = 20;
            g.drawImage(il.bullseye3, pos.center.x-foof/2, (int) (pos.center.y-(foof*0.84)/2)+5, foof, (int) (foof*0.84), null);
        }
        g.drawImage(
                il.unit(team.tribe, stats.type),
                pos.center.x - 25 + delta.x, pos.center.y - 3 * hhh / 4 + delta.y, 56, hhh, null
        );
        g.setFont(fl.text3);
        if (stats.curHP < stats.maxHP / 2) {
            g.setColor(Color.red);
        } else {
            g.setColor(Color.white);
        }
        g.drawString(
                String.valueOf(stats.curHP), pos.center.x + delta.x - 17,
                pos.center.y - 12 + delta.y
        );
    }

    public void draw(Graphics g, ImageLoader il, Point p) {
        g.drawImage(
                il.unit(team.tribe, stats.type),
                p.x - 25, p.y - 47, 56, 60, null
        );
    }
}
