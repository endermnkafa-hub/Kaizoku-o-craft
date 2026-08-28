package net.mcreator.kaizokuocraft.client;

import net.neoforged.bus.api.IEventBus;

public final class ClientInit {

    private ClientInit() {
    }

    public static void init(IEventBus modEventBus) {

        modEventBus.addListener(
                CombatKeyMappings::register
        );

        modEventBus.register(
                CombatClientEvents.class
        );

        modEventBus.register(
                CombatHud.class
        );

        modEventBus.register(
                CombatVanillaHud.class
        );
    }
}