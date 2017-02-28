package net.qbar.common.card;

import net.minecraft.nbt.NBTTagCompound;

public class PunchedCardDataManager
{
    private static volatile PunchedCardDataManager instance;

    public static PunchedCardDataManager getInstance()
    {
        if (PunchedCardDataManager.instance == null)
        {
            synchronized (PunchedCardDataManager.class)
            {
                if (PunchedCardDataManager.instance == null)
                    PunchedCardDataManager.instance = new PunchedCardDataManager();
            }
        }
        return PunchedCardDataManager.instance;
    }

    private PunchedCardDataManager()
    {
    }

    public IPunchedCard readFromNBT(final NBTTagCompound tag)
    {
        final IPunchedCard data = ECardType.values()[tag.getInteger("cardTypeID")].data;
        return data.readFromNBT(tag);
    }

    public void writeToNBT(final NBTTagCompound tag, final IPunchedCard data)
    {
        tag.setInteger("cardTypeID", data.getID());
        data.writeToNBT(tag);
    }

    public static enum ECardType
    {
        CRAFT(0, new CraftCard(0)), FILTER(1, new FilterCard(1));

        private int          ID;
        private IPunchedCard data;

        private ECardType(final int id, final IPunchedCard data)
        {
            this.ID = id;
            this.data = data;
        }

        public int getID()
        {
            return this.ID;
        }

        public IPunchedCard getData()
        {
            return this.data;
        }
    }
}
