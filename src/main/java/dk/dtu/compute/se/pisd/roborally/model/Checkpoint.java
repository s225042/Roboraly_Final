package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

public class Checkpoint extends FieldAction {

    private final int checkpointNr;

    public Checkpoint (int checkpointNr){
        this.checkpointNr = checkpointNr;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        int checkpoint = gameController.board.getCurrentPlayer().getCheckpoint();
        if(checkpointNr == checkpoint +1){
            gameController.board.getCurrentPlayer().setCheckpoint(checkpoint + 1);
        }
        return true;
    }
}
