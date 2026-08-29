package net.mcreator.kaizokuocraft.network;

import net.mcreator.kaizokuocraft.KaizokuOCraftMod;
import net.mcreator.kaizokuocraft.player.SkillManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SkillUsePacket(
        String skillId
) implements CustomPacketPayload {

    public static final Type<SkillUsePacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            KaizokuOCraftMod.MODID,
                            "skill_use"
                    )
            );

    public static final StreamCodec<
            FriendlyByteBuf,
            SkillUsePacket
            > STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) ->
                            buf.writeUtf(
                                    packet.skillId()
                            ),
                    buf ->
                            new SkillUsePacket(
                                    buf.readUtf(
                                            64
                                    )
                            )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            SkillUsePacket packet,
            IPayloadContext context
    ) {

        if (
                context.flow()
                        != PacketFlow.SERVERBOUND
        ) {
            return;
        }

        context.enqueueWork(() -> {

            if (
                    context.player()
                            instanceof net.minecraft.server.level.ServerPlayer player
            ) {

                SkillManager.useSkill(
                        player,
                        packet.skillId()
                );
            }
        });
    }
}