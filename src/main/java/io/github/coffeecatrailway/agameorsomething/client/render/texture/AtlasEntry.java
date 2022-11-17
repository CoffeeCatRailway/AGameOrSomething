package io.github.coffeecatrailway.agameorsomething.client.render.texture;

import com.google.gson.JsonObject;
import io.github.coffeecatrailway.agameorsomething.client.render.vbo.VBOModel;
import io.github.coffeecatrailway.agameorsomething.common.utils.ObjectLocation;

/**
 * @author CoffeeCatRailway
 * Created: 16/11/2022
 */
public class AtlasEntry
{
    private final ObjectLocation id;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private VBOModel model = null; // TODO: Shaders?

    public AtlasEntry(ObjectLocation id, int x, int y, int width, int height)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public AtlasEntry(JsonObject json)
    {
        this.id = new ObjectLocation(json.getAsJsonObject("id"));
        this.x = json.get("x").getAsInt();
        this.y = json.get("y").getAsInt();
        this.width = json.get("width").getAsInt();
        this.height = json.get("height").getAsInt();
    }

    public JsonObject serialize(JsonObject json)
    {
        json.add("id", this.id.serialize(new JsonObject()));
        json.addProperty("x", this.x);
        json.addProperty("y", this.y);
        json.addProperty("width", this.width);
        json.addProperty("height", this.height);
        return json;
    }

    public boolean isOverlapping(AtlasEntry entry)
    {
        return this.x < entry.x + entry.width &&
                this.x + this.width > entry.x &&
                this.y < entry.y + entry.height &&
                this.y + this.height > entry.y;
    }

    public VBOModel getModel()
    {
        if (this.model == null)
        {
            float[] vertices = new float[]
                    {
                            0f, 1f, 0f, // top left     0
                            1f, 1f, 0f, // top right    1
                            1f, 0f, 0f, // bottom right 2
                            0f, 0f, 0f  // bottom left  3
                    };

            float x = (float) this.x / TextureAtlas.ATLAS_SIZE;
            float y = (float) this.y / TextureAtlas.ATLAS_SIZE;
            float w = (float) this.width / TextureAtlas.ATLAS_SIZE;
            float h = (float) this.height / TextureAtlas.ATLAS_SIZE;
            float[] textureCoords = new float[]
                    {
                            x, y,
                            x + w, y,
                            x + w, y + h,
                            x, y + h
                    };

            int[] indices = new int[]
                    {
                            0, 1, 2,
                            2, 3, 0
                    };
            this.model = new VBOModel(vertices, textureCoords, indices);
        }

        return this.model;
    }

    public ObjectLocation getId()
    {
        return this.id;
    }
}
