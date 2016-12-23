package net.qbar.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.qbar.common.init.QBarItems;
import net.qbar.common.tile.TileKeypunch;

public class BlockKeypunch extends BlockMachineBase
{
    public BlockKeypunch()
    {
        super("keypunch", Material.IRON);
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileKeypunch();
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
	{
		super.onBlockClicked(worldIn, pos, playerIn);
		TileKeypunch t = (TileKeypunch) worldIn.getTileEntity(pos);

		ItemStack it = playerIn.getHeldItem(playerIn.getActiveHand() == null ? EnumHand.MAIN_HAND : playerIn.getActiveHand());

		if (it != ItemStack.EMPTY)
			t.setInventorySlotContents(0, it);
		else
		{
			t.setInventorySlotContents(0, );
			playerIn.inventory.addItemStackToInventory(new ItemStack(QBarItems.itemPunchedCard));
		}
	}
}
