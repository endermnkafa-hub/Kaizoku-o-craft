package net.mcreator.kaizokuocraft.client;

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
    }
}