package com.playstarnet.essentials.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class ParseItemName {
    public static TextColor getItemTextColor(ItemStack item) {
        List<Component> lore = item.getTooltipLines(Item.TooltipContext.EMPTY, null, new TooltipFlag.Default(false, false)); // Get the item's lore
        if (lore != null && !lore.isEmpty()) {
            for (Component loreLine : lore) {
                String loreText = loreLine.getString();
                if (loreText.contains("ꐶ")) {
                    return TextColor.fromRgb(0xFFFFFF);
                } else if (loreText.contains("ꐸ")) {
                    return TextColor.fromRgb(0xfcfc54);
                } else if (loreText.contains("ꐹ")) {
                    return TextColor.fromRgb(0x54fcfc);
                }
            }
        }
        return null;
    }

    public static String getItemId(ItemStack itemstack) {
        if (itemstack == null || !itemstack.has(DataComponents.CUSTOM_DATA)) {
            System.out.println("ItemStack does not have custom data: " + itemstack);
            return "";
        }

        CustomData itemCompounds = itemstack.get(DataComponents.CUSTOM_DATA);

        if (itemCompounds != null && itemCompounds.copyTag() != null) {
            if (itemCompounds.copyTag().contains("CustomItem")) {
                return itemCompounds.copyTag().getString("CustomItem");
            }
        }

        return ""; // Return empty if no custom item ID found
    }
}
