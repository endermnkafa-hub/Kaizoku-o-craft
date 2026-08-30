package net.mcreator.kaizokuocraft.client;

import net.mcreator.kaizokuocraft.KaizokuOCraftMod;
import net.mcreator.kaizokuocraft.network.CreateCharacterPacket;
import net.mcreator.kaizokuocraft.player.FactionType;
import net.mcreator.kaizokuocraft.player.FightingStyle;
import net.mcreator.kaizokuocraft.player.RaceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.PacketDistributor;

public class CharacterCreationScreen extends Screen {

    private static final int PANEL_WIDTH = 340;
    private static final int PANEL_HEIGHT = 210;

    // Textures provided by user
    private static final ResourceLocation GUI_KENARLIGI =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/guikenarligi.png");

    private static final ResourceLocation BTN_GERI =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/geri.png");
    private static final ResourceLocation BTN_ILERI =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/ileri.png");
    private static final ResourceLocation BTN_KAYDET =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/karakterikaydet.png");
    private static final ResourceLocation BTN_IPTAL =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/iptal.png");

    // Race Icons
    private static final ResourceLocation ICON_HUMAN =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/human.png");
    private static final ResourceLocation ICON_FISHMAN =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/fishman.png");
    private static final ResourceLocation ICON_MINK =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/mink.png");
    private static final ResourceLocation ICON_LUNARIAN =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/lunarian.png");

    // Faction Icons
    private static final ResourceLocation ICON_PIRATE =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/pirate.png");
    private static final ResourceLocation ICON_MARINE =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/marine.png");
    private static final ResourceLocation ICON_REVOLUTIONARY =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/revolutionaryarmy.png");

    // Fighting Style Icons
    private static final ResourceLocation ICON_FIGHTER =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/fighter.png");
    private static final ResourceLocation ICON_SWORDSMAN =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/sworsman.png");
    private static final ResourceLocation ICON_SNIPER =
            ResourceLocation.fromNamespaceAndPath(KaizokuOCraftMod.MODID, "textures/gui/sniper.png");

    private int selectedRaceIndex = 0;
    private int selectedFactionIndex = 0;
    private int selectedStyleIndex = 0;

    private final RaceType[] races = RaceType.values();
    private final FactionType[] factions = FactionType.values();
    private final FightingStyle[] styles = FightingStyle.values();

    public CharacterCreationScreen() {
        super(Component.literal("Karakter Seçimi"));
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
        // Darkened overlay
        graphics.fillGradient(0, 0, this.width, this.height, 0x85000000, 0xB5000000);

        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;

        // 1. Draw User's Frame Background (guikenarligi.png)
        graphics.blit(GUI_KENARLIGI, left, top, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, 255, 224);

        // 2. Draw Header Title Banner
        drawHeader(graphics, left, top, PANEL_WIDTH);

        // 3. Draw Row 1: IRK (Race)
        int row1Y = top + 34;
        drawSelectionRow(graphics, left + 14, row1Y, "IRK",
                races[selectedRaceIndex].getDisplayName(),
                races[selectedRaceIndex].getDescription(),
                getRaceIcon(races[selectedRaceIndex]),
                54, 55, mouseX, mouseY);

        // 4. Draw Row 2: TARAF (Faction)
        int row2Y = top + 80;
        drawSelectionRow(graphics, left + 14, row2Y, "TARAF",
                factions[selectedFactionIndex].getDisplayName(),
                factions[selectedFactionIndex].getDescription(),
                getFactionIcon(factions[selectedFactionIndex]),
                53, 53, mouseX, mouseY);

        // 5. Draw Row 3: DÖVÜŞ YÖNTEMİ (Fighting Style)
        int row3Y = top + 126;
        drawSelectionRow(graphics, left + 14, row3Y, "DÖVÜŞ YÖNTEMİ",
                styles[selectedStyleIndex].getDisplayName(),
                styles[selectedStyleIndex].getDescription(),
                getStyleIcon(styles[selectedStyleIndex]),
                62, 67, mouseX, mouseY);

        // 6. Draw Bottom Action Buttons (karakterikaydet.png & iptal.png)
        int btnY = top + 172;
        int btnW = 100;
        int btnH = 22;

        // Create Character Button
        int createBtnX = left + 35;
        graphics.blit(BTN_KAYDET, createBtnX, btnY, 0, 0, btnW, btnH, 172, 32);

        // Cancel Button
        int cancelBtnX = left + PANEL_WIDTH - 35 - btnW;
        graphics.blit(BTN_IPTAL, cancelBtnX, btnY, 0, 0, btnW, btnH, 171, 31);
    }

