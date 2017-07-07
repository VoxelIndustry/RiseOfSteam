package net.qbar.common.container.sync;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SyncableProperty<T extends Object>
{
    @Getter
    private final Supplier<T> supplier;
    @Getter
    private final Consumer<T> consumer;
    @Getter
    @Setter
    protected     T           stored;

    public SyncableProperty(final Supplier<T> supplier, final Consumer<T> consumer)
    {
        this.supplier = supplier;
        this.consumer = consumer;
        this.stored = null;
    }

    public boolean areEquals(final T other)
    {
        return this.stored.equals(other);
    }

    public boolean needRefresh()
    {
        final T supplied = this.supplier.get();

        return this.stored == null && supplied != null || this.stored != null && supplied == null ||
                this.stored != null && !this.areEquals(supplied);
    }

    public void updateInternal()
    {
        this.stored = this.copy(this.supplier.get());
    }

    public void update()
    {
        this.consumer.accept(this.stored);
    }

    public abstract NBTTagCompound toNBT(final NBTTagCompound tag);

    public abstract void fromNBT(NBTTagCompound tag);

    public T copy(T original)
    {
        return original;
    }
}
