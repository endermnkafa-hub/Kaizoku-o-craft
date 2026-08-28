package net.mcreator.kaizokuocraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

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

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_COMBAT);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (TOGGLE_COMBAT.consumeClick()) {
            CombatState.toggle();
        }
    }
}