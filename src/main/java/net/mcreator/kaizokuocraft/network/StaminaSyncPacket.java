package net.mcreator.kaizokuocraft.network;

import net.mcreator.kaizokuocraft.KaizokuOCraftMod;
import net.mcreator.kaizokuocraft.client.ClientStamina;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StaminaSyncPacket(
        double stamina,
        double maxStamina
) implements CustomPacketPayload {

    public static final Type<StaminaSyncPacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            KaizokuOCraftMod.MODID,
                            "sync_stamina"
                    )
            );

    public static final StreamCodec<
            FriendlyByteBuf,
            StaminaSyncPacket
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {

                        buf.writeDouble(
                                packet.stamina()
                        );

                        buf.writeDouble(
                                packet.maxStamina()
                        );
                    },
                    buf ->
                            new StaminaSyncPacket(
                                    buf.readDouble(),
                                    buf.readDouble()
                            )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            StaminaSyncPacket packet,
            IPayloadContext context
    ) {

        if (
                context.flow()
                        != PacketFlow.CLIENTBOUND
        ) {
            return;
        }

        context.enqueueWork(
                () ->
                        ClientStamina.set(
                                packet.stamina(),
                                packet.maxStamina()
                        )
        );
    }
}