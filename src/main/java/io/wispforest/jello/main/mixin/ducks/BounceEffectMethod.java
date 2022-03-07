package io.wispforest.jello.main.mixin.ducks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface BounceEffectMethod {

    Logger LOGGER = LogManager.getLogger("BOUNCE");

    static void bounce(LivingEntity livingEntity) {
        World world = livingEntity.getWorld();

        Vec3d vec3d = livingEntity.getVelocity();
        if (vec3d.y < 0.0 && world.isClient) {
            double d = 1.0; //entity instanceof LivingEntity ? 1.0 : 0.8;

            Vec3d rotationVector = livingEntity.getRotationVector();
            double horizontalVel = vec3d.horizontalLength();



            if(livingEntity.isPlayer() && world.getTime() % 20 == 0){
                LOGGER.info(world.getTime());
                LOGGER.info("1: Player's Rotation Vec: [" + rotationVector+ "]");
                LOGGER.info("2: Player's Velocity: [" + vec3d + "]");
                LOGGER.info("3: Player's Horizontal Velocity: [" + horizontalVel + "]");
            }

            //double xzMultiplier = horizontalVel / 2000;

            livingEntity.setVelocity(0, vec3d.y * d, 0);

            //livingEntity.setVelocity(vec3d.x * Math.min(1.5, xzMultiplier), (-1 * vec3d.y /* / Math.min(1.0, xzMultiplier)*/), vec3d.z * Math.min(1.5, xzMultiplier));
        }

    }
}
