package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.player.RaceType;

public final class ClientPlayerData {

    private static long level = 1L;
    private static long experience = 0L;
    private static RaceType race = RaceType.HUMAN;

    private ClientPlayerData() {
    }

    public static long getLevel() {
        return level;
    }

    public static long getExperience() {
        return experience;
    }

    public static RaceType getRace() {
        return race;
    }

    public static void set(
            long newLevel,
            long newExperience,
            RaceType newRace
    ) {
        level = Math.max(1L, newLevel);
        experience = Math.max(0L, newExperience);
        race = newRace == null ? RaceType.HUMAN : newRace;
    }

    public static void reset() {
        level = 1L;
        experience = 0L;
        race = RaceType.HUMAN;
    }
}