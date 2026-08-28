package net.mcreator.kaizokuocraft.player;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerData implements INBTSerializable<CompoundTag> {

    private long level = 1L;
    private long experience = 0L;

    private RaceType race = RaceType.HUMAN;

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = Math.max(1L, level);
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = Math.max(0L, experience);
    }

    public void addExperience(long amount) {
        if (amount > 0) {
            experience += amount;
        }
    }

    public RaceType getRace() {
        return race;
    }

    public void setRace(RaceType race) {
        this.race = race == null ? RaceType.HUMAN : race;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putLong("Level", level);
        tag.putLong("Experience", experience);
        tag.putString("Race", race.name());

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        level = Math.max(1L, tag.getLong("Level"));
        experience = Math.max(0L, tag.getLong("Experience"));

        String raceName = tag.getString("Race");

        try {
            race = RaceType.valueOf(raceName);
        } catch (IllegalArgumentException exception) {
            race = RaceType.HUMAN;
        }
    }
}