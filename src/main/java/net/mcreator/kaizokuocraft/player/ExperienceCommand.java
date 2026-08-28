package net.mcreator.kaizokuocraft.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ExperienceCommand {

    private ExperienceCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("kaizoku_xp")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.argument("amount", LongArgumentType.longArg(1))
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            long amount = LongArgumentType.getLong(context, "amount");

                                            PlayerDataManager.addExperience(player, amount);

                                            PlayerData data = PlayerDataManager.get(player);

                                            double damageMultiplier =
                                                    PowerManager.getDamageMultiplier(player);

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal(
                                                            "§6Kaizoku-ō Craft §7| "
                                                                    + "§fXP: §e"
                                                                    + data.getExperience()
                                                                    + " §7| Level: §e"
                                                                    + data.getLevel()
                                                                    + " §7| Damage: §e×"
                                                                    + String.format("%.2f", damageMultiplier)
                                                    ),
                                                    true
                                            );

                                            return 1;
                                        })
                        )
        );
    }
}