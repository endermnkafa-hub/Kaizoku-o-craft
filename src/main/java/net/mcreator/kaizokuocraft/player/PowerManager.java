package net.mcreator.kaizokuocraft.player;

import net.minecraft.server.level.ServerPlayer;

public final class PowerManager {

    private PowerManager() {
    }

    public static double getLevelDamageMultiplier(
            long level
    ) {

        if (level < 1L) {
            level = 1L;
        }

        return Math.sqrt(
                level
        );
    }

    public static double getDamageMultiplier(
            ServerPlayer player
    ) {

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        double levelMultiplier =
                getLevelDamageMultiplier(
                        data.getLevel()
                );

        double raceMultiplier =
                data.getRace()
                        .getDamageMultiplier();

        return levelMultiplier
                * raceMultiplier;
    }

    /*
     * Oyuncunun verdiği hasar için değil,
     * aldığı hasara karşı savunma için.
     */
    public static double getDurabilityMultiplier(
            ServerPlayer player
    ) {

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        double levelDurability =
                getLevelDamageMultiplier(
                        data.getLevel()
                );

        double raceDurability =
                data.getRace()
                        .getDefenseMultiplier();

        return levelDurability
                * raceDurability;
    }

    /*
     * Eski isimle uyumluluk.
     */
    public static double getDefenseMultiplier(
            ServerPlayer player
    ) {

        return getDurabilityMultiplier(
                player
        );
    }

    public static double getSpeedMultiplier(
            ServerPlayer player
    ) {

        return PlayerDataManager
                .get(player)
                .getRace()
                .getSpeedMultiplier();
    }
}