package org.cis120.polytopia.GameAction;

import org.cis120.polytopia.ImageLoader;

import java.awt.*;
import java.util.Map;

/*
 * List of possible actions in game
 * Existing units are found based on their position tile id
 * New units are generated from their type (i.g. "Warrior")
 * Tiles are found based on their position id, using gamemap in GameBoard
 * 
 * Troop based:
 * Attack: Unit origin, Unit target, double damage
 * Deals damage from origin to target. Calculates additional movement like
 * killing, persist, and escape
 * Move: Unit self, List<GameTile> path
 * Moves self down the path of GameTiles
 * Death: Unit self
 * Dies lmao
 * Clients send this when retiring units, as stars are kept client side
 * Spawn: Unit self, GameTile city
 * Spawns a unit self at city
 * Upgrade: Unit self, boolean veteran
 * Upgrades the unit, either as veteran or as an upgrade, or as a heal
 * 
 * Land based:
 * Build: GameTile tile
 * Sends information about gametile resource, terrain, and building
 * If client building does not match terrain, builds a building of type b on
 * tile
 * if b is null, that indicates a destroy on tile
 * Updates all adjacent tiles and the tile city
 * Expand: GameTile tile, Tribe tribe
 * Expands the village on tile
 * If the village was not already owned by tribe, convert it to tribe
 * Population: GameTile tile
 * Adds a population to the city on tile
 * 
 * Misc:
 * Tech: String tech, Tribe tribe
 * tribe gains additional tech
 * update all unit stats for that tribe (to calculate defense bonuses)
 * 
 */
public interface GameAction {
    public void end();

    public boolean tick(); // return true to end the action

    public void draw(Graphics g, ImageLoader il); // draws the game action

    public Map<String, Object> encode();
}
