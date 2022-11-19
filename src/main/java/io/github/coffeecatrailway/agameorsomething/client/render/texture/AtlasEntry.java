package io.github.coffeecatrailway.agameorsomething.client.render.texture;

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
    private final TextureAtlas<?> atlas;

    public AtlasEntry(ObjectLocation id, TextureAtlas<?> atlas, int x, int y, int width, int height)
    {
        this.id = id;
        this.atlas = atlas;
        this.uvCoords = new Vector4f((float) x / (float) atlas.getWidth(), (float) y / (float) atlas.getHeight(), (float) width / (float) atlas.getWidth(), (float) height / (float) atlas.getHeight());
    }

    public AtlasEntry(JsonObject json, TextureAtlas<?> atlas)
    {
        this.id = new ObjectLocation(json.getAsJsonObject("id"));
        this.atlas = atlas;
        this.uvCoords = new Vector4f(json.get("x").getAsFloat(), json.get("y").getAsFloat(), json.get("width").getAsFloat(), json.get("height").getAsFloat());
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

    public ObjectLocation getId()
    {
        return this.id;
    }

    public Vector4fc getUVCoords()
    {
        return this.uvCoords;
    }

    public TextureAtlas<?> getAtlas() {
        return atlas;
    }
}
