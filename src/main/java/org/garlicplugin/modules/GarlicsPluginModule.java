package org.garlicplugin.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.rusherhack.client.api.feature.module.ModuleCategory;
import org.rusherhack.client.api.feature.module.ToggleableModule;
import org.rusherhack.core.setting.BooleanSetting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rusherhack.client.api.events.client.EventUpdate;
import org.rusherhack.core.event.subscribe.Subscribe;

public class GarlicsPluginModule extends ToggleableModule {
    private static final Logger LOGGER = LogManager.getLogger("GarlicPlugin");
    private final Minecraft minecraft = Minecraft.getInstance();

    // Main settings
    private final BooleanSetting lightningPop = new BooleanSetting("LightningPop", "Enable lightning effects", true);

    // Sub settings for LightningPop
    private final BooleanSetting totemPop = new BooleanSetting("TotemPop", "Lightning strike on totem pop", true);
    private final BooleanSetting totemPopSelf = new BooleanSetting("Self", "Include your own totem pops", true);
    private final BooleanSetting playerDeath = new BooleanSetting("PlayerDeath", "Lightning strike on player death",
            true);

    // Particle settings
    private final GarlicParticleModule particleModule;

    public GarlicsPluginModule() {
        super("Garlic'sPlugin", "Various plugin features", ModuleCategory.MISC);

        // Set up the setting hierarchy for lightning
        this.lightningPop.addSubSettings(this.totemPop, this.playerDeath);
        this.totemPop.addSubSettings(this.totemPopSelf);

        // Initialize GarlicParticleModule
        this.particleModule = new GarlicParticleModule();

        // Register all settings
        this.registerSettings(this.lightningPop, this.particleModule.garlicParticlesSetting);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (this.isToggled()) {
            particleModule.spawnParticles();
        }
    }

    public void onTotemPop(Player player) {
        LOGGER.debug("onTotemPop called for player: " + player.getName().getString());
        if (!this.isToggled() || !lightningPop.getValue() || !totemPop.getValue()) {
            LOGGER.debug("Lightning pop skipped due to settings");
            return;
        }
        if (!totemPopSelf.getValue() && player == minecraft.player) {
            LOGGER.debug("Lightning pop skipped for self");
            return;
        }
        LOGGER.debug("Attempting to spawn lightning for totem pop");
        spawnLightning(player);
    }

    public void onPlayerDeath(Player player) {
        LOGGER.debug("onPlayerDeath called for player: " + player.getName().getString());
        if (!this.isToggled() || !lightningPop.getValue() || !playerDeath.getValue()) {
            LOGGER.debug("Lightning on death skipped due to settings");
            return;
        }
        LOGGER.debug("Attempting to spawn lightning for player death");
        spawnLightning(player);
    }

    private void spawnLightning(Player player) {
        if (minecraft.level == null || !minecraft.level.isClientSide) {
            LOGGER.debug("Cannot spawn lightning: level null or not on client side");
            return;
        }
        LightningBolt lightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, minecraft.level);
        lightningBolt.setPos(player.position());
        boolean added = minecraft.level.addFreshEntity(lightningBolt);
        LOGGER.debug("Lightning bolt added to world: " + added);
        // Add a particle effect
        minecraft.level.addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 1, player.getZ(), 0, 0, 0);
        // Play a sound
        minecraft.level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.WEATHER, 10000.0F, 0.8F + minecraft.level.random.nextFloat() * 0.2F, false);
    }
}