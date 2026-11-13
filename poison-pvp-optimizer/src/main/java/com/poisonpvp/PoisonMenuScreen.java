package com.poisonpvp;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

public class PoisonMenuScreen extends Screen {

    private static final int MAX_MENU_WIDTH = 560;
    private static final int OUTER_GAP = 12;
    private static final int COLUMN_GAP = 7;
    private static final int MODULE_ROW_HEIGHT = 22;

    private static final String[] PROFILE_MARKS = {
        "⚔", "⚒", "✦", "♆", "★", "◈", "⚗", "◉", "◇", "✧", "▣"
    };

    private boolean binding;
    private Button zoomButton;
    private Button profileButton;
    private Button fovButton;
    private int selectedProfile;
    private final Map<Module, Button> moduleButtons = new LinkedHashMap<>();

    public PoisonMenuScreen() {
        super(Component.literal("Poison PvP"));
    }

    @Override
    protected void init() {
        clearWidgets();
        moduleButtons.clear();

        int width = Math.max(220, Math.min(MAX_MENU_WIDTH, this.width - OUTER_GAP * 2));
        int left = (this.width - width) / 2;
        int columns = width >= 500 ? 4 : width >= 350 ? 3 : 2;
        int rows = (Modules.ALL.size() + columns - 1) / columns;
        int cellWidth = (width - COLUMN_GAP * (columns - 1)) / columns;

        int contentHeight = 27 + 4 + 18 + 6 + 23 + 6 + 22 + 6 + 18 + 5
            + rows * MODULE_ROW_HEIGHT + 5 + 18 + 6 + 22;
        int top = Math.max(6, (this.height - contentHeight) / 2);
        int y = top;

        Button title = Button.builder(
            Component.literal("✦  POISON PVP  ✦")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(left, y, width, 27).build();
        title.active = false;
        addRenderableWidget(title);

        y += 31;
        Button subtitle = Button.builder(
            Component.literal("GÖRÜNTÜ  •  PERFORMANS  •  PROFİL YÖNETİMİ")
                .withStyle(ChatFormatting.GRAY),
            b -> {}).bounds(left, y, width, 18).build();
        subtitle.active = false;
        addRenderableWidget(subtitle);

        y += 24;
        int actionWidth = (width - COLUMN_GAP * 3) / 4;
        profileButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            selectedProfile = (selectedProfile + 1) % Modules.PVP_PROFILES.size();
            refresh();
        }).bounds(left, y, actionWidth, 23).build());

        addRenderableWidget(Button.builder(Component.literal("UYGULA"), b -> {
            Modules.applyPreset(Modules.profilePreset(Modules.PVP_PROFILES.get(selectedProfile)));
            refresh();
        }).bounds(left + actionWidth + COLUMN_GAP, y, actionWidth, 23).build());

        addRenderableWidget(Button.builder(Component.literal("MAKS FPS"), b -> {
            Modules.applyPreset(Modules.PRESET_MAX_FPS);
            refresh();
        }).bounds(left + (actionWidth + COLUMN_GAP) * 2, y, actionWidth, 23).build());

        addRenderableWidget(Button.builder(Component.literal("SIFIRLA"), b -> {
            Modules.applyPreset(java.util.Set.of());
            refresh();
        }).bounds(left + (actionWidth + COLUMN_GAP) * 3, y, actionWidth, 23).build());

        y += 29;
        zoomButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            binding = true;
            refresh();
        }).bounds(left, y, width - 112, 22).build());

        fovButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            Config.zoomFov = Config.zoomFov >= 110 ? 30 : Math.min(110, Config.zoomFov + 10);
            Config.save();
            refresh();
        }).bounds(left + width - 105, y, 105, 22).build());

        y += 28;
        Button section = Button.builder(
            Component.literal("—  GÖRÜNTÜ VE FPS AYARLARI  —")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(left, y, width, 18).build();
        section.active = false;
        addRenderableWidget(section);

        y += 23;
        for (int i = 0; i < Modules.ALL.size(); i++) {
            Module module = Modules.ALL.get(i);
            int col = i % columns;
            int row = i / columns;
            int x = left + col * (cellWidth + COLUMN_GAP);
            Button button = Button.builder(Component.empty(), b -> {
                module.setEnabled(!module.isEnabled());
                refresh();
            }).bounds(x, y + row * MODULE_ROW_HEIGHT, cellWidth, 20).build();
            moduleButtons.put(module, addRenderableWidget(button));
        }

        y += rows * MODULE_ROW_HEIGHT + 5;
        boolean c2meLoaded = FabricLoader.getInstance().isModLoaded("c2me");
        Button status = addRenderableWidget(Button.builder(
            Component.literal(c2meLoaded
                ? "● C2ME ALGILANDI  ·  Chunk yükleme desteği"
                : "○ C2ME YÜKLÜ DEĞİL  ·  İsteğe bağlı")
                .withStyle(ChatFormatting.GRAY),
            b -> {}).bounds(left, y, width, 18).build());
        status.active = false;

        y += 24;
        addRenderableWidget(Button.builder(
            Component.literal("MENÜYÜ KAPAT").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
            b -> onClose()).bounds(this.width / 2 - 58, y, 116, 22).build());

        refresh();
    }

    private void refresh() {
        String profile = Modules.PVP_PROFILES.get(selectedProfile);
        profileButton.setMessage(Component.literal(PROFILE_MARKS[selectedProfile] + "  " + profile)
            .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        for (Map.Entry<Module, Button> entry : moduleButtons.entrySet()) {
            Module module = entry.getKey();
            boolean enabled = module.isEnabled();
            entry.getValue().setMessage(Component.literal(
                (enabled ? "✓  " : "○  ") + module.label)
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.WHITE));
        }

        if (binding) {
            zoomButton.setMessage(Component.literal("BİR TUŞA BAS  ·  ESC İPTAL  ·  DEL SİL")
                .withStyle(ChatFormatting.YELLOW));
        } else {
            zoomButton.setMessage(Component.literal("ZOOM  ·  ")
                .withStyle(ChatFormatting.GRAY)
                .append(PoisonPvpClient.ZOOM_KEY.getTranslatedKeyMessage())
                .append(Component.literal("  ·  DEĞİŞTİR")));
        }
        fovButton.setMessage(Component.literal("FOV  " + Config.zoomFov)
            .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
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
