package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;


/**
 * @Author s235074
 * This class represents the Antenna object in the game Robo Rally.
 * The Antenna object has a board, x and y position
 * on the board.
 *
 */
public class Antenna {
    public final Board board;
    public final int x;
    public final int y;


    public Antenna(Board board, int x, int y){
        this.board = board;
        this.x =x;
        this.y = y;

}



    /**
     * Calculates the Manhattan distance between the antenna and the player's position.
     *
     * @param player The player whose distance is to be calculated.
     * @return The Manhattan distance between the antenna and the player.
     */
    public int calculateDistance(Player player) {
        int playerX = player.getSpace().getX();
        int playerY = player.getSpace().getY();
        return Math.abs(this.x - playerX) + Math.abs(this.y - playerY);
    }



}


