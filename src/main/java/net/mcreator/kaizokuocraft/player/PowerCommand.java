package net.mcreator.kaizokuocraft.player;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class PowerCommand {

    private PowerCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("kaizoku_power")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            long level = PlayerDataManager.getLevel(player);
                            double multiplier = PowerManager.getDamageMultiplier(player);

                            context.getSource().sendSuccess(
                                    () -> Component.literal(
                                            "§6Kaizoku-ō Craft §7| "
                                                    + "§fLevel: §e"
                                                    + level
                                                    + " §7| "
                                                    + "§fDamage Multiplier: §e×"
                                                    + String.format("%.2f", multiplier)
                                    ),
                                    true
                            );

                            return 1;
                        })
        );
    }
}