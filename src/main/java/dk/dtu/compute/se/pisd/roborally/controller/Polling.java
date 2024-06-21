package dk.dtu.compute.se.pisd.roborally.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerServer;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Polling {

    private static AppController appController;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
    private static final int POLLING_INTERVAL_SECONDS = 2;
    private static CountDownLatch latch = new CountDownLatch(1);
    private static CountDownLatch latch2 = new CountDownLatch(1);

    private static ScheduledFuture<?> startGame;
    private static ScheduledFuture<?> programmingDone;
    private static ScheduledFuture<?> roundDone;

    public static void gameStart(int gameID) {
        startGame = executorService.scheduleAtFixedRate(() -> gameStarted(gameID), 0, POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public static void finishProgramming(int gameID){
        programmingDone = executorService.scheduleAtFixedRate(() -> programmingCompleted(gameID), 0, POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public static void finishRound(int gameID){
        roundDone = executorService.scheduleAtFixedRate(() -> roundCompleted(gameID), 0, POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private static void gameStarted(int gameID) {
        Lobby lobby;

        try {
            lobby = httpController.getByGameID(gameID);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        if (lobby.getPhase() == Lobby.Phase.PROGRAMMING){
            startGame.cancel(false);
            appController.startGame();
        }
    }


    private final HttpClient httpClient;

    private static HttpController httpController = new HttpController();

    public Polling(AppController appController) {
        Polling.appController = appController;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void playerList(int gameID) throws Exception {
        //this.playerList = playerList;
        //Skal have og opdatere listen af spillere som er i lobbyen til spillet

    }

    private static void programmingCompleted(int gameID){
        //Tjekke om alle spillere med samme gameID har programmingDone = true
        List<PlayerServer> playerServers = new ArrayList<>();
        try {
            playerServers.addAll(httpController.getByGameID(gameID).getPlayers());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }


        for(int i = 0; i<playerServers.size(); i++) {

            PlayerServer playerServer = playerServers.get(i);
            if (!playerServer.isProgrammingDone()){
                try {
                    latch.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            if(playerServers.get(playerServers.size() -1) == playerServer){
                latch.countDown();
                programmingDone.cancel(false);

            }
        }
    }

    private static void roundCompleted(int gameID){
        //Når spillet er kørt igennem skal det rykkes tilbage til PROGRAMMING phase
        //Tjekke om alle spillere med samme gameID har programmingDone = false
        List<PlayerServer> playerServers = new ArrayList<>();
        try {
            playerServers.addAll(httpController.getByGameID(gameID).getPlayers());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        for(PlayerServer playerServer : playerServers) {
            if (playerServer.isProgrammingDone()){
                try {
                    latch2.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            if(playerServers.get(playerServers.size() -1) != playerServer){
                latch2.countDown();
                roundDone.cancel(false);
            }
        }

    }



}
