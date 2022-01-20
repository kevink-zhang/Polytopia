package org.cis120.polytopia.Bots;

import org.cis120.polytopia.GameAction.*;
import org.cis120.polytopia.Server.ServerPacket;
import org.cis120.polytopia.Tile.City;
import org.cis120.polytopia.Tile.GameTile;
import org.cis120.polytopia.Tile.Tech;
import org.cis120.polytopia.Tile.Tribe;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.cis120.polytopia.GameBody.*;

public class HardBot extends Tribe {
    Set<GameTile> targetcities = new HashSet<>();

    public HardBot(String tribename, String u, String id, Color c) {
        super(tribename, u, id, c);
    }
    public HardBot(String tribename, Color c){
        this(tribename, "Hard Bot", String.valueOf(Math.random()), c);
    }

    /*
    Buys a tech
     */
    public void buyTech(String techname){
        for(Tech t: techtree){
            if(t.name.toLowerCase().equals(techname.toLowerCase())){
                if(!t.status && t.cost(cities.size())<= money){
                    money -= t.cost(cities.size());
                    t.status = true;
                    return;
                }
            }
        }
    }
    /*
    Calculates if a radius is a safe zone
     */
    public int safeZone(GameTile src, int radius){
        int ret = 0;
        for(GameTile gt: src.BFS2(radius)){
            if(gt.unit!=null && gt.unit.team!=this){
                ret++;
            }
        }
        return ret;
    }
    /*
    Performs a bot unit action
     */
    public boolean unitTurn(){
        List<GameAction> todo = new LinkedList<>();

        //sends units to go ham
        for(GameTile gt: GAME.board){
            if(gt.unit!=null && gt.unit.team == this){
                if(gt.unit.movephase < 3){
                    gt.unit.setMoves();
                    if(gt.unit.attackset.size()>0){
                        for(GameAction ga: gt.unit.attack(gt.unit.attackset.iterator().next().unit)){
                            todo.add(ga);
                        }

                        for(GameTile ggt: GAME.board){
                            ggt.targetselector = 0;
                        }
                        break;
                    }
                    else if(gt.unit.moveset.size() >0){
                        double lowdist = Integer.MAX_VALUE;
                        GameTile tomoveto = null;
                        for(GameTile ggt: gt.unit.moveset){
                             for(GameTile gggt: targetcities){
                                //not perfect due to y axis distortion but ah well
                                if(ggt.center.distance(gggt.center) < lowdist){
                                    lowdist = ggt.center.distance(gggt.center);
                                    tomoveto = ggt;
                                }
                            }
                        }

                        for (GameAction ga: gt.unit.move(GAME.findPath(gt.unit.pos,tomoveto))){
                            todo.add(ga);
                        }

                        for(GameTile ggt: GAME.board){
                            ggt.targetselector = 0;
                        }
                        break;
                    }
                }
            }
        }
        //flushes actions
        for(GameAction ga: todo){
            GAME.ACTIONS.add(ga);
            CLIENT.send(new ServerPacket("action", ga.encode()));
        }
        return todo.size() >0;
    }
    /*
    Performs a bot turn start
     */
    public void startTurn(){
        List<GameAction> todo = new LinkedList<>();

        money += income;
        System.out.println(money);

        //captures cities
        for(GameTile gt: GAME.board){
            if(gt.unit!=null && gt.unit.team == this && gt.village!=null && gt.village.team!=this){
                gt.city.convert(gt.unit.team);
                gt.unit.movephase = 3;
                todo.add(new Build(gt));
            }
        }

        //finds cities to occupy
        targetcities = new HashSet<>();
        for(GameTile gt: GAME.board){
            if(gt.city!=null && gt.city.team != this){
                targetcities.add(gt);
            }
        }

        for(GameAction ga: todo){
            GAME.ACTIONS.add(ga);
            CLIENT.send(new ServerPacket("action", ga.encode()));
        }
    }
    /*
    Performs a bot turn end
     */
    public void endturn(){
        List<GameAction> todo = new LinkedList<>();

        //trains
        //training priority: knight -> catapult -> swordsman -> rider -> archer -> defender -> warrior
        //train catapults on tiles 6+ away from enemies

        for(City c: cities){
            if(c.src.unit==null && c.units.size() < c.unitcap){
                boolean d1 = safeZone(c.src, 1) == 0; //must spawn in defenders/swordsmen
                boolean d2 = safeZone(c.src, 3) == 0;
                boolean d3 = safeZone(c.src, 6) == 0;

                if(d2 && getTech().get("chivalry") && d2 && money >= 10){
                    todo.add(new Spawn("knight",c.src));
                    money -= 10;
                }
                else if(getTech().get("smithery") && money >= 5){
                    todo.add(new Spawn("swordsman",c.src));
                    money-=5;
                }
                else if(d3 && getTech().get("mathematics") && money >= 8){
                    todo.add(new Spawn("catapult",c.src));
                    money -= 8;
                }
                else if(getTech().get("riding") && money >= 3){
                    todo.add(new Spawn("rider",c.src));
                    money -= 3;
                }
                else if(d2 && getTech().get("archery") && money >= 3){
                    todo.add(new Spawn("archer",c.src));
                    money -= 3;
                }
                else if(!d1 && getTech().get("shields") && money >= 3){
                    todo.add(new Spawn("defender",c.src));
                    money -= 3;
                }
                else if(money >= 2){
                    todo.add(new Spawn("warrior",c.src));
                    money -= 2;
                }
            }
        }

        //surveys resources
        int whales = 0;
        int fish = 0;
        int animals = 0;
        int forests = 0;
        int crop = 0;
        int fruits = 0;
        int ores = 0;
        for(GameTile gt: GAME.board){
            if(gt.resource!=null){
                if(gt.resource.equals("animals")){
                    animals++;
                }
                else if(gt.resource.equals("forest")){
                    forests++;
                }
                else if(gt.resource.equals("crop")){
                    crop++;
                }
                else if(gt.resource.equals("fruit")){
                    fruits++;
                }
                else if(gt.resource.equals("ore")){
                    ores++;
                }
                else if(gt.resource.equals("fish")){
                    fish++;
                }
                else if(gt.resource.equals("whale")){
                    whales++;
                }
            }
        }

        //buys techs
        if(animals>=2){
            buyTech("hunting");
        }
        if(fish>=2){
            buyTech("fishing");
        }
        if(fruits>=2){
            buyTech("organization");
        }
        if(whales>=2 && getTech().get("fishing")){
            buyTech("whaling");
        }
        if(ores>=2){
            if(!getTech().get("climbing")) buyTech("climbing");
            buyTech("mining");
        }
        if(crop>=3){
            if(!getTech().get("organization")){
                buyTech("organization");
            }
            buyTech("farming");
        }

        //uses resources
       for(GameTile l: GAME.board){
            City c = l.city;
            if((l.unit == null || l.unit.team == this) && l.team == this && l.resource!=null){
                if(money>=3 && l.resource.equals("animals") && getTech().get("hunting")){
                    l.resource = null;
                    c.addPopulation(2, true);
                    todo.add(new Build(l));
                    money -= 3;
                }
                else if(money>=3 && l.resource.equals("fish") && getTech().get("fishing")){
                    l.resource = null;
                    c.addPopulation(2, true);
                    todo.add(new Build(l));
                    money -= 3;
                }
                else if(money>=3 && l.resource.equals("fruit") && getTech().get("organization")){
                    l.resource = null;
                    c.addPopulation(2, true);
                    todo.add(new Build(l));
                    money -= 3;
                }
                else if(money >= 5 && l.resource.equals("ore") && getTech().get("mining")){
                    l.resource = null;
                    c.addPopulation(3, true);
                    todo.add(new Build(l));
                    money -= 5;
                }
                else if(money >= 5 && l.resource.equals("crop") && getTech().get("farming")){
                    l.resource = null;
                    c.addPopulation(3, true);
                    todo.add(new Build(l));
                    money -= 5;
                }
            }

        }

        //builds

        //gets troop tech
        if(getTech().get("mining")){
            buyTech("smithery");
        }
        else if(getTech().get("riding")){
            buyTech("free spirit");
            buyTech("chivalry");
        }
        else if(getTech().get("forestry")){
            buyTech("mathematics");
        }

        if(!getTech().get("riding")){
            buyTech("riding");
        }
        else if(getTech().get("organization")){
            buyTech("shields");
        }
        else if(getTech().get("hunting")){
            buyTech("archery");
        }

        todo.add(new BotEndTurn());
        //flushes game actions to game queue and server
        for(GameAction ga: todo){
            GAME.ACTIONS.add(ga);
            CLIENT.send(new ServerPacket("action", ga.encode()));
        }
    }
    public void earlygame(){

    }
}
