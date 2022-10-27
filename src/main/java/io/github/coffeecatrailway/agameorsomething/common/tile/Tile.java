package io.github.coffeecatrailway.agameorsomething.common.tile;

import io.github.coffeecatrailway.agameorsomething.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.registry.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.registry.RegistrableSomething;
import org.joml.Math;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class Tile implements RegistrableSomething
{
    private final TileData tileData;

    private int id;
    private ObjectLocation objectId;

    public Tile(TileData tileData)
    {
        this.tileData = tileData;
    }

//    /**
//     * Main update method
//     * @param pos {@link Vector2fc} - World position
//     * @param something {@link AGameOrSomething} - Game instance
//     */
//    public void tick(Vector2fc pos, AGameOrSomething something) // TODO: world
//    {
//    }
//
//    /**
//     * Called when player or other object/entity 'uses' the tile
//     * @param pos {@link Vector2fc} - World position
//     * @param direction {@link Direction} - Direction this tile was used from
//     * @param something {@link AGameOrSomething} - Game instance
//     */
//    public void use(Vector2fc pos, Direction direction, AGameOrSomething something) // TODO: world, what used it
//    {
//    }


    public boolean isUnbreakable()
    {
        return this.tileData.isUnbreakable();
    }

    public int getHarvestLevel()
    {
        return this.tileData.harvestLevel;
    }

    public RegistrableSomething getDrop()
    {
        return this.tileData.getDrop();
    }

    public boolean hasTexture()
    {
        return this.tileData.hasTexture;
    }

    @Override
    public int getId()
    {
        return this.id;
    }

    @Override
    public ObjectLocation getObjectId()
    {
        return this.objectId;
    }

    @Override
    public void setId(int id, ObjectLocation objectId)
    {
        this.id = id;
        this.objectId = objectId;
    }

    @Override
    public RegistrableSomething getInstance()
    {
        return this;
    }

    public static class TileData
    {
        private int harvestLevel = 0;
        private RegistrableSomething drop = null;
        private boolean hasTexture = true;

        public TileData()
        {
        }

        private TileData(int harvestLevel, RegistrableSomething drop, boolean hasTexture)
        {
            this.harvestLevel = harvestLevel;
            this.drop = drop;
            this.hasTexture = hasTexture;
        }

        public boolean isUnbreakable()
        {
            return this.harvestLevel >= 100;
        }

        public TileData setUnbreakable(boolean unbreakable)
        {
            this.harvestLevel = unbreakable ? 100 : this.harvestLevel;
            return this;
        }

        public TileData setHarvestLevel(int harvestLevel)
        {
            this.harvestLevel = Math.clamp(0, 100, harvestLevel);
            return this;
        }

        public RegistrableSomething getDrop()
        {
            return this.isUnbreakable() ? null : this.drop;
        }

        public TileData setDrop(RegistrableSomething drop)
        {
            this.drop = drop;
            return this;
        }

        public TileData setHasTexture(boolean hasTexture)
        {
            this.hasTexture = hasTexture;
            return this;
        }

        public TileData build()
        {
            return new TileData(this.harvestLevel, this.drop, this.hasTexture);
        }
    }
}
