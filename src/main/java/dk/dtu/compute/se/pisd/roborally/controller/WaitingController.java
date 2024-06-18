package dk.dtu.compute.se.pisd.roborally.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.WaitingRoom;

import java.lang.reflect.Type;
import java.util.List;

public class WaitingController {
    HttpController httpController;

    private boolean startingGame = false;

    final public WaitingRoom waitingRoom;

    public WaitingController(WaitingRoom waitingRoom, HttpController httpController){
        this.waitingRoom = waitingRoom;
        this.httpController = httpController;
    }

    public void watingRomePlayers() throws Exception{
        String playersJson = httpController.getPlayers();

        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> players = gson.fromJson(playersJson, listType);

        // Add player names to the waiting room
        for (String playerName : players) {
            waitingRoom.addPlayerID(playerName);
        }

    }

    public boolean starttingGame(){
        return startingGame;
    }

    public  void  setStartingGame(boolean startGame){
        startingGame = startGame;
    }



}
