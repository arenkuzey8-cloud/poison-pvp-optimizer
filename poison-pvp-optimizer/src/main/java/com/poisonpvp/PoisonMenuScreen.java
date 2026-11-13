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

    private boolean binding;
    private Button zoomButton;
    private Button profileButton;
    private int selectedProfile;
    private Button fovButton;
    private final Map<Module, Button> moduleButtons = new LinkedHashMap<>();

    private static final String[] PROFILE_MARKS = {
        "⚔", "⚒", "✦", "♆", "★", "◈", "⚗", "◉", "◇", "✧", "▣"
    };

    public PoisonMenuScreen() {
        super(Component.literal("Poison PvP"));
    }

    @Override
    protected void init() {
        clearWidgets();
        moduleButtons.clear();

        int contentWidth = Math.min(520, this.width - 24);
        int left = (this.width - contentWidth) / 2;
        int top = Math.max(8, (this.height - 286) / 2);
        int gap = 7;
        int columns = contentWidth >= 480 ? 4 : 3;
        int cellWidth = (contentWidth - gap * (columns - 1)) / columns;

        Button title = Button.builder(
            Component.literal("✦  POISON PVP  ✦")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(left, top, contentWidth, 27).build();
        title.active = false;
        addRenderableWidget(title);

        int y = top + 31;
        Button subtitle = Button.builder(
            Component.literal("GÖRÜNTÜ  •  PERFORMANS  •  PROFİLLER")
                .withStyle(ChatFormatting.GRAY),
            b -> {}).bounds(left, y, contentWidth, 20).build();
        subtitle.active = false;
        addRenderableWidget(subtitle);

        y += 25;
        int actionWidth = (contentWidth - gap * 3) / 4;
        profileButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            selectedProfile = (selectedProfile + 1) % Modules.PVP_PROFILES.size();
            refresh();
        }).bounds(left, y, actionWidth, 25).build());

        addRenderableWidget(Button.builder(Component.literal("UYGULA"), b -> {
            Modules.applyPreset(Modules.profilePreset(Modules.PVP_PROFILES.get(selectedProfile)));
            refresh();
        }).bounds(left + actionWidth + gap, y, actionWidth, 25).build());

        addRenderableWidget(Button.builder(Component.literal("MAKS FPS"), b -> {
            Modules.applyPreset(Modules.PRESET_MAX_FPS);
            refresh();
        }).bounds(left + (actionWidth + gap) * 2, y, actionWidth, 25).build());

        addRenderableWidget(Button.builder(Component.literal("TÜMÜNÜ KAPAT"), b -> {
            Modules.applyPreset(java.util.Set.of());
            refresh();
        }).bounds(left + (actionWidth + gap) * 3, y, actionWidth, 25).build());

        y += 30;
        zoomButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            binding = true;
            refresh();
        }).bounds(left, y, contentWidth - 116, 24).build());

        fovButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            Config.zoomFov = Config.zoomFov >= 110 ? 30 : Math.min(110, Config.zoomFov + 10);
            Config.save();
            refresh();
        }).bounds(left + contentWidth - 109, y, 109, 24).build());

        y += 29;
        Button section = Button.builder(
            Component.literal("—  GÖRÜNTÜ VE FPS AYARLARI  —")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(left, y, contentWidth, 20).build();
        section.active = false;
        addRenderableWidget(section);

        y += 24;
        for (int i = 0; i < Modules.ALL.size(); i++) {
            Module module = Modules.ALL.get(i);
            int col = i % columns;
            int row = i / columns;
            Button button = Button.builder(Component.empty(), b -> {
                module.setEnabled(!module.isEnabled());
                refresh();
            }).bounds(left + col * (cellWidth + gap), y + row * 25, cellWidth, 23).build();
            moduleButtons.put(module, addRenderableWidget(button));
        }

        int rows = (Modules.ALL.size() + columns - 1) / columns;
        y += rows * 25 + 5;
        boolean c2meLoaded = FabricLoader.getInstance().isModLoaded("c2me");
        Button status = addRenderableWidget(Button.builder(
            Component.literal(c2meLoaded
                ? "● C2ME AKTİF  ·  Chunk yükleme desteği"
                : "○ C2ME YOK  ·  İsteğe bağlı")
                .withStyle(ChatFormatting.GRAY),
            b -> {}).bounds(left, y, contentWidth, 20).build());
        status.active = false;

        y += 25;
        addRenderableWidget(Button.builder(
            Component.literal("KAPAT").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
            b -> onClose()).bounds(this.width / 2 - 55, y, 110, 23).build());

        refresh();
    }

    private void refresh() {
        String profile = Modules.PVP_PROFILES.get(selectedProfile);
        profileButton.setMessage(Component.literal(PROFILE_MARKS[selectedProfile] + "  " + profile)
            .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        for (Map.Entry<Module, Button> entry : moduleButtons.entrySet()) {
            Module module = entry.getKey();
            entry.getValue().setMessage(Component.literal(
                (module.isEnabled() ? "✓  " : "○  ") + module.label)
                .withStyle(module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.WHITE));
        }

        if (binding) {
            zoomButton.setMessage(Component.literal("BİR TUŞA BAS...  (ESC İPTAL / DEL SİL)")
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
