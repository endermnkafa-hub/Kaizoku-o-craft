package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class CombatHud {

    private static final int SLOT_WIDTH = 64;
    private static final int SLOT_HEIGHT = 72;
    private static final int SLOT_GAP = 5;

    private static final int ICON_SIZE = 32;

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

        int totalWidth =
                (SLOT_WIDTH * 3) + (SLOT_GAP * 2);

        int startX =
                (screenWidth - totalWidth) / 2;

        int y =
                screenHeight - SLOT_HEIGHT - 12;

        /*
         * İlk slot:
         * Punch
         */
        drawSkill(
                graphics,
                minecraft,
                startX,
                y,
                "Punch",
                "Z",
                new ItemStack(Items.LEATHER)
        );

        /*
         * İkinci slot:
         * Şimdilik boş
         */
        drawSkill(
                graphics,
                minecraft,
                startX + SLOT_WIDTH + SLOT_GAP,
                y,
                "Empty",
                "X",
                ItemStack.EMPTY
        );

        /*
         * Üçüncü slot:
         * Şimdilik boş
         */
        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 2,
                y,
                "Empty",
                "C",
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

        /*
         * Slot gölgesi
         */
        graphics.fill(
                x + 2,
                y + 2,
                x + SLOT_WIDTH + 2,
                y + SLOT_HEIGHT + 2,
                0x90000000
        );

        /*
         * Slot arka planı
         */
        graphics.fill(
                x,
                y,
                x + SLOT_WIDTH,
                y + SLOT_HEIGHT,
                0xD0161616
        );

        /*
         * Üst kenar
         */
        graphics.fill(
                x,
                y,
                x + SLOT_WIDTH,
                y + 2,
                0xFFFFFFFF
        );

        /*
         * Alt kenar
         */
        graphics.fill(
                x,
                y + SLOT_HEIGHT - 2,
                x + SLOT_WIDTH,
                y + SLOT_HEIGHT,
                0xFF555555
        );

        /*
         * Sol kenar
         */
        graphics.fill(
                x,
                y,
                x + 2,
                y + SLOT_HEIGHT,
                0xFFFFFFFF
        );

        /*
         * Sağ kenar
         */
        graphics.fill(
                x + SLOT_WIDTH - 2,
                y,
                x + SLOT_WIDTH,
                y + SLOT_HEIGHT,
                0xFF555555
        );

        /*
         * Skill icon alanı
         */
        int iconX =
                x + (SLOT_WIDTH - ICON_SIZE) / 2;

        int iconY =
                y + 5;

        graphics.fill(
                iconX - 3,
                iconY - 3,
                iconX + ICON_SIZE + 3,
                iconY + ICON_SIZE + 3,
                0xFF242424
        );

        /*
         * Gerçek item iconunu çiz.
         */
        if (!icon.isEmpty()) {
            graphics.renderItem(
                    icon,
                    iconX,
                    iconY
            );
        } else {

            /*
             * Boş skill için ? göster.
             */
            String emptyText = "?";

            int emptyWidth =
                    minecraft.font.width(emptyText);

            graphics.drawString(
                    minecraft.font,
                    emptyText,
                    iconX + (ICON_SIZE - emptyWidth) / 2,
                    iconY + 9,
                    0xFF777777,
                    false
            );
        }

        /*
         * Skill adı
         */
        int nameWidth =
                minecraft.font.width(name);

        graphics.drawString(
                minecraft.font,
                name,
                x + (SLOT_WIDTH - nameWidth) / 2,
                y + 43,
                0xFFFFFFFF,
                true
        );

        /*
         * Kullanım tuşu
         */
        int keyWidth =
                minecraft.font.width("[" + key + "]");

        graphics.drawString(
                minecraft.font,
                "[" + key + "]",
                x + (SLOT_WIDTH - keyWidth) / 2,
                y + 57,
                0xFFFFD54A,
                true
        );
    }
}