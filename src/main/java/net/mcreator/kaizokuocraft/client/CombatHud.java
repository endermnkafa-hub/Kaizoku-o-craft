package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class CombatHud {

    private static final int SLOT_WIDTH = 44;
    private static final int SLOT_HEIGHT = 58;
    private static final int SLOT_GAP = 4;

    private static final int ICON_SIZE = 20;

    private CombatHud() {
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {

        if (!CombatState.isActive()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.screen != null) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();

        int screenWidth =
                minecraft.getWindow().getGuiScaledWidth();

        int screenHeight =
                minecraft.getWindow().getGuiScaledHeight();

        final int slotCount = 9;

        int totalWidth =
                (SLOT_WIDTH * slotCount)
                        + (SLOT_GAP * (slotCount - 1));

        int startX =
                (screenWidth - totalWidth) / 2;

        int y =
                screenHeight - SLOT_HEIGHT - 8;

        drawSkill(
                graphics,
                minecraft,
                startX,
                y,
                "Punch",
                "Z",
                new ItemStack(Items.LEATHER)
        );

        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP),
                y,
                "Empty",
                "X",
                ItemStack.EMPTY
        );

        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 2,
                y,
                "Empty",
                "C",
                ItemStack.EMPTY
        );

        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 3,
                y,
                "Empty",
                "V",
                ItemStack.EMPTY
        );

        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 4,
                y,
                "Empty",
                "B",
                ItemStack.EMPTY
        );

        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 5,
                y,
                "Empty",
                "N",
                ItemStack.EMPTY
        );

        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 6,
                y,
                "Empty",
                "1",
                ItemStack.EMPTY
        );

        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 7,
                y,
                "Empty",
                "2",
                ItemStack.EMPTY
        );

        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 8,
                y,
                "Empty",
                "3",
                ItemStack.EMPTY
        );
    }

    private static void drawSkill(
            GuiGraphics graphics,
            Minecraft minecraft,
            int x,
            int y,
            String name,
            String key,
            ItemStack icon
    ) {

        // Gölge
        graphics.fill(
                x + 2,
                y + 2,
                x + SLOT_WIDTH + 2,
                y + SLOT_HEIGHT + 2,
                0x90000000
        );

        // Ana arka plan
        graphics.fill(
                x,
                y,
                x + SLOT_WIDTH,
                y + SLOT_HEIGHT,
                0xD0181818
        );

        // Kenarlık
        drawBorder(
                graphics,
                x,
                y,
                SLOT_WIDTH,
                SLOT_HEIGHT,
                0xFFAAAAAA
        );

        // İkon kutusu
        int iconX =
                x + (SLOT_WIDTH - ICON_SIZE) / 2;

        int iconY =
                y + 5;

        graphics.fill(
                iconX - 2,
                iconY - 2,
                iconX + ICON_SIZE + 2,
                iconY + ICON_SIZE + 2,
                0xFF252525
        );

        // İkon
        if (!icon.isEmpty()) {

            int renderedIconX =
                    x + (SLOT_WIDTH - 16) / 2;

            int renderedIconY =
                    iconY + 2;

            graphics.renderItem(
                    icon,
                    renderedIconX,
                    renderedIconY
            );

        } else {

            String emptyText = "?";

            int emptyWidth =
                    minecraft.font.width(emptyText);

            graphics.drawString(
                    minecraft.font,
                    emptyText,
                    x + (SLOT_WIDTH - emptyWidth) / 2,
                    iconY + 5,
                    0xFF666666,
                    false
            );
        }

        // Skill adı
        String displayName =
                name.length() > 9
                        ? name.substring(0, 9)
                        : name;

        int nameWidth =
                minecraft.font.width(displayName);

        graphics.drawString(
                minecraft.font,
                displayName,
                x + (SLOT_WIDTH - nameWidth) / 2,
                y + 32,
                0xFFFFFFFF,
                true
        );

        // Tuş
        String keyText =
                "[" + key + "]";

        int keyWidth =
                minecraft.font.width(keyText);

        graphics.drawString(
                minecraft.font,
                keyText,
                x + (SLOT_WIDTH - keyWidth) / 2,
                y + 45,
                0xFFFFD54A,
                true
        );
    }

    private static void drawBorder(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int color
    ) {

        graphics.fill(
                x,
                y,
                x + width,
                y + 1,
                color
        );

        graphics.fill(
                x,
                y + height - 1,
                x + width,
                y + height,
                color
        );

        graphics.fill(
                x,
                y,
                x + 1,
                y + height,
                color
        );

        graphics.fill(
                x + width - 1,
                y,
                x + width,
                y + height,
                color
        );
    }
}