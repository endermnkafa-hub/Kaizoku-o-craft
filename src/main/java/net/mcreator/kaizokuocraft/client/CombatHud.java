package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class CombatHud {

    /*
     * Combat skill kutuları
     */
    private static final int SLOT_WIDTH = 30;
    private static final int SLOT_HEIGHT = 40;
    private static final int SLOT_GAP = 2;

    /*
     * Stamina barı skill barının sağında
     */
    private static final int STAMINA_WIDTH = 82;
    private static final int STAMINA_HEIGHT = 12;
    private static final int STAMINA_GAP = 5;

    /*
     * Combat açılış animasyonu
     */
    private static final float ANIMATION_DISTANCE = 18.0F;
    private static final double ANIMATION_SPEED = 0.18D;

    /*
     * Combat bar ekranın en altında
     */
    private static final int BOTTOM_MARGIN = 5;

    private static boolean lastCombatState = false;

    private static double animationProgress = 0.0D;

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

        /*
         * Açılış / kapanış animasyonu
         */
        if (
                combatActive
                        != lastCombatState
        ) {

            if (combatActive) {

                animationProgress = 0.0D;

            } else {

                animationProgress = 1.0D;
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

        /*
         * Animasyon zamanlaması
         */
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
                1.0D
                        - Math.pow(
                                1.0D
                                        - animationProgress,
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

        /*
         * ==========================================
         * SKILL BAR GENİŞLİĞİ
         * ==========================================
         */
        int slotCount =
                SkillLoadout.getSlotCount();

        int skillWidth =
                SLOT_WIDTH * slotCount
                        + SLOT_GAP
                        * (slotCount - 1);

        /*
         * ÖNEMLİ:
         *
         * Skill bar'ın KENDİSİNİ merkeze alıyoruz.
         *
         * Stamina sonradan sağ tarafa ekleniyor.
         */
        int skillStartX =
                (
                        screenWidth
                                - skillWidth
                ) / 2;

        /*
         * ==========================================
         * Y KONUMU
         * ==========================================
         *
         * Can / açlık üst tarafta kalıyor.
         * Combat bar en altta.
         */
        int y =
                screenHeight
                        - SLOT_HEIGHT
                        - BOTTOM_MARGIN
                        + animationOffset;

        /*
         * ==========================================
         * SKILL SLOT'LARI
         * ==========================================
         */
        for (
                int slot = 0;
                slot < slotCount;
                slot++
        ) {

            int x =
                    skillStartX
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
         * ==========================================
         * STAMINA
         * ==========================================
         *
         * Skill barının SAĞINDA.
         */
        int staminaX =
                skillStartX
                        + skillWidth
                        + STAMINA_GAP;

        int staminaY =
                y
                        + (
                        SLOT_HEIGHT
                                - STAMINA_HEIGHT
                        ) / 2;

        drawStamina(
                graphics,
                minecraft,
                staminaX,
                staminaY
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

        /*
         * Ana arka plan
         */
        graphics.fill(
                x,
                y,
                x + SLOT_WIDTH,
                y + SLOT_HEIGHT,
                0xD0181818
        );

        /*
         * Cooldown
         */
        long remaining =
                skill == null
                        ? 0L
                        : SkillCooldownClient
                                .getRemainingMillis(
                                        skill.id()
                                );

        boolean onCooldown =
                remaining > 0L;

        /*
         * Cooldown karartması
         */
        if (onCooldown) {

            graphics.fill(
                    x,
                    y,
                    x + SLOT_WIDTH,
                    y + SLOT_HEIGHT,
                    0xBB080808
            );
        }

        drawBorder(
                graphics,
                x,
                y,
                SLOT_WIDTH,
                SLOT_HEIGHT,
                onCooldown
                        ? 0xFF666666
                        : 0xFF999999
        );

        /*
         * Boş slot
         */
        if (skill == null) {

            graphics.drawCenteredString(
                    minecraft.font,
                    "?",
                    x + SLOT_WIDTH / 2,
                    y + 9,
                    0xFF666666
            );

            return;
        }

        /*
         * İkon
         */
        ItemStack icon =
                skill.icon();

        if (
                !icon.isEmpty()
        ) {

            graphics.renderItem(
                    icon,
                    x + 7,
                    y + 2
            );
        }

        /*
         * Skill adı
         */
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

        /*
         * Kullanım tuşu
         */
        graphics.drawCenteredString(
                minecraft.font,
                key,
                x + SLOT_WIDTH / 2,
                y + 32,
                0xFFFFD54A
        );

        /*
         * Cooldown sayacı
         */
        if (onCooldown) {

            String timeText =
                    String.format(
                            "%.1f",
                            remaining / 1000.0D
                    );

            graphics.fill(
                    x + 3,
                    y + 11,
                    x + SLOT_WIDTH - 3,
                    y + 21,
                    0xDD000000
            );

            graphics.drawCenteredString(
                    minecraft.font,
                    timeText,
                    x + SLOT_WIDTH / 2,
                    y + 12,
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

        /*
         * XP bar benzeri stamina
         */
        graphics.fill(
                x,
                y,
                x + STAMINA_WIDTH,
                y + STAMINA_HEIGHT,
                0xFF181818
        );

        double percentage =
                ClientStamina.getPercentage();

        int filled =
                (int) (
                        STAMINA_WIDTH
                                * percentage
                );

        if (
                filled > 0
        ) {

            graphics.fill(
                    x,
                    y,
                    x + filled,
                    y + STAMINA_HEIGHT,
                    0xFFFFD54A
            );
        }

        /*
         * Üst ve alt çizgi
         */
        graphics.fill(
                x,
                y,
                x + STAMINA_WIDTH,
                y + 1,
                0xFFAAAAAA
        );

        graphics.fill(
                x,
                y + STAMINA_HEIGHT - 1,
                x + STAMINA_WIDTH,
                y + STAMINA_HEIGHT,
                0xFF555555
        );

        /*
         * STA yazısı
         */
        String text =
                String.format(
                        "STA %.0f/%.0f",
                        ClientStamina.getStamina(),
                        ClientStamina.getMaxStamina()
                );

        graphics.drawCenteredString(
                minecraft.font,
                text,
                x + STAMINA_WIDTH / 2,
                y - 11,
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