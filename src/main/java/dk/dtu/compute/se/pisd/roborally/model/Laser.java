package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.model.Heading;

public class Laser {

    private boolean start;
    private Heading heading;
    private int damage;

    public boolean start() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }


}
