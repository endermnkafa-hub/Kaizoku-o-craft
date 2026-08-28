package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.Minecraft;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class CombatClientEvents {

    private CombatClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        while (CombatKeyMappings.TOGGLE_COMBAT.consumeClick()) {

            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.player == null) {
                return;
            }

            CombatState.toggle();
        }
    }
}