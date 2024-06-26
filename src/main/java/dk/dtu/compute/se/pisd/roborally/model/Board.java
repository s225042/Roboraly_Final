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
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.PushPanel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private List<Space> spaceBLueConveyor = new ArrayList<>();

    private List<Space> spacesGreanConveyor = new ArrayList<>();

    private List<Space> spacesGears = new ArrayList<>();

    private List<Space> laisers = new ArrayList<>();

    private List<Space> chekpoints = new ArrayList<>();

    private List<Space> pushPanels = new ArrayList<>();

    private List<SpawnPoint> spawnPoints = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private int counter = 0;

    private int maxNumberofChekpoints;

    private boolean stepMode;

    private final Antenna antenna;

    private List<Player> playersOrder = new ArrayList<>();

    private Space rebootSpace; // Assuming there is a single reboot space
    private Heading rebootDirection; // Default direction indicated by the arrow on the reboot token

    public Board(int width, int height, int antennaX, int antennaY) {
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.antenna = new Antenna(this, antennaX, antennaY);
        this.stepMode = false;
    }


    public Antenna getAntenna() {
        return antenna;
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

    public int getCounter() {return counter;}

    public void setCounter(int counter){
        this.counter = counter;
    }

    /**
     * @author Rebecca Moss, s225042@dtu.dk
     * @return int
     */
    public int getMaxNumberofChekpoints(){return maxNumberofChekpoints;}

    public void setMaxNumberofChekpoints(){
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space =  getSpace(x, y);
                spaces[x][y] = space;

                if (space.getFieldAction() instanceof ConveyorBelt){
                    ConveyorBelt conveyorBelt = (ConveyorBelt) space.getFieldAction();
                    if(conveyorBelt.getType() == ConveyorBelt.BeltType.BLUE){
                        spaceBLueConveyor.add(space);
                    }
                    else {
                        spacesGreanConveyor.add(space);
                    }
                }

                if (space.getFieldAction() instanceof  Gear){
                    spacesGears.add(space);
                }

                if (space.getFieldAction() instanceof PushPanel){
                    pushPanels.add(space);
                }

                if (space.getFieldAction() instanceof Laiser){
                    laisers.add(space);
                }

                if(space.getFieldAction() instanceof Checkpoint){
                    chekpoints.add(space);
                }
            }
        }
        maxNumberofChekpoints = chekpoints.size();
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }


    /** Author s235074 Dennis Eren Dogulu
     * Method to get the size of the array of playersOrder list
     * @return size of the playersOrder list which is the order of the players
     */
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
            counter ++;
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



    /** Author s235074 Dennis Eren Dogulu
     * Method to set the order of the players
     * @param playersOrder list of players
     */

    public void setPlayerOrder(List<Player> playersOrder) {
        this.playersOrder = playersOrder;
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
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    public List<Player> getPlayers() {
        return players;
    }




    /** Author s235074 Dennis Eren Dogulu
     * Method to get the order of the players
     * @return list of players
     */
    public List <Player> getPlayerOrder() {
        return playersOrder;
    }

    public List<Space> getSpaceBLueConveyor(){
        return spaceBLueConveyor;
    }

    public List<Space> getSpacesGreanConveyor(){
        return spacesGreanConveyor;
    }

    public List<Space> getSpacesGears(){
        return spacesGears;
    }

    public List<Space> getLaisers(){
        return laisers;
    }

    public List<Space> getChekpoints(){
        return chekpoints;
    }

    public List<Space> getPushPanels(){
        return pushPanels;
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;

        switch (heading) {
            case SOUTH:
                y = y + 1;
                break;
            case WEST:
                x = x - 1;
                break;
            case NORTH:
                y = y - 1;
                break;
            case EAST:
                x = x + 1;
                break;
        }

        // Check if the new coordinates are out of bounds
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }

        Space result = getSpace(x, y);
        if (result != null) {
            Heading reverse = Heading.values()[(heading.ordinal() + 2) % Heading.values().length];
            if (space.getWalls().contains(heading) || result.getWalls().contains(reverse)) {
                return space; // Returning the current space if there's a wall in the way
            }
        }

        return result;
    }



    public String getStatusMessage() {

        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", checkpoint tokens = " + getCurrentPlayer().getCheckpoint() +
                ", Step: " + getStep() +
                ", Counter;" + (counter-1);
    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * Returns the reboot direction of the board.
     * @return the reboot direction of the board
     */

    public Heading getRebootDirection() {
        return rebootDirection;
    }
    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * Sets the reboot direction of the board.
     * @param heading the reboot direction to be set
     */

    public void setRebootDirection(Heading heading) {
        if (heading == null) {
            throw new IllegalArgumentException("Reboot direction cannot be null.");
        }
        this.rebootDirection = heading;
    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * Returns the reboot space of the board.
     * @return the reboot space of the board
     */

    public Space getRebootSpace() {
        return rebootSpace;
    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * Returns the reboot space of the board.
     * @param x the x-coordinate of the reboot space
     * @param y the y-coordinate of the reboot space
     */
    public void setRebootSpace(int x, int y) {
        this.rebootSpace = getSpace(x, y);
    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * Returns the reboot space of the board.
     * @param spawnPoint the reboot space to be set
     */

    public void addSpawnPoint(SpawnPoint spawnPoint) {
        spawnPoints.add(spawnPoint);
    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * Returns the list of spawn points on the board.
     * @return spawnPoints the list of spawn points on the board
     */
    public List<SpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * Returns the spawn point of the board.
     * @param space the space to be checked
     * @return true if the space is a spawn point, false otherwise
     */

    public boolean isSpawnPoint(Space space) {
        for (SpawnPoint spawnPoint : spawnPoints) {
            if (spawnPoint.x == space.getX() && spawnPoint.y == space.getY()) {
                return true;
            }
        }
        return false;
    }

}