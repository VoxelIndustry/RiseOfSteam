package net.qbar.common.ore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class OreStack
{
    @Getter
    private QBarMineral mineral;
    @Getter
    @Setter
    private float       quantity;

    public OreStack(QBarMineral mineral)
    {
        this(mineral, 1);
    }

    public void grow(float quantity)
    {
        this.quantity += quantity;
    }

    public void shrink(float quantity)
    {
        this.quantity -= quantity;
    }
}
