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
        "Sword", "Axe", "Mace", "Spear", "UHC", "NetPot",
        "Pot", "SMP", "DiaSMP", "Crystal", "Cart"
    );

    // Client-side visual/performance settings only. No combat or item-use automation.
    public static final List<Module> ALL = List.of(
        // ParticleStatus.MINIMAL reduces particle load, including explosion particles;
        // it does not remove every explosion visual or sound.
        new Module("particles", "Parçacık Min. (patlama azalır)",
            (o, u) -> Module.set(u, o.particles(), ParticleStatus.MINIMAL)),
        new Module("shadows", "Varlık Gölgeleri Kapat",
            (o, u) -> Module.set(u, o.entityShadows(), false)),
        new Module("renderdist", "Render Mesafesi 6",
            (o, u) -> Module.set(u, o.renderDistance(), 6)),
        new Module("simdist", "Simülasyon Mesafesi 4",
            (o, u) -> Module.set(u, o.simulationDistance(), 4)),
        new Module("entitydist", "Varlık Mesafesi %50",
            (o, u) -> Module.set(u, o.entityDistanceScaling(), 0.50D)),
        new Module("biomeblend", "Biome Blend 0",
            (o, u) -> Module.set(u, o.biomeBlendRadius(), 0)),
        new Module("vsync", "VSync Kapat",
            (o, u) -> Module.set(u, o.enableVsync(), false)),
        new Module("bobbing", "Kamera Sallantısı Kapat",
            (o, u) -> Module.set(u, o.bobView(), false)),
        new Module("fovfx", "FOV Efektleri Kapat",
            (o, u) -> Module.set(u, o.fovEffectScale(), 0.0D)),
        new Module("hurttilt", "Hasar Kamera Eğimi Kapat",
            (o, u) -> Module.set(u, o.damageTiltStrength(), 0.0D)),
        new Module("brightness", "Parlaklık Yükselt",
            (o, u) -> Module.set(u, o.gamma(), 1.0D)),
        new Module("clouds", "Bulutları Kapat",
            (o, u) -> Module.set(u, o.cloudStatus(), CloudStatus.OFF)),
        new Module("fastgfx", "Hızlı Grafik",
            (o, u) -> Module.set(u, o.graphicsPreset(), GraphicsPreset.FAST)),
        new Module("mipmap", "Mipmap 0",
            (o, u) -> Module.set(u, o.mipmapLevels(), 0)),
        new Module("screenfx", "Ekran Efektlerini Kapat",
            (o, u) -> Module.set(u, o.screenEffectScale(), 0.0D)),
        new Module("autosave", "Autosave Göstergesini Kapat",
            (o, u) -> Module.set(u, o.showAutosaveIndicator(), false)),
        // A separate Crystal-only visual toggle; does not alter combat or entity behavior.
        new Module("crystalvisual", "Crystal Görsel Parlaklık",
            (o, u) -> Module.set(u, o.gamma(), 1.0D))
    );

    // Distinct mode presets. Differences are visual/performance settings only.
    private static final Map<String, Set<String>> PROFILE_PRESETS = Map.ofEntries(
        Map.entry("Sword", Set.of("particles", "shadows", "simdist", "entitydist", "clouds", "fastgfx", "mipmap", "screenfx", "fovfx", "bobbing", "vsync")),
        Map.entry("Axe", Set.of("particles", "shadows", "simdist", "entitydist", "clouds", "fastgfx", "mipmap", "screenfx", "fovfx", "hurttilt", "bobbing")),
        Map.entry("Mace", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "clouds", "fastgfx", "mipmap", "screenfx", "fovfx", "bobbing", "vsync")),
        Map.entry("Spear", Set.of("particles", "shadows", "simdist", "entitydist", "biomeblend", "clouds", "fastgfx", "mipmap", "screenfx", "fovfx", "bobbing")),
        Map.entry("UHC", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "clouds", "fastgfx", "mipmap", "screenfx", "fovfx", "bobbing")),
        Map.entry("NetPot", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend", "clouds", "fastgfx", "mipmap", "screenfx", "fovfx", "hurttilt")),
        Map.entry("Pot", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "clouds", "fastgfx", "mipmap", "screenfx", "fovfx", "hurttilt", "vsync")),
        Map.entry("SMP", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend", "clouds", "fastgfx", "mipmap", "autosave")),
        Map.entry("DiaSMP", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend", "clouds", "fastgfx", "mipmap", "screenfx", "autosave")),
        Map.entry("Crystal", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend", "clouds", "fastgfx", "mipmap", "screenfx", "fovfx", "hurttilt", "bobbing", "vsync", "crystalvisual")),
        Map.entry("Cart", Set.of("particles", "shadows", "renderdist", "simdist", "entitydist", "clouds", "fastgfx", "mipmap", "screenfx", "fovfx", "hurttilt", "bobbing", "vsync"))
    );

    // A one-click graphics/performance preset. No artificial FPS cap is applied.
    public static final Set<String> PRESET_MAX_FPS = Set.of(
        "particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend",
        "vsync", "bobbing", "fovfx", "hurttilt", "brightness", "clouds",
        "fastgfx", "mipmap", "screenfx", "autosave"
    );

    public static final Set<String> PRESET_PVP = PRESET_MAX_FPS;

    public static Set<String> profilePreset(String profile) {
        return PROFILE_PRESETS.getOrDefault(profile, PROFILE_PRESETS.get("SMP"));
    }

    public static void applyPreset(Set<String> ids) {
        for (Module m : ALL) m.setEnabled(ids.contains(m.id), false);
        Config.save();
    }
}
