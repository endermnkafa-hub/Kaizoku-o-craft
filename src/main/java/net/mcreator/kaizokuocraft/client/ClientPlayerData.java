package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.player.RaceType;

public final class ClientPlayerData {

    private static long level = 1L;
    private static long experience = 0L;
    private static RaceType race = RaceType.HUMAN;
    private static String combatStyle = "FIST";
    private static int statPoints = 0;
    private static int strength = 0;
    private static int defense = 0;
    private static double swordMastery = 0.0D;
    private static double fightingMastery = 0.0D;
    private static double sniperMastery = 0.0D;
    private static double kickMastery = 0.0D;
    private static net.minecraft.nbt.CompoundTag hakiData = new net.minecraft.nbt.CompoundTag();
    private static net.minecraft.nbt.CompoundTag fruitData = new net.minecraft.nbt.CompoundTag();

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

    public static String getCombatStyle() {
        return combatStyle;
    }

    public static int getStatPoints() {
        return statPoints;
    }

    public static int getStrength() {
        return strength;
    }

    public static int getDefense() {
        return defense;
    }

    public static double getSwordMastery() {
        return swordMastery;
    }

    public static double getFightingMastery() {
        return fightingMastery;
    }

    public static double getSniperMastery() {
        return sniperMastery;
    }

    public static double getKickMastery() {
        return kickMastery;
    }

    public static net.minecraft.nbt.CompoundTag getHakiData() {
        return hakiData;
    }

    public static net.minecraft.nbt.CompoundTag getFruitData() {
        return fruitData;
    }

    public static void set(
            long newLevel,
            long newExperience,
            RaceType newRace,
            String newCombatStyle,
            int newStatPoints,
            int newStrength,
            int newDefense,
            double newSwordMastery,
            double newFightingMastery,
            double newSniperMastery,
            double newKickMastery,
            net.minecraft.nbt.CompoundTag newHakiData,
            net.minecraft.nbt.CompoundTag newFruitData
    ) {
        level = Math.max(1L, newLevel);
        experience = Math.max(0L, newExperience);
        race = newRace == null ? RaceType.HUMAN : newRace;
        if (newCombatStyle != null && !newCombatStyle.equalsIgnoreCase(combatStyle)) {
            SkillLoadout.clearAll();
            if (newCombatStyle.equalsIgnoreCase("FIST")) {
                SkillLoadout.setSkill(0, "punch");
            } else if (newCombatStyle.equalsIgnoreCase("SWORD")) {
                SkillLoadout.setSkill(0, "sword_slash");
            } else if (newCombatStyle.equalsIgnoreCase("KICK")) {
                SkillLoadout.setSkill(0, "collier_kick");
            } else if (newCombatStyle.equalsIgnoreCase("SNIPER")) {
                SkillLoadout.setSkill(0, "firebird_star");
            }
        }
        combatStyle = newCombatStyle == null ? "FIST" : newCombatStyle;
        statPoints = Math.max(0, newStatPoints);
        strength = Math.max(0, newStrength);
        defense = Math.max(0, newDefense);
        swordMastery = Math.max(0.0D, newSwordMastery);
        fightingMastery = Math.max(0.0D, newFightingMastery);
        sniperMastery = Math.max(0.0D, newSniperMastery);
        kickMastery = Math.max(0.0D, newKickMastery);
        hakiData = newHakiData == null ? new net.minecraft.nbt.CompoundTag() : newHakiData;
        fruitData = newFruitData == null ? new net.minecraft.nbt.CompoundTag() : newFruitData;
    }

    public static void reset() {
        level = 1L;
        experience = 0L;
        race = RaceType.HUMAN;
        combatStyle = "FIST";
        statPoints = 0;
        strength = 0;
        defense = 0;
        swordMastery = 0.0D;
        fightingMastery = 0.0D;
        sniperMastery = 0.0D;
        kickMastery = 0.0D;
        hakiData = new net.minecraft.nbt.CompoundTag();
        fruitData = new net.minecraft.nbt.CompoundTag();
    }
}