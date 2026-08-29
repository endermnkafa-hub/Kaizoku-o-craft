package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class KaizokuMenuScreen extends Screen {

    private static final int PANEL_WIDTH = 620;
    private static final int PANEL_HEIGHT = 380;

    private static final int SIDE_WIDTH = 120;

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

        /*
         * SOL MENÜ
         *
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
                        panelLeft + 12,
                        panelTop + 45,
                        SIDE_WIDTH - 24,
                        32
                ).build()
        );

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
                        panelLeft + 12,
                        panelTop + 85,
                        SIDE_WIDTH - 24,
                        32
                ).build()
        );

        /*
         * SKILL SAYFASI
         */
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
                panelLeft + SIDE_WIDTH + 12;

        int contentTop =
                panelTop + 45;

        int slotWidth = 105;
        int slotHeight = 85;
        int gap = 8;

        /*
         * 9 skill slotu.
         *
         * 3 x 3 düzen.
         */
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

            String skillName =
                    SkillLoadout.getSkillName(
                            slotIndex
                    );

            boolean selected =
                    selectedSlot == slotIndex;

            String buttonText =
                    (selected ? "> " : "")
                            + (slotIndex + 1)
                            + "  "
                            + skillName;

            addRenderableWidget(
                    Button.builder(
                            Component.literal(buttonText),
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

        /*
         * PUNCH EKLE
         */
        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Punch ekle"
                        ),
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
                        panelTop + 325,
                        130,
                        30
                ).build()
        );

        /*
         * SLOTU TEMİZLE
         */
        addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Slotu temizle"
                        ),
                        button -> {

                            SkillLoadout.clearSlot(
                                    selectedSlot
                            );

                            rebuild();
                        }
                ).bounds(
                        contentLeft + 140,
                        panelTop + 325,
                        130,
                        30
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
         * Normal Minecraft arka planı + hafif karartma.
         */
        this.renderTransparentBackground(
                graphics
        );

        int panelLeft =
                (this.width - PANEL_WIDTH) / 2;

        int panelTop =
                (this.height - PANEL_HEIGHT) / 2;

        /*
         * Ana panel gövdesi.
         */
        graphics.fill(
                panelLeft,
                panelTop,
                panelLeft + PANEL_WIDTH,
                panelTop + PANEL_HEIGHT,
                0xF0141414
        );

        /*
         * Ana panel kenarlığı.
         */
        drawBorder(
                graphics,
                panelLeft,
                panelTop,
                PANEL_WIDTH,
                PANEL_HEIGHT,
                0xFF777777
        );

        /*
         * Sol panel.
         */
        graphics.fill(
                panelLeft,
                panelTop,
                panelLeft + SIDE_WIDTH,
                panelTop + PANEL_HEIGHT,
                0xFF0E0E0E
        );

        /*
         * Başlık.
         */
        graphics.drawCenteredString(
                this.font,
                "KAIZOKU-Ō CRAFT",
                panelLeft + PANEL_WIDTH / 2,
                panelTop + 14,
                0xFFFFFFFF
        );

        /*
         * Sol menü başlığı.
         */
        graphics.drawString(
                this.font,
                "MENÜ",
                panelLeft + 30,
                panelTop + 25,
                0xFFAAAAAA
        );

        /*
         * Skills sayfası.
         */
        if (currentTab == Tab.SKILLS) {

            graphics.drawString(
                    this.font,
                    "SKİLLER",
                    panelLeft + SIDE_WIDTH + 18,
                    panelTop + 38,
                    0xFFFFD54A
            );

            graphics.drawString(
                    this.font,
                    "Combat Bar'ındaki skillleri düzenle.",
                    panelLeft + SIDE_WIDTH + 18,
                    panelTop + 25,
                    0xFFAAAAAA
            );

            drawSelectedSkillInfo(
                    graphics,
                    panelLeft,
                    panelTop
            );
        }

        /*
         * Stats sayfası.
         *
         * Şimdilik sadece yer tutucu.
         * Daha sonra gerçek stat sistemi gelecek.
         */
        if (currentTab == Tab.STATS) {

            graphics.drawString(
                    this.font,
                    "STATLAR",
                    panelLeft + SIDE_WIDTH + 18,
                    panelTop + 38,
                    0xFFFFD54A
            );

            graphics.drawString(
                    this.font,
                    "Karakter istatistikleri burada olacak.",
                    panelLeft + SIDE_WIDTH + 18,
                    panelTop + 70,
                    0xFFFFFFFF
            );
        }

        /*
         * Butonları çiz.
         */
        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    private void drawSelectedSkillInfo(
            GuiGraphics graphics,
            int panelLeft,
            int panelTop
    ) {

        int x =
                panelLeft + SIDE_WIDTH + 370;

        int y =
                panelTop + 325;

        String skillName =
                SkillLoadout.getSkillName(
                        selectedSlot
                );

        String key =
                SkillLoadout.getSkillKey(
                        selectedSlot
                );

        graphics.drawString(
                this.font,
                "Seçili: "
                        + (selectedSlot + 1)
                        + ". slot",
                x,
                y,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                skillName
                        + "  ["
                        + key
                        + "]",
                x,
                y + 16,
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