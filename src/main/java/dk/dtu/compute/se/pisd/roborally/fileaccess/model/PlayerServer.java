package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

public class PlayerServer {

    private int playerID;
    private String playerName;
    private String program1;
    private String program2;
    private String program3;
    private String program4;
    private String program5;

    private Lobby gameInfo;

    public PlayerServer(String playerName, String program1, String program2, String program3, String program4, String program5, Lobby gameInfo){
        this.playerName = playerName;
        this.program1 = program1;
        this.program2 = program2;
        this.program3 = program3;
        this.program4 = program4;
        this.program5 = program5;
        this.gameInfo = gameInfo;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerID(String playerName) {
        this.playerName = playerName;
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

    public Lobby getGameInfo(){
        return gameInfo;
    }

    public void setGameInfo(Lobby gameInfo){
        this.gameInfo = gameInfo;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID(){
        return playerID;
    }
}
