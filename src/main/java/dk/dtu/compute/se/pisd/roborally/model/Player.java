package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

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

    private List<CommandCard> discardPile = new ArrayList<>();
    private List<CommandCard> programmingDeck = new ArrayList<>();



    private boolean infected = false; // Track if the player is infected

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

    public List<CommandCard> getDiscardPile() {
        return discardPile;
    }

    public void setProgrammingDeck(List<CommandCard> deck) {
        this.programmingDeck = deck;
    }

    public void initializeProgrammingDeck() {
        if (programmingDeck.isEmpty()) {
            Map<Command, Integer> deckComposition = new HashMap<>();
            deckComposition.put(Command.FORWARD, 2);
            deckComposition.put(Command.FAST_FORWARD, 2);
            deckComposition.put(Command.OPTION_LEFT_RIGHT, 2);
            deckComposition.put(Command.OPTION_LEFT_RIGHT, 2);
            deckComposition.put(Command.FAST_FAST_FORWARD, 2);
            deckComposition.put(Command.U_TURN, 2);
            deckComposition.put(Command.BACK_UP, 2);
            deckComposition.put(Command.LEFT, 2);
            deckComposition.put(Command.RIGHT, 2);

            for (Map.Entry<Command, Integer> entry : deckComposition.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    programmingDeck.add(new CommandCard(entry.getKey()));
                }
            }

            Collections.shuffle(programmingDeck);
            System.out.println("Deck initialized: " + programmingDeck.stream().map(card -> card.command).collect(Collectors.toList()));

        }
    }

    public void takeDamage(CommandCard damageCard) {
        discardPile.add(damageCard);
        notifyChange(); // Notify observers of the change
        System.out.println("Damage card added to discard pile."); // Debugging line
    }


    public void shuffleDiscardPileIntoDeck() {
        programmingDeck.addAll(discardPile);
        discardPile.clear();
        Collections.shuffle(programmingDeck);
    }



    public void drawProgrammingCards(int count) {
        for (int i = 0; i < count; i++) {
            if (programmingDeck.isEmpty()) {
                shuffleDiscardPileIntoDeck();
            }
            if (!programmingDeck.isEmpty()) {
                CommandCard drawnCard = programmingDeck.remove(programmingDeck.size() - 1);
                cards[i].setCard(drawnCard);
                cards[i].setVisible(true);  // Ensure the card is visible
                System.out.println("Player " + name + " drew: " + drawnCard.command);
            }
        }
        notifyChange(); // Notify that the player's cards have changed
    }

    // In Player class
    public CommandCard drawRandomProgrammingCard() {
        if (programmingDeck.isEmpty()) {
            initializeProgrammingDeck();
        }
        Collections.shuffle(programmingDeck);
        return programmingDeck.isEmpty() ? null : programmingDeck.remove(0);
    }


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

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    // Method to apply virus damage to nearby players
    public void applyVirusDamage() {
        List<Player> playersWithinRadius = getPlayersWithinRadius();
        for (Player player : playersWithinRadius) {
            CommandCard virusCard = new CommandCard(Command.VIRUS);
            player.takeDamage(virusCard);
        }
    }

    // Method to get players within a radius of 6 spaces
    public List<Player> getPlayersWithinRadius() {
        List<Player> playersWithinRadius = new ArrayList<>();
        for (Player player : board.getPlayers()) {
            if (player != this && isWithinRadius(player.getSpace(), 6)) {
                playersWithinRadius.add(player);
            }
        }
        return playersWithinRadius;
    }

    // Method to check if a space is within a certain radius
    public boolean isWithinRadius(Space target, int radius) {
        if (target == null || this.space == null) {
            return false;
        }
        int dx = Math.abs(this.space.getX() - target.getX());
        int dy = Math.abs(this.space.getY() - target.getY());
        double distance = Math.sqrt(dx * dx + dy * dy);
        boolean withinRadius = distance <= radius;
        System.out.println("Checking radius for " + (target.getPlayer() != null ? target.getPlayer().getName() : "null") + ": " + withinRadius);
        return withinRadius;
    }


    // Method to play the virus card
    public void playVirusCard(CommandCard virusCard) {
        applyVirusDamage();
        discardPile.remove(virusCard);  // Remove the virus card from the discard pile
    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * Reboot the player by taking two SPAM damage cards and placing them in the player's discard pile.
     * Discard all programming cards and place the player on the reboot token.
     */

    public void reboot() {
        // Take two SPAM damage cards and place them in your discard pile
        CommandCard spamCard = new CommandCard(Command.SPAM);
            takeDamage(spamCard);
            takeDamage(spamCard);


        // Discard programming cards
        discardProgrammingCards();

        // Place robot on the reboot token
        moveToRebootToken();
    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * Move the player to the reboot token and set the player's heading according to the reboot direction.
     * If the reboot space is occupied, move the occupying player to an adjacent space.
     */

    public void moveToRebootToken() {
        Space rebootSpace = board.getRebootSpace();
        if (rebootSpace != null) {
            Heading rebootDirection = board.getRebootDirection();
            if (rebootDirection == null) {
                rebootDirection = Heading.NORTH; // Fallback to default direction
            }

            if (rebootSpace.getPlayer() == null) {
                setSpace(rebootSpace);
                setHeading(rebootDirection); // Set heading according to reboot direction
            } else {
                // Recursive handling for moving the occupying player
                moveOccupyingPlayer(rebootSpace, rebootDirection);
                setSpace(rebootSpace);
                setHeading(rebootDirection); // Set heading according to reboot direction
            }
        } else {
            // Handle case where reboot space is not set
            throw new IllegalStateException("Reboot space not set on the board.");
        }
    }

    /**
     * @Author s235112 Tobias Kolstrup Vittrup
     * Move the occupying player to an adjacent space in the direction of the reboot token.
     * If the target space is also occupied, recursively handle this scenario.
     * @param space the space occupied by the player
     * @param heading the direction of the reboot token
     */
    private void moveOccupyingPlayer(Space space, Heading heading) {
        Player occupyingPlayer = space.getPlayer();
        if (occupyingPlayer != null) {
            Space targetSpace = board.getNeighbour(space, heading);
            if (targetSpace != null && targetSpace.getPlayer() == null) {
                occupyingPlayer.setSpace(targetSpace);
                occupyingPlayer.setHeading(heading); // Ensure the occupying player is facing the correct direction

            } else if (targetSpace != null) {
                // If the target space is also occupied, recursively handle this scenario
                moveOccupyingPlayer(targetSpace, heading);
                occupyingPlayer.setSpace(targetSpace);
                occupyingPlayer.setHeading(heading); // Ensure the occupying player is facing the correct direction

            } else {
                // Handle case where no target space is free or out of bounds
                throw new IllegalStateException("No unoccupied adjacent space in the direction of the reboot token.");
            }
        }
    }
}