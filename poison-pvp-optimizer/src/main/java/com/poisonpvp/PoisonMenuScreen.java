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
import java.util.Set;

public class PoisonMenuScreen extends Screen {
    private static final int MENU_WIDTH = 590;
    private static final int MENU_HEIGHT = 390;
    private static final int GAP = 7;
    private static final String[] PROFILE_MARKS = {
        "⚔", "⚒", "✦", "♆", "★", "◈", "⚗", "◉", "◇", "✧", "▣"
    };
    private static final Set<String> PERFORMANCE = Set.of(
        "particles", "shadows", "renderdist", "simdist", "entitydist",
        "biomeblend", "vsync", "clouds", "fastgfx", "mipmap", "autosave"
    );

    private boolean binding;
    private int selectedProfile;
    private int selectedTab;
    private Button profileButton;
    private Button zoomButton;
    private Button fovButton;
    private Button armorHudButton;
    private Button armorXButton;
    private Button armorYButton;
    private Button armorScaleDownButton;
    private Button armorScaleUpButton;
    private Button sectionButton;
    private final Button[] tabButtons = new Button[3];
    private final Map<Module, Button> moduleButtons = new LinkedHashMap<>();

    public PoisonMenuScreen() {
        super(Component.literal("Poison PvP Optimizer"));
    }

    @Override
    protected void init() {
        clearWidgets();
        moduleButtons.clear();

        int width = Math.min(MENU_WIDTH, this.width - 16);
        int height = Math.min(MENU_HEIGHT, this.height - 12);
        int left = (this.width - width) / 2;
        int top = (this.height - height) / 2;
        int sideWidth = Math.max(92, Math.min(116, width / 5));
        int mainX = left + sideWidth + 12;
        int mainWidth = width - sideWidth - 20;
        int y = top + 10;

        Button brand = Button.builder(
            Component.literal("POISON").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
            b -> {}).bounds(left + 8, y, sideWidth - 8, 25).build();
        brand.active = false;
        addRenderableWidget(brand);

        Button version = Button.builder(
            Component.literal("PVP OPTIMIZER").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD),
            b -> {}).bounds(left + 8, y + 29, sideWidth - 8, 20).build();
        version.active = false;
        addRenderableWidget(version);

        String[] tabs = {"GENEL", "PERFORMANS", "GÖRÜNTÜ"};
        for (int i = 0; i < tabs.length; i++) {
            final int tab = i;
            tabButtons[i] = addRenderableWidget(Button.builder(Component.literal(tabs[i]), b -> {
                selectedTab = tab;
                rebuildWidgets();
            }).bounds(left + 8, y + 64 + i * 27, sideWidth - 8, 23).build());
        }

        int mainY = top + 10;
        Button heading = Button.builder(
            Component.literal("PVP OPTIMIZER  /  MODÜLLER")
                .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
            b -> {}).bounds(mainX, mainY, mainWidth, 25).build();
        heading.active = false;
        addRenderableWidget(heading);

