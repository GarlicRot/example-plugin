package org.garlicplugin;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.core.feature.IFeatureManager;
import org.rusherhack.client.api.system.IHudManager;
import org.garlicplugin.modules.GarlicsPluginModule;
import org.garlicplugin.hud.GarlicSightHudElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GarlicPlugin extends Plugin {
    private static final Logger LOGGER = LogManager.getLogger("GarlicPlugin");

    /* ModuleManager & Modules */
    private final IFeatureManager<IModule> moduleManager = RusherHackAPI.getModuleManager();
    private final GarlicsPluginModule garlicsPluginModule = new GarlicsPluginModule();

    /* HUD Element */
    private final GarlicSightHudElement garlicSightHudElement;

    /* HUD Manager */
    private final IHudManager hudManager = RusherHackAPI.getHudManager();

    public GarlicPlugin() {
        this.garlicSightHudElement = new GarlicSightHudElement();
    }

    @Override
    public void onLoad() {
        LOGGER.info("Garlic Plugin Initialized!");

        // Register the module
        this.moduleManager.registerFeature(this.garlicsPluginModule);

        // Register the HUD element
        this.hudManager.registerFeature(this.garlicSightHudElement);
    }

    @Override
    public void onUnload() {
        LOGGER.info("Garlic Plugin unloaded!");

        // Disable the module
        if (this.garlicsPluginModule.isToggled()) {
            this.garlicsPluginModule.toggle();
        }

        // Disable the HUD element
        this.garlicSightHudElement.setToggled(false);
    }
}