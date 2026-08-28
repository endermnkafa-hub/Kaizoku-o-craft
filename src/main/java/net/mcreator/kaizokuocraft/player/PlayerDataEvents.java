package net.mcreator.kaizokuocraft.player;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class PlayerDataEvents {

    private PlayerDataEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        PlayerData data = PlayerDataManager.get(player);
        long requiredXp = PlayerDataManager.getRequiredExperience(data.getLevel());

        player.sendSystemMessage(
                Component.literal(
                        "§6Kaizoku-ō Craft §7| §fLevel: §e"
                                + data.getLevel()
                                + " §7| §fXP: §e"
                                + data.getExperience()
                                + "§7/§e"
                                + requiredXp
                )
        );
    }
}