package net.ros.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class AABBRaytracer
{
    public static Optional<AxisAlignedBB> raytraceClosest(Vec3d start, Vec3d end, AxisAlignedBB... boxes)
    {
        AxisAlignedBB result = null;
        double closest = Double.MAX_VALUE;

        for (AxisAlignedBB box : boxes)
        {
            RayTraceResult rayTraceResult = raytrace(start, end, box);

            if (rayTraceResult != null && rayTraceResult.typeOfHit != RayTraceResult.Type.MISS)
            {
                double dist = rayTraceResult.hitVec.subtract(start).lengthSquared();

                if (dist < closest)
                {
                    closest = dist;
                    result = box;
                }
            }
        }

        return Optional.ofNullable(result);
    }

    public static RayTraceResult raytrace(Vec3d start, Vec3d end, AxisAlignedBB box)
    {
        return box.calculateIntercept(start, end);
    }

    public static double getBlockReach(EntityPlayer player)
    {
        return player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
    }

    public static Optional<AxisAlignedBB> raytraceClosest(EntityPlayer player, float partialTicks,
                                                          AxisAlignedBB... boxes)
    {
        Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d end = start.add(player.getLook(partialTicks).scale(getBlockReach(player)));
        return raytraceClosest(start, end, boxes);
    }
}
