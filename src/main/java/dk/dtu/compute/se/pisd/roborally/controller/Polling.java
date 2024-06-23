package dk.dtu.compute.se.pisd.roborally.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerServer;
import dk.dtu.compute.se.pisd.roborally.view.WhatingromeView;

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
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(8);

    private static CountDownLatch programmingLatch = new CountDownLatch(1);
    private static final int POLLING_INTERVAL_SECONDS = 2;
    private static CountDownLatch latch = new CountDownLatch(1);
    private static CountDownLatch latch2 = new CountDownLatch(1);

    private static ScheduledFuture<?> startGame;
    private static ScheduledFuture<?> programmingDone;
    private static ScheduledFuture<?> roundDone;

    private final HttpClient httpClient;

    private static HttpController httpController = new HttpController();

    public Polling(AppController appController) {
        Polling.appController = appController;
        this.httpClient = HttpClient.newHttpClient();
    }


    public static void gameStart(Lobby lobby) {
        startGame = executorService.scheduleAtFixedRate(() -> gameStarted(lobby), 0, POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public static void finishProgramming(Lobby lobby){
        programmingDone = executorService.scheduleAtFixedRate(() -> programmingCompleted(lobby), 0, POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public static void finishRound(int gameID){
        roundDone = executorService.scheduleAtFixedRate(() -> roundCompleted(gameID), 0, POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private static void gameStarted(Lobby lobby) {

        try {
            lobby = httpController.getByGameID(lobby.getID());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        if (lobby.getPhase() == Lobby.Phase.PROGRAMMING){
            startGame.cancel(false);
            appController.startGame();
        }
    }

    public void playerList(int gameID) throws Exception {
        //this.playerList = playerList;
        //Skal have og opdatere listen af spillere som er i lobbyen til spillet

    }

        private static void programmingCompleted(Lobby lobby) {
        List<PlayerServer> playerServers = new ArrayList<>();
        try {
            lobby = httpController.getByGameID(lobby.getID());
            playerServers.addAll(lobby.getPlayers());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        boolean allPlayersDone = true;
            System.out.println("print 1");
        for (PlayerServer playerServer : playerServers) {
            if (!playerServer.isProgrammingDone()) {
                // Retry logic (you can replace this with a non-blocking approach)
                allPlayersDone = false;
                break;
            }
        }
        if (allPlayersDone) {
            System.out.println("done");
            programmingDone.cancel(false);
            programmingLatch.countDown();
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
