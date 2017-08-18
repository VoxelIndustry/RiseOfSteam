package net.qbar.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.QBar;
import net.qbar.common.block.BlockVeinOre;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.gui.EGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.item.ItemDrillCoreSample;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.ore.QBarMineral;
import net.qbar.common.ore.QBarOres;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.tile.TileInventoryBase;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TileTinyMiningDrill extends TileInventoryBase implements ITickable, ITileMultiblockCore, IContainerProvider
{
    @Getter
    @Setter
    private float                   progress;

    private BlockPos                lastPos;
    private Map<QBarMineral, Float> results;

    public TileTinyMiningDrill()
    {
        super("tinyminingdrill", 2);

        this.results = new HashMap<>();
        this.lastPos = this.getPos();
    }

    @Override
    public void update()
    {
        if (this.isClient())
        {
            if (this.getProgress() < 1)
                this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST,
                        this.getPos().getX() + 0.5 + (this.world.rand.nextFloat() / 4.0F) - 0.125F, this.getPos().getY(),
                        this.getPos().getZ() + 0.5 + (this.world.rand.nextFloat() / 4.0F) - 0.125F,
                        (this.world.rand.nextFloat() / 4.0F) - 0.125F, 0.25D,
                        (this.world.rand.nextFloat() / 4.0F) - 0.125F,
                        Block.getStateId(this.world.getBlockState(pos.down())));
            return;
        }

        if (this.getProgress() < 1 && this.getSteam() >= 20)
        {
            BlockPos toCheck = this.lastPos;

            for (int i = 0; i < 25; i++)
            {
                if (lastPos.equals(BlockPos.ORIGIN))
                    toCheck = new BlockPos(this.getPos().getX() - 7, 0, this.getPos().getZ() - 7);
                else
                {
                    if (toCheck.getY() == this.getPos().getY() - 1)
                    {
                        if (toCheck.getX() == this.getPos().getX() + 7)
                        {
                            if (toCheck.getZ() == this.getPos().getZ() + 7)
                            {
                                this.progress = 1;
                                this.setInventorySlotContents(0, ItemDrillCoreSample.getSample(this.getPos(), results));
                            }
                            toCheck = new BlockPos(this.getPos().getX() - 7, 0, toCheck.getZ() + 1);
                        }
                        else
                            toCheck = new BlockPos(toCheck.getX() + 1, 0, toCheck.getZ());
                    }
                    else
                        toCheck = toCheck.up();
                }
                this.progress = (((toCheck.getZ() - this.getPos().getZ() + 7) * 15 * (this.getPos().getY() - 1))
                        + ((toCheck.getX() - this.getPos().getX() + 7) * (this.getPos().getY() - 1)) + toCheck.getY())
                        / (float) (15 * 15 * (this.getPos().getY() - 1));

                IBlockState state = this.world.getBlockState(toCheck);

                if (state.getBlock() instanceof BlockVeinOre)
                {
                    for (Map.Entry<QBarMineral, Float> mineral : QBarOres.getOreFromState(state)
                            .orElse(QBarOres.CASSITERITE).getMinerals().entrySet())
                    {
                        this.results.putIfAbsent(mineral.getKey(), 0F);
                        this.results.put(mineral.getKey(),
                                mineral.getValue() * (state.getValue(BlockVeinOre.RICHNESS).ordinal() + 1)
                                        + this.results.get(mineral.getKey()));
                    }
                }
                lastPos = toCheck;

                if (this.progress == 1)
                    this.setInventorySlotContents(0, ItemDrillCoreSample.getSample(this.getPos(), results));
            }
            // TODO: Change to real value when the portable storage is implemented
            this.drainSteam(0, true);

            this.sync();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("progress", this.progress);
        tag.setLong("lastPos", this.lastPos.toLong());

        int i = 0;
        for (Map.Entry<QBarMineral, Float> ore : this.results.entrySet())
        {
            tag.setString("oreType" + i, ore.getKey().getName());
            tag.setFloat("oreCount" + i, ore.getValue());
            i++;
        }
        tag.setInteger("ores", i);

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        this.progress = tag.getFloat("progress");
        this.lastPos = BlockPos.fromLong(tag.getLong("lastPos"));

        for (int i = 0; i < tag.getInteger("ores"); i++)
            this.results.put(QBarOres.getMineralFromName(tag.getString("oreType" + i)).orElse(null),
                    tag.getFloat("oreCount" + i));

        super.readFromNBT(tag);
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.forceSync();
    }

    public int getSteam()
    {
        if (this.getStackInSlot(1).hasCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH))
            return this.getStackInSlot(1)
                    .getCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH).getSteam();
        // TODO: Change to real value when the portable storage is implemented
        return 1000;
    }

    public int drainSteam(int quantity, boolean doDrain)
    {
        if (this.getStackInSlot(1).hasCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH))
            return this.getStackInSlot(1)
                    .getCapability(CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY, EnumFacing.NORTH)
                    .drainSteam(quantity, doDrain);
        return 0;
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.getPos(), false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return super.getCapability(capability, facing);
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("tinyminingdrill", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this).outputSlot(0, 80, 12).slot(1, 80, 58)
                .syncFloatValue(this::getProgress, this::setProgress).addInventory().create();
    }

    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBar.instance, EGui.TINYMININGDRILL.ordinal(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }
}
