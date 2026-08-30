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

        // ==============================
        // FIST STYLES (DÖVÜŞÇÜ)
        // ==============================
        register(
                new SkillDefinition(
                        "punch",
                        "Punch",
                        "Düşmana anında hızlı bir yumruk indirir.",
                        new ItemStack(Items.LEATHER),
                        350L,
                        FightingStyle.FIST,
                        0.0D,
                        5.0D
                )
        );

        register(
                new SkillDefinition(
                        "double_strike",
                        "Double Strike",
                        "Seri sağ ve sol yumruklarla çift darbe vurur.",
                        new ItemStack(Items.FLINT),
                        800L,
                        FightingStyle.FIST,
                        5.0D,
                        8.0D
                )
        );

        register(
                new SkillDefinition(
                        "front_kick",
                        "Front Kick",
                        "Hedefi geriye savuran sert bir düz tekme.",
                        new ItemStack(Items.LEATHER_BOOTS),
                        1000L,
                        FightingStyle.FIST,
                        10.0D,
                        12.0D
                )
        );

        register(
                new SkillDefinition(
                        "uppercut",
                        "Uppercut",
                        "Aşağıdan yukarıya çeneye patlayıcı aparkat darbesi.",
                        new ItemStack(Items.GOLD_INGOT),
                        1300L,
                        FightingStyle.FIST,
                        15.0D,
                        10.0D
                )
        );

        register(
                new SkillDefinition(
                        "heavy_punch",
                        "Heavy Punch",
                        "Güçlü ve savurucu ağır bir yumruk darbesi.",
                        new ItemStack(Items.IRON_INGOT),
                        1200L,
                        FightingStyle.FIST,
                        20.0D,
                        15.0D
                )
        );

        register(
                new SkillDefinition(
                        "shockwave",
                        "Shockwave",
                        "Yere sertçe vurarak etraftaki düşmanları savurur.",
                        new ItemStack(Items.COBBLESTONE),
                        1800L,
                        FightingStyle.FIST,
                        30.0D,
                        35.0D
                )
        );

        register(
                new SkillDefinition(
                        "downslam",
                        "Downslam",
                        "Havaya zıplayıp yere şiddetle çakılarak alanı sarsar.",
                        new ItemStack(Items.ANVIL),
                        2500L,
                        FightingStyle.FIST,
                        40.0D,
                        25.0D
                )
        );

        // ==============================
        // SWORD STYLES (KILIÇ USTASI)
        // ==============================
        register(
                new SkillDefinition(
                        "sword_slash",
                        "1-Sword Slash",
                        "Kılıçla hızlı ve keskin bir kavisli kesik atar.",
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
                        "İleri doğru atılarak düşmanı çapraz kesen teknik.",
                        new ItemStack(Items.DIAMOND_SWORD),
                        2500L,
                        FightingStyle.SWORD,
                        20.0D,
                        20.0D
                )
        );

        register(
                new SkillDefinition(
                        "shishi_sonson",
                        "Shishi Sonson",
                        "Göz açıp kapayıncaya kadar ölümcül kın çekişi.",
                        new ItemStack(Items.NETHERITE_SWORD),
                        4000L,
                        FightingStyle.SWORD,
                        45.0D,
                        40.0D
                )
        );

        // ==============================
        // KICK STYLES (KARA BACAK)
        // ==============================
        register(
                new SkillDefinition(
                        "collier_kick",
                        "Collier Kick",
                        "Boyun bölgesine indirilen hızlı tekme darbesi.",
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
                        "Tepeden balta gibi indirilen ağır tekme darbesi.",
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
                        "Alev alan bacakla düşmanı tutuşturan cehennem tekmesi.",
                        new ItemStack(Items.BLAZE_POWDER),
                        6000L,
                        FightingStyle.KICK,
                        50.0D,
                        50.0D
                )
        );

        // ==============================
        // SNIPER STYLES (NİŞANCI)
        // ==============================
        register(
                new SkillDefinition(
                        "firebird_star",
                        "Firebird Star",
                        "Alevler saçan ateş kuşu mermisi fırlatır.",
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
                        "Çarptığı yerde patlayan barut yıldızı fırlatır.",
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

    public static List<SkillDefinition> getSkillsForStyle(FightingStyle style) {
        List<SkillDefinition> list = new ArrayList<>();
        for (SkillDefinition skill : SKILLS) {
            if (skill.style() == style) {
                list.add(skill);
            }
        }
        return list;
    }

    public static SkillDefinition getSkill(
            int index
    ) {
        if (index < 0 || index >= SKILLS.size()) {
            return null;
        }
        return SKILLS.get(index);
    }

    public static SkillDefinition getSkill(
            String id
    ) {
        for (SkillDefinition skill : SKILLS) {
            if (skill.id().equals(id)) {
                return skill;
            }
        }
        return null;
    }
}