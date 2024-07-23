package org.garlicplugin;

import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.core.command.annotations.CommandExecutor;
import org.garlicplugin.windows.ParticlePreviewWindow;

public class ParticlePreviewCommand extends Command {

    private final GarlicPlugin plugin;

    public ParticlePreviewCommand(GarlicPlugin plugin) {
        super("particlepreview", "Opens or manages the particle preview window");
        this.plugin = plugin;
    }

    @CommandExecutor
    private String openWindow() {
        ParticlePreviewWindow window = this.plugin.getParticlePreviewWindow();
        if (window != null) {
            window.setHidden(false);
            return "Opened Particle Preview Window";
        } else {
            return "Particle preview window is not available.";
        }
    }

    @CommandExecutor(subCommand = "close")
    private String closeWindow() {
        ParticlePreviewWindow window = this.plugin.getParticlePreviewWindow();
        if (window != null) {
            window.setHidden(true);
            return "Closed Particle Preview Window";
        } else {
            return "Particle preview window is not available.";
        }
    }

    @CommandExecutor(subCommand = "toggle")
    private String toggleWindow() {
        ParticlePreviewWindow window = this.plugin.getParticlePreviewWindow();
        if (window != null) {
            boolean newState = !window.isHidden();
            window.setHidden(newState);
            return newState ? "Closed Particle Preview Window" : "Opened Particle Preview Window";
        } else {
            return "Particle preview window is not available.";
        }
    }

    @CommandExecutor(subCommand = "setcount")
    @CommandExecutor.Argument("count")
    private String setParticleCount(int count) {
        ParticlePreviewWindow window = this.plugin.getParticlePreviewWindow();
        if (window != null) {
            window.getParticleCount().setValue(count);
            return "Set particle count to " + count;
        } else {
            return "Particle preview window is not available.";
        }
    }
}