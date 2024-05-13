package dk.dtu.compute.se.pisd.roborally.model;


public class DamageCard {
    private final DamageType damageType;

    public DamageCard(DamageType damageType) {
        this.damageType = damageType;
    }

    public DamageType getDamageType() {
        return damageType;
    }
}
