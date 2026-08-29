package net.mcreator.kaizokuocraft.player;

import net.mcreator.kaizokuocraft.network.StaminaSyncPacket;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.PacketDistributor;

public final class StaminaManager {

    /*
     * Level 1:
     * 100 stamina
     */
    public static final double BASE_MAX_STAMINA =
            100.0D;

    /*
     * Her level:
     * +2 max stamina
     *
     * Level 10:
     * 118 stamina
     */
    public static final double STAMINA_PER_LEVEL =
            2.0D;

    /*
     * 20 tick = 1 saniye
     *
     * 0.35 x 20 = 7 stamina / saniye
     */
    public static final double REGEN_PER_TICK =
            0.35D;

    /*
     * Client'a her 5 tickte bir sync.
     */
    private static final int SYNC_INTERVAL =
            5;

    private StaminaManager() {
    }

    public static double getMaxStaminaForLevel(
            long level
    ) {

        if (
                level < 1L
        ) {

            level = 1L;
        }

        return BASE_MAX_STAMINA
                + (
                Math.max(
                        0L,
                        level - 1L
                )
                        * STAMINA_PER_LEVEL
        );
    }

    /*
     * Oyuncunun max stamina'sını
     * level'a göre günceller.
     */
    public static void updateMaxStamina(
            ServerPlayer player
    ) {

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        double newMax =
                getMaxStaminaForLevel(
                        data.getLevel()
                );

        data.setMaxStamina(
                newMax
        );
    }

    /*
     * Stamina harca.
     *
     * true  = başarılı
     * false = stamina yetmedi
     */
    public static boolean consume(
            ServerPlayer player,
            double amount
    ) {

        if (
                amount <= 0.0D
        ) {

            return true;
        }

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        /*
         * Her kullanımda level'a göre
         * max değeri garanti et.
         */
        updateMaxStamina(
                player
        );

        if (
                data.getStamina()
                        < amount
        ) {

            /*
             * Client eski değer gösteriyor
             * olabilir. Gerçeği tekrar gönder.
             */
            sync(
                    player
            );

            return false;
        }

        data.setStamina(
                data.getStamina()
                        - amount
        );

        /*
         * Kullanım sonrası anında sync.
         */
        sync(
                player
        );

        return true;
    }

    /*
     * Regen.
     */
    public static void regenerate(
            ServerPlayer player
    ) {

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        updateMaxStamina(
                player
        );

        if (
                data.getStamina()
                        < data.getMaxStamina()
        ) {

            data.addStamina(
                    REGEN_PER_TICK
            );
        }
    }

    /*
     * Server tick.
     */
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

            regenerate(
                    player
            );

            /*
             * Client sync
             */
            if (
                    tick % SYNC_INTERVAL
                            == 0
            ) {

                sync(
                        player
                );
            }
        }
    }

    /*
     * Client'a stamina gönder.
     */
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