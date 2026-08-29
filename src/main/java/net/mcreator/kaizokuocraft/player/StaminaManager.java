package net.mcreator.kaizokuocraft.player;

import net.mcreator.kaizokuocraft.network.StaminaSyncPacket;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.PacketDistributor;

public final class StaminaManager {

    /*
     * ==========================================
     * MAX STAMINA
     * ==========================================
     *
     * Level 1  = 100
     * Her level = +30
     *
     * Level 10 = 370
     */
    public static final double BASE_MAX_STAMINA =
            100.0D;

    public static final double STAMINA_PER_LEVEL =
            30.0D;

    /*
     * ==========================================
     * REGEN SÜRESİ
     * ==========================================
     *
     * Bu değer:
     *
     * "0 -> max stamina"
     * ne kadar sürede tamamlanacak?
     *
     * 120 = 2 dakika
     * 60  = 1 dakika
     *
     * ÖNEMLİ:
     *
     * Max stamina yükselirse regen hızı
     * otomatik olarak yükselir.
     *
     * Örnek:
     *
     * 120 max / 120 sn = 1 STA/sn
     * 240 max / 120 sn = 2 STA/sn
     * 370 max / 120 sn = 3.08 STA/sn
     */
    public static final double REGEN_DURATION_SECONDS =
            120.0D;

    /*
     * Client'a her 5 tickte bir sync.
     */
    private static final int SYNC_INTERVAL =
            5;

    private StaminaManager() {
    }

    /*
     * ==========================================
     * MAX STAMINA HESABI
     * ==========================================
     */
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
     * ==========================================
     * MAX STAMINA GÜNCELLE
     * ==========================================
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
     * ==========================================
     * STAMINA HARCAMA
     * ==========================================
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

        /*
         * Yeterli stamina yok.
         */
        if (
                data.getStamina()
                        < amount
        ) {

            sync(
                    player
            );

            return false;
        }

        /*
         * Stamina düşür.
         */
        data.setStamina(
                data.getStamina()
                        - amount
        );

        /*
         * Anında client'a gönder.
         */
        sync(
                player
        );

        return true;
    }

    /*
     * ==========================================
     * STAMINA REGEN
     * ==========================================
     *
     * FORMÜL:
     *
     * regen/tick =
     * maxStamina /
     * (regenDurationSeconds * 20)
     *
     * Böylece:
     *
     * 100 max → 0 -> 100 = 120 sn
     * 370 max → 0 -> 370 = 120 sn
     *
     * Yani max stamina büyüdükçe
     * regen hızı da büyür.
     */
    public static void regenerate(
            ServerPlayer player
    ) {

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        /*
         * Level değişmiş olabilir.
         */
        updateMaxStamina(
                player
        );

        double current =
                data.getStamina();

        double max =
                data.getMaxStamina();

        /*
         * Zaten full.
         */
        if (
                current >= max
        ) {

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
         * 20 tick = 1 saniye.
         */
        double regenPerTick =
                max
                        / (
                        REGEN_DURATION_SECONDS
                                * 20.0D
                );

        /*
         * Max değerini aşma.
         */
        data.setStamina(
                Math.min(
                        max,
                        current
                                + regenPerTick
                )
        );
    }

    /*
     * ==========================================
     * SERVER TICK
     * ==========================================
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

            /*
             * HER DURUMDA regen:
             *
             * Normal
             * Combat
             * Menü
             */
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
     * ==========================================
     * CLIENT SYNC
     * ==========================================
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