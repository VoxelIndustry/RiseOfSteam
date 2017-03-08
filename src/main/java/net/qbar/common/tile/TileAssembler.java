package net.qbar.common.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.qbar.common.card.CraftCard;
import net.qbar.common.card.IPunchedCard;
import net.qbar.common.card.PunchedCardDataManager;
import net.qbar.common.card.PunchedCardDataManager.ECardType;
import net.qbar.common.container.BuiltContainer;
import net.qbar.common.container.IContainerProvider;
import net.qbar.common.multiblock.ITileMultiblockCore;
import net.qbar.common.steam.CapabilitySteamHandler;
import net.qbar.common.steam.SteamTank;
import net.qbar.common.steam.SteamUtil;
import net.qbar.common.util.ItemUtils;

public class TileAssembler extends TileInventoryBase
        implements ITileInfoProvider, IContainerProvider, ITickable, ITileMultiblockCore
{
    private final SteamTank steamTank;

    private CraftCard       craft;
    private ItemStack       cached = ItemStack.EMPTY;

    public TileAssembler()
    {
        super("InventoryAssembler", 11);

        this.steamTank = new SteamTank(0, 4000, SteamUtil.AMBIANT_PRESSURE * 2);
    }

    @Override
    public void update()
    {
        if (!ItemUtils.deepEquals(this.cached, this.getStackInSlot(0)))
        {
            this.craft = null;
            this.cached = this.getStackInSlot(0).copy();
            if (this.cached.hasTagCompound())
            {
                final IPunchedCard card = PunchedCardDataManager.getInstance()
                        .readFromNBT(this.getStackInSlot(0).getTagCompound());
                if (card.getID() == ECardType.CRAFT.getID())
                    this.craft = (CraftCard) card;
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        final NBTTagCompound subTag = new NBTTagCompound();
        this.steamTank.writeToNBT(subTag);
        tag.setTag("steamTank", subTag);

        return tag;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        if (tag.hasKey("steamTank"))
            this.steamTank.readFromNBT(tag.getCompoundTag("steamTank"));
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY && facing == EnumFacing.UP)
            return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing)
    {
        if (capability == CapabilitySteamHandler.STEAM_HANDLER_CAPABILITY)
            return (T) this.steamTank;
        return super.getCapability(capability, facing);
    }

    @Override
    public void addInfo(final List<String> lines)
    {
        lines.add("Steam " + this.steamTank.getSteam() + " / " + this.steamTank.getCapacity());
        lines.add("Pressure " + SteamUtil.pressureFormat.format(this.steamTank.getPressure()) + " / "
                + SteamUtil.pressureFormat.format(this.steamTank.getMaxPressure()));
    }

    @Override
    public BuiltContainer createContainer(final EntityPlayer player)
    {
        return null;
    }

    @Override
    public void breakCore()
    {
        this.world.destroyBlock(this.getPos(), true);
    }

    @Override
    public BlockPos getCorePos()
    {
        return this.getPos();
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final BlockPos from, final EnumFacing facing)
    {
        return false;
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final BlockPos from, final EnumFacing facing)
    {
        return null;
    }
}
