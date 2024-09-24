package com.playstarnet.essentials.mixins;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.block.NoteBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoteBlock.class)
public class NoteblockInteractionMixin {

    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void se$useWithoutItem(CallbackInfoReturnable<InteractionResult> cir) {
        cir.setReturnValue(InteractionResult.PASS);
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void se$useItemOn(CallbackInfoReturnable<ItemInteractionResult> cir) {
        cir.setReturnValue(ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
    }
}
