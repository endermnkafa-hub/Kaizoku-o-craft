package net.mcreator.kaizokuocraft.player;

import net.mcreator.kaizokuocraft.network.SyncPlayerDataPacket;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class PlayerDataEvents {

    private static final int STAMINA_SYNC_INTERVAL =
            5;

    private PlayerDataEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(
            PlayerEvent.PlayerLoggedInEvent event
    ) {

        if (
                !(event.getEntity()
                        instanceof ServerPlayer player)
        ) {

            return;
        }

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        /*
         * Level -> max stamina.
         */
        StaminaManager.updateMaxStamina(
                player
        );

        /*
         * Race.
         */
        RaceManager.applyRace(
                player
        );

        /*
         * Normal player data.
         */
        PlayerDataManager.sync(player);

        /*
         * Gerçek server stamina'sı.
         */
        StaminaManager.sync(
                player
        );
    }

    @SubscribeEvent
    public static void onPlayerClone(
            PlayerEvent.Clone event
    ) {

        if (
                !(event.getEntity()
                        instanceof ServerPlayer player)
        ) {

            return;
        }

        RaceManager.applyRace(
                player
        );

        StaminaManager.updateMaxStamina(
                player
        );

        StaminaManager.sync(
                player
        );
    }

    /*
     * ==========================================
     * STAMINA TICK
     * ==========================================
     *
     * Bu event her oyuncu için her tick çalışır.
     *
     * Sadece logical server tarafında çalıştırıyoruz.
     *
     * Böylece:
     *
     * Normal oyun       -> regen
     * Combat Mode       -> regen
     * Menü              -> regen
     *
     * Hepsi aynı sistemden geçer.
     */
    @SubscribeEvent
    public static void onPlayerTick(
            PlayerTickEvent.Post event
    ) {

        /*
         * Client tarafında stamina değiştirme.
         */
        if (
                event.getEntity()
                        .level()
                        .isClientSide()
        ) {

            return;
        }

        /*
         * Server oyuncusu değilse çık.
         */
        if (
                !(event.getEntity()
                        instanceof ServerPlayer player)
        ) {

            return;
        }

        /*
         * ASIL REGEN.
         */
        StaminaManager.regenerate(
                player
        );

        /*
         * Client HUD'ına güncel değeri gönder.
         *
         * Her tick packet göndermek yerine
         * 5 tickte bir.
         */
        if (
                player.tickCount
                        % STAMINA_SYNC_INTERVAL
                        == 0
        ) {

            StaminaManager.sync(
                    player
            );
        }
    }
}