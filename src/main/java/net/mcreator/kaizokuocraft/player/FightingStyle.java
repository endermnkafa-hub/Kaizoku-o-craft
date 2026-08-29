package net.mcreator.kaizokuocraft.player;

public enum FightingStyle {
    FIST("Yumruk"),
    SWORD("Kılıç"),
    KICK("Tekme"),
    SNIPER("Keskin Nişancı");

    private final String displayName;

    FightingStyle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
