package com.poisonpvp;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsPreset;
import net.minecraft.server.level.ParticleStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Modules {
    private Modules() {}

    public static final List<String> PVP_PROFILES = List.of(
        "SMP", "UHC", "Crystal", "Mace", "DiaPot", "NetPot",
        "DiaSMP", "Sword", "Axe", "Cart", "Spear"
    );

    // These are client-side visual/graphics settings only.
    public static final List<Module> ALL = List.of(
        new Module("particles", "Parçacık Min.", (o, u) -> Module.set(u, o.particles(), ParticleStatus.MINIMAL)),
        new Module("shadows", "Gölge Kapat", (o, u) -> Module.set(u, o.entityShadows(), false)),
        new Module("renderdist", "Render 8", (o, u) -> Module.set(u, o.renderDistance(), 8)),
        new Module("simdist", "Simulation 5", (o, u) -> Module.set(u, o.simulationDistance(), 5)),
        new Module("entitydist", "Entity %75", (o, u) -> Module.set(u, o.entityDistanceScaling(), 0.75D)),
        new Module("biomeblend", "Biome Blend 1", (o, u) -> Module.set(u, o.biomeBlendRadius(), 1)),
        new Module("unlimitedfps", "FPS Limiti 260", (o, u) -> Module.set(u, o.framerateLimit(), 260)),
        new Module("vsync", "VSync Kapat", (o, u) -> Module.set(u, o.enableVsync(), false)),
        new Module("bobbing", "Bobbing Kapat", (o, u) -> Module.set(u, o.bobView(), false)),
        new Module("fovfx", "FOV Efekti Kapat", (o, u) -> Module.set(u, o.fovEffectScale(), 0.0D)),
        new Module("hurttilt", "Hasar Eğimi Kapat", (o, u) -> Module.set(u, o.damageTiltStrength(), 0.0D)),
        new Module("brightness", "Max Parlaklık", (o, u) -> Module.set(u, o.gamma(), 1.0D)),
        new Module("clouds", "Bulut Kapat", (o, u) -> Module.set(u, o.cloudStatus(), CloudStatus.OFF)),
        new Module("fastgfx", "Fast Grafik", (o, u) -> Module.set(u, o.graphicsPreset(), GraphicsPreset.FAST)),
        new Module("mipmap", "Mipmap 0", (o, u) -> Module.set(u, o.mipmapLevels(), 0)),
        new Module("screenfx", "Ekran Efekti Kapat", (o, u) -> Module.set(u, o.screenEffectScale(), 0.0D)),
        new Module("autosave", "Autosave Gizle", (o, u) -> Module.set(u, o.showAutosaveIndicator(), false)),
        new Module("togglesprint", "Toggle Sprint", (o, u) -> Module.set(u, o.toggleSprint(), true))
    );

    // Each profile selects a different combination of visual and graphics options.
    // No profile changes combat mechanics or input timing.
    private static final Map<String, Set<String>> PROFILE_PRESETS = Map.ofEntries(
        Map.entry("SMP", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend", "clouds", "fastgfx", "mipmap", "screenfx", "autosave")),
        Map.entry("UHC", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "clouds", "fastgfx", "mipmap", "screenfx")),
        Map.entry("Crystal", Set.of("particles", "shadows", "simdist", "entitydist", "biomeblend", "clouds", "fastgfx", "mipmap", "screenfx")),
        Map.entry("Mace", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "clouds", "fastgfx", "screenfx")),
        Map.entry("DiaPot", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "clouds", "fastgfx", "mipmap", "screenfx")),
        Map.entry("NetPot", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend", "clouds", "fastgfx", "screenfx")),
        Map.entry("DiaSMP", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend", "clouds", "fastgfx", "mipmap", "screenfx", "autosave")),
        Map.entry("Sword", Set.of("particles", "shadows", "simdist", "entitydist", "clouds", "fastgfx", "mipmap", "screenfx")),
        Map.entry("Axe", Set.of("particles", "shadows", "simdist", "entitydist", "clouds", "fastgfx", "screenfx")),
        Map.entry("Cart", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "clouds", "fastgfx", "mipmap", "screenfx")),
        Map.entry("Spear", Set.of("particles", "shadows", "simdist", "entitydist", "biomeblend", "clouds", "fastgfx", "mipmap", "screenfx"))
    );

    public static final Set<String> PRESET_MAX_FPS = Set.of(
        "particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend",
        "clouds", "fastgfx", "mipmap", "screenfx", "autosave"
    );

    public static final Set<String> PRESET_PVP = Set.of(
        "particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend",
        "clouds", "fastgfx", "mipmap", "screenfx", "autosave"
    );

    public static Set<String> profilePreset(String profile) {
        return PROFILE_PRESETS.getOrDefault(profile, PROFILE_PRESETS.get("SMP"));
    }

    public static void applyPreset(Set<String> ids) {
        for (Module m : ALL) m.setEnabled(ids.contains(m.id), false);
        Config.save();
    }
}
