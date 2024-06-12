package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

/**
 * ...
 *
 *
 * @author Rebecca Moss, s225042@dtu.dk
 * @parem checkpointNr (type int)
 */
public class Checkpoint extends FieldAction {

    private final int checkpointNr;

    public Checkpoint (int checkpointNr){
        this.checkpointNr = checkpointNr;
    }

    public int getCheckpointNr(){return checkpointNr;}

    /**
     * @author Rebecca Moss, s225042@dtu.dk
     * @param gameController
     * @param space
     * @return
     */
    @Override
    public boolean doAction(GameController gameController, Space space) {
        if(gameController.won){
            return false;
        }
        Player player = space.getPlayer();
        if (player == null){
            return true;
        }

        int checkpoint = player.getCheckpoint();
        if(checkpointNr == checkpoint +1){
            space.getPlayer().setCheckpoint(checkpoint + 1);
        }
        if (checkpointNr == gameController.board.getMaxNumberofChekpoints()) {
            gameController.showWinningMessage(space.getPlayer());
        }
        return true;
    }
}
