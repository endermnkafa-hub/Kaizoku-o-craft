package net.mcreator.kaizokuocraft.client;

public final class PlayerPowerClient {

    private PlayerPowerClient() {
    }

    public static double getDamageMultiplier(
            long level
    ) {
        if (level < 1L) {
            level = 1L;
        }

        return Math.sqrt(level);
    }
}