package net.qbar.client.fx;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public class SteamParticleHelper
{
    public static void createSmallSteamJet(World w, BlockPos pos, BlockPos target, boolean small)
    {
        Random rand = w.rand;

        BlockPos offset = target.subtract(pos);

        for (int i = 0; i < 20; i++)
        {
            Vector3f substract = new Vector3f(offset.getX(), offset.getY(), offset.getZ());

            substract.x += (rand.nextFloat() - 0.5) / 4;
            substract.y += (rand.nextFloat() - 0.5) / 4;
            substract.z += (rand.nextFloat() - 0.5) / 4;

            float xSpeed = rand.nextInt(15) + 5;
            float ySpeed = rand.nextInt(15) + 5;
            float zSpeed = rand.nextInt(15) + 5;

            w.spawnParticle(small ? EnumParticleTypes.SMOKE_NORMAL : EnumParticleTypes.SMOKE_LARGE,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    substract.x / xSpeed, substract.y / ySpeed, substract.z / zSpeed);
        }
    }
}
