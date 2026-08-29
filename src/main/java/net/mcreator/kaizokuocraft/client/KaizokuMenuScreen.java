package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class KaizokuMenuScreen extends Screen {

    private enum Tab {
        SKILLS,
        STATS
    }

    private static final int PANEL_WIDTH = 540;
    private static final int PANEL_HEIGHT = 310;

    private static final int SIDE_WIDTH = 92;

    private static final int SLOT_SIZE = 42;
    private static final int SLOT_GAP = 4;

    private static final int TOP_SLOT_Y = 72;
    private static final int COMBAT_SLOT_Y = 205;

    private Tab currentTab = Tab.SKILLS;

    private int draggingSkill = -1;

    private int mouseX;
    private int mouseY;

    public KaizokuMenuScreen() {
        super(Component.literal("Kaizoku-ō Craft"));
    }

    @Override
    protected void init() {
        // Bu ekran tamamen custom çizildiği için
        // vanilla Button kullanmıyoruz.
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        this.mouseX = mouseX;
        this.mouseY = mouseY;

        /*
         * VANILLA BLUR YOK.
         *
         * Ekranı kendimiz hafif karartıyoruz.
         */
        graphics.fill(
                0,
                0,
                this.width,
                this.height,
                0x90000000
        );

        int panelLeft =
                (this.width - PANEL_WIDTH) / 2;

        int panelTop =
                (this.height - PANEL_HEIGHT) / 2;

        /*
         * Ana panel
         */
        graphics.fill(
                panelLeft,
                panelTop,
                panelLeft + PANEL_WIDTH,
                panelTop + PANEL_HEIGHT,
                0xFF151515
        );

        drawBorder(
                graphics,
                panelLeft,
                panelTop,
                PANEL_WIDTH,
                PANEL_HEIGHT,
                0xFF777777
        );

        /*
         * Sol menü alanı
         */
        graphics.fill(
                panelLeft,
                panelTop,
                panelLeft + SIDE_WIDTH,
                panelTop + PANEL_HEIGHT,
                0xFF0D0D0D
        );

        /*
         * Sol ayırıcı
         */
        graphics.fill(
                panelLeft + SIDE_WIDTH,
                panelTop,
                panelLeft + SIDE_WIDTH + 1,
                panelTop + PANEL_HEIGHT,
                0xFF555555
        );

        /*
         * Başlık
         */
        graphics.drawCenteredString(
                this.font,
                "KAIzoku-Ō CRAFT",
                panelLeft + PANEL_WIDTH / 2,
                panelTop + 12,
                0xFFFFFFFF
        );

        /*
         * Sol sekmeler
         */
        drawTab(
                graphics,
                panelLeft,
                panelTop,
                "SKİLLER",
                Tab.SKILLS,
                40
        );

        drawTab(
                graphics,
                panelLeft,
                panelTop,
                "STATLAR",
                Tab.STATS,
                78
        );

        /*
         * İçerik
         */
        if (currentTab == Tab.SKILLS) {

            renderSkills(
                    graphics,
                    panelLeft,
                    panelTop
            );

        } else {

            renderStats(
                    graphics,
                    panelLeft,
                    panelTop
            );
        }

        /*
         * Sürüklenen skill'i mouse altında çiz.
         */
        if (draggingSkill != -1) {
            drawDraggedSkill(
                    graphics,
                    mouseX,
                    mouseY
            );
        }
    }

    private void drawTab(
            GuiGraphics graphics,
            int panelLeft,
            int panelTop,
            String text,
            Tab tab,
            int offsetY
    ) {

        boolean selected =
                currentTab == tab;

        int x =
                panelLeft + 8;

        int y =
                panelTop + offsetY;

        int width =
                SIDE_WIDTH - 16;

        int height = 28;

        /*
         * Aktif sekmenin arka planı
         */
        if (selected) {

            graphics.fill(
                    x,
                    y,
                    x + width,
                    y + height,
                    0xFF292929
            );

            graphics.fill(
                    x,
                    y,
                    x + 3,
                    y + height,
                    0xFFFFD54A
            );
        }

        int textWidth =
                this.font.width(text);

        graphics.drawString(
                this.font,
                text,
                x + (width - textWidth) / 2,
                y + 10,
                selected
                        ? 0xFFFFD54A
                        : 0xFFAAAAAA,
                true
        );
    }

    private void renderSkills(
            GuiGraphics graphics,
            int panelLeft,
            int panelTop
    ) {

        int contentLeft =
                panelLeft + SIDE_WIDTH + 18;

        /*
         * Üst başlık
         */
        graphics.drawString(
                this.font,
                "KULLANILABİLİR SKİLLER",
                contentLeft,
                panelTop + 42,
                0xFFFFFFFF,
                true
        );

        /*
         * Üst skill kutuları
         */
        for (int slot = 0; slot < 9; slot++) {

            int x =
                    contentLeft
                            + slot * (SLOT_SIZE + SLOT_GAP);

            int y =
                    panelTop + TOP_SLOT_Y;

            drawLibrarySlot(
                    graphics,
                    slot,
                    x,
                    y
            );
        }

        /*
         * Alt başlık
         */
        graphics.drawString(
                this.font,
                "COMBAT BAR",
                contentLeft,
                panelTop + 174,
                0xFFFFFFFF,
                true
        );

        /*
         * Alt combat slotları
         */
        for (int slot = 0; slot < 9; slot++) {

            int x =
                    contentLeft
                            + slot * (SLOT_SIZE + SLOT_GAP);

            int y =
                    panelTop + COMBAT_SLOT_Y;

            drawCombatSlot(
                    graphics,
                    slot,
                    x,
                    y
            );
        }

        /*
         * Bilgi
         */
        graphics.drawString(
                this.font,
                "Skill'i üstten tutup aşağıdaki slota sürükle.",
                contentLeft,
                panelTop + 264,
                0xFF888888
        );

        graphics.drawString(
                this.font,
                "Sağ tık = slotu temizle",
                contentLeft,
                panelTop + 280,
                0xFF666666
        );
    }

    private void drawLibrarySlot(
            GuiGraphics graphics,
            int slot,
            int x,
            int y
    ) {

        drawSlotBackground(
                graphics,
                x,
                y,
                false
        );

        String name =
                getLibrarySkillName(slot);

        if (name.equals("Empty")) {

            graphics.drawCenteredString(
                    this.font,
                    "?",
                    x + SLOT_SIZE / 2,
                    y + 12,
                    0xFF555555
            );

            return;
        }

        ItemStack icon =
                getLibrarySkillIcon(slot);

        if (!icon.isEmpty()) {

            graphics.renderItem(
                    icon,
                    x + 13,
                    y + 4
            );
        }

        graphics.drawCenteredString(
                this.font,
                name,
                x + SLOT_SIZE / 2,
                y + 26,
                0xFFFFFFFF
        );
    }

    private void drawCombatSlot(
            GuiGraphics graphics,
            int slot,
            int x,
            int y
    ) {

        boolean hover =
                isInside(
                        this.mouseX,
                        this.mouseY,
                        x,
                        y,
                        SLOT_SIZE,
                        SLOT_SIZE
                );

        drawSlotBackground(
                graphics,
                x,
                y,
                hover
        );

        String name =
                SkillLoadout.getSkillName(slot);

        ItemStack icon =
                SkillLoadout.getSkillIcon(slot);

        if (!icon.isEmpty()) {

            graphics.renderItem(
                    icon,
                    x + 13,
                    y + 4
            );
        }

        graphics.drawCenteredString(
                this.font,
                name.equals("Empty")
                        ? "?"
                        : name,
                x + SLOT_SIZE / 2,
                y + 26,
                name.equals("Empty")
                        ? 0xFF555555
                        : 0xFFFFFFFF
        );

        /*
         * Kullanım tuşu
         */
        graphics.drawString(
                this.font,
                SkillLoadout.getSkillKey(slot),
                x + 3,
                y + 3,
                0xFFFFD54A
        );
    }

    private void drawSlotBackground(
            GuiGraphics graphics,
            int x,
            int y,
            boolean hover
    ) {

        graphics.fill(
                x + 1,
                y + 1,
                x + SLOT_SIZE + 1,
                y + SLOT_SIZE + 1,
                0x70000000
        );

        graphics.fill(
                x,
                y,
                x + SLOT_SIZE,
                y + SLOT_SIZE,
                hover
                        ? 0xFF303030
                        : 0xFF202020
        );

        drawBorder(
                graphics,
                x,
                y,
                SLOT_SIZE,
                SLOT_SIZE,
                hover
                        ? 0xFFFFD54A
                        : 0xFF777777
        );
    }

    private void drawDraggedSkill(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        if (draggingSkill == -1) {
            return;
        }

        ItemStack icon =
                getLibrarySkillIcon(draggingSkill);

        String name =
                getLibrarySkillName(draggingSkill);

        int x =
                mouseX - SLOT_SIZE / 2;

        int y =
                mouseY - SLOT_SIZE / 2;

        graphics.fill(
                x,
                y,
                x + SLOT_SIZE,
                y + SLOT_SIZE,
                0xEE202020
        );

        drawBorder(
                graphics,
                x,
                y,
                SLOT_SIZE,
                SLOT_SIZE,
                0xFFFFD54A
        );

        if (!icon.isEmpty()) {

            graphics.renderItem(
                    icon,
                    x + 13,
                    y + 4
            );
        }

        graphics.drawCenteredString(
                this.font,
                name,
                x + SLOT_SIZE / 2,
                y + 26,
                0xFFFFFFFF
        );
    }

    private void renderStats(
            GuiGraphics graphics,
            int panelLeft,
            int panelTop
    ) {

        int x =
                panelLeft + SIDE_WIDTH + 20;

        int y =
                panelTop + 52;

        graphics.drawString(
                this.font,
                "KARAKTER",
                x,
                y,
                0xFFFFD54A,
                true
        );

        graphics.drawString(
                this.font,
                "Level: "
                        + ClientPlayerData.getLevel(),
                x,
                y + 28,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "XP: "
                        + ClientPlayerData.getExperience(),
                x,
                y + 48,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "Race: "
                        + ClientPlayerData
                                .getRace()
                                .getDisplayName(),
                x,
                y + 68,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "Combat sistemi:",
                x,
                y + 105,
                0xFFFFD54A,
                true
        );

        graphics.drawString(
                this.font,
                "9 skill slotu",
                x,
                y + 125,
                0xFFAAAAAA
        );

        graphics.drawString(
                this.font,
                "Level tabanlı güç",
                x,
                y + 143,
                0xFFAAAAAA
        );
    }

    private String getLibrarySkillName(int slot) {

        /*
         * Şu an elimizde yalnızca Punch var.
         *
         * Daha sonra SkillRegistry'den
         * bütün açılmış skillleri okuyacağız.
         */
        if (slot == 0) {
            return "Punch";
        }

        return "Empty";
    }

    private ItemStack getLibrarySkillIcon(int slot) {

        if (slot == 0) {
            return new ItemStack(
                    net.minecraft.world.item.Items.LEATHER
            );
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        int panelLeft =
                (this.width - PANEL_WIDTH) / 2;

        int panelTop =
                (this.height - PANEL_HEIGHT) / 2;

        /*
         * Sol sekme: Skills
         */
        if (
                isInside(
                        mouseX,
                        mouseY,
                        panelLeft + 8,
                        panelTop + 40,
                        SIDE_WIDTH - 16,
                        28
                )
        ) {

            currentTab = Tab.SKILLS;
            return true;
        }

        /*
         * Sol sekme: Stats
         */
        if (
                isInside(
                        mouseX,
                        mouseY,
                        panelLeft + 8,
                        panelTop + 78,
                        SIDE_WIDTH - 16,
                        28
                )
        ) {

            currentTab = Tab.STATS;
            return true;
        }

        if (currentTab != Tab.SKILLS) {
            return super.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            );
        }

        int contentLeft =
                panelLeft + SIDE_WIDTH + 18;

        /*
         * Üstten skill sürüklemeye başla.
         */
        if (button == 0) {

            for (int slot = 0; slot < 9; slot++) {

                int x =
                        contentLeft
                                + slot * (SLOT_SIZE + SLOT_GAP);

                int y =
                        panelTop + TOP_SLOT_Y;

                if (
                        isInside(
                                mouseX,
                                mouseY,
                                x,
                                y,
                                SLOT_SIZE,
                                SLOT_SIZE
                        )
                ) {

                    if (
                            !getLibrarySkillName(slot)
                                    .equals("Empty")
                    ) {

                        draggingSkill = slot;
                        return true;
                    }
                }
            }
        }

        /*
         * Sağ tık = combat slotunu temizle.
         */
        if (button == 1) {

            for (int slot = 0; slot < 9; slot++) {

                int x =
                        contentLeft
                                + slot * (SLOT_SIZE + SLOT_GAP);

                int y =
                        panelTop + COMBAT_SLOT_Y;

                if (
                        isInside(
                                mouseX,
                                mouseY,
                                x,
                                y,
                                SLOT_SIZE,
                                SLOT_SIZE
                        )
                ) {

                    SkillLoadout.clearSlot(slot);
                    return true;
                }
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (
                button == 0
                        && draggingSkill != -1
        ) {

            int panelLeft =
                    (this.width - PANEL_WIDTH) / 2;

            int panelTop =
                    (this.height - PANEL_HEIGHT) / 2;

            int contentLeft =
                    panelLeft + SIDE_WIDTH + 18;

            /*
             * Bırakılan yeri kontrol et.
             */
            for (int slot = 0; slot < 9; slot++) {

                int x =
                        contentLeft
                                + slot * (SLOT_SIZE + SLOT_GAP);

                int y =
                        panelTop + COMBAT_SLOT_Y;

                if (
                        isInside(
                                mouseX,
                                mouseY,
                                x,
                                y,
                                SLOT_SIZE,
                                SLOT_SIZE
                        )
                ) {

                    String name =
                            getLibrarySkillName(
                                    draggingSkill
                            );

                    ItemStack icon =
                            getLibrarySkillIcon(
                                    draggingSkill
                            );

                    SkillLoadout.setSkill(
                            slot,
                            name,
                            icon
                    );

                    break;
                }
            }

            draggingSkill = -1;
            return true;
        }

        return super.mouseReleased(
                mouseX,
                mouseY,
                button
        );
    }

    private boolean isInside(
            double mouseX,
            double mouseY,
            int x,
            int y,
            int width,
            int height
    ) {

        return mouseX >= x
                && mouseX < x + width
                && mouseY >= y
                && mouseY < y + height;
    }

    private void drawBorder(
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}