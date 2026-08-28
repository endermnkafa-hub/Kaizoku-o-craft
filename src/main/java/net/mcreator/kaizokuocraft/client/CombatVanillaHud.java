package net.mcreator.kaizokuocraft.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public final class CombatVanillaHud {

    private CombatVanillaHud() {
    }

    @SubscribeEvent
    public static void hideVanillaHotbar(
            RenderGuiLayerEvent.Pre event
    ) {

        if (!CombatState.isActive()) {
            return;
        }

        if (event.getName().equals(VanillaGuiLayers.HOTBAR)) {
            event.setCanceled(true);
        }
    }
}