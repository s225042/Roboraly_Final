package dk.dtu.compute.se.pisd.roborally.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerServer;
import javafx.application.Platform;

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


    private static  GameController gameController;
    private static AppController appController;

    private static HttpController httpController;

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
    private static final int POLLING_INTERVAL_SECONDS = 2;

    private static ScheduledFuture<?> startGame;
    private static ScheduledFuture<?> programmingDone;
    private static ScheduledFuture<?> roundDone;
    private final HttpClient httpClient;
    //private static HttpController httpController = new HttpController();






    public static void gameStart(int gameID) {
        startGame = executorService.scheduleAtFixedRate(() -> gameStarted(gameID), 0, POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public static void finishProgramming(int gameID){
        programmingDone = executorService.scheduleAtFixedRate(() -> programmingCompleted(gameID), 0, POLLING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    static void finishRound(int gameID){
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
            Platform.runLater(() -> appController.startGame());

        }
    }


    public Polling(GameController gameController, HttpController httpController) {
        Polling.gameController = gameController;
        Polling.httpController = httpController;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void playerList(int gameID) throws Exception {
        //this.playerList = playerList;
        //Skal have og opdatere listen af spillere som er i lobbyen til spillet

    }
    //Tjekke om alle spillere med samme gameID har programmingDone = true
    private static void programmingCompleted(int gameID) {
        List<PlayerServer> playerServers = new ArrayList<>();
        try {
            playerServers.addAll(httpController.getByGameID(gameID).getPlayers());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (PlayerServer playerServer : playerServers) {
            if (!playerServer.isProgrammingDone()) {
                break;
            }
            if (playerServers.get(playerServers.size() - 1) == playerServer) {
                programmingDone.cancel(false);
                Platform.runLater(() -> gameController.finishProgrammingPhase());
            }
        }
    }

    private static void roundCompleted(int gameID) {
        List<PlayerServer> playerServers = new ArrayList<>();
        try {
            playerServers.addAll(httpController.getByGameID(gameID).getPlayers());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (PlayerServer playerServer : playerServers) {
            if (!playerServer.isProgrammingDone()) {
                break;
            }
            if (playerServers.get(playerServers.size() - 1) == playerServer) {
                roundDone.cancel(false);
                Platform.runLater(() -> gameController.startProgrammingPhase());
            }
        }
    }
}