package com.playstarnet.essentials.mixins.ext;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
	@Accessor("width")
	int getWidth();

	@Accessor("height")
	int getHeight();

	@Accessor("children")
	List<GuiEventListener> getChildren();

	@Accessor("renderables")
	List<Renderable> getRenderables();

	@Invoker("addRenderableWidget")
	<T extends GuiEventListener & Renderable & NarratableEntry> T invokeAddRenderableWidget(T drawableElement);


}
