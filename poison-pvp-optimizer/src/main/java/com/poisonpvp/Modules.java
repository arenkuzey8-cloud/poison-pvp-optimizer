package com.poisonpvp;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsPreset;
import net.minecraft.server.level.ParticleStatus;

import java.util.List;
import java.util.Set;

public final class Modules {
    private Modules() {}

    public static final List<Module> ALL = List.of(
        new Module("particles", "Parçacık Min.", (o, u) -> Module.set(u, o.particles(), ParticleStatus.MINIMAL)),
        new Module("shadows", "Gölge Kapat", (o, u) -> Module.set(u, o.entityShadows(), false)),
        new Module("renderdist", "Render 8", (o, u) -> Module.set(u, o.renderDistance(), 8)),
        new Module("simdist", "Simulation 5", (o, u) -> Module.set(u, o.simulationDistance(), 5)),
        new Module("entitydist", "Entity %75", (o, u) -> Module.set(u, o.entityDistanceScaling(), 0.75D)),
        new Module("biomeblend", "Biome Blend 1", (o, u) -> Module.set(u, o.biomeBlendRadius(), 1)),
        new Module("unlimitedfps", "Sınırsız FPS", (o, u) -> Module.set(u, o.framerateLimit(), 260)),
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

    public static final Set<String> PRESET_MAX_FPS = Set.of(
        "particles", "shadows", "renderdist", "simdist", "entitydist", "biomeblend", "unlimitedfps", "vsync",
        "clouds", "fastgfx", "mipmap", "screenfx", "autosave");

    public static final Set<String> PRESET_PVP = Set.of(
        "particles", "shadows", "unlimitedfps", "vsync", "bobbing", "fovfx", "hurttilt", "brightness",
        "clouds", "screenfx");

    public static void applyPreset(Set<String> ids) {
        for (Module m : ALL) m.setEnabled(ids.contains(m.id), false);
        Config.save();
    }
}
