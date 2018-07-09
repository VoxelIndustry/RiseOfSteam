package net.ros.client.render.model.obj;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;
import net.ros.common.grid.node.PipeSize;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PipeOBJStates
{
    private static final BiMap<Pair<PipeSize, String>, ROSOBJState> variants = HashBiMap.create();

    public static ROSOBJState getVisibilityState(PipeSize size, EnumFacing... facings)
    {
        return getVisibilityState(size, false, facings);
    }

    public static ROSOBJState getVisibilityState(PipeSize size, boolean forceCenter, EnumFacing... facings)
    {
        String key = getVariantKey(forceCenter, facings);

        Pair<PipeSize, String> variantKey = Pair.of(size, key);

        if (!variants.containsKey(variantKey))
            variants.put(variantKey, buildVisibilityState(size, forceCenter, facings));
        return variants.get(variantKey);
    }

    public static ROSOBJState getVisibilityState(PipeSize size, String key)
    {
        Pair<PipeSize, String> variantKey = Pair.of(size, key);

        if (!variants.containsKey(variantKey))
        {
            List<EnumFacing> facings = new ArrayList<>();

            if (key.contains("x+"))
                facings.add(EnumFacing.EAST);
            if (key.contains("x-"))
                facings.add(EnumFacing.WEST);
            if (key.contains("y+"))
                facings.add(EnumFacing.UP);
            if (key.contains("y-"))
                facings.add(EnumFacing.DOWN);
            if (key.contains("z+"))
                facings.add(EnumFacing.SOUTH);
            if (key.contains("z-"))
                facings.add(EnumFacing.NORTH);

            variants.put(variantKey, buildVisibilityState(size, key.startsWith("c"),
                    facings.toArray(new EnumFacing[0])));
        }
        return variants.get(variantKey);
    }

    public static Pair<PipeSize, String> getVariantKey(ROSOBJState state)
    {
        return variants.inverse().get(state);
    }

    public static String getVariantKey(boolean forceCenter, EnumFacing... facings)
    {
        StringBuilder rtn = new StringBuilder(13);

        if (isStraight(facings) && forceCenter)
            rtn.append("c");
        if (isConnected(EnumFacing.EAST, facings))
            rtn.append("x+");
        if (isConnected(EnumFacing.WEST, facings))
            rtn.append("x-");
        if (isConnected(EnumFacing.UP, facings))
            rtn.append("y+");
        if (isConnected(EnumFacing.DOWN, facings))
            rtn.append("y-");
        if (isConnected(EnumFacing.SOUTH, facings))
            rtn.append("z+");
        if (isConnected(EnumFacing.NORTH, facings))
            rtn.append("z-");
        return rtn.toString();
    }

    private static ROSOBJState buildVisibilityState(PipeSize size, boolean forceCenter, EnumFacing... facings)
    {
        if (size == PipeSize.LARGE)
        {
            if (facings.length == 1 || isStraight(facings))
            {
                TRSRTransformation transform;

                if (isConnected(EnumFacing.NORTH, facings))
                    transform = TRSRTransformation.from(EnumFacing.NORTH);
                else if (isConnected(EnumFacing.SOUTH, facings))
                    transform = TRSRTransformation.from(EnumFacing.NORTH);
                else if (isConnected(EnumFacing.WEST, facings))
                    transform = TRSRTransformation.from(EnumFacing.EAST);
                else if (isConnected(EnumFacing.EAST, facings))
                    transform = TRSRTransformation.from(EnumFacing.EAST);
                else if (isConnected(EnumFacing.DOWN, facings))
                    transform = TRSRTransformation.from(EnumFacing.UP);
                else
                    transform = TRSRTransformation.from(EnumFacing.UP);

                return new ROSOBJState(Collections.singletonList("center"), false, transform);
            }
            return new ROSOBJState(Collections.singletonList("center"), true);
        }

        List<String> parts = new ArrayList<>();

        if (facings.length == 0)
        {
            parts.add("armx1");
            parts.add("armx2");
            parts.add("army1");
            parts.add("army2");
            parts.add("armz1");
            parts.add("armz2");
            parts.add("straightx");
            parts.add("straighty");
            parts.add("straightz");
        }
        else if (isStraight(facings))
        {
            if (!forceCenter)
                parts.add("center");
            parts.add("armx1");
            parts.add("armx2");

            parts.add("army1");
            parts.add("army2");

            parts.add("armz1");
            parts.add("armz2");

            if (isConnected(EnumFacing.WEST, facings))
            {
                parts.add("straighty");
                parts.add("straightz");
            }
            else if (isConnected(EnumFacing.NORTH, facings))
            {
                parts.add("straighty");
                parts.add("straightx");
            }
            else if (isConnected(EnumFacing.UP, facings))
            {
                parts.add("straightx");
                parts.add("straightz");
            }
        }
        else
        {
            parts.add("straightx");
            parts.add("straighty");
            parts.add("straightz");

            if (!isConnected(EnumFacing.UP, facings))
                parts.add("army1");
            if (!isConnected(EnumFacing.DOWN, facings))
                parts.add("army2");
            if (!isConnected(EnumFacing.NORTH, facings))
                parts.add("armz1");
            if (!isConnected(EnumFacing.SOUTH, facings))
                parts.add("armz2");
            if (!isConnected(EnumFacing.EAST, facings))
                parts.add("armx1");
            if (!isConnected(EnumFacing.WEST, facings))
                parts.add("armx2");
        }
        return new ROSOBJState(parts, false);
    }

    private static boolean isConnected(EnumFacing facing, EnumFacing... facings)
    {
        return ArrayUtils.contains(facings, facing);
    }

    private static boolean isStraight(EnumFacing... facings)
    {
        if (facings.length == 2)
            return isConnected(EnumFacing.NORTH, facings) && isConnected(EnumFacing.SOUTH, facings)
                    || isConnected(EnumFacing.WEST, facings) && isConnected(EnumFacing.EAST, facings)
                    || isConnected(EnumFacing.UP, facings) && isConnected(EnumFacing.DOWN, facings);
        return false;
    }
}
