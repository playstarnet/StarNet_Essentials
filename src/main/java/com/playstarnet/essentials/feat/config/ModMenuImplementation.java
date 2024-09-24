package com.playstarnet.essentials.feat.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.playstarnet.essentials.feat.ui.ConfigUI;

public class ModMenuImplementation implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigUI::new;
    }
}
