package net.ros.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.ros.common.tile.machine.TileSolidBoiler;

public class BlockSolidBoiler extends BlockMultiModularMachine<TileSolidBoiler>
{
    public BlockSolidBoiler()
    {
        super("solid_boiler", Material.IRON, TileSolidBoiler::new, TileSolidBoiler.class);
    }

    @Override
    public void onBlockDestroyedByExplosion(final World w, final BlockPos pos, final Explosion exp)
    {
        super.onBlockDestroyedByExplosion(w, pos, exp);

        w.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 2, true);
    }
}
