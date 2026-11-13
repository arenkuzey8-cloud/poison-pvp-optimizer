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
            Component.literal("İstemci tarafı görsel ayarlar · FPS odaklı profiller").withStyle(ChatFormatting.GRAY),
            b -> {}).bounds(cx - 180, y, 360, 20).build();
        subtitle.active = false;
        this.addRenderableWidget(subtitle);

        y += 25;
        profileButton = this.addRenderableWidget(Button.builder(Component.empty(), b -> {
            selectedProfile = (selectedProfile + 1) % Modules.PVP_PROFILES.size();
            refresh();
        }).bounds(cx - 180, y, 87, 22).build());

        this.addRenderableWidget(Button.builder(Component.literal("Profili Uygula"), b -> {
            Modules.applyPreset(Modules.profilePreset(Modules.PVP_PROFILES.get(selectedProfile)));
            refresh();
        }).bounds(cx - 89, y, 87, 22).build());

        this.addRenderableWidget(Button.builder(Component.literal("Maks FPS"), b -> {
            Modules.applyPreset(Modules.PRESET_MAX_FPS);
            refresh();
        }).bounds(cx + 2, y, 87, 22).build());

        this.addRenderableWidget(Button.builder(Component.literal("Hepsini Kapat"), b -> {
            Modules.applyPreset(java.util.Set.of());
            refresh();
        }).bounds(cx + 93, y, 87, 22).build());

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
            Component.literal("GÖRSEL / FPS AYARLARI  ·  Aç / Kapat").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(cx - 180, y, 360, 20).build();
        section.active = false;
        this.addRenderableWidget(section);

        y += 24;
        int i = 0;
        for (Module m : Modules.ALL) {
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
        y += rows * 25 + 5;
        boolean c2meLoaded = FabricLoader.getInstance().isModLoaded("c2me");
        Button c2meStatusButton = this.addRenderableWidget(Button.builder(
            Component.literal(c2meLoaded ? "C2ME algılandı · Chunk yükleme/generasyon optimizasyonu" : "C2ME bulunamadı · İsteğe bağlı mod"),
            b -> {}).bounds(cx - 180, y, 360, 20).build());
        c2meStatusButton.active = false;

        y += 25;
        Button close = Button.builder(Component.literal("Kapat"), b -> this.onClose())
            .bounds(cx - 55, y, 110, 22).build();
        this.addRenderableWidget(close);

        refresh();
    }

    private void refresh() {
        profileButton.setMessage(Component.literal("Mod: " + Modules.PVP_PROFILES.get(selectedProfile)));
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
