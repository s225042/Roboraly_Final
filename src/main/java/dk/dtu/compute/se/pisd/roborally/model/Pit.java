package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;


/**
 * Author s235074, Dennis Eren Dogulu
 * This class represents a pit field action.
 * When a player steps on a pit, the player is rebooted.
 */
public class Pit extends FieldAction {
    @Override
    public boolean doAction(GameController gameController, Space space) {
        gameController.rebootPlayer(space.getPlayer());
        return true;
    }
}