    private void drawHeader(GuiGraphics graphics, int panelX, int panelY, int panelWidth) {
        int bannerW = 190;
        int bannerH = 20;
        int bannerX = panelX + (panelWidth - bannerW) / 2;
        int bannerY = panelY - 5;

        // Wooden Header Banner
        graphics.fill(bannerX - 1, bannerY - 1, bannerX + bannerW + 1, bannerY + bannerH + 1, 0xFF1E1107);
        graphics.fillGradient(bannerX, bannerY, bannerX + bannerW, bannerY + bannerH, 0xFF543111, 0xFF3D220A);

        // Gold studs
        graphics.fill(bannerX + 3, bannerY + 3, bannerX + 5, bannerY + 5, 0xFFFFD700);
        graphics.fill(bannerX + bannerW - 5, bannerY + 3, bannerX + bannerW - 3, bannerY + 5, 0xFFFFD700);
        graphics.fill(bannerX + 3, bannerY + bannerH - 5, bannerX + 5, bannerY + bannerH - 3, 0xFFFFD700);
        graphics.fill(bannerX + bannerW - 5, bannerY + bannerH - 5, bannerX + bannerW - 3, bannerY + bannerH - 3, 0xFFFFD700);

        String title = "KARAKTER SEÇİMİ";
        int textW = this.font.width(title);
        graphics.drawString(this.font, title, bannerX + (bannerW - textW) / 2, bannerY + 6, 0xFFFFF1AA, true);
    }

    private void drawSelectionRow(GuiGraphics graphics, int x, int y, String label, String title, String desc,
                                  ResourceLocation icon, int iconSrcW, int iconSrcH, int mouseX, int mouseY) {
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

        // 3. Navigation Buttons (geri.png & ileri.png)
        int navBtnW = 55;
        int navBtnH = 16;

        int prevBtnX = x + 4;
        int prevBtnY = y + 18;
        graphics.blit(BTN_GERI, prevBtnX, prevBtnY, 0, 0, navBtnW, navBtnH, 170, 31);

        int nextBtnX = x + rowW - navBtnW - 4;
        int nextBtnY = y + 12;
        graphics.blit(BTN_ILERI, nextBtnX, nextBtnY, 0, 0, navBtnW, navBtnH, 170, 32);

        // 4. Portrait Box (Golden Frame)
        int portraitX = x + 72;
        int portraitY = y + 3;
        int portraitSize = 34;

        // Gold border
        graphics.fill(portraitX - 1, portraitY - 1, portraitX + portraitSize + 1, portraitY + portraitSize + 1, 0xFFFFD700);
        graphics.fill(portraitX, portraitY, portraitX + portraitSize, portraitY + portraitSize, 0xFF142733);

        // Custom Icon Sprite from user
        if (icon != null) {
            graphics.blit(icon, portraitX + 2, portraitY + 2, 0, 0, portraitSize - 4, portraitSize - 4, iconSrcW, iconSrcH);
        }

        // 5. Title and Subtitle Description
        int textX = portraitX + portraitSize + 6;
        graphics.drawString(this.font, title, textX, y + 6, 0xFFFFFFFF, true);

        String shortDesc = desc;
        if (this.font.width(shortDesc) > 130) {
            shortDesc = this.font.plainSubstrByWidth(desc, 125) + "...";
        }
        graphics.drawString(this.font, shortDesc, textX, y + 20, 0xFFD8D8D8, false);
    }

