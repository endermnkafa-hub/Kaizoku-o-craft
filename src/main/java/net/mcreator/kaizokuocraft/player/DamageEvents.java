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
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        float originalDamage = event.getAmount();

        if (originalDamage <= 0.0F) {
            return;
        }

        double multiplier =
                PowerManager.getDamageMultiplier(player);

        float finalDamage =
                (float) (originalDamage * multiplier);

        event.setAmount(finalDamage);
    }
}