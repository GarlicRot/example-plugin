package org.garlicplugin;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;
import org.rusherhack.client.api.feature.module.IModule;
import org.rusherhack.core.feature.IFeatureManager;
import org.rusherhack.client.api.system.IHudManager;
import org.rusherhack.client.api.system.IWindowManager;
import org.garlicplugin.modules.GarlicsPluginModule;
import org.garlicplugin.hud.GarlicSightHudElement;
import org.garlicplugin.windows.ParticlePreviewWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GarlicPlugin extends Plugin {
    private static final Logger LOGGER = LogManager.getLogger("GarlicPlugin");

    /* ModuleManager & Modules */
    private final IFeatureManager<IModule> moduleManager = RusherHackAPI.getModuleManager();
    private final GarlicsPluginModule garlicsPluginModule = new GarlicsPluginModule();

    /* HUD Element */
    private final GarlicSightHudElement garlicSightHudElement;

    /* Window */
    private ParticlePreviewWindow particlePreviewWindow;

    /* Managers */
    private final IHudManager hudManager = RusherHackAPI.getHudManager();
    private final IWindowManager windowManager = RusherHackAPI.getWindowManager();

    public GarlicPlugin() {
        this.garlicSightHudElement = new GarlicSightHudElement();
    }

    @Override
    public void onLoad() {
        LOGGER.info("Planting the Garlic Plugin...");

        // Register the module
        this.moduleManager.registerFeature(this.garlicsPluginModule);
        LOGGER.info("Garlic module sprouting...");

        // Register the HUD element
        this.hudManager.registerFeature(this.garlicSightHudElement);
        LOGGER.info("Garlic Sight HUD growing strong...");

        // Create and register the particle preview window
        this.particlePreviewWindow = new ParticlePreviewWindow();
        this.windowManager.registerFeature(this.particlePreviewWindow);
        LOGGER.info("Garlic Particle Preview Window blossoming...");

        // Register the command to open the particle preview window
        RusherHackAPI.getCommandManager().registerFeature(new ParticlePreviewCommand(this));
        LOGGER.info("Garlic commands ready for harvest!");

        LOGGER.info("Garlic Plugin fully grown and ready!");
    }

    @Override
    public void onUnload() {
        LOGGER.info("Harvesting the Garlic Plugin...");

        // Disable the module
        if (this.garlicsPluginModule.isToggled()) {
            this.garlicsPluginModule.toggle();
        }
        LOGGER.info("Garlic module uprooted.");

        // Disable the HUD element
        this.garlicSightHudElement.setToggled(false);
        LOGGER.info("Garlic Sight HUD cleared.");

        // Hide the particle preview window
        if (this.particlePreviewWindow != null) {
            this.particlePreviewWindow.setHidden(true);
            this.particlePreviewWindow.onClose();
        }
        LOGGER.info("Garlic Particle Preview Window stored away.");

        LOGGER.info("Garlic Plugin successfully harvested and stored!");
    }

    public ParticlePreviewWindow getParticlePreviewWindow() {
        return particlePreviewWindow;
    }
}