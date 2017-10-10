package net.qbar.client;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleHelper
{
    public static void spawnParticles(final EnumParticleTypes particle, World w, BlockPos origin)
    {
        final int rand = w.rand.nextInt(5);

        switch (rand)
        {
            case 0:
                w.spawnParticle(particle, origin.getX(),
                        origin.getY() + w.rand.nextFloat() / 2 + 0.2,
                        origin.getZ() + .25 + w.rand.nextFloat() / 2, -0.01, 0.1f, 0);
                w.spawnParticle(particle, origin.getX(),
                        origin.getY() + w.rand.nextFloat() / 2 + 0.2,
                        origin.getZ() + .25 + w.rand.nextFloat() / 2, -0.01, 0.1f, 0);
                break;
            case 1:
                w.spawnParticle(particle, origin.getX() + .25 + w.rand.nextFloat() / 2,
                        origin.getY() + w.rand.nextFloat() / 2 + 0.2, origin.getZ(), 0, 0.1f, -0.01);
                w.spawnParticle(particle, origin.getX() + .25 + w.rand.nextFloat() / 2,
                        origin.getY() + w.rand.nextFloat() / 2 + 0.2, origin.getZ(), 0, 0.1f, -0.01);
                break;
            case 2:
                w.spawnParticle(particle, origin.getX() + 1,
                        origin.getY() + w.rand.nextFloat() / 2 + 0.2,
                        origin.getZ() + .25 + w.rand.nextFloat() / 2, 0.01, 0.1f, 0);
                w.spawnParticle(particle, origin.getX() + 1,
                        origin.getY() + w.rand.nextFloat() / 2 + 0.2,
                        origin.getZ() + .25 + w.rand.nextFloat() / 2, 0.01, 0.1f, 0);
                break;
            case 3:
                w.spawnParticle(particle, origin.getX() + .25 + w.rand.nextFloat() / 2,
                        origin.getY() + w.rand.nextFloat() / 2 + 0.2, origin.getZ() + 1, 0, 0.1f, 0.01);
                w.spawnParticle(particle, origin.getX() + .25 + w.rand.nextFloat() / 2,
                        origin.getY() + w.rand.nextFloat() / 2 + 0.2, origin.getZ() + 1, 0, 0.1f, 0.01);
                break;
            case 4:
                w.spawnParticle(particle, origin.getX() + .25 + w.rand.nextFloat() / 2,
                        origin.getY() + 1, origin.getZ() + w.rand.nextFloat() / 2, 0.01, 0.1f, 0.01);
                w.spawnParticle(particle, origin.getX() + .25 + w.rand.nextFloat() / 2,
                        origin.getY() + 1, origin.getZ() + w.rand.nextFloat() / 2, 0.01, 0.1f, 0.01);
                break;
            default:
                break;
        }
    }
}
