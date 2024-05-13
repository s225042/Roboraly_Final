package dk.dtu.compute.se.pisd.roborally.model;

public enum DamageType {
    SPAM("Spam"),
    WORM("Worm"),
    TROJAN_HORSE("Trojan Horse"),

    VIRUS("Virus");
    final public String displayName;

    // Constructor for the enum values
    DamageType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the damage type.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}
