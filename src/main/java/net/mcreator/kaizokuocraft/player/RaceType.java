package net.mcreator.kaizokuocraft.player;

public enum RaceType {

    HUMAN(
            "Human",
            1.00D,
            1.00D,
            1.00D
    ),

    FISH_MAN(
            "Fish-Man",
            1.05D,
            1.00D,
            1.00D
    ),

    MINK(
            "Mink",
            1.00D,
            0.95D,
            1.10D
    ),

    GIANT(
            "Giant",
            1.10D,
            1.10D,
            0.85D
    );

    private final String displayName;
    private final double damageMultiplier;
    private final double defenseMultiplier;
    private final double speedMultiplier;

    RaceType(
            String displayName,
            double damageMultiplier,
            double defenseMultiplier,
            double speedMultiplier
    ) {
        this.displayName = displayName;
        this.damageMultiplier = damageMultiplier;
        this.defenseMultiplier = defenseMultiplier;
        this.speedMultiplier = speedMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public double getDefenseMultiplier() {
        return defenseMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}