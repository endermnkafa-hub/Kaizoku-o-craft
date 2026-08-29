package net.mcreator.kaizokuocraft.client;

import net.neoforged.bus.api.SubscribeEvent;
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

        event.register(
                MenuKeyMappings.OPEN_MENU
        );
    }
}