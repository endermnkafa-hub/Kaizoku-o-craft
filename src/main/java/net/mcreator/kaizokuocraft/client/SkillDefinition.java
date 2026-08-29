package net.mcreator.kaizokuocraft.client;

import net.minecraft.world.item.ItemStack;

public record SkillDefinition(
        String id,
        String name,
        ItemStack icon
) {
}