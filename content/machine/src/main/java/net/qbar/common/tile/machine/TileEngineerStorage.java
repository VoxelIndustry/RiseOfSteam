package net.qbar.common.tile.machine;

import com.google.common.collect.LinkedListMultimap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.ITileWorkshop;
import net.qbar.common.grid.WorkshopMachine;
import net.qbar.common.network.action.ActionSender;
import net.qbar.common.network.action.ClientActionBuilder;
import net.qbar.common.network.action.IActionReceiver;
import net.qbar.common.tile.TileInventoryBase;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.IntStream;

public class TileEngineerStorage extends TileInventoryBase implements IContainerProvider, ISidedInventory,
        ITileWorkshop, IActionReceiver
{
    private final EnumMap<EnumFacing, SidedInvWrapper> inventoryWrapperCache;
    @Getter
    private final LinkedListMultimap<BlockPos, ITileWorkshop> connectionsMap = LinkedListMultimap.create();
    @Getter
    @Setter
    private int grid;

    public TileEngineerStorage()
    {
        super("engineerstorage", 32);

        this.inventoryWrapperCache = new EnumMap<>(EnumFacing.class);
        this.grid = -1;
    }

    private void reloadWorkbench()
    {
        if (this.isClient())
            return;
        if (!this.hasGrid() || !this.getGridObject().getMachines().containsKey(WorkshopMachine.WORKBENCH))
            return;

        this.getGridObject().getMachines().get(WorkshopMachine.WORKBENCH).refreshWorkbenchCrafts();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        this.reloadWorkbench();
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
    public int[] getSlotsForFace(EnumFacing side)
    {
        return IntStream.range(0, 32).toArray();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return index >= 0 && index <= 31;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index >= 0 && index <= 31;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("engineerstorage", player)
                .player(player.inventory).inventory(19, 84).hotbar(19, 142)
                .addInventory().tile(this)
                .slotLine(0, 27, 8, 8, EnumFacing.Axis.X)
                .slotLine(8, 27, 26, 8, EnumFacing.Axis.X)
                .slotLine(16, 27, 44, 8, EnumFacing.Axis.X)
                .slotLine(24, 27, 62, 8, EnumFacing.Axis.X)
                .addInventory().create();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) this.getInventoryWrapper(facing);
        return super.getCapability(capability, facing);
    }

    protected SidedInvWrapper getInventoryWrapper(EnumFacing side)
    {
        if (!this.inventoryWrapperCache.containsKey(side))
            this.inventoryWrapperCache.put(side, new SidedInvWrapper(this, side));
        return this.inventoryWrapperCache.get(side);
    }

    @Override
    public BlockPos getBlockPos()
    {
        return this.getPos();
    }

    @Override
    public World getBlockWorld()
    {
        return this.world;
    }

    @Override
    public WorkshopMachine getType()
    {
        return WorkshopMachine.STORAGE;
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

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if ("MACHINES_LOAD".equals(actionID) && this.hasGrid())
        {
            ClientActionBuilder builder = sender.answer();

            this.getGridObject().getMachines().forEach((machine, node) ->
                    builder.withLong(machine.name(), node.getBlockPos().toLong()));

            builder.send();
        }
    }
}
