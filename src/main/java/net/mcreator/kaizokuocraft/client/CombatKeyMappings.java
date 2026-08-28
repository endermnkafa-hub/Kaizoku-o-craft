package net.mcreator.kaizokuocraft.client;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import org.lwjgl.glfw.GLFW;

public final class CombatKeyMappings {

    public static final String CATEGORY =
            "key.categories.kaizoku_o_craft";

    public static final KeyMapping TOGGLE_COMBAT =
            new KeyMapping(
                    "key.kaizoku_o_craft.toggle_combat",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_K,
                    CATEGORY
            );

    private CombatKeyMappings() {
    }

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_COMBAT);
    }
}