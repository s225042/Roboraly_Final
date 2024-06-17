package dk.dtu.compute.se.pisd.roborally.controller;

public class WaitingController {
    HttpController httpController = new HttpController();

    boolean WatingRomePlauers() throws Exception{
        String players = httpController.getPlayers();

    }

}
