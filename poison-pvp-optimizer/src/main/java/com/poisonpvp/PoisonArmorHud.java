package com.poisonpvp;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class PoisonArmorHud {
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST,
        EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

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

        float scale = Config.armorHudScale / 100.0f;
        int slotSize = Math.round(34 * scale);
        int gap = Math.max(2, Math.round(3 * scale));
        int padding = Math.max(3, Math.round(4 * scale));
        int inset = Math.max(2, Math.round(3 * scale));
        int barHeight = Math.max(2, Math.round(3 * scale));
        int width = padding * 2 + SLOTS.length * slotSize + (SLOTS.length - 1) * gap;
        int height = slotSize + padding * 2 + 1;
        int x = Config.armorHudX;
        int y = Config.armorHudY;

        graphics.fill(x, y, x + width, y + height, 0xE0101010);
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, 0xFF777777);
        graphics.fill(x + 4, y + 4, x + width - 4, y + height - 4, 0xFF292929);

        for (int i = 0; i < SLOTS.length; i++) {
            int slotX = x + padding + i * (slotSize + gap);
            int slotY = y + padding;
            ItemStack stack = mc.player.getItemBySlot(SLOTS[i]);

            graphics.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, 0xFF111111);
            graphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0xFF353535);
            graphics.fill(slotX + inset, slotY + inset, slotX + slotSize - inset, slotY + slotSize - inset, 0xFF202020);

            if (!stack.isEmpty()) {
                // Keep the 16px item icon centered in its slot at every HUD size.
                int itemX = slotX + (slotSize - 16) / 2;
                int itemY = slotY + (slotSize - 16) / 2;
                graphics.item(stack, itemX, itemY);

                if (stack.isDamageableItem()) {
                    int barX = slotX + Math.max(3, Math.round(4 * scale));
                    int barY = slotY + slotSize - barHeight - Math.max(2, Math.round(3 * scale));
                    int barWidth = slotSize - 2 * Math.max(3, Math.round(4 * scale));
                    float remaining = Math.max(0, stack.getMaxDamage() - stack.getDamageValue())
                        / (float) stack.getMaxDamage();
                    graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF080808);
                    graphics.fill(barX, barY, barX + Math.round(barWidth * remaining), barY + barHeight,
                        durabilityColor(remaining));
                }
            }
        }
    }

    private static int durabilityColor(float remaining) {
        if (remaining <= 0.20f) return 0xFFFF3333;
        if (remaining <= 0.50f) return 0xFFFFC52E;
        return 0xFF35D05B;
    }
}
