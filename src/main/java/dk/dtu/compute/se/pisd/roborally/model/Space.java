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
import javafx.scene.image.Image;
import dk.dtu.compute.se.pisd.roborally.controller.PushPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    private Player player;

    private Antenna antenna;

    private List<Heading> walls = new ArrayList<>();

    private List<Heading> pushPanel = new ArrayList<>();
    private List<FieldAction> actions = new ArrayList<>();

    public final Board board;

    public final int x;
    public final int y;

    private boolean hasPowerUp;
    private Image powerUpImage;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Board getBoard() {
        return board;
    }

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
    }

    public Antenna getAntenna() {
        return antenna;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    public List<Heading> getWalls() {
        return walls;
    }

    public List<Heading> getPushPanel(){
        return pushPanel;
    }


    /**
     * @Author s235074
     * Add a wall in the given direction to this space.
     *
     * @param heading the direction of the wall to be added
     */

    public void addWall (Heading heading){
        if(!walls.contains(heading)){
            walls.add(heading);
            notifyChange();
        }
    }

    public List<FieldAction> getActions() {
        return actions;
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    /**
     * This class is used to add a wall to the space.
     * @return belt
     */
    public FieldAction getFieldAction() {

        FieldAction fieldAction = null;

        for (FieldAction action : this.actions) {
            if (action instanceof ConveyorBelt && fieldAction == null) {
                fieldAction = (ConveyorBelt) action;
            }
            if (action instanceof  Checkpoint && fieldAction == null){
                fieldAction = (Checkpoint) action;
            }
            if (action instanceof Gear && fieldAction == null){
                fieldAction = (Gear) action;
            }
            if (action instanceof Laiser && fieldAction == null){
                fieldAction = (Laiser) action;
            }
            if (action instanceof  Pit && fieldAction == null){
                fieldAction = (Pit) action;
            }
            if(action instanceof PushPanel && fieldAction == null){
                fieldAction = (PushPanel) action;
            }
        }
        return fieldAction;
    }





    public boolean hasPowerUp() {
        return hasPowerUp;
    }

    public void setHasPowerUp(boolean hasPowerUp) {
        this.hasPowerUp = hasPowerUp;
        notifyChange();  // Notify change when power-up status changes
    }

    public Image getPowerUpImage() {
        return powerUpImage;
    }

    public void setPowerUpImage(Image powerUpImage) {
        this.powerUpImage = powerUpImage;
        notifyChange();  // Notify change when power-up image changes
    }
}
