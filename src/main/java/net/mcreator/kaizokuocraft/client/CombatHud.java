package net.mcreator.kaizokuocraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class CombatHud {

    private static final int SLOT_WIDTH = 34;
    private static final int SLOT_HEIGHT = 44;
    private static final int SLOT_GAP = 3;

    private static final float ANIMATION_DISTANCE = 18.0F;
    private static final double ANIMATION_SPEED = 0.010D;

    private static boolean lastCombatState = false;
    private static double animationProgress = 0.0D;
    private static long lastFrameTime = System.nanoTime();

    private CombatHud() {
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {

        boolean combatActive = CombatState.isActive();

        /*
         * Combat Mode açıldı/kapatıldıysa animasyonu
         * başlangıç durumuna getir.
         */
        if (combatActive != lastCombatState) {

            if (combatActive) {
                animationProgress = 0.0D;
            } else {
                animationProgress = 1.0D;
            }

            lastCombatState = combatActive;
        }

        /*
         * Combat kapalı ve kapanma animasyonu tamamlandıysa
         * hiçbir şey çizme.
         */
        if (!combatActive && animationProgress <= 0.0D) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.screen != null) {
            return;
        }

        /*
         * Geçen gerçek zamanı hesapla.
         * Böylece animasyon FPS'e bağlı olmaz.
         */
        long currentTime = System.nanoTime();

        double deltaSeconds =
                (currentTime - lastFrameTime) / 1_000_000_000.0D;

        lastFrameTime = currentTime;

        /*
         * Çok büyük frame aralıklarında animasyonun
         * fırlamasını engelle.
         */
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

        /*
         * Smooth easing.
         *
         * Başlangıçta hızlı,
         * sona yaklaşırken yavaşlar.
         */
        double easedProgress =
                1.0D - Math.pow(
                        1.0D - animationProgress,
                        3.0D
                );

        /*
         * Skill bar aşağıdan başlayıp yukarı çıkar.
         *
         * Combat açık:
         * 18 px aşağı → normal konum
         */
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

        int slotCount = 9;

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

        /*
         * 1 — Punch
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
         * 2 — Empty
         */
        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP),
                y,
                "Empty",
                "X",
                ItemStack.EMPTY
        );

        /*
         * 3 — Empty
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

        /*
         * 4 — Empty
         */
        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 3,
                y,
                "Empty",
                "V",
                ItemStack.EMPTY
        );

        /*
         * 5 — Empty
         */
        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 4,
                y,
                "Empty",
                "B",
                ItemStack.EMPTY
        );

        /*
         * 6 — Empty
         */
        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 5,
                y,
                "Empty",
                "N",
                ItemStack.EMPTY
        );

        /*
         * 7 — Empty
         */
        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 6,
                y,
                "Empty",
                "1",
                ItemStack.EMPTY
        );

        /*
         * 8 — Empty
         */
        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 7,
                y,
                "Empty",
                "2",
                ItemStack.EMPTY
        );

        /*
         * 9 — Empty
         */
        drawSkill(
                graphics,
                minecraft,
                startX + (SLOT_WIDTH + SLOT_GAP) * 8,
                y,
                "Empty",
                "3",
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
         * Hafif gölge
         */
        graphics.fill(
                x + 1,
                y + 1,
                x + SLOT_WIDTH + 1,
                y + SLOT_HEIGHT + 1,
                0x80000000
        );

        /*
         * Arka plan
         */
        graphics.fill(
                x,
                y,
                x + SLOT_WIDTH,
                y + SLOT_HEIGHT,
                0xD0181818
        );

        /*
         * Kenarlık
         */
        drawBorder(
                graphics,
                x,
                y,
                SLOT_WIDTH,
                SLOT_HEIGHT,
                0xFF999999
        );

        /*
         * İkon
         */
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

        /*
         * Skill adı
         */
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

        /*
         * Kullanım tuşu
         */
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