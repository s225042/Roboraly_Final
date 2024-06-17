package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.WaitingRoom;

public class WaitingController {
    HttpController httpController = new HttpController();

    final public WaitingRoom waitingRoom;

    public WaitingController(WaitingRoom waitingRoom){
        this.waitingRoom = waitingRoom;
    }

    boolean WatingRomePlauers() throws Exception{
        String players = httpController.getPlayers();
        return true;

    }





}
