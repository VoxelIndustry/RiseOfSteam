package net.ros.common.tile.machine;

import com.google.common.collect.LinkedListMultimap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.ROSConstants;
import net.ros.common.card.CardDataStorage;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.event.TickHandler;
import net.ros.common.grid.WorkshopMachine;
import net.ros.common.grid.node.ITileWorkshop;
import net.ros.common.gui.MachineGui;
import net.ros.common.init.ROSItems;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.network.action.ActionSender;
import net.ros.common.network.action.ClientActionBuilder;
import net.ros.common.network.action.IActionReceiver;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Getter
public class TileCraftCardLibrary extends TileModularMachine implements IContainerProvider, ITileWorkshop,
        IActionReceiver
{
    private static Predicate<ItemStack> CARD_FILTER = stack -> stack.getItem() == ROSItems.PUNCHED_CARD &&
            stack.hasTagCompound() && CardDataStorage.instance().read(stack.getTagCompound())
            .getID() == CardDataStorage.ECardType.CRAFT.getID();

    private final LinkedListMultimap<BlockPos, ITileWorkshop> connectionsMap = LinkedListMultimap.create();
    @Setter
    private       int                                         grid;

    public TileCraftCardLibrary()
    {
        super(Machines.CRAFTCARD_LIBRARY);
        this.grid = -1;
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 72));
        this.getModule(InventoryModule.class).getInventory("basic")
                .addSlotFilters(CARD_FILTER, IntStream.rangeClosed(0, 71).toArray());

        this.addModule(new IOModule(this));
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
            lines.add("Contains: " + this.getGridObject().getCables().size());
        else
            lines.add("Errored grid!");
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("craftcardlibrary", player).player(player)
                .inventory(19, 123).hotbar(19, 181).addInventory()
                .tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .filterSlotLine(0, 19, -25, 9, EnumFacing.Axis.X, CARD_FILTER)
                .filterSlotLine(9, 19, -7, 9, EnumFacing.Axis.X, CARD_FILTER)
                .filterSlotLine(18, 19, 11, 9, EnumFacing.Axis.X, CARD_FILTER)
                .filterSlotLine(27, 19, 29, 9, EnumFacing.Axis.X, CARD_FILTER)
                .filterSlotLine(36, 19, 47, 9, EnumFacing.Axis.X, CARD_FILTER)
                .filterSlotLine(45, 19, 65, 9, EnumFacing.Axis.X, CARD_FILTER)
                .filterSlotLine(54, 19, 83, 9, EnumFacing.Axis.X, CARD_FILTER)
                .filterSlotLine(63, 19, 101, 9, EnumFacing.Axis.X, CARD_FILTER)
                .addInventory().create();
    }

    @Override
    public boolean onRightClick(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.CRAFTCARDLIBRARY.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
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
