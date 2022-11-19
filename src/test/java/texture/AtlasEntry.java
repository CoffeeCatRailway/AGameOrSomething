package texture;

import com.google.gson.JsonObject;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/**
 * @author CoffeeCatRailway
 * Created: 16/11/2022
 */
public class AtlasEntry
{
    private final ObjectLocation id;
    private final Vector4fc uvCoords;

    public AtlasEntry(ObjectLocation id, float x, float y, float width, float height)
    {
        this.id = id;
        this.uvCoords = new Vector4f(x / TextureAtlas.ATLAS_SIZE, y / TextureAtlas.ATLAS_SIZE, width / TextureAtlas.ATLAS_SIZE, height / TextureAtlas.ATLAS_SIZE);
    }

    public AtlasEntry(JsonObject json)
    {
        this.id = new ObjectLocation(json.getAsJsonObject("id"));
        this.uvCoords = new Vector4f(json.get("x").getAsFloat() / TextureAtlas.ATLAS_SIZE,
                json.get("y").getAsFloat() / TextureAtlas.ATLAS_SIZE,
                json.get("width").getAsFloat() / TextureAtlas.ATLAS_SIZE,
                json.get("height").getAsFloat() / TextureAtlas.ATLAS_SIZE);
    }

    public JsonObject serialize(JsonObject json)
    {
        json.add("id", this.id.serialize(new JsonObject()));
        json.addProperty("x", this.uvCoords.x());
        json.addProperty("y", this.uvCoords.y());
        json.addProperty("width", this.uvCoords.z());
        json.addProperty("height", this.uvCoords.w());
        return json;
    }

    public boolean isOverlapping(AtlasEntry entry)
    {
        return this.uvCoords.x() < entry.uvCoords.x() + entry.uvCoords.z() &&
                this.uvCoords.x() + this.uvCoords.z() > entry.uvCoords.x() &&
                this.uvCoords.y() < entry.uvCoords.y() + entry.uvCoords.w() &&
                this.uvCoords.y() + this.uvCoords.w() > entry.uvCoords.y();
    }

    public Vector4fc getUVCoords()
    {
        return this.uvCoords;
    }

    public ObjectLocation getId()
    {
        return this.id;
    }
}
