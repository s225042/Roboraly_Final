package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private String board;
    private int turnID;
    private int id;
    public enum phase {WAITING, PROGRAMMING, EXECUTION};
    private phase phase;

    private List<PlayerServer> players = new ArrayList<>();

    public Lobby(String board, int turnID){
        this.board = board;
        this.turnID = turnID;
        this.phase = phase.WAITING;
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

    public Lobby.phase getPhase() {return phase;}

    public void setPhase(Lobby.phase phase) {this.phase = phase;}
}
