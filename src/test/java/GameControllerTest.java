package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    private Board board;
    private GameController gameController;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    public void setUp() {
        board = new Board(10, 10, 8, 8); // Example board size
        gameController = new GameController(board);

        player1 = new Player(board, "Red", "Player 1");
        player2 = new Player(board, "Blue", "Player 2");
        player3 = new Player(board, "Green", "Player 3");

        board.addPlayer(player1);
        board.addPlayer(player2);
        board.addPlayer(player3);

        // Position players on the board
        player1.setSpace(board.getSpace(1, 1));
        player2.setSpace(board.getSpace(2, 2));
        player3.setSpace(board.getSpace(8, 8));
    }

    @Test
    public void testVirusCardSpreadWithinRange() {
        CommandCard virusCard = new CommandCard(Command.VIRUS);
        player1.takeDamage(virusCard);

        // Player1 plays the virus card
        player1.playVirusCard(virusCard);

        // Check that Player2 received a virus card
        List<CommandCard> player2DamageCards = player2.getDiscardPile();
        boolean player2HasVirusCard = player2DamageCards.stream()
                .anyMatch(card -> card.command == Command.VIRUS);
        assertTrue(player2HasVirusCard, "Player 2 should have received a virus card");

        // Check that Player3 did not receive a virus card (out of range)
        List<CommandCard> player3DamageCards = player3.getDiscardPile();
        boolean player3HasVirusCard = player3DamageCards.stream()
                .anyMatch(card -> card.command == Command.VIRUS);
        assertFalse(player3HasVirusCard, "Player 3 should not have received a virus card");
    }

    @Test
    public void testVirusCardNoSpreadOutOfRange() {
        player1.setSpace(board.getSpace(1, 1));
        player2.setSpace(board.getSpace(7, 7)); // Outside 6 spaces of Player1
        player3.setSpace(board.getSpace(8, 8)); // Outside 6 spaces of Player1

        CommandCard virusCard = new CommandCard(Command.VIRUS);
        player1.takeDamage(virusCard);

        // Player1 plays the virus card
        player1.playVirusCard(virusCard);

        // Check that Player2 did not receive a virus card
        List<CommandCard> player2DamageCards = player2.getDiscardPile();
        boolean player2HasVirusCard = player2DamageCards.stream()
                .anyMatch(card -> card.command == Command.VIRUS);
        assertFalse(player2HasVirusCard, "Player 2 should not have received a virus card");

        // Check that Player3 did not receive a virus card
        List<CommandCard> player3DamageCards = player3.getDiscardPile();
        boolean player3HasVirusCard = player3DamageCards.stream()
                .anyMatch(card -> card.command == Command.VIRUS);
        assertFalse(player3HasVirusCard, "Player 3 should not have received a virus card");
    }

    @Test
    public void testEdgeCaseVirusSpread() {
        player1.setSpace(board.getSpace(1, 1));
        player2.setSpace(board.getSpace(5, 5)); // Within 6 spaces of Player1 diagonally
        player3.setSpace(board.getSpace(8, 8)); // Outside 6 spaces of Player1

        CommandCard virusCard = new CommandCard(Command.VIRUS);
        player1.takeDamage(virusCard);

        // Player1 plays the virus card
        gameController.executeCommand(player1, Command.VIRUS);

        // Check that Player2 received a virus card
        List<CommandCard> player2DamageCards = player2.getDiscardPile();
        boolean player2HasVirusCard = player2DamageCards.stream()
                .anyMatch(card -> card.command == Command.VIRUS);
        assertTrue(player2HasVirusCard, "Player 2 should have received a virus card");

        // Check that Player3 did not receive a virus card (out of range)
        List<CommandCard> player3DamageCards = player3.getDiscardPile();
        boolean player3HasVirusCard = player3DamageCards.stream()
                .anyMatch(card -> card.command == Command.VIRUS);
        assertFalse(player3HasVirusCard, "Player 3 should not have received a virus card");
    }


    @Test
    public void testVirusCardSpreadAfterMovement() {
        // Initial setup: Player1 at (1, 1), Player2 at (2, 2), Player3 at (8, 8)
        player1.setSpace(board.getSpace(1, 1));
        player2.setSpace(board.getSpace(2, 2)); // Within range initially
        player3.setSpace(board.getSpace(8, 8)); // Outside range

        CommandCard virusCard = new CommandCard(Command.VIRUS);
        player1.takeDamage(virusCard);

        // Player1 plays the virus card, Player2 should receive it
        player1.playVirusCard(virusCard);

        // Check that Player2 received a virus card initially
        List<CommandCard> player2InitialDamageCards = player2.getDiscardPile();
        boolean player2HasInitialVirusCard = player2InitialDamageCards.stream()
                .anyMatch(card -> card.command == Command.VIRUS);
        assertTrue(player2HasInitialVirusCard, "Player 2 should have received a virus card initially");

        // Move Player2 out of range
        player2.setSpace(board.getSpace(9, 9));

        // Clear discard pile for accurate checking after move
        player2.getDiscardPile().clear();

        // Player1 plays the virus card again to check the spread
        player1.playVirusCard(virusCard);

        // Check that Player2 did not receive a virus card after moving out of range
        List<CommandCard> player2PostMoveDamageCards = player2.getDiscardPile();
        boolean player2HasPostMoveVirusCard = player2PostMoveDamageCards.stream()
                .anyMatch(card -> card.command == Command.VIRUS);
        assertFalse(player2HasPostMoveVirusCard, "Player 2 should not have received a virus card after moving out of range");

        // Check that Player3 did not receive a virus card
        List<CommandCard> player3DamageCards = player3.getDiscardPile();
        boolean player3HasVirusCard = player3DamageCards.stream()
                .anyMatch(card -> card.command == Command.VIRUS);
        assertFalse(player3HasVirusCard, "Player 3 should not have received a virus card");
    }



}