        mainY += 31;
        int profileWidth = Math.max(90, (mainWidth - GAP * 3) / 4);
        profileButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            selectedProfile = (selectedProfile + 1) % Modules.PVP_PROFILES.size();
            refresh();
        }).bounds(mainX, mainY, profileWidth, 23).build());

        addRenderableWidget(Button.builder(Component.literal("UYGULA"), b -> {
            Modules.applyPreset(Modules.profilePreset(Modules.PVP_PROFILES.get(selectedProfile)));
            refresh();
        }).bounds(mainX + profileWidth + GAP, mainY, profileWidth, 23).build());

        addRenderableWidget(Button.builder(Component.literal("MAKS FPS"), b -> {
            Modules.applyPreset(Modules.PRESET_MAX_FPS);
            refresh();
        }).bounds(mainX + (profileWidth + GAP) * 2, mainY, profileWidth, 23).build());

        addRenderableWidget(Button.builder(Component.literal("SIFIRLA"), b -> {
            Modules.applyPreset(Set.of());
            refresh();
        }).bounds(mainX + (profileWidth + GAP) * 3, mainY, profileWidth, 23).build());

        mainY += 29;
        sectionButton = addRenderableWidget(Button.builder(Component.empty(), b -> {})
            .bounds(mainX, mainY, mainWidth, 19).build());
        sectionButton.active = false;

        mainY += 24;
        int columns = 3;
        int cellWidth = (mainWidth - GAP) / columns;
        int availableForModules = Math.max(88, height - (mainY - top) - 104);
        int rowHeight = Math.max(19, Math.min(23, availableForModules / 9));
        int index = 0;
        for (Module module : Modules.ALL) {
            if (!visibleInTab(module)) continue;
            int col = index % columns;
            int row = index / columns;
            Button button = Button.builder(Component.empty(), b -> {
                module.setEnabled(!module.isEnabled());
                refresh();
            }).bounds(mainX + col * (cellWidth + GAP), mainY + row * rowHeight,
                cellWidth, rowHeight - 2).build();
            moduleButtons.put(module, addRenderableWidget(button));
            index++;
        }

        int bottomY = top + height - 100;
        zoomButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            binding = true;
            refresh();
        }).bounds(mainX, bottomY, mainWidth - 94, 21).build());
        fovButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            Config.zoomFov = Config.zoomFov >= 110 ? 30 : Math.min(110, Config.zoomFov + 10);
            Config.save();
            refresh();
        }).bounds(mainX + mainWidth - 88, bottomY, 88, 21).build());

        int hudGap = 4;
        int hudCell = (mainWidth - hudGap * 6) / 7;
        armorHudButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            Config.armorHudEnabled = !Config.armorHudEnabled;
            Config.save();
            refresh();
        }).bounds(mainX, bottomY + 25, hudCell, 21).build());
        armorXButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            Config.armorHudX = Math.max(0, Config.armorHudX - 10);
            Config.save();
            refresh();
        }).bounds(mainX + hudCell + hudGap, bottomY + 25, hudCell, 21).build());
        addRenderableWidget(Button.builder(Component.literal("X+"), b -> {
            Config.armorHudX = Math.min(2000, Config.armorHudX + 10);
            Config.save();
            refresh();
        }).bounds(mainX + (hudCell + hudGap) * 2, bottomY + 25, hudCell, 21).build());
        armorYButton = addRenderableWidget(Button.builder(Component.empty(), b -> {
            Config.armorHudY = Math.max(0, Config.armorHudY - 10);
            Config.save();
            refresh();
        }).bounds(mainX + (hudCell + hudGap) * 3, bottomY + 25, hudCell, 21).build());
        addRenderableWidget(Button.builder(Component.literal("Y+"), b -> {
            Config.armorHudY = Math.min(2000, Config.armorHudY + 10);
            Config.save();
            refresh();
        }).bounds(mainX + (hudCell + hudGap) * 4, bottomY + 25, hudCell, 21).build());

        boolean c2meLoaded = FabricLoader.getInstance().isModLoaded("c2me");
        Button status = addRenderableWidget(Button.builder(
            Component.literal(c2meLoaded ? "C2ME  •  ALGILANDI" : "C2ME  •  İSTEĞE BAĞLI")
                .withStyle(ChatFormatting.GRAY), b -> {})
            .bounds(mainX, bottomY + 50, mainWidth, 18).build());
        status.active = false;

        addRenderableWidget(Button.builder(
            Component.literal("KAPAT").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD),
            b -> onClose()).bounds(mainX, top + height - 27, mainWidth, 21).build());

        refresh();
    }

    private boolean visibleInTab(Module module) {
        if (selectedTab == 0) return true;
        return selectedTab == 1 ? PERFORMANCE.contains(module.id) : !PERFORMANCE.contains(module.id);
    }

    private void refresh() {
        if (profileButton == null || zoomButton == null || fovButton == null) return;

        for (int i = 0; i < tabButtons.length; i++) {
            if (tabButtons[i] != null) {
                tabButtons[i].setMessage(Component.literal(
                    (selectedTab == i ? "›  " : "   ") +
                    new String[]{"GENEL", "PERFORMANS", "GÖRÜNTÜ"}[i])
                    .withStyle(selectedTab == i ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.GRAY,
                        ChatFormatting.BOLD));
            }
        }

        String profile = Modules.PVP_PROFILES.get(selectedProfile);
        profileButton.setMessage(Component.literal(PROFILE_MARKS[selectedProfile] + "  " + profile)
            .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        for (Map.Entry<Module, Button> entry : moduleButtons.entrySet()) {
            Module module = entry.getKey();
            boolean enabled = module.isEnabled();
            entry.getValue().setMessage(Component.literal(
                (enabled ? "●  " : "○  ") + module.label)
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        }

        sectionButton.setMessage(Component.literal(switch (selectedTab) {
            case 1 -> "PERFORMANS AYARLARI";
            case 2 -> "GÖRSEL AYARLAR";
            default -> "TÜM MODÜLLER";
        }).withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

        if (binding) {
            zoomButton.setMessage(Component.literal("TUŞA BAS  ·  ESC İPTAL  ·  DEL SİL")
                .withStyle(ChatFormatting.YELLOW));
        } else {
            zoomButton.setMessage(Component.literal("ZOOM  ·  ")
                .withStyle(ChatFormatting.GRAY)
                .append(PoisonPvpClient.ZOOM_KEY.getTranslatedKeyMessage())
                .append(Component.literal("  ·  DEĞİŞTİR")));
        }
        fovButton.setMessage(Component.literal("FOV " + Config.zoomFov)
            .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
        if (armorHudButton != null) armorHudButton.setMessage(Component.literal(
            "HUD " + (Config.armorHudEnabled ? "AÇIK" : "KAPALI"))
            .withStyle(Config.armorHudEnabled ? ChatFormatting.GREEN : ChatFormatting.RED));
        if (armorXButton != null) armorXButton.setMessage(Component.literal("X " + Config.armorHudX + " −"));
        if (armorYButton != null) armorYButton.setMessage(Component.literal("Y " + Config.armorHudY + " −"));
        if (armorScaleDownButton != null) armorScaleDownButton.setMessage(Component.literal("BOYUT −"));
        if (armorScaleUpButton != null) armorScaleUpButton.setMessage(Component.literal("BOYUT +"));
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
