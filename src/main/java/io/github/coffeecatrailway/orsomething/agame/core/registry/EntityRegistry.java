package io.github.coffeecatrailway.orsomething.agame.core.registry;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.agame.common.entity.PlayerEntity;
import io.github.coffeecatrailway.orsomething.agame.common.entity.TestEntity;
import io.github.coffeecatrailway.orsomething.anengine.common.entity.Entity;
import io.github.coffeecatrailway.orsomething.anengine.core.registry.SomethingRegistry;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * @author CoffeeCatRailway
 * Created: 17/11/2022
 */
public class EntityRegistry implements SomethingRegistry<Entity>
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final SomethingRegistry.Registry<Entity> REGISTRY = SomethingRegistry.Registry.create(Entity.class);
    public static final EntityRegistry ENTITIES = new EntityRegistry();

    public static final Supplier<PlayerEntity> PLAYER = REGISTRY.register("player", () -> new PlayerEntity(new Entity.EntityData().setDrop(null).setBounds(new Vector2f(1f, 1.5f)).build()));
    public static final Supplier<TestEntity> TEST = REGISTRY.register("test", () -> new TestEntity(new Entity.EntityData().setDrop(null).setBounds(new Vector2f(1f, 2f)).build()));

    @Override
    public @Nullable Supplier<? extends Entity> getDefault()
    {
        return null;
    }

    @Override
    public void load()
    {
        LOGGER.info("Entity registry loaded");
    }
}
