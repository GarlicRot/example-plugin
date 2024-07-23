package org.garlicplugin.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.block.state.BlockState;
import org.rusherhack.client.api.feature.hud.TextHudElement;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.StringSetting;

public class GarlicSightHudElement extends TextHudElement {

    private final Minecraft minecraft = Minecraft.getInstance();

    private final StringSetting titleSetting = new StringSetting("Title", "GarlicSight");

    // Block settings
    private final BooleanSetting blockInfo = new BooleanSetting("Block Info", true);
    private final BooleanSetting blockTitle = new BooleanSetting("'Block:' Title", true);
    private final BooleanSetting blockPosition = new BooleanSetting("Block Position", false);
    private final BooleanSetting positionTitle = new BooleanSetting("'Pos:' Title", true);

    // Entity settings
    private final BooleanSetting entityInfo = new BooleanSetting("Entity Info", true);
    private final BooleanSetting entityTitle = new BooleanSetting("'Entity:' Title", true);
    private final BooleanSetting entityHealth = new BooleanSetting("Entity Health", true);
    private final BooleanSetting healthTitle = new BooleanSetting("'Health:' Title", true);

    public GarlicSightHudElement() {
        super("GarlicSight", true);

        // Set up sub-settings
        blockInfo.addSubSettings(blockTitle, blockPosition);
        blockPosition.addSubSettings(positionTitle);
        entityInfo.addSubSettings(entityTitle, entityHealth);
        entityHealth.addSubSettings(healthTitle);

        this.registerSettings(titleSetting, blockInfo, entityInfo);

        // Set the snap point to TOP_LEFT
        this.setSnapPoint(SnapPoint.TOP_LEFT);
    }

    @Override
    public String getLabel() {
        return titleSetting.getValue();
    }

    @Override
    public String getText() {
        if (minecraft.level == null || minecraft.player == null) {
            return "";
        }

        HitResult hitResult = minecraft.hitResult;
        if (hitResult == null) {
            return "";
        }

        StringBuilder info = new StringBuilder();

        switch (hitResult.getType()) {
            case BLOCK:
                if (blockInfo.getValue()) {
                    BlockHitResult blockHit = (BlockHitResult) hitResult;
                    BlockState blockState = minecraft.level.getBlockState(blockHit.getBlockPos());
                    if (blockTitle.getValue()) {
                        info.append("Block: ");
                    }
                    info.append(blockState.getBlock().getName().getString());
                    if (blockPosition.getValue()) {
                        info.append('\n');
                        if (positionTitle.getValue()) {
                            info.append("Pos: ");
                        }
                        info.append(blockHit.getBlockPos().toShortString());
                    }
                }
                break;
            case ENTITY:
                if (entityInfo.getValue()) {
                    EntityHitResult entityHit = (EntityHitResult) hitResult;
                    Entity entity = entityHit.getEntity();
                    if (entityTitle.getValue()) {
                        info.append("Entity: ");
                    }
                    info.append(entity.getName().getString());
                    if (entity instanceof LivingEntity && entityHealth.getValue()) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        info.append('\n');
                        if (healthTitle.getValue()) {
                            info.append("Health: ");
                        }
                        info.append(
                                String.format("%.1f / %.1f", livingEntity.getHealth(), livingEntity.getMaxHealth()));
                    }
                }
                break;
            default:
                return "";
        }

        return info.toString();
    }

    @Override
    public double getWidth() {
        double textWidth = super.getWidth();
        double labelWidth = this.getFontRenderer().getStringWidth(this.getLabel());
        return Math.max(textWidth, labelWidth);
    }
}