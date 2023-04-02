package io.github.coffeecatrailway.orsomething.anengine.core.registry;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.anengine.common.ObjectLocation;
import org.agrona.collections.Int2ObjectHashMap;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author CoffeeCatRailway
 * Created: 02/04/2023
 *
 * Implemented in the registry 'class'
 */
public interface SomethingRegistry
{
    @Nullable
    <T extends RegistrableSomething> T getDefault();

    void load();

    /**
     * @author CoffeeCatRailway
     * Created: 25/10/2022
     *
     * Used to create the regitry 'object'
     */
    final class Registry<T extends RegistrableSomething>
    {
        private static final Logger LOGGER = LogUtils.getLogger();
        private static int INTEGER_ID = 0;

        private final Int2ObjectHashMap<T> registry = new Int2ObjectHashMap<>();

        private Registry()
        {
        }

        public void foreach(BiConsumer<Integer, T> action)
        {
            this.registry.forEach(action);
        }

        public Int2ObjectHashMap<T>.ValueCollection values()
        {
            return this.registry.values();
        }

        public <C extends T> Supplier<C> register(String id, Supplier<C> factory)
        {
            return register(new ObjectLocation(id), factory);
        }

        public <C extends T> Supplier<C> register(ObjectLocation objectId, Supplier<C> factory)
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

            C object = factory.get();
            object.setId(id, objectId);
            this.registry.put(id, object);
            LOGGER.debug("Object {} registered with id of {}", objectId, id);
            return () -> (C) this.registry.get(object.getId());
        }

        public Supplier<T> getById(int id)
        {
            return () -> this.registry.get(id);
        }

        public Supplier<T> getByStringId(ObjectLocation objectId)
        {
            int id = objectId.hashCode();
            if (!this.registry.containsKey(id))
                return () -> this.registry.values().stream().filter(obj1 -> obj1.getObjectId().equals(objectId)).findFirst().orElse(null);
            return () -> this.registry.get(id);
        }

        public static <C extends RegistrableSomething> Registry<C> create(Class<C> clazz)
        {
            return new Registry<>();
        }
    }
}
