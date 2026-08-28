package net.mcreator.kaizokuocraft.player;

import net.minecraft.server.level.ServerPlayer;

public final class PowerManager {

    private PowerManager() {
    }

    public static double getLevelDamageMultiplier(long level) {
        if (level < 1L) {
            level = 1L;
        }

        return 1.0D + (Math.log1p(level) * 0.25D);
    }

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