package io.github.coffeecatrailway.agameorsomething.common.entity;

import io.github.coffeecatrailway.agameorsomething.client.camera.Camera;
import io.github.coffeecatrailway.agameorsomething.client.render.BatchRenderer;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.HasTexture;
import io.github.coffeecatrailway.agameorsomething.client.render.texture.atlas.TextureAtlas;
import io.github.coffeecatrailway.agameorsomething.common.collision.BoundingBox;
import io.github.coffeecatrailway.agameorsomething.common.entity.ai.Task;
import io.github.coffeecatrailway.agameorsomething.common.utils.MatUtils;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import io.github.coffeecatrailway.agameorsomething.common.world.TileSet;
import io.github.coffeecatrailway.agameorsomething.common.world.World;
import io.github.coffeecatrailway.agameorsomething.core.AGameOrSomething;
import io.github.coffeecatrailway.agameorsomething.core.registry.RegistrableSomething;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author CoffeeCatRailway
 * Created: 15/11/2022
 */
public abstract class Entity implements RegistrableSomething, HasTexture
{
    protected final EntityData entityData;

    private int id;
    private ObjectLocation objectId;
    protected UUID uuid = MatUtils.insecureUUID();

    protected Vector2f position = new Vector2f();
    protected BoundingBox boundingBox;

    protected float health;

    protected boolean hasTasks = false;
    protected final Set<Task> tasks = new HashSet<>();

    protected World world;

    public Entity(EntityData entityData)
    {
        this.entityData = entityData;
        this.boundingBox = new BoundingBox(this.position, this.entityData.bounds);
        this.health = this.entityData.maxHealth;
    }

    public void init()
    {}

    public void tick(float delta, AGameOrSomething something, Camera camera, World world)
    {
        this.boundingBox.setPosition(this.position);
        if (this.hasTasks)
            this.tasks.forEach(task -> task.tick(delta, world));
    }

    public void render(AGameOrSomething something, BatchRenderer batch)
    {
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(TextureAtlas.ENTITY_ATLAS.getEntry(this.getObjectId()), this.position.x, this.position.y, this.boundingBox.getBounds().x(), this.boundingBox.getBounds().y());
        batch.end();
    }

    public void checkTileCollision(World world)
    {
        Vector2i pos = new Vector2i();
        for (int y = -2; y < 3; y++)
        {
            for (int x = -2; x < 3; x++)
            {
                BoundingBox box = world.getTileBounds(pos.set((int) this.position.x, (int) this.position.y).add(x, y), TileSet.Level.FOREGROUND);
                if (box != null && this.boundingBox.isIntersecting(box))
                    this.boundingBox.correctAndStop(box);
            }
        }
    }

    public void checkEntityCollision(Entity entity)
    {
        if (!this.uuid.equals(entity.uuid) && entity.isCollidable() && this.boundingBox.isIntersecting(entity.boundingBox))
        {
            this.boundingBox.correctAndPush(entity.boundingBox);
            entity.boundingBox.correctAndPush(this.boundingBox);
        }
    }

    @Override
    public ObjectLocation getTextureLocation()
    {
        return new ObjectLocation(this.getObjectId().getNamespace(), "textures/entity/" + this.getObjectId().getPath());
    }

    public Vector2f getPosition()
    {
        return this.position;
    }

    public BoundingBox getBoundingBox()
    {
        return this.boundingBox;
    }

    public Vector2fc getBounds()
    {
        return this.boundingBox.getBounds();
    }

    public boolean isCollidable()
    {
        return this.getBounds().x() > 0f && this.getBounds().y() > 0f;
    }

    public float getHealth()
    {
        return this.health;
    }

    public void setHealth(float health)
    {
        this.health = health;
    }

    protected void addTask(Task task)
    {
        this.hasTasks = true;
        this.tasks.add(task);
    }

    public World getWorld()
    {
        return this.world;
    }

    public void setWorld(World world)
    {
        this.world = world;
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

    protected void setId(Entity entity)
    {
        this.setId(entity.getId(), entity.getObjectId());
    }

    @Override
    public void setId(int id, ObjectLocation objectId)
    {
        this.id = id;
        this.objectId = objectId;
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public void setUUID(UUID uuid)
    {
        this.uuid = uuid;
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
        return id == entity.id && objectId.equals(entity.objectId) && uuid.equals(entity.uuid);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, objectId, uuid);
    }

    public static class EntityData
    {
        private float maxHealth = 20f;
        private float passiveDefense = 0f;
        private RegistrableSomething drop = null;
        private Vector2f bounds = new Vector2f(1f);

        public EntityData()
        {
        }

        public EntityData(float maxHealth, float passiveDefense, RegistrableSomething drop, Vector2f bounds)
        {
            this.maxHealth = maxHealth;
            this.passiveDefense = passiveDefense;
            this.drop = drop;
            this.bounds = bounds;
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

        public EntityData setBounds(Vector2f bounds)
        {
            this.bounds = bounds;
            return this;
        }

        public EntityData build()
        {
            return new EntityData(this.maxHealth, this.passiveDefense, this.drop, this.bounds);
        }
    }
}
