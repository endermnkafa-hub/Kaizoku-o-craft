package net.mcreator.kaizokuocraft.player;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerData implements INBTSerializable<CompoundTag> {

    private int level = 1;
    private long experience = 0L;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
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

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        tag.putInt("Level", level);
        tag.putLong("Experience", experience);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        level = Math.max(1, tag.getInt("Level"));
        experience = Math.max(0L, tag.getLong("Experience"));
    }
}