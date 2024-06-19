package dk.dtu.compute.se.pisd.roborally.controller;

import com.google.gson.Gson;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class HttpController {

    private HttpClient httpClient;

    public HttpController() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String getPlayers() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8089/players"))
                .header("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
              httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        return result;
    }

    public PlayerServer getPlayerByID(String playerID) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8089/players/" + playerID))
                .header("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        Gson gson = new Gson();
        PlayerServer playerServer = gson.fromJson(result, PlayerServer.class);
        return playerServer;
    }

    public Lobby getByGameID(int gameID) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8089/gameInfos/" + gameID))
                .header("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);

        Gson gson = new Gson();
        Lobby lobby = gson.fromJson(result, Lobby.class);
        return lobby;
    }

    public Lobby addGame(Lobby lobby) throws Exception {
        Gson gson = new Gson();
        String requestBody = gson.toJson(lobby);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://localhost:8089/gameInfos"))
                .header("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .build();
        try {
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            Lobby serverLoby = gson.fromJson(responseBody, Lobby.class);
            return serverLoby;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public boolean addPlayer(PlayerServer p) throws Exception {
        Gson gson = new Gson();
        String requestBody = gson.toJson(p);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://localhost:8089/players"))
                .header("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .build();
        try {
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            return true;
        } catch (Exception e1) {
            return false;
        }
    }

    public boolean updatePlayer(PlayerServer p) throws Exception {
        Gson gson = new Gson();
        String requestBody = gson.toJson(p);

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://localhost:8089/players/" + p.getPlayerID()))
                .header("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .build();
        try {
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            return true;
        } catch (Exception e1) {
            return false;
        }
    }

    public void updateGameInfo(int id, Lobby lobby) throws Exception {
        Gson gson = new Gson();
        String requestBody = gson.toJson(lobby);

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://localhost:8089/gameInfos/" + id))
                .header("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .build();
        try {
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        } catch (Exception e1) {
        }
    }


}
