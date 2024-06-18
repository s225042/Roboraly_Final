package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

public class PlayerServer {
    private String playerID;

    private String program1;
    private String program2;
    private String program3;
    private String program4;
    private String program5;

    private int gameID;

    public PlayerServer(String playerID, String program1, String program2, String program3, String program4, String program5, int gameID){
        this.playerID = playerID;
        this.program1 = program1;
        this.program2 = program2;
        this.program3 = program3;
        this.program4 = program4;
        this.program5 = program5;
        this.gameID = gameID;
    }

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getProgram1() {
        return program1;
    }

    public void setProgram1(String program1) {
        this.program1 = program1;
    }

    public String getProgram2() {
        return program2;
    }

    public void setProgram2(String program2) {
        this.program2 = program2;
    }

    public String getProgram3() {
        return program3;
    }

    public void setProgram3(String program3) {
        this.program3 = program3;
    }

    public String getProgram4() {
        return program4;
    }

    public void setProgram4(String program4) {
        this.program4 = program4;
    }

    public String getProgram5() {
        return program5;
    }

    public void setProgram5(String program5) {
        this.program5 = program5;
    }

    public int getGameID(){
        return gameID;
    }

    public void  setGameID(int gameID){
        this.gameID = gameID;
    }
}
