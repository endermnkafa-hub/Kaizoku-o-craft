package net.mcreator.kaizokuocraft.client;

public final class ClientPlayerData {

    private static long level = 1L;
    private static long experience = 0L;

    private ClientPlayerData() {
    }

    public static long getLevel() {
        return level;
    }

    public static long getExperience() {
        return experience;
    }

    public static void set(long newLevel, long newExperience) {
        level = Math.max(1L, newLevel);
        experience = Math.max(0L, newExperience);
    }

    public static void reset() {
        level = 1L;
        experience = 0L;
    }
}