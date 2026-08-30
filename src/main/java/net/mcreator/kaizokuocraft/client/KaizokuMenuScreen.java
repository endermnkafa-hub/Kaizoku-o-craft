package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.player.FightingStyle;
import net.mcreator.kaizokuocraft.player.PowerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class KaizokuMenuScreen extends Screen {

    public enum MenuTab {
        GENEL("GENEL"),
        DOVUS_STILI("DÖVÜŞ\nSTİLİ"),
        HAKI("HAKİ"),
        IRK("IRK"),
        MEYVE("ŞEYTAN\nMEYVESİ");

        private final String displayName;

        MenuTab(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final int PANEL_WIDTH = 370;
    private static final int PANEL_HEIGHT = 205;

    private MenuTab currentTab = MenuTab.GENEL;

    // Tooltip hover target
    private SkillDefinition hoveredSkill = null;
    private int tooltipMouseX = 0;
    private int tooltipMouseY = 0;

    private static final ResourceLocation STATS_BG =
            ResourceLocation.fromNamespaceAndPath(
                    net.mcreator.kaizokuocraft.KaizokuOCraftMod.MODID,
                    "textures/gui/stats_menu_bg.png"
            );

    public KaizokuMenuScreen() {
        super(Component.literal("Genel İstatistiklerim"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Blur shader tetiklenmemesi için boş bırakıldı
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        hoveredSkill = null;

        // Darkened background
        graphics.fillGradient(0, 0, this.width, this.height, 0x80000000, 0xB0000000);

        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;

        // 1. Draw High Definition One Piece GUI PNG Background
        graphics.blit(STATS_BG, left, top, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, 654, 533);

        // 2. Draw Header Banner
        drawHeader(graphics, left, top, PANEL_WIDTH, getHeaderTitle());

        // 3. Draw Left Navigation Tabs
        drawLeftTabs(graphics, left + 10, top + 26, mouseX, mouseY);

        // 4. Draw Right Content Area based on current tab
        int contentX = left + 96;
        int contentY = top + 26;
        int contentW = PANEL_WIDTH - 108;

        if (currentTab == MenuTab.GENEL) {
            renderGenelTab(graphics, contentX, contentY, contentW, mouseX, mouseY);
        } else if (currentTab == MenuTab.DOVUS_STILI) {
            renderDovusStiliTab(graphics, contentX, contentY, contentW, mouseX, mouseY);
        } else {
            renderPlaceholderTab(graphics, contentX, contentY, contentW, currentTab.name());
        }

        // 5. Render Hover Tooltip on top of everything
        if (hoveredSkill != null) {
            renderSkillTooltip(graphics, hoveredSkill, tooltipMouseX, tooltipMouseY);
        }
    }

    private String getHeaderTitle() {
        return switch (currentTab) {
            case GENEL -> "GENEL İSTATİSTİKLERİM";
            case DOVUS_STILI -> ClientPlayerData.getFightingStyle().getDisplayName() + " STİLİ";
            case HAKI -> "HAKİ GÜÇLERİ";
            case IRK -> "IRK BİLGİSİ";
            case MEYVE -> "ŞEYTAN MEYVESİ";
        };
    }

    private void renderGenelTab(GuiGraphics graphics, int x, int y, int w, int mouseX, int mouseY) {
        long level = ClientPlayerData.getLevel();
        long experience = ClientPlayerData.getExperience();
        long requiredXp = 100L + (level * 25L);
        double progress = Math.min(1.0D, (double) experience / (double) requiredXp);

        double levelDmg = PowerManager.getLevelDamageMultiplier(level);
        double raceDmg = ClientPlayerData.getRace().getDamageMultiplier();
        double totalDmg = levelDmg * raceDmg * (1.0D + ClientPlayerData.getStrength() * 0.02D);

        double raceDef = ClientPlayerData.getRace().getDefenseMultiplier();
        double totalDef = raceDef * (1.0D + ClientPlayerData.getDefense() * 0.02D);

        // 1. Current Level Header Box
        int lvlH = 20;
        graphics.fill(x - 1, y - 1, x + w + 1, y + lvlH + 1, 0xFF2A170B);
        graphics.fillGradient(x, y, x + w, y + lvlH, 0xFF5E3917, 0xFF43260D);
        graphics.fill(x, y, x + w, y + 1, 0xFF8A5A2B);

        String lvlTitle = "MEVCUT GENEL SEVİYE: ";
        String lvlVal = String.valueOf(level);
        int totalLvlW = this.font.width(lvlTitle) + this.font.width(lvlVal);
        int lvlStartX = x + (w - totalLvlW) / 2;
        graphics.drawString(this.font, lvlTitle, lvlStartX, y + 6, 0xFFFFFFFF, true);
        graphics.drawString(this.font, lvlVal, lvlStartX + this.font.width(lvlTitle), y + 6, 0xFFFFD700, true);

        // 2. XP Progress Bar Box
        int barY = y + 24;
        int barH = 16;
        graphics.fill(x - 1, barY - 1, x + w + 1, barY + barH + 1, 0xFF2A170B);
        graphics.fill(x, barY, x + w, barY + barH, 0xFF1B1B1B);

        int fillW = (int) ((w - 24) * progress);
        if (fillW > 0) {
            graphics.fillGradient(x + 2, barY + 2, x + 2 + fillW, barY + barH - 2, 0xFFE5A91E, 0xFFF5D061);
        }

        String xpText = "XP İLERLEMESİ: " + experience + "/" + requiredXp + " XP";
        int xpTextW = this.font.width(xpText);
        graphics.drawString(this.font, xpText, x + (w - 24 - xpTextW) / 2 + 2, barY + 4, 0xFFFFFFFF, true);

        int crownX = x + w - 20;
        int crownY = barY + 2;
        graphics.fill(crownX, crownY, crownX + 18, crownY + 12, 0xFF4A3510);
        graphics.fill(crownX + 1, crownY + 1, crownX + 17, crownY + 11, 0xFFFFD700);
        graphics.drawString(this.font, "XP", crownX + 4, crownY + 2, 0xFF1B1B1B, false);

        // 3. Two Multiplier Cards (Damage and Defense)
        int cardY = barY + 20;
        int cardH = 24;
        int cardW = (w - 6) / 2;

        drawStatCard(graphics, x, cardY, cardW, cardH, new ItemStack(Items.IRON_SWORD), "HASAR ÇARPANI:", String.format("x%.1f", totalDmg));
        drawStatCard(graphics, x + cardW + 6, cardY, cardW, cardH, new ItemStack(Items.SHIELD), "DEFANS ÇARPANI:", String.format("x%.1f", totalDef));

        // 4. Large Bottom Wooden Panel (Empty as requested for future additions)
        int bottomY = cardY + 28;
        int bottomH = y + PANEL_HEIGHT - 36 - bottomY;
        drawWoodenPlanksPanel(graphics, x, bottomY, w, bottomH);
    }

    private void renderDovusStiliTab(GuiGraphics graphics, int x, int y, int w, int mouseX, int mouseY) {
        FightingStyle style = ClientPlayerData.getFightingStyle();
        double currentMastery = switch (style) {
            case FIST -> ClientPlayerData.getFightingMastery();
            case SWORD -> ClientPlayerData.getSwordMastery();
            case KICK -> ClientPlayerData.getKickMastery();
            case SNIPER -> ClientPlayerData.getSniperMastery();
        };

        // 1. Mastery Level & Header Box
        int masteryH = 20;
        graphics.fill(x - 1, y - 1, x + w + 1, y + masteryH + 1, 0xFF2A170B);
        graphics.fillGradient(x, y, x + w, y + masteryH, 0xFF5E3917, 0xFF43260D);
        graphics.fill(x, y, x + w, y + 1, 0xFF8A5A2B);

        String masteryTitle = style.getDisplayName() + " USTALIĞI: ";
        String masteryVal = "Lv. " + (int) currentMastery;
        int totalMW = this.font.width(masteryTitle) + this.font.width(masteryVal);
        int mStartX = x + (w - totalMW) / 2;
        graphics.drawString(this.font, masteryTitle, mStartX, y + 6, 0xFFFFFFFF, true);
        graphics.drawString(this.font, masteryVal, mStartX + this.font.width(masteryTitle), y + 6, 0xFFFFD700, true);

        // 2. Skills Grid Area
        int gridY = y + 24;
        int gridH = y + PANEL_HEIGHT - 36 - gridY;
        drawWoodenPlanksPanel(graphics, x, gridY, w, gridH);

        List<SkillDefinition> skills = SkillRegistry.getSkillsForStyle(style);
        int cardW = 125;
        int cardH = 34;
        int gapX = 6;
        int gapY = 4;
        int startX = x + 4;
        int startY = gridY + 4;

        for (int i = 0; i < skills.size(); i++) {
            SkillDefinition skill = skills.get(i);
            int col = i % 2;
            int row = i / 2;
            int cx = startX + col * (cardW + gapX);
            int cy = startY + row * (cardH + gapY);

            if (cy + cardH > gridY + gridH) {
                break;
            }

            boolean unlocked = currentMastery >= skill.requiredMastery();
            boolean isHover = mouseX >= cx && mouseX <= cx + cardW && mouseY >= cy && mouseY <= cy + cardH;

            if (isHover) {
                hoveredSkill = skill;
                tooltipMouseX = mouseX;
                tooltipMouseY = mouseY;
            }

            drawSkillCard(graphics, cx, cy, cardW, cardH, skill, unlocked, isHover);
        }
    }

    private void drawSkillCard(GuiGraphics graphics, int x, int y, int w, int h, SkillDefinition skill, boolean unlocked, boolean hover) {
        int border = hover ? 0xFFFFFFFF : (unlocked ? 0xFFFFD700 : 0xFF3D2512);
        int bg1 = unlocked ? (hover ? 0xFF7A4A22 : 0xFF543111) : 0xFF2A170B;
        int bg2 = unlocked ? (hover ? 0xFF543111 : 0xFF3D220A) : 0xFF1C0E06;

        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, border);
        graphics.fillGradient(x, y, x + w, y + h, bg1, bg2);

        // Icon frame
        int iconSize = 24;
        int ix = x + 4;
        int iy = y + (h - iconSize) / 2;
        graphics.fill(ix - 1, iy - 1, ix + iconSize + 1, iy + iconSize + 1, unlocked ? 0xFFFFD700 : 0xFF555555);
        graphics.fill(ix, iy, ix + iconSize, iy + iconSize, 0xFF141414);

        // Item icon render
        graphics.pose().pushPose();
        graphics.pose().translate(ix + 4, iy + 4, 0);
        graphics.renderItem(skill.icon(), 0, 0);
        graphics.pose().popPose();

        // Texts
        int textX = ix + iconSize + 5;
        String name = skill.name();
        if (this.font.width(name) > w - textX + x - 2) {
            name = this.font.plainSubstrByWidth(name, w - textX + x - 8) + "...";
        }
        graphics.drawString(this.font, name, textX, y + 4, unlocked ? 0xFFFFFFFF : 0xFF888888, true);

        // Subtext / Mastery Status
        if (unlocked) {
            graphics.drawString(this.font, "§a✔ Açık §7| §b" + (int)skill.staminaCost() + " Stm", textX, y + 15, 0xFFCCCCCC, false);
            graphics.drawString(this.font, "§6" + skill.getCooldownSeconds() + "s", textX, y + 23, 0xFFE0E0E0, false);
        } else {
            graphics.drawString(this.font, "§c🔒 Kilitli", textX, y + 14, 0xFFFFAAAA, false);
            graphics.drawString(this.font, "§eLv." + (int)skill.requiredMastery() + " Mastery", textX, y + 23, 0xFFFFE57F, false);
        }
    }

    private void renderSkillTooltip(GuiGraphics graphics, SkillDefinition skill, int mx, int my) {
        List<Component> tooltipLines = new ArrayList<>();
        tooltipLines.add(Component.literal("§6§l" + skill.name()));
        tooltipLines.add(Component.literal("§7" + skill.description()));
        tooltipLines.add(Component.literal(""));
        tooltipLines.add(Component.literal("§b⚡ Stamina: §f" + (int)skill.staminaCost() + " §8| §e⏳ Bekleme: §f" + skill.getCooldownSeconds() + "s"));

        FightingStyle style = skill.style();
        double currentMastery = switch (style) {
            case FIST -> ClientPlayerData.getFightingMastery();
            case SWORD -> ClientPlayerData.getSwordMastery();
            case KICK -> ClientPlayerData.getKickMastery();
            case SNIPER -> ClientPlayerData.getSniperMastery();
        };

        if (currentMastery >= skill.requiredMastery()) {
            tooltipLines.add(Component.literal("§a✔ Gereken Ustalık: Lv. " + (int)skill.requiredMastery() + " (Açıldı)"));
        } else {
            tooltipLines.add(Component.literal("§c🔒 Gereken Ustalık: Lv. " + (int)skill.requiredMastery() + " (Mevcut: Lv. " + (int)currentMastery + ")"));
        }

        graphics.renderComponentTooltip(this.font, tooltipLines, mx + 8, my + 8);
    }

    private void drawStatCard(GuiGraphics graphics, int x, int y, int w, int h, ItemStack icon, String label, String value) {
        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF2A170B);
        graphics.fillGradient(x, y, x + w, y + h, 0xFF5E3917, 0xFF43260D);
        graphics.fill(x, y, x + w, y + 1, 0xFF8A5A2B);

        graphics.pose().pushPose();
        graphics.pose().translate(x + 3, y + 3, 0);
        graphics.pose().scale(1.1F, 1.1F, 1.1F);
        graphics.renderItem(icon, 0, 0);
        graphics.pose().popPose();

        int textX = x + 24;
        graphics.drawString(this.font, label, textX, y + 4, 0xFFE0E0E0, false);
        graphics.drawString(this.font, value, textX, y + 13, 0xFFFFD700, true);
    }

    private void drawWoodenPlanksPanel(GuiGraphics graphics, int x, int y, int w, int h) {
        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF23140A);
        graphics.fill(x, y, x + w, y + h, 0xFF43260D);

        int plankHeight = 15;
        for (int py = y; py < y + h; py += plankHeight) {
            int curH = Math.min(plankHeight, y + h - py);
            graphics.fillGradient(x, py, x + w, py + curH, 0xFF5E3917, 0xFF43260D);
            graphics.fill(x, py, x + w, py + 1, 0xFF7A4A20);
            graphics.fill(x, py + curH - 1, x + w, py + curH, 0xFF2A170B);
        }
    }

    private void renderPlaceholderTab(GuiGraphics graphics, int x, int y, int w, String tabName) {
        int bottomH = PANEL_HEIGHT - 38;
        drawWoodenPlanksPanel(graphics, x, y, w, bottomH);

        String text = tabName + " İÇERİĞİ ÇOK YAKINDA...";
        int tw = this.font.width(text);
        graphics.drawString(this.font, text, x + (w - tw) / 2, y + bottomH / 2 - 4, 0xFFFFD700, true);
    }

    private void drawLeftTabs(GuiGraphics graphics, int x, int y, int mouseX, int mouseY) {
        MenuTab[] tabs = MenuTab.values();
        int tabW = 82;
        int tabH = 32;
        int gap = 3;

        for (int i = 0; i < tabs.length; i++) {
            MenuTab tab = tabs[i];
            int ty = y + i * (tabH + gap);
            boolean isSelected = tab == currentTab;
            boolean isHover = mouseX >= x && mouseX <= x + tabW && mouseY >= ty && mouseY <= ty + tabH;

            int border = isSelected ? 0xFFFFD700 : (isHover ? 0xFF9E6530 : 0xFF2A170B);
            int bg1 = isSelected ? 0xFF8C5326 : (isHover ? 0xFF6D421C : 0xFF46270E);
            int bg2 = isSelected ? 0xFF6B3E1A : (isHover ? 0xFF523013 : 0xFF331B08);

            graphics.fill(x - 1, ty - 1, x + tabW + 1, ty + tabH + 1, border);
            graphics.fillGradient(x, ty, x + tabW, ty + tabH, bg1, bg2);
            graphics.fill(x, ty, x + tabW, ty + 1, isSelected ? 0xFFFFE57F : 0xFF7A4A20);

            drawTabIcon(graphics, x + 3, ty + 3, tab);

            String[] lines = tab.getDisplayName().split("\n");
            int textX = x + 30;
            if (lines.length == 1) {
                int textColor = isSelected ? 0xFFFFE57F : (isHover ? 0xFFFFFFFF : 0xFFE0E0E0);
                graphics.drawString(this.font, lines[0], textX, ty + 12, textColor, true);
            } else {
                int textColor = isSelected ? 0xFFFFE57F : (isHover ? 0xFFFFFFFF : 0xFFE0E0E0);
                graphics.drawString(this.font, lines[0], textX, ty + 7, textColor, true);
                graphics.drawString(this.font, lines[1], textX, ty + 17, textColor, true);
            }
        }
    }

    private void drawTabIcon(GuiGraphics graphics, int x, int y, MenuTab tab) {
        graphics.fill(x, y, x + 24, y + 24, 0xFF2A170B);
        graphics.fill(x + 1, y + 1, x + 23, y + 23, 0xFF543111);

        ItemStack icon = switch (tab) {
            case GENEL -> new ItemStack(Items.GOLDEN_HELMET);
            case DOVUS_STILI -> new ItemStack(Items.IRON_SWORD);
            case HAKI -> new ItemStack(Items.COAL);
            case IRK -> new ItemStack(Items.PRISMARINE_SHARD);
            case MEYVE -> new ItemStack(Items.CHORUS_FRUIT);
        };

        graphics.pose().pushPose();
        graphics.pose().translate(x + 4, y + 4, 0);
        graphics.pose().scale(1.0F, 1.0F, 1.0F);
        graphics.renderItem(icon, 0, 0);
        graphics.pose().popPose();
    }

    private void drawParchmentPanel(GuiGraphics graphics, int x, int y, int w, int h) {
        graphics.fill(x - 5, y - 5, x + w + 5, y + h + 5, 0xFF3D4148);
        graphics.fill(x - 3, y - 3, x + w + 3, y + h + 3, 0xFF606670);
        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF2B2E33);

        graphics.fill(x, y, x + w, y + h, 0xFFD8B983);
        graphics.fillGradient(x + 2, y + 2, x + w - 2, y + h - 2, 0xFFE4CB9B, 0xFFC9A66B);

        graphics.fill(x + 10, y + 10, x + w - 10, y + 11, 0x308B6B3D);
        graphics.fill(x + 10, y + h - 11, x + w - 10, y + h - 10, 0x308B6B3D);

        drawCornerBadge(graphics, x - 4, y - 4);
        drawCornerBadge(graphics, x + w - 8, y - 4);
        drawCornerBadge(graphics, x - 4, y + h - 8);
        drawCornerBadge(graphics, x + w - 8, y + h - 8);
    }

    private void drawCornerBadge(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 12, y + 12, 0xFF2A2D32);
        graphics.fill(x + 1, y + 1, x + 11, y + 11, 0xFFEFEFEF);
        graphics.fill(x + 3, y + 4, x + 5, y + 6, 0xFF111111);
        graphics.fill(x + 7, y + 4, x + 9, y + 6, 0xFF111111);
        graphics.fill(x + 4, y + 8, x + 8, y + 10, 0xFFB0B0B0);
    }

    private void drawHeader(GuiGraphics graphics, int panelX, int panelY, int panelWidth, String title) {
        int bannerW = 210;
        int bannerH = 22;
        int bannerX = panelX + (panelWidth - bannerW) / 2;
        int bannerY = panelY - 7;

        graphics.fill(bannerX - 2, bannerY - 2, bannerX + bannerW + 2, bannerY + bannerH + 2, 0xFF1E1107);
        graphics.fill(bannerX - 1, bannerY - 1, bannerX + bannerW + 1, bannerY + bannerH + 1, 0xFF7A481C);
        graphics.fillGradient(bannerX, bannerY, bannerX + bannerW, bannerY + bannerH, 0xFF543111, 0xFF3D220A);

        graphics.fill(bannerX + 3, bannerY + 3, bannerX + 5, bannerY + 5, 0xFFFFD700);
        graphics.fill(bannerX + bannerW - 5, bannerY + 3, bannerX + bannerW - 3, bannerY + 5, 0xFFFFD700);
        graphics.fill(bannerX + 3, bannerY + bannerH - 5, bannerX + 5, bannerY + bannerH - 3, 0xFFFFD700);
        graphics.fill(bannerX + bannerW - 5, bannerY + bannerH - 5, bannerX + bannerW - 3, bannerY + bannerH - 3, 0xFFFFD700);

        int textW = this.font.width(title);
        graphics.drawString(this.font, title, bannerX + (bannerW - textW) / 2, bannerY + 7, 0xFFFFF1AA, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;
        int tabX = left + 10;
        int tabY = top + 26;
        int tabW = 82;
        int tabH = 32;
        int gap = 3;

        MenuTab[] tabs = MenuTab.values();
        for (int i = 0; i < tabs.length; i++) {
            int ty = tabY + i * (tabH + gap);
            if (mouseX >= tabX && mouseX <= tabX + tabW && mouseY >= ty && mouseY <= ty + tabH) {
                if (currentTab != tabs[i]) {
                    currentTab = tabs[i];
                    playClickSound();
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void playClickSound() {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.level().playSound(
                    Minecraft.getInstance().player,
                    Minecraft.getInstance().player.getX(),
                    Minecraft.getInstance().player.getY(),
                    Minecraft.getInstance().player.getZ(),
                    SoundEvents.UI_BUTTON_CLICK.value(),
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }
    }
}