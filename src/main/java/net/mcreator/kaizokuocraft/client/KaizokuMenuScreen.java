package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.KaizokuOCraftMod;
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
        GENEL("GENEL", "textures/gui/icon_crown_gold.png"),
        DOVUS_STILI("DÖVÜŞ\nSTİLİ", "textures/gui/icon_swords_color.png"),
        HAKI("HAKİ", "textures/gui/icon_haki_fire.png"),
        IRK("IRK", "textures/gui/icon_fishman.png"),
        MEYVE("ŞEYTAN\nMEYVESİ", "textures/gui/icon_devil_fruit_purple.png");

        private final String displayName;
        private final String iconPath;

        MenuTab(String displayName, String iconPath) {
            this.displayName = displayName;
            this.iconPath = iconPath;
        }

        public String getDisplayName() {
            return displayName;
        }

        public ResourceLocation getIcon() {
            return ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, iconPath);
        }
    }

    private static final int PANEL_WIDTH = 370;
    private static final int PANEL_HEIGHT = 205;

    private static final ResourceLocation STATS_BG =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/stats_menu_bg.png");

    private static final ResourceLocation BANNER_GENEL =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/banner_genel_istatistikler.png");
    private static final ResourceLocation BANNER_DOVUS =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/banner_dovus_stili.png");

    private static final ResourceLocation TAB_WOOD =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/tab_wood.png");
    private static final ResourceLocation TAB_SELECTED =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/tab_selected_glow.png");

    private static final ResourceLocation ICON_PUNCH_1 =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/skill_punch_1.png");
    private static final ResourceLocation ICON_PUNCH_FIRE =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/skill_punch_fire.png");
    private static final ResourceLocation ICON_PUNCH_BLACK =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/skill_punch_black.png");
    private static final ResourceLocation ICON_BRAWLER =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/skill_brawler_power.png");
    private static final ResourceLocation ICON_SWORD_SLASH =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/skill_sword_slash_gold.png");
    private static final ResourceLocation ICON_KICK =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/skill_kick_slash.png");
    private static final ResourceLocation ICON_LOCK =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/icon_lock_large.png");

    private MenuTab currentTab = MenuTab.GENEL;

    private SkillDefinition hoveredSkill = null;
    private int tooltipMouseX = 0;
    private int tooltipMouseY = 0;

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

        // 2. Draw Top Header Banner Sprite
        drawHeaderBanner(graphics, left, top, PANEL_WIDTH);

        // 3. Draw Left Navigation Tabs with real button sprites
        drawLeftTabs(graphics, left + 10, top + 26, mouseX, mouseY);

        // 4. Draw Right Content Area
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

        // 5. Render Hover Tooltip
        if (hoveredSkill != null) {
            renderSkillTooltip(graphics, hoveredSkill, tooltipMouseX, tooltipMouseY);
        }
    }

    private void drawHeaderBanner(GuiGraphics graphics, int panelX, int panelY, int panelWidth) {
        int bannerW = 160;
        int bannerH = 19;
        int bannerX = panelX + (panelWidth - bannerW) / 2;
        int bannerY = panelY - 5;

        if (currentTab == MenuTab.GENEL) {
            graphics.blit(BANNER_GENEL, bannerX, bannerY, 0, 0, bannerW, bannerH, 271, 31);
        } else if (currentTab == MenuTab.DOVUS_STILI) {
            graphics.blit(BANNER_DOVUS, bannerX, bannerY, 0, 0, bannerW, bannerH, 279, 38);
        } else {
            // Dynamic title banner
            graphics.fill(bannerX - 1, bannerY - 1, bannerX + bannerW + 1, bannerY + bannerH + 1, 0xFF1E1107);
            graphics.fillGradient(bannerX, bannerY, bannerX + bannerW, bannerY + bannerH, 0xFF543111, 0xFF3D220A);
            String title = currentTab.name() + " BİLGİLERİ";
            int textW = this.font.width(title);
            graphics.drawString(this.font, title, bannerX + (bannerW - textW) / 2, bannerY + 5, 0xFFFFF1AA, true);
        }
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

        // 1. Current Level Header Text (centered over texture box)
        String lvlTitle = "MEVCUT GENEL SEVİYE: ";
        String lvlVal = String.valueOf(level);
        int totalLvlW = this.font.width(lvlTitle) + this.font.width(lvlVal);
        int lvlStartX = x + (w - totalLvlW) / 2;
        graphics.drawString(this.font, lvlTitle, lvlStartX, y + 6, 0xFFFFFFFF, true);
        graphics.drawString(this.font, lvlVal, lvlStartX + this.font.width(lvlTitle), y + 6, 0xFFFFD700, true);

        // 2. XP Progress Bar Fill & Text
        int barY = y + 24;
        int barH = 16;
        int fillW = (int) ((w - 28) * progress);
        if (fillW > 0) {
            graphics.fillGradient(x + 3, barY + 2, x + 3 + fillW, barY + barH - 2, 0xFFE5A91E, 0xFFF5D061);
        }

        String xpText = "XP İLERLEMESİ: " + experience + "/" + requiredXp + " XP";
        int xpTextW = this.font.width(xpText);
        graphics.drawString(this.font, xpText, x + (w - 24 - xpTextW) / 2 + 2, barY + 4, 0xFFFFFFFF, true);

        // 3. Two Multiplier Cards (Damage and Defense values)
        int cardY = barY + 20;
        int cardW = (w - 6) / 2;

        // Damage multiplier text
        graphics.drawString(this.font, "HASAR ÇARPANI:", x + 24, cardY + 4, 0xFFE0E0E0, false);
        graphics.drawString(this.font, String.format("x%.1f", totalDmg), x + 24, cardY + 13, 0xFFFFD700, true);

        // Defense multiplier text
        graphics.drawString(this.font, "DEFANS ÇARPANI:", x + cardW + 30, cardY + 4, 0xFFE0E0E0, false);
        graphics.drawString(this.font, String.format("x%.1f", totalDef), x + cardW + 30, cardY + 13, 0xFFFFD700, true);
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

            drawSkillCard(graphics, cx, cy, cardW, cardH, skill, unlocked, isHover, i);
        }
    }

    private void drawSkillCard(GuiGraphics graphics, int x, int y, int w, int h, SkillDefinition skill, boolean unlocked, boolean hover, int index) {
        int border = hover ? 0xFFFFFFFF : (unlocked ? 0xFFFFD700 : 0xFF3D2512);
        int bg1 = unlocked ? (hover ? 0xFF7A4A22 : 0xFF543111) : 0xFF2A170B;
        int bg2 = unlocked ? (hover ? 0xFF543111 : 0xFF3D220A) : 0xFF1C0E06;

        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, border);
        graphics.fillGradient(x, y, x + w, y + h, bg1, bg2);

        // Icon sprite
        int iconSize = 22;
        int ix = x + 4;
        int iy = y + (h - iconSize) / 2;

        ResourceLocation skillIcon = getSkillTexture(skill.id(), unlocked);
        graphics.blit(skillIcon, ix, iy, 0, 0, iconSize, iconSize, 41, 43);

        // Texts
        int textX = ix + iconSize + 5;
        String name = skill.name();
        if (this.font.width(name) > w - textX + x - 2) {
            name = this.font.plainSubstrByWidth(name, w - textX + x - 8) + "...";
        }
        graphics.drawString(this.font, name, textX, y + 4, unlocked ? 0xFFFFFFFF : 0xFF888888, true);

        if (unlocked) {
            graphics.drawString(this.font, "§a✔ Açık §7| §b" + (int)skill.staminaCost() + " Stm", textX, y + 15, 0xFFCCCCCC, false);
            graphics.drawString(this.font, "§6" + skill.getCooldownSeconds() + "s", textX, y + 23, 0xFFE0E0E0, false);
        } else {
            graphics.drawString(this.font, "§c🔒 Kilitli", textX, y + 14, 0xFFFFAAAA, false);
            graphics.drawString(this.font, "§eLv." + (int)skill.requiredMastery() + " Mastery", textX, y + 23, 0xFFFFE57F, false);
        }
    }

    private ResourceLocation getSkillTexture(String skillId, boolean unlocked) {
        if (!unlocked) {
            return ICON_LOCK;
        }
        return switch (skillId) {
            case "punch" -> ICON_PUNCH_1;
            case "double_strike" -> ICON_PUNCH_FIRE;
            case "front_kick" -> ICON_KICK;
            case "uppercut" -> ICON_PUNCH_BLACK;
            case "heavy_punch" -> ICON_BRAWLER;
            case "shockwave" -> ICON_PUNCH_FIRE;
            case "downslam" -> ICON_BRAWLER;
            case "sword_slash", "oni_giri", "shishi_sonson" -> ICON_SWORD_SLASH;
            case "collier_kick", "concasse", "diable_jambe" -> ICON_KICK;
            default -> ICON_PUNCH_1;
        };
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

            // Draw button sprite
            if (isSelected) {
                graphics.blit(TAB_SELECTED, x, ty, 0, 0, tabW, tabH, 170, 55);
            } else {
                graphics.blit(TAB_WOOD, x, ty, 0, 0, tabW, tabH, 170, 35);
            }

            // Draw custom icon sprite
            graphics.blit(tab.getIcon(), x + 4, ty + 5, 0, 0, 20, 20, 48, 48);

            // Tab Text
            String[] lines = tab.getDisplayName().split("\n");
            int textX = x + 28;
            int textColor = isSelected ? 0xFFFFE57F : (isHover ? 0xFFFFFFFF : 0xFFE0E0E0);
            if (lines.length == 1) {
                graphics.drawString(this.font, lines[0], textX, ty + 12, textColor, true);
            } else {
                graphics.drawString(this.font, lines[0], textX, ty + 7, textColor, true);
                graphics.drawString(this.font, lines[1], textX, ty + 17, textColor, true);
            }
        }
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