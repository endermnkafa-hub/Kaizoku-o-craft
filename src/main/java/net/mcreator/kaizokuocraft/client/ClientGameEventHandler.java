package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.Minecraft;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class ClientGameEventHandler {

    private ClientGameEventHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(
            ClientTickEvent.Post event
    ) {

        while (
                CombatKeyMappings.TOGGLE_COMBAT.consumeClick()
        ) {
            CombatState.toggle();
        }

        while (
                MenuKeyMappings.OPEN_MENU.consumeClick()
        ) {
            Minecraft minecraft =
                    Minecraft.getInstance();

            if (minecraft.player == null) {
                return;
            }

            if (minecraft.screen == null) {
                minecraft.setScreen(
                        new KaizokuMenuScreen()
                );
            } else if (
                    minecraft.screen
                            instanceof KaizokuMenuScreen
            ) {
                minecraft.setScreen(null);
            }
        }
    }
}