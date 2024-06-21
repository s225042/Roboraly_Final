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

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerServer;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import java.util.*;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    final private HttpController httpController;

    public boolean won = false;
    public String playerName;
    private Queue<Player> rebootQueue = new LinkedList<>();
    private List<CommandCard> damageDeck = new ArrayList<>();


    public GameController(Board board, HttpController httpController) {
        this.board = board;
        this.httpController = httpController;
        initializeDamageDeck();
    }



    public void determinePlayerOrder(){
        List<Player> players = new ArrayList<>(board.getPlayers());
        players.sort(Comparator.comparingInt(player -> board.getAntenna().calculateDistance(player)));
        board.setPlayerOrder(players);
    }

    private void initializeDamageDeck() {
        int totalCards = 40; // Total number of damage cards in the deck
        int cardTypes = 4; // Number of different damage card types
        int cardsPerType = totalCards / cardTypes; // Number of each type of damage card

        // Add SPAM cards
        for (int i = 0; i < cardsPerType; i++) {
            damageDeck.add(new CommandCard(Command.SPAM));
        }

        // Add TROJAN_HORSE cards
        for (int i = 0; i < cardsPerType; i++) {
            damageDeck.add(new CommandCard(Command.TROJAN_HORSE));
        }

        // Add WORM cards
        for (int i = 0; i < cardsPerType; i++) {
            damageDeck.add(new CommandCard(Command.WORM));
        }

        // Add VIRUS cards
        for (int i = 0; i < cardsPerType; i++) {
            damageDeck.add(new CommandCard(Command.VIRUS));
        }

        Collections.shuffle(damageDeck);
    }

    public CommandCard drawRandomDamageCard() {
        if (damageDeck.isEmpty()) {
            initializeDamageDeck();
        }
        int randomIndex = new Random().nextInt(damageDeck.size());
        return damageDeck.remove(randomIndex);
    }


    public void applyTrojanHorseDamage(Player player) {
        System.out.println("Applying Trojan Horse damage.");
        player.takeDamage(new CommandCard(Command.SPAM));
        player.takeDamage(new CommandCard(Command.SPAM));
        showTrojanHorseMessage(player);
    }

    public void applyWormDamage(Player player) {
        System.out.println("Applying WORM damage.");
        rebootPlayer(player);
        showWormMessage(player);
    }

    public void applyVirusDamage(Player player) {
        System.out.println("Applying VIRUS damage.");
        player.setInfected(true); // Infect the player
        spreadVirus(player);
    }

    public void spreadVirus(Player infectedPlayer) {
        System.out.println("Spreading virus from player: " + infectedPlayer.getName());
        List<Player> playersWithinRadius = getPlayersWithinRadius(infectedPlayer);
        for (Player player : playersWithinRadius) {
            if (!player.isInfected()) {
                player.setInfected(true);
                System.out.println("Player " + player.getName() + " is infected.");
                // Each newly infected player draws a virus card
                player.takeDamage(new CommandCard(Command.VIRUS));
                showVirusCardMessage(player);
            }
        }
    }

    public List<Player> getPlayersWithinRadius(Player sourcePlayer) {
        List<Player> playersWithinRadius = new ArrayList<>();
        for (Player player : board.getPlayers()) {
            if (player != sourcePlayer && isWithinRadius(sourcePlayer.getSpace(), player.getSpace(), 6)) {
                playersWithinRadius.add(player);
            }
        }
        return playersWithinRadius;
    }

    private boolean isWithinRadius(Space source, Space target, int radius) {
        int dx = Math.abs(source.getX() - target.getX());
        int dy = Math.abs(source.getY() - target.getY());
        return Math.sqrt(dx * dx + dy * dy) <= radius;
    }

    public void applyRandomDamage(Player player) {
        CommandCard damageCard = drawRandomDamageCard();
        switch (damageCard.command) {
            case TROJAN_HORSE:
                applyTrojanHorseDamage(player);
                break;
            case WORM:
                applyWormDamage(player);
                break;
            case VIRUS:
                applyVirusDamage(player);
                break;
            default:
                player.takeDamage(damageCard);
                showDamageMessage(player, damageCard);
                break;
        }
    }

    public void executeSpamDamageCard(Player player, CommandCard damageCard) {
        if (damageCard.command == Command.SPAM) {
            System.out.println("Executing SPAM card action");
            CommandCard randomCard;
            do {
                randomCard = player.drawRandomProgrammingCard();
            } while (randomCard != null && randomCard.command == Command.SPAM);

            if (randomCard != null) {
                executeCommand(player, randomCard.command);
            }
            player.getDiscardPile().add(damageCard);
        }
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
                    // Handle the exception appropriately
                }
            } else {
                // Reboot the player if moving out of bounds
                rebootPlayer(player);
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

    /**
     * @Author s235074 Dennis Eren Dogulu
     * Move the player forward and check if the player is on the reboot space before it moves further
     * @param player
     */

    public void fastForward(@NotNull Player player) {
        moveForward(player);
        if(player.getSpace() != board.getRebootSpace())
        moveForward(player);

        else;
    }

    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
    }

    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
    }

    /**
     * @Author s235074 Dennis Eren Dogulu
     * Move the player and check if the player is on the reboot space before it moves further
     * @param player
     */

    public void fastFastForward(@NotNull Player player) {
        moveForward(player);
        if(player.getSpace() != board.getRebootSpace()) {
            moveForward(player);
        }
        if(player.getSpace() != board.getRebootSpace()) {
            moveForward(player);
        }

    }

    void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space; // make sure the move to here is possible in principle

        // Check if the space is within board boundaries
        if (space.getX() < 0 || space.getX() >= board.getWidth() || space.getY() < 0 || space.getY() >= board.getHeight()) {
            // Space is out of bounds, trigger a reboot
            rebootPlayer(player);
            return;
        }

        Player other = space.getPlayer();
        if (other != null) {
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // Check if the target space is within board boundaries
                if (target.getX() < 0 || target.getX() >= board.getWidth() || target.getY() < 0 || target.getY() >= board.getHeight()) {
                    // Target space is out of bounds, trigger a reboot for the other player
                    rebootPlayer(other);
                    return;
                }

                // Ensure the target is free now
                assert target.getPlayer() == null : target;
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }

        /* Handle the field action if the space is valid
        FieldAction fieldAction = space.getFieldAction();
        if (fieldAction instanceof ConveyorBelt) {
            ConveyorBelt conveyorBelt = (ConveyorBelt) fieldAction;
            Heading beltHeading = conveyorBelt.getHeading();
            for (int i = 0; i < conveyorBelt.getMovement(); i++) {
                space = board.getNeighbour(space, beltHeading);
                if (space == null || space.getX() < 0 || space.getX() >= board.getWidth() || space.getY() < 0 || space.getY() >= board.getHeight()) {
                    rebootPlayer(player);
                    return;
                }
            }
        }*/

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
        Player player = board.getCurrentPlayer();
        int playernr = board.getPlayerNumber(player);

        determinePlayerOrder();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        if (!board.getPlayerOrder().isEmpty()) {
            board.setCurrentPlayer(board.getPlayerOrder().get(0));
        }
        board.setStep(0);

        Lobby lobby;
        try {
            lobby = httpController.getByGameID(board.getGameId());
            PlayerServer playerServer = lobby.getPlayers().get(playernr);
            playerServer.setProgrammingDone(true);
            httpController.updatePlayer(playerServer.getPlayerID(), playerServer);
            Polling.finishProgramming(lobby.getID());
            lobby = httpController.getByGameID(board.getGameId());
            for (int i = 0; i<board.getPlayersNumber(); i++){
                for (int j = 1; j<=5.; j++){
                    switch (j){
                        case 1:
                            board.getPlayer(i).getProgramField(j).setCard( new CommandCard(new Command(lobby.getPlayers().get(i).getProgram1())));
                            break;

                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                    }
                }
            }
        }
        catch (Exception e){

        }


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

    /**
     * s225042, Rebecca Moss
     * This is the actions that hapens after eatch turn is taken for the spaces
     */


    private void spaceActions() {
        for (Player player : board.getPlayers()) {
            if (won) {
                break;
            }
            Space space = player.getSpace();
            FieldAction fieldAction = space.getFieldAction();
            if (fieldAction instanceof Pit) {
                Pit pit = (Pit) fieldAction;
                pit.doAction(this, space);
            }
        }
        for (Space space: board.getSpaceBLueConveyor()){
            ConveyorBelt conveyorBelt = (ConveyorBelt) space.getFieldAction();
            conveyorBelt.doAction(this, space);
        }
        for (Space space: board.getSpacesGreanConveyor()){
            ConveyorBelt conveyorBelt = (ConveyorBelt) space.getFieldAction();
            conveyorBelt.doAction(this, space);
        }
        for (Space space: board.getPushPanels()){
            PushPanel pushPanel = (PushPanel) space.getFieldAction();
            pushPanel.doAction(this, space);
        }
        for (Space space: board.getSpacesGears()){
            Gear gear = (Gear) space.getFieldAction();
            gear.doAction(this, space);
        }
        for (Space space: board.getLaisers()){
            Laiser laiser = (Laiser) space.getFieldAction();
            laiser.doAction(this, space);
        }
        for (Space space: board.getChekpoints()){
            Checkpoint checkpoint = (Checkpoint) space.getFieldAction();
            checkpoint.doAction(this, space);
        }
    }


    private void uTurn(@NotNull Player player) {
        player.setHeading(player.getHeading().next().next());
    }

    public void backUp(@NotNull Player player) {
        if (!won) {
            Space currentSpace = player.getSpace();
            Heading oppositeHeading = player.getHeading().prev().prev();
            Space targetSpace = board.getNeighbour(currentSpace, oppositeHeading);

            if (targetSpace != null && !targetSpace.getWalls().contains(oppositeHeading)) {
                try {
                    moveToSpace(player, targetSpace, oppositeHeading);
                } catch (ImpossibleMoveException e) {
                    // Handle the exception appropriately
                }
            } else {
                // Reboot the player if moving out of bounds
                rebootPlayer(player);
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
                case SPAM:
                    this.executeSpamDamageCard(player, new CommandCard(Command.SPAM));
                    break;
                case TROJAN_HORSE:
                    this.applyTrojanHorseDamage(player);
                    break;
                case WORM:
                    this.applyWormDamage(player);
                    break;
                case VIRUS:
                    this.applyVirusDamage(player);
                    break;
                default:
                    break;
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
        for (int i = 0; i<board.getPlayers().size(); i++){
            Player player = board.getPlayer(i);
            if(player.getName().equals(playerName)){
                board.setCurrentPlayer(board.getPlayer(i));
            }
        }
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                player.initializeProgrammingDeck(); // Initialize the deck if needed
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                player.drawProgrammingCards(Player.NO_CARDS); // Draw cards for the player
            }
        }
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

    private void showDamageMessage(Player player, CommandCard damageCard) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Damage Taken");
        alert.setHeaderText("Robot hit by laser!");
        alert.setContentText(player.getName() + " has drawn a " + damageCard.command + " card.");
        alert.showAndWait();
    }

    private void showTrojanHorseMessage(Player player) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Damage Taken");
        alert.setHeaderText("Trojan Horse Activated!");
        alert.setContentText(player.getName() + " has drawn two SPAM cards.");
        alert.showAndWait();
    }

    private void showWormMessage(Player player) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Damage Taken");
        alert.setHeaderText("WORM Activated!");
        alert.setContentText(player.getName() + " has been rebooted.");
        alert.showAndWait();
    }

    private void showVirusCardMessage(Player player) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Virus Card");
        alert.setHeaderText("Virus Activated!");
        alert.setContentText(player.getName() + " is now infected and spreads the virus to nearby players.");
        alert.showAndWait();
    }



    /**
     * @Author s235112
     * Reboot the player and add it to the reboot queue
     * @param player
     */
    // Reboot player
    public void rebootPlayer(@NotNull Player player) {
        player.reboot();
    }
}