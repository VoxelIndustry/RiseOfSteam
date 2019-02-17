package net.ros.common.world;

import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class BiomeMatcher
{
    public static final BiomePredicate WILDCARD = biome -> true;

    public static BiomePredicate fromNames(String... biomeNames)
    {
        return biome -> biome != null && ArrayUtils.contains(biomeNames, biome.getRegistryName().getPath());
    }

    public static BiomePredicate fromBiomes(Biome... biomes)
    {
        return biome -> Stream.of(biomes).anyMatch(check -> check.getRegistryName().getPath().equals(biome.getRegistryName().getPath()));
    }

    public static BiomePredicate reverse(BiomePredicate predicate)
    {
        return (BiomePredicate) predicate.negate();
    }

    @FunctionalInterface
    public interface BiomePredicate extends Predicate<Biome>
    {
        boolean test(Biome biome);
    }
}
