package net.qbar.common.world;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenLeftOver
{
    @Getter
    private ChunkPos pos;

    @Getter
    private ConcurrentHashMap<BlockPos, IBlockState> blocks;

    public GenLeftOver(ChunkPos pos)
    {
        this.pos = pos;

        this.blocks = new ConcurrentHashMap<>();
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

    public int generate(World w, int maxBlocks)
    {
        int count = 0;
        Iterator<Map.Entry<BlockPos, IBlockState>> iterator = this.blocks.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry<BlockPos, IBlockState> block = iterator.next();

            IBlockState state = w.getBlockState(block.getKey());
            if (state.getBlock().isReplaceableOreGen(state, w, block.getKey(),
                    QBarOreGenerator.instance().STONE_PREDICATE))
            {
                w.setBlockState(block.getKey(), block.getValue(), 0);
                count++;
            }
            iterator.remove();
            if (count >= maxBlocks)
                break;
        }
        return count;
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
