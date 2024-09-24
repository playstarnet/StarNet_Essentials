package com.playstarnet.essentials.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class ParseItemName {
    public static String getItemId(ItemStack itemstack) {
        if (itemstack == null || !itemstack.has(DataComponents.CUSTOM_DATA)) return "";
        CustomData itemCompounds = itemstack.get(DataComponents.CUSTOM_DATA);

        if (itemCompounds != null && itemCompounds.copyTag().contains("CustomItem")) {
            return itemCompounds.copyTag().getString("CustomItem");
        }
        return "";
    }
}
