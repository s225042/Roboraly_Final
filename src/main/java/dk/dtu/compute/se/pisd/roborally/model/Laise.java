package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

public class Laise extends FieldAction {

    private Heading heading;

    private boolean startingPoint;

    public Laise(Heading heading, boolean startingPoint){
        this.heading = heading;
        this.startingPoint = startingPoint;
    }

    public Heading getHeading() {
        return heading;
    }

    public boolean isStartingPoint(){ return startingPoint;}

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Space currentSpace = space;
        while (!space.getWalls().contains(heading)) {
            if (currentSpace.getPlayer() != null) {
                //space.getPlayer(). shold give the plyer a dameg card whe dmeg card is implermentet
                break;
            } else {
                Space nextSpace = currentSpace.board.getNeighbour(currentSpace, heading);
                if (nextSpace == null) {
                    break; // Exit if there is no neighbor in the given heading
                }
                currentSpace = nextSpace;
            }
        }
        return true;
    }
}
