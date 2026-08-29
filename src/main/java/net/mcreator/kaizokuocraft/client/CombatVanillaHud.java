package net.mcreator.kaizokuocraft.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public final class CombatVanillaHud {

    /*
     * Can ve açlığı Combat Bar'dan
     * uzaklaştırmak için yukarı taşıyoruz.
     */
    private static final float HEALTH_FOOD_OFFSET_Y =
            -26.0F;

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
         * Vanilla hotbar
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
         * Vanilla XP barı
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
         * ========================================
         * CAN
         * ========================================
         */
        if (
                event.getName()
                        .equals(
                                VanillaGuiLayers.PLAYER_HEALTH
                        )
        ) {

            event.getGuiGraphics()
                    .pose()
                    .pushPose();

            event.getGuiGraphics()
                    .pose()
                    .translate(
                            0.0D,
                            HEALTH_FOOD_OFFSET_Y,
                            0.0D
                    );
        }

        /*
         * ========================================
         * AÇLIK
         * ========================================
         */
        if (
                event.getName()
                        .equals(
                                VanillaGuiLayers.FOOD_LEVEL
                        )
        ) {

            event.getGuiGraphics()
                    .pose()
                    .pushPose();

            event.getGuiGraphics()
                    .pose()
                    .translate(
                            0.0D,
                            HEALTH_FOOD_OFFSET_Y,
                            0.0D
                    );
        }

        /*
         * Armor'a dokunmuyoruz.
         */
    }

    @SubscribeEvent
    public static void restoreVanillaHud(
            RenderGuiLayerEvent.Post event
    ) {

        if (
                !CombatState.isActive()
        ) {
            return;
        }

        /*
         * Can transformunu geri al.
         */
        if (
                event.getName()
                        .equals(
                                VanillaGuiLayers.PLAYER_HEALTH
                        )
        ) {

            event.getGuiGraphics()
                    .pose()
                    .popPose();
        }

        /*
         * Açlık transformunu geri al.
         */
        if (
                event.getName()
                        .equals(
                                VanillaGuiLayers.FOOD_LEVEL
                        )
        ) {

            event.getGuiGraphics()
                    .pose()
                    .popPose();
        }
    }
}