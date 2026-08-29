package net.mcreator.kaizokuocraft.player;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class DamageEvents {

    private DamageEvents() {
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(
            LivingIncomingDamageEvent event
    ) {

        if (
                !(event.getEntity()
                        instanceof ServerPlayer player)
        ) {
            return;
        }

        float originalDamage =
                event.getAmount();

        if (
                originalDamage <= 0.0F
        ) {
            return;
        }

        double durability =
                PowerManager.getDurabilityMultiplier(
                        player
                );

        if (
                durability <= 0.0D
        ) {
            return;
        }

        /*
         * Dayanıklılık arttıkça alınan hasar azalır.
         */
        float finalDamage =
                (float) (
                        originalDamage
                                / durability
                );

        event.setAmount(
                Math.max(
                        0.0F,
                        finalDamage
                )
        );
    }
}