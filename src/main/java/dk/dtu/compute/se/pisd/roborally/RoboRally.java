package dk.dtu.compute.se.pisd.roborally;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.RoboRallyMenuBar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RoboRally extends Application {

    private static final int MIN_APP_WIDTH = 600;

    private Stage stage;
    private BorderPane boardRoot;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setResizable(true); // Allow stage to be resized by user

        AppController appController = new AppController(this);

        RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        boardRoot = new BorderPane();
        VBox vbox = new VBox(menuBar, boardRoot);
        Scene primaryScene = new Scene(vbox);

        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(e -> {
            e.consume();
            appController.exit();
        });
        stage.show();
    }

    public void createBoardView(GameController gameController) {
        boardRoot.getChildren().clear();

        if (gameController != null) {
            BoardView boardView = new BoardView(gameController);
            boardRoot.setCenter(boardView);
        }

        stage.sizeToScene(); // Adjust stage size to fit updated content
    }

    public static void main(String[] args) {
        launch(args);
    }

}
