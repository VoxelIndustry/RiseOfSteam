package net.qbar.common.tile.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.qbar.common.QBarConstants;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.ContainerBuilder;
import net.qbar.common.grid.IBelt;
import net.qbar.common.gui.MachineGui;
import net.qbar.common.init.QBarItems;
import net.qbar.common.machine.QBarMachines;
import net.qbar.common.multiblock.BlockMultiblockBase;
import net.qbar.common.multiblock.MultiblockComponent;
import net.qbar.common.multiblock.MultiblockSide;
import net.qbar.common.recipe.QBarRecipe;
import net.qbar.common.recipe.QBarRecipeHandler;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.tile.TileCraftingMachineBase;

public class TileSortingMachine extends TileCraftingMachineBase
{
    public TileSortingMachine()
    {
        super(QBarMachines.SORTING_MACHINE);
    }

    @Override
    public boolean acceptRecipe(QBarRecipe recipe)
    {
        return recipe.getRecipeOutputs(ItemStack.class).size() <= 4;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("sortingmachine", player).player(player.inventory).inventory(8, 84).hotbar(8, 142)
                .addInventory().tile(this)
                .recipeSlot(0, QBarRecipeHandler.SORTING_MACHINE_UID, 0, 47, 36,
                        slot -> this.isBufferEmpty() && this.isOutputEmpty())
                .outputSlot(1, 107, 26).outputSlot(2, 125, 26).outputSlot(3, 107, 44).outputSlot(4, 125, 44)
                .displaySlot(5, -1000, 0).syncFloatValue(this::getCurrentProgress, this::setCurrentProgress)
                .syncFloatValue(this::getMaxProgress, this::setMaxProgress)
                .syncIntegerValue(this.getSteamTank()::getSteam, this.getSteamTank()::setSteam).addInventory().create();
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        MultiblockSide side = QBarMachines.SORTING_MACHINE.get(MultiblockComponent.class)
                .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side.getFacing() == EnumFacing.NORTH
                && side.getPos().getX() == 0 && side.getPos().getY() == 1 && side.getPos().getZ() == 0)
        {
            return true;
        }
        else if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && side.getFacing() == EnumFacing.WEST
                && side.getPos().getX() == 0 && side.getPos().getY() == 0 && side.getPos().getZ() == 1)
        {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        MultiblockSide side = QBarMachines.SORTING_MACHINE.get(MultiblockComponent.class)
                .worldSideToMultiblockSide(new MultiblockSide(from, facing), this.getFacing());

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side.getFacing() == EnumFacing.NORTH
                && side.getPos().getX() == 0 && side.getPos().getY() == 1 && side.getPos().getZ() == 0)
        {
            return (T) this.getInventoryWrapper(facing);
        }
        else if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && side.getFacing() == EnumFacing.WEST
                && side.getPos().getX() == 0 && side.getPos().getY() == 0 && side.getPos().getZ() == 1)
        {
            return (T) this.getSteamTank();
        }
        return null;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    public EnumFacing getFacing()
    {
        return this.world.getBlockState(this.pos).getValue(BlockMultiblockBase.FACING);
    }

    @Override
    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
            final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == QBarItems.WRENCH)
            return false;

        player.openGui(QBarConstants.MODINSTANCE, MachineGui.SORTINGMACHINE.getUniqueID(), this.world, this.pos.getX(), this.pos.getY(),
                this.pos.getZ());
        return true;
    }
}
