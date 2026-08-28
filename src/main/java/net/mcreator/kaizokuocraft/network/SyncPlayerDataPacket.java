package net.mcreator.kaizokuocraft.network;

import net.mcreator.kaizokuocraft.client.ClientPlayerData;
import net.mcreator.kaizokuocraft.KaizokuOCraftMod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.network.handling.IPayloadContext;


import net.minecraft.network.codec.StreamCodec;

public record SyncPlayerDataPacket(long level, long experience) implements CustomPacketPayload {

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
                    },
                    buf -> new SyncPlayerDataPacket(
                            buf.readVarLong(),
                            buf.readVarLong()
                    )
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
                        packet.experience()
                )
        );
    }
}