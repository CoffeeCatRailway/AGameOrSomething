package io.github.coffeecatrailway.agameorsomething.core.registry;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.agameorsomething.common.entity.Entity;
import io.github.coffeecatrailway.agameorsomething.common.entity.PlayerEntity;
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
    public static final SomethingRegistry<Entity> ENTITIES = SomethingRegistry.create(Entity.class);

    public static final Supplier<PlayerEntity> PLAYER = ENTITIES.register("player", () -> new PlayerEntity(new Entity.EntityData().setDrop(null).setBounds(new Vector2f(1f, 1.5f)).build()));

    public static void load()
    {
        LOGGER.info("Entity registry loaded");
    }
}
