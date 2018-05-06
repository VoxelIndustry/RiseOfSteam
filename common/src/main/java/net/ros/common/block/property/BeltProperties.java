package net.ros.common.block.property;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;

public class BeltProperties
{
    public static final PropertyEnum<BeltDirection> FACING   = PropertyEnum.create("facing", BeltDirection.class);
    public static final PropertyEnum<BeltSlope>     SLOP     = PropertyEnum.create("slope", BeltSlope.class);
    public static final PropertyBool                ANIMATED = PropertyBool.create("animated");
}
