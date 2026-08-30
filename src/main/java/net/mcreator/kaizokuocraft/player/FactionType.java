package net.mcreator.kaizokuocraft.player;

public enum FactionType {
    PIRATE(
        "KORSANLAR",
        "Özgürlük, Bağımsızlık ve Hazine Arayışı."
    ),
    MARINE(
        "DENİZCİLER",
        "Adalet, Düzen ve Hükümet Desteği."
    ),
    REVOLUTIONARY(
        "DEVRİMCİLER",
        "Özgürlük İdealleri ve Taktiksel Güç."
    ),
    BOUNTY_HUNTER(
        "ÖDÜL AVCILARI",
        "Kelle Avcılığı ve Ekstra Kazanç."
    );

    private final String displayName;
    private final String description;

    FactionType(String displayName, String description) {
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