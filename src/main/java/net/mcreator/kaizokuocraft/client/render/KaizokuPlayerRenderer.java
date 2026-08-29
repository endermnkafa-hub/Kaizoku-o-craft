package net.mcreator.kaizokuocraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;

/**
 * Intercepts vanilla player rendering via RenderPlayerEvent and renders the
 * custom GeckoLib model (player.geo.json) with skill animations instead.
 *
 * Registered on NeoForge.EVENT_BUS (game bus) inside KaizokuOCraftMod constructor.
 */
public class KaizokuPlayerRenderer extends GeoReplacedEntityRenderer<Player, KaizokuPlayerAnimatable> {

    private static KaizokuPlayerRenderer INSTANCE_RENDERER;

    public KaizokuPlayerRenderer(EntityRendererProvider.Context context) {
        super(context, new KaizokuPlayerModel(), KaizokuPlayerAnimatable.INSTANCE);
        INSTANCE_RENDERER = this;
    }

    // -----------------------------------------------------------------------
    // RenderPlayerEvent — hooked on NeoForge.EVENT_BUS (game bus)
    // -----------------------------------------------------------------------

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (INSTANCE_RENDERER == null) return;

        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

        // Set skin for this player
        KaizokuPlayerModel.CURRENT_SKIN.set(player.getSkin().texture());

        // Check for a queued animation and trigger it
        String queued = KaizokuPlayerAnimatable.QUEUED_ANIMS.remove(player.getId());
        if (queued != null && !queued.isEmpty()) {
            System.out.println("[KaizokuRenderer] Triggering '" + queued + "' for " + player.getId());
            KaizokuPlayerAnimatable.INSTANCE
                    .getAnimatableInstanceCache()
                    .getManagerForId((long) player.getId())
                    .tryTriggerAnimation("skill_controller", queued);
        }

        // Render GeckoLib model using super implementation
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        int packedLight = event.getPackedLight();

        poseStack.pushPose();
        // Align the GeckoLib model to match vanilla player positioning
        poseStack.translate(0, 0, 0);
        INSTANCE_RENDERER.render(player, event.getEntity().getYRot(),
                event.getPartialTick(), poseStack, bufferSource, packedLight);
        poseStack.popPose();

        KaizokuPlayerModel.CURRENT_SKIN.remove();

        // Cancel vanilla render so models don't overlap
        event.setCanceled(true);
    }

    @Override
    public ResourceLocation getTextureLocation(KaizokuPlayerAnimatable animatable) {
        ResourceLocation skin = KaizokuPlayerModel.CURRENT_SKIN.get();
        return skin != null ? skin : ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");
    }
}
