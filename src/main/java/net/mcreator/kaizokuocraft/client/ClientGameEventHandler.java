package net.mcreator.kaizokuocraft.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(
        modid = "kaizoku_o_craft",
        bus = EventBusSubscriber.Bus.GAME,
        value = net.neoforged.api.distmarker.Dist.CLIENT
)
public final class ClientGameEventHandler {

    private ClientGameEventHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (CombatKeyMappings.TOGGLE_COMBAT.consumeClick()) {
            CombatState.toggle();
        }
    }
}