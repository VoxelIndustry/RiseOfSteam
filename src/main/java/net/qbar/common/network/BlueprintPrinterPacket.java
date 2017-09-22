package net.qbar.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.qbar.QBar;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.blueprint.Blueprint;
import net.qbar.common.tile.machine.TileBlueprintPrinter;

@ReceivedOn(Side.SERVER)
public class BlueprintPrinterPacket extends Message
{
    private int      dimensionID;
    private BlockPos pos;
    private String   blueprintName;

    public BlueprintPrinterPacket(NetworkContext ctx)
    {
        super(ctx);
    }

    public BlueprintPrinterPacket(TileBlueprintPrinter printer, String blueprintName)
    {
        this(QBar.network);
        this.pos = printer.getPos();
        this.dimensionID = printer.getWorld().provider.getDimension();
        this.blueprintName = blueprintName;
    }

    @Override
    protected void handle(EntityPlayer sender)
    {
        if (sender.getEntityWorld().provider.getDimension() == this.dimensionID
                && sender.getEntityWorld().isBlockLoaded(this.pos)
                && sender.getEntityWorld().getTileEntity(this.pos) != null
                && sender.getEntityWorld().getTileEntity(this.pos) instanceof TileBlueprintPrinter)
        {
            TileBlueprintPrinter printer = (TileBlueprintPrinter) sender.getEntityWorld().getTileEntity(this.pos);

            if (printer.getStackInSlot(0).getCount() > 0 && QBarMachines.contains(Blueprint.class, this.blueprintName))
            {
                ItemStack blueprint = new ItemStack(QBarItems.BLUEPRINT);
                NBTTagCompound tag = new NBTTagCompound();
                blueprint.setTagCompound(tag);

                tag.setString("blueprint", this.blueprintName);

                if (sender.inventory.getItemStack().isEmpty())
                    sender.inventory.setItemStack(blueprint);
                else
                    sender.addItemStackToInventory(blueprint);

                printer.decrStackSize(0, 1);
            }
        }
    }
}
