package com.poisonpvp;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class PoisonArmorHud {
    private PoisonArmorHud() {}

    public static void register() {
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath(PoisonPvpClient.MOD_ID, "armor_hud"),
            PoisonArmorHud::render
        );
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (!Config.armorHudEnabled || mc.player == null || mc.gui.hud.isHidden()) return;

        EquipmentSlot[] slots = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST,
            EquipmentSlot.LEGS, EquipmentSlot.FEET,
            EquipmentSlot.OFFHAND
        };
        String[] labels = {"KASK", "GÖĞÜSLÜK", "PANTOLON", "BOT", "EL"};
        int x = Config.armorHudX;
        int y = Config.armorHudY;
        int width = 126;
        int rowHeight = 18;
        int height = 19 + rowHeight * slots.length;

        graphics.fill(x, y, x + width, y + height, 0xA8202028);
        graphics.fill(x, y, x + width, y + 2, 0xFFB46BFF);
        graphics.text(mc.font, "POISON  ARMOR", x + 6, y + 5, 0xFFE4C8FF, true);

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = mc.player.getItemBySlot(slots[i]);
            String value = stack.isEmpty() ? "--" : durabilityText(stack);
            int color = stack.isEmpty() ? 0xFF888888 : durabilityColor(stack);
            int rowY = y + 19 + i * rowHeight;
            graphics.text(mc.font, labels[i], x + 6, rowY, 0xFFE6E6E6, true);
            graphics.text(mc.font, value, x + width - mc.font.width(value) - 6, rowY, color, true);
        }
    }

    private static String durabilityText(ItemStack stack) {
        if (!stack.isDamageableItem()) return "VAR";
        int max = stack.getMaxDamage();
        int remaining = Math.max(0, max - stack.getDamageValue());
        return Math.round(remaining * 100.0f / max) + "%";
    }

    private static int durabilityColor(ItemStack stack) {
        if (!stack.isDamageableItem()) return 0xFF75D99A;
        float ratio = (stack.getMaxDamage() - stack.getDamageValue()) / (float) stack.getMaxDamage();
        if (ratio <= 0.20f) return 0xFFFF5555;
        if (ratio <= 0.50f) return 0xFFFFC45C;
        return 0xFF75D99A;
    }
}
