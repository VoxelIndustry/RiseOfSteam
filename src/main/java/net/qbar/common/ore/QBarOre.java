package net.qbar.common.ore;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class QBarOre
{
    private String name;
    private List<QBarMineral> minerals;

    public QBarOre(String name, QBarMineral... minerals)
    {
        this.name = name;
        this.minerals = Arrays.asList(minerals);
    }
}
