package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;

    private String name;
    private String color;

    private Space space;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] cards;

    private Command lastCommand;
    private int energyCubes = 0;  // Initialize energy cubes to zero

    private int checkpoint = 0;

    private int spamCount = 0; // to count the number of SPAM cards

    private static Queue<Player> rebootQueue = new LinkedList<>();


    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public Space getSpace() {
        return space;
    }

    public void setCheckpoint(int checkpoint) {
        this.checkpoint = checkpoint;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public void addEnergyCube() {
        this.energyCubes++;
        notifyChange();  // Notify observers that the player's energy cube count has changed
    }

    public int getEnergyCubes() {
        return this.energyCubes;
    }

    public int getCheckpoint() {
        return this.checkpoint;
    }

    public void setLastCommand(Command command) {
        this.lastCommand = command;
    }

    public Command getLastCommand() {
        return this.lastCommand;
    }

    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    public void addSpamCard() {
        this.spamCount++;
        notifyChange(); // Notify observers that the player's SPAM card count has changed
    }

    public void resetSpamCards() {
        this.spamCount = 0;
        notifyChange();
    }

    public int getSpamCount() {
        return this.spamCount;
    }

    // Additional methods...


    private void discardProgrammingCards() {
        for (int i = 0; i < NO_REGISTERS; i++) {
            program[i].setCard(null);
            program[i].setVisible(false);
        }
        for (int i = 0; i < NO_CARDS; i++) {
            cards[i].setCard(null);
            cards[i].setVisible(false);
        }
    }

    private Space findUnoccupiedAdjacentSpace(Space space) {
        for (Heading heading : Heading.values()) {
            Space adjacentSpace = board.getNeighbour(space, heading);
            if (adjacentSpace != null && adjacentSpace.getPlayer() == null) {
                return adjacentSpace;
            }
        }
        return null;
    }

    public void reboot() {
        // Take two SPAM damage cards and place them in your discard pile
        addSpamCard();
        addSpamCard();

        // Discard programming cards
        discardProgrammingCards();

        // Place robot on the reboot token
        moveToRebootToken();
    }

    private void moveToRebootToken() {
        Space rebootSpace = board.getRebootSpace();
        if (rebootSpace != null) {
            if (rebootSpace.getPlayer() == null) {
                setSpace(rebootSpace);
                setHeading(Heading.NORTH); // Default facing direction
            } else {
                Space adjacentSpace = findUnoccupiedAdjacentSpace(rebootSpace);
                if (adjacentSpace != null) {
                    setSpace(adjacentSpace);
                    setHeading(Heading.NORTH); // Default facing direction
                } else {
                    // Handle case where no adjacent space is free
                    throw new IllegalStateException("No unoccupied adjacent space near the reboot token.");
                }
            }
        } else {
            // Handle case where reboot space is not set
            throw new IllegalStateException("Reboot space not set on the board.");
        }
    }
}
