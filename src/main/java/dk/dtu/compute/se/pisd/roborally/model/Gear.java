package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;



/**
 * @Author s235074
 * This class represents the Gear object in the game Robo Rally.
 * The Gear object is a FieldAction and has a direction.
 *
 */

public class Gear extends FieldAction {
    public Direction direction;

    public void setDirection(Direction direction) {

        this.direction = direction;
    }


    public Gear(Direction direction) {
        this.direction = direction;
    }

    /**
     * @Author s235074
     * This method is used to turn the player in the direction of the gear.
     * @param gC GameController
     * @param space Space
     * @return boolean
     */

    @Override
    public boolean doAction(GameController gC, Space space) {
        Player player = space.getPlayer();

        switch (direction) {


            case LEFT:
                gC.turnLeft(player);
                break;


            case RIGHT:
                gC.turnRight(player);
                break;

        }
        return true;

    }
}


