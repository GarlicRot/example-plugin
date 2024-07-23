package org.garlicplugin.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.rusherhack.core.setting.BooleanSetting;
import org.rusherhack.core.setting.NumberSetting;
import org.rusherhack.core.setting.EnumSetting;
import org.rusherhack.core.setting.Setting;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GarlicParticleModule {

    private final Minecraft mc = Minecraft.getInstance();
    private final Random random = new Random();

    // Main particle setting
    public final Setting<Boolean> garlicParticlesSetting = new BooleanSetting("GarlicParticles",
            "Enable garlic particle effects", false);

    // Map to store all particle options
    private final Map<String, ParticleOption> particleOptions = new HashMap<>();

    public GarlicParticleModule() {
        // Initialize particle options
        addParticleOption("Flame", ParticleTypes.FLAME);
        addParticleOption("Soul Fire Flame", ParticleTypes.SOUL_FIRE_FLAME);
        addParticleOption("Lava", ParticleTypes.LAVA);
        addParticleOption("Dripping Water", ParticleTypes.DRIPPING_WATER);
        addParticleOption("Falling Water", ParticleTypes.FALLING_WATER);
        addParticleOption("Smoke", ParticleTypes.SMOKE);
        addParticleOption("Large Smoke", ParticleTypes.LARGE_SMOKE);
        addParticleOption("Snowflake", ParticleTypes.SNOWFLAKE);
        addParticleOption("Dripping Honey", ParticleTypes.DRIPPING_HONEY);
        addParticleOption("Falling Nectar", ParticleTypes.FALLING_NECTAR);
        addParticleOption("Enchant", ParticleTypes.ENCHANT);
        addParticleOption("End Rod", ParticleTypes.END_ROD);
        addParticleOption("Portal", ParticleTypes.PORTAL);
        addParticleOption("Totem of Undying", ParticleTypes.TOTEM_OF_UNDYING);
        addParticleOption("Witch", ParticleTypes.WITCH);
        addParticleOption("Heart", ParticleTypes.HEART);
        addParticleOption("Note", ParticleTypes.NOTE);
        addParticleOption("Critical Hit", ParticleTypes.CRIT);
        addParticleOption("Explosion", ParticleTypes.EXPLOSION);
        addParticleOption("Firework", ParticleTypes.FIREWORK);

        // Add all particle options as sub-settings
        for (ParticleOption option : particleOptions.values()) {
            garlicParticlesSetting.addSubSettings(option.mainSetting);
        }
    }

    private void addParticleOption(String name, SimpleParticleType particleType) {
        particleOptions.put(name, new ParticleOption(name, particleType));
    }

    public void spawnParticles() {
        if (mc.player == null || mc.level == null || !garlicParticlesSetting.getValue())
            return;

        Vec3 playerPos = mc.player.position();

        for (ParticleOption option : particleOptions.values()) {
            if (option.mainSetting.getValue()) {
                spawnParticle(option, playerPos);
            }
        }
    }

    private void spawnParticle(ParticleOption option, Vec3 playerPos) {
        double x = playerPos.x;
        double y = playerPos.y + option.yOffset.getValue();
        double z = playerPos.z;

        for (int i = 0; i < option.count.getValue(); i++) {
            Vec3 offset = getOffsetForPattern(option, i, option.count.getValue());

            double speed = option.speed.getValue();
            Vec3 velocity = offset.normalize().scale(speed);

            mc.level.addParticle(option.particleType,
                    x + offset.x,
                    y + offset.y,
                    z + offset.z,
                    velocity.x,
                    velocity.y,
                    velocity.z);
        }
    }

    private Vec3 getOffsetForPattern(ParticleOption option, int currentIndex, int totalCount) {
        double radius = option.radius.getValue();
        double density = option.patternDensity.getValue();
        double variation = option.patternVariation.getValue();
        double wobble = option.wobble.getValue();
        double spin = option.spin.getValue();

        double t = option.reverse.getValue() ? 1.0 - ((double) currentIndex / totalCount)
                : (double) currentIndex / totalCount;
        double angle = t * 2 * Math.PI * density + spin * currentIndex;

        Vec3 baseOffset = Vec3.ZERO;
        switch (option.pattern.getValue()) {
            case CIRCLE:
                baseOffset = new Vec3(
                        Math.cos(angle) * radius,
                        Math.sin(angle * variation) * radius * 0.1,
                        Math.sin(angle) * radius);
                break;
            case SPHERE:
                double theta = random.nextDouble() * 2 * Math.PI;
                double phi = Math.acos(2 * random.nextDouble() - 1);
                baseOffset = new Vec3(
                        radius * Math.sin(phi) * Math.cos(theta),
                        radius * Math.sin(phi) * Math.sin(theta),
                        radius * Math.cos(phi));
                break;
            case CUBE:
                baseOffset = new Vec3(
                        (random.nextDouble() * 2 - 1) * radius,
                        (random.nextDouble() * 2 - 1) * radius,
                        (random.nextDouble() * 2 - 1) * radius);
                break;
            case HELIX:
                double helixRadius = radius * (1 - 0.3 * Math.sin(t * Math.PI * 4 * variation));
                baseOffset = new Vec3(
                        Math.cos(angle * 3) * helixRadius,
                        t * radius * 2 - radius,
                        Math.sin(angle * 3) * helixRadius);
                break;
            case CROWN:
                double crownHeight = radius * 0.5 * (1 + Math.sin(angle * 7 * variation));
                double crownRadius = radius * (1 + 0.2 * Math.cos(angle * 7 * variation));
                baseOffset = new Vec3(
                        Math.cos(angle) * crownRadius,
                        crownHeight,
                        Math.sin(angle) * crownRadius);
                break;
            case WINGS:
                double wingSpan = Math.sin(t * Math.PI * variation);
                double wingCurve = Math.sin(t * Math.PI * 2) * radius * 0.5;
                baseOffset = new Vec3(
                        Math.cos(angle) * radius * wingSpan,
                        wingCurve,
                        Math.sin(angle) * radius * 0.5 * (1 - t));
                break;
            case BUTTERFLY:
                double r = Math.exp(Math.cos(angle)) - 2 * Math.cos(4 * angle * variation)
                        + Math.pow(Math.sin(angle / 12), 5);
                baseOffset = new Vec3(
                        Math.sin(angle) * r * radius * 0.4,
                        Math.cos(angle) * r * radius * 0.4,
                        Math.sin(angle * 6 * density) * radius * 0.1);
                break;
            case HORNS:
                double hornAngle = t * Math.PI * variation;
                double hornRadius = radius * (1 - Math.pow(t, 2));
                double hornTwist = Math.sin(t * Math.PI * 4 * density) * radius * 0.2;
                baseOffset = new Vec3(
                        Math.cos(hornAngle) * hornRadius * (currentIndex < totalCount / 2 ? 1 : -1) + hornTwist,
                        Math.sin(hornAngle) * hornRadius,
                        hornTwist);
                break;
            case HALO:
                double haloRadius = radius * (0.7 + 0.3 * Math.sin(angle * 8 * variation));
                double haloHeight = radius * 1.2 + Math.sin(angle * 16 * density) * radius * 0.05;
                baseOffset = new Vec3(
                        Math.cos(angle) * haloRadius,
                        haloHeight,
                        Math.sin(angle) * haloRadius);
                break;
            case SPIRAL:
                double spiralRadius = t * radius * density;
                double spiralHeight = t * radius * 2 - radius;
                baseOffset = new Vec3(
                        Math.cos(angle * 5 * variation) * spiralRadius,
                        spiralHeight + Math.sin(angle * 10 * density) * radius * 0.1,
                        Math.sin(angle * 5 * variation) * spiralRadius);
                break;
        }

        // Apply wobble
        if (wobble > 0) {
            baseOffset = baseOffset.add(
                    (random.nextDouble() - 0.5) * wobble * radius,
                    (random.nextDouble() - 0.5) * wobble * radius,
                    (random.nextDouble() - 0.5) * wobble * radius);
        }

        return baseOffset;
    }

    public enum SpawnPattern {
        CIRCLE, SPHERE, CUBE, HELIX, CROWN, WINGS, BUTTERFLY, HORNS, HALO, SPIRAL
    }

    public class ParticleOption {
        private final SimpleParticleType particleType;
        public final Setting<Boolean> mainSetting;
        public final Setting<Integer> count;
        public final Setting<Double> radius;
        public final Setting<Double> yOffset;
        public final Setting<Double> speed;
        public final Setting<SpawnPattern> pattern;
        public final Setting<Double> patternDensity;
        public final Setting<Double> patternVariation;
        public final Setting<Boolean> reverse;
        public final Setting<Double> wobble;
        public final Setting<Double> spin;

        public ParticleOption(String name, SimpleParticleType particleType) {
            this.particleType = particleType;
            this.mainSetting = new BooleanSetting(name, name + " particles", false);
            this.count = new NumberSetting<>("Count", 20, 1, 100);
            this.radius = new NumberSetting<>("Radius", 1.0, 0.1, 5.0);
            this.yOffset = new NumberSetting<>("Y Offset", 1.0, -5.0, 5.0);
            this.speed = new NumberSetting<>("Speed", 0.1, 0.0, 2.0);
            this.pattern = new EnumSetting<>("Pattern", SpawnPattern.CIRCLE);
            this.patternDensity = new NumberSetting<>("Pattern Density", 1.0, 0.1, 10.0);
            this.patternVariation = new NumberSetting<>("Pattern Variation", 1.0, 0.1, 10.0);
            this.reverse = new BooleanSetting("Reverse", false);
            this.wobble = new NumberSetting<>("Wobble", 0.0, 0.0, 1.0);
            this.spin = new NumberSetting<>("Spin", 0.0, -5.0, 5.0);

            mainSetting.addSubSettings(count, radius, yOffset, speed, pattern, patternDensity, patternVariation,
                    reverse, wobble, spin);
        }
    }
}