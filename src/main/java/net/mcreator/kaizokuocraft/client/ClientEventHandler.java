package net.mcreator.kaizokuocraft.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(
        modid = "kaizoku_o_craft",
        bus = EventBusSubscriber.Bus.MOD,
        value = net.neoforged.api.distmarker.Dist.CLIENT
)
public final class ClientEventHandler {

    private ClientEventHandler() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(CombatKeyMappings.TOGGLE_COMBAT);
    }
}