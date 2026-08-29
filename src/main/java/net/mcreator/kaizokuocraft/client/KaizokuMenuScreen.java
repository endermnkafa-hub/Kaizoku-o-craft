package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.minecraft.world.item.ItemStack;

public class KaizokuMenuScreen extends Screen {

    private enum Tab {
        STATS,
        SKILLS
    }

    private Tab currentTab = Tab.STATS;

    private int selectedSlot = 0;

    private static final int PANEL_WIDTH = 620;
    private static final int PANEL_HEIGHT = 360;

    public KaizokuMenuScreen() {
        super(
                Component.literal("Kaizoku-ō Craft")
        );
    }

    @Override
    protected void init() {

        clearWidgets();

        int panelLeft =
                (this.width - PANEL_WIDTH) / 2;

        int panelTop =
                (this.height - PANEL_HEIGHT) / 2;

        /*
         * STATS
         */
        addRenderableWidget(
                Button.builder(
                        Component.literal("STATLAR"),
                        button -> {
                            currentTab = Tab.STATS;
                            rebuild();
                        }
                ).bounds(
                        panelLeft + 8,
                        panelTop + 12,
                        105,
                        28
                ).build()
        );

        /*
         * SKILLS
         */
        addRenderableWidget(
                Button.builder(
                        Component.literal("SKİLLER"),
                        button -> {
                            currentTab = Tab.SKILLS;
                            rebuild();
                        }
                ).bounds(
                        panelLeft + 118,
                        panelTop + 12,
                        105,
                        28
                ).build()
        );

        /*
         * İçerik
         */
        if (currentTab == Tab.SKILLS) {
            createSkillButtons(
                    panelLeft,
                    panelTop
            );
        }
    }

    private void rebuild() {
        clearWidgets();
        init();
    }

    private void createSkillButtons(
            int panelLeft,
            int panelTop
    ) {

        int startX =
                panelLeft + 24;

        int startY =
                panelTop + 62;

        int buttonWidth = 150;
        int buttonHeight = 42;

        int horizontalGap = 10;
        int verticalGap = 8;

        for (int slot = 0; slot < 9; slot++) {

            final int slotIndex = slot;

            int column =
                    slot % 3;

            int row =
                    slot / 3;

            int x =
                    startX
                            + (buttonWidth + horizontalGap)
                            * column;

            int y =
                    startY
                            + (buttonHeight + verticalGap)
                            * row;

            boolean selected =
                    selectedSlot == slotIndex;

            String text =
                    (selected ? "> " : "")
                            + (slotIndex + 1)
                            + ". "
                            + SkillLoadout.getSkillName(
                                    slotIndex
                            );

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
                            buttonWidth,
                            buttonHeight
                    ).build()
            );
        }

        /*
         * Seçili slota Punch koy
         */
        addRenderableWidget(
                Button.builder(
                        Component.literal("Punch"),
                        button -> {

                            SkillLoadout.setSkill(
                                    selectedSlot,
                                    "Punch",
                                    new ItemStack(
                                            net.minecraft.world.item.Items.LEATHER
                                    )
                            );

                            rebuild();
                        }
                ).bounds(
                        panelLeft + 24,
                        panelTop + 276,
                        150,
                        32
                ).build()
        );

        /*
         * Seçili slotu boşalt
         */
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
                        panelLeft + 184,
                        panelTop + 276,
                        150,
                        32
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

        /*
         * Arka plan
         */
        renderBackground(
                graphics,
                mouseX,
                mouseY,
                partialTick
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
                0xF0121212
        );

        /*
         * Panel kenarlığı
         */
        drawBorder(
                graphics,
                panelLeft,
                panelTop,
                PANEL_WIDTH,
                PANEL_HEIGHT,
                0xFF8A8A8A
        );

        /*
         * Başlık
         */
        graphics.drawCenteredString(
                this.font,
                "KAIzoku-o CRAFT",
                this.width / 2,
                panelTop - 18,
                0xFFFFFFFF
        );

        if (currentTab == Tab.STATS) {
            renderStats(
                    graphics,
                    panelLeft,
                    panelTop
            );
        } else {
            renderSkillsInfo(
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

        Minecraft minecraft =
                Minecraft.getInstance();

        long level =
                ClientPlayerData.getLevel();

        long experience =
                ClientPlayerData.getExperience();

        graphics.drawString(
                this.font,
                "Oyuncu İstatistikleri",
                panelLeft + 25,
                panelTop + 62,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "Level: " + level,
                panelLeft + 25,
                panelTop + 92,
                0xFFFFD54A
        );

        graphics.drawString(
                this.font,
                "XP: " + experience,
                panelLeft + 25,
                panelTop + 112,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "Race: "
                        + ClientPlayerData
                                .getRace()
                                .getDisplayName(),
                panelLeft + 25,
                panelTop + 132,
                0xFFFFFFFF
        );

        double levelMultiplier =
                PlayerPowerClient
                        .getDamageMultiplier(level);

        graphics.drawString(
                this.font,
                String.format(
                        "Level Gücü: ×%.2f",
                        levelMultiplier
                ),
                panelLeft + 25,
                panelTop + 152,
                0xFFFFD54A
        );

        graphics.drawString(
                this.font,
                "Bu ekran ileride bütün stat sisteminin",
                panelLeft + 25,
                panelTop + 195,
                0xFFAAAAAA
        );

        graphics.drawString(
                this.font,
                "yönetildiği ana karakter menüsü olacak.",
                panelLeft + 25,
                panelTop + 211,
                0xFFAAAAAA
        );
    }

    private void renderSkillsInfo(
            GuiGraphics graphics,
            int panelLeft,
            int panelTop
    ) {

        graphics.drawString(
                this.font,
                "Combat Bar Düzeni",
                panelLeft + 25,
                panelTop + 45,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "Bir slot seç ve o slota skill ata.",
                panelLeft + 25,
                panelTop + 296,
                0xFFAAAAAA
        );

        graphics.drawString(
                this.font,
                "Seçili slot: "
                        + (selectedSlot + 1)
                        + "  ["
                        + SkillLoadout.getSkillKey(
                                selectedSlot
                        )
                        + "]",
                panelLeft + 25,
                panelTop + 317,
                0xFFFFD54A
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
                y + 2,
                color
        );

        graphics.fill(
                x,
                y + height - 2,
                x + width,
                y + height,
                color
        );

        graphics.fill(
                x,
                y,
                x + 2,
                y + height,
                color
        );

        graphics.fill(
                x + width - 2,
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