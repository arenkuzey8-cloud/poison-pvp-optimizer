package com.poisonpvp;

import com.mojang.blaze3d.platform.InputConstants;
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
    private Button fovButton;
    private final Map<Module, Button> moduleButtons = new LinkedHashMap<>();

    public PoisonMenuScreen() {
        super(Component.literal("Poison PVP Optimizer"));
    }

    @Override
    protected void init() {
        moduleButtons.clear();
        int cx = this.width / 2;
        int y = Math.max(8, this.height / 2 - 105);

        Button title = Button.builder(
            Component.literal("Poison PVP Optimizer").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(cx - 170, y, 340, 20).build();
        title.active = false;
        this.addRenderableWidget(title);

        y += 24;
        this.addRenderableWidget(Button.builder(Component.literal("Maks FPS"), b -> {
            Modules.applyPreset(Modules.PRESET_MAX_FPS);
            refresh();
        }).bounds(cx - 170, y, 110, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("PvP Modu"), b -> {
            Modules.applyPreset(Modules.PRESET_PVP);
            refresh();
        }).bounds(cx - 55, y, 110, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Hepsini Kapat"), b -> {
            Modules.applyPreset(java.util.Set.of());
            refresh();
        }).bounds(cx + 60, y, 110, 20).build());

        y += 24;
        zoomButton = this.addRenderableWidget(Button.builder(Component.empty(), b -> {
            binding = true;
            refresh();
        }).bounds(cx - 170, y, 230, 20).build());
        fovButton = this.addRenderableWidget(Button.builder(Component.empty(), b -> {
            Config.zoomFov = Config.zoomFov >= 50 ? 30 : Config.zoomFov + 10;
            Config.save();
            refresh();
        }).bounds(cx + 65, y, 105, 20).build());

        y += 24;
        int i = 0;
        for (Module m : Modules.ALL) {
            int col = i % 3;
            int row = i / 3;
            Button btn = Button.builder(Component.empty(), b -> {
                m.setEnabled(!m.isEnabled());
                refresh();
            }).bounds(cx - 170 + col * 115, y + row * 22, 110, 20).build();
            moduleButtons.put(m, this.addRenderableWidget(btn));
            i++;
        }
        int rows = (Modules.ALL.size() + 2) / 3;
        y += rows * 22 + 6;

        this.addRenderableWidget(Button.builder(Component.literal("Kapat"), b -> this.onClose())
            .bounds(cx - 50, y, 100, 20).build());

        refresh();
    }

    private void refresh() {
        for (Map.Entry<Module, Button> e : moduleButtons.entrySet()) {
            Module m = e.getKey();
            e.getValue().setMessage(Component.literal(m.label)
                .withStyle(m.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.WHITE));
        }
        if (binding) {
            zoomButton.setMessage(Component.literal("Bir tuşa bas... (ESC iptal, DEL sil)")
                .withStyle(ChatFormatting.YELLOW));
        } else {
            zoomButton.setMessage(Component.literal("Zoom Tuşu: ")
                .append(PoisonPvpClient.ZOOM_KEY.getTranslatedKeyMessage())
                .append(Component.literal("  [tıkla]")));
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
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    // Menu acikken oyun durmasin (PvP icin).
    public boolean isPauseScreen() {
        return false;
    }
}
