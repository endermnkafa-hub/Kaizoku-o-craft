package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class CombatHud {

    private static final int SLOT_SIZE = 56;
    private static final int SLOT_GAP = 6;

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

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int totalWidth =
                (SLOT_SIZE * 3) + (SLOT_GAP * 2);

        int startX =
                (screenWidth - totalWidth) / 2;

        int y =
                screenHeight - 82;

        drawSkill(
                graphics,
                minecraft,
                startX,
                y,
                "Punch",
                "Z"
        );

        drawSkill(
                graphics,
                minecraft,
                startX + SLOT_SIZE + SLOT_GAP,
                y,
                "Empty",
                "X"
        );

        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_SIZE + SLOT_GAP) * 2,
                y,
                "Empty",
                "C"
        );
    }

    private static void drawSkill(
            GuiGraphics graphics,
            Minecraft minecraft,
            int x,
            int y,
            String name,
            String key
    ) {

        // Slot arka planı
        graphics.fill(
                x,
                y,
                x + SLOT_SIZE,
                y + SLOT_SIZE,
                0xCC111111
        );

        // Slot kenarı
        drawBorder(
                graphics,
                x,
                y,
                SLOT_SIZE,
                SLOT_SIZE,
                0xFFFFFFFF
        );

        // Şimdilik skill ikonu yerine placeholder
        graphics.fill(
                x + 10,
                y + 7,
                x + SLOT_SIZE - 10,
                y + 37,
                0xFF333333
        );

        // Skill adı
        int nameWidth =
                minecraft.font.width(name);

        graphics.drawString(
                minecraft.font,
                name,
                x + (SLOT_SIZE - nameWidth) / 2,
                y + 40,
                0xFFFFFFFF,
                true
        );

        // Kullanma tuşu
        int keyWidth =
                minecraft.font.width(key);

        graphics.drawString(
                minecraft.font,
                key,
                x + (SLOT_SIZE - keyWidth) / 2,
                y + 49,
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

        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y, x + 1, y + height, color);
        graphics.fill(x + width - 1, y, x + width, y + height, color);
    }
}