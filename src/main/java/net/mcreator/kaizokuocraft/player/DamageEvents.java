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

        PlayerData data = PlayerDataManager.get(player);
        double levelDefense = data.getLevel() * 0.2D;
        double statDefense = data.getDefense() * 1.5D;
        double raceDefenseMult = data.getRace().getDefenseMultiplier();
        double totalDefense = (levelDefense + statDefense) * raceDefenseMult;

        /*
         * Diminishing returns formula: damage reduction = defense / (defense + 50)
         */
        double damageReduction = totalDefense / (totalDefense + 50.0D);
        float finalDamage = (float) (originalDamage * (1.0D - damageReduction));

        event.setAmount(
                Math.max(
                        0.0F,
                        finalDamage
                )
        );
    }
}