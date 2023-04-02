package io.github.coffeecatrailway.orsomething.anengine.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.coffeecatrailway.orsomething.anengine.core.AnEngineOrSomething;

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
            this.namespace = AnEngineOrSomething.NAMESPACE;
            this.path = location;
        }
    }

    public ObjectLocation(String namespace, String path)
    {
        this.namespace = namespace.toLowerCase(Locale.ROOT);
        this.path = path.toLowerCase(Locale.ROOT);
    }

    public ObjectLocation(JsonElement json)
    {
        if (json.isJsonObject())
        {
            this.namespace = json.getAsJsonObject().get("namespace").getAsString().toLowerCase(Locale.ROOT);
            this.path = json.getAsJsonObject().get("path").getAsString().toLowerCase(Locale.ROOT);
        } else
        {
            String[] parts = json.getAsString().toLowerCase(Locale.ROOT).split(":", 2);
            this.namespace = parts[0];
            this.path = parts[1];
        }
    }

    public JsonObject serialize(JsonObject json)
    {
        json.addProperty("namespace", this.namespace);
        json.addProperty("path", this.path);
        return json;
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
        return namespace.equals(that.namespace) && path.equals(that.path);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(namespace, path);
    }
}
