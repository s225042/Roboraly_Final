package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.WaitingRoom;

public class WaitingController {
    HttpController httpController;

    final public WaitingRoom waitingRoom;

    public WaitingController(WaitingRoom waitingRoom, HttpController httpController){
        this.waitingRoom = waitingRoom;
        this.httpController = httpController;
    }

    public boolean watingRomePlayers() throws Exception{
        String players = httpController.getPlayers();
        return true;

    }

    public boolean starttingGame(){
        return true;
    }



}
