package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;


/**
 * @Author s235074
 * This class represents the Antenna object in the game Robo Rally.
 * The Antenna object is a subject and has a board, x and y position
 * on the board.
 *
 */
public class Antenna extends Subject {
    public final Board board;
    public final int x;
    public final int y;


    public Antenna(Board board, int x, int y){
        this.board = board;
        this.x =x;
        this.y = y;



}



}


