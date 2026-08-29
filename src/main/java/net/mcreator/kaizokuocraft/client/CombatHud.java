package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class CombatHud {

    private static final int SLOT_WIDTH = 34;
    private static final int SLOT_HEIGHT = 44;
    private static final int SLOT_GAP = 3;

    private static final float ANIMATION_DISTANCE = 18.0F;
    private static final double ANIMATION_SPEED = 0.18D;

    private static boolean lastCombatState = false;
    private static double animationProgress = 0.0D;
    private static long lastFrameTime = System.nanoTime();

    private CombatHud() {
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {

        boolean combatActive = CombatState.isActive();

        if (combatActive != lastCombatState) {

            if (combatActive) {
                animationProgress = 0.0D;
            } else {
                animationProgress = 1.0D;
            }

            lastCombatState = combatActive;
        }

        if (!combatActive && animationProgress <= 0.0D) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.screen != null) {
            return;
        }

        long currentTime = System.nanoTime();

        double deltaSeconds =
                (currentTime - lastFrameTime) / 1_000_000_000.0D;

        lastFrameTime = currentTime;

        deltaSeconds =
                Math.min(deltaSeconds, 0.05D);

        if (combatActive) {
            animationProgress +=
                    deltaSeconds / ANIMATION_SPEED;
        } else {
            animationProgress -=
                    deltaSeconds / ANIMATION_SPEED;
        }

        animationProgress =
                Math.max(
                        0.0D,
                        Math.min(1.0D, animationProgress)
                );

        double easedProgress =
                1.0D - Math.pow(
                        1.0D - animationProgress,
                        3.0D
                );

        int animationOffset =
                (int) (
                        ANIMATION_DISTANCE
                                * (1.0D - easedProgress)
                );

        GuiGraphics graphics =
                event.getGuiGraphics();

        int screenWidth =
                minecraft.getWindow().getGuiScaledWidth();

        int screenHeight =
                minecraft.getWindow().getGuiScaledHeight();

        int slotCount =
                SkillLoadout.getSlotCount();

        int totalWidth =
                (SLOT_WIDTH * slotCount)
                        + (SLOT_GAP * (slotCount - 1));

        int startX =
                (screenWidth - totalWidth) / 2;

        int y =
                screenHeight
                        - SLOT_HEIGHT
                        - 6
                        + animationOffset;

        for (int slot = 0; slot < slotCount; slot++) {

            int x =
                    startX
                            + (SLOT_WIDTH + SLOT_GAP) * slot;

            drawSkill(
                    graphics,
                    minecraft,
                    x,
                    y,
                    SkillLoadout.getSkillName(slot),
                    SkillLoadout.getSkillKey(slot),
                    SkillLoadout.getSkillIcon(slot)
            );
        }
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

        graphics.fill(
                x + 1,
                y + 1,
                x + SLOT_WIDTH + 1,
                y + SLOT_HEIGHT + 1,
                0x80000000
        );

        graphics.fill(
                x,
                y,
                x + SLOT_WIDTH,
                y + SLOT_HEIGHT,
                0xD0181818
        );

        drawBorder(
                graphics,
                x,
                y,
                SLOT_WIDTH,
                SLOT_HEIGHT,
                0xFF999999
        );

        if (!icon.isEmpty()) {

            int iconX =
                    x + (SLOT_WIDTH - 16) / 2;

            int iconY =
                    y + 3;

            graphics.renderItem(
                    icon,
                    iconX,
                    iconY
            );

        } else {

            String emptyText = "?";

            int textWidth =
                    minecraft.font.width(emptyText);

            graphics.drawString(
                    minecraft.font,
                    emptyText,
                    x + (SLOT_WIDTH - textWidth) / 2,
                    y + 6,
                    0xFF666666,
                    false
            );
        }

        String displayName =
                name.length() > 6
                        ? name.substring(0, 6)
                        : name;

        int nameWidth =
                minecraft.font.width(displayName);

        graphics.drawString(
                minecraft.font,
                displayName,
                x + (SLOT_WIDTH - nameWidth) / 2,
                y + 23,
                0xFFFFFFFF,
                true
        );

        int keyWidth =
                minecraft.font.width(key);

        graphics.drawString(
                minecraft.font,
                key,
                x + (SLOT_WIDTH - keyWidth) / 2,
                y + 35,
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