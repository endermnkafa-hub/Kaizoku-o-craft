package net.mcreator.kaizokuocraft.player;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class StyleCommand {

    private StyleCommand() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
                Commands.literal("kaizoku_style")
                        .requires(source -> source.hasPermission(0))
                        .then(
                                Commands.literal("fist")
                                        .executes(context -> setStyle(context.getSource().getPlayerOrException(), "FIST"))
                        )
                        .then(
                                Commands.literal("sword")
                                        .executes(context -> setStyle(context.getSource().getPlayerOrException(), "SWORD"))
                        )
                        .then(
                                Commands.literal("kick")
                                        .executes(context -> setStyle(context.getSource().getPlayerOrException(), "KICK"))
                        )
                        .then(
                                Commands.literal("sniper")
                                        .executes(context -> setStyle(context.getSource().getPlayerOrException(), "SNIPER"))
                        )
        );
    }

    private static int setStyle(ServerPlayer player, String styleName) {
        PlayerData data = PlayerDataManager.get(player);
        data.setCombatStyle(styleName);
        PlayerDataManager.sync(player);

        player.sendSystemMessage(Component.literal("§6Kaizoku-ō Craft §7| §fSavaş Stili: §e" + styleName));
        return 1;
    }
}
