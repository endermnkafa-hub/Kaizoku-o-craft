/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.kaizokuocraft.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.mcreator.kaizokuocraft.KaizokuOCraftMod;

public class KaizokuOCraftModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, KaizokuOCraftMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> PUNCH1 = REGISTRY.register("punch1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("kaizoku_o_craft", "punch1")));
	public static final DeferredHolder<SoundEvent, SoundEvent> PUNCH2 = REGISTRY.register("punch2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("kaizoku_o_craft", "punch2")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DOWNSLAM = REGISTRY.register("downslam", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("kaizoku_o_craft", "downslam")));
	public static final DeferredHolder<SoundEvent, SoundEvent> UPPERCUT = REGISTRY.register("uppercut", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("kaizoku_o_craft", "uppercut")));
}