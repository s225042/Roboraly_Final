package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

public class Pit extends FieldAction {
    @Override
    public boolean doAction(GameController gameController, Space space) {
        gameController.rebootPlayer(space.getPlayer());
        return true;
    }
}
