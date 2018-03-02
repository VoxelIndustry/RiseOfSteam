package net.qbar.common.block.property;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.block.properties.PropertyHelper;

import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode
@Getter
public class PropertyString extends PropertyHelper<String>
{
    private List<String> allowedValues;

    public PropertyString(String name, String... values)
    {
        super(name, String.class);

        this.allowedValues = Lists.newArrayList(values);
    }

    public String getByIndex(int index)
    {
        return this.allowedValues.get(index);
    }

    public int indexOf(String value)
    {
        return this.allowedValues.indexOf(value);
    }

    public void addValues(String... values)
    {
        this.allowedValues.addAll(Arrays.asList(values));
    }

    public static PropertyString create(String name, String... values)
    {
        return new PropertyString(name, values);
    }

    @Override
    public Optional<String> parseValue(String value)
    {
        return Optional.of(value);
    }

    @Override
    public String getName(String value)
    {
        return value;
    }
}
