package net.mcreator.kaizokuocraft.player;

import net.minecraft.server.level.ServerPlayer;

public final class PowerManager {

    private PowerManager() {
    }

    /**
     * Level'a göre genel hasar çarpanını hesaplar.
     *
     * Formül:
     * 1 + ln(level + 1) * 0.25
     *
     * Bu değer ileride fighting style, Haki, Devil Fruit,
     * race vb. bonuslarla birlikte kullanılacaktır.
     */
    public static double getDamageMultiplier(long level) {
        if (level < 1L) {
            level = 1L;
        }

        return 1.0D + (Math.log1p(level) * 0.25D);
    }

    public static double getDamageMultiplier(ServerPlayer player) {
        return getDamageMultiplier(
                PlayerDataManager.getLevel(player)
        );
    }
}