package com.playstarnet.essentials.feat.ui;

import com.playstarnet.essentials.StarNetEssentials;
import com.playstarnet.essentials.feat.ext.AbstractContainerScreenAccessor;
import com.playstarnet.essentials.mixins.ext.ClientPacketListenerAccessor;
import com.playstarnet.essentials.util.StaticValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

public class FriendsListUI {
    private static ChestMenu oldMenu = null;
    public static void tick() {
        if (StaticValues.friendsCheck) return;
        Minecraft client = StarNetEssentials.client();

        if (client.screen instanceof ContainerScreen abstractContainerScreen) {
            ChestMenu menu = abstractContainerScreen.getMenu();
            if (oldMenu != null && oldMenu == menu) return;
            oldMenu = menu;

            Slot nextPage = null;
            for (Slot slot : menu.slots) {
                ItemStack itemStack = slot.getItem();
                CustomData tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, null);

                if (tag != null) {
                    Item item = itemStack.getItem();
                    String tagStr = tag.toString();
                    if (item == Items.PLAYER_HEAD && !tagStr.contains("Left click to Accept")) {
                        CompoundTag skull = tag.copyTag().getCompound("SkullOwner");
                        String name = skull.getString("Name");
                        if (!StaticValues.friends.contains(name)) StaticValues.friends.add(name);
                    } else if (item == Items.PAPER && tagStr.contains("→")) {
                        nextPage = slot;
                    }
                }
            }

            if (nextPage == null) {
                client.setScreen(null);
                StaticValues.friendsCheck = true;
                System.out.println(StaticValues.friends);
            } else {
                ((AbstractContainerScreenAccessor) abstractContainerScreen).se$slotChange(nextPage, 0, 0, ClickType.PICKUP);
            }
        } else if (client.getConnection() != null) {
            ((ClientPacketListenerAccessor) client.getConnection()).se$sendCommand("friend");
        }
    }
}