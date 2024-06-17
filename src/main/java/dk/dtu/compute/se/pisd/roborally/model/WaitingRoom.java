package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;
import java.util.List;

public class WaitingRoom {
    int gameID;

    List<String> playerNames = new ArrayList<>();
    public WaitingRoom(int gameID){
        this.gameID = gameID;
    }

    List<String> getPlayerNames(){
        return playerNames;
    }

    void addPlayerID(String playerID){
        playerNames.add(playerID);
    }

    Integer getGameID(){
        return gameID;
    }
}
