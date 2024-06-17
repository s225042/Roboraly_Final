package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GameController {

    private Image energyCube;
    final public Board board;
    public boolean won = false;
    private List<CommandCard> damageDeck = new ArrayList<>();

    public GameController(Board board) {
        this.board = board;
        loadEnergyCubeImage();
        initializeDamageDeck();
    }

    private void loadEnergyCubeImage() {
        try {
            energyCube = new Image(getClass().getResource("/images/energyCube.png").toExternalForm());
            System.out.println("Image loaded successfully: " + energyCube.getUrl());
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void determinePlayerOrder(){
        List<Player> players = new ArrayList<>(board.getPlayers());
        players.sort(Comparator.comparingInt(player -> board.getAntenna().calculateDistance(player)));
        board.setPlayerOrder(players);
    }

    private void initializeDamageDeck() {
        for (int i = 0; i < 20; i++) {
            damageDeck.add(new CommandCard(Command.SPAM));
        }
        Collections.shuffle(damageDeck);
    }

    public CommandCard drawDamageCard() {
        if (damageDeck.isEmpty()) {
            initializeDamageDeck();
        }
        return damageDeck.remove(damageDeck.size() - 1);
    }

    public void applySpamDamage(Player player) {
        CommandCard spamCard = drawDamageCard();
        player.takeDamage(spamCard);
        showDamageMessage(player);
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
        if (!won && player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
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
        assert board.getNeighbour(player.getSpace(), heading) == space;
        Player other = space.getPlayer();
        if (other != null) {
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                moveToSpace(other, target, heading);
                assert target.getPlayer() == null : target;
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
                    return false;
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayerOrder().get(0));
                    } else {
                        startProgrammingPhase();
                    }
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void spaceActions() {
        for (Space space: board.getSpaceBLueConveyor()){
            ConveyorBelt conveyorBelt = (ConveyorBelt) space.getFieldAction();
            conveyorBelt.doAction(this, space);
        }
        for (Space space: board.getSpacesGreanConveyor()){
            ConveyorBelt conveyorBelt = (ConveyorBelt) space.getFieldAction();
            conveyorBelt.doAction(this, space);
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

    private void backUp(@NotNull Player player) {
        if (!won) {
            Space currentSpace = player.getSpace();
            Heading oppositeHeading = player.getHeading().prev().prev();
            Space targetSpace = board.getNeighbour(currentSpace, oppositeHeading);
            if (targetSpace != null && !targetSpace.getWalls().contains(oppositeHeading)) {
                try {
                    moveToSpace(player, targetSpace, oppositeHeading);
                } catch (ImpossibleMoveException e) {
                }
            }
        }
    }

    private void updateSpaceViewWithPowerUp(@NotNull Player player) {
        Space playerSpace = player.getSpace();
        if (playerSpace != null) {
            playerSpace.setHasPowerUp(true);
            playerSpace.setPowerUpImage(energyCube);
        }
    }

    private void powerUp(@NotNull Player player) {
        if (!won) {
            player.addEnergyCube();
            updateSpaceViewWithPowerUp(player);
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
                player.initializeProgrammingDeck();
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                player.drawProgrammingCards(Player.NO_CARDS);
            }
        }
    }

    private void rebootPlayer(Player player) {
        player.setSpace(board.getPlayerStartingPoint());
        System.out.println("Player " + player.getName() + " is rebooted to starting position.");
    }

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

    private void showDamageMessage(Player player) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Damage Taken");
        alert.setHeaderText("Robot hit by laser!");
        alert.setContentText(player.getName() + " has drawn a SPAM card.");
        alert.showAndWait();
    }
}

