package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class KaizokuMenuScreen extends Screen {

    private enum Tab {
        SKILLS,
        STATS
    }

    private static final int PANEL_WIDTH = 340;
    private static final int PANEL_HEIGHT = 230;

    private static final int SIDE_WIDTH = 70;

    private static final int SLOT_SIZE = 26;
    private static final int SLOT_GAP = 2;

    private static final int SKILL_TOP_Y = 62;
    private static final int COMBAT_Y = 148;

    private Tab currentTab = Tab.SKILLS;

    private int draggingSkill = -1;

    public KaizokuMenuScreen() {
        super(
                Component.literal(
                        "Kaizoku-ō Craft"
                )
        );
    }
    @Override
	public boolean isPauseScreen() {
	    return false;
	}

    @Override
    protected void init() {
        // Custom GUI
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        /*
         * Blur YOK.
         */
        graphics.fill(
                0,
                0,
                this.width,
                this.height,
                0x70000000
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
                0xFF666666
        );

        /*
         * Sol panel
         */
        graphics.fill(
                panelLeft,
                panelTop,
                panelLeft + SIDE_WIDTH,
                panelTop + PANEL_HEIGHT,
                0xFF0C0C0C
        );

        graphics.fill(
                panelLeft + SIDE_WIDTH,
                panelTop,
                panelLeft + SIDE_WIDTH + 1,
                panelTop + PANEL_HEIGHT,
                0xFF444444
        );

        /*
         * Başlık
         */
        graphics.drawCenteredString(
                this.font,
                "KAIzoku-Ō",
                panelLeft + PANEL_WIDTH / 2,
                panelTop + 10,
                0xFFFFFFFF
        );

        drawTab(
                graphics,
                panelLeft,
                panelTop,
                "SKİLLER",
                Tab.SKILLS,
                42
        );

        drawTab(
                graphics,
                panelLeft,
                panelTop,
                "STATLAR",
                Tab.STATS,
                76
        );

        if (
                currentTab == Tab.SKILLS
        ) {

            renderSkills(
                    graphics,
                    panelLeft,
                    panelTop,
                    mouseX,
                    mouseY
            );

        } else {

            renderStats(
                    graphics,
                    panelLeft,
                    panelTop
            );
        }

        if (
                draggingSkill != -1
        ) {

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
            int yOffset
    ) {

        boolean active =
                currentTab == tab;

        int x =
                panelLeft + 6;

        int y =
                panelTop + yOffset;

        int width =
                SIDE_WIDTH - 12;

        int height =
                26;

        if (active) {

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
                    x + 2,
                    y + height,
                    0xFFFFD54A
            );
        }

        graphics.drawCenteredString(
                this.font,
                text,
                x + width / 2,
                y + 9,
                active
                        ? 0xFFFFD54A
                        : 0xFFAAAAAA
        );
    }

    private void renderSkills(
            GuiGraphics graphics,
            int panelLeft,
            int panelTop,
            int mouseX,
            int mouseY
    ) {

        int left =
                panelLeft
                        + SIDE_WIDTH
                        + 14;

        graphics.drawString(
                this.font,
                "SKİLLER",
                left,
                panelTop + 30,
                0xFFFFD54A,
                true
        );

        int libraryIndex =
                0;

        for (
                SkillDefinition skill :
                SkillRegistry.getSkills()
        ) {

            if (
                    libraryIndex >= 9
            ) {
                break;
            }

            int x =
                    left
                            + libraryIndex
                            * (
                            SLOT_SIZE
                                    + SLOT_GAP
                    );

            int y =
                    panelTop
                            + SKILL_TOP_Y;

            drawLibrarySkill(
                    graphics,
                    skill,
                    x,
                    y,
                    mouseX,
                    mouseY
            );

            libraryIndex++;
        }

        while (
                libraryIndex < 9
        ) {

            int x =
                    left
                            + libraryIndex
                            * (
                            SLOT_SIZE
                                    + SLOT_GAP
                    );

            int y =
                    panelTop
                            + SKILL_TOP_Y;

            drawEmptyLibrarySlot(
                    graphics,
                    x,
                    y
            );

            libraryIndex++;
        }

        graphics.drawString(
                this.font,
                "COMBAT BAR",
                left,
                panelTop + 126,
                0xFFFFD54A,
                true
        );

        for (
                int slot = 0;
                slot < 9;
                slot++
        ) {

            int x =
                    left
                            + slot
                            * (
                            SLOT_SIZE
                                    + SLOT_GAP
                    );

            int y =
                    panelTop
                            + COMBAT_Y;

            drawCombatSlot(
                    graphics,
                    slot,
                    x,
                    y,
                    mouseX,
                    mouseY
            );
        }

        graphics.drawString(
                this.font,
                "Skill'i sürükleyip Combat Bar'a bırak.",
                left,
                panelTop + 184,
                0xFF777777
        );
    }

    private void drawLibrarySkill(
            GuiGraphics graphics,
            SkillDefinition skill,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {

        boolean hover =
                isInside(
                        mouseX,
                        mouseY,
                        x,
                        y,
                        SLOT_SIZE,
                        SLOT_SIZE
                );

        drawSlot(
                graphics,
                x,
                y,
                hover
        );

        ItemStack icon =
                skill.icon();

        if (!icon.isEmpty()) {

            graphics.renderItem(
                    icon,
                    x + 5,
                    y + 2
            );
        }

        graphics.drawCenteredString(
                this.font,
                getShortName(
                        skill.name()
                ),
                x + SLOT_SIZE / 2,
                y + 17,
                0xFFFFFFFF
        );
    }

    private void drawEmptyLibrarySlot(
            GuiGraphics graphics,
            int x,
            int y
    ) {

        drawSlot(
                graphics,
                x,
                y,
                false
        );

        graphics.drawCenteredString(
                this.font,
                "?",
                x + SLOT_SIZE / 2,
                y + 8,
                0xFF555555
        );
    }

    private void drawCombatSlot(
            GuiGraphics graphics,
            int slot,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {

        boolean hover =
                isInside(
                        mouseX,
                        mouseY,
                        x,
                        y,
                        SLOT_SIZE,
                        SLOT_SIZE
                );

        drawSlot(
                graphics,
                x,
                y,
                hover
        );

        SkillDefinition skill =
                SkillLoadout.getSkill(
                        slot
                );

        if (skill != null) {

            if (
                    !skill.icon()
                            .isEmpty()
            ) {

                graphics.renderItem(
                        skill.icon(),
                        x + 5,
                        y + 2
                );
            }

            graphics.drawCenteredString(
                    this.font,
                    getShortName(
                            skill.name()
                    ),
                    x + SLOT_SIZE / 2,
                    y + 17,
                    0xFFFFFFFF
            );

        } else {

            graphics.drawCenteredString(
                    this.font,
                    "?",
                    x + SLOT_SIZE / 2,
                    y + 8,
                    0xFF555555
            );
        }

        graphics.drawString(
                this.font,
                SkillLoadout.getSkillKey(
                        slot
                ),
                x + 2,
                y + 2,
                0xFFFFD54A
        );
    }

    private void drawSlot(
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
                        ? 0xFF353535
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

    private void renderStats(
            GuiGraphics graphics,
            int panelLeft,
            int panelTop
    ) {

        int x =
                panelLeft
                        + SIDE_WIDTH
                        + 14;

        int y =
                panelTop
                        + 30;

        long level =
                ClientPlayerData.getLevel();

        long experience =
                ClientPlayerData.getExperience();

        double levelPower =
                Math.sqrt(
                        Math.max(
                                1L,
                                level
                        )
                );

        double raceMultiplier =
                ClientPlayerData
                        .getRace()
                        .getDamageMultiplier();

        double finalPower =
                levelPower
                        * raceMultiplier;

        double stamina =
                ClientStamina.getStamina();

        double maxStamina =
                ClientStamina.getMaxStamina();

        double staminaPercent =
                ClientStamina.getPercentage();

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
                "Level: " + level,
                x,
                y + 24,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "XP: " + experience,
                x,
                y + 43,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                "Race: "
                        + ClientPlayerData
                                .getRace()
                                .getDisplayName(),
                x,
                y + 62,
                0xFFFFFFFF
        );

        graphics.drawString(
                this.font,
                String.format(
                        "Güç: ×%.2f",
                        finalPower
                ),
                x,
                y + 81,
                0xFFFFD54A,
                true
        );

        graphics.drawString(
                this.font,
                String.format(
                        "Hasar: ×%.2f",
                        raceMultiplier
                ),
                x,
                y + 100,
                0xFFAAAAAA
        );

        graphics.drawString(
                this.font,
                String.format(
                        "Hız: ×%.2f",
                        ClientPlayerData
                                .getRace()
                                .getSpeedMultiplier()
                ),
                x,
                y + 119,
                0xFFAAAAAA
        );

        /*
         * STAMINA
         */
        graphics.drawString(
                this.font,
                "Stamina",
                x,
                y + 141,
                0xFFFFD54A,
                true
        );

        graphics.drawString(
                this.font,
                String.format(
                        "%.0f / %.0f",
                        stamina,
                        maxStamina
                ),
                x + 58,
                y + 141,
                0xFFFFFFFF
        );

        int barX =
                x;

        int barY =
                y + 157;

        int barWidth =
                200;

        int barHeight =
                7;

        graphics.fill(
                barX,
                barY,
                barX + barWidth,
                barY + barHeight,
                0xFF303030
        );

        int filled =
                (int) (
                        barWidth
                                * staminaPercent
                );

        if (filled > 0) {

            graphics.fill(
                    barX,
                    barY,
                    barX + filled,
                    barY + barHeight,
                    0xFFFFD54A
            );
        }

        graphics.drawString(
                this.font,
                "Combat: "
                        + (
                        CombatState.isActive()
                                ? "AKTİF"
                                : "KAPALI"
                ),
                x,
                y + 178,
                CombatState.isActive()
                        ? 0xFF6CFF8A
                        : 0xFFAAAAAA
        );
    }

    private void drawDraggedSkill(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        SkillDefinition skill =
                SkillRegistry.getSkill(
                        draggingSkill
                );

        if (
                skill == null
        ) {
            return;
        }

        int x =
                mouseX
                        - SLOT_SIZE / 2;

        int y =
                mouseY
                        - SLOT_SIZE / 2;

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

        if (
                !skill.icon().isEmpty()
        ) {

            graphics.renderItem(
                    skill.icon(),
                    x + 5,
                    y + 2
            );
        }

        graphics.drawCenteredString(
                this.font,
                getShortName(
                        skill.name()
                ),
                x + SLOT_SIZE / 2,
                y + 17,
                0xFFFFFFFF
        );
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

        if (
                isInside(
                        mouseX,
                        mouseY,
                        panelLeft + 6,
                        panelTop + 42,
                        SIDE_WIDTH - 12,
                        26
                )
        ) {

            currentTab =
                    Tab.SKILLS;

            return true;
        }

        if (
                isInside(
                        mouseX,
                        mouseY,
                        panelLeft + 6,
                        panelTop + 76,
                        SIDE_WIDTH - 12,
                        26
                )
        ) {

            currentTab =
                    Tab.STATS;

            return true;
        }

        if (
                currentTab == Tab.SKILLS
                        && button == 0
        ) {

            int left =
                    panelLeft
                            + SIDE_WIDTH
                            + 14;

            int index =
                    0;

            for (
                    SkillDefinition skill :
                    SkillRegistry.getSkills()
            ) {

                if (
                        index >= 9
                ) {
                    break;
                }

                int x =
                        left
                                + index
                                * (
                                SLOT_SIZE
                                        + SLOT_GAP
                        );

                int y =
                        panelTop
                                + SKILL_TOP_Y;

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

                    draggingSkill =
                            index;

                    return true;
                }

                index++;
            }
        }

        if (
                currentTab == Tab.SKILLS
                        && button == 1
        ) {

            int left =
                    panelLeft
                            + SIDE_WIDTH
                            + 14;

            for (
                    int slot = 0;
                    slot < 9;
                    slot++
            ) {

                int x =
                        left
                                + slot
                                * (
                                SLOT_SIZE
                                        + SLOT_GAP
                        );

                int y =
                        panelTop
                                + COMBAT_Y;

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

                    SkillLoadout.clearSlot(
                            slot
                    );

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
                    (this.width
                            - PANEL_WIDTH)
                            / 2;

            int panelTop =
                    (this.height
                            - PANEL_HEIGHT)
                            / 2;

            int left =
                    panelLeft
                            + SIDE_WIDTH
                            + 14;

            for (
                    int slot = 0;
                    slot < 9;
                    slot++
            ) {

                int x =
                        left
                                + slot
                                * (
                                SLOT_SIZE
                                        + SLOT_GAP
                        );

                int y =
                        panelTop
                                + COMBAT_Y;

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

                    SkillDefinition skill =
                            SkillRegistry.getSkill(
                                    draggingSkill
                            );

                    if (
                            skill != null
                    ) {

                        SkillLoadout.setSkill(
                                slot,
                                skill.id()
                        );
                    }

                    break;
                }
            }

            draggingSkill =
                    -1;

            return true;
        }

        return super.mouseReleased(
                mouseX,
                mouseY,
                button
        );
    }

    private String getShortName(
            String name
    ) {

        if (name == null) {
            return "";
        }

        if (name.length() <= 5) {
            return name;
        }

        return name.substring(
                0,
                5
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