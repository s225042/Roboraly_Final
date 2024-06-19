package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private String board;
    private int turnID;
    private int id;

    private List<PlayerServer> players = new ArrayList<>();

    public Lobby(String board, int turnID){
        this.board = board;
        this.turnID = turnID;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        board = board;
    }

    public int getTurnID() {
        return turnID;
    }

    public void setTurnID(int turnID) {
        this.turnID = turnID;
    }

    public int getID() {
        return id;
    }

    public void setGameID(int id) {
        this.id = id;
    }

    public List<PlayerServer> getPlayers(){
        return players;
    }
}
