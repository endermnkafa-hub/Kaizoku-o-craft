package net.mcreator.kaizokuocraft.player;

import net.mcreator.kaizokuocraft.network.StaminaSyncPacket;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.PacketDistributor;

public final class StaminaManager {

    /*
     * Level 1:
     * 100 stamina
     *
     * Her level:
     * +30 max stamina
     *
     * Level 10:
     * 370 stamina
     */
    public static final double BASE_MAX_STAMINA =
            100.0D;

    public static final double STAMINA_PER_LEVEL =
            30.0D;

    /*
     * STAMİNANIN 0 -> FULL OLMASI:
     *
     * 2 dakika = 120 saniye
     * 20 tick = 1 saniye
     * 120 x 20 = 2400 tick
     *
     * Dolayısıyla:
     *
     * regen / tick =
     * max stamina / 2400
     *
     * Böylece level ne olursa olsun
     * 0'dan full'e tam 2 dakika sürer.
     */
    private static final double FULL_REGEN_TICKS =
            120.0D * 20.0D;

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
     * Level'a göre max stamina.
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

        updateMaxStamina(
                player
        );

        if (
                data.getStamina()
                        < amount
        ) {

            /*
             * Client tarafında eski değer varsa
             * server gerçeğini tekrar gönder.
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

        sync(
                player
        );

        return true;
    }

    /*
     * REGEN
     *
     * Her oyuncunun max stamina'sına göre
     * dinamik hesaplanır.
     *
     * Level 1:
     * 100 / 2400 = 0.041666... / tick
     *
     * Level 10:
     * 370 / 2400 = 0.154166... / tick
     *
     * İkisinde de:
     * 0 -> full = 120 saniye
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

        double current =
                data.getStamina();

        double max =
                data.getMaxStamina();

        if (
                current >= max
        ) {

            /*
             * Full ise hiçbir şey yapma.
             */
            if (
                    current > max
            ) {

                data.setStamina(
                        max
                );
            }

            return;
        }

        /*
         * 0 -> full tam 2400 tick.
         */
        double regenPerTick =
                max
                        / FULL_REGEN_TICKS;

        /*
         * Full'e yaklaşırken taşmayı önle.
         */
        double newStamina =
                Math.min(
                        max,
                        current
                                + regenPerTick
                );

        data.setStamina(
                newStamina
        );
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
             * Client sync.
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