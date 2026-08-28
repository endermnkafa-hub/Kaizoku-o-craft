package net.mcreator.kaizokuocraft.player;

import java.util.function.Supplier;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.mcreator.kaizokuocraft.KaizokuOCraftMod;

public final class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, KaizokuOCraftMod.MODID);

    public static final Supplier<AttachmentType<PlayerData>> PLAYER_DATA =
            ATTACHMENT_TYPES.register(
                    "player_data",
                    () -> AttachmentType.serializable(PlayerData::new)
                            .copyOnDeath()
                            .build()
            );

    private ModAttachments() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}