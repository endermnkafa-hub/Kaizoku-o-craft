package net.mcreator.kaizokuocraft.player;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerData implements INBTSerializable<CompoundTag> {

    /*
     * ==============================
     * LEVEL / XP
     * ==============================
     */

    private long level =
            1L;

    private long experience =
            0L;

    /*
     * ==============================
     * RACE
     * ==============================
     */

    private RaceType race =
            RaceType.HUMAN;

    /*
     * ==============================
     * STAMINA
     * ==============================
     *
     * Level 1 = 100
     *
     * Her level = +30
     *
     * Level 10 = 370
     */

    private double stamina =
            100.0D;

    private double maxStamina =
            100.0D;

    /*
     * ==============================
     * EXPANDED PLAYER STATS & DATA
     * ==============================
     */

    private String combatStyle = "FIST";

    private int statPoints = 0;

    private int strength = 0;

    private int defense = 0;

    private double swordMastery = 0.0D;

    private double fightingMastery = 0.0D;

    private double sniperMastery = 0.0D;

    private double kickMastery = 0.0D;

    private CompoundTag hakiData = new CompoundTag();

    private CompoundTag fruitData = new CompoundTag();

    /*
     * ==============================
     * LEVEL
     * ==============================
     */

    public long getLevel() {
        return level;
    }

    public void setLevel(
            long level
    ) {

        this.level =
                Math.max(
                        1L,
                        level
                );

        /*
         * Level değiştiği anda
         * max stamina da değişsin.
         *
         * 1  -> 100
         * 2  -> 130
         * 10 -> 370
         * 20 -> 670
         */
        double newMax =
                100.0D
                        + (
                        Math.max(
                                0L,
                                this.level - 1L
                        )
                                * 30.0D
                );

        /*
         * Yeni max değeri uygula.
         */
        this.maxStamina =
                Math.max(
                        1.0D,
                        newMax
                );

        /*
         * Mevcut stamina yeni max'ın
         * üzerinde kalamaz.
         *
         * ÖNEMLİ:
         * Level atlayınca mevcut stamina
         * otomatik full yapılmaz.
         */
        this.stamina =
                Math.min(
                        this.stamina,
                        this.maxStamina
                );
    }

    /*
     * ==============================
     * EXPERIENCE
     * ==============================
     */

    public long getExperience() {
        return experience;
    }

    public void setExperience(
            long experience
    ) {

        this.experience =
                Math.max(
                        0L,
                        experience
                );
    }

    public void addExperience(
            long amount
    ) {

        if (
                amount > 0
        ) {

            experience +=
                    amount;
        }
    }

    /*
     * ==============================
     * RACE
     * ==============================
     */

    public RaceType getRace() {
        return race;
    }

    public void setRace(
            RaceType race
    ) {

        this.race =
                race == null
                        ? RaceType.HUMAN
                        : race;
    }

    /*
     * ==============================
     * STAMINA
     * ==============================
     */

    public double getStamina() {
        return stamina;
    }

    public void setStamina(
            double stamina
    ) {

        this.stamina =
                Math.max(
                        0.0D,
                        Math.min(
                                maxStamina,
                                stamina
                        )
                );
    }

    public void addStamina(
            double amount
    ) {

        if (
                amount > 0.0D
        ) {

            setStamina(
                    stamina + amount
            );
        }
    }

    public double getMaxStamina() {
        return maxStamina;
    }

    public void setMaxStamina(
            double maxStamina
    ) {

        this.maxStamina =
                Math.max(
                        1.0D,
                        maxStamina
                );

        this.stamina =
                Math.min(
                        this.stamina,
                        this.maxStamina
                );
    }

    /*
     * ==============================
     * GETTERS & SETTERS FOR EXPANDED FIELDS
     * ==============================
     */

    public String getCombatStyle() {
        return combatStyle;
    }

    public void setCombatStyle(String combatStyle) {
        this.combatStyle = combatStyle == null ? "FIST" : combatStyle;
    }

    public int getStatPoints() {
        return statPoints;
    }

    public void setStatPoints(int statPoints) {
        this.statPoints = Math.max(0, statPoints);
    }

    public void addStatPoints(int amount) {
        if (amount > 0) {
            this.statPoints += amount;
        }
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = Math.max(0, strength);
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = Math.max(0, defense);
    }

    public double getSwordMastery() {
        return swordMastery;
    }

    public void setSwordMastery(double swordMastery) {
        this.swordMastery = Math.max(0.0D, swordMastery);
    }

    public double getFightingMastery() {
        return fightingMastery;
    }

    public void setFightingMastery(double fightingMastery) {
        this.fightingMastery = Math.max(0.0D, fightingMastery);
    }

    public double getSniperMastery() {
        return sniperMastery;
    }

    public void setSniperMastery(double sniperMastery) {
        this.sniperMastery = Math.max(0.0D, sniperMastery);
    }

    public double getKickMastery() {
        return kickMastery;
    }

    public void setKickMastery(double kickMastery) {
        this.kickMastery = Math.max(0.0D, kickMastery);
    }

    public CompoundTag getHakiData() {
        return hakiData;
    }

    public void setHakiData(CompoundTag hakiData) {
        this.hakiData = hakiData == null ? new CompoundTag() : hakiData;
    }

    public CompoundTag getFruitData() {
        return fruitData;
    }

    public void setFruitData(CompoundTag fruitData) {
        this.fruitData = fruitData == null ? new CompoundTag() : fruitData;
    }

    /*
     * ==============================
     * SAVE
     * ==============================
     */

    @Override
    public CompoundTag serializeNBT(
            HolderLookup.Provider provider
    ) {

        CompoundTag tag =
                new CompoundTag();

        tag.putLong(
                "Level",
                level
        );

        tag.putLong(
                "Experience",
                experience
        );

        tag.putString(
                "Race",
                race.name()
        );

        tag.putDouble(
                "Stamina",
                stamina
        );

        tag.putDouble(
                "MaxStamina",
                maxStamina
        );

        tag.putString(
                "CombatStyle",
                combatStyle
        );

        tag.putInt(
                "StatPoints",
                statPoints
        );

        tag.putInt(
                "Strength",
                strength
        );

        tag.putInt(
                "Defense",
                defense
        );

        tag.putDouble(
                "SwordMastery",
                swordMastery
        );

        tag.putDouble(
                "FightingMastery",
                fightingMastery
        );

        tag.putDouble(
                "SniperMastery",
                sniperMastery
        );

        tag.putDouble(
                "KickMastery",
                kickMastery
        );

        tag.put(
                "HakiData",
                hakiData
        );

        tag.put(
                "FruitData",
                fruitData
        );

        return tag;
    }

    /*
     * ==============================
     * LOAD
     * ==============================
     */

    @Override
    public void deserializeNBT(
            HolderLookup.Provider provider,
            CompoundTag tag
    ) {

        level =
                Math.max(
                        1L,
                        tag.getLong(
                                "Level"
                        )
                );

        experience =
                Math.max(
                        0L,
                        tag.getLong(
                                "Experience"
                        )
                );

        String raceName =
                tag.getString(
                        "Race"
                );

        try {

            race =
                    RaceType.valueOf(
                            raceName
                    );

        } catch (
                IllegalArgumentException exception
        ) {

            race =
                    RaceType.HUMAN;
        }

        /*
         * ==========================
         * MAX STAMINA
         * ==========================
         *
         * Eski kayıtların maxStamina'sı
         * 100 kalmış olsa bile level'a
         * göre yeniden hesaplanır.
         */
        maxStamina =
                100.0D
                        + (
                        Math.max(
                                0L,
                                level - 1L
                        )
                                * 30.0D
                );

        /*
         * Kayıtlı stamina.
         */
        stamina =
                tag.contains(
                        "Stamina"
                )
                        ? Math.max(
                        0.0D,
                        Math.min(
                                maxStamina,
                                tag.getDouble(
                                        "Stamina"
                                )
                        )
                )
                        : maxStamina;

        combatStyle =
                tag.contains(
                        "CombatStyle"
                )
                        ? tag.getString(
                        "CombatStyle"
                )
                        : "FIST";

        statPoints =
                tag.getInt(
                        "StatPoints"
                );

        strength =
                tag.getInt(
                        "Strength"
                );

        defense =
                tag.getInt(
                        "Defense"
                );

        swordMastery =
                tag.getDouble(
                        "SwordMastery"
                );

        fightingMastery =
                tag.getDouble(
                        "FightingMastery"
                );

        sniperMastery =
                tag.getDouble(
                        "SniperMastery"
                );

        kickMastery =
                tag.getDouble(
                        "KickMastery"
                );

        hakiData =
                tag.contains(
                        "HakiData"
                )
                        ? tag.getCompound(
                        "HakiData"
                )
                        : new CompoundTag();

        fruitData =
                tag.contains(
                        "FruitData"
                )
                        ? tag.getCompound(
                        "FruitData"
                )
                        : new CompoundTag();
    }
}