package com.playstarnet.essentials.mixins;

import com.mojang.blaze3d.platform.InputConstants;
import com.playstarnet.essentials.feat.ext.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyMapping.class)
public class KeyMappingsMixin implements KeyMappingAccessor {
    @Shadow private InputConstants.Key key;

    @Override
    public InputConstants.Key getKey() {
        return this.key;
    }
}
