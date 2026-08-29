package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.player.FightingStyle;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SkillRegistry {

    private static final List<SkillDefinition> SKILLS =
            new ArrayList<>();

    static {

        // FIST STYLES
        register(
                new SkillDefinition(
                        "punch",
                        "Punch",
                        new ItemStack(Items.LEATHER),
                        350L,
                        FightingStyle.FIST,
                        0.0D,
                        5.0D
                )
        );

        register(
                new SkillDefinition(
                        "heavy_punch",
                        "Heavy Punch",
                        new ItemStack(Items.IRON_INGOT),
                        1200L,
                        FightingStyle.FIST,
                        15.0D,
                        15.0D
                )
        );

        register(
                new SkillDefinition(
                        "shockwave",
                        "Shockwave",
                        new ItemStack(Items.COBBLESTONE),
                        1800L,
                        FightingStyle.FIST,
                        30.0D,
                        35.0D
                )
        );

        register(
                new SkillDefinition(
                        "uppercut",
                        "Uppercut",
                        new ItemStack(Items.GOLD_INGOT),
                        1300L,
                        FightingStyle.FIST,
                        10.0D,
                        10.0D
                )
        );

        register(
                new SkillDefinition(
                        "guard",
                        "Guard",
                        new ItemStack(Items.SHIELD),
                        3000L,
                        FightingStyle.FIST,
                        5.0D,
                        15.0D
                )
        );

        // SWORD STYLES
        register(
                new SkillDefinition(
                        "sword_slash",
                        "1-Sword Slash",
                        new ItemStack(Items.IRON_SWORD),
                        1000L,
                        FightingStyle.SWORD,
                        0.0D,
                        8.0D
                )
        );

        register(
                new SkillDefinition(
                        "oni_giri",
                        "Oni Giri",
                        new ItemStack(Items.DIAMOND_SWORD),
                        2500L,
                        FightingStyle.SWORD,
                        25.0D,
                        20.0D
                )
        );

        register(
                new SkillDefinition(
                        "shishi_sonson",
                        "Shishi Sonson",
                        new ItemStack(Items.NETHERITE_SWORD),
                        4000L,
                        FightingStyle.SWORD,
                        50.0D,
                        40.0D
                )
        );

        // KICK STYLES
        register(
                new SkillDefinition(
                        "collier_kick",
                        "Collier Kick",
                        new ItemStack(Items.LEATHER_BOOTS),
                        1200L,
                        FightingStyle.KICK,
                        0.0D,
                        8.0D
                )
        );

        register(
                new SkillDefinition(
                        "concasse",
                        "Concassé",
                        new ItemStack(Items.IRON_BOOTS),
                        2800L,
                        FightingStyle.KICK,
                        25.0D,
                        22.0D
                )
        );

        register(
                new SkillDefinition(
                        "diable_jambe",
                        "Diable Jambe",
                        new ItemStack(Items.BLAZE_POWDER),
                        6000L,
                        FightingStyle.KICK,
                        60.0D,
                        50.0D
                )
        );

        // SNIPER STYLES
        register(
                new SkillDefinition(
                        "firebird_star",
                        "Firebird Star",
                        new ItemStack(Items.FIRE_CHARGE),
                        1500L,
                        FightingStyle.SNIPER,
                        0.0D,
                        12.0D
                )
        );

        register(
                new SkillDefinition(
                        "gunpowder_star",
                        "Gunpowder Star",
                        new ItemStack(Items.GUNPOWDER),
                        2000L,
                        FightingStyle.SNIPER,
                        20.0D,
                        18.0D
                )
        );
    }

    private SkillRegistry() {
    }

    public static void register(
            SkillDefinition skill
    ) {
        SKILLS.add(skill);
    }

    public static List<SkillDefinition> getSkills() {
        return Collections.unmodifiableList(
                SKILLS
        );
    }

    public static SkillDefinition getSkill(
            int index
    ) {

        if (
                index < 0
                        || index >= SKILLS.size()
        ) {
            return null;
        }

        return SKILLS.get(index);
    }

    public static SkillDefinition getSkill(
            String id
    ) {

        for (
                SkillDefinition skill :
                SKILLS
        ) {

            if (
                    skill.id().equals(id)
            ) {
                return skill;
            }
        }

        return null;
    }
}