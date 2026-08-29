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

    private static final long DASH_UNLOCK_LEVEL =
            10L;

    private SkillManager() {
    }

    public static void useSkill(
            ServerPlayer player,
            String skillId
    ) {

        if (
                skillId == null
                        || skillId.isBlank()
        ) {
            return;
        }

        /*
         * Dash artık normal skill değil.
         * Fakat özel hareket olarak buradan çalışıyor.
         */
        if (skillId.equals("dash")) {

            useDash(
                    player
            );

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

        boolean success;

        switch (skillId) {

            case "punch" -> {
                punch(
                        player,
                        1.0D
                );
                success = true;
            }

            case "heavy_punch" -> {
                punch(
                        player,
                        1.8D
                );
                success = true;
            }

            case "shockwave" -> {
                shockwave(
                        player
                );
                success = true;
            }

            case "uppercut" -> {
                uppercut(
                        player
                );
                success = true;
            }

            case "guard" -> {
                guard(
                        player
                );
                success = true;
            }

            default -> {
                return;
            }
        }

        if (success) {

            setCooldown(
                    player,
                    skillId,
                    now + cooldown
            );
        }
    }

    public static void useDash(
            ServerPlayer player
    ) {

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

        /*
         * Level 10 olmadan Dash kullanılamaz.
         */
        if (
                data.getLevel()
                        < DASH_UNLOCK_LEVEL
        ) {
            return;
        }

        long now =
                System.currentTimeMillis();

        if (
                !isReady(
                        player,
                        "dash",
                        now
                )
        ) {
            return;
        }

        Vec3 look =
                player.getLookAngle()
                        .normalize();

        /*
         * Önce mevcut yatay hareketi alıyoruz.
         */
        Vec3 movement =
                look.scale(
                        1.25D
                );

        player.setDeltaMovement(
                movement.x,
                Math.max(
                        0.0D,
                        player.getDeltaMovement().y
                ),
                movement.z
        );

        player.hurtMarked = true;

        setCooldown(
                player,
                "dash",
                now + getCooldown("dash")
        );
    }

    public static long getCooldown(
            String skillId
    ) {

        return switch (skillId) {

            case "punch" ->
                    350L;

            case "heavy_punch" ->
                    1200L;

            case "shockwave" ->
                    1800L;

            case "uppercut" ->
                    1300L;

            case "guard" ->
                    3000L;

            case "dash" ->
                    1000L;

            default ->
                    1000L;
        };
    }

    public static long getDashUnlockLevel() {
        return DASH_UNLOCK_LEVEL;
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
                        3.5D
                );

        if (
                target == null
        ) {
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

        /*
         * Önceden multiplier kadar knockback
         * veriyorduk.
         *
         * Şimdi çok daha kontrollü.
         */
        Vec3 direction =
                target.position()
                        .subtract(
                                player.position()
                        );

        if (
                direction.lengthSqr()
                        > 0.0001D
        ) {

            direction =
                    direction.normalize();

            double knockback =
                    multiplier == 1.0D
                            ? 0.18D
                            : 0.28D;

            target.push(
                    direction.x * knockback,
                    0.08D,
                    direction.z * knockback
            );
        }
    }

    private static void shockwave(
            ServerPlayer player
    ) {

        double range =
                4.0D;

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
                            );

            if (
                    direction.lengthSqr()
                            > 0.0001D
            ) {

                direction =
                        direction.normalize();

                /*
                 * Shockwave güçlü ama
                 * artık kilometrelerce fırlatmaz.
                 */
                target.push(
                        direction.x * 0.35D,
                        0.16D,
                        direction.z * 0.35D
                );
            }
        }
    }

    private static void uppercut(
            ServerPlayer player
    ) {

        LivingEntity target =
                findTarget(
                        player,
                        3.0D
                );

        if (
                target == null
        ) {
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

        /*
         * Daha kontrollü yukarı savurma.
         */
        target.setDeltaMovement(
                target.getDeltaMovement().x,
                0.45D,
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
                                0.5D
                        );

        LivingEntity closest =
                null;

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
                    entity.getBoundingBox();

            if (
                    entityBox.contains(
                            start
                    )
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
}