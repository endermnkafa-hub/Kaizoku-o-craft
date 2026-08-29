package net.mcreator.kaizokuocraft.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public final class CombatVanillaHud {

    private CombatVanillaHud() {
    }

    @SubscribeEvent
    public static void hideVanillaHud(
            RenderGuiLayerEvent.Pre event
    ) {

        if (
                !CombatState.isActive()
        ) {
            return;
        }

        /*
         * Vanilla hotbarı gizle.
         */
        if (
                event.getName()
                        .equals(
                                VanillaGuiLayers.HOTBAR
                        )
        ) {

            event.setCanceled(
                    true
            );

            return;
        }

        /*
         * Vanilla XP barı gizle.
         */
        if (
                event.getName()
                        .equals(
                                VanillaGuiLayers.EXPERIENCE_BAR
                        )
        ) {

            event.setCanceled(
                    true
            );

            return;
        }

        /*
         * HEALTH → BIRAK
         * FOOD   → BIRAK
         * ARMOR  → BIRAK
         */
    }
}