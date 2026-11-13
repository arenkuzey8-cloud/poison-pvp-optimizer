package com.poisonpvp;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public final class Config {
    private Config() {}

    public static int zoomFov = 30;
    public static boolean armorHudEnabled = true;
    public static int armorHudX = 8;
    public static int armorHudY = 8;
    private static final Set<String> savedEnabled = new HashSet<>();

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("poisonpvp.properties");
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(f)) {
            p.load(r);
        } catch (IOException e) {
            return;
        }
        try {
            zoomFov = Math.max(30, Math.min(110, Integer.parseInt(p.getProperty("zoomFov", "30"))));
        } catch (NumberFormatException ignored) {}
        armorHudEnabled = Boolean.parseBoolean(p.getProperty("armorHudEnabled", "true"));
        try { armorHudX = Math.max(0, Math.min(2000, Integer.parseInt(p.getProperty("armorHudX", "8")))); } catch (NumberFormatException ignored) {}
        try { armorHudY = Math.max(0, Math.min(2000, Integer.parseInt(p.getProperty("armorHudY", "8")))); } catch (NumberFormatException ignored) {}
        for (String k : p.stringPropertyNames()) {
            if (k.startsWith("module.") && Boolean.parseBoolean(p.getProperty(k))) {
                savedEnabled.add(k.substring("module.".length()));
            }
        }
    }

    public static boolean wasEnabled(String id) {
        return savedEnabled.contains(id);
    }

    public static void save() {
        Properties p = new Properties();
        p.setProperty("zoomFov", Integer.toString(zoomFov));
        p.setProperty("armorHudEnabled", Boolean.toString(armorHudEnabled));
        p.setProperty("armorHudX", Integer.toString(armorHudX));
        p.setProperty("armorHudY", Integer.toString(armorHudY));
        for (Module m : Modules.ALL) p.setProperty("module." + m.id, Boolean.toString(m.isEnabled()));
        try (Writer w = Files.newBufferedWriter(file())) {
            p.store(w, "Poison PVP Optimizer");
        } catch (IOException ignored) {}
    }
}
