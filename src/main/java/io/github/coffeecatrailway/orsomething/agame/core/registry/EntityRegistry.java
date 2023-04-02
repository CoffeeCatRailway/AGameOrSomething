package io.github.coffeecatrailway.orsomething.agame.core.registry;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.anengine.common.entity.Entity;
import io.github.coffeecatrailway.orsomething.agame.common.entity.PlayerEntity;
import io.github.coffeecatrailway.orsomething.agame.common.entity.TestEntity;
import io.github.coffeecatrailway.orsomething.anengine.core.registry.SomethingRegistry;
import org.joml.Vector2f;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * @author CoffeeCatRailway
 * Created: 17/11/2022
 */
public class EntityRegistry
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final SomethingRegistry.Registry<Entity> ENTITIES = SomethingRegistry.Registry.create(Entity.class);

    public static final Supplier<PlayerEntity> PLAYER = ENTITIES.register("player", () -> new PlayerEntity(new Entity.EntityData().setDrop(null).setBounds(new Vector2f(1f, 1.5f)).build()));

    public static final Supplier<TestEntity> TEST = ENTITIES.register("test", () -> new TestEntity(new Entity.EntityData().setDrop(null).setBounds(new Vector2f(1f, 2f)).build()));

    public static void load()
    {
        LOGGER.info("Entity registry loaded");
    }
}
