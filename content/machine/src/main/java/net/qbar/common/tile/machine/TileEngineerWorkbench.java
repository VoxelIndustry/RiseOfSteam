package net.qbar.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.ITileCable;
import net.qbar.common.grid.ITileWorkshop;
import net.qbar.common.grid.WorkshopGrid;
import net.qbar.common.grid.WorkshopMachine;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.tile.ILoadable;
import net.qbar.common.tile.QBarTileBase;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;

@Getter
public class TileEngineerWorkbench extends QBarTileBase implements IContainerProvider, ITileMultiblockCore,
        ILoadable, ITileWorkshop
{
    private final EnumMap<EnumFacing, ITileCable<WorkshopGrid>> connectionsMap;
    @Setter
    private       int                                           grid;

    public TileEngineerWorkbench()
    {
        this.connectionsMap = new EnumMap<>(EnumFacing.class);
        this.grid = -1;
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Grid: " + this.getGrid());

        if (this.getGrid() != -1 && this.getGridObject() != null)
        {
            lines.add("Contains: " + this.getGridObject().getCables().size());
        }
        else
            lines.add("Errored grid!");
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        return null;
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(pos, false);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.ENGINEERWORKBENCH.getUniqueID(), this.world, this.pos
                        .getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }

    @Override
    public BlockPos getBlockPos()
    {
        return this.getCorePos();
    }

    @Override
    public World getBlockWorld()
    {
        return this.world;
    }

    @Override
    public WorkshopMachine getType()
    {
        return WorkshopMachine.WORKBENCH;
    }

    @Override
    public void onChunkUnload()
    {
        this.disconnectItself();
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        if (this.isServer() && this.getGrid() == -1)
            TickHandler.loadables.add(this);
        if (this.isClient())
        {
            this.forceSync();
            this.updateState();
        }
    }
}
