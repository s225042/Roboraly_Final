package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.WaitingController;
import dk.dtu.compute.se.pisd.roborally.model.WaitingRoom;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * WhatingromeView is a placeholder for the actual view content.
 */
public class WhatingromeView extends VBox implements ViewObserver {

    WaitingRoom waitingRoom;

    public WhatingromeView(WaitingController waitingController) {
        waitingRoom = waitingController.waitingRoom;

        // Create the Player label
        Label playerLabel = new Label("Player");

        // Create the Start button
        Button startButton = new Button("Start");

        //Skal bruge en bolyen
        //startButton.setOnAction(e -> appController.startGame());

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
        //this.setTop(topContainer);
    }

    /**
     * @param subject
     */
    @Override
    public void updateView(Subject subject) {

    }
}
