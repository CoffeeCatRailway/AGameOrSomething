package io.github.coffeecatrailway.agameorsomething.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
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

    private final Map<Integer, T> registry = new HashMap<>();
    private final Supplier<T> orElse;

    private SomethingRegistry(Supplier<T> orElse)
    {
        this.orElse = orElse;
    }

    public void foreach(BiConsumer<Integer, T> action)
    {
        this.registry.forEach(action);
    }

    public Supplier<T> register(String id, Supplier<T> factory)
    {
        return register(new ObjectLocation(id), factory);
    }

    public Supplier<T> register(ObjectLocation objectId, Supplier<T> factory)
    {
        int id = objectId.hashCode();
        if (registry.values().stream().filter(obj -> obj.getObjectId().equals(objectId)).collect(Collectors.toSet()).size() > 0)
            throw new IllegalStateException("'" + objectId + "' already exists!");

        if (registry.containsKey(id))
        {
            while (registry.containsKey(id))
                id = INTEGER_ID++;
            LOGGER.warn("{} has hash code of {} witch already exists, given new id of {}", objectId, objectId.hashCode(), id);
        }

        T object = factory.get();
        object.setId(id, objectId);
        registry.put(id, object);
        LOGGER.debug("Tile {} registered with id of {}", objectId, id);
        return () -> registry.get(object.getId());
    }

    public Supplier<T> getById(int id)
    {
        return () -> registry.get(id);
    }

    public Supplier<T> getByStringId(ObjectLocation objectId)
    {
        int id = objectId.hashCode();
        if (!registry.containsKey(id))
            return () -> registry.values().stream().filter(obj1 -> obj1.getObjectId().equals(objectId)).findFirst().orElse(this.orElse.get());
        return () -> registry.get(id);
    }

    public static <C extends RegistrableSomething> SomethingRegistry<C> create(Supplier<C> orElse)
    {
        return new SomethingRegistry<>(orElse);
    }
}
