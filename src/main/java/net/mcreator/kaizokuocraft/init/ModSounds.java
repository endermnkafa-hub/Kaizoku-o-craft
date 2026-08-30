package net.mcreator.kaizokuocraft.init;

import net.mcreator.kaizokuocraft.KaizokuOCraftMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, KaizokuOCraftMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> PUNCH1 = REGISTRY.register("punch1",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "punch1")));
    public static final DeferredHolder<SoundEvent, SoundEvent> PUNCH2 = REGISTRY.register("punch2",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "punch2")));
    public static final DeferredHolder<SoundEvent, SoundEvent> UPPERCUT = REGISTRY.register("uppercut",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "uppercut")));
    public static final DeferredHolder<SoundEvent, SoundEvent> DOWNSLAM = REGISTRY.register("downslam",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "downslam")));

    public static void register(IEventBus bus) {
        REGISTRY.register(bus);
    }
}