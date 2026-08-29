package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class CombatHud {

    private static final int SLOT_WIDTH = 30;
    private static final int SLOT_HEIGHT = 40;
    private static final int SLOT_GAP = 2;

    private static final int STAMINA_WIDTH = 22;
    private static final int STAMINA_GAP = 3;

    private static final float ANIMATION_DISTANCE = 18.0F;
    private static final double ANIMATION_SPEED = 0.18D;

    private static boolean lastCombatState = false;

    private static double animationProgress =
            0.0D;

    private static long lastFrameTime =
            System.nanoTime();

    private CombatHud() {
    }

    @SubscribeEvent
    public static void render(
            RenderGuiEvent.Post event
    ) {

        boolean combatActive =
                CombatState.isActive();

        if (
                combatActive
                        != lastCombatState
        ) {

            if (combatActive) {
                animationProgress =
                        0.0D;
            } else {
                animationProgress =
                        1.0D;
            }

            lastCombatState =
                    combatActive;
        }

        if (
                !combatActive
                        && animationProgress <= 0.0D
        ) {
            return;
        }

        Minecraft minecraft =
                Minecraft.getInstance();

        if (
                minecraft.player == null
                        || minecraft.screen != null
        ) {
            return;
        }

        long currentTime =
                System.nanoTime();

        double deltaSeconds =
                (
                        currentTime
                                - lastFrameTime
                )
                        / 1_000_000_000.0D;

        lastFrameTime =
                currentTime;

        deltaSeconds =
                Math.min(
                        deltaSeconds,
                        0.05D
                );

        if (combatActive) {

            animationProgress +=
                    deltaSeconds
                            / ANIMATION_SPEED;

        } else {

            animationProgress -=
                    deltaSeconds
                            / ANIMATION_SPEED;
        }

        animationProgress =
                Math.max(
                        0.0D,
                        Math.min(
                                1.0D,
                                animationProgress
                        )
                );

        double easedProgress =
                1.0D - Math.pow(
                        1.0D - animationProgress,
                        3.0D
                );

        int animationOffset =
                (int) (
                        ANIMATION_DISTANCE
                                * (
                                1.0D
                                        - easedProgress
                        )
                );

        GuiGraphics graphics =
                event.getGuiGraphics();

        int screenWidth =
                minecraft.getWindow()
                        .getGuiScaledWidth();

        int screenHeight =
                minecraft.getWindow()
                        .getGuiScaledHeight();

        int slotCount =
                SkillLoadout.getSlotCount();

        int skillWidth =
                SLOT_WIDTH * slotCount
                        + SLOT_GAP
                        * (slotCount - 1);

        int totalWidth =
                skillWidth
                        + STAMINA_GAP
                        + STAMINA_WIDTH;

        int startX =
                (
                        screenWidth
                                - totalWidth
                ) / 2;

        int y =
                screenHeight
                        - SLOT_HEIGHT
                        - 6
                        + animationOffset;

        /*
         * SKILL BAR
         */
        for (
                int slot = 0;
                slot < slotCount;
                slot++
        ) {

            int x =
                    startX
                            + (
                            SLOT_WIDTH
                                    + SLOT_GAP
                    ) * slot;

            drawSkill(
                    graphics,
                    minecraft,
                    x,
                    y,
                    SkillLoadout.getSkill(
                            slot
                    ),
                    SkillLoadout.getSkillKey(
                            slot
                    )
            );
        }

        /*
         * STAMINA BAR
         */
        int staminaX =
                startX
                        + skillWidth
                        + STAMINA_GAP;

        drawStamina(
                graphics,
                minecraft,
                staminaX,
                y
        );
    }

    private static void drawSkill(
            GuiGraphics graphics,
            Minecraft minecraft,
            int x,
            int y,
            SkillDefinition skill,
            String key
    ) {

        graphics.fill(
                x,
                y,
                x + SLOT_WIDTH,
                y + SLOT_HEIGHT,
                0xD0181818
        );

        long remaining =
                skill == null
                        ? 0L
                        : SkillCooldownClient
                                .getRemainingMillis(
                                        skill.id()
                                );

        boolean cooldown =
                remaining > 0L;

        /*
         * Cooldown arka planı.
         */
        if (cooldown) {

            graphics.fill(
                    x,
                    y,
                    x + SLOT_WIDTH,
                    y + SLOT_HEIGHT,
                    0xAA080808
            );
        }

        drawBorder(
                graphics,
                x,
                y,
                SLOT_WIDTH,
                SLOT_HEIGHT,
                cooldown
                        ? 0xFF555555
                        : 0xFF999999
        );

        if (skill == null) {

            graphics.drawCenteredString(
                    minecraft.font,
                    "?",
                    x + SLOT_WIDTH / 2,
                    y + 8,
                    0xFF666666
            );

            return;
        }

        ItemStack icon =
                skill.icon();

        if (!icon.isEmpty()) {

            graphics.renderItem(
                    icon,
                    x + 7,
                    y + 2
            );
        }

        String name =
                skill.name().length() > 4
                        ? skill.name().substring(
                                0,
                                4
                        )
                        : skill.name();

        graphics.drawCenteredString(
                minecraft.font,
                name,
                x + SLOT_WIDTH / 2,
                y + 21,
                0xFFFFFFFF
        );

        graphics.drawCenteredString(
                minecraft.font,
                key,
                x + SLOT_WIDTH / 2,
                y + 32,
                0xFFFFD54A
        );

        /*
         * Cooldown yazısı artık skillin
         * üstünde, okunabilir şekilde.
         */
        if (cooldown) {

            String timeText;

            if (remaining >= 1000L) {

                timeText =
                        String.format(
                                "%.1f",
                                remaining
                                        / 1000.0D
                        );

            } else {

                timeText =
                        String.valueOf(
                                Math.max(
                                        0L,
                                        remaining
                                                / 100L
                                )
                        );
            }

            int boxLeft =
                    x + 2;

            int boxTop =
                    y + 11;

            graphics.fill(
                    boxLeft,
                    boxTop,
                    x + SLOT_WIDTH - 2,
                    boxTop + 11,
                    0xDD000000
            );

            graphics.drawCenteredString(
                    minecraft.font,
                    timeText,
                    x + SLOT_WIDTH / 2,
                    boxTop + 1,
                    0xFFFFFFFF
            );

            /*
             * Cooldown ilerleme çizgisi
             */
            double progress =
                    SkillCooldownClient.getProgress(
                            skill.id(),
                            skill.cooldownMillis()
                    );

            int progressWidth =
                    (int) (
                            SLOT_WIDTH
                                    * progress
                    );

            if (
                    progressWidth > 0
            ) {

                graphics.fill(
                        x,
                        y + SLOT_HEIGHT - 2,
                        x + progressWidth,
                        y + SLOT_HEIGHT,
                        0xFF777777
                );
            }
        }
    }

    private static void drawStamina(
            GuiGraphics graphics,
            Minecraft minecraft,
            int x,
            int y
    ) {

        graphics.fill(
                x,
                y,
                x + STAMINA_WIDTH,
                y + SLOT_HEIGHT,
                0xD0181818
        );

        drawBorder(
                graphics,
                x,
                y,
                STAMINA_WIDTH,
                SLOT_HEIGHT,
                0xFF777777
        );

        double percentage =
                ClientStamina.getPercentage();

        int barHeight =
                SLOT_HEIGHT - 12;

        graphics.fill(
                x + 5,
                y + 5,
                x + STAMINA_WIDTH - 5,
                y + 5 + barHeight,
                0xFF303030
        );

        int filled =
                (int) (
                        barHeight
                                * percentage
                );

        if (
                filled > 0
        ) {

            int top =
                    y + 5
                            + barHeight
                            - filled;

            graphics.fill(
                    x + 5,
                    top,
                    x + STAMINA_WIDTH - 5,
                    y + 5 + barHeight,
                    0xFFFFD54A
            );
        }

        graphics.drawCenteredString(
                minecraft.font,
                "S",
                x + STAMINA_WIDTH / 2,
                y + SLOT_HEIGHT - 8,
                0xFFFFFFFF
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