package com.poisonpvp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;

import java.util.ArrayList;
import java.util.List;

/** Tek bir ac/kapat ayar paketi. Kapatinca eski degerler geri yuklenir. */
public final class Module {

    public interface Applier {
        void apply(Options options, List<Runnable> undo);
    }

    public final String id;
    public final String label;
    private final Applier applier;
    private final List<Runnable> undo = new ArrayList<>();
    private boolean enabled;

    public Module(String id, String label, Applier applier) {
        this.id = id;
        this.label = label;
        this.applier = applier;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean on) {
        setEnabled(on, true);
    }

    public void setEnabled(boolean on, boolean persist) {
        if (on == enabled) return;
        if (on) {
            applier.apply(Minecraft.getInstance().options, undo);
        } else {
            revert();
        }
        enabled = on;
        if (persist) Config.save();
    }

    /** Eski degerleri geri yukler (enabled bayragina dokunmaz). */
    public void revert() {
        for (int i = undo.size() - 1; i >= 0; i--) undo.get(i).run();
        undo.clear();
    }

    public static <T> void set(List<Runnable> undo, OptionInstance<T> option, T value) {
        T old = option.get();
        undo.add(() -> option.set(old));
        option.set(value);
    }
}
