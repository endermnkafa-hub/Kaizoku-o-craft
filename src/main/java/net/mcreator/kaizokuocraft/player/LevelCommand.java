package net.mcreator.kaizokuocraft.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;

import net.mcreator.kaizokuocraft.network.SyncPlayerDataPacket;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.PacketDistributor;

public final class LevelCommand {

    private LevelCommand() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
                Commands.literal("kaizoku_level")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.literal("set")
                                        .then(
                                                Commands.argument(
                                                        "level",
                                                        LongArgumentType.longArg(1)
                                                )
                                                        .executes(context -> {
                                                            ServerPlayer player =
                                                                    context.getSource()
                                                                            .getPlayerOrException();

                                                            long level =
                                                                    LongArgumentType.getLong(
                                                                            context,
                                                                            "level"
                                                                    );

                                                            PlayerData data =
                                                                    PlayerDataManager.get(player);

                                                            data.setLevel(level);

                                                            sync(player);

                                                            context.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "§6Kaizoku-ō Craft §7| §fLevel: §e"
                                                                                    + level
                                                                    ),
                                                                    true
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )
                        .then(
                                Commands.literal("reset")
                                        .executes(context -> {
                                            ServerPlayer player =
                                                    context.getSource()
                                                            .getPlayerOrException();

                                            PlayerData data =
                                                    PlayerDataManager.get(player);

                                            data.setLevel(1L);
                                            data.setExperience(0L);

                                            sync(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal(
                                                            "§6Kaizoku-ō Craft §7| §fLevel ve XP sıfırlandı."
                                                    ),
                                                    true
                                            );

                                            return 1;
                                        })
                        )
        );
    }

    private static void sync(ServerPlayer player) {
        PlayerData data = PlayerDataManager.get(player);

        PacketDistributor.sendToPlayer(
                player,
                new SyncPlayerDataPacket(
                        data.getLevel(),
                        data.getExperience(),
                        data.getRace()
                )
        );
    }
}