package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

public class Lobby {
    String board;
    int turnID;
    int id;

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
}
