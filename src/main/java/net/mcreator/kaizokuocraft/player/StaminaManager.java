package net.mcreator.kaizokuocraft.player;

import net.mcreator.kaizokuocraft.network.StaminaSyncPacket;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.PacketDistributor;

public final class StaminaManager {

    public static final double DEFAULT_MAX_STAMINA =
            100.0D;

    public static final double REGEN_PER_TICK =
            0.20D;

    private static final int SYNC_INTERVAL =
            5;

    private StaminaManager() {
    }

    public static boolean consume(
            ServerPlayer player,
            double amount
    ) {

        if (amount <= 0.0D) {
            return true;
        }

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        if (
                data.getStamina()
                        < amount
        ) {
            return false;
        }

        data.setStamina(
                data.getStamina()
                        - amount
        );

        sync(player);

        return true;
    }

    public static void regenerate(
            ServerPlayer player
    ) {

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        if (
                data.getMaxStamina()
                        <= 0.0D
        ) {

            data.setMaxStamina(
                    DEFAULT_MAX_STAMINA
            );
        }

        if (
                data.getStamina()
                        < data.getMaxStamina()
        ) {

            data.addStamina(
                    REGEN_PER_TICK
            );
        }
    }

    public static void tickServer(
            MinecraftServer server
    ) {

        int tick =
                server.getTickCount();

        for (
                ServerPlayer player :
                server.getPlayerList()
                        .getPlayers()
        ) {

            regenerate(player);

            /*
             * Her 5 tickte clientı güncelle.
             */
            if (
                    tick % SYNC_INTERVAL
                            == 0
            ) {

                sync(player);
            }
        }
    }

    public static void sync(
            ServerPlayer player
    ) {

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        PacketDistributor.sendToPlayer(
                player,
                new StaminaSyncPacket(
                        data.getStamina(),
                        data.getMaxStamina()
                )
        );
    }
}