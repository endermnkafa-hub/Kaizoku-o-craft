package net.mcreator.kaizokuocraft.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SkillManager {

    private static final Map<
            UUID,
            Map<String, Long>
            > COOLDOWNS =
            new HashMap<>();

    private SkillManager() {
    }

    public static void useSkill(
            ServerPlayer player,
            String skillId
    ) {

        if (skillId == null) {
            return;
        }

        long now =
                System.currentTimeMillis();

        long cooldown =
                getCooldown(
                        skillId
                );

        if (
                !isReady(
                        player,
                        skillId,
                        now
                )
        ) {
            return;
        }

        switch (skillId) {

            case "punch" ->
                    punch(
                            player,
                            1.0D
                    );

            case "heavy_punch" ->
                    punch(
                            player,
                            1.8D
                    );

            case "dash" ->
                    dash(player);

            case "shockwave" ->
                    shockwave(player);

            case "uppercut" ->
                    uppercut(player);

            case "guard" ->
                    guard(player);

            default ->
                    return;
        }

        setCooldown(
                player,
                skillId,
                now + cooldown
        );
    }

    private static long getCooldown(
            String skillId
    ) {

        return switch (skillId) {

            case "punch" ->
                    350L;

            case "heavy_punch" ->
                    1200L;

            case "dash" ->
                    1000L;

            case "shockwave" ->
                    1800L;

            case "uppercut" ->
                    1300L;

            case "guard" ->
                    3000L;

            default ->
                    1000L;
        };
    }

    private static boolean isReady(
            ServerPlayer player,
            String skillId,
            long now
    ) {

        Map<String, Long> playerCooldowns =
                COOLDOWNS.get(
                        player.getUUID()
                );

        if (
                playerCooldowns == null
        ) {
            return true;
        }

        Long end =
                playerCooldowns.get(
                        skillId
                );

        return end == null
                || now >= end;
    }

    private static void setCooldown(
            ServerPlayer player,
            String skillId,
            long end
    ) {

        COOLDOWNS
                .computeIfAbsent(
                        player.getUUID(),
                        ignored ->
                                new HashMap<>()
                )
                .put(
                        skillId,
                        end
                );
    }

    private static void punch(
            ServerPlayer player,
            double multiplier
    ) {

        LivingEntity target =
                findTarget(
                        player,
                        4.0D
                );

        if (target == null) {
            return;
        }

        float baseDamage =
                player.getAttackStrengthScale(
                        0.0F
                ) * 5.0F;

        if (
                baseDamage < 1.0F
        ) {
            baseDamage = 1.0F;
        }

        double damage =
                baseDamage
                        * PowerManager.getDamageMultiplier(
                        player
                )
                        * multiplier;

        target.hurt(
                player.damageSources().playerAttack(
                        player
                ),
                (float) damage
        );

        Vec3 direction =
                target.position()
                        .subtract(
                                player.position()
                        )
                        .normalize();

        target.push(
                direction.x * multiplier,
                0.15D,
                direction.z * multiplier
        );
    }

    private static void dash(
            ServerPlayer player
    ) {

        Vec3 look =
                player.getLookAngle()
                        .normalize();

        Vec3 movement =
                look.scale(
                        1.6D
                );

        player.setDeltaMovement(
                movement.x,
                0.25D,
                movement.z
        );

        player.hurtMarked = true;
    }

    private static void shockwave(
            ServerPlayer player
    ) {

        double range = 4.0D;

        AABB box =
                player.getBoundingBox()
                        .inflate(
                                range
                        );

        for (
                LivingEntity target :
                player.level()
                        .getEntitiesOfClass(
                                LivingEntity.class,
                                box,
                                entity ->
                                        entity != player
                        )
        ) {

            double damage =
                    4.0D
                            * PowerManager.getDamageMultiplier(
                            player
                    );

            target.hurt(
                    player.damageSources().playerAttack(
                            player
                    ),
                    (float) damage
            );

            Vec3 direction =
                    target.position()
                            .subtract(
                                    player.position()
                            )
                            .normalize();

            target.push(
                    direction.x * 1.4D,
                    0.45D,
                    direction.z * 1.4D
            );
        }
    }

    private static void uppercut(
            ServerPlayer player
    ) {

        LivingEntity target =
                findTarget(
                        player,
                        3.5D
                );

        if (target == null) {
            return;
        }

        double damage =
                6.0D
                        * PowerManager.getDamageMultiplier(
                        player
                );

        target.hurt(
                player.damageSources().playerAttack(
                        player
                ),
                (float) damage
        );

        target.setDeltaMovement(
                target.getDeltaMovement().x,
                1.0D,
                target.getDeltaMovement().z
        );

        target.hurtMarked = true;
    }

    private static void guard(
            ServerPlayer player
    ) {

        player.addEffect(
                new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        40,
                        1,
                        false,
                        false,
                        true
                )
        );
    }

    private static LivingEntity findTarget(
            ServerPlayer player,
            double range
    ) {

        Vec3 start =
                player.getEyePosition();

        Vec3 direction =
                player.getLookAngle()
                        .normalize();

        Vec3 end =
                start.add(
                        direction.scale(
                                range
                        )
                );

        AABB searchBox =
                player.getBoundingBox()
                        .expandTowards(
                                direction.scale(
                                        range
                                )
                        )
                        .inflate(
                                1.0D
                        );

        LivingEntity closest = null;

        double closestDistance =
                Double.MAX_VALUE;

        for (
                LivingEntity entity :
                player.level()
                        .getEntitiesOfClass(
                                LivingEntity.class,
                                searchBox,
                                candidate ->
                                        candidate != player
                        )
        ) {

            AABB entityBox =
                    candidateBoundingBox(
                            entity
                    );

            if (
                    entityBox.contains(start)
                    || entityBox.clip(
                            start,
                            end
                    ).isPresent()
            ) {

                double distance =
                        player.distanceToSqr(
                                entity
                        );

                if (
                        distance
                                < closestDistance
                ) {

                    closestDistance =
                            distance;

                    closest =
                            entity;
                }
            }
        }

        return closest;
    }

    private static AABB candidateBoundingBox(
            LivingEntity entity
    ) {

        return entity.getBoundingBox();
    }
}