package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Direction;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;


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

    public Direction getHeading() {
        return this.direction;
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
        if (player == null) {
            System.err.println("Error: No player on the space to perform Gear action.");
            return false; // No player to act upon
        }

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


