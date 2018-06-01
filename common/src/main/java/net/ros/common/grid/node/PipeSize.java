package net.ros.common.grid.node;

import lombok.Getter;

public enum PipeSize
{
    SMALL(6 / 16F),
    MEDIUM(11 / 16F),
    LARGE(16 / 16F),
    HUGE(32 / 16F),
    EXTRA_HUGE(48 / 16F);

    @Getter
    private float radius;

    PipeSize(float radius)
    {
        this.radius = radius;
    }

    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }
}
