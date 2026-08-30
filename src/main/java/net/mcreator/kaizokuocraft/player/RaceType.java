package net.mcreator.kaizokuocraft.player;

public enum RaceType {

    HUMAN(
        "İNSAN",
        "Dengeli Güç, Hızlı Öğrenme.",
        1.00D,
        1.00D,
        1.00D
    ),

    FISH_MAN(
        "BALIKADAM",
        "Hız, Güç, Sualtı Nefes Alma.",
        1.10D,
        1.05D,
        1.05D
    ),

    MINK(
        "MİNK",
        "Elektrik Gücü, Çeviklik ve Hız.",
        1.05D,
        0.95D,
        1.15D
    ),

    CYBORG(
        "CYBORG",
        "Ağır Zırh, Yüksek Savunma.",
        1.05D,
        1.20D,
        0.90D
    ),

    GIANT(
        "DEV",
        "Devasa Can ve Yıkıcı Güç.",
        1.25D,
        1.15D,
        0.80D
    ),

    LUNARIAN(
        "LUNARIAN",
        "Ateş Direnci, Yüksek Dayanıklılık.",
        1.15D,
        1.25D,
        1.00D
    );

    private final String displayName;
    private final String description;
    private final double damageMultiplier;
    private final double defenseMultiplier;
    private final double speedMultiplier;

    RaceType(
        String displayName,
        String description,
        double damageMultiplier,
        double defenseMultiplier,
        double speedMultiplier
    ) {
        this.displayName = displayName;
        this.description = description;
        this.damageMultiplier = damageMultiplier;
        this.defenseMultiplier = defenseMultiplier;
        this.speedMultiplier = speedMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
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