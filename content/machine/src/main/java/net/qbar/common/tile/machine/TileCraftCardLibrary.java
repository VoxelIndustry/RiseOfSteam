package net.qbar.common.tile.machine;

import com.google.common.collect.LinkedListMultimap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.card.CardDataStorage;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.event.TickHandler;
import net.qbar.common.grid.ITileWorkshop;
import net.qbar.common.grid.WorkshopMachine;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.network.action.ActionSender;
import net.qbar.common.network.action.ClientActionBuilder;
import net.qbar.common.network.action.IActionReceiver;
import net.qbar.common.tile.TileMultiblockInventoryBase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Getter
public class TileCraftCardLibrary extends TileMultiblockInventoryBase implements ITileWorkshop, IActionReceiver
{
    private final LinkedListMultimap<BlockPos, ITileWorkshop> connectionsMap = LinkedListMultimap.create();
    @Setter
    private int grid;

    public TileCraftCardLibrary()
    {
        super(QBarMachines.CRAFTCARD_LIBRARY, 72);
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
        return IntStream.range(0, 72).toArray();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index >= 0 && index <= 71;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index >= 0 && index <= 71;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        Predicate<ItemStack> cardFilter = stack -> stack.getItem() == QBarItems.PUNCHED_CARD &&
                stack.hasTagCompound() && CardDataStorage.instance().read(stack.getTagCompound())
                .getID() == CardDataStorage.ECardType.CRAFT.getID();

        return new ContainerBuilder("craftcardlibrary", player).player(player.inventory)
                .inventory(19, 123).hotbar(19, 181).addInventory()
                .tile(this)
                .filterSlotLine(0, 19, -25, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(9, 19, -7, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(18, 19, 11, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(27, 19, 29, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(36, 19, 47, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(45, 19, 65, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(54, 19, 83, 9, EnumFacing.Axis.X, cardFilter)
                .filterSlotLine(63, 19, 101, 9, EnumFacing.Axis.X, cardFilter)
                .addInventory().create();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return this.hasCapability(capability, BlockPos.ORIGIN, facing) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        T rtn = this.getCapability(capability, BlockPos.ORIGIN, facing);
        return rtn != null ? rtn : super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, BlockPos from, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) this.getInventoryWrapper(facing);
        return null;
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.CRAFTCARDLIBRARY.getUniqueID(), this.world, this.pos
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
        return WorkshopMachine.CARDLIBRARY;
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
