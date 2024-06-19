package dk.dtu.compute.se.pisd.roborally.controller;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class Polling {
    private static final String REST_URL = "http://localhost:8089/";

    private final HttpClient httpClient;

    private HttpController httpController = new HttpController();

    public Polling() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public boolean gameStarted(int gameID) {
        //Skal tjekke om spillet er rykket til PROGRAMMING phase hos en spiller, og opdatere det hos alle

           // if (httpController.getByGameID(gameID).)

        return true;
    }

    public void playerList(String playerList) {
        this.playerList = playerList;
        //Skal have og opdatere listen af spillere som er i lobbyen til spillet
    }

    public boolean programmingCompleted() {
        //Tjekke om alle spillere med samme gameID har programmingDone = true
        return true;
    }

    public boolean roundCompleted() {
        //Når spillet er kørt igennem skal det rykkes tilbage til PROGRAMMING phase
        return true;
    }



}
