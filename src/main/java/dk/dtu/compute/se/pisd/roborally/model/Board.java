package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

public class Board extends Subject {

    public final int width;
    public final int height;
    private Integer gameId;
    private final Space[][] spaces;
    private final List<Player> players = new ArrayList<>();
    private List<Player> playersOrder = new ArrayList<>();
    private Player current;
    private Phase phase = INITIALISATION;
    private int step = 0;
    private int counter = 0;
    private int maxNumberofChekpoints;
    private boolean stepMode;
    private final Antenna antenna;

    public Board(int width, int height, int antennaX, int antennaY) {
        this.width = width;
        this.height = height;
        this.spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.antenna = new Antenna(this, antennaX, antennaY);
        this.stepMode = false;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getMaxNumberofChekpoints() {
        return maxNumberofChekpoints;
    }

    public void setMaxNumberofChekpoints() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space = getSpace(x, y);
                spaces[x][y] = space;
                if (space.getFieldAction() instanceof Checkpoint) {
                    Checkpoint chekpoint = (Checkpoint) space.getFieldAction();
                    if (chekpoint.getCheckpointNr() > maxNumberofChekpoints) {
                        maxNumberofChekpoints = chekpoint.getCheckpointNr();
                    }
                }
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public int getPlayersNumber() {
        return playersOrder.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            playersOrder.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < playersOrder.size()) {
            return playersOrder.get(i);
        } else {
            return null;
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            counter++;
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return playersOrder.indexOf(player);
        } else {
            return -1;
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayerOrder(List<Player> playersOrder) {
        this.playersOrder = playersOrder;
    }

    public List<Player> getPlayerOrder() {
        return playersOrder;
    }

    public Antenna getAntenna() {
        return antenna;
    }

    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        if (space.getWalls().contains(heading)) {
            return null;
        }

        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }
        Heading reverse = Heading.values()[(heading.ordinal() + 2) % Heading.values().length];
        Space result = getSpace(x, y);
        if (result != null) {
            if (result.getWalls().contains(reverse)) {
                return null;
            }
        }
        return result;
    }

    public String getStatusMessage() {
        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", checkpoint tokens = " + getCurrentPlayer().getCheckpoint() +
                ", Step: " + getStep() +
                ", Counter:" + (counter - 1);
    }
}
