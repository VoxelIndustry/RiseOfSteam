package net.ros.common.tile.machine;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.ros.common.ROSConstants;
import net.ros.common.block.BlockVeinOre;
import net.ros.common.init.ROSItems;
import net.ros.common.inventory.InventoryHandler;
import net.ros.common.machine.Machines;
import net.ros.common.machine.module.InventoryModule;
import net.ros.common.ore.Mineral;
import net.ros.common.ore.Ores;
import net.ros.common.steam.SteamCapabilities;
import net.ros.common.container.BuiltContainer;
import net.ros.common.container.ContainerBuilder;
import net.ros.common.container.IContainerProvider;
import net.ros.common.gui.MachineGui;
import net.ros.common.item.ItemDrillCoreSample;
import net.ros.common.network.action.ActionSender;
import net.ros.common.network.action.IActionReceiver;

import java.util.HashMap;
import java.util.Map;

public class TileTinyMiningDrill extends TileTickingModularMachine implements IContainerProvider, IActionReceiver
{
    private final int consumption = 20;

    @Getter
    @Setter
    private float progress;

    private BlockPos            lastPos;
    private Map<Mineral, Float> results;

    @Getter
    @Setter
    private boolean doStart = false;

    public TileTinyMiningDrill()
    {
        super(Machines.TINY_MINING_DRILL);

        this.results = new HashMap<>();
        this.lastPos = this.getPos();
    }

    @Override
    protected void reloadModules()
    {
        super.reloadModules();

        this.addModule(new InventoryModule(this, 2));
    }

    @Override
    public void update()
    {
        if (this.isClient())
        {
            if (this.getProgress() < 1 && this.getSteam() >= this.consumption && this.doStart)
                this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST,
                        this.getPos().getX() + 0.5 + (this.world.rand.nextFloat() / 4.0F) - 0.125F,
                        this.getPos().getY(),
                        this.getPos().getZ() + 0.5 + (this.world.rand.nextFloat() / 4.0F) - 0.125F,
                        (this.world.rand.nextFloat() / 4.0F) - 0.125F, 0.25D,
                        (this.world.rand.nextFloat() / 4.0F) - 0.125F,
                        Block.getStateId(this.world.getBlockState(pos.down())));
            return;
        }

        if (this.doStart && this.getProgress() < 1 && this.getSteam() >= this.consumption)
        {
            InventoryHandler inventory = this.getModule(InventoryModule.class).getInventory("basic");
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
                                inventory.setStackInSlot(0, ItemDrillCoreSample.getSample(this.getPos(), results));
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
                    for (Map.Entry<Mineral, Float> mineral : Ores.getOreFromState(state)
                            .orElse(Ores.CASSITERITE).getMinerals().entrySet())
                    {
                        this.results.putIfAbsent(mineral.getKey(), 0F);
                        this.results.put(mineral.getKey(),
                                mineral.getValue() * (state.getValue(BlockVeinOre.RICHNESS).ordinal() + 1)
                                        + this.results.get(mineral.getKey()));
                    }
                }
                lastPos = toCheck;

                if (this.progress == 1)
                {
                    inventory.setStackInSlot(0, ItemDrillCoreSample.getSample(this.getPos(), results));
                    this.progress = 0;
                    this.lastPos = BlockPos.ORIGIN;
                    this.results.clear();
                    this.doStart = false;
                }
            }
            this.drainSteam(this.consumption, true);

            this.sync();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("progress", this.progress);
        tag.setLong("lastPos", this.lastPos.toLong());

        int i = 0;
        for (Map.Entry<Mineral, Float> ore : this.results.entrySet())
        {
            tag.setString("oreType" + i, ore.getKey().getName());
            tag.setFloat("oreCount" + i, ore.getValue());
            i++;
        }
        tag.setInteger("ores", i);

        tag.setBoolean("doStart", this.doStart);

        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        this.progress = tag.getFloat("progress");
        this.lastPos = BlockPos.fromLong(tag.getLong("lastPos"));

        for (int i = 0; i < tag.getInteger("ores"); i++)
            this.results.put(Ores.getMineralFromName(tag.getString("oreType" + i)).orElse(null),
                    tag.getFloat("oreCount" + i));

        this.doStart = tag.getBoolean("doStart");

        super.readFromNBT(tag);
    }

    @Override
    public void onLoad()
    {
        if (this.isClient())
            this.askServerSync();
    }

    private int getSteam()
    {
        ItemStack steam = this.getModule(InventoryModule.class).getInventory("basic").getStackInSlot(1);

        if (steam.hasCapability(SteamCapabilities.ITEM_STEAM_HANDLER, EnumFacing.NORTH))
            return steam.getCapability(SteamCapabilities.ITEM_STEAM_HANDLER, EnumFacing.NORTH).getSteam();
        return 0;
    }

    private int drainSteam(int quantity, boolean doDrain)
    {
        ItemStack steam = this.getModule(InventoryModule.class).getInventory("basic").getStackInSlot(1);

        if (steam.hasCapability(SteamCapabilities.ITEM_STEAM_HANDLER, EnumFacing.NORTH))
            return steam.getCapability(SteamCapabilities.ITEM_STEAM_HANDLER, EnumFacing.NORTH)
                    .drainSteam(quantity, doDrain);
        return 0;
    }

    @Override
    public BuiltContainer createContainer(EntityPlayer player)
    {
        return new ContainerBuilder("tinyminingdrill", player)
                .player(player).inventory(8, 84).hotbar(8, 142).addInventory()
                .tile(this.getModule(InventoryModule.class).getInventory("basic"))
                .outputSlot(0, 134, 35).steamSlot(1, 80, 58)
                .syncFloatValue(this::getProgress, this::setProgress)
                .addInventory().create();
    }

    public boolean onRightClick(final EntityPlayer player, final EnumFacing side, final float hitX, final float hitY,
                                final float hitZ, BlockPos from)
    {
        if (player.isSneaking())
            return false;
        if (player.getHeldItemMainhand().getItem() == ROSItems.WRENCH)
            return false;

        player.openGui(ROSConstants.MODINSTANCE, MachineGui.TINYMININGDRILL.getUniqueID(), this.world,
                this.pos.getX(), this.pos.getY(), this.pos.getZ());
        return true;
    }

    @Override
    public void handle(ActionSender sender, String actionID, NBTTagCompound payload)
    {
        if ("START".equals(actionID))
        {
            if (this.getProgress() == 0)
                this.doStart = true;
        }
    }
}
