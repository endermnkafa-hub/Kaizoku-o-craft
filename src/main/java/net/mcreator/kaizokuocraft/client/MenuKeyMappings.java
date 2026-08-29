package net.mcreator.kaizokuocraft.client;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import org.lwjgl.glfw.GLFW;

public final class MenuKeyMappings {

    public static final KeyMapping OPEN_MENU =
            new KeyMapping(
                    "key.kaizoku_o_craft.open_menu",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_M,
                    "key.categories.kaizoku_o_craft"
            );

    private MenuKeyMappings() {
    }
}