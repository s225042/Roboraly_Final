package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class ConveyorBelt extends FieldAction {

    public enum BeltType {
        GREEN,  // Moves the robot one space
        BLUE    // Moves the robot two spaces
    }

    private Heading heading;
    private int movement;  // Number of spaces to move; 1 for green, 2 for blue
    private BeltType type;

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * Constructor for the ConveyorBelt class.
     * @param heading The direction the conveyor belt moves in.
     * @param type The type of conveyor belt.
     */

    public ConveyorBelt(Heading heading, BeltType type) {
        this.heading = heading;
        this.type = type;
        this.movement = (type == BeltType.GREEN) ? 1 : 2;  // Set movement based on belt type
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public BeltType getType() {
        return type;
    }

    public void setType(BeltType type) {
        this.type = type;
        this.movement = (type == BeltType.GREEN) ? 1 : 2;  // Update movement when belt type changes
    }

    public int getMovement() {
        return movement;
    }

    /**
     *
     * @author s235112 Tobias Kolstrup Vittrup
     * Moves the player on the conveyor belt.
     * @param gameController The game controller.
     * @param space The space the player is on.
     * @return True if the player was moved, false otherwise.
     */

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        if (space == null) return false;

        Player player = space.getPlayer();
        if (player == null) return false;  // No player to move

        int x = space.getX();
        int y = space.getY();
        Board board = space.getBoard();

        for (int i = 0; i < movement; i++) {
            switch (heading) {
                case NORTH:
                    y--;
                    break;
                case SOUTH:
                    y++;
                    break;
                case EAST:
                    x++;
                    break;
                case WEST:
                    x--;
                    break;
            }

            // Check if the new position is within the bounds of the board
            if (x >= 0 && x < board.getWidth() && y >= 0 && y < board.getHeight()) {
                gameController.board.getSpace(x,y).setPlayer(player);
                if (space == null) return false;  // Invalid space, stop moving
            } else {
                return false;  // Out of bounds, stop moving
            }
        }

        // Move the player to the new position
        player.setSpace(space);

        return true;
    }
}
