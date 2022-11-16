package io.github.coffeecatrailway.agameorsomething.core.registry;

import io.github.coffeecatrailway.agameorsomething.common.entity.Entity;
import io.github.coffeecatrailway.agameorsomething.common.entity.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/**
 * @author CoffeeCatRailway
 * Created: 17/11/2022
 */
public class EntityRegistry
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final SomethingRegistry<Entity> ENTITIES = SomethingRegistry.create(Entity.class);

    public static final Supplier<PlayerEntity> PLAYER = ENTITIES.register("player", () -> new PlayerEntity(new Entity.EntityData().setDrop(null).build()));

    public static void load()
    {
        LOGGER.info("Entity registry loaded");
    }
}
