package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.player.FightingStyle;
import net.minecraft.world.item.ItemStack;

public record SkillDefinition(
        String id,
        String name,
        ItemStack icon,
        long cooldownMillis,
        FightingStyle style,
        double requiredMastery,
        double staminaCost
) {

    public double getCooldownSeconds() {
        return cooldownMillis / 1000.0D;
    }
}