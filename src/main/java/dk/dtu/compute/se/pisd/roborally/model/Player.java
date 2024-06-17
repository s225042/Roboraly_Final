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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
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
    private List<CommandCard> discardPile = new ArrayList<>();
    private List<CommandCard> programmingDeck = new ArrayList<>();

    private boolean infected = false; // Track if the player is infected




    private int energyCubes = 0;  // Initialize energy cubes to zero


    private int checkpoint = 0;

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

    public void setCheckpoint(int checkpoint){
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

    public void initializeProgrammingDeck() {
        if (programmingDeck.isEmpty()) {
            Map<Command, Integer> deckComposition = new HashMap<>();
            deckComposition.put(Command.FORWARD, 2);
            deckComposition.put(Command.FAST_FORWARD, 2);
            deckComposition.put(Command.OPTION_LEFT_RIGHT, 2);
            deckComposition.put(Command.FAST_FAST_FORWARD, 2);
            deckComposition.put(Command.U_TURN, 2);
            deckComposition.put(Command.BACK_UP, 2);
            deckComposition.put(Command.POWER_UP, 2);
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

    public CommandCard drawProgrammingCard() {
        if (programmingDeck.isEmpty()) {
            shuffleDiscardPileIntoDeck();
        }
        return programmingDeck.isEmpty() ? null : programmingDeck.remove(programmingDeck.size() - 1);
    }

    public void drawProgrammingCards(int count) {
        for (int i = 0; i < count; i++) {
            if (programmingDeck.isEmpty()) {
                shuffleDiscardPileIntoDeck();
            }
            if (!programmingDeck.isEmpty()) {
                CommandCard drawnCard = programmingDeck.remove(programmingDeck.size() - 1);
                cards[i].setCard(drawnCard);
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


    public void addEnergyCube() {
        this.energyCubes++;
        notifyChange();  // Notify observers that the player's energy cube count has changed
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

    public void takeVirusDamage(CommandCard damageCard) {
        if (damageCard.command == Command.VIRUS && !this.isInfected()) {
            this.applyVirusDamage();
        } else {
            discardPile.add(damageCard);
            notifyChange(); // Notify observers of the change
            System.out.println("Damage card added to discard pile."); // Debugging line
        }
    }




    public int getEnergyCubes() {
        return this.energyCubes;
    }

    public int getCheckpoint(){return this.checkpoint;}

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

    public List<CommandCard> getDiscardPile() {
        return discardPile;
    }

    public void setProgrammingDeck(List<CommandCard> deck) {
        this.programmingDeck = deck;
    }





}



