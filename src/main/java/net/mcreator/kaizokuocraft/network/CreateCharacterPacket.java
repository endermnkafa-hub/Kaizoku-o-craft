package net.mcreator.kaizokuocraft.network;

import net.mcreator.kaizokuocraft.KaizokuOCraftMod;
import net.mcreator.kaizokuocraft.player.FactionType;
import net.mcreator.kaizokuocraft.player.FightingStyle;
import net.mcreator.kaizokuocraft.player.PlayerData;
import net.mcreator.kaizokuocraft.player.PlayerDataManager;
import net.mcreator.kaizokuocraft.player.RaceManager;
import net.mcreator.kaizokuocraft.player.RaceType;
import net.mcreator.kaizokuocraft.player.StaminaManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CreateCharacterPacket(
        RaceType race,
        FactionType faction,
        FightingStyle fightingStyle
) implements CustomPacketPayload {

    public static final Type<CreateCharacterPacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            KaizokuOCraftMod.MODID,
                            "create_character"
                    )
            );

    public static final StreamCodec<FriendlyByteBuf, CreateCharacterPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeUtf(packet.race() != null ? packet.race().name() : "HUMAN");
                        buf.writeUtf(packet.faction() != null ? packet.faction().name() : "PIRATE");
                        buf.writeUtf(packet.fightingStyle() != null ? packet.fightingStyle().name() : "FIST");
                    },
                    buf -> {
                        RaceType race;
                        try {
                            race = RaceType.valueOf(buf.readUtf());
                        } catch (Exception e) {
                            race = RaceType.HUMAN;
                        }

                        FactionType faction;
                        try {
                            faction = FactionType.valueOf(buf.readUtf());
                        } catch (Exception e) {
                            faction = FactionType.PIRATE;
                        }

                        FightingStyle style;
                        try {
                            style = FightingStyle.valueOf(buf.readUtf());
                        } catch (Exception e) {
                            style = FightingStyle.FIST;
                        }

                        return new CreateCharacterPacket(race, faction, style);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            CreateCharacterPacket packet,
            IPayloadContext context
    ) {
        if (context.flow() != PacketFlow.SERVERBOUND) {
            return;
        }

        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerData data = PlayerDataManager.get(player);
                data.setCharacterCreated(true);
                data.setRace(packet.race());
                data.setFaction(packet.faction());
                data.setFightingStyle(packet.fightingStyle());
                RaceManager.applyRace(player);
                PlayerDataManager.sync(player);
                StaminaManager.sync(player);

                // Play level up / quest fanfare sound
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.sendSystemMessage(Component.literal("§6[Kaizoku-ō Craft] §aKarakterin başarıyla oluşturuldu! Yolculuğun başlıyor..."));
            }
        });
    }
}