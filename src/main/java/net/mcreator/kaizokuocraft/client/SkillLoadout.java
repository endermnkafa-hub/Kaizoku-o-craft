package net.mcreator.kaizokuocraft.client;

public final class SkillLoadout {

    private static final int SLOT_COUNT = 9;

    private static final String[] slots =
            new String[SLOT_COUNT];

    private static final String[] keys = {
            "Z",
            "X",
            "C",
            "V",
            "B",
            "N",
            "1",
            "2",
            "3"
    };

    private static int selectedSlot = 0;

    static {

        clearAll();

        slots[0] = "punch";
    }

    private SkillLoadout() {
    }

    public static int getSlotCount() {
        return SLOT_COUNT;
    }

    public static String getSkillId(
            int slot
    ) {

        if (!isValidSlot(slot)) {
            return null;
        }

        return slots[slot];
    }

    public static SkillDefinition getSkill(
            int slot
    ) {

        String id =
                getSkillId(slot);

        if (id == null) {
            return null;
        }

        return SkillRegistry.getSkill(id);
    }

    public static String getSkillName(
            int slot
    ) {

        SkillDefinition skill =
                getSkill(slot);

        if (skill == null) {
            return "Empty";
        }

        return skill.name();
    }

    public static net.minecraft.world.item.ItemStack getSkillIcon(
            int slot
    ) {

        SkillDefinition skill =
                getSkill(slot);

        if (skill == null) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }

        return skill.icon().copy();
    }

    public static String getSkillKey(
            int slot
    ) {

        if (!isValidSlot(slot)) {
            return "";
        }

        return keys[slot];
    }

    public static boolean isEmpty(
            int slot
    ) {

        return getSkill(slot) == null;
    }

    public static int getSelectedSlot() {
        return selectedSlot;
    }

    public static void selectSlot(
            int slot
    ) {

        if (isValidSlot(slot)) {
            selectedSlot = slot;
        }
    }

    public static void setSkill(
            int slot,
            String skillId
    ) {

        if (!isValidSlot(slot)) {
            return;
        }

        if (
                skillId == null
                        || SkillRegistry.getSkill(skillId) == null
        ) {
            slots[slot] = null;
            return;
        }

        slots[slot] = skillId;
    }

    public static void clearSlot(
            int slot
    ) {

        if (!isValidSlot(slot)) {
            return;
        }

        slots[slot] = null;
    }

    public static void clearAll() {

        for (int i = 0; i < SLOT_COUNT; i++) {
            slots[i] = null;
        }
    }

    private static boolean isValidSlot(
            int slot
    ) {

        return slot >= 0
                && slot < SLOT_COUNT;
    }
}