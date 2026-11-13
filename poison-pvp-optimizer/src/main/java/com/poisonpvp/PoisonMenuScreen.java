package com.poisonpvp;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public class PoisonMenuScreen extends Screen {

    private static final int PANEL_WIDTH = 460;
    private static final int PANEL_COLOR = 0xE6101420;
    private static final int ACCENT_COLOR = 0xFFB56CFF;

    private boolean binding;
    private Button zoomButton;
    private Button profileButton;
    private int selectedProfile;
    private Button fovButton;
    private final Map<Module, Button> moduleButtons = new LinkedHashMap<>();
    private int panelTop;
    private int panelLeft;
    private int panelBottom;

    public PoisonMenuScreen() {
        super(Component.literal("Poison PvP"));
    }

    @Override
    protected void init() {
        clearWidgets();
        moduleButtons.clear();

        int panelWidth = Math.min(PANEL_WIDTH, this.width - 20);
        int left = (this.width - panelWidth) / 2;
        int innerWidth = panelWidth - 24;
        int columns = panelWidth < 390 ? 3 : 4;
        int gap = 6;
        int cellWidth = (innerWidth - (columns - 1) * gap) / columns;
        int rows = (Modules.ALL.size() + columns - 1) / columns;

        int contentHeight = 24 + 4 + 19 + 7 + 24 + 5 + 24 + 7 + 20 + 5
            + rows * 25 + 7 + 20 + 7 + 22;
        int y = Math.max(8, (this.height - contentHeight) / 2);
        panelTop = y - 8;
        panelLeft = left;
        panelBottom = y + contentHeight + 8;

        Button title = Button.builder(
            Component.literal("POISON PVP").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(left + 12, y, innerWidth, 24).build();
        title.active = false;
        addRenderableWidget(title);

        y += 28;
        Button subtitle = Button.builder(
            Component.literal("GÖRSEL AYARLAR  •  FPS PROFİLLERİ").withStyle(ChatFormatting.GRAY),
            b -> {}).bounds(left + 12, y, innerWidth, 19).build();
        subtitle.active = false;
        addRenderableWidget(subtitle);

        y += 26;
        int actionGap = 6;
        int actionWidth = (innerWidth - actionGap * 3) / 4;
        profileButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            selectedProfile = (selectedProfile + 1) % Modules.PVP_PROFILES.size();
            refresh();
        }).bounds(left + 12, y, actionWidth, 24).build());

        addRenderableWidget(Button.builder(Component.literal("Profili Uygula"), b -> {
            Modules.applyPreset(Modules.profilePreset(Modules.PVP_PROFILES.get(selectedProfile)));
            refresh();
        }).bounds(left + 12 + actionWidth + actionGap, y, actionWidth, 24).build());

        addRenderableWidget(Button.builder(Component.literal("Maks FPS"), b -> {
            Modules.applyPreset(Modules.PRESET_MAX_FPS);
            refresh();
        }).bounds(left + 12 + (actionWidth + actionGap) * 2, y, actionWidth, 24).build());

        addRenderableWidget(Button.builder(Component.literal("Hepsini Kapat"), b -> {
            Modules.applyPreset(java.util.Set.of());
            refresh();
        }).bounds(left + 12 + (actionWidth + actionGap) * 3, y, actionWidth, 24).build());

        y += 29;
        zoomButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            binding = true;
            refresh();
        }).bounds(left + 12, y, innerWidth - 116, 24).build());

        fovButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            Config.zoomFov = Config.zoomFov >= 110 ? 30 : Math.min(110, Config.zoomFov + 10);
            Config.save();
            refresh();
        }).bounds(left + 12 + innerWidth - 110, y, 110, 24).build());

        y += 30;
        Button section = Button.builder(
            Component.literal("PERFORMANS ANAHTARLARI").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(left + 12, y, innerWidth, 20).build();
        section.active = false;
        addRenderableWidget(section);

        y += 25;
        for (int i = 0; i < Modules.ALL.size(); i++) {
            Module module = Modules.ALL.get(i);
            int col = i % columns;
            int row = i / columns;
            int x = left + 12 + col * (cellWidth + gap);
            Button btn = Button.builder(Component.empty(), b -> {
                module.setEnabled(!module.isEnabled());
                refresh();
            }).bounds(x, y + row * 25, cellWidth, 22).build();
            moduleButtons.put(module, addRenderableWidget(btn));
        }

        y += rows * 25 + 7;
        boolean c2meLoaded = FabricLoader.getInstance().isModLoaded("c2me");
        Button c2meStatus = addRenderableWidget(Button.builder(
            Component.literal(c2meLoaded
                ? "C2ME aktif  •  Chunk yükleme optimizasyonu"
                : "C2ME yok  •  İsteğe bağlı mod").withStyle(ChatFormatting.GRAY),
            b -> {}).bounds(left + 12, y, innerWidth, 20).build());
        c2meStatus.active = false;

        y += 27;
        addRenderableWidget(Button.builder(Component.literal("Kapat"), b -> onClose())
            .bounds(left + panelWidth / 2 - 55, y, 110, 22).build());

        refresh();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(panelLeft, panelTop, panelLeft + Math.min(PANEL_WIDTH, this.width - 20),
            panelBottom, PANEL_COLOR);
        graphics.fill(panelLeft, panelTop, panelLeft + Math.min(PANEL_WIDTH, this.width - 20),
            panelTop + 2, ACCENT_COLOR);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void refresh() {
        profileButton.setMessage(Component.literal("Mod: " + Modules.PVP_PROFILES.get(selectedProfile))
            .withStyle(ChatFormatting.LIGHT_PURPLE));
        for (Map.Entry<Module, Button> entry : moduleButtons.entrySet()) {
            Module module = entry.getKey();
            entry.getValue().setMessage(Component.literal(module.label)
                .withStyle(module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.WHITE));
        }
        if (binding) {
            zoomButton.setMessage(Component.literal("Tuşa bas... (ESC iptal, DEL sil)")
                .withStyle(ChatFormatting.YELLOW));
        } else {
            zoomButton.setMessage(Component.literal("Zoom: ")
                .append(PoisonPvpClient.ZOOM_KEY.getTranslatedKeyMessage())
                .append(Component.literal("  •  Değiştir")));
        }
        fovButton.setMessage(Component.literal("Zoom FOV: " + Config.zoomFov));
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
