package net.mcreator.kaizokuocraft.client.render;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * GeckoLib model that loads the player's custom geo.json (with lower_arm / lower_leg bones)
 * and the matching animation.json.
 *
 * Texture is driven by a ThreadLocal set by KaizokuPlayerRenderer right before
 * each render call, so every player shows their own skin.
 */
public class KaizokuPlayerModel extends GeoModel<KaizokuPlayerAnimatable> {

    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath("kaizoku_o_craft", "geo/entity/player.geo.json");

    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath("kaizoku_o_craft", "animations/entity/player.animation.json");

    /** Fallback texture shown when no skin is available (shouldn't happen in normal play). */
    private static final ResourceLocation FALLBACK =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png");

    /**
     * The renderer sets this before calling super.render() so the model knows which
     * player skin to use, then clears it afterwards.
     */
    public static final ThreadLocal<ResourceLocation> CURRENT_SKIN = new ThreadLocal<>();

    @Override
    public ResourceLocation getModelResource(KaizokuPlayerAnimatable animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(KaizokuPlayerAnimatable animatable) {
        ResourceLocation skin = CURRENT_SKIN.get();
        return skin != null ? skin : FALLBACK;
    }

    @Override
    public ResourceLocation getAnimationResource(KaizokuPlayerAnimatable animatable) {
        return ANIMATIONS;
    }
}
