package net.mcreator.kaizokuocraft.client.render;

import net.minecraft.world.entity.EntityType;
import software.bernie.geckolib.animatable.GeoReplacedEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton GeckoLib animatable that acts as the replaced entity for all players.
 * Per-player animation state is tracked via the AnimatableInstanceCache using entity IDs.
 */
public class KaizokuPlayerAnimatable implements GeoReplacedEntity {

    public static final KaizokuPlayerAnimatable INSTANCE = new KaizokuPlayerAnimatable();

    /** Maps entity ID -> short animation name to trigger next frame. */
    public static final Map<Integer, String> QUEUED_ANIMS = new ConcurrentHashMap<>();

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private KaizokuPlayerAnimatable() {}

    @Override
    public EntityType<?> getReplacingEntityType() {
        return EntityType.PLAYER;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        AnimationController<KaizokuPlayerAnimatable> controller =
                new AnimationController<>(this, "skill_controller", 2, state -> PlayState.STOP);

        // Register skill animations as triggerable (one-shot)
        controller.triggerableAnim("punch",
                RawAnimation.begin().then("animation.model.punch", Animation.LoopType.PLAY_ONCE));

        // Add more when animation.json has them:
        // controller.triggerableAnim("kick",       RawAnimation.begin().then("animation.model.kick",       Animation.LoopType.PLAY_ONCE));
        // controller.triggerableAnim("sword_slash", RawAnimation.begin().then("animation.model.sword_slash", Animation.LoopType.PLAY_ONCE));
        // controller.triggerableAnim("guard",      RawAnimation.begin().then("animation.model.guard",      Animation.LoopType.HOLD_ON_LAST_FRAME));

        registrar.add(controller);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}

