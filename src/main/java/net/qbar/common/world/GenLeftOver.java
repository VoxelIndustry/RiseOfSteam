package net.qbar.common.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class GenLeftOver
{
    private ChunkPos pos;

    private HashMap<BlockPos, IBlockState> blocks;

    public GenLeftOver(ChunkPos pos)
    {
        this.pos = pos;

        this.blocks = new HashMap<>();
    }

    public GenLeftOver(NBTTagCompound tag)
    {
        this(new ChunkPos(tag.getInteger("chunkX"), tag.getInteger("chunkZ")));

        for (int i = 0; i < tag.getInteger("blocks"); i++)
        {
            this.blocks.put(BlockPos.fromLong(tag.getLong("posBlock" + i)),
                    Block.REGISTRY.getObject(new ResourceLocation(tag.getString("block" + i)))
                            .getStateFromMeta(tag.getInteger("metaBlock" + i)));
        }
    }

    public ChunkPos getPos()
    {
        return pos;
    }

    public HashMap<BlockPos, IBlockState> getBlocks()
    {
        return blocks;
    }

    public void generate(World w)
    {
        for (Map.Entry<BlockPos, IBlockState> block : this.blocks.entrySet())
            w.setBlockState(block.getKey(), block.getValue(), 2);
    }

    public NBTTagCompound toNBT()
    {
        NBTTagCompound tag = new NBTTagCompound();

        tag.setInteger("posX", pos.x);
        tag.setInteger("posZ", pos.z);

        int i = 0;
        for (Map.Entry<BlockPos, IBlockState> block : this.blocks.entrySet())
        {
            tag.setLong("posBlock" + i, block.getKey().toLong());
            tag.setInteger("metaBlock" + i, block.getValue().getBlock().getMetaFromState(block.getValue()));
            tag.setString("block" + i, Block.REGISTRY.getNameForObject(block.getValue().getBlock()).toString());
            i++;
        }
        tag.setInteger("blocks", i);
        return tag;
    }
}
