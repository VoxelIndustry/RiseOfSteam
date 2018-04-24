package net.qbar.common.machine;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.qbar.common.machine.module.impl.SteamModule;
import net.qbar.common.multiblock.MultiblockComponent;
import net.qbar.common.network.SteamEffectPacket;
import net.qbar.common.steam.ISteamHandler;
import net.qbar.common.tile.machine.TileModularMachine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SteamOverloadManager
{
    private static SteamOverloadManager INSTANCE;

    public static SteamOverloadManager getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new SteamOverloadManager();
        return INSTANCE;
    }

    private List<TileModularMachine> machines;

    private SteamOverloadManager()
    {
        this.machines = new ArrayList<>();
    }

    public void tick()
    {
        Iterator<TileModularMachine> iterator = machines.iterator();

        while (iterator.hasNext())
        {
            TileModularMachine machine = iterator.next();

            if (machine.getWorld().getTileEntity(machine.getPos()) != machine)
            {
                iterator.remove();
                break;
            }

            if (machine.getDescriptor() == null)
                continue;

            ISteamHandler steam = machine.getModule(SteamModule.class).getInternalSteamHandler();
            Random rand = machine.getWorld().rand;

            BlockPos origin = machine.getPos();

            if (machine.getDescriptor().has(MultiblockComponent.class))
            {
                MultiblockComponent multiblock = machine.getDescriptor().get(MultiblockComponent.class);

                origin = machine.getPos().add(-rand.nextInt(multiblock.getWidth()) + multiblock.getOffsetX(),
                        rand.nextInt(multiblock.getHeight()) - multiblock.getOffsetY(),
                        -rand.nextInt(multiblock.getLength()) + multiblock.getOffsetZ());
            }
            BlockPos target = origin.add(rand.nextInt(4) - 2, rand.nextInt(4) - 2, rand.nextInt(4) - 2);

            float pressureDiff = steam.getPressure() /
                    steam.getSafePressure() - 1;
            int smallJetWeight = (int) Math.ceil(pressureDiff / 0.02f);

            if (rand.nextInt(200) < smallJetWeight)
            {
                new SteamEffectPacket(machine.getWorld(), origin, target, true)
                        .sendToAllAround(machine.getWorld(), machine.getPos(), 24);
                steam.drainSteam(20, true);
            }

            if (steam.getPressure() >
                    steam.getSafePressure() + (steam.getMaxPressure() - steam.getSafePressure()) * .25f)
            {
                int bigJetWeight = (int) Math.ceil(pressureDiff / 0.04f);
                target = origin.add(rand.nextInt(4) - 2, rand.nextInt(4) - 2, rand.nextInt(4) - 2);

                if (rand.nextInt(200) < bigJetWeight)
                {
                    new SteamEffectPacket(machine.getWorld(), origin, target, false)
                            .sendToAllAround(machine.getWorld(), machine.getPos(), 24);
                    steam.drainSteam(60, true);

                    machine.getWorld().getEntitiesWithinAABB(EntityLivingBase.class,
                            new AxisAlignedBB(origin, target)).forEach(entity ->
                    {
                        entity.attackEntityFrom(DamageSource.IN_FIRE, 4);
                        entity.setFire(3);
                    });
                }
                if (rand.nextInt(300) < bigJetWeight)
                {
                    for (EnumFacing facing : EnumFacing.VALUES)
                    {
                        BlockPos adjacent = origin.offset(facing);

                        if (!machine.getWorld().isAirBlock(adjacent) ||
                                !machine.getWorld().isSideSolid(adjacent.down(), EnumFacing.DOWN))
                            continue;
                        machine.getWorld().setBlockState(adjacent, Blocks.FIRE.getDefaultState(), 11);
                        break;
                    }
                }
            }

            if (steam.getPressure() >= steam.getMaxPressure())
                machine.getWorld().createExplosion(null, machine.getPos().getX(), machine.getPos().getY(),
                        machine.getPos().getZ(), steam.getMaxPressure() * 2, true);
        }
    }

    public void addMachine(TileModularMachine machine)
    {
        this.machines.add(machine);
    }

    public void removeMachine(TileModularMachine machine)
    {
        this.machines.remove(machine);
    }

    public boolean hasMachine(TileModularMachine machine)
    {
        return this.machines.contains(machine);
    }
}
