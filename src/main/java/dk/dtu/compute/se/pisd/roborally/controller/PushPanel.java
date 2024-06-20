package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class PushPanel extends FieldAction {
    private Heading heading;



    public PushPanel(Heading heading) {
        this.heading = heading;
    }


    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }





    @Override
    public boolean doAction(GameController gameController, Space space) {
        if(space == null) return false;

        Player player = space.getPlayer();
        if(player == null) return false;

        Board board = gameController.board;
        Space target = null;

        if(player.board == board){
            target = board.getNeighbour(space, heading);
        }

        if (target != null) {
            try {
                gameController.moveToSpace(player, target, heading);
            } catch (GameController.ImpossibleMoveException e) {
                // Handle impossible move exception
            }
        }


        return false;
    }
}
