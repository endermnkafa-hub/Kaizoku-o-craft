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
         * Vanilla hotbar yok.
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
         * Vanilla XP bar yok.
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
        }

        /*
         * HEALTH ve FOOD'A DOKUNMUYORUZ.
         */
    }
}