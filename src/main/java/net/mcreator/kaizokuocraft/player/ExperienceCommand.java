package net.mcreator.kaizokuocraft.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;

import net.mcreator.kaizokuocraft.network.SyncPlayerDataPacket;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.PacketDistributor;

public final class ExperienceCommand {

    private ExperienceCommand() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        dispatcher.register(
                Commands.literal("kaizoku_xp")

                        // /kaizoku_xp <amount>
                        // XP ekler
                        .then(
                                Commands.argument(
                                        "amount",
                                        LongArgumentType.longArg(1)
                                )
                                        .executes(context -> {

                                            ServerPlayer player =
                                                    context.getSource()
                                                            .getPlayerOrException();

                                            long amount =
                                                    LongArgumentType.getLong(
                                                            context,
                                                            "amount"
                                                    );

                                            PlayerDataManager.addExperience(
                                                    player,
                                                    amount
                                            );

                                            PlayerData data =
                                                    PlayerDataManager.get(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal(
                                                            "§6Kaizoku-ō Craft §7| "
                                                                    + "§fXP eklendi: §e"
                                                                    + amount
                                                                    + " §7| Level: §e"
                                                                    + data.getLevel()
                                                                    + " §7| XP: §e"
                                                                    + data.getExperience()
                                                    ),
                                                    true
                                            );

                                            return 1;
                                        })
                        )

                        // /kaizoku_xp set <amount>
                        // XP'yi doğrudan belirtilen değere ayarlar
                        .then(
                                Commands.literal("set")
                                        .then(
                                                Commands.argument(
                                                        "amount",
                                                        LongArgumentType.longArg(0)
                                                )
                                                        .executes(context -> {

                                                            ServerPlayer player =
                                                                    context.getSource()
                                                                            .getPlayerOrException();

                                                            long amount =
                                                                    LongArgumentType.getLong(
                                                                            context,
                                                                            "amount"
                                                                    );

                                                            PlayerData data =
                                                                    PlayerDataManager.get(
                                                                            player
                                                                    );

                                                            data.setExperience(
                                                                    amount
                                                            );

                                                            sync(player);

                                                            context.getSource()
                                                                    .sendSuccess(
                                                                            () -> Component.literal(
                                                                                    "§6Kaizoku-ō Craft §7| "
                                                                                            + "§fXP ayarlandı: §e"
                                                                                            + amount
                                                                            ),
                                                                            true
                                                                    );

                                                            return 1;
                                                        })
                                        )
                        )
        );
    }

    private static void sync(ServerPlayer player) {
        PlayerDataManager.sync(player);
    }
}