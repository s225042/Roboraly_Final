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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerServer;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import dk.dtu.compute.se.pisd.roborally.model.WaitingRoom;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard.loadBoard;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private List<String> Game_Bord = Arrays.asList("defaultboard", "STARTER COURSE: DIZZY HIGHWAY", "RISKY CROSSING", "HIGH OCTANE", "SPRINT CRAMP", "CORRIDOR BLITZ", "FRACTIONATION", "BURNOUT", "LOST BEARINGS", "PASSING LANE", "TWISTER", "DODGE THIS", "CHOP SHOP CHALLENGE", "UNDERTOW", "HEAVY MERGE AREA", "DEATH TRAP", "PILGRIMAGE", "GEAR STRIPPER", "EXTRA CRISPY", "BURN RUN");

    private  List<String> Saved_Bord = new ArrayList<>();

    final private RoboRally roboRally;

    final private HttpController httpController = new HttpController();

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void newGame(){
        ChoiceDialog<String> boards = new ChoiceDialog<>(Game_Bord.get(0),Game_Bord);
        boards.setTitle("Table");
        boards.setHeaderText("select game table");
        Optional<String> boardname = boards.showAndWait();
        String boardsname = boardname.get();

       ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }


            // XXX the board should eventually be created programmatically or loaded from a file
            //     here we just create an empty board with the required number of players.
            Board board = loadBoard(boardsname);
            gameController = new GameController(board, httpController);

            //setGameID
            TextInputDialog gameID = new TextInputDialog();
            gameID.setHeaderText("Enter GameID");
            gameID.setContentText("GameID:");

            Optional<String> gameIDs = gameID.showAndWait();
            if(gameIDs.isPresent()) {
                try {
                    board.setGameId(Integer.parseInt(gameIDs.get()));
                    httpController.addGame(new Lobby(boardsname, 0, board.getGameId()));
                } catch (Exception e1) {
                    System.out.println(e1);
                }
            }


            //make the first player
            TextInputDialog dialog1 = new TextInputDialog();
            dialog1.setHeaderText("Enter PlayerID");
            dialog1.setContentText("PlayerID:");

            Optional<String> playerID = dialog1.showAndWait();
            if(playerID.isPresent()){
                try {
                    Lobby lobby = httpController.getByGameID(board.getGameId());
                    httpController.addPlayer(new PlayerServer(playerID.get(), null, null, null, null, null, lobby));
                }
                catch (Exception e1){
                    System.out.println(e1);
                }
            }

            WaitingRoom waitingRoom = new WaitingRoom(gameController.board.getGameId());
            WaitingController waitingController = new WaitingController(waitingRoom, httpController);

            while (!waitingController.starttingGame()) {
                roboRally.createVatingRomeView(waitingController);
            }

            this.startGame();

        }
    }

    public void startGame(){
        //get the plaayers and plays them on the bord
        /*
        int no = result.get();
        for (int i = 0; i < no; i++) {
            //player skal lave på en lidt anden måde
            Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
            board.addPlayer(player);
            player.setSpace(board.getSpace(i % board.width, i));
        }
*/
        // XXX: V2
        // board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);
    }

    public void joinGame(){
        String bordName;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter GameID");
        dialog.setContentText("GameID:");

        while (true) {
            try {
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    Integer iResult = Integer.valueOf(result.get());
                    bordName = httpController.getByGameID(iResult).getBoard();

                    break;
                }
            } catch (Exception e1) {

            }
        }
        //Shold macke the gamecontroler from the https nolegs
        Board board = loadBoard(bordName);
        gameController = new GameController(board, httpController);

        //make the first player
        TextInputDialog dialog1 = new TextInputDialog();
        dialog1.setHeaderText("Enter PlayerID");
        dialog1.setContentText("PlayerID:");

        Optional<String> playerID = dialog1.showAndWait();
        if(playerID.isPresent()){
            try {
                Lobby lobby = httpController.getByGameID(board.getGameId());
                httpController.addPlayer(new PlayerServer(playerID.get(), null, null, null, null, null, lobby));
            }
            catch (Exception e1){
                System.out.println(e1);
            }
        }

        WaitingRoom waitingRoom = new WaitingRoom(gameController.board.getGameId());
        WaitingController waitingController = new WaitingController(waitingRoom, httpController);

        /*
        while (!waitingController.starttingGame()) {
            roboRally.createVatingRomeView(waitingController);
        }
        //shold getsomting from http that wil start the game*/

    }

    /**
     * @author Rebecca Moss, s225042@gmail.com
     *
     */
    public void saveGame() {
        if (gameController == null){
            return; //no game to save
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save game");
        dialog.setHeaderText("Enter name for the game");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            LoadBoard.saveBoard(gameController.board, result.get());
        }
    }
    /**
     * @author Rebecca Moss, s225042@gmail.com
     *
     */
    public void loadGame() {
        // XXX needs to be implemented eventually
        // for now, we just create a new game
        String dirkteryPath = "target/classes/boards/games";
        Path dirktery = Paths.get(dirkteryPath);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirktery)) {

            //with forEach loop get all the path of files present in directory
            for (Path file : stream){
                String fileString = file.getFileName().toString();
                String fileName = fileString.substring(0, fileString.lastIndexOf("."));
                Saved_Bord.add(fileName);
            }
            ChoiceDialog<String> saveGames = new ChoiceDialog<>(Saved_Bord.get(0),Saved_Bord);
            saveGames.setTitle("");
            saveGames.setHeaderText("select your saved game");
            Optional<String> boardname = saveGames.showAndWait();
            String boardsname = boardname.get();

            Board board = loadBoard("games/" + boardsname);
            gameController = new GameController(board, httpController);

            roboRally.createBoardView(gameController);

        }
        catch (IOException e1){
            System.out.println(e1.getMessage());
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
