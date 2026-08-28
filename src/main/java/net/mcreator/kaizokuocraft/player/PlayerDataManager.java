package net.mcreator.kaizokuocraft.player;

import net.minecraft.server.level.ServerPlayer;

public final class PlayerDataManager {

    private PlayerDataManager() {
    }

    public static PlayerData get(ServerPlayer player) {
        return player.getData(ModAttachments.PLAYER_DATA);
    }

    public static long getLevel(ServerPlayer player) {
        return get(player).getLevel();
    }

    public static long getExperience(ServerPlayer player) {
        return get(player).getExperience();
    }

    public static long getRequiredExperience(long level) {
        return 100L + (level * 25L);
    }

    public static void addExperience(ServerPlayer player, long amount) {
        if (amount <= 0) {
            return;
        }

        PlayerData data = get(player);
        data.addExperience(amount);

        while (data.getExperience() >= getRequiredExperience(data.getLevel())) {
            long required = getRequiredExperience(data.getLevel());

            data.setExperience(data.getExperience() - required);
            data.setLevel(data.getLevel() + 1);

            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal(
                            "§6Kaizoku-ō Craft §7| §eLEVEL UP! §f→ Level §e" + data.getLevel()
                    )
            );
        }
    }
}