package com.playstarnet.essentials.util;

import com.playstarnet.essentials.StarNetEssentials;
import org.lwjgl.glfw.GLFW;

public class KeyboardManager {
    private boolean rightBtnPressedLastTick = false;

    public boolean isMouseKey(int button) {
        boolean isPressed = GLFW.glfwGetMouseButton(StarNetEssentials.client().getWindow().getWindow(), button) == GLFW.GLFW_PRESS;

        boolean result = !isPressed && rightBtnPressedLastTick;
        rightBtnPressedLastTick = isPressed;

        return result;
    }
}