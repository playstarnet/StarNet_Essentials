package com.playstarnet.essentials.mixins;

import com.playstarnet.essentials.feat.ext.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Mixin(ContainerScreen.class)
public abstract class ContainerScreenMixin extends AbstractContainerScreen<ChestMenu> implements AbstractContainerScreenAccessor {

    public ContainerScreenMixin(ChestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void se$slotChange(Slot slot, int slotId, int mouseButton, ClickType type) {
        slotClicked(slot, slotId, mouseButton, type);
    }

//    @Override
//    protected void init() {
//        super.init();
//
//        // Add the "Sort" button only if the chest name contains specific symbols
//        String chestTitle = this.title.getString();
//        if (chestTitle.contains("ꑂ") || chestTitle.contains("ꑄ") || chestTitle.contains("ꐼ")) {
//            Button sortButton = Button.builder(Component.literal("Sort"), button -> {
//                        sortChestBySimulatingClicks();
//                        button.setMessage(Component.literal("Sort"));
//                    })
//                    .bounds(this.leftPos + 110, this.topPos - 35, 60, 20) // Adjust position
//                    .tooltip(Tooltip.create(
//                            Component.literal("This feature is experimental!\nRecommended to use on your island."),
//                            Component.literal("Recommended to use on your island.")
//                    ))
//                    .build();
//
//            this.addRenderableWidget(sortButton);
//
//            this.addRenderableWidget(sortButton);
//        }
//    }
//
//    @Unique
//    private void sortChestBySimulatingClicks() {
//        if (!(this.menu instanceof ChestMenu chestMenu)) {
//            System.out.println("Not a chest menu");
//            return;
//        }
//
//        // Get all chest slots
//        List<Slot> chestSlots = this.menu.slots.stream()
//                .filter(slot -> slot.container == chestMenu.getContainer())
//                .collect(Collectors.toList());
//
//        if (chestSlots.isEmpty()) return;
//
//        // Define rarity order
//        Map<String, Integer> rarityOrder = Map.of(
//                "ꐶ", 0, // Common
//                "ꐸ", 1, // Uncommon
//                "ꐹ", 2, // Rare
//                "꒜", 3  // Exclusive
//        );
//
//        // Collect items and sort them
//        List<ItemStack> sortedItems = chestSlots.stream()
//                .map(Slot::getItem)
//                .sorted(Comparator.comparingInt(stack -> {
//                    String rarity = getRarityFromTooltip(stack);
//                    return rarityOrder.getOrDefault(rarity, Integer.MAX_VALUE);
//                }))
//                .collect(Collectors.toList());
//
//        // Map each sorted item to its desired index
//        Map<Integer, ItemStack> targetMapping = new HashMap<>();
//        for (int i = 0; i < chestSlots.size(); i++) {
//            targetMapping.put(chestSlots.get(i).index, sortedItems.get(i));
//        }
//
//        // Sort the chest using simulated clicks
//        new Thread(() -> {
//            try {
//                for (Slot currentSlot : chestSlots) {
//                    int currentIndex = currentSlot.index;
//                    ItemStack currentItem = currentSlot.getItem();
//                    ItemStack targetItem = targetMapping.get(currentIndex);
//
//                    // Skip if already in the correct slot
//                    if (ItemStack.isSameItemSameComponents(currentItem, targetItem)) {
//                        continue;
//                    }
//
//                    // Use AtomicInteger to allow mutation in lambda
//                    AtomicInteger sourceIndex = new AtomicInteger(-1);
//
//                    chestSlots.forEach(slot -> {
//                        if (ItemStack.isSameItemSameComponents(slot.getItem(), targetItem) && sourceIndex.get() == -1) {
//                            sourceIndex.set(slot.index);
//                        }
//                    });
//
//                    if (sourceIndex.get() == -1) {
//                        System.err.println("Failed to find source slot for item: " + targetItem);
//                        continue;
//                    }
//
//                    // Simulate click actions to move items
//                    this.minecraft.execute(() -> {
//                        // Step 1: Pick up the target item from the source slot
//                        this.minecraft.gameMode.handleInventoryMouseClick(
//                                this.menu.containerId,
//                                sourceIndex.get(),
//                                0,
//                                ClickType.PICKUP,
//                                this.minecraft.player
//                        );
//
//                        // Step 2: Place it in the target slot
//                        this.minecraft.gameMode.handleInventoryMouseClick(
//                                this.menu.containerId,
//                                currentIndex,
//                                0,
//                                ClickType.PICKUP,
//                                this.minecraft.player
//                        );
//
//                        // Step 3: Handle leftover items in carried slot (if any)
//                        if (!this.minecraft.player.containerMenu.getCarried().isEmpty()) {
//                            this.minecraft.gameMode.handleInventoryMouseClick(
//                                    this.menu.containerId,
//                                    -999, // Drop to the void
//                                    0,
//                                    ClickType.PICKUP,
//                                    this.minecraft.player
//                            );
//                        }
//                    });
//
//                    // Delay between actions to prevent desynchronization
//                    Thread.sleep(100);
//                }
//
//                // Final cleanup after sorting
//                this.minecraft.execute(() -> {
//                    if (!this.minecraft.player.containerMenu.getCarried().isEmpty()) {
//                        // Drop any leftover carried items
//                        this.minecraft.gameMode.handleInventoryMouseClick(
//                                this.menu.containerId,
//                                -999,
//                                0,
//                                ClickType.PICKUP,
//                                this.minecraft.player
//                        );
//                    }
//
//                    // Reset the sort button state
//                    resetSortButtonState();
//                    System.out.println("Chest sorting complete!");
//                });
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }).start();
//    }
//
//
//
//
//    @Unique
//    private String getRarityFromTooltip(ItemStack stack) {
//        if (stack.isEmpty()) return "Unknown";
//
//        List<Component> tooltip = stack.getTooltipLines(Item.TooltipContext.EMPTY, this.minecraft.player, TooltipFlag.NORMAL);
//        for (Component line : tooltip) {
//            String lineText = line.getString();
//            if (lineText.contains("ꐶ")) return "ꐶ"; // Common
//            if (lineText.contains("ꐸ")) return "ꐸ"; // Uncommon
//            if (lineText.contains("ꐹ")) return "ꐹ"; // Rare
//            if (lineText.contains("꒜")) return "꒜"; // Exclusive
//        }
//        return "Unknown";
//    }
//
//    @Unique
//    private void resetSortButtonState() {
//        this.renderables.forEach(widget -> {
//            if (widget instanceof Button button) {
//                button.setMessage(Component.literal("Sort")); // Reset text
//                button.active = true; // Enable the button again
//                button.setFocused(false); // Remove focus/visual glow
//            }
//        });
//    }
}
