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
    private final Stage stage;
    private final HBox window;
    private List<PlayerServer> playerLabels;

    private HttpController httpController = new HttpController();


    public WhatingromeView(Lobby lobby) {
        this.lobby = lobby;

        // list of players
        playerLabels = lobby.getPlayers();

        // Create the Start button
        Button startButton = new Button("Start");
        startButton.setOnAction(e -> {
            try {
                lobby.setPhase(Lobby.Phase.PROGRAMMING);
                httpController.updateGameInfo(lobby.getID(), lobby);

            }
            catch (Exception er){
                throw new RuntimeException(er);
            }
        });

        stage = new Stage();
        window = new HBox();

        //Making the lobbyLayout
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setPadding(new Insets(10));

        window.getChildren().add(this);


        // Add the Player label to the space
        for (PlayerServer playerName: playerLabels){
            Label playerLabel = new Label(playerName.getPlayerName());
            this.getChildren().add(playerLabel);
        }

        // Add an empty HBox for spacing
        HBox spacer = new HBox();
        spacer.setMinWidth(500); // Adjust this width as needed

        // Add the Start button to the top right
        HBox startButtonContainer = new HBox();
        startButtonContainer.setAlignment(Pos.TOP_RIGHT);
        startButtonContainer.getChildren().add(startButton);

        // Add the components to the top container
        this.getChildren().addAll(spacer, startButtonContainer);
    }



    /**
     * @param subject
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == lobby){

        }
    }
}
