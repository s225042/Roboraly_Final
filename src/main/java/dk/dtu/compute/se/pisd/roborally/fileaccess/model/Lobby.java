package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import java.util.ArrayList;
import java.util.List;

public class Lobby extends Subject {
    private String board;
    private int turnID;
    private int id;
    public enum Phase {WAITING, PROGRAMMING, EXECUTION};
    private Phase phase;

    private List<PlayerServer> players = new ArrayList<>();

    public Lobby(String board, int turnID){
        this.board = board;
        this.turnID = turnID;
        this.phase = Phase.WAITING;
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

    public void addPlayer(PlayerServer playerServer){
        players.add(playerServer);
    }

    public Phase getPhase() {return phase;}

    public void setPhase(Phase phase) {this.phase = phase;}
}
