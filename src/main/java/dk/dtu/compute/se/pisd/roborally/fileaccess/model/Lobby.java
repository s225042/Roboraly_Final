package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

public class Lobby {
    String board;
    int turnID;
    int gameID;

    public Lobby(String board, int turnID, int gameID){
        this.board = board;
        this.turnID = turnID;
        this.gameID = gameID;

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

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
