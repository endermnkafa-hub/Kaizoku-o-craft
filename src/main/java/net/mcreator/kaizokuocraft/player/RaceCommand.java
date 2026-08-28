package net.mcreator.kaizokuocraft.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class RaceCommand {

    private RaceCommand() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
                Commands.literal("kaizoku_race")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.argument(
                                        "race",
                                        StringArgumentType.word()
                                )
                                        .executes(context -> {
                                            ServerPlayer player =
                                                    context.getSource()
                                                            .getPlayerOrException();

                                            String raceName =
                                                    StringArgumentType.getString(
                                                            context,
                                                            "race"
                                                    );

                                            RaceType race;

                                            try {
                                                race = RaceType.valueOf(
                                                        raceName.toUpperCase()
                                                );
                                            } catch (IllegalArgumentException exception) {
                                                context.getSource().sendFailure(
                                                        Component.literal(
                                                                "Geçersiz race. Kullanılabilir: human, fish_man, mink, giant"
                                                        )
                                                );

                                                return 0;
                                            }

                                            PlayerDataManager.setRace(
                                                    player,
                                                    race
                                            );

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal(
                                                            "§6Kaizoku-ō Craft §7| §fRace: §e"
                                                                    + race.getDisplayName()
                                                    ),
                                                    true
                                            );

                                            return 1;
                                        })
                        )
        );
    }
}