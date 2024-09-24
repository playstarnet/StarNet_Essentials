package com.playstarnet.essentials.mixins.ext;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyMapping.class)
public interface KeyBindingAccessor {
    @Accessor("CATEGORY_SORT_ORDER")
    static Map<String, Integer> se$getCategoryMap() {
        throw new AssertionError();
    }
}
