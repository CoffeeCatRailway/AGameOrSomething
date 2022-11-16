package io.github.coffeecatrailway.agameorsomething.core.registry;

import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import org.agrona.collections.Int2ObjectHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public final class SomethingRegistry<T extends RegistrableSomething>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static int INTEGER_ID = 0;

    private final Int2ObjectHashMap<T> registry = new Int2ObjectHashMap<>();
    private final Supplier<T> orElse;

    private SomethingRegistry(Supplier<T> orElse)
    {
        this.orElse = orElse;
    }

    public void foreach(BiConsumer<Integer, T> action)
    {
        this.registry.forEach(action);
    }

    public Int2ObjectHashMap<T>.ValueCollection values()
    {
        return this.registry.values();
    }

    public Supplier<T> register(String id, Supplier<T> factory)
    {
        return register(new ObjectLocation(id), factory);
    }

    public Supplier<T> register(ObjectLocation objectId, Supplier<T> factory)
    {
        int id = objectId.hashCode();
        if (this.registry.values().stream().filter(obj -> obj.getObjectId().equals(objectId)).collect(Collectors.toSet()).size() > 0)
            throw new IllegalStateException("'" + objectId + "' already exists!");

        if (this.registry.containsKey(id))
        {
            while (this.registry.containsKey(id))
                id = INTEGER_ID++;
            LOGGER.warn("{} has hash code of {} witch already exists, given new id of {}", objectId, objectId.hashCode(), id);
        }

        T object = factory.get();
        object.setId(id, objectId);
        this.registry.put(id, object);
        LOGGER.debug("Object {} registered with id of {}", objectId, id);
        return () -> this.registry.get(object.getId());
    }

    public Supplier<T> getById(int id)
    {
        return () -> this.registry.get(id);
    }

    public Supplier<T> getByStringId(ObjectLocation objectId)
    {
        int id = objectId.hashCode();
        if (!this.registry.containsKey(id))
            return () -> this.registry.values().stream().filter(obj1 -> obj1.getObjectId().equals(objectId)).findFirst().orElse(this.orElse.get());
        return () -> this.registry.get(id);
    }

    public static <C extends RegistrableSomething> SomethingRegistry<C> create(Supplier<C> orElse)
    {
        return new SomethingRegistry<>(orElse);
    }
}
