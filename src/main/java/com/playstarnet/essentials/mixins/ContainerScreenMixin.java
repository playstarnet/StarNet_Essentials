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

    @Override
    protected void init() {
        super.init();

        // Add the "Sort" button only if the chest name contains specific symbols
        String chestTitle = this.title.getString();
        if (chestTitle.contains("ꑂ") || chestTitle.contains("ꑄ") || chestTitle.contains("ꐼ")) {
            Button sortButton = Button.builder(Component.literal("Sort"), button -> {
                        sortChestBySimulatingClicks();
                        button.setMessage(Component.literal("Sort"));
                    })
                    .bounds(this.leftPos + 110, this.topPos - 35, 60, 20) // Adjust position
                    .tooltip(Tooltip.create(
                            Component.literal("This feature is experimental!\nRecommended to use on your island.")
                    ))
                    .build();

            this.addRenderableWidget(sortButton);

            this.addRenderableWidget(sortButton);
        }
    }

    @Unique
    private void sortChestBySimulatingClicks() {
        if (!(this.menu instanceof ChestMenu chestMenu)) {
            System.out.println("Not a chest menu");
            return;
        }

        // Get all chest slots
        List<Slot> chestSlots = this.menu.slots.stream()
                .filter(slot -> slot.container == chestMenu.getContainer())
                .collect(Collectors.toList());

        if (chestSlots.isEmpty()) return;

        // Define rarity order
        Map<String, Integer> rarityOrder = Map.of(
                "ꐶ", 0, // Common
                "ꐸ", 1, // Uncommon
                "ꐹ", 2, // Rare
                "꒜", 3  // Exclusive
        );

        // Collect items and sort them
        List<ItemStack> sortedItems = chestSlots.stream()
                .map(Slot::getItem)
                .sorted(Comparator.comparingInt(stack -> {
                    String rarity = getRarityFromTooltip(stack);
                    return rarityOrder.getOrDefault(rarity, Integer.MAX_VALUE);
                }))
                .collect(Collectors.toList());

        // Track empty slots dynamically
        List<Integer> emptySlots = chestSlots.stream()
                .filter(slot -> slot.getItem().isEmpty())
                .map(slot -> slot.index)
                .collect(Collectors.toList());

        // Start sorting
        new Thread(() -> {
            try {
                for (int i = 0; i < chestSlots.size(); i++) {
                    final int currentIndex = i; // Capture index as final
                    Slot currentSlot = chestSlots.get(currentIndex);
                    ItemStack currentItem = currentSlot.getItem();
                    ItemStack targetItem = sortedItems.get(currentIndex);
                    int targetIndex = currentSlot.index;

                    // Skip if the item is already in the correct slot
                    if (ItemStack.isSameItemSameComponents(currentItem, targetItem)) {
                        continue;
                    }

                    // Find the source slot for the target item
                    final int[] sourceIndex = {-1}; // Wrap in an array to allow mutation
                    for (int j = 0; j < chestSlots.size(); j++) {
                        if (ItemStack.isSameItemSameComponents(chestSlots.get(j).getItem(), targetItem)) {
                            sourceIndex[0] = chestSlots.get(j).index;
                            break;
                        }
                    }

                    if (sourceIndex[0] == -1) {
                        System.err.println("Failed to find source slot for item: " + targetItem);
                        continue;
                    }

                    // Handle item movement
                    this.minecraft.execute(() -> {
                        // Ensure carried slot is empty before starting
                        if (!this.minecraft.player.containerMenu.getCarried().isEmpty()) {
                            if (!emptySlots.isEmpty()) {
                                int tempSlotIndex = emptySlots.remove(0);
                                this.minecraft.gameMode.handleInventoryMouseClick(
                                        this.menu.containerId,
                                        tempSlotIndex,
                                        0,
                                        ClickType.PICKUP,
                                        this.minecraft.player
                                );
                            } else {
                                // Drop carried item if no empty slot available
                                this.minecraft.gameMode.handleInventoryMouseClick(
                                        this.menu.containerId,
                                        -999,
                                        0,
                                        ClickType.PICKUP,
                                        this.minecraft.player
                                );
                            }
                        }

                        // Pick up the item from the source slot
                        this.minecraft.gameMode.handleInventoryMouseClick(
                                this.menu.containerId,
                                sourceIndex[0], // Access the wrapped source index
                                0,
                                ClickType.PICKUP,
                                this.minecraft.player
                        );

                        // Place it in the target slot
                        this.minecraft.gameMode.handleInventoryMouseClick(
                                this.menu.containerId,
                                targetIndex,
                                0,
                                ClickType.PICKUP,
                                this.minecraft.player
                        );
                    });

                    // Update empty slots list dynamically
                    if (currentItem.isEmpty()) {
                        emptySlots.add(currentSlot.index);
                    }
                    emptySlots.remove((Integer) targetIndex);

                    // Delay between actions to avoid server desync
                    Thread.sleep(100);
                }

                // Perform final cleanup
                this.minecraft.execute(() -> {
                    for (int i = 0; i < chestSlots.size(); i++) {
                        Slot slot = chestSlots.get(i);
                        ItemStack current = slot.getItem();
                        ItemStack target = sortedItems.get(i);

                        // Fix any mismatches
                        if (!ItemStack.isSameItemSameComponents(current, target)) {
                            this.minecraft.gameMode.handleInventoryMouseClick(
                                    this.menu.containerId,
                                    slot.index,
                                    0,
                                    ClickType.PICKUP,
                                    this.minecraft.player
                            );
                            this.minecraft.gameMode.handleInventoryMouseClick(
                                    this.menu.containerId,
                                    chestSlots.get(i).index,
                                    0,
                                    ClickType.PICKUP,
                                    this.minecraft.player
                            );
                        }
                    }

                    // Handle leftover carried item
                    if (!this.minecraft.player.containerMenu.getCarried().isEmpty()) {
                        if (!emptySlots.isEmpty()) {
                            int tempSlotIndex = emptySlots.remove(0);
                            this.minecraft.gameMode.handleInventoryMouseClick(
                                    this.menu.containerId,
                                    tempSlotIndex,
                                    0,
                                    ClickType.PICKUP,
                                    this.minecraft.player
                            );
                        } else {
                            this.minecraft.gameMode.handleInventoryMouseClick(
                                    this.menu.containerId,
                                    -999,
                                    0,
                                    ClickType.PICKUP,
                                    this.minecraft.player
                            );
                        }
                    }

                    System.out.println("Chest sorting complete!");
                    resetSortButtonState();
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }


    @Unique
    private String getRarityFromTooltip(ItemStack stack) {
        if (stack.isEmpty()) return "Unknown";

        List<Component> tooltip = stack.getTooltipLines(Item.TooltipContext.EMPTY, this.minecraft.player, TooltipFlag.NORMAL);
        for (Component line : tooltip) {
            String lineText = line.getString();
            if (lineText.contains("ꐶ")) return "ꐶ"; // Common
            if (lineText.contains("ꐸ")) return "ꐸ"; // Uncommon
            if (lineText.contains("ꐹ")) return "ꐹ"; // Rare
            if (lineText.contains("꒜")) return "꒜"; // Exclusive
        }
        return "Unknown";
    }

    @Unique
    private void resetSortButtonState() {
        this.renderables.forEach(widget -> {
            if (widget instanceof Button button) {
                button.setMessage(Component.literal("Sort")); // Reset text
                button.active = true; // Enable the button again
                button.setFocused(false); // Remove focus/visual glow
            }
        });
    }
}
