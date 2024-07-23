package org.garlicplugin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.garlicplugin.modules.GarlicsPluginModule;
import org.rusherhack.client.api.events.network.EventPacket;
import org.rusherhack.core.event.subscribe.Subscribe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventHandler {
    private static final Logger LOGGER = LogManager.getLogger("GarlicPlugin");
    private final Minecraft minecraft = Minecraft.getInstance();
    private final GarlicsPluginModule garlicsPluginModule;

    public EventHandler(GarlicsPluginModule garlicsPluginModule) {
        this.garlicsPluginModule = garlicsPluginModule;
        LOGGER.info("EventHandler initialized with GarlicsPluginModule");
    }

    @Subscribe
    private void onPacket(EventPacket.Receive event) {
        if (!garlicsPluginModule.isToggled()) {
            LOGGER.debug("GarlicsPluginModule is not toggled, skipping packet processing");
            return;
        }

        final Packet<?> packet = event.getPacket();
        if (!(packet instanceof ClientboundEntityEventPacket entityPacket)) {
            return;
        }

        if (this.minecraft.level == null) {
            LOGGER.debug("Minecraft level is null, skipping packet processing");
            return;
        }

        byte eventId = entityPacket.getEventId();
        LOGGER.debug("Received entity event packet with ID: " + eventId);

        boolean isTotemPop = (eventId == 35);
        boolean isPlayerDeath = (eventId == 3);

        if (!isTotemPop && !isPlayerDeath) {
            LOGGER.debug("Event is neither totem pop nor player death, skipping");
            return;
        }

        final Entity entity = entityPacket.getEntity(this.minecraft.level);
        if (!(entity instanceof Player player)) {
            LOGGER.debug("Entity is not a player, skipping");
            return;
        }

        if (isTotemPop) {
            LOGGER.debug("Totem pop detected for player: " + player.getName().getString());
            this.garlicsPluginModule.onTotemPop(player);
        } else if (isPlayerDeath) {
            LOGGER.debug("Player death detected for player: " + player.getName().getString());
            this.garlicsPluginModule.onPlayerDeath(player);
        }
    }
}