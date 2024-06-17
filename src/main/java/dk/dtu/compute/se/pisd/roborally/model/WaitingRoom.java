package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;
import java.util.List;

public class WaitingRoom {
    final private int gameID;

    private List<String> playerNames = new ArrayList<>();
    public WaitingRoom(int gameID){
        this.gameID = gameID;
    }

    public List<String> getPlayerNames(){
        return playerNames;
    }

    public void addPlayerID(String playerID){
        playerNames.add(playerID);
    }

    Integer getGameID(){
        return gameID;
    }
}
