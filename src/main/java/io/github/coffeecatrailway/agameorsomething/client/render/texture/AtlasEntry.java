package io.github.coffeecatrailway.agameorsomething.client.render.texture;

import io.github.coffeecatrailway.agameorsomething.core.registry.ObjectLocation;

/**
 * @author CoffeeCatRailway
 * Created: 16/11/2022
 */
public record AtlasEntry(ObjectLocation id, int x, int y, int width, int height)
{
    public boolean isOverlapping(AtlasEntry entry)
    {
        return this.x < entry.x + entry.width &&
                this.x + this.width > entry.x &&
                this.y < entry.y + entry.height &&
                this.y + this.height > entry.y;
    }
}
