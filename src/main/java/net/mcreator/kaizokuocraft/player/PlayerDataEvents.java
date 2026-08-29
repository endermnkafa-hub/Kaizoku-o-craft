package net.mcreator.kaizokuocraft.player;

import net.mcreator.kaizokuocraft.network.SyncPlayerDataPacket;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class PlayerDataEvents {

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
        PacketDistributor.sendToPlayer(
                player,
                new SyncPlayerDataPacket(
                        data.getLevel(),
                        data.getExperience(),
                        data.getRace()
                )
        );

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
}