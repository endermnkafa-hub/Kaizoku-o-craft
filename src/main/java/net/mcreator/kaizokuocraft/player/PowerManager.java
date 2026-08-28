package net.mcreator.kaizokuocraft.player;

import net.minecraft.server.level.ServerPlayer;

public final class PowerManager {

    private PowerManager() {
    }

    /**
     * Level'ın verdiği genel hasar çarpanı.
     *
     * Örnek:
     * Level 1      -> x1.00
     * Level 10     -> x3.16
     * Level 100    -> x10.00
     * Level 300    -> x17.32
     * Level 1000   -> x31.62
     */
    public static double getLevelDamageMultiplier(long level) {
        if (level < 1L) {
            level = 1L;
        }

        return Math.sqrt(level);
    }

    /**
     * Level + Race birlikte gerçek genel hasar çarpanını verir.
     */
    public static double getDamageMultiplier(ServerPlayer player) {
        PlayerData data = PlayerDataManager.get(player);

        double levelMultiplier =
                getLevelDamageMultiplier(data.getLevel());

        double raceMultiplier =
                data.getRace().getDamageMultiplier();

        return levelMultiplier * raceMultiplier;
    }

    public static double getDefenseMultiplier(ServerPlayer player) {
        return PlayerDataManager
                .get(player)
                .getRace()
                .getDefenseMultiplier();
    }

    public static double getSpeedMultiplier(ServerPlayer player) {
        return PlayerDataManager
                .get(player)
                .getRace()
                .getSpeedMultiplier();
    }
}