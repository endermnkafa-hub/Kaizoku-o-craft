package net.mcreator.kaizokuocraft.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public final class ClientEventHandler {

    private ClientEventHandler() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(
            RegisterKeyMappingsEvent event
    ) {
        event.register(
                CombatKeyMappings.TOGGLE_COMBAT
        );
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
    }
}