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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.PushPanel;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 75;
    final public static int SPACE_WIDTH = 60; // 75;

    public final Space space;

    /**
     * @param space
     * @author s225042 Rebecca Moss
     */

    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        this.setStyle("-fx-background-color: white;");


        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    /**
     * @author s225042 Rebecca Moss
     */
    private void updatePlayer() {
        Player player = space.getPlayer();
        String imagePath = null;
        ImageView imageView = new ImageView();

        if (player != null) {
            switch (player.getColor()) {
                case "red":
                    imagePath = getClass().getResource("/images/r6.png").toExternalForm();
                    break;
                case "green":
                    imagePath = getClass().getResource("/images/r5.png").toExternalForm();
                    break;
                case "blue":
                    imagePath = getClass().getResource("/images/r2.png").toExternalForm();
                    break;
                case "orange":
                    imagePath = getClass().getResource("/images/r4.png").toExternalForm();
                    break;
                case "grey":
                    imagePath = getClass().getResource("/images/r1.png").toExternalForm();
                    break;
                case "magenta":
                    imagePath = getClass().getResource("/images/r3.png").toExternalForm();
                    break;
            }
            Image image = new Image(imagePath);
            imageView.setImage(image);
            imageView.setFitWidth(SPACE_WIDTH);
            imageView.setFitHeight(SPACE_HEIGHT);
            switch (player.getHeading()) {
                case NORTH:
                    imageView.setRotate(180);
                    break;
                case SOUTH:
                    imageView.setRotate(0);
                    break;
                case EAST:
                    imageView.setRotate(270);
                    break;
                case WEST:
                    imageView.setRotate(90);
                    break;
            }

            this.getChildren().add(imageView);
        }

    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * @author s225042 Rebecca Moss
     * This function makes the belts visible on the board.
     * The belts are represented by arrows that point in the direction of the belt.
     */

    private void updateFieldactions() {
        String imagePath = null;
        ImageView imageView = new ImageView();

        Board board = space.getBoard();
        Antenna antenna = board.getAntenna();

        if (board.getRebootSpace() == space) {
            imagePath = getClass().getResource("/images/respawn.png").toExternalForm();
            imageView.setImage(new Image(imagePath));
            Heading rebootDirection = board.getRebootDirection();
            switch (rebootDirection) {
                case NORTH:
                    imageView.setRotate(0);
                    break;
                case EAST:
                    imageView.setRotate(90);
                    break;
                case SOUTH:
                    imageView.setRotate(180);
                    break;
                case WEST:
                    imageView.setRotate(270);
                    break;
            }
        } else if (antenna != null && antenna.x == space.x && antenna.y == space.y) {
            imagePath = getClass().getResource("/images/antenna.png").toExternalForm();
        } else if (space.getFieldAction() instanceof ConveyorBelt) {
            ConveyorBelt belt = (ConveyorBelt) space.getFieldAction();
            if (belt.getType() == ConveyorBelt.BeltType.GREEN) {
                imagePath = getClass().getResource("/images/green.png").toExternalForm();
            } else {
                imagePath = getClass().getResource("/images/blue.png").toExternalForm();
            }

            switch (belt.getHeading()) {
                case NORTH:
                    imageView.setRotate(0);
                    break;
                case EAST:
                    imageView.setRotate(90);
                    break;
                case SOUTH:
                    imageView.setRotate(180);
                    break;
                case WEST:
                    imageView.setRotate(270);
                    break;
            }
        } else if (space.getFieldAction() instanceof Checkpoint) {
            Checkpoint checkpoint = (Checkpoint) space.getFieldAction();
            switch (checkpoint.getCheckpointNr()) {
                case 1:
                    imagePath = getClass().getResource("/images/1.png").toExternalForm();
                    break;
                case 2:
                    imagePath = getClass().getResource("/images/2.png").toExternalForm();
                    break;
                case 3:
                    imagePath = getClass().getResource("/images/3.png").toExternalForm();
                    break;
                case 4:
                    imagePath = getClass().getResource("/images/4.png").toExternalForm();
                    break;
                case 5:
                    imagePath = getClass().getResource("/images/5.png").toExternalForm();
                    break;
                case 6:
                    imagePath = getClass().getResource("/images/6.png").toExternalForm();
                    break;
                case 7:
                    imagePath = getClass().getResource("/images/7.png").toExternalForm();
                    break;
                case 8:
                    imagePath = getClass().getResource("/images/8.png").toExternalForm();
                    break;
            }
        } else if (space.getFieldAction() instanceof Gear) {
            Gear gear = (Gear) space.getFieldAction();
            if (gear.getType() == Gear.GearType.LEFT) {
                imagePath = getClass().getResource("/images/gearLeft.png").toExternalForm();
            } else {
                imagePath = getClass().getResource("/images/gearRight.png").toExternalForm();
            }
        } else if (space.getFieldAction() instanceof Pit) {
            Pit pit = (Pit) space.getFieldAction();
            imagePath = getClass().getResource("/images/hole.png").toExternalForm();

        } else if (space.getFieldAction() instanceof PushPanel){
            PushPanel pushPanel = (PushPanel) space.getFieldAction();
            imagePath = getClass().getResource("/images/push24.png").toExternalForm();
            Image pushImage = new Image(imagePath);
            ImageView pushImageView = new ImageView(pushImage);
            pushImageView.setFitWidth(SPACE_WIDTH);
            pushImageView.setFitHeight(5);

            switch (pushPanel.getHeading()) {
                case NORTH:
                    imageView.setRotate(0);
                    break;
                case EAST:
                    imageView.setRotate(90);
                    break;
                case SOUTH:
                    imageView.setRotate(180);
                    break;
                case WEST:
                    imageView.setRotate(270);
                    break;
            }

        }
        
        else {
            imagePath = getClass().getResource("/images/empty.png").toExternalForm();
        }

        if (imagePath == null || imagePath.isEmpty()) {
            System.err.println("Image path could not be resolved.");
        } else {
            Image image = new Image(imagePath);
            imageView.setImage(image);
            imageView.setFitWidth(SPACE_WIDTH);
            imageView.setFitHeight(SPACE_HEIGHT);
            this.getChildren().add(imageView);
        }
    }

    /**
     * @author s225042 Rebecca Moss
     * This function makes the Lase visibel becos it wil be on many filds wher ther ar other filactions i mackes sens to macke a methed for it self.
     */
    private void updateLaser() {
        if (space.getFieldAction() instanceof Laiser) {
            Laiser laise = (Laiser) space.getFieldAction();
            Space currentSpace = space;

            while (true) {
                String imagePath = getClass().getResource("/images/laser.png").toExternalForm();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);

                imageView.setFitWidth(SPACE_WIDTH);
                imageView.setFitHeight(5);

                if (laise.getHeading() == SOUTH || laise.getHeading() == Heading.NORTH) {
                    imageView.setRotate(90);
                }

                this.getChildren().add(imageView);

                // Move to the next space in the direction of the laser
                Space nextSpace = currentSpace.board.getNeighbour(currentSpace, laise.getHeading());
                if (nextSpace == null || nextSpace.getWalls().contains(laise.getHeading())) {
                    break; // Exit if there is no neighbor or there's a wall blocking the laser
                }

                currentSpace = nextSpace;
            }
        }
    }



    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * @author s225042 Rebecca Moss
     * This function makes the walls visible on the board.
     */

    private void updateWalls() {
        Space space = this.space;
        if (space != null && !space.getWalls().isEmpty()) {
            for (Heading wall : space.getWalls()) {
                String imagePath = getClass().getResource("/images/wall.png").toExternalForm();
                Image wallImage = new Image(imagePath);
                ImageView wallImageView = new ImageView(wallImage);

                //wall size
                wallImageView.setFitWidth(5);
                wallImageView.setFitHeight(SPACE_HEIGHT);

                switch (wall) {
                    case EAST:
                        wallImageView.setTranslateX(SPACE_WIDTH / 2 - wallImageView.getFitWidth() / 2);
                        wallImageView.setTranslateY(0);
                        break;

                    case SOUTH:
                        wallImageView.setRotate(90);
                        wallImageView.setTranslateX(SPACE_WIDTH / 2 - wallImageView.getFitHeight() / 2); // Adjusted for rotation
                        wallImageView.setTranslateY(SPACE_HEIGHT / 2 - wallImageView.getFitWidth() / 2); // Align with the top side of the tile
                        break;

                    case WEST:
                        wallImageView.setTranslateX(-SPACE_WIDTH / 2 + wallImageView.getFitWidth() / 2);
                        wallImageView.setTranslateY(0);
                        break;

                    case NORTH:
                        wallImageView.setRotate(90);
                        wallImageView.setTranslateX(-SPACE_WIDTH / 2 + wallImageView.getFitHeight() / 2);
                        wallImageView.setTranslateY(-SPACE_HEIGHT / 2 + wallImageView.getFitWidth() / 2);
                        break;
                }
                this.getChildren().add(wallImageView);
            }
        }
    }

    private void updateSpawnPoint() {
        Board board = space.getBoard();

        if (board.isSpawnPoint(space)) {
            String imagePath = getClass().getResource("/images/startField.png").toExternalForm(); // Make sure you have an appropriate spawn point image
            Image image = new Image(imagePath);
            ImageView imageView = new ImageView(image);

            imageView.setFitWidth(SPACE_WIDTH);
            imageView.setFitHeight(SPACE_HEIGHT);

            this.getChildren().add(imageView);
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();
            updateFieldactions();
            updateLaser();
            updateWalls();
            updateSpawnPoint();
            updatePlayer();
        }
    }
}
