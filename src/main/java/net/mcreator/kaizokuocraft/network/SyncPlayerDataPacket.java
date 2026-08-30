package net.mcreator.kaizokuocraft.network;

import net.mcreator.kaizokuocraft.client.ClientPlayerData;
import net.mcreator.kaizokuocraft.KaizokuOCraftMod;
import net.mcreator.kaizokuocraft.player.FactionType;
import net.mcreator.kaizokuocraft.player.RaceType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;

public record SyncPlayerDataPacket(
        boolean characterCreated,
        FactionType faction,
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
        CompoundTag hakiData,
        CompoundTag fruitData
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
                        buf.writeBoolean(packet.characterCreated());
                        buf.writeUtf(packet.faction() != null ? packet.faction().name() : "PIRATE");
                        buf.writeVarLong(packet.level());
                        buf.writeVarLong(packet.experience());
                        buf.writeUtf(packet.race() != null ? packet.race().name() : "HUMAN");
                        buf.writeUtf(packet.combatStyle() != null ? packet.combatStyle() : "FIST");
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
                        boolean characterCreated = buf.readBoolean();
                        FactionType faction;
                        try {
                            faction = FactionType.valueOf(buf.readUtf());
                        } catch (Exception e) {
                            faction = FactionType.PIRATE;
                        }

                        long level = buf.readVarLong();
                        long experience = buf.readVarLong();

                        RaceType race;
                        try {
                            race = RaceType.valueOf(buf.readUtf());
                        } catch (Exception e) {
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
                        CompoundTag hakiData = buf.readNbt();
                        CompoundTag fruitData = buf.readNbt();

                        return new SyncPlayerDataPacket(
                                characterCreated,
                                faction,
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
                                hakiData != null ? hakiData : new CompoundTag(),
                                fruitData != null ? fruitData : new CompoundTag()
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
                        packet.characterCreated(),
                        packet.faction(),
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