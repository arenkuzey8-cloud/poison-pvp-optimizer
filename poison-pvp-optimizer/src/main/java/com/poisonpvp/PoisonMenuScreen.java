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
import java.util.Set;

public class PoisonMenuScreen extends Screen {

    private boolean binding;
    private Button zoomButton;
    private Button fovButton;
    private final Map<Module, Button> moduleButtons = new LinkedHashMap<>();

    public PoisonMenuScreen() {
        super(Component.literal("Poison PvP"));
    }

    @Override
    protected void init() {
        moduleButtons.clear();
        int cx = this.width / 2;
        int y = Math.max(8, this.height / 2 - 145);

        Button title = Button.builder(
            Component.literal("POISON PVP").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(cx - 180, y, 360, 24).build();
        title.active = false;
        this.addRenderableWidget(title);

        y += 29;
        Button subtitle = Button.builder(
            Component.literal("PvP için görünürlük ve performans ayarları").withStyle(ChatFormatting.GRAY),
            b -> {}).bounds(cx - 180, y, 360, 20).build();
        subtitle.active = false;
        this.addRenderableWidget(subtitle);

        y += 25;
        this.addRenderableWidget(Button.builder(Component.literal("PvP Profili"), b -> {
            Modules.applyPreset(Modules.PRESET_PVP);
            refresh();
        }).bounds(cx - 180, y, 114, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("Sade Görünüm"), b -> {
            Modules.applyPreset(Set.of(
                "particles", "shadows", "bobbing", "fovfx", "hurttilt",
                "clouds", "screenfx"));
            refresh();
        }).bounds(cx - 57, y, 114, 22).build());
        this.addRenderableWidget(Button.builder(Component.literal("Hepsini Kapat"), b -> {
            Modules.applyPreset(Set.of());
            refresh();
        }).bounds(cx + 66, y, 114, 22).build());

        y += 27;
        zoomButton = this.addRenderableWidget(Button.builder(Component.empty(), b -> {
            binding = true;
            refresh();
        }).bounds(cx - 180, y, 245, 22).build());
        fovButton = this.addRenderableWidget(Button.builder(Component.empty(), b -> {
            Config.zoomFov = Config.zoomFov >= 110 ? 30 : Math.min(110, Config.zoomFov + 10);
            Config.save();
            refresh();
        }).bounds(cx + 71, y, 109, 22).build());

        y += 29;
        Button section = Button.builder(
            Component.literal("PVP AYARLARI  ·  Aç / Kapat").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(cx - 180, y, 360, 20).build();
        section.active = false;
        this.addRenderableWidget(section);

        y += 24;
        int i = 0;
        for (Module m : Modules.ALL) {
            if (!Modules.PRESET_PVP.contains(m.id)) continue;
            int col = i % 3;
            int row = i / 3;
            Button btn = Button.builder(Component.empty(), b -> {
                m.setEnabled(!m.isEnabled());
                refresh();
            }).bounds(cx - 180 + col * 123, y + row * 25, 117, 22).build();
            moduleButtons.put(m, this.addRenderableWidget(btn));
            i++;
        }

        int rows = (i + 2) / 3;
        y += rows * 25 + 8;

        Button close = Button.builder(Component.literal("Kapat"), b -> this.onClose())
            .bounds(cx - 55, y, 110, 22).build();
        this.addRenderableWidget(close);

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
                .append(Component.literal("  ·  Değiştir")));
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
