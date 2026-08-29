package net.mcreator.kaizokuocraft.network;

import net.mcreator.kaizokuocraft.KaizokuOCraftMod;
import net.mcreator.kaizokuocraft.player.PlayerData;
import net.mcreator.kaizokuocraft.player.PlayerDataManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;

public record AllocateStatPacket(
        String statType
) implements CustomPacketPayload {

    public static final Type<AllocateStatPacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            KaizokuOCraftMod.MODID,
                            "allocate_stat"
                    )
            );

    public static final StreamCodec<FriendlyByteBuf, AllocateStatPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeUtf(packet.statType()),
                    buf -> new AllocateStatPacket(buf.readUtf())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            AllocateStatPacket packet,
            IPayloadContext context
    ) {
        if (context.flow() != PacketFlow.SERVERBOUND) {
            return;
        }

        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerData data = PlayerDataManager.get(player);
                if (data.getStatPoints() > 0) {
                    if ("STRENGTH".equals(packet.statType())) {
                        data.setStatPoints(data.getStatPoints() - 1);
                        data.setStrength(data.getStrength() + 1);
                        PlayerDataManager.sync(player);
                    } else if ("DEFENSE".equals(packet.statType())) {
                        data.setStatPoints(data.getStatPoints() - 1);
                        data.setDefense(data.getDefense() + 1);
                        PlayerDataManager.sync(player);
                    }
                }
            }
        });
    }
}
