package net.ros.common.card;

import net.minecraft.nbt.NBTTagCompound;

public class CardDataStorage
{
    private static volatile CardDataStorage instance;

    public static CardDataStorage instance()
    {
        if (CardDataStorage.instance == null)
        {
            synchronized (CardDataStorage.class)
            {
                if (CardDataStorage.instance == null)
                    CardDataStorage.instance = new CardDataStorage();
            }
        }
        return CardDataStorage.instance;
    }

    private CardDataStorage()
    {
    }

    public ECardType getType(NBTTagCompound tag)
    {
        return ECardType.values()[tag.getInteger("cardTypeID")];
    }

    public <T extends IPunchedCard> T read(NBTTagCompound tag, Class<T> cardClass)
    {
        return cardClass.cast(this.read(tag));
    }

    public IPunchedCard read(final NBTTagCompound tag)
    {
        final IPunchedCard data = ECardType.values()[tag.getInteger("cardTypeID")].data;
        return data.readFromNBT(tag);
    }

    public void write(final NBTTagCompound tag, final IPunchedCard data)
    {
        tag.setInteger("cardTypeID", data.getID());
        data.writeToNBT(tag);
    }

    public enum ECardType
    {
        CRAFT(0, new CraftCard(0)), FILTER(1, new FilterCard(1));

        private int          ID;
        private IPunchedCard data;

        ECardType(final int id, final IPunchedCard data)
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
