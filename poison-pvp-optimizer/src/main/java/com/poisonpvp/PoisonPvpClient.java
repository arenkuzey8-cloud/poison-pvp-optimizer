package com.poisonpvp;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class PoisonPvpClient implements ClientModInitializer {

    public static final String MOD_ID = "poisonpvp";
    public static KeyMapping MENU_KEY;
    public static KeyMapping ZOOM_KEY;

    private static boolean zooming;
    private static int prevFov;
    private static double prevSensitivity;

    @Override
    public void onInitializeClient() {
        Config.load();
        PoisonArmorHud.register();

        KeyMapping.Category category = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(MOD_ID, "main"));

        MENU_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.poisonpvp.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        ZOOM_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.poisonpvp.zoom", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, category));

        // Kayitli modulleri oyun tamamen acildiktan sonra uygula (options hazir olsun).
        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> {
            for (Module m : Modules.ALL) {
                if (Config.wasEnabled(m.id)) m.setEnabled(true, false);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(PoisonPvpClient::tick);

        // Kapanirken orijinal ayarlari geri yaz ki options.txt bozulmasin.
        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> {
            stopZoom(mc);
            for (Module m : Modules.ALL) m.revert();
            mc.options.save();
        });
    }

    private static void tick(Minecraft mc) {
        if (mc.player == null) {
            if (zooming) stopZoom(mc);
            return;
        }

        while (MENU_KEY.consumeClick()) {
            mc.gui.setScreen(new PoisonMenuScreen());
        }

        boolean want = ZOOM_KEY.isDown();
        if (want && !zooming) {
            prevFov = mc.options.fov().get();
            prevSensitivity = mc.options.sensitivity().get();
            mc.options.fov().set(Config.zoomFov);
            mc.options.sensitivity().set(prevSensitivity * 0.35D);
            zooming = true;
        } else if (!want && zooming) {
            stopZoom(mc);
        }
    }

    private static void stopZoom(Minecraft mc) {
        if (!zooming) return;
        mc.options.fov().set(prevFov);
        mc.options.sensitivity().set(prevSensitivity);
        zooming = false;
    }

    public static void setZoomKey(InputConstants.Key key) {
        ZOOM_KEY.setKey(key);
        KeyMapping.resetMapping();
        Minecraft.getInstance().options.save();
    }
}
