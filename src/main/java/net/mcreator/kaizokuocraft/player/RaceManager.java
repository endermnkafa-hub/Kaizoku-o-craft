package net.mcreator.kaizokuocraft.player;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class RaceManager {

    private static final ResourceLocation DAMAGE_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "kaizoku_o_craft",
                    "race_damage"
            );

    private static final ResourceLocation SPEED_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    "kaizoku_o_craft",
                    "race_speed"
            );

    private RaceManager() {
    }

    public static void applyRace(ServerPlayer player) {
        PlayerData data = PlayerDataManager.get(player);
        RaceType race = data.getRace();

        removeRaceModifiers(player);

        if (race == RaceType.HUMAN) {
            return;
        }

        AttributeInstance damage =
                player.getAttribute(Attributes.ATTACK_DAMAGE);

        if (damage != null) {
            double damageBonus =
                    race.getDamageMultiplier() - 1.0D;

            if (damageBonus != 0.0D) {
                damage.addOrUpdateTransientModifier(
                        new AttributeModifier(
                                DAMAGE_MODIFIER_ID,
                                damageBonus,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                );
            }
        }

        AttributeInstance speed =
                player.getAttribute(Attributes.MOVEMENT_SPEED);

        if (speed != null) {
            double speedBonus =
                    race.getSpeedMultiplier() - 1.0D;

            if (speedBonus != 0.0D) {
                speed.addOrUpdateTransientModifier(
                        new AttributeModifier(
                                SPEED_MODIFIER_ID,
                                speedBonus,
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                );
            }
        }
    }

    public static void removeRaceModifiers(ServerPlayer player) {
        AttributeInstance damage =
                player.getAttribute(Attributes.ATTACK_DAMAGE);

        if (damage != null) {
            damage.removeModifier(DAMAGE_MODIFIER_ID);
        }

        AttributeInstance speed =
                player.getAttribute(Attributes.MOVEMENT_SPEED);

        if (speed != null) {
            speed.removeModifier(SPEED_MODIFIER_ID);
        }
    }
}