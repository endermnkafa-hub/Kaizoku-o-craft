package net.mcreator.kaizokuocraft.player;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class MasteryCommand {

    private MasteryCommand() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
                Commands.literal("kaizoku_mastery")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.argument("type", StringArgumentType.word())
                                        .then(
                                                Commands.literal("set")
                                                        .then(
                                                                Commands.argument("amount", DoubleArgumentType.doubleArg(0.0))
                                                                        .executes(context -> {
                                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                                            String type = StringArgumentType.getString(context, "type");
                                                                            double amount = DoubleArgumentType.getDouble(context, "amount");
                                                                            return setMastery(player, type, amount, context.getSource());
                                                                        })
                                                        )
                                        )
                                        .then(
                                                Commands.literal("add")
                                                        .then(
                                                                Commands.argument("amount", DoubleArgumentType.doubleArg(0.0))
                                                                        .executes(context -> {
                                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                                            String type = StringArgumentType.getString(context, "type");
                                                                            double amount = DoubleArgumentType.getDouble(context, "amount");
                                                                            return addMastery(player, type, amount, context.getSource());
                                                                        })
                                                        )
                                        )
                        )
        );
    }

    private static int setMastery(ServerPlayer player, String type, double amount, CommandSourceStack source) {
        PlayerData data = PlayerDataManager.get(player);
        if ("sword".equalsIgnoreCase(type)) {
            data.setSwordMastery(amount);
        } else if ("fighting".equalsIgnoreCase(type)) {
            data.setFightingMastery(amount);
        } else if ("kick".equalsIgnoreCase(type)) {
            data.setKickMastery(amount);
        } else if ("sniper".equalsIgnoreCase(type)) {
            data.setSniperMastery(amount);
        } else {
            source.sendFailure(Component.literal("Geçersiz Ustalık Türü! (sword, fighting, kick, sniper)"));
            return 0;
        }

        PlayerDataManager.sync(player);
        source.sendSuccess(() -> Component.literal("§6Kaizoku-ō Craft §7| §e" + type + " §fustalığı §e" + amount + " §folarak ayarlandı."), true);
        return 1;
    }

    private static int addMastery(ServerPlayer player, String type, double amount, CommandSourceStack source) {
        PlayerData data = PlayerDataManager.get(player);
        double current = 0.0;
        if ("sword".equalsIgnoreCase(type)) {
            current = data.getSwordMastery();
            data.setSwordMastery(current + amount);
        } else if ("fighting".equalsIgnoreCase(type)) {
            current = data.getFightingMastery();
            data.setFightingMastery(current + amount);
        } else if ("kick".equalsIgnoreCase(type)) {
            current = data.getKickMastery();
            data.setKickMastery(current + amount);
        } else if ("sniper".equalsIgnoreCase(type)) {
            current = data.getSniperMastery();
            data.setSniperMastery(current + amount);
        } else {
            source.sendFailure(Component.literal("Geçersiz Ustalık Türü! (sword, fighting, kick, sniper)"));
            return 0;
        }

        PlayerDataManager.sync(player);
        source.sendSuccess(() -> Component.literal("§6Kaizoku-ō Craft §7| §e" + type + " §fustalığına §e" + amount + " §feklendi."), true);
        return 1;
    }
}
