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
    }
}