package org.cis120.polytopia;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.cis120.polytopia.GameBody.USER;

public class ImageLoader {
    public boolean loaded = false;
    public boolean light = false;
    public boolean light_start = false;
    public boolean light_end = false;

    public Image background;
    public Image science;
    public Image bullseye;
    public Image bullseye2;
    public Image bullseye3;
    public Image village;
    public Image ruins;
    public Image fog;
    public Image heal;
    public Image ok;
    public Image exit;

    public Image fish;
    public Image whale;
    public Image ore;
    public Image crop;
    public Image ocean;
    public Image water;
    public Image star;
    public Image suggestion;
    public Image wall;

    public Image burn_forest;
    public Image capture;
    public Image clear_forest;
    public Image forest_defense;
    public Image water_defense;
    public Image ocean_defense;
    public Image mountain_defense;
    public Image literacy;
    public Image grow_forest;
    public Image destroy;
    public Image roads;

    private Map<String, Map<String, Image>> units = new HashMap<>();
    private Map<String, Map<String, Image>> terrain = new HashMap<>();
    private Map<String, Map<Integer, Image>> city = new HashMap<>();
    private Map<String, Image> heads = new HashMap<>();
    private Map<String, Map<Integer, Image>> buildings = new HashMap<>();

    private Map<String, Map<String, Image>> outline = new HashMap<>();

    private String[] tribes = { "Ai-Mo", "Bardur", "Hoodrick", "Imperius",
        "Kickoo", "Luxidoor", "Oumaji", "Quetzali",
        "Vengir", "Xin-xi", "Yadakk", "Zebasi" };
    private String[] validunits = {
        "archer", "battleship", "boat",
        "catapult", "defender", "giant",
        "knight", "mind bender", "rider",
        "ship", "swordsman", "warrior", "guard tower"
    };
    private String[] validbuildings = {
        "Customs house", "Forest temple", "Forge", "Mountain temple",
        "Sawmill", "Temple", "Water temple", "Windmill", "Farm",
        "Lumber hut", "Mine", "Port"
    };

    public ImageLoader() {

    }

