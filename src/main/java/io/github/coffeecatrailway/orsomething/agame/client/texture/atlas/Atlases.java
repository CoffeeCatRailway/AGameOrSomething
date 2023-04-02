package io.github.coffeecatrailway.orsomething.agame.client.texture.atlas;

import com.mojang.logging.LogUtils;
import io.github.coffeecatrailway.orsomething.agame.core.registry.AGameTiles;
import io.github.coffeecatrailway.orsomething.anengine.client.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.orsomething.anengine.core.io.ObjectLocation;
import io.github.coffeecatrailway.orsomething.agame.core.registry.EntityRegistry;
import org.slf4j.Logger;

/**
 * @author CoffeeCatRailway
 * Created: 02/04/2023
 */
public class Atlases
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final TextureAtlas TILE_ATLAS = new TextureAtlas(AGameTiles.REGISTRY, "tile");
    public static final TextureAtlas ENTITY_ATLAS = new TextureAtlas(EntityRegistry.REGISTRY, "entity");
    public static final TextureAtlas PARTICLE_ATLAS = new TextureAtlas(new ObjectLocation("textures/particle/textures.json"), "particle");

    public static void delete()
    {
        TILE_ATLAS.delete();
        ENTITY_ATLAS.delete();
        PARTICLE_ATLAS.delete();
        LOGGER.warn("Atlases deleted!");
    }
}
