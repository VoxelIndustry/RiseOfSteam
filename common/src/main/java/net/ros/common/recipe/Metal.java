package net.ros.common.recipe;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Metal
{
    private String  name;
    private boolean isAlloy;
    private float   meltingPoint;
}
