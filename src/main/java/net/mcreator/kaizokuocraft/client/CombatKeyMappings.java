package net.mcreator.kaizokuocraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class CombatKeyMappings {

    public static final KeyMapping TOGGLE_COMBAT =
            new KeyMapping(
                    "key.kaizoku_o_craft.toggle_combat",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_K,
                    "key.categories.kaizoku_o_craft"
            );

    private CombatKeyMappings() {
    }
}