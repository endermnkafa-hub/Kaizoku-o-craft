package net.mcreator.kaizokuocraft.player;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.PacketDistributor;

import net.mcreator.kaizokuocraft.network.SyncPlayerDataPacket;

public final class PlayerDataManager {

    private PlayerDataManager() {
    }

    public static PlayerData get(
            ServerPlayer player
    ) {

        return player.getData(
                ModAttachments.PLAYER_DATA
        );
    }

    public static RaceType getRace(
            ServerPlayer player
    ) {

        return get(player)
                .getRace();
    }

    public static void setRace(
            ServerPlayer player,
            RaceType race
    ) {

        get(player).setRace(
                race
        );

        RaceManager.applyRace(
                player
        );
    }

    public static long getLevel(
            ServerPlayer player
    ) {

        return get(player)
                .getLevel();
    }

    public static long getExperience(
            ServerPlayer player
    ) {

        return get(player)
                .getExperience();
    }

    public static long getRequiredExperience(
            long level
    ) {

        if (
                level < 1L
        ) {

            level = 1L;
        }

        double required =
                100.0D
                        * Math.pow(
                                level,
                                1.5D
                        );

        if (
                required >= Long.MAX_VALUE
        ) {

            return Long.MAX_VALUE;
        }

        return Math.max(
                100L,
                (long) required
        );
    }

    public static void addExperience(
            ServerPlayer player,
            long amount
    ) {

        if (
                amount <= 0
        ) {

            return;
        }

        PlayerData data =
                get(player);

        data.addExperience(
                amount
        );

        boolean levelChanged =
                false;

        while (
                data.getExperience()
                        >= getRequiredExperience(
                        data.getLevel()
                )
        ) {

            long required =
                    getRequiredExperience(
                            data.getLevel()
                    );

            data.setExperience(
                    data.getExperience()
                            - required
            );

            data.setLevel(
                    data.getLevel()
                            + 1L
            );

            data.addStatPoints(5);

            levelChanged =
                    true;
        }

        /*
         * Level değiştiyse max stamina
         * anında level'a göre güncellenir.
         */
        if (
                levelChanged
        ) {

            StaminaManager.updateMaxStamina(
                    player
            );
        }

        /*
         * Level / XP sync.
         */
        sync(player);

        /*
         * Max stamina değişmişse
         * client'a da gönder.
         */
        if (
                levelChanged
        ) {

            StaminaManager.sync(
                    player
            );
        }
    }

    public static void sync(ServerPlayer player) {
        PlayerData data = get(player);
        PacketDistributor.sendToPlayer(
                player,
                new SyncPlayerDataPacket(
                        data.isCharacterCreated(),
                        data.getFaction(),
                        data.getLevel(),
                        data.getExperience(),
                        data.getRace(),
                        data.getCombatStyle(),
                        data.getStatPoints(),
                        data.getStrength(),
                        data.getDefense(),
                        data.getSwordMastery(),
                        data.getFightingMastery(),
                        data.getSniperMastery(),
                        data.getKickMastery(),
                        data.getHakiData(),
                        data.getFruitData()
                )
        );
    }
}