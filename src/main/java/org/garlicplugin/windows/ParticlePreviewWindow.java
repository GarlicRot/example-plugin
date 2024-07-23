package org.garlicplugin.windows;

import org.rusherhack.client.api.feature.window.ResizeableWindow;
import org.rusherhack.client.api.ui.window.view.WindowView;
import org.rusherhack.core.setting.NumberSetting;
import org.rusherhack.client.api.setting.ColorSetting;
import org.rusherhack.client.api.render.IRenderer2D;
import org.rusherhack.client.api.render.font.IFontRenderer;

import java.awt.Color;
import java.util.ArrayList;

public class ParticlePreviewWindow extends ResizeableWindow {

    private final NumberSetting<Integer> particleCount = new NumberSetting<>("Particle Count", 100, 0, 1000);
    private final ColorSetting particleColor = new ColorSetting("Particle Color", Color.WHITE);
    private final ParticlePreviewView rootView;

    public ParticlePreviewWindow() {
        super("Particle Preview", 300, 300);
        this.rootView = new ParticlePreviewView();

        // Set minimum and maximum sizes
        this.setMinWidth(200);
        this.setMinHeight(200);
        this.setMaxWidth(600);
        this.setMaxHeight(600);
    }

    @Override
    public WindowView getRootView() {
        return this.rootView;
    }

    private class ParticlePreviewView extends WindowView {
        public ParticlePreviewView() {
            super("Particle Preview", ParticlePreviewWindow.this, new ArrayList<>());
        }

        @Override
        public void renderViewContent(double mouseX, double mouseY) {
            IRenderer2D renderer = getRenderer();
            IFontRenderer fontRenderer = getFontRenderer();

            // Draw background
            renderer.drawRectangle(0, 0, getWidth(), getHeight(), new Color(30, 30, 30, 200).getRGB());

            // Draw settings
            fontRenderer.drawString("Particle Count: " + particleCount.getValue(), 5, 5, -1);
            fontRenderer.drawString("Particle Color:", 5, 20, -1);
            renderer.drawRectangle(100, 20, 20, 10, particleColor.getValue().getRGB());

            // TODO: Add actual particle rendering here
        }
    }

    // Getter for settings
    public NumberSetting<Integer> getParticleCount() {
        return particleCount;
    }

    public ColorSetting getParticleColor() {
        return particleColor;
    }
}