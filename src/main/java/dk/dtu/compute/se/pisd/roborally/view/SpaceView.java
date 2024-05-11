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
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

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


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

       if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    20.0, 40.0,
                    40.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * This function makes the belts visible on the board.
     * The belts are represented by arrows that point in the direction of the belt.
     */

    private void updateBelt(){
        ConveyorBelt belt = space.getConveyorBelt();
        if (belt != null) {
            Polygon arrow = new Polygon(
                    0.0, 0.0,
                    30.0, 60.0,
                    60.0, 0.0
            );
            arrow.setFill(belt.getType() == ConveyorBelt.BeltType.GREEN ? Color.GREEN : Color.BLUE);
            arrow.setRotate((90 * belt.getHeading().ordinal()) % 360);

            this.getChildren().add(arrow);
        }
    }

    /**
     * @author s235112 Tobias Kolstrup Vittrup
     * This function makes the walls visible on the board.
     */

    private void updateWalls() {
        Space space = this.space;
        if (space != null && !space.getWalls().isEmpty()) {
            for (Heading wall : space.getWalls()) {
                Rectangle wallRect = new Rectangle(70, 5); // Rectangle dimensions

                // Adjust position based on wall orientation and tile size
                switch (wall) {
                    case EAST:
                        wallRect.setTranslateX(45.0); // Align with right side of the tile
                        wallRect.setTranslateY((90*wall.ordinal()) % 360); // Center vertically in the tile
                        break;

                    case SOUTH:
                        wallRect.setTranslateY(32.5); // Align with bottom of the tile
                        break;

                    case WEST:
                        wallRect.setTranslateX(-45.0); // Align with left side of the tile
                        wallRect.setTranslateY((90*wall.ordinal()) % 360); // Center vertically in the tile
                        break;

                    case NORTH:
                        wallRect.setTranslateY(-32.5); // Align with top of the tile
                        break;
                }

                wallRect.setFill(Color.ORANGE);
                this.getChildren().add(wallRect); // Assuming 'this' is a container like Group or Pane
            }
        }
    }


    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();
            updateBelt();
            updateWalls();
            updatePlayer();

        }
    }
}
