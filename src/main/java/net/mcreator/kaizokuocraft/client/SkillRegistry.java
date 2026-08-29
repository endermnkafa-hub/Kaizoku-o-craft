package net.mcreator.kaizokuocraft.client;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SkillRegistry {

    private static final List<SkillDefinition> SKILLS =
            new ArrayList<>();

    static {

        register(
                new SkillDefinition(
                        "punch",
                        "Punch",
                        new ItemStack(Items.LEATHER),
                        350L
                )
        );

        register(
                new SkillDefinition(
                        "heavy_punch",
                        "Heavy",
                        new ItemStack(Items.IRON_INGOT),
                        1200L
                )
        );

        register(
                new SkillDefinition(
                        "shockwave",
                        "Shock",
                        new ItemStack(Items.COBBLESTONE),
                        1800L
                )
        );

        register(
                new SkillDefinition(
                        "uppercut",
                        "Upper",
                        new ItemStack(Items.GOLD_INGOT),
                        1300L
                )
        );

        register(
                new SkillDefinition(
                        "guard",
                        "Guard",
                        new ItemStack(Items.SHIELD),
                        3000L
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