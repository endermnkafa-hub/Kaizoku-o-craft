package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.network.CreateCharacterPacket;
import net.mcreator.kaizokuocraft.player.FactionType;
import net.mcreator.kaizokuocraft.player.FightingStyle;
import net.mcreator.kaizokuocraft.player.RaceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

public class CharacterCreationScreen extends Screen {

    private static final int PANEL_WIDTH = 350;
    private static final int PANEL_HEIGHT = 200;

    private int selectedRaceIndex = 0;
    private int selectedFactionIndex = 0;
    private int selectedStyleIndex = 0;

    private final RaceType[] races = RaceType.values();
    private final FactionType[] factions = FactionType.values();
    private final FightingStyle[] styles = FightingStyle.values();

    private static final net.minecraft.resources.ResourceLocation CHAR_BG =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                    net.mcreator.kaizokuocraft.KaizokuOCraftMod.MODID,
                    "textures/gui/character_selection_bg.png"
            );

    public CharacterCreationScreen() {
        super(Component.literal("Karakter Seçimi"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Darkened background
        graphics.fillGradient(0, 0, this.width, this.height, 0x90000000, 0xC0000000);

        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;

        // 1. Draw High Definition One Piece Character Selection PNG Background
        graphics.blit(CHAR_BG, left, top, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, 654, 533);

        // 2. Draw Header "KARAKTER SEÇİMİ"
        drawHeader(graphics, left, top, PANEL_WIDTH);

        // 3. Draw Row 1: IRK (Race)
        int row1Y = top + 34;
        drawSelectionRow(graphics, left + 14, row1Y, "IRK",
                races[selectedRaceIndex].getDisplayName(),
                races[selectedRaceIndex].getDescription(),
                selectedRaceIndex, races.length, 0, mouseX, mouseY);

        // 4. Draw Row 2: TARAF (Faction)
        int row2Y = top + 80;
        drawSelectionRow(graphics, left + 14, row2Y, "TARAF",
                factions[selectedFactionIndex].getDisplayName(),
                factions[selectedFactionIndex].getDescription(),
                selectedFactionIndex, factions.length, 1, mouseX, mouseY);

        // 5. Draw Row 3: DÖVÜŞ YÖNTEMİ (Fighting Style)
        int row3Y = top + 126;
        drawSelectionRow(graphics, left + 14, row3Y, "DÖVÜŞ YÖNTEMİ",
                styles[selectedStyleIndex].getDisplayName(),
                styles[selectedStyleIndex].getDescription(),
                selectedStyleIndex, styles.length, 2, mouseX, mouseY);

        // 6. Draw Bottom Buttons: [KARAKTERİ OLUŞTUR] and [İPTAL]
        int btnY = top + 172;
        int btnWidth = 140;
        int btnHeight = 20;

        int createBtnX = left + 28;
        boolean createHover = mouseX >= createBtnX && mouseX <= createBtnX + btnWidth && mouseY >= btnY && mouseY <= btnY + btnHeight;
        drawStyledButton(graphics, createBtnX, btnY, btnWidth, btnHeight, "KARAKTERİ OLUŞTUR", createHover, 0xFF2ECC71, 0xFF27AE60);

        int cancelBtnX = left + PANEL_WIDTH - 28 - btnWidth;
        boolean cancelHover = mouseX >= cancelBtnX && mouseX <= cancelBtnX + btnWidth && mouseY >= btnY && mouseY <= btnY + btnHeight;
        drawStyledButton(graphics, cancelBtnX, btnY, btnWidth, btnHeight, "İPTAL", cancelHover, 0xFFE74C3C, 0xFFC0392B);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Blur shader tetiklenmemesi için boş bırakıldı
    }

    private void drawParchmentPanel(GuiGraphics graphics, int x, int y, int w, int h) {
        // Outer iron border
        graphics.fill(x - 5, y - 5, x + w + 5, y + h + 5, 0xFF3D4148);
        graphics.fill(x - 3, y - 3, x + w + 3, y + h + 3, 0xFF606670);
        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF2B2E33);

        // Parchment map fill
        graphics.fill(x, y, x + w, y + h, 0xFFD8B983);
        graphics.fillGradient(x + 2, y + 2, x + w - 2, y + h - 2, 0xFFE4CB9B, 0xFFC9A66B);

        // Subtle map compass / nautical grid accents
        graphics.fill(x + 10, y + 10, x + w - 10, y + 11, 0x308B6B3D);
        graphics.fill(x + 10, y + h - 11, x + w - 10, y + h - 10, 0x308B6B3D);

        // 4 Corner Skull & Crossbones Badges
        drawCornerBadge(graphics, x - 4, y - 4);
        drawCornerBadge(graphics, x + w - 8, y - 4);
        drawCornerBadge(graphics, x - 4, y + h - 8);
        drawCornerBadge(graphics, x + w - 8, y + h - 8);
    }

    private void drawCornerBadge(GuiGraphics graphics, int x, int y) {
        // Pirate skull / anchor corner emblem
        graphics.fill(x, y, x + 12, y + 12, 0xFF2A2D32);
        graphics.fill(x + 1, y + 1, x + 11, y + 11, 0xFFEFEFEF);
        // Eyes
        graphics.fill(x + 3, y + 4, x + 5, y + 6, 0xFF111111);
        graphics.fill(x + 7, y + 4, x + 9, y + 6, 0xFF111111);
        // Teeth
        graphics.fill(x + 4, y + 8, x + 8, y + 10, 0xFFB0B0B0);
    }

    private void drawHeader(GuiGraphics graphics, int panelX, int panelY, int panelWidth) {
        int bannerW = 200;
        int bannerH = 22;
        int bannerX = panelX + (panelWidth - bannerW) / 2;
        int bannerY = panelY - 7;

        // Wooden header plate
        graphics.fill(bannerX - 2, bannerY - 2, bannerX + bannerW + 2, bannerY + bannerH + 2, 0xFF1E1107);
        graphics.fill(bannerX - 1, bannerY - 1, bannerX + bannerW + 1, bannerY + bannerH + 1, 0xFF7A481C);
        graphics.fillGradient(bannerX, bannerY, bannerX + bannerW, bannerY + bannerH, 0xFF543111, 0xFF3D220A);

        // Gold studs on banner
        graphics.fill(bannerX + 3, bannerY + 3, bannerX + 5, bannerY + 5, 0xFFFFD700);
        graphics.fill(bannerX + bannerW - 5, bannerY + 3, bannerX + bannerW - 3, bannerY + 5, 0xFFFFD700);
        graphics.fill(bannerX + 3, bannerY + bannerH - 5, bannerX + 5, bannerY + bannerH - 3, 0xFFFFD700);
        graphics.fill(bannerX + bannerW - 5, bannerY + bannerH - 5, bannerX + bannerW - 3, bannerY + bannerH - 3, 0xFFFFD700);

        // Title text
        String title = "KARAKTER SEÇİMİ";
        int textW = this.font.width(title);
        graphics.drawString(this.font, title, bannerX + (bannerW - textW) / 2, bannerY + 7, 0xFFFFF1AA, true);
    }

    private void drawSelectionRow(GuiGraphics graphics, int x, int y, String label, String title, String desc,
                                  int index, int total, int rowIndex, int mouseX, int mouseY) {
        int rowW = PANEL_WIDTH - 28;
        int rowH = 40;

        // 1. Wooden bar background
        graphics.fill(x - 1, y - 1, x + rowW + 1, y + rowH + 1, 0xFF2A170B);
        graphics.fillGradient(x, y, x + rowW, y + rowH, 0xFF5E3917, 0xFF43260D);
        graphics.fill(x, y, x + rowW, y + 1, 0xFF8A5A2B);

        // 2. Category Label (Left vertical badge)
        int labelWidth = 65;
        graphics.fill(x + 2, y + 2, x + labelWidth, y + 14, 0xFF351B08);
        graphics.drawString(this.font, label, x + 5, y + 4, 0xFFFFD700, true);

        // 3. Navigation Buttons: [< Geri] and [İleri >]
        int btnW = 48;
        int btnH = 18;
        int prevBtnX = x + 4;
        int prevBtnY = y + 18;
        boolean prevHover = mouseX >= prevBtnX && mouseX <= prevBtnX + btnW && mouseY >= prevBtnY && mouseY <= prevBtnY + btnH;
        drawArrowButton(graphics, prevBtnX, prevBtnY, btnW, btnH, "< Geri", prevHover);

        int nextBtnX = x + rowW - btnW - 4;
        int nextBtnY = y + 11;
        boolean nextHover = mouseX >= nextBtnX && mouseX <= nextBtnX + btnW && mouseY >= nextBtnY && mouseY <= nextBtnY + btnH;
        drawArrowButton(graphics, nextBtnX, nextBtnY, btnW, btnH, "İleri >", nextHover);

        // 4. Portrait Box (Golden Frame)
        int portraitX = x + 76;
        int portraitY = y + 3;
        int portraitSize = 34;
        drawGoldenFrame(graphics, portraitX, portraitY, portraitSize, portraitSize);
        drawRowIcon(graphics, portraitX + 2, portraitY + 2, portraitSize - 4, rowIndex, index);

        // 5. Title and Subtitle Description
        int textX = portraitX + portraitSize + 6;
        graphics.drawString(this.font, title, textX, y + 6, 0xFFFFFFFF, true);
        
        // Wrap or draw description
        String shortDesc = desc;
        if (this.font.width(shortDesc) > 135) {
            shortDesc = this.font.plainSubstrByWidth(desc, 130) + "...";
        }
        graphics.drawString(this.font, shortDesc, textX, y + 20, 0xFFD8D8D8, false);
    }

    private void drawGoldenFrame(GuiGraphics graphics, int x, int y, int w, int h) {
        // Frame outer gold
        graphics.fill(x - 2, y - 2, x + w + 2, y + h + 2, 0xFF8A6200);
        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFFFFD700);
        graphics.fill(x, y, x + w, y + h, 0xFF4A3400);

        // Inner portrait canvas background
        graphics.fillGradient(x + 1, y + 1, x + w - 1, y + h - 1, 0xFF24485D, 0xFF142733);
    }

    private void drawRowIcon(GuiGraphics graphics, int x, int y, int size, int rowIndex, int index) {
        ItemStack displayItem = ItemStack.EMPTY;
        if (rowIndex == 0) {
            // IRK (Races)
            RaceType r = races[index];
            displayItem = switch (r) {
                case HUMAN -> new ItemStack(Items.LEATHER_HELMET);
                case FISH_MAN -> new ItemStack(Items.PRISMARINE_SHARD);
                case MINK -> new ItemStack(Items.LIGHTNING_ROD);
                case CYBORG -> new ItemStack(Items.IRON_CHESTPLATE);
                case GIANT -> new ItemStack(Items.NETHERITE_AXE);
                case LUNARIAN -> new ItemStack(Items.BLAZE_POWDER);
            };
        } else if (rowIndex == 1) {
            // TARAF (Factions)
            FactionType f = factions[index];
            displayItem = switch (f) {
                case PIRATE -> new ItemStack(Items.SKELETON_SKULL);
                case MARINE -> new ItemStack(Items.IRON_SWORD);
                case REVOLUTIONARY -> new ItemStack(Items.FEATHER);
                case BOUNTY_HUNTER -> new ItemStack(Items.GOLD_INGOT);
            };
        } else if (rowIndex == 2) {
            // DÖVÜŞ YÖNTEMİ (Styles)
            FightingStyle s = styles[index];
            displayItem = switch (s) {
                case FIST -> new ItemStack(Items.LEATHER);
                case SWORD -> new ItemStack(Items.DIAMOND_SWORD);
                case KICK -> new ItemStack(Items.IRON_BOOTS);
                case SNIPER -> new ItemStack(Items.BOW);
            };
        }

        if (!displayItem.isEmpty()) {
            graphics.pose().pushPose();
            graphics.pose().translate(x + 6, y + 6, 0);
            graphics.pose().scale(1.2F, 1.2F, 1.2F);
            graphics.renderItem(displayItem, 0, 0);
            graphics.pose().popPose();
        }
    }

    private void drawArrowButton(GuiGraphics graphics, int x, int y, int w, int h, String text, boolean hover) {
        int bg1 = hover ? 0xFF6D441D : 0xFF4A2B0F;
        int bg2 = hover ? 0xFF4E2D10 : 0xFF311A06;
        int border = hover ? 0xFFFFD700 : 0xFF241305;

        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, border);
        graphics.fillGradient(x, y, x + w, y + h, bg1, bg2);

        int textW = this.font.width(text);
        int color = hover ? 0xFFFFFFFF : 0xFFE0E0E0;
        graphics.drawString(this.font, text, x + (w - textW) / 2, y + (h - 8) / 2, color, true);
    }

    private void drawStyledButton(GuiGraphics graphics, int x, int y, int w, int h, String text, boolean hover, int activeColor, int darkColor) {
        int border = hover ? 0xFFFFFFFF : 0xFF1B1B1B;
        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, border);
        graphics.fillGradient(x, y, x + w, y + h, hover ? activeColor : darkColor, hover ? darkColor : 0xFF1B1B1B);

        int textW = this.font.width(text);
        graphics.drawString(this.font, text, x + (w - textW) / 2, y + (h - 8) / 2, 0xFFFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;
        int rowW = PANEL_WIDTH - 28;
        int btnW = 48;
        int btnH = 18;

        // Row 1: IRK
        int row1Y = top + 34;
        int r1PrevX = left + 14 + 4;
        int r1PrevY = row1Y + 18;
        if (mouseX >= r1PrevX && mouseX <= r1PrevX + btnW && mouseY >= r1PrevY && mouseY <= r1PrevY + btnH) {
            selectedRaceIndex = (selectedRaceIndex - 1 + races.length) % races.length;
            playClickSound();
            return true;
        }
        int r1NextX = left + 14 + rowW - btnW - 4;
        int r1NextY = row1Y + 11;
        if (mouseX >= r1NextX && mouseX <= r1NextX + btnW && mouseY >= r1NextY && mouseY <= r1NextY + btnH) {
            selectedRaceIndex = (selectedRaceIndex + 1) % races.length;
            playClickSound();
            return true;
        }

        // Row 2: TARAF
        int row2Y = top + 80;
        int r2PrevX = left + 14 + 4;
        int r2PrevY = row2Y + 18;
        if (mouseX >= r2PrevX && mouseX <= r2PrevX + btnW && mouseY >= r2PrevY && mouseY <= r2PrevY + btnH) {
            selectedFactionIndex = (selectedFactionIndex - 1 + factions.length) % factions.length;
            playClickSound();
            return true;
        }
        int r2NextX = left + 14 + rowW - btnW - 4;
        int r2NextY = row2Y + 11;
        if (mouseX >= r2NextX && mouseX <= r2NextX + btnW && mouseY >= r2NextY && mouseY <= r2NextY + btnH) {
            selectedFactionIndex = (selectedFactionIndex + 1) % factions.length;
            playClickSound();
            return true;
        }

        // Row 3: DÖVÜŞ YÖNTEMİ
        int row3Y = top + 126;
        int r3PrevX = left + 14 + 4;
        int r3PrevY = row3Y + 18;
        if (mouseX >= r3PrevX && mouseX <= r3PrevX + btnW && mouseY >= r3PrevY && mouseY <= r3PrevY + btnH) {
            selectedStyleIndex = (selectedStyleIndex - 1 + styles.length) % styles.length;
            playClickSound();
            return true;
        }
        int r3NextX = left + 14 + rowW - btnW - 4;
        int r3NextY = row3Y + 11;
        if (mouseX >= r3NextX && mouseX <= r3NextX + btnW && mouseY >= r3NextY && mouseY <= r3NextY + btnH) {
            selectedStyleIndex = (selectedStyleIndex + 1) % styles.length;
            playClickSound();
            return true;
        }

        // Bottom Button: KARAKTERİ OLUŞTUR
        int btnY = top + 172;
        int createBtnX = left + 28;
        int actionBtnW = 140;
        int actionBtnH = 20;
        if (mouseX >= createBtnX && mouseX <= createBtnX + actionBtnW && mouseY >= btnY && mouseY <= btnY + actionBtnH) {
            // Send create character packet
            PacketDistributor.sendToServer(new CreateCharacterPacket(
                    races[selectedRaceIndex],
                    factions[selectedFactionIndex],
                    styles[selectedStyleIndex]
            ));
            playClickSound();
            this.onClose();
            return true;
        }

        // Bottom Button: İPTAL
        int cancelBtnX = left + PANEL_WIDTH - 28 - actionBtnW;
        if (mouseX >= cancelBtnX && mouseX <= cancelBtnX + actionBtnW && mouseY >= btnY && mouseY <= btnY + actionBtnH) {
            playClickSound();
            this.onClose();
            return true;
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