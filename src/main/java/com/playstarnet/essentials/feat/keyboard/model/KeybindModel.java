package com.playstarnet.essentials.feat.keyboard.model;

import com.mojang.blaze3d.platform.InputConstants;
import com.playstarnet.essentials.feat.ext.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public enum KeybindModel {
    POUCH(
            "key.se.pouch",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            KeybindCategoryModel.STARNET_ESSENTIALS.translationString
    ),
    PROFILE(
            "key.se.profile",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            KeybindCategoryModel.STARNET_ESSENTIALS.translationString
    ),
    MAIL(
            "key.se.mail",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            KeybindCategoryModel.STARNET_ESSENTIALS.translationString
    ),
    MAP(
            "key.se.map",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            KeybindCategoryModel.STARNET_ESSENTIALS.translationString
    ),
    ISLAND(
            "key.se.island",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            KeybindCategoryModel.STARNET_ESSENTIALS.translationString
    ),
    SPAWN(
            "key.se.spawn",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            KeybindCategoryModel.STARNET_ESSENTIALS.translationString
    ),
    SETTINGS(
            "key.se.settings",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            KeybindCategoryModel.STARNET_ESSENTIALS.translationString
    );

    public final String translationString;
    public final InputConstants.Type type;
    public final int keyCode;
    public final String category;
    public final KeyMapping keyMapping;

    public boolean isDown() {
        return GLFW.glfwGetKey(GLFW.glfwGetCurrentContext(), ((KeyMappingAccessor) this.keyMapping).getKey().getValue()) == GLFW.GLFW_PRESS;
    }

    KeybindModel(String translationString, InputConstants.Type type, int keyCode, String category) {
        this.translationString = translationString;
        this.type = type;
        this.keyCode = keyCode;
        this.category = category;
        this.keyMapping = new KeyMapping(translationString, type, keyCode, category);
    }
}
