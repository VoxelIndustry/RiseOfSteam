package net.qbar.common.multiblock;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class MultiblockSide
{
    private final BlockPos   pos;
    private final EnumFacing facing;
}
