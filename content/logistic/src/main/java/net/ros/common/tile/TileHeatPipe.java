package net.ros.common.tile;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.ros.common.grid.impl.HeatPipeGrid;
import net.ros.common.grid.node.IHeatPipe;
import net.ros.common.grid.node.PipeType;
import net.ros.common.heat.HeatCapabilities;
import net.ros.common.heat.HeatTank;
import net.ros.common.heat.IHeatHandler;
import net.voxelindustry.steamlayer.tile.ITileInfoList;

public class TileHeatPipe extends TilePipeBase<HeatPipeGrid, IHeatHandler> implements IHeatPipe
{
    @Getter
    private float    heatConductivity;
    @Getter
    private HeatTank heatTank;

    public TileHeatPipe(PipeType type)
    {
        super(type, PipeType.getTransferRate(type), HeatCapabilities.HEAT_HANDLER);

        this.heatConductivity = PipeType.getHeatConductivity(type);

        this.heatTank = new HeatTank(PipeType.getHeatLimit(type));
    }

    public TileHeatPipe()
    {
        this(null);
    }

    @Override
    public void load()
    {
        super.load();

        int minimumTemp = (int) (this.getWorld().getBiome(this.getPos()).getTemperature(this.getPos()) * 20);
        if (this.heatTank.getHeat() < minimumTemp)
            this.heatTank.setHeat(minimumTemp);
    }

    @Override
    public void addSpecificInfo(ITileInfoList list)
    {
        list.addText("Temperature: " + heatTank.getHeat() + "/" + heatTank.getCapacity() + " °C");
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        this.heatConductivity = tag.getFloat("heatConductivity");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setFloat("heatConductivity", this.heatConductivity);

        return super.writeToNBT(tag);
    }

    @Override
    public float getLoss()
    {
        return PipeType.getHeatLoss(this.getType());
    }

    @Override
    protected void scanHandler(BlockPos posNeighbor)
    {

    }

    @Override
    public HeatPipeGrid createGrid(int nextID)
    {
        return new HeatPipeGrid(nextID);
    }
}
