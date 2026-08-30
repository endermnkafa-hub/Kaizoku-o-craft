package net.mcreator.kaizokuocraft.player;

public enum FightingStyle {
    FIST("DÖVÜŞÇÜ", "Ağır Yumruklar, Seri Kombom"),
    SWORD("KILIÇ USTASI", "Hızlı, Keskin Hasar."),
    KICK("KARA BACAK", "Akrobatik Tekmeler, Çeviklik."),
    SNIPER("NİŞANCI", "Uzak Mesafe, Patlayıcı Atışlar.");

    private final String displayName;
    private final String description;

    FightingStyle(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}