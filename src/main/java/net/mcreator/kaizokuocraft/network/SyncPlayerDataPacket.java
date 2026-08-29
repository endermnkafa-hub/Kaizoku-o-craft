package net.mcreator.kaizokuocraft.network;

import net.mcreator.kaizokuocraft.client.ClientPlayerData;
import net.mcreator.kaizokuocraft.KaizokuOCraftMod;
import net.mcreator.kaizokuocraft.player.RaceType;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;

public record SyncPlayerDataPacket(
        long level,
        long experience,
        RaceType race,
        String combatStyle,
        int statPoints,
        int strength,
        int defense,
        double swordMastery,
        double fightingMastery,
        double sniperMastery,
        double kickMastery,
        net.minecraft.nbt.CompoundTag hakiData,
        net.minecraft.nbt.CompoundTag fruitData
) implements CustomPacketPayload {

    public static final Type<SyncPlayerDataPacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            KaizokuOCraftMod.MODID,
                            "sync_player_data"
                    )
            );

    public static final StreamCodec<FriendlyByteBuf, SyncPlayerDataPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeVarLong(packet.level());
                        buf.writeVarLong(packet.experience());
                        buf.writeUtf(packet.race().name());
                        buf.writeUtf(packet.combatStyle());
                        buf.writeInt(packet.statPoints());
                        buf.writeInt(packet.strength());
                        buf.writeInt(packet.defense());
                        buf.writeDouble(packet.swordMastery());
                        buf.writeDouble(packet.fightingMastery());
                        buf.writeDouble(packet.sniperMastery());
                        buf.writeDouble(packet.kickMastery());
                        buf.writeNbt(packet.hakiData());
                        buf.writeNbt(packet.fruitData());
                    },
                    buf -> {
                        long level = buf.readVarLong();
                        long experience = buf.readVarLong();

                        RaceType race;
                        try {
                            race = RaceType.valueOf(buf.readUtf());
                        } catch (IllegalArgumentException exception) {
                            race = RaceType.HUMAN;
                        }

                        String combatStyle = buf.readUtf();
                        int statPoints = buf.readInt();
                        int strength = buf.readInt();
                        int defense = buf.readInt();
                        double swordMastery = buf.readDouble();
                        double fightingMastery = buf.readDouble();
                        double sniperMastery = buf.readDouble();
                        double kickMastery = buf.readDouble();
                        net.minecraft.nbt.CompoundTag hakiData = buf.readNbt();
                        net.minecraft.nbt.CompoundTag fruitData = buf.readNbt();

                        return new SyncPlayerDataPacket(
                                level,
                                experience,
                                race,
                                combatStyle,
                                statPoints,
                                strength,
                                defense,
                                swordMastery,
                                fightingMastery,
                                sniperMastery,
                                kickMastery,
                                hakiData != null ? hakiData : new net.minecraft.nbt.CompoundTag(),
                                fruitData != null ? fruitData : new net.minecraft.nbt.CompoundTag()
                        );
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            SyncPlayerDataPacket packet,
            IPayloadContext context
    ) {
        if (context.flow() != PacketFlow.CLIENTBOUND) {
            return;
        }

        context.enqueueWork(() ->
                ClientPlayerData.set(
                        packet.level(),
                        packet.experience(),
                        packet.race(),
                        packet.combatStyle(),
                        packet.statPoints(),
                        packet.strength(),
                        packet.defense(),
                        packet.swordMastery(),
                        packet.fightingMastery(),
                        packet.sniperMastery(),
                        packet.kickMastery(),
                        packet.hakiData(),
                        packet.fruitData()
                )
        );
    }
}