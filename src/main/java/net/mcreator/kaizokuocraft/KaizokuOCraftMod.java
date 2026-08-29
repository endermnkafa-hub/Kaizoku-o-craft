package net.mcreator.kaizokuocraft;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.server.TickTask;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.kaizokuocraft.player.*;
import net.mcreator.kaizokuocraft.network.SyncPlayerDataPacket;
import net.mcreator.kaizokuocraft.network.StaminaSyncPacket;
import net.mcreator.kaizokuocraft.network.SkillUsePacket;
import net.mcreator.kaizokuocraft.client.KaizokuHud;
import net.mcreator.kaizokuocraft.client.CombatVanillaHud;
import net.mcreator.kaizokuocraft.client.CombatHud;
import net.mcreator.kaizokuocraft.client.ClientGameEventHandler;
import net.mcreator.kaizokuocraft.client.ClientEventHandler;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;

@Mod("kaizoku_o_craft")
public class KaizokuOCraftMod {
	public static final Logger LOGGER = LogManager.getLogger(KaizokuOCraftMod.class);
	public static final String MODID = "kaizoku_o_craft";

	public KaizokuOCraftMod(IEventBus modEventBus) {
		// Start of user code block mod constructor
		ModAttachments.register(modEventBus);
		NeoForge.EVENT_BUS.register(PlayerDataEvents.class);
		NeoForge.EVENT_BUS.register(ExperienceEvents.class);
		NeoForge.EVENT_BUS.register(DamageEvents.class);
		addNetworkMessage(SyncPlayerDataPacket.TYPE, SyncPlayerDataPacket.STREAM_CODEC, SyncPlayerDataPacket::handle);
		addNetworkMessage(SkillUsePacket.TYPE, SkillUsePacket.STREAM_CODEC, SkillUsePacket::handle);
		addNetworkMessage(StaminaSyncPacket.TYPE, StaminaSyncPacket.STREAM_CODEC, StaminaSyncPacket::handle);
		if (FMLEnvironment.dist == Dist.CLIENT) {
			modEventBus.register(ClientEventHandler.class);
			NeoForge.EVENT_BUS.register(ClientGameEventHandler.class);
			NeoForge.EVENT_BUS.register(KaizokuHud.class);
			NeoForge.EVENT_BUS.register(CombatHud.class);
			NeoForge.EVENT_BUS.register(CombatVanillaHud.class);
		}
		// End of user code block mod constructor
		NeoForge.EVENT_BUS.register(this);
		modEventBus.addListener(this::registerNetworking);
		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent event) {
		ExperienceCommand.register(event.getDispatcher());
		PowerCommand.register(event.getDispatcher());
		RaceCommand.register(event.getDispatcher());
		LevelCommand.register(event.getDispatcher());
	}

	// End of user code block mod methods
	private static boolean networkingRegistered = false;
	private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

	private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
	}

	public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
		if (networkingRegistered)
			throw new IllegalStateException("Cannot register new network messages after networking has been registered");
		MESSAGES.put(id, new NetworkMessage<>(reader, handler));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void registerNetworking(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(MODID);
		MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, ((NetworkMessage) networkMessage).reader(), ((NetworkMessage) networkMessage).handler()));
		networkingRegistered = true;
	}

	private static final Queue<IntObjectPair<Runnable>> workToBeScheduled = new ConcurrentLinkedQueue<>();
	private static final PriorityQueue<TickTask> workQueue = new PriorityQueue<>(Comparator.comparingInt(TickTask::getTick));

	public static void queueServerWork(int delay, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workToBeScheduled.add(new IntObjectImmutablePair<>(delay, action));
	}

	while (!workQueue.isEmpty() && currentTick >= workQueue.peek().getTick()) {
	    workQueue.poll().run();
	}
	
	StaminaManager.tickServer(
	        event.getServer()
	);
}