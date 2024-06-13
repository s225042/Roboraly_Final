/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import java.util.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;
    public boolean won = false;

    public GameController(Board board) {
        this.board = board;
    }


    public void determinePlayerOrder(){
            List<Player> players = new ArrayList<>(board.getPlayers());
            players.sort(Comparator.comparingInt(player -> board.getAntenna().calculateDistance(player)));
            board.setPlayerOrder(players);
        }


        public void moveForward(@NotNull Player player) {
        if (!won && player.board == board) { // Check if game is won before moving
            Space space = player.getSpace();
            Heading heading = player.getHeading();

            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here for now; we just catch the
                    // exception so that we do not pass it on to the caller
                    // (which would be very bad style).
                }
            }
        }
    }

    public void leftOrRight(Player player) {
        if (!won) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Choose Direction");
            alert.setHeaderText("Direction Choice");
            alert.setContentText("Choose your direction:");

            ButtonType buttonLeft = new ButtonType("Left");
            ButtonType buttonRight = new ButtonType("Right");

            alert.getButtonTypes().setAll(buttonLeft, buttonRight);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonLeft) {
                turnLeft(player);
            } else if (result.isPresent() && result.get() == buttonRight) {
                turnRight(player);
            }
        }
    }

    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
    }

    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
    }

    public void fastFastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
        moveForward(player);
    }

    void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space; // make sure the move to here is possible in principle
        Player other = space.getPlayer();
        if (other != null) {
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // XXX Note that there might be additional problems with
                //     infinite recursion here (in some special cases)!
                //     We will come back to that!
                moveToSpace(other, target, heading);

                // Note that we do NOT embed the above statement in a try-catch block, since
                // the thrown exception is supposed to be passed on to the caller

                assert target.getPlayer() == null : target; // make sure target is free now
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        FieldAction fieldAction = space.getFieldAction();
        if (fieldAction instanceof ConveyorBelt) {
            ConveyorBelt conveyorBelt = (ConveyorBelt) fieldAction;
            Heading beltHeading = conveyorBelt.getHeading();
            for (int i = 0; i < conveyorBelt.getMovement(); i++) {
                space = board.getNeighbour(space, beltHeading);
                if (space == null) {
                    break;
                }
            }
        }
        player.setSpace(space);
    }

    public void moveCurrentPlayerToSpace(Space space) {
        if (!won && space.getPlayer() == null) {
            Player current;
            space.setPlayer(space.board.getCurrentPlayer());
            int playerNumber = space.board.getPlayerNumber(space.board.getCurrentPlayer()) + 1;
            if (playerNumber >= space.board.getPlayersNumber()) {
                current = space.board.getPlayer(0);
            } else {
                current = space.board.getPlayer(playerNumber);
            }
            space.board.setCurrentPlayer(current);
        }
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (Player player : board.getPlayerOrder()) {
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (Player player : board.getPlayerOrder()) {
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        determinePlayerOrder();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        if (!board.getPlayerOrder().isEmpty()) {
            board.setCurrentPlayer(board.getPlayerOrder().get(0));
        }
        board.setStep(0);
    }


    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
        spaceActions();
    }


    private boolean executeNextStep() {
        if (won) {
            return false;
        }

        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }

                int nextPlayerIndex = (board.getPlayerOrder().indexOf(currentPlayer) + 1) % board.getPlayersNumber();
                if (nextPlayerIndex != 0) {
                    board.setCurrentPlayer(board.getPlayerOrder().get(nextPlayerIndex));
                    return false; // Not all players have finished this step
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayerOrder().get(0)); // Set the first player for the next step
                    } else {
                        startProgrammingPhase();
                    }
                    return true; // All players have finished the current step
                }
            } else {
                // This should not happen
                return false;
            }
        } else {
            // This should not happen
            return false;
        }
    }



    private void spaceActions() {
        for (Player player : board.getPlayers()) {
            if (won) {
                break;
            }

            Space space = player.getSpace();
            FieldAction fieldAction = space.getFieldAction();
            if (fieldAction instanceof ConveyorBelt) {
                ConveyorBelt conveyorBelt = (ConveyorBelt) fieldAction;
                conveyorBelt.doAction(this, space);
            } else if (fieldAction instanceof Checkpoint) {
                Checkpoint checkpoint = (Checkpoint) fieldAction;
                checkpoint.doAction(this, space);
            } else if (fieldAction instanceof Gear) {
                Gear gear = (Gear) fieldAction;
                gear.doAction(this, space);
            }
        }
    }

    private void uTurn(@NotNull Player player) {
        player.setHeading(player.getHeading().next().next());
    }

    private void backUp(@NotNull Player player) {
        if (!won) {
            Space currentSpace = player.getSpace();
            Heading oppositeHeading = player.getHeading().prev().prev();
            Space targetSpace = board.getNeighbour(currentSpace, oppositeHeading);

            if (targetSpace != null && !targetSpace.getWalls().contains(oppositeHeading)) {
                try {
                    moveToSpace(player, targetSpace, oppositeHeading);
                } catch (ImpossibleMoveException e) {
                    // Handle exception if the move is not possible
                }
            }
        }
    }

    private void powerUp(@NotNull Player player) {
        if (!won) {
            player.addEnergyCube();
        }
    }

    private void executeCommand(@NotNull Player player, Command command) {
        if (!won && player != null && player.board == board && command != null) {
            if (command != Command.AGAIN) {
                player.setLastCommand(command);
            }

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case OPTION_LEFT_RIGHT:
                    this.leftOrRight(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                case BACK_UP:
                    this.backUp(player);
                    break;
                case POWER_UP:
                    this.powerUp(player);
                    break;
                case AGAIN:
                    this.executeAgain(player);
                    break;
                case FAST_FAST_FORWARD:
                    this.fastFastForward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    private void moveForwardInDirection(Player player, Heading heading) {
        if (!won && player.board == board) {
            Space space = player.getSpace();
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // Handle impossible move exception
                }
            }
        }
    }

    private void executeAgain(@NotNull Player player) {
        if (!won) {
            Command lastCommand = player.getLastCommand();
            if (lastCommand != null) {
                executeCommand(player, lastCommand);
            } else {
                System.out.println("No previous command to repeat or not allowed!");
            }
        }
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        if (!won) {
            CommandCard sourceCard = source.getCard();
            CommandCard targetCard = target.getCard();
            if (sourceCard != null && targetCard == null) {
                target.setCard(sourceCard);
                source.setCard(null);
                return true;
            }
        }
        return false;
    }

    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }
    /**
     * Applies damage effects to the current player based on the specified damage type.
     * This method handles different types of damage that can affect game strategy and player progress.
     *
     * @param currentPlayer The player currently affected by the damage effect.
     * @param damageType The type of damage to apply, which determines the effect executed.
     * @author Aisha Farah, student ID: 235123
     */
    public void applyDamageEffects(@NotNull Player currentPlayer, DamageType damageType) {
        System.out.println("Applying effects to " + currentPlayer.getName());
        switch (damageType) {
            case SPAM:
                System.out.println("Applying SPAM damage to " + currentPlayer.getName());
                break;
            case WORM:
                rebootPlayer(currentPlayer);
                break;
            case TROJAN_HORSE:
                giveAdditionalDamageCards(currentPlayer, DamageType.SPAM);
                break;
            case VIRUS:
                spreadVirusDamage(currentPlayer);
                break;
        }
    }

    /**
     * Resets the player's position to the starting point on the game board.
     *
     * @param player The player whose position needs to be reset.
     * @author Aisha Farah, student ID: 235123
     */
    private void rebootPlayer(Player player) {
        player.setSpace(board.getPlayerStartingPoint());
        System.out.println("Player " + player.getName() + " is rebooted to starting position.");
    }

    /**
     * Adds a specific type of damage cards to the player's discard pile.
     *
     * @param player The player who will receive the additional damage cards.
     * @param type The type of damage card to add.
     * @author Aisha Farah, student ID: 235123
     */
    private void giveAdditionalDamageCards(Player player, DamageType type) {
        for (int i = 0; i < 2; i++) {
            player.getDiscardPile().add(new DamageCard(type));
        }
    }

    /**
     * Spreads virus damage to all players within a specified radius of the source player.
     *
     * @param source The player from whom the virus originates.
     * @author Aisha Farah, student ID: 235123
     */
    private void spreadVirusDamage(Player source) {
        for (Player player : board.getPlayers()) {
            if (player != source && isWithinRadius(source.getSpace(), player.getSpace(), 6)) {
                player.getDiscardPile().add(new DamageCard(DamageType.VIRUS));
            }
        }
    }

    /**
     * Checks if a target space is within a specified radius of a source space.
     * This method is used to determine the proximity of one space to another, typically for area-of-effect damage.
     *
     * @param source The source space from which to measure.
     * @param target The target space to check against the source.
     * @param radius The radius within which the target must fall to be considered within range.
     * @return true if the target is within the specified radius of the source, false otherwise.
     */
    private boolean isWithinRadius(Space source, Space target, int radius) {
        int dx = Math.abs(source.getX() - target.getX());
        int dy = Math.abs(source.getY() - target.getY());
        return Math.sqrt(dx * dx + dy * dy) <= radius;
    }


    public void notImplemented() {
        assert false;
    }

    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }

    public void showWinningMessage(Player player) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Congratulations!");
        alert.setContentText(player.getName() + " has won the game!");
        this.won = true;

        alert.showAndWait();
    }
}