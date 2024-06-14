package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * WhatingromeView is a placeholder for the actual view content.
 */
public class WhatingromeView extends BorderPane {

    public WhatingromeView(AppController appController) {
        // Create the Player label
        Label playerLabel = new Label("Player");

        // Create the Start button
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> appController.startGame());

        // Create a container for the top part
        HBox topContainer = new HBox();
        topContainer.setAlignment(Pos.TOP_LEFT);

        // Add the Player label to the top left
        topContainer.getChildren().add(playerLabel);

        // Add an empty HBox for spacing
        HBox spacer = new HBox();
        spacer.setMinWidth(500); // Adjust this width as needed

        // Add the Start button to the top right
        HBox startButtonContainer = new HBox();
        startButtonContainer.setAlignment(Pos.TOP_RIGHT);
        startButtonContainer.getChildren().add(startButton);

        // Add the components to the top container
        topContainer.getChildren().addAll(spacer, startButtonContainer);

        // Set the top container to the top of the BorderPane
        this.setTop(topContainer);
    }
}
