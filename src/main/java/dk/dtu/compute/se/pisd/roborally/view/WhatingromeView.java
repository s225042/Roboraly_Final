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

import java.util.ArrayList;
import java.util.List;

/**
 * WhatingromeView is a placeholder for the actual view content.
 * @author Rebecca Moss, s225042@dtu.dk
 */
public class WhatingromeView extends VBox implements ViewObserver {

    private Lobby lobby;
    private List<PlayerServer> playerLabels;

    private HttpController httpController = new HttpController();

    /**
     *
     * @param lobby
     */
    public WhatingromeView(Lobby lobby) {
        try {
            this.lobby = lobby;
        } catch (Exception er) {
            throw new RuntimeException(er);
        }

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

        playerLabels = new ArrayList<>();

        // Add the Start button to the top right
        HBox startButtonContainer = new HBox();
        startButtonContainer.setAlignment(Pos.BOTTOM_RIGHT);
        startButtonContainer.getChildren().add(startButton);

        //
        Label labelLobbynr = new Label("GameID: " + lobby.getID());
        HBox lobynr = new HBox();
        lobynr.setAlignment(Pos.TOP_CENTER);
        lobynr.getChildren().addAll(labelLobbynr);

        // Add the components to the VBox
        this.getChildren().addAll(lobynr);
        this.getChildren().addAll(startButtonContainer);

        //lobby.attach(this);
        update(lobby);
    }

    /**
     *
     * @param subject
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == lobby) {
            try {
                lobby = httpController.getByGameID(lobby.getID());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Clear existing player labels
            this.getChildren().removeAll(playerLabels);
            playerLabels.clear();

            // Add the updated Player labels
            playerLabels.addAll(lobby.getPlayers());
            for (PlayerServer playerName : playerLabels) {
                Label playerLabel = new Label("Player name: " + playerName.getPlayerName());
                this.getChildren().add(playerLabel);
            }
        }
    }
}
