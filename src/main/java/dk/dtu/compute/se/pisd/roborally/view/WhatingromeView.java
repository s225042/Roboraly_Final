package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.HttpController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerServer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * WhatingromeView is a placeholder for the actual view content.
 */
public class WhatingromeView extends VBox implements ViewObserver {

    private Lobby lobby;
    private List<PlayerServer> playerLabels;

    private HttpController httpController = new HttpController();

    public WhatingromeView(int id) {
        try {
            this.lobby = httpController.getByGameID(id);
        } catch (Exception er) {
            throw new RuntimeException(er);
        }

        // List of players
        playerLabels = lobby.getPlayers();

        // Create the Start button
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            try {
                lobby.setPhase(Lobby.Phase.PROGRAMMING);
                httpController.updateGameInfo(lobby.getID(), lobby);
            } catch (Exception er) {
                throw new RuntimeException(er);
            }
        });

        // Add the Player labels
        for (PlayerServer playerName : playerLabels) {
            Label playerLabel = new Label(playerName.getPlayerName());
            this.getChildren().add(playerLabel);
        }

        // Add the Start button to the top right
        HBox startButtonContainer = new HBox();
        startButtonContainer.setAlignment(Pos.TOP_RIGHT);
        startButtonContainer.getChildren().add(startButton);

        // Add the components to the VBox
        this.getChildren().addAll(startButtonContainer);
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == lobby) {

        }
    }
}
