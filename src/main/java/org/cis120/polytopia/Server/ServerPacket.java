package org.cis120.polytopia.Server;

import com.google.gson.Gson;

public class ServerPacket {
    public String type;
    public Object information;
    /*
     * types of server packets
     * action: troop movement
     * sends a gameaction object
     * processed by all players except sender
     * tech: player tech
     * sends List of two Strings, one for player id and the other for tech name
     * end turn: end turn
     * sends no additional information
     * each client will call nextTurn()
     * user info: player id, username, and tribe
     * sends List of three strings as above
     * server adds a new player to its player info list
     * init: initializes board
     * sends the entire gameboard of the host client to all clients
     * each client initializes their gameboard with it
     * all client scenes are set to in game
     * log out: player leaves game
     * sends no additional information
     * each client will change the player.active to false
     * end game: host leaves game
     * sends no additional information
     * each client will close, and players will be forced into the multiplayer menu
     */

    public ServerPacket() {

    }

    public ServerPacket(String t, Object info) {
        type = t;
        information = info;
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }

    public ServerPacket fromJSON(String json) {
        return new Gson().fromJson(json, ServerPacket.class);
    }
}