    private ResourceLocation getRaceIcon(RaceType race) {
        return switch (race) {
            case HUMAN -> ICON_HUMAN;
            case FISH_MAN -> ICON_FISHMAN;
            case MINK -> ICON_MINK;
            case LUNARIAN -> ICON_LUNARIAN;
            case CYBORG -> ICON_FIGHTER;
            case GIANT -> ICON_SWORDSMAN;
        };
    }

    private ResourceLocation getFactionIcon(FactionType faction) {
        return switch (faction) {
            case PIRATE -> ICON_PIRATE;
            case MARINE -> ICON_MARINE;
            case REVOLUTIONARY -> ICON_REVOLUTIONARY;
            case BOUNTY_HUNTER -> ICON_SNIPER;
        };
    }

    private ResourceLocation getStyleIcon(FightingStyle style) {
        return switch (style) {
            case FIST -> ICON_FIGHTER;
            case SWORD -> ICON_SWORDSMAN;
            case SNIPER -> ICON_SNIPER;
            case KICK -> ICON_FIGHTER;
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int left = (this.width - PANEL_WIDTH) / 2;
        int top = (this.height - PANEL_HEIGHT) / 2;
        int rowW = PANEL_WIDTH - 28;
        int navBtnW = 55;
        int navBtnH = 16;

        // Row 1: IRK
        int row1Y = top + 34;
        int r1PrevX = left + 14 + 4;
        int r1PrevY = row1Y + 18;
        if (mouseX >= r1PrevX && mouseX <= r1PrevX + navBtnW && mouseY >= r1PrevY && mouseY <= r1PrevY + navBtnH) {
            selectedRaceIndex = (selectedRaceIndex - 1 + races.length) % races.length;
            playClickSound();
            return true;
        }
        int r1NextX = left + 14 + rowW - navBtnW - 4;
        int r1NextY = row1Y + 12;
        if (mouseX >= r1NextX && mouseX <= r1NextX + navBtnW && mouseY >= r1NextY && mouseY <= r1NextY + navBtnH) {
            selectedRaceIndex = (selectedRaceIndex + 1) % races.length;
            playClickSound();
            return true;
        }

        // Row 2: TARAF
        int row2Y = top + 80;
        int r2PrevX = left + 14 + 4;
        int r2PrevY = row2Y + 18;
        if (mouseX >= r2PrevX && mouseX <= r2PrevX + navBtnW && mouseY >= r2PrevY && mouseY <= r2PrevY + navBtnH) {
            selectedFactionIndex = (selectedFactionIndex - 1 + factions.length) % factions.length;
            playClickSound();
            return true;
        }
        int r2NextX = left + 14 + rowW - navBtnW - 4;
        int r2NextY = row2Y + 12;
        if (mouseX >= r2NextX && mouseX <= r2NextX + navBtnW && mouseY >= r2NextY && mouseY <= r2NextY + navBtnH) {
            selectedFactionIndex = (selectedFactionIndex + 1) % factions.length;
            playClickSound();
            return true;
        }

        // Row 3: DÖVÜŞ YÖNTEMİ
        int row3Y = top + 126;
        int r3PrevX = left + 14 + 4;
        int r3PrevY = row3Y + 18;
        if (mouseX >= r3PrevX && mouseX <= r3PrevX + navBtnW && mouseY >= r3PrevY && mouseY <= r3PrevY + navBtnH) {
            selectedStyleIndex = (selectedStyleIndex - 1 + styles.length) % styles.length;
            playClickSound();
            return true;
        }
        int r3NextX = left + 14 + rowW - navBtnW - 4;
        int r3NextY = row3Y + 12;
        if (mouseX >= r3NextX && mouseX <= r3NextX + navBtnW && mouseY >= r3NextY && mouseY <= r3NextY + navBtnH) {
            selectedStyleIndex = (selectedStyleIndex + 1) % styles.length;
            playClickSound();
            return true;
        }

        // Bottom Button: KARAKTERİ OLUŞTUR
        int btnY = top + 172;
        int btnW = 100;
        int btnH = 22;
        int createBtnX = left + 35;
        if (mouseX >= createBtnX && mouseX <= createBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
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
        int cancelBtnX = left + PANEL_WIDTH - 35 - btnW;
        if (mouseX >= cancelBtnX && mouseX <= cancelBtnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
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