    public boolean load() {
        try {
            background = ImageIO
                    .read(new File("files/assets/Polytopia Sprites/Miscellaneous/Background.png"));
            science = ImageIO.read(new File("files/assets/Polytopia Sprites/Symbols/Science.png"));

            bullseye = ImageIO.read(new File("files/assets/bullseye.png"));
            bullseye2 = ImageIO.read(new File("files/assets/bullseye2.png"));
            bullseye3 = ImageIO.read(new File("files/assets/bullseye3.png"));
            fog = ImageIO.read(new File("files/assets/clouds.png"));
            heal = ImageIO.read(new File("files/assets/heal.png"));
            ok = ImageIO.read(new File("files/assets/ok.png"));
            exit = ImageIO.read(new File("files/assets/exit.png"));

            village = ImageIO
                    .read(new File("files/assets/Polytopia Sprites/Miscellaneous/Village.png"));
            ruins = ImageIO.read(new File("files/assets/Polytopia Sprites/Miscellaneous/Ruin.png"));
            fish = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Miscellaneous/Fish.png")
            );
            whale = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Miscellaneous/Whale.png")
            );
            ore = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Miscellaneous/Metal.png")
            );
            crop = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Miscellaneous/Crop.png")
            );
            ocean = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Miscellaneous/Deep water.png")
            );
            water = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Miscellaneous/Shallow water.png")
            );
            star = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Miscellaneous/Star.png")
            );
            suggestion = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Miscellaneous/Suggestion.png")
            );

            wall = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Buildings/City wall.png")
            );

            burn_forest = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Symbols/Burn forest.png")
            );
            capture = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Symbols/capture.png")
            );
            clear_forest = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Symbols/Clear forest.png")
            );
            forest_defense = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Symbols/Forest defense.png")
            );
            mountain_defense = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Symbols/Mountain defense.png")
            );
            ocean_defense = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Symbols/Deep water defense.png")
            );
            water_defense = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Symbols/Shallow water defense.png")
            );
            literacy = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Symbols/Literacy.png")
            );
            grow_forest = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Symbols/Grow forest.png")
            );
            destroy = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Symbols/Destroy.png")
            );
            roads = ImageIO.read(
                    new File("files/assets/Polytopia Sprites/Miscellaneous/Road_Icon.png")
            );

            System.out.println("Misc images loaded");

            for (String s : validbuildings) {
                Map<Integer, Image> m = new HashMap<>();
                File ff = new File("files/assets/Polytopia Sprites/Buildings/" + s);
                int index = 1;
                for (File f : ff.listFiles()) {
                    m.put(index, ImageIO.read(f));
                    index++;
                }

                buildings.put(s.toLowerCase(), m);
            }

            System.out.println("Buildings loaded");

            for (String s : tribes) {
                heads.put(
                        s.toLowerCase(), ImageIO.read(
                                new File(
                                        "files/assets/Polytopia Sprites/Tribes/" + s + "/" + s
                                                + " head.png"
                                )
                        )
                );
            }
            System.out.println("heads loaded");

            if (false){//Runtime.getRuntime().maxMemory() > 3000000000.0) { // program uses about
                        // 2.5k MB in
                        // image storage
                for (String s : tribes) {
                    Map<String, Image> m = new HashMap<>();
                    File f = new File("files/assets/Polytopia Sprites/Tribes/" + s + "/Units");
                    for (File ff : f.listFiles()) {
                        if (ff.isFile()) {
                            String str2 = ff.getName().substring(0, ff.getName().length() - 4);

                            for (String str3 : validunits) {
                                if (str3.equals(str2)) {
                                    m.put(str2, ImageIO.read(ff));
                                    break;
                                }
                            }
                        }
                    }
                    units.put(s.toLowerCase(), m);
                    //outliner(s.toLowerCase());
                }
                System.out.println("Unit images loaded");

                for (String s : tribes) {
                    Map<String, Image> m = new HashMap<>();
                    m.put(
                            "forest", ImageIO.read(
                                    new File(
                                            "files/assets/Polytopia Sprites/Tribes/" + s + "/" + s
                                                    + " forest.png"
                                    )
                            )
                    );
                    m.put(
                            "fruit", ImageIO.read(
                                    new File(
                                            "files/assets/Polytopia Sprites/Tribes/" + s + "/" + s
                                                    + " fruit.png"
                                    )
                            )
                    );
                    m.put(
                            "animals", ImageIO.read(
                                    new File(
                                            "files/assets/Polytopia Sprites/Tribes/" + s + "/" + s
                                                    + " game.png"
                                    )
                            )
                    );
                    m.put(
                            "mountains", ImageIO.read(
                                    new File(
                                            "files/assets/Polytopia Sprites/Tribes/" + s + "/" + s
                                                    + " mountain.png"
                                    )
                            )
                    );
                    m.put("fish", fish);
                    m.put("whale", whale);
                    m.put("ore", ore);
                    m.put("crop", crop);

                    terrain.put(s, m);
                }
                System.out.println("Terrain images loaded");

                for (String s : tribes) {
                    Map<Integer, Image> m = new HashMap<>();
                    m.put(
                            0, ImageIO.read(
                                    new File(
                                            "files/assets/Polytopia Sprites/Tribes/" + s + "/City/"
                                                    + s + " city castle.png"
                                    )
                            )
                    );
                    m.put(
                            1, ImageIO.read(
                                    new File(
                                            "files/assets/Polytopia Sprites/Tribes/" + s + "/City/"
                                                    + s + " city 1.png"
                                    )
                            )
                    );
                    m.put(
                            2, ImageIO.read(
                                    new File(
                                            "files/assets/Polytopia Sprites/Tribes/" + s + "/City/"
                                                    + s + " city 3.png"
                                    )
                            )
                    );
                    m.put(
                            3, ImageIO.read(
                                    new File(
                                            "files/assets/Polytopia Sprites/Tribes/" + s + "/City/"
                                                    + s + " city 4.png"
                                    )
                            )
                    );
                    m.put(
                            4, ImageIO.read(
                                    new File(
                                            "files/assets/Polytopia Sprites/Tribes/" + s + "/City/"
                                                    + s + " city 4.png"
                                    )
                            )
                    );
                    city.put(s, m);
                }
                System.out.println("City images loaded");
            } else {
                light = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loaded = true;
        return true;
    }

    // loading in for those without high heap memory
    // manually loads in a specific tribe
    public boolean reload(String t) {
        light_start = true;

        if (!light) {
            return false;
        }
        boolean valid = false; // checks if tribe is valid
        System.out.println(t);
        for (String s : tribes) {
            if (s.equals(t)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            return false;
        }
        try {
            Map<String, Image> m = new HashMap<>();
            File f = new File("files/assets/Polytopia Sprites/Tribes/" + t + "/Units");
            for (File ff : f.listFiles()) {
                if (ff.isFile()) {
                    String str2 = ff.getName().substring(0, ff.getName().length() - 4); // remove
                                                                                        // .png
                    for (String str3 : validunits) {
                        if (str3.equals(str2)) {
                            m.put(str2, ImageIO.read(ff));
                            break;
                        }
                    }
                }
            }
            units.put(t.toLowerCase(), m);
            //outliner(t.toLowerCase());

            System.out.println(t + " units reloaded!");

            Map<String, Image> m2 = new HashMap<>();
            m2.put(
                    "forest", ImageIO.read(
                            new File(
                                    "files/assets/Polytopia Sprites/Tribes/" + t + "/" + t
                                            + " forest.png"
                            )
                    )
            );
            m2.put(
                    "fruit", ImageIO.read(
                            new File(
                                    "files/assets/Polytopia Sprites/Tribes/" + t + "/" + t
                                            + " fruit.png"
                            )
                    )
            );
            m2.put(
                    "animals", ImageIO.read(
                            new File(
                                    "files/assets/Polytopia Sprites/Tribes/" + t + "/" + t
                                            + " game.png"
                            )
                    )
            );
            m2.put(
                    "mountains", ImageIO.read(
                            new File(
                                    "files/assets/Polytopia Sprites/Tribes/" + t + "/" + t
                                            + " mountain.png"
                            )
                    )
            );
            m2.put(
                    "fish", ImageIO.read(
                            new File("files/assets/Polytopia Sprites/Miscellaneous/Fish.png")
                    )
            );
            m2.put(
                    "whale", ImageIO.read(
                            new File("files/assets/Polytopia Sprites/Miscellaneous/Whale.png")
                    )
            );
            m2.put(
                    "ore", ImageIO.read(
                            new File("files/assets/Polytopia Sprites/Miscellaneous/Metal.png")
                    )
            );
            m2.put(
                    "crop", ImageIO.read(
                            new File("files/assets/Polytopia Sprites/Miscellaneous/Crop.png")
                    )
            );

            terrain.put(t, m2);
            System.out.println(t + " terrain reloaded!");

            Map<Integer, Image> m3 = new HashMap<>();
            m3.put(
                    0, ImageIO.read(
                            new File(
                                    "files/assets/Polytopia Sprites/Tribes/" + t + "/City/" + t
                                            + " city castle.png"
                            )
                    )
            );
            m3.put(
                    1, ImageIO.read(
                            new File(
                                    "files/assets/Polytopia Sprites/Tribes/" + t + "/City/" + t
                                            + " city 1.png"
                            )
                    )
            );
            m3.put(
                    2, ImageIO.read(
                            new File(
                                    "files/assets/Polytopia Sprites/Tribes/" + t + "/City/" + t
                                            + " city 3.png"
                            )
                    )
            );
            m3.put(
                    3, ImageIO.read(
                            new File(
                                    "files/assets/Polytopia Sprites/Tribes/" + t + "/City/" + t
                                            + " city 4.png"
                            )
                    )
            );
            m3.put(
                    4, ImageIO.read(
                            new File(
                                    "files/assets/Polytopia Sprites/Tribes/" + t + "/City/" + t
                                            + " city 4.png"
                            )
                    )
            );
            city.put(t, m3);

            System.out.println(t + " city loaded");
            light_end = true;
        } catch (IOException e) {
            e.printStackTrace();
            light_end = light_start = true;
        }
        return true;
    }

    public Image unit(String t, String u) {
        t = t.toLowerCase();
        String u1 = u.toLowerCase();
        if (!units.containsKey(t)) {
            return null;
        }
        if (!units.get(t).containsKey(u1)) {
            return null;
        }
        return units.get(t).get(u1);
    }

    public Image terrain(String t, String l) {
        String u1 = l.toLowerCase();
        if (!terrain.containsKey(t)) {
            return null;
        }
        if (!terrain.get(t).containsKey(u1)) {
            return null;
        }
        return terrain.get(t).get(u1);
    }

    public Image city(String t, int i) {
        if (!city.containsKey(t)) {
            return null;
        }
        if (!city.get(t).containsKey(i)) {
            return null;
        }
        return city.get(t).get(i);
    }

    public Image building(String b, int lvl) {
        b = b.toLowerCase();
        lvl = Math.max(lvl, 1);
        if (!buildings.containsKey(b)) {
            return null;
        }
        if (!buildings.get(b).containsKey(lvl)) {
            return null;
        }
        return buildings.get(b).get(lvl);
    }

    public Image getHead(String t) {
        t = t.toLowerCase();
        if (!heads.containsKey(t)) {
            return null;
        }
        return heads.get(t);
    }

    public Image getTech(String t) {
        t = t.toLowerCase();

        if (t.equals("hunting")) {
            return terrain(USER.tribe, "animals");
        } else if (t.equals("archer")) {
            return unit(USER.tribe, "archer");
        } else if (t.equals("forest bonus")) {
            return forest_defense;
        } else if (t.equals("grow forest")) {
            return grow_forest;
        } else if (t.equals("forest temple")) {
            return building("forest temple", 1);
        } else if (t.equals("lumber hut")) {
            return building("lumber hut", 1);
        } else if (t.equals("catapult")) {
            return unit(USER.tribe, "catapult");
        } else if (t.equals("sawmill")) {
            return building("sawmill", 1);
        } else if (t.equals("rider")) {
            return unit(USER.tribe, "rider");
        } else if (t.equals("roads")) {
            return roads;
        } else if (t.equals("customs house")) {
            return building("customs house", 1);
        } else if (t.equals("temple")) {
            return building("temple", 1);
        } else if (t.equals("clear forest")) {
            return clear_forest;
        } else if (t.equals("retire")) {
            return star;
        } else if (t.equals("knight")) {
            return unit(USER.tribe, "knight");
        } else if (t.equals("burn forest")) {
            return burn_forest;
        } else if (t.equals("harvest")) {
            return terrain(USER.tribe, "fruit");
        } else if (t.equals("farm")) {
            return building("farm", 1);
        } else if (t.equals("windmill")) {
            return building("windmill", 1);
        } else if (t.equals("destroy")) {
            return destroy;
        } else if (t.equals("defender")) {
            return unit(USER.tribe, "defender");
        } else if (t.equals("guard tower")) {
            return unit(USER.tribe, "guard tower");
        } else if (t.equals("mountain")) {
            return terrain(USER.tribe, "mountains");
        } else if (t.equals("mine")) {
            return building("mine", 1);
        } else if (t.equals("swordsman")) {
            return unit(USER.tribe, "swordsman");
        } else if (t.equals("forge")) {
            return building("forge", 1);
        } else if (t.equals("mountain temple")) {
            return building("mountain temple", 1);
        } else if (t.equals("mountain bonus")) {
            return mountain_defense;
        } else if (t.equals("mind bender")) {
            return unit(USER.tribe, "mind bender");
        } else if (t.equals("literacy")) {
            return literacy;
        } else if (t.equals("fish")) {
            return terrain(USER.tribe, "fish");
        } else if (t.equals("ship")) {
            return unit(USER.tribe, "ship");
        } else if (t.equals("port")) {
            return building("port", 1);
        } else if (t.equals("water")) {
            return water;
        } else if (t.equals("battleship")) {
            return unit(USER.tribe, "battleship");
        } else if (t.equals("ocean")) {
            return ocean;
        } else if (t.equals("whale")) {
            return terrain(USER.tribe, "whale");
        } else if (t.equals("water temple")) {
            return building("water temple", 1);
        } else if (t.equals("water bonus")) {
            return water_defense;
        } else if (t.equals("ocean bonus")) {
            return ocean_defense;
        } else if (t.equals("warrior")) {
            return unit(USER.tribe, "warrior");
        } else if (t.equals("heal")) {
            return heal;
        } else if (t.equals("capture")) {
            return capture;
        }
        return null;
    }

    //attempted to outline images, but it was too laggy and/or caused heap overflows
//    private void outliner(String key){
//        Map<String, Image> m = new HashMap<>();
//
//        for(String s: units.get(key).keySet()) {
//            Image im = units.get(key).get(s);
//
//            BufferedImage image =
//                    new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//            Graphics g = image.createGraphics();
//            g.drawImage(im, 0, 0, null);
//            //BufferedImage image = (BufferedImage) im;
//            Color fillColor = new Color(110, 243, 255); // Black
//
//            for (int y = 0; y < image.getHeight(); y++) {
//                for (int x = 0; x < image.getWidth(); x++) {
//                    int color = image.getRGB(x, y);
//                    int alpha = (color >> 24) & 0xff;
//
//                    if (alpha >= 100) {
//                        image.setRGB(x, y, fillColor.getRGB());
//                    }
//                }
//            }
//
//            m.put(s, image);
//        }
//        outline.put(key, m);
//    }

//    public Image outline(Image im){
//        BufferedImage image =
//                new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//        Graphics g = image.createGraphics();
//        g.drawImage(im, 0, 0, null);
//        //BufferedImage image = (BufferedImage) im;
//        Color fillColor = new Color(110, 243, 255); // Black
//
//        for (int y = 0; y < image.getHeight(); y++) {
//            for (int x = 0; x < image.getWidth(); x++) {
//                int color = image.getRGB(x, y);
//                int alpha = (color >> 24) & 0xff;
//
//                if (alpha >= 100) {
//                    image.setRGB(x, y, fillColor.getRGB());
//                }
//            }
//        }
//
//        return image;
//    }

//    public Image outline(String t, String u) {
//        t = t.toLowerCase();
//        String u1 = u.toLowerCase();
//        if (!outline.containsKey(t)) {
//            return null;
//        }
//        if (!outline.get(t).containsKey(u1)) {
//            return null;
//        }
//        return outline.get(t).get(u1);
//    }
}
