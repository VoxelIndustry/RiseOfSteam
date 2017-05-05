package net.qbar.common.ore;

public class OreStack
{
    private QBarOre ore;
    private float   quantity;

    public OreStack(QBarOre ore, float quantity)
    {
        this.ore = ore;
        this.quantity = quantity;
    }

    public OreStack(QBarOre ore)
    {
        this(ore, 1);
    }

    public QBarOre getOre()
    {
        return ore;
    }

    public float getQuantity()
    {
        return quantity;
    }

    public void grow(float quantity)
    {
        this.quantity += quantity;
    }

    public void shrink(float quantity)
    {
        this.quantity -= quantity;
    }

    public void setQuantity(float quantity)
    {
        this.quantity = quantity;
    }

    @Override
    public String toString()
    {
        return "OreStack{" + "ore=" + ore + ", quantity=" + quantity + '}';
    }
}
