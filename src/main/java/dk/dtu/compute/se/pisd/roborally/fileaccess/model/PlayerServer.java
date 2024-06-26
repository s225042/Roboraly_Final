package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;


/**
 * @author Amalie Bojsen, s235119@dtu.dk
 * @author Rebecca Moss, s225042@dtu.dk
 */
public class PlayerServer extends Subject {

    private int id;
    private String playerName;
    private String program1;
    private String program2;
    private String program3;
    private String program4;
    private String program5;

    private Lobby gameInfo;

    private boolean programmingDone;

    /**
     *
     * @param playerName
     * @param gameInfo
     */
    public PlayerServer(String playerName, Lobby gameInfo){
        this.playerName = playerName;
        this.gameInfo = gameInfo;
        this.programmingDone = false;
    }

    public String getPlayerName() {
        return playerName;
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

    public int getPlayerID(){
        return id;
    }

    public boolean isProgrammingDone() {return programmingDone;}

    public void setProgrammingDone(boolean programmingDone) {this.programmingDone = programmingDone;}

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
