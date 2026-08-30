package net.mcreator.kaizokuocraft.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class ExperienceEvents {

    private ExperienceEvents() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        // Ölen şey oyuncuysa XP verme.
        if (event.getEntity() instanceof Player) {
            return;
        }

        // Ölüm sebebinin sahibi oyuncu mu?
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Karakter oluşturulmadıysa XP verme (Vanilla mod)
        if (!PlayerDataManager.get(player).isCharacterCreated()) {
            return;
        }

        // Şimdilik bütün moblar 10 XP veriyor.
        PlayerDataManager.addExperience(player, 10L);
    }
}