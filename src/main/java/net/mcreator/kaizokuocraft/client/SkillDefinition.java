package net.mcreator.kaizokuocraft.client;

import net.minecraft.world.item.ItemStack;

public record SkillDefinition(
        String id,
        String name,
        ItemStack icon,
        long cooldownMillis
) {

    public double getCooldownSeconds() {
        return cooldownMillis / 1000.0D;
    }
}