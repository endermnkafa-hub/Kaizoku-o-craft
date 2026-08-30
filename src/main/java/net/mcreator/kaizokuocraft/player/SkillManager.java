package net.mcreator.kaizokuocraft.player;

import net.mcreator.kaizokuocraft.KaizokuOCraftMod;
import net.mcreator.kaizokuocraft.client.SkillRegistry;
import net.mcreator.kaizokuocraft.client.SkillDefinition;
import net.mcreator.kaizokuocraft.init.ModSounds;
import net.mcreator.kaizokuocraft.network.PlayPlayerAnimationMessage;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
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

        if (!PlayerDataManager.get(player).isCharacterCreated()) {
            return;
        }

        /*
         * Dash is a special non-slot unlock skill.
         */
        if (
                skillId.equals("dash")
        ) {
            useDash(player);
            return;
        }

        SkillDefinition skill = SkillRegistry.getSkill(skillId);
        if (skill == null) {
            return;
        }

        PlayerData data = PlayerDataManager.get(player);

        // Verify active combat style matches skill style
        if (skill.style() != null && !data.getCombatStyle().equalsIgnoreCase(skill.style().name())) {
            return;
        }

        // Verify mastery requirement
        double playerMastery = 0.0D;
        if (skill.style() == FightingStyle.FIST) playerMastery = data.getFightingMastery();
        else if (skill.style() == FightingStyle.SWORD) playerMastery = data.getSwordMastery();
        else if (skill.style() == FightingStyle.KICK) playerMastery = data.getKickMastery();
        else if (skill.style() == FightingStyle.SNIPER) playerMastery = data.getSniperMastery();

        if (playerMastery < skill.requiredMastery()) {
            return;
        }

        long now =
                System.currentTimeMillis();

        long cooldown = skill.cooldownMillis();

        if (
                !isReady(
                        player,
                        skillId,
                        now
                )
        ) {
            return;
        }

        double staminaCost = skill.staminaCost();

        if (
                !StaminaManager.consume(
                        player,
                        staminaCost
                )
        ) {
            return;
        }

        switch (skillId) {
            case "punch" -> {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new PlayPlayerAnimationMessage(player.getId(), "kaizoku_o_craft:animation.model.punch", true, true));
                KaizokuOCraftMod.queueServerWork(6, () -> punch(player, 1.0D));
            }
            case "heavy_punch" -> {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new PlayPlayerAnimationMessage(player.getId(), "kaizoku_o_craft:animation.model.heavypunch", true, true));
                KaizokuOCraftMod.queueServerWork(10, () -> punch(player, 1.8D));
            }
            case "shockwave" -> {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new PlayPlayerAnimationMessage(player.getId(), "kaizoku_o_craft:animation.model.shockwave", true, true));
                KaizokuOCraftMod.queueServerWork(11, () -> shockwave(player));
            }
            case "uppercut" -> {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new PlayPlayerAnimationMessage(player.getId(), "kaizoku_o_craft:animation.model.uppercut", true, true));
                KaizokuOCraftMod.queueServerWork(9, () -> uppercut(player));
            }
            case "downslam" -> {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new PlayPlayerAnimationMessage(player.getId(), "kaizoku_o_craft:animation.model.downslam", true, true));
                KaizokuOCraftMod.queueServerWork(12, () -> downslam(player));
            }
            case "double_strike" -> {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new PlayPlayerAnimationMessage(player.getId(), "kaizoku_o_craft:animation.model.doublestrike", true, true));
                KaizokuOCraftMod.queueServerWork(4, () -> doubleStrikeHit(player, 1));
                KaizokuOCraftMod.queueServerWork(8, () -> doubleStrikeHit(player, 2));
            }
            case "front_kick" -> {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new PlayPlayerAnimationMessage(player.getId(), "kaizoku_o_craft:animation.model.frontkick", true, true));
                KaizokuOCraftMod.queueServerWork(8, () -> frontKick(player));
            }

            // SWORD
            case "sword_slash" -> swordSlash(player, 1.2D);
            case "oni_giri" -> swordSlash(player, 2.2D);
            case "shishi_sonson" -> swordSlash(player, 3.5D);

            // KICK
            case "collier_kick" -> kick(player, 1.2D);
            case "concasse" -> kick(player, 2.4D);
            case "diable_jambe" -> diableJambe(player);

            // SNIPER
            case "firebird_star" -> sniperShoot(player, 1.5D, true);
            case "gunpowder_star" -> sniperShoot(player, 2.0D, false);

            default -> {
                return;
            }
        }

        setCooldown(
                player,
                skillId,
                now + cooldown
        );
    }

    private static void swordSlash(ServerPlayer player, double multiplier) {
        player.swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);
        LivingEntity target = findTarget(player, 4.0D);
        if (target == null) {
            // Whoosh miss sound
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.2F);
            return;
        }
        double damage = 6.0D * PowerManager.getDamageMultiplier(player) * multiplier;
        target.hurt(player.damageSources().playerAttack(player), (float) damage);
        target.knockback(0.3F, player.getX() - target.getX(), player.getZ() - target.getZ());

        // Sword hit sound and sweep particle
        if (multiplier > 3.0D) {
            // Shishi Sonson (high dmg) - slow sharp slash effect
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.5F);
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.2F, 0.7F);
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FLASH, target.getX(), target.getY() + 1.0D, target.getZ(), 2, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1.0D, target.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.2D);
            }
        } else if (multiplier > 2.0D) {
            // Oni Giri
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, 0.9F);
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 0.5F, 1.5F);
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY() + 1.0D, target.getZ(), 3, 0.3D, 0.3D, 0.3D, 0.0D);
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, target.getX(), target.getY() + 1.0D, target.getZ(), 10, 0.2D, 0.2D, 0.2D, 0.1D);
            }
        } else {
            // Normal sword slash
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY() + 1.0D, target.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private static void kick(ServerPlayer player, double multiplier) {
        player.swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);
        LivingEntity target = findTarget(player, 3.5D);
        if (target == null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.PLAYERS, 1.0F, 1.2F);
            return;
        }
        double damage = 5.0D * PowerManager.getDamageMultiplier(player) * multiplier;
        target.hurt(player.damageSources().playerAttack(player), (float) damage);
        target.knockback(0.5F, player.getX() - target.getX(), player.getZ() - target.getZ());

        // Kick hit sound & dust particles
        float pitch = multiplier > 2.0D ? 0.8F : 1.1F;
        player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.0F, pitch);
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD, target.getX(), target.getY() + 0.5D, target.getZ(), multiplier > 2.0D ? 12 : 5, 0.2D, 0.2D, 0.2D, 0.1D);
            if (multiplier > 2.0D) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY() + 1.0D, target.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private static void diableJambe(ServerPlayer player) {
        LivingEntity target = findTarget(player, 3.5D);
        if (target == null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 0.6F);
            return;
        }
        double damage = 8.0D * PowerManager.getDamageMultiplier(player);
        target.hurt(player.damageSources().playerAttack(player), (float) damage);
        target.setRemainingFireTicks(80); // 4 seconds of fire
        target.knockback(0.4F, player.getX() - target.getX(), player.getZ() - target.getZ());

        // Diable Jambe fiery blast sound and particles
        player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.2F, 1.0F);
        player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.8F, 1.2F);
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, target.getX(), target.getY() + 0.5D, target.getZ(), 20, 0.3D, 0.5D, 0.3D, 0.15D);
            serverLevel.sendParticles(ParticleTypes.LAVA, target.getX(), target.getY() + 1.0D, target.getZ(), 6, 0.2D, 0.2D, 0.2D, 0.0D);
        }
    }

    private static void sniperShoot(ServerPlayer player, double multiplier, boolean firebird) {
        LivingEntity target = findTarget(player, 16.0D);
        if (target == null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.5F);
            return;
        }
        double damage = 4.0D * PowerManager.getDamageMultiplier(player) * multiplier;
        target.hurt(player.damageSources().playerAttack(player), (float) damage);
        if (firebird) {
            target.setRemainingFireTicks(60);
        }
        target.knockback(0.2F, player.getX() - target.getX(), player.getZ() - target.getZ());

        // Sniper sound and particle trail
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIREWORK_ROCKET_SHOOT, SoundSource.PLAYERS, 1.0F, 1.2F);
        player.level().playSound(null, target.getX(), target.getY(), target.getZ(), firebird ? SoundEvents.DRAGON_FIREBALL_EXPLODE : SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.0F, 1.0F);
        
        if (player.level() instanceof ServerLevel serverLevel) {
            // Draw particle trail from player eyes to target body
            Vec3 start = player.getEyePosition();
            Vec3 end = target.position().add(0.0D, target.getBbHeight() / 2.0D, 0.0D);
            Vec3 diff = end.subtract(start);
            int steps = 15;
            for (int i = 0; i <= steps; i++) {
                Vec3 point = start.add(diff.scale((double) i / steps));
                serverLevel.sendParticles(firebird ? ParticleTypes.FLAME : ParticleTypes.CRIT, point.x, point.y, point.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            // Impact particles
            serverLevel.sendParticles(firebird ? ParticleTypes.FLAME : ParticleTypes.EXPLOSION, target.getX(), target.getY() + 1.0D, target.getZ(), 8, 0.2D, 0.2D, 0.2D, 0.1D);
        }
    }

    public static double getSkillStaminaCost(
            String skillId
    ) {
        if (skillId.equals("dash")) {
            return 20.0D;
        }
        SkillDefinition skill = SkillRegistry.getSkill(skillId);
        return skill != null ? skill.staminaCost() : 0.0D;
    }

    public static long getCooldown(
            String skillId
    ) {
        if (skillId.equals("dash")) {
            return 1000L;
        }
        SkillDefinition skill = SkillRegistry.getSkill(skillId);
        return skill != null ? skill.cooldownMillis() : 1000L;
    }

    public static long getDashUnlockLevel() {
        return DASH_UNLOCK_LEVEL;
    }

    private static boolean isReady(
            ServerPlayer player,
            String skillId,
            long now
    ) {

        Map<String, Long>
                playerCooldowns =
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

    private static void useDash(
            ServerPlayer player
    ) {

        PlayerData data =
                PlayerDataManager.get(
                        player
                );

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

        if (
                !StaminaManager.consume(
                        player,
                        getSkillStaminaCost("dash")
                )
        ) {
            return;
        }

        Vec3 look =
                player.getLookAngle()
                        .normalize();

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

        // Play sound and spawn dust particles
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 1.0F, 1.5F);
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.5D, player.getZ(), 8, 0.2D, 0.2D, 0.2D, 0.05D);
        }

        setCooldown(
                player,
                "dash",
                now + getCooldown("dash")
        );
    }

    private static void spawnHitSmokeRing(ServerLevel serverLevel, Vec3 center, int count, double radius, double speed) {
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI * i) / count;
            double dx = Math.cos(angle);
            double dz = Math.sin(angle);
            serverLevel.sendParticles(ParticleTypes.CLOUD, center.x + dx * radius, center.y, center.z + dz * radius, 1, dx * speed, 0.02D, dz * speed, 0.05D);
            serverLevel.sendParticles(ParticleTypes.POOF, center.x + dx * radius, center.y, center.z + dz * radius, 1, dx * (speed * 0.5D), 0.01D, dz * (speed * 0.5D), 0.02D);
        }
    }

    private static void punch(
            ServerPlayer player,
            double multiplier
    ) {
        player.swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);

        LivingEntity target =
                findTarget(
                        player,
                        3.5D
                );

        if (
                target == null
        ) {
            // Miss sound
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.NEUTRAL, 1.0F, 1.0F);
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
                player.damageSources()
                        .playerAttack(
                                player
                        ),
                (float) damage
        );

        // Custom hit sound: punch1 for normal punch, punch2 for heavy punch
        if (multiplier == 1.0D) {
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.PUNCH1.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        } else {
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.PUNCH2.get(), SoundSource.NEUTRAL, 1.1F, 1.0F);
        }
        
        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 hitPos = target.position().add(0, target.getBbHeight() * 0.55D, 0);
            // Dairesel duman halkası efekti
            spawnHitSmokeRing(serverLevel, hitPos, multiplier == 1.0D ? 8 : 12, 0.2D, multiplier == 1.0D ? 0.15D : 0.25D);
            serverLevel.sendParticles(ParticleTypes.CRIT, hitPos.x, hitPos.y, hitPos.z, multiplier == 1.0D ? 4 : 8, 0.1D, 0.1D, 0.1D, 0.1D);
        }

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
                            ? 0.2D
                            : 0.38D;

            target.push(
                    direction.x * knockback,
                    0.09D,
                    direction.z * knockback
            );
            target.hurtMarked = true;
        }
    }

    private static void shockwave(
            ServerPlayer player
    ) {

        double range =
                4.5D;

        AABB box =
                player.getBoundingBox()
                        .inflate(
                                range
                        );

        // Shockwave custom sound: downslam
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.DOWNSLAM.get(), SoundSource.NEUTRAL, 1.1F, 1.1F);
        
        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 center = player.position().add(0, 0.15D, 0);
            spawnHitSmokeRing(serverLevel, center, 18, 0.5D, 0.35D);
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 4) {
                double dx = Math.cos(angle) * 2.0D;
                double dz = Math.sin(angle) * 2.0D;
                serverLevel.sendParticles(ParticleTypes.CLOUD, player.getX() + dx, player.getY() + 0.1D, player.getZ() + dz, 2, 0.1D, 0.05D, 0.1D, 0.08D);
            }
        }

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
                    4.5D
                            * PowerManager.getDamageMultiplier(
                                    player
                            );

            target.hurt(
                    player.damageSources()
                            .playerAttack(
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

                target.push(
                        direction.x * 0.4D,
                        0.18D,
                        direction.z * 0.4D
                );
                target.hurtMarked = true;
            }
        }
    }

    private static void uppercut(
            ServerPlayer player
    ) {

        LivingEntity target =
                findTarget(
                        player,
                        3.2D
                );

        if (
                target == null
        ) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.NEUTRAL, 1.0F, 1.2F);
            return;
        }

        double damage =
                6.5D
                        * PowerManager.getDamageMultiplier(
                                player
                        );

        target.hurt(
                player.damageSources()
                        .playerAttack(
                                player
                        ),
                (float) damage
        );

        // Custom uppercut sound
        player.level().playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.UPPERCUT.get(), SoundSource.NEUTRAL, 1.1F, 1.0F);
        
        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 hitPos = target.position().add(0, target.getBbHeight() * 0.5D, 0);
            spawnHitSmokeRing(serverLevel, hitPos, 10, 0.2D, 0.18D);
            serverLevel.sendParticles(ParticleTypes.CLOUD, target.getX(), target.getY() + 0.5D, target.getZ(), 8, 0.1D, 0.45D, 0.1D, 0.08D);
        }

        target.setDeltaMovement(
                target.getDeltaMovement().x,
                0.52D,
                target.getDeltaMovement().z
        );

        target.hurtMarked = true;
    }

    private static void downslam(ServerPlayer player) {
        player.swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);
        double range = 4.2D;
        AABB box = player.getBoundingBox().inflate(range);
        
        // Custom downslam sound
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.DOWNSLAM.get(), SoundSource.NEUTRAL, 1.2F, 0.95F);
        
        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 center = player.position().add(0, 0.15D, 0);
            spawnHitSmokeRing(serverLevel, center, 20, 0.4D, 0.38D);
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 6) {
                double dx = Math.cos(angle) * 2.2D;
                double dz = Math.sin(angle) * 2.2D;
                serverLevel.sendParticles(ParticleTypes.CLOUD, player.getX() + dx, player.getY() + 0.1D, player.getZ() + dz, 3, 0.2D, 0.08D, 0.2D, 0.1D);
            }
        }

        for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, box, entity -> entity != player)) {
            double damage = 8.5D * PowerManager.getDamageMultiplier(player);
            target.hurt(player.damageSources().playerAttack(player), (float) damage);
            
            Vec3 direction = target.position().subtract(player.position());
            if (direction.lengthSqr() > 0.0001D) {
                direction = direction.normalize();
                target.setDeltaMovement(direction.x * 0.25D, -0.45D, direction.z * 0.25D);
                target.hurtMarked = true;
            }
        }
    }

    private static void doubleStrikeHit(ServerPlayer player, int strikeNumber) {
        player.swing(strikeNumber == 1 ? net.minecraft.world.InteractionHand.OFF_HAND : net.minecraft.world.InteractionHand.MAIN_HAND, true);
        
        LivingEntity target = findTarget(player, 3.5D);
        if (target == null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.NEUTRAL, 1.0F, strikeNumber == 1 ? 1.2F : 1.0F);
            return;
        }

        double multiplier = strikeNumber == 1 ? 3.5D : 4.5D;
        double damage = multiplier * PowerManager.getDamageMultiplier(player);
        target.hurt(player.damageSources().playerAttack(player), (float) damage);
        
        if (strikeNumber == 1) {
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.PUNCH1.get(), SoundSource.NEUTRAL, 1.0F, 1.05F);
        } else {
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.PUNCH2.get(), SoundSource.NEUTRAL, 1.05F, 1.0F);
        }
        
        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 hitPos = target.position().add(0, target.getBbHeight() * 0.55D, 0);
            spawnHitSmokeRing(serverLevel, hitPos, strikeNumber == 1 ? 8 : 12, 0.2D, strikeNumber == 1 ? 0.16D : 0.24D);
            serverLevel.sendParticles(ParticleTypes.CRIT, hitPos.x, hitPos.y, hitPos.z, 4, 0.1D, 0.1D, 0.1D, 0.1D);
        }

        Vec3 direction = target.position().subtract(player.position());
        if (direction.lengthSqr() > 0.0001D) {
            direction = direction.normalize();
            target.push(direction.x * (strikeNumber == 1 ? 0.15D : 0.3D), 0.1D, direction.z * (strikeNumber == 1 ? 0.15D : 0.3D));
            target.hurtMarked = true;
        }
    }

    private static void frontKick(ServerPlayer player) {
        player.swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);
        LivingEntity target = findTarget(player, 3.8D);
        if (target == null) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.NEUTRAL, 1.0F, 1.0F);
            return;
        }

        double damage = 7.5D * PowerManager.getDamageMultiplier(player);
        target.hurt(player.damageSources().playerAttack(player), (float) damage);
        
        // Custom front kick sound: punch2
        player.level().playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.PUNCH2.get(), SoundSource.NEUTRAL, 1.15F, 0.9F);
        
        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 hitPos = target.position().add(0, target.getBbHeight() * 0.5D, 0);
            spawnHitSmokeRing(serverLevel, hitPos, 14, 0.25D, 0.28D);
            serverLevel.sendParticles(ParticleTypes.CLOUD, hitPos.x, hitPos.y, hitPos.z, 8, 0.2D, 0.2D, 0.2D, 0.1D);
        }

        Vec3 direction = target.position().subtract(player.position());
        if (direction.lengthSqr() > 0.0001D) {
            direction = direction.normalize();
            // High horizontal knockback
            target.push(direction.x * 0.72D, 0.14D, direction.z * 0.72D);
            target.hurtMarked = true;
        }
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