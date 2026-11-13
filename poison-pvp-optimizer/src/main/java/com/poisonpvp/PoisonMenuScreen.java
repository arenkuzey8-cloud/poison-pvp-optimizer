package com.poisonpvp;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public class PoisonMenuScreen extends Screen {

    private static final int PANEL_WIDTH = 540;
    private static final int PANEL_COLOR = 0xF0121724;
    private static final int PANEL_INNER = 0xFF171F30;
    private static final int ACCENT = 0xFFB56CFF;
    private static final int MUTED = 0xFF9AA7BD;

    private boolean binding;
    private Button zoomButton;
    private Button profileButton;
    private int selectedProfile;
    private Button fovButton;
    private final Map<Module, Button> moduleButtons = new LinkedHashMap<>();
    private int panelTop;
    private int panelLeft;
    private int panelBottom;
    private int panelWidth;

    public PoisonMenuScreen() {
        super(Component.literal("Poison PvP"));
    }

    @Override
    protected void init() {
        clearWidgets();
        moduleButtons.clear();

        panelWidth = Math.min(PANEL_WIDTH, this.width - 24);
        int left = (this.width - panelWidth) / 2;
        int innerWidth = panelWidth - 32;
        int columns = innerWidth >= 470 ? 4 : 3;
        int gap = 7;
        int cellWidth = (innerWidth - (columns - 1) * gap) / columns;
        int rows = (Modules.ALL.size() + columns - 1) / columns;

        int contentHeight = 28 + 25 + 10 + 25 + 8 + 26 + 9 + 20 + 8
            + rows * 27 + 10 + 21 + 10 + 24;
        int y = Math.max(10, (this.height - contentHeight) / 2);
        panelTop = y - 12;
        panelLeft = left;
        panelBottom = y + contentHeight + 12;

        Button title = Button.builder(
            Component.literal("POISON").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD)
                .append(Component.literal("  PVP").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)),
            b -> {}).bounds(left + 16, y, innerWidth, 28).build();
        title.active = false;
        addRenderableWidget(title);

        y += 33;
        Button subtitle = Button.builder(
            Component.literal("PERFORMANS MERKEZİ  /  İSTEMCİ TARAFI AYARLAR")
                .withStyle(ChatFormatting.GRAY),
            b -> {}).bounds(left + 16, y, innerWidth, 21).build();
        subtitle.active = false;
        addRenderableWidget(subtitle);

        y += 31;
        int actionGap = 7;
        int actionWidth = (innerWidth - actionGap * 3) / 4;
        profileButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            selectedProfile = (selectedProfile + 1) % Modules.PVP_PROFILES.size();
            refresh();
        }).bounds(left + 16, y, actionWidth, 25).build());

        addRenderableWidget(Button.builder(Component.literal("Uygula"), b -> {
            Modules.applyPreset(Modules.profilePreset(Modules.PVP_PROFILES.get(selectedProfile)));
            refresh();
        }).bounds(left + 16 + actionWidth + actionGap, y, actionWidth, 25).build());

        addRenderableWidget(Button.builder(Component.literal("Maks FPS"), b -> {
            Modules.applyPreset(Modules.PRESET_MAX_FPS);
            refresh();
        }).bounds(left + 16 + (actionWidth + actionGap) * 2, y, actionWidth, 25).build());

        addRenderableWidget(Button.builder(Component.literal("Tümünü Kapat"), b -> {
            Modules.applyPreset(java.util.Set.of());
            refresh();
        }).bounds(left + 16 + (actionWidth + actionGap) * 3, y, actionWidth, 25).build());

        y += 34;
        zoomButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            binding = true;
            refresh();
        }).bounds(left + 16, y, innerWidth - 132, 26).build());

        fovButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            Config.zoomFov = Config.zoomFov >= 110 ? 30 : Math.min(110, Config.zoomFov + 10);
            Config.save();
            refresh();
        }).bounds(left + 16 + innerWidth - 125, y, 125, 26).build());

        y += 35;
        Button section = Button.builder(
            Component.literal("GÖRÜNTÜ VE FPS ANAHTARLARI").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(left + 16, y, innerWidth, 20).build();
        section.active = false;
        addRenderableWidget(section);

        y += 25;
        for (int i = 0; i < Modules.ALL.size(); i++) {
            Module module = Modules.ALL.get(i);
            int col = i % columns;
            int row = i / columns;
            int x = left + 16 + col * (cellWidth + gap);
            Button btn = Button.builder(Component.empty(), b -> {
                module.setEnabled(!module.isEnabled());
                refresh();
            }).bounds(x, y + row * 27, cellWidth, 24).build();
            moduleButtons.put(module, addRenderableWidget(btn));
        }

        y += rows * 27 + 10;
        boolean c2meLoaded = FabricLoader.getInstance().isModLoaded("c2me");
        Button status = addRenderableWidget(Button.builder(
            Component.literal(c2meLoaded
                ? "● C2ME ALGILANDI  ·  Chunk yükleme iyileştirmesi"
                : "○ C2ME YÜKLÜ DEĞİL  ·  İsteğe bağlı").withStyle(ChatFormatting.GRAY),
            b -> {}).bounds(left + 16, y, innerWidth, 21).build());
        status.active = false;

        y += 31;
        addRenderableWidget(Button.builder(Component.literal("MENÜYÜ KAPAT"), b -> onClose())
            .bounds(left + panelWidth / 2 - 66, y, 132, 24).build());

        refresh();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0x99070A12);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelBottom, PANEL_COLOR);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + 3, ACCENT);
        graphics.fill(panelLeft + 1, panelTop + 3, panelLeft + panelWidth - 1,
            panelBottom - 1, PANEL_INNER);
        graphics.fill(panelLeft + 16, panelTop + 43, panelLeft + panelWidth - 16,
            panelTop + 44, 0xFF30394D);
        super.render(graphics, mouseX, mouseY, partialTick);

        if (profileButton != null) {
            graphics.renderItem(profileIcon(), profileButton.getX() + 7, profileButton.getY() + 4);
        }
        graphics.drawString(this.font, "POISON PVP  •  CLIENT SETTINGS",
            panelLeft + 18, panelTop + 9, 0xFFE8D9FF, false);
        graphics.drawString(this.font, "PROFİL", panelLeft + 18, panelTop + 68, MUTED, false);
        graphics.drawString(this.font, "AYARLAR", panelLeft + 18, panelTop + 164, MUTED, false);
    }

    private ItemStack profileIcon() {
        return switch (selectedProfile) {
            case 0 -> new ItemStack(Items.DIAMOND_SWORD);
            case 1 -> new ItemStack(Items.DIAMOND_AXE);
            case 2 -> new ItemStack(Items.MACE);
            case 3 -> new ItemStack(Items.TRIDENT);
            case 4 -> new ItemStack(Items.GOLDEN_APPLE);
            case 5 -> new ItemStack(Items.NETHERITE_HELMET);
            case 6 -> new ItemStack(Items.SPLASH_POTION);
            case 7 -> new ItemStack(Items.ENDER_PEARL);
            case 8 -> new ItemStack(Items.DIAMOND_CHESTPLATE);
            case 9 -> new ItemStack(Items.END_CRYSTAL);
            case 10 -> new ItemStack(Items.TNT_MINECART);
            default -> ItemStack.EMPTY;
        };
    }

    private void refresh() {
        profileButton.setMessage(Component.literal(Modules.PVP_PROFILES.get(selectedProfile))
            .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
        for (Map.Entry<Module, Button> entry : moduleButtons.entrySet()) {
            Module module = entry.getKey();
            Component state = Component.literal(module.isEnabled() ? "✓ " : "○ ")
                .withStyle(module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY);
            entry.getValue().setMessage(state.append(Component.literal(module.label)
                .withStyle(module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.WHITE)));
        }
        if (binding) {
            zoomButton.setMessage(Component.literal("Bir tuşa bas...  (ESC iptal / DEL sil)")
                .withStyle(ChatFormatting.YELLOW));
        } else {
            zoomButton.setMessage(Component.literal("ZOOM  ·  ")
                .withStyle(ChatFormatting.GRAY)
                .append(PoisonPvpClient.ZOOM_KEY.getTranslatedKeyMessage())
                .append(Component.literal("  ·  Değiştir")));
        }
        fovButton.setMessage(Component.literal("FOV  " + Config.zoomFov)
            .withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (binding) {
            int key = event.key();
            if (key == GLFW.GLFW_KEY_DELETE) {
                PoisonPvpClient.setZoomKey(InputConstants.UNKNOWN);
            } else if (key != GLFW.GLFW_KEY_ESCAPE) {
                PoisonPvpClient.setZoomKey(InputConstants.Type.KEYSYM.getOrCreate(key));
            }
            binding = false;
            refresh();
            return true;
        }
        if (PoisonPvpClient.MENU_KEY.matches(event)) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
