package net.ros.common.tile.machine;

import com.google.common.collect.LinkedListMultimap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ros.common.ROSConstants;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.event.TickHandler;
import net.ros.common.grid.WorkshopMachine;
import net.ros.common.grid.node.ITileWorkshop;
import net.ros.common.gui.MachineGui;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.machine.module.impl.IOModule;
import net.ros.common.multiblock.blueprint.Blueprint;
import net.ros.common.network.action.ActionSender;
import net.ros.common.network.action.ClientActionBuilder;
import net.ros.common.network.action.IActionReceiver;
import net.ros.common.tile.ITileInfoList;

@Getter
public class TileBlueprintPrinter extends TileTickingModularMachine
        implements IContainerProvider, IActionReceiver, ITileWorkshop
{
    private final LinkedListMultimap<BlockPos, ITileWorkshop> connectionsMap = LinkedListMultimap.create();
    @Setter
    private       int                                         grid;

    public TileBlueprintPrinter()
    {
        super(Machines.BLUEPRINT_PRINTER);

        this.grid = -1;
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 1));
        this.getModule(InventoryModule.class).getInventory("basic")
                .addSlotFilter(0, stack -> stack.getItem() == Items.PAPER);
        this.addModule(new IOModule(this));
    }

    @Override
    public void addInfo(ITileInfoList list)
    {
        list.addText("Grid: " + this.getGrid());

        if (this.getGrid() != -1 && this.getGridObject() != null)
            list.addText("Contains: " + this.getGridObject().getCables().size());
        else
            list.addText("Errored grid!");
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("blueprintprinter", player).player(player)
                .inventory(31, 140).hotbar(31, 198).addInventory()
                .tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .filterSlot(0, 31, 118, stack -> stack.getItem() == Items.PAPER)
                .addInventory().create();
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.BLUEPRINTPRINTER.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if ("PRINT".equals(actionID))
        {
            InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");

            if (inventory.getStackInSlot(0).getCount() > 0 &&
                    Machines.contains(Blueprint.class, payload.getString("blueprint")))
            {
                ItemStack blueprint = new ItemStack(ROSItems.BLUEPRINT);
                NBTTagCompound tag = new NBTTagCompound();
                blueprint.setTagCompound(tag);

                tag.setString("blueprint", payload.getString("blueprint"));

                if (sender.getPlayer().inventory.getItemStack().isEmpty())
                {
                    sender.getPlayer().inventory.setItemStack(blueprint);
                    sender.answer().withBoolean("blueprint", true).send();
                }
                else
                    sender.getPlayer().addItemStackToInventory(blueprint);

                inventory.extractItem(0, 1, false);
            }
        }
        else if ("MACHINES_LOAD".equals(actionID) && this.hasGrid())
        {
            ClientActionBuilder builder = sender.answer();

            this.getGridObject().getMachines().forEach((machine, node) ->
                    builder.withLong(machine.name(), node.getBlockPos().toLong()));

            builder.send();
        }
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
        return WorkshopMachine.PRINTER;
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
            this.askServerSync();
            this.updateState();
        }
    }
}