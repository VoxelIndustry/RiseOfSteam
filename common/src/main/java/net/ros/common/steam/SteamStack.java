package net.ros.common.steam;

import lombok.*;
import net.minecraft.nbt.NBTTagCompound;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SteamStack
{
    private int amount;

    public void writeToNBT(final NBTTagCompound nbt)
    {
        nbt.setInteger("amount", this.getAmount());
    }

    public static SteamStack readFromNBT(final NBTTagCompound nbt)
    {
        SteamStack stack = null;
        if (nbt.hasKey("amount"))
            stack = new SteamStack(nbt.getInteger("amount"));
        return stack;
    }

    public SteamStack copy()
    {
        return new SteamStack(this.amount);
    }
}
