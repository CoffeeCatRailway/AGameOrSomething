package io.github.coffeecatrailway.agameorsomething.common.entity;

import io.github.coffeecatrailway.agameorsomething.client.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.HasTexture;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.RegistrableSomething;
import org.joml.Vector2f;

/**
 * @author CoffeeCatRailway
 * Created: 15/11/2022
 */
public abstract class Entity implements RegistrableSomething, HasTexture
{
    protected final EntityData entityData;

    private int id;
    private ObjectLocation objectId;

    protected Vector2f position = new Vector2f(); // TODO: Have `lastPosition` for collision box updating
    protected float health;

    public Entity(EntityData entityData)
    {
        this.entityData = entityData;
        this.health = this.entityData.maxHealth;
    }

    public abstract void tick(float delta, AGameOrSomething something, Camera camera, World world);

    public void render(AGameOrSomething something, BatchRenderer batch, Camera camera)
    {
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(TextureAtlas.ENTITY_ATLAS.getEntry(this.getObjectId()), this.position.x, this.position.y, 1f, 2f);
        batch.end();
    }

    @Override
    public boolean hasTexture()
    {
        return TextureAtlas.ENTITY_ATLAS.has(this.getObjectId()) && TextureAtlas.ENTITY_ATLAS.getEntry(this.getObjectId()) != null;
    }

    @Override
    public ObjectLocation getTextureLocation()
    {
        return new ObjectLocation(this.getObjectId().getNamespace(), "textures/entity/" + this.getObjectId().getPath());
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
    public Entity getInstance()
    {
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id == entity.id && objectId.equals(entity.objectId);
    }

    public static class EntityData
    {
        private float maxHealth = 20f;
        private float passiveDefense = 0f;
        private RegistrableSomething drop = null;

        public EntityData()
        {
        }

        public EntityData(float maxHealth, float passiveDefense, RegistrableSomething drop)
        {
            this.maxHealth = maxHealth;
            this.passiveDefense = passiveDefense;
            this.drop = drop;
        }

        public EntityData setMaxHealth(float maxHealth)
        {
            this.maxHealth = maxHealth;
            return this;
        }

        public EntityData setPassiveDefense(float passiveDefense)
        {
            this.passiveDefense = passiveDefense;
            return this;
        }

        public EntityData setDrop(RegistrableSomething drop)
        {
            this.drop = drop;
            return this;
        }

        public EntityData build()
        {
            return new EntityData(this.maxHealth, this.passiveDefense, this.drop);
        }
    }
}
