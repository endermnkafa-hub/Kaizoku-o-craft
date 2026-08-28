package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.player.PowerManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class KaizokuHud {

    private static final int BAR_WIDTH = 140;
    private static final int BAR_HEIGHT = 8;

    private KaizokuHud() {
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        // HUD'ı ekran menülerinin üstünde göstermiyoruz.
        if (minecraft.screen != null) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();

        long level = ClientPlayerData.getLevel();
        long experience = ClientPlayerData.getExperience();

        long requiredExperience =
                getRequiredExperience(level);

        double progress =
                Math.min(
                        1.0D,
                        (double) experience / (double) requiredExperience
                );

        double multiplier =
        PowerManager.getLevelDamageMultiplier(level);

        int x = 8;
        int y = 8;

        // Arka plan
        graphics.fill(
                x,
                y,
                x + BAR_WIDTH + 4,
                y + 42,
                0xB0000000
        );

        // Level yazısı
        graphics.drawString(
                minecraft.font,
                "Level " + level,
                x + 4,
                y + 4,
                0xFFFFFFFF,
                true
        );

        // XP bar arka planı
        int barX = x + 4;
        int barY = y + 17;

        graphics.fill(
                barX,
                barY,
                barX + BAR_WIDTH,
                barY + BAR_HEIGHT,
                0xFF333333
        );

        // XP bar doluluk
        int filledWidth =
                (int) (BAR_WIDTH * progress);

        if (filledWidth > 0) {
            graphics.fill(
                    barX,
                    barY,
                    barX + filledWidth,
                    barY + BAR_HEIGHT,
                    0xFF35C759
            );
        }

        // XP yazısı
        String xpText =
                experience + " / " + requiredExperience;

        graphics.drawString(
                minecraft.font,
                xpText,
                x + 4,
                y + 28,
                0xFFFFFFFF,
                true
        );

        // Hasar çarpanı
        String multiplierText =
                String.format("×%.2f", multiplier);

        graphics.drawString(
                minecraft.font,
                multiplierText,
                x + BAR_WIDTH - minecraft.font.width(multiplierText),
                y + 4,
                0xFFFFD54A,
                true
        );
    }

    private static long getRequiredExperience(long level) {
        return 100L + (level * 25L);
    }
}