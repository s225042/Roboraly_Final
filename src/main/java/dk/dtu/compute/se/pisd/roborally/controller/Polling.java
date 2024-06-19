package dk.dtu.compute.se.pisd.roborally.controller;

import java.net.HttpURLConnection;
import java.net.URL;

public class Polling {

    public boolean gameStarted() {
        //Skal tjekke om spillet er rykket til PROGRAMMING phase hos en spiller, og opdatere det hos alle
        return true;
    }

    public void playerList(String playerList) {
        this.playerList = playerList;
        //Skal have og opdatere listen af spillere som er i lobbyen til spillet
    }

    public boolean programmingCompleted() {
        //Tjekke om alle spillere har alle 5 programming cards i databasen, og sende spillet til EXECUTION phase
        return true;
    }

    public boolean roundCompleted() {
        //Når spillet er kørt igennem skal det rykkes tilbage til PROGRAMMING phase
        return true;
    }



}
