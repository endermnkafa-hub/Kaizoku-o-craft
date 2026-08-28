package net.mcreator.kaizokuocraft.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import net.minecraft.client.Minecraft;

@EventBusSubscriber(
        modid = "kaizoku_o_craft",
        bus = EventBusSubscriber.Bus.MOD,
        value = net.neoforged.api.distmarker.Dist.CLIENT
)

@SubscribeEvent
public static void onClientTick(ClientTickEvent.Post event) {
    while (CombatKeyMappings.TOGGLE_COMBAT.consumeClick()) {
        CombatState.toggle();
    }
}

public final class ClientEventHandler {

    private ClientEventHandler() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(CombatKeyMappings.TOGGLE_COMBAT);
    }
}