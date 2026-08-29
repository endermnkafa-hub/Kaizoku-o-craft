package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class KaizokuMenuScreen extends Screen {

    private static final int PANEL_WIDTH = 500;
    private static final int PANEL_HEIGHT = 300;

    private static final int SIDE_WIDTH = 90;

    private enum Tab {
        SKILLS,
        STATS
    }

    private Tab currentTab = Tab.SKILLS;

    private int selectedSlot = 0;

    public KaizokuMenuScreen() {
        super(Component.literal("Kaizoku-ō Craft"));
    }

    @Override
    protected void init() {

        clearWidgets();

        int panelLeft =
                (this.width - PANEL_WIDTH) / 2;

        int panelTop =
                (this.height - PANEL_HEIGHT) / 2;

        // SKILLER
        addRenderableWidget(
                Button.builder(
                        Component.literal("SKİLLER"),
                        button -> {
                            currentTab = Tab.SKILLS;
                            rebuild();
                        }
                ).bounds(
                        panelLeft + 8,
                        panelTop + 48,
                        SIDE_WIDTH - 16,
                        28
                ).build()
        );

        // STATLAR
        addRenderableWidget(
                Button.builder(
                        Component.literal("STATLAR"),
                        button -> {
                            currentTab = Tab.STATS;
                            rebuild();
                        }
                ).bounds(
                        panelLeft + 8,
                        panelTop + 82,
                        SIDE_WIDTH - 16,
                        28
                ).build()
        );

        if (currentTab == Tab.SKILLS) {
            createSkillPage(
                    panelLeft,
                    panelTop
            );
        }
    }

    private void rebuild() {
        clearWidgets();
        init();
    }

    private void createSkillPage(
            int panelLeft,
            int panelTop
    ) {

        int contentLeft =
                panelLeft + SIDE_WIDTH + 18;

        int contentTop =
                panelTop + 48;

        int slotWidth = 112;
        int slotHeight = 40;
        int gap = 6;

        // 9 slot - 3x3
        for (int slot = 0; slot < 9; slot++) {

            final int slotIndex = slot;

            int column =
                    slot % 3;

            int row =
                    slot / 3;

            int x =
                    contentLeft
                            + column * (slotWidth + gap);

            int y =
                    contentTop
                            + row * (slotHeight + gap);

            boolean selected =
                    selectedSlot == slotIndex;

            String skillName =
                    SkillLoadout.getSkillName(
                            slotIndex
                    );

            String text =
                    (selected ? "> " : "")
                            + (slotIndex + 1)
                            + "  "
                            + skillName;

            addRenderableWidget(
                    Button.builder(
                            Component.literal(text),
                            button -> {

                                selectedSlot =
                                        slotIndex;

                                SkillLoadout.selectSlot(
                                        slotIndex
                                );

                                rebuild();
                            }
                    ).bounds(
                            x,
                            y,
                            slotWidth,
                            slotHeight
                    ).build()
            );
        }

        // Punch ata
        addRenderableWidget(
                Button.builder(
                        Component.literal("Punch"),
                        button -> {

                            SkillLoadout.setSkill(
                                    selectedSlot,
                                    "Punch",
                                    new ItemStack(
                                            Items.LEATHER
                                    )
                            );

                            rebuild();
                        }
                ).bounds(
                        contentLeft,
                        panelTop + 258,
                        100,
                        28
                ).build()
        );

        // Temizle
        addRenderableWidget(
                Button.builder(
                        Component.literal("Temizle"),
                        button -> {

                            SkillLoadout.clearSlot(
                                    selectedSlot
                            );

                            rebuild();
                        }
                ).bounds(
                        contentLeft + 106,
                        panelTop + 258,
                        100,
                        28
                ).build()
        );
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        // Arka planı hafif karart
        renderTransparentBackground(graphics);

        int panelLeft =
                (this.width - PANEL_WIDTH) / 2;

        int panelTop =
                (this.height - PANEL_HEIGHT) / 2;

        // Ana panel
        graphics.fill(
                panelLeft,
                panelTop,
                panelLeft + PANEL_WIDTH,
                panelTop + PANEL_HEIGHT,
                0xF0141414
        );

        // Sol panel
        graphics.fill(
                panelLeft,
                panelTop,
                panelLeft + SIDE_WIDTH,
                panelTop + PANEL_HEIGHT,
                0xFF0D0D0D
        );

        // Dış kenarlık
        drawBorder(
                graphics,
                panelLeft,
                panelTop,
                PANEL_WIDTH,
                PANEL_HEIGHT,
                0xFF666666
        );

        // Ayırıcı çizgi
        graphics.fill(
                panelLeft + SIDE_WIDTH,
                panelTop,
                panelLeft + SIDE_WIDTH + 1,
                panelTop + PANEL_HEIGHT,
                0xFF555555
        );

        // Başlık
        graphics.drawCenteredString(
                this.font,
                "KAIzoku-Ō CRAFT",
                this.width / 2,
                panelTop + 14,
                0xFFFFFFFF
        );

        // Aktif sayfa
        if (currentTab == Tab.SKILLS) {

            graphics.drawString(
                    this.font,
                    "SKİLLER",
                    panelLeft + SIDE_WIDTH + 18,
                    panelTop + 32,
                    0xFFFFD54A
            );

            graphics.drawString(
                    this.font,
                    "Combat Bar",
                    panelLeft + SIDE_WIDTH + 72,
                    panelTop + 32,
                    0xFF888888
            );

        } else {

            graphics.drawString(
                    this.font,
                    "STATLAR",
                    panelLeft + SIDE_WIDTH + 18,
                    panelTop + 32,
                    0xFFFFD54A
            );

            graphics.drawString(
                    this.font,
                    "Karakter",
                    panelLeft + SIDE_WIDTH + 72,
                    panelTop + 32,
                    0xFF888888
            );

            renderStats(
                    graphics,
                    panelLeft,
                    panelTop
            );
        }

        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    private void renderStats(
            GuiGraphics graphics,
            int panelLeft,
            int panelTop
    ) {

        long level =
                ClientPlayerData.getLevel();

        long experience =
                ClientPlayerData.getExperience();

        String race =
                ClientPlayerData
                        .getRace()
                        .getDisplayName();

        graphics.drawString(
                this.font,
                "Level: " + level,
                panelLeft + SIDE_WIDTH + 20,
                panelTop + 65,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "XP: " + experience,
                panelLeft + SIDE_WIDTH + 20,
                panelTop + 85,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "Race: " + race,
                panelLeft + SIDE_WIDTH + 20,
                panelTop + 105,
                0xFFFFFFFF
        );
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