package io.github.coffeecatrailway.agameorsomething.common.tile;

import io.github.coffeecatrailway.agameorsomething.client.render.texture.HasTexture;
import io.github.coffeecatrailway.agameorsomething.client.render.vbo.VBOModel;
import io.github.coffeecatrailway.agameorsomething.client.render.vbo.VBOModels;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.core.registry.RegistrableSomething;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.Objects;

/**
 * @author CoffeeCatRailway
 * Created: 25/10/2022
 */
public class Tile implements RegistrableSomething, HasTexture
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
//     * @param world {@link World} - World of the block
//     * @param pos {@link Vector2fc} - World position
//     * @param something {@link AGameOrSomething} - Game instance
//     */
//    public void tick(World world, Vector2fc pos, AGameOrSomething something) // TODO: Tile entity of some sort
//    {
//    }

//    /**
//     * Called when player or other object/entity 'uses' the tile
//     * @param world {@link World} - World of the block
//     * @param pos {@link Vector2fc} - World position
//     * @param direction {@link Direction} - Direction this tile was used from
//     * @param something {@link AGameOrSomething} - Game instance
//     */
//    public void onUse(World world, Vector2fc pos, Direction direction, AGameOrSomething something) // TODO: what used it
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

    @Override
    public boolean hasTexture()
    {
        return this.tileData.hasTexture;
    }

    public boolean isVisible()
    {
        return this.hasTexture();
    }

    @Override
    public ObjectLocation getTextureLocation()
    {
        return this.tileData.customTexture != null ? this.tileData.customTexture : new ObjectLocation(this.getObjectId().getNamespace(), "textures/tile/" + this.getObjectId().getPath());
    }

    public VBOModel getModel()
    {
        return this.tileData.model;
    }

    public Vector2fc getBounds()
    {
        return this.tileData.bounds;
    }

    public boolean isCollidable()
    {
        return this.getBounds().x() > 0f && this.getBounds().y() > 0f && this.isVisible();
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
    public Tile getInstance()
    {
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return id == tile.id && objectId.equals(tile.objectId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, objectId);
    }

    public static class TileData
    {
        private int harvestLevel = 0;
        private RegistrableSomething drop = null;
        private boolean hasTexture = true;
        private ObjectLocation customTexture = null;
        private VBOModel model = VBOModels.SIMPLE_1X1;
        private Vector2f bounds = new Vector2f(1f); // TODO: Make other tiles respect bounds

        public TileData()
        {
        }

        private TileData(int harvestLevel, RegistrableSomething drop, boolean hasTexture, ObjectLocation customTexture, VBOModel model, Vector2f bounds)
        {
            this.harvestLevel = harvestLevel;
            this.drop = drop;
            this.hasTexture = hasTexture;
            this.customTexture = customTexture;
            this.model = model;
            this.bounds = bounds;
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

        public TileData setCustomTexture(ObjectLocation customTexture)
        {
            this.customTexture = new ObjectLocation(customTexture.getNamespace(), "textures/" + customTexture.getPath());
            return this.setHasTexture(true);
        }

        public TileData setModel(VBOModel model)
        {
            this.model = model;
            return this;
        }

        public TileData setBounds(Vector2f bounds)
        {
            this.bounds = bounds.absolute();
            return this;
        }

        public TileData build()
        {
            return new TileData(this.harvestLevel, this.drop, this.hasTexture, this.customTexture, this.model, this.bounds);
        }
    }
}
