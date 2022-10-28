package io.github.coffeecatrailway.agameorsomething.core.registry;

import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;

import java.util.Locale;
import java.util.Objects;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class ObjectLocation
{
    private final String namespace;
    private final String path;

    public ObjectLocation(String location)
    {
        if (location.contentEquals(":"))
        {
            String[] parts = location.toLowerCase(Locale.ROOT).split(":", 2);
            this.namespace = parts[0];
            this.path = parts[1];
        } else
        {
            this.namespace = AGameOrSomething.NAMESPACE;
            this.path = location;
        }
    }

    public ObjectLocation(String namespace, String path)
    {
        this.namespace = namespace.toLowerCase(Locale.ROOT);
        this.path = path.toLowerCase(Locale.ROOT);
    }

    public String getNamespace()
    {
        return this.namespace;
    }

    public String getPath()
    {
        return this.path;
    }

    @Override
    public String toString()
    {
        return this.namespace + ":" + this.path;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectLocation that = (ObjectLocation) o;
        return Objects.equals(namespace, that.namespace) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(namespace, path);
    }
}
