package net.mcreator.kaizokuocraft.client;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class SkillLoadout {

    private static final int SLOT_COUNT = 9;

    private static final String[] skillNames = new String[SLOT_COUNT];
    private static final ItemStack[] skillIcons = new ItemStack[SLOT_COUNT];
    private static final String[] skillKeys = {
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

        skillNames[0] = "Punch";
        skillIcons[0] = new ItemStack(Items.LEATHER);
    }

    private SkillLoadout() {
    }

    public static int getSlotCount() {
        return SLOT_COUNT;
    }

    public static String getSkillName(int slot) {
        if (!isValidSlot(slot)) {
            return "Empty";
        }

        return skillNames[slot] == null
                ? "Empty"
                : skillNames[slot];
    }

    public static ItemStack getSkillIcon(int slot) {
        if (!isValidSlot(slot)) {
            return ItemStack.EMPTY;
        }

        if (skillIcons[slot] == null) {
            return ItemStack.EMPTY;
        }

        return skillIcons[slot].copy();
    }

    public static String getSkillKey(int slot) {
        if (!isValidSlot(slot)) {
            return "";
        }

        return skillKeys[slot];
    }

    public static boolean isEmpty(int slot) {
        return getSkillName(slot).equals("Empty");
    }

    public static int getSelectedSlot() {
        return selectedSlot;
    }

    public static void selectSlot(int slot) {
        if (isValidSlot(slot)) {
            selectedSlot = slot;
        }
    }

    public static void setSkill(
            int slot,
            String name,
            ItemStack icon
    ) {
        if (!isValidSlot(slot)) {
            return;
        }

        skillNames[slot] =
                name == null || name.isBlank()
                        ? "Empty"
                        : name;

        skillIcons[slot] =
                icon == null
                        ? ItemStack.EMPTY
                        : icon.copy();
    }

    public static void clearSlot(int slot) {
        if (!isValidSlot(slot)) {
            return;
        }

        skillNames[slot] = "Empty";
        skillIcons[slot] = ItemStack.EMPTY;
    }

    public static void clearAll() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            skillNames[i] = "Empty";
            skillIcons[i] = ItemStack.EMPTY;
        }
    }

    private static boolean isValidSlot(int slot) {
        return slot >= 0 && slot < SLOT_COUNT;
    }
